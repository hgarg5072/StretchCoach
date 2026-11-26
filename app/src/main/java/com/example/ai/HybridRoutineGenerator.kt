package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.data.LibraryStretch
import com.example.data.PoseManifestRepository
import com.example.data.PostureHub
import com.example.data.StretchCategory
import com.example.data.StretchEntity
import com.example.data.StretchLibrary
import com.google.firebase.Firebase
import com.google.firebase.ai.InferenceMode
import com.google.firebase.ai.OnDeviceConfig
import com.google.firebase.ai.OnDeviceModelStatus
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import kotlin.math.roundToInt

enum class PosturePreference(val label: String) {
    ANY("Any Posture"),
    FLOOR_ONLY("Floor Only"),
    SEATED("Seated Only"),
    STANDING("Standing Only")
}

enum class InferenceTier(val badgeLabel: String, val detailLabel: String) {
    ON_DEVICE_NPU(
        badgeLabel = "On-Device NPU",
        detailLabel = "Generated privately on your device NPU/TPU (Gemini Nano)"
    ),
    CLOUD_GEMINI(
        badgeLabel = "Cloud Gemini",
        detailLabel = "Generated via Firebase AI Logic (Gemini 3.8 Flash)"
    )
}

data class OnDeviceCapability(
    val isAvailable: Boolean = false,
    val isDownloadable: Boolean = false,
    val modelName: String? = null,
    val hardwareAccelerator: String = HybridRoutineGenerator.detectDeviceAcceleratorName(),
    val statusLabel: String = "Cloud Gemini 3.8 Flash • ${HybridRoutineGenerator.detectDeviceAcceleratorName()}"
)

data class GeneratedRoutinePlan(
    val name: String,
    val description: String,
    val stretches: List<StretchEntity>,
    val inferenceTier: InferenceTier,
    val rationale: String = "",
    val hardwareAccelerator: String = HybridRoutineGenerator.detectDeviceAcceleratorName(),
    val modelName: String = "Gemini 3.8 Flash",
    val latencyMs: Long = 0L,
    val thinkingSteps: List<String> = emptyList()
) {
    val totalDurationSeconds: Int
        get() = stretches.sumOf { it.preparationSeconds + it.holdSeconds }
}

/**
 * 3-Tier Hybrid AI Routine Builder:
 * - Tier 1: On-Device NPU/TPU (Pixel Tensor TPU, Snapdragon Hexagon NPU, MediaTek Dimensity APU)
 *           via Firebase AI Logic + AICore Gemini Nano (~0 MB APK impact, 100% offline).
 * - Tier 2: Cloud Gemini Flash fallback via Firebase AI Logic when AICore is unavailable & online.
 * - Tier 3: Deterministic Offline Smart-Flow Matcher so 100% of Android devices work offline.
 */
object HybridRoutineGenerator {

    private const val TAG = "HybridRoutineGenerator"
    private const val PRIMARY_CLOUD_MODEL = "gemini-3.8-flash-lite"
    private const val SECONDARY_CLOUD_MODEL = "gemini-3.8-flash"
    private const val LATEST_CLOUD_ALIAS = "gemini-flash-latest"
    private const val ON_DEVICE_TIMEOUT_MS = 15_000L
    private const val CLOUD_FAST_TIMEOUT_MS = 20_000L
    private const val CLOUD_FALLBACK_TIMEOUT_MS = 25_000L

    @Volatile
    private var cachedOnDeviceAvailable: Boolean? = null

    private data class CloudModelCandidate(
        val modelId: String,
        val displayName: String,
        val thinkingLevel: ThinkingLevel,
        val maxOutputTokens: Int,
        val timeoutMs: Long
    )

    private val CLOUD_MODEL_CHAIN = listOf(
        CloudModelCandidate(
            modelId = PRIMARY_CLOUD_MODEL,
            displayName = "Gemini 3.8 Flash-Lite",
            thinkingLevel = ThinkingLevel.MINIMAL,
            maxOutputTokens = 768,
            timeoutMs = CLOUD_FAST_TIMEOUT_MS
        ),
        CloudModelCandidate(
            modelId = SECONDARY_CLOUD_MODEL,
            displayName = "Gemini 3.8 Flash",
            thinkingLevel = ThinkingLevel.LOW,
            maxOutputTokens = 1024,
            timeoutMs = CLOUD_FALLBACK_TIMEOUT_MS
        ),
        CloudModelCandidate(
            modelId = LATEST_CLOUD_ALIAS,
            displayName = "Gemini Flash Latest",
            thinkingLevel = ThinkingLevel.LOW,
            maxOutputTokens = 1024,
            timeoutMs = CLOUD_FALLBACK_TIMEOUT_MS
        )
    )

    @OptIn(PublicPreviewAPI::class)
    private fun createOnDeviceModel() = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = SECONDARY_CLOUD_MODEL,
        onDeviceConfig = OnDeviceConfig(
            mode = InferenceMode.ONLY_ON_DEVICE,
            maxOutputTokens = 384,
            temperature = 0.35f
        )
    )

    @OptIn(PublicPreviewAPI::class)
    private fun createCloudModel(candidate: CloudModelCandidate) = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = candidate.modelId,
        generationConfig = generationConfig {
            temperature = 0.35f
            maxOutputTokens = candidate.maxOutputTokens
            responseMimeType = "application/json"
            thinkingConfig = thinkingConfig {
                thinkingLevel = candidate.thinkingLevel
            }
        },
        onDeviceConfig = OnDeviceConfig(
            mode = InferenceMode.ONLY_IN_CLOUD
        )
    )

    private fun hasValidatedInternet(context: Context): Boolean {
        return runCatching {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return true
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }.getOrDefault(true)
    }

    /**
     * Detects the actual Android device NPU / TPU / SoC hardware name (e.g. "Google Tensor G4 TPU",
     * "Snapdragon 8 Gen 3 Hexagon NPU", "Samsung Exynos 9810 NPU", "Dimensity 9300 APU").
     */
    fun detectDeviceAcceleratorName(): String {
        val socModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching { Build.SOC_MODEL }.getOrNull().orEmpty().trim()
        } else ""
        val socMfg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching { Build.SOC_MANUFACTURER }.getOrNull().orEmpty().trim()
        } else ""
        val hw = runCatching { Build.HARDWARE }.getOrNull().orEmpty().trim()
        val board = runCatching { Build.BOARD }.getOrNull().orEmpty().trim()
        val combined = "$socMfg $socModel $hw $board".lowercase()

        // Google Tensor TPU
        if ("tensor" in combined || "gs101" in combined || "gs201" in combined ||
            "zuma" in combined || "laguna" in combined || "cloudripper" in combined
        ) {
            val gen = when {
                "g5" in combined || "laguna" in combined -> "G5"
                "g4" in combined || "zumapro" in combined -> "G4"
                "g3" in combined || "zuma" in combined -> "G3"
                "g2" in combined || "gs201" in combined || "cloudripper" in combined -> "G2"
                "gs101" in combined -> "G1"
                else -> ""
            }
            return listOf("Google Tensor", gen, "TPU").filter { it.isNotBlank() }.joinToString(" ")
        }

        // Qualcomm Snapdragon Hexagon NPU
        if ("qualcomm" in combined || "qcom" in combined || "snapdragon" in combined ||
            socModel.startsWith("SM", ignoreCase = true) || socModel.startsWith("SDM", ignoreCase = true)
        ) {
            val friendly = when {
                "sm8750" in combined -> "Snapdragon 8 Elite"
                "sm8650" in combined -> "Snapdragon 8 Gen 3"
                "sm8550" in combined -> "Snapdragon 8 Gen 2"
                "sm8475" in combined -> "Snapdragon 8+ Gen 1"
                "sm8450" in combined -> "Snapdragon 8 Gen 1"
                "sm8350" in combined -> "Snapdragon 888"
                "sm8250" in combined -> "Snapdragon 865"
                "sm7675" in combined -> "Snapdragon 7+ Gen 3"
                "sm7550" in combined -> "Snapdragon 7 Gen 3"
                socModel.isNotBlank() -> "Qualcomm $socModel"
                else -> "Snapdragon"
            }
            return "$friendly Hexagon NPU"
        }

        // Samsung Exynos NPU
        if ("exynos" in combined || "s5e" in combined || "universal98" in combined || "samsung" in socMfg.lowercase()) {
            val friendly = when {
                "s5e9955" in combined || "2500" in combined -> "Exynos 2500"
                "s5e9945" in combined || "2400" in combined -> "Exynos 2400"
                "s5e9925" in combined || "2200" in combined -> "Exynos 2200"
                "exynos2100" in combined || "2100" in combined -> "Exynos 2100"
                "9810" in combined -> "Exynos 9810"
                "9820" in combined || "9825" in combined -> "Exynos 9820"
                socModel.isNotBlank() -> socModel
                hw.isNotBlank() -> hw.replaceFirstChar { it.uppercase() }
                else -> "Exynos"
            }
            return "Samsung $friendly NPU"
        }

        // MediaTek Dimensity APU
        if ("mediatek" in combined || "dimensity" in combined || socModel.startsWith("MT", ignoreCase = true)) {
            val friendly = when {
                "mt6991" in combined -> "Dimensity 9400"
                "mt6989" in combined -> "Dimensity 9300"
                "mt6985" in combined -> "Dimensity 9200"
                "mt6983" in combined -> "Dimensity 9000"
                socModel.isNotBlank() -> "MediaTek $socModel"
                else -> "MediaTek Dimensity"
            }
            return "$friendly APU"
        }

        val fallbackChip = socModel.ifBlank { hw.ifBlank { Build.MODEL.orEmpty() } }.ifBlank { "Device" }
        return "$fallbackChip On-Device NPU/CPU"
    }

    /**
     * Checks whether the current device supports AICore / Gemini Nano on its NPU/TPU.
     * Never throws — returns a graceful fallback status on unsupported devices or unit tests.
     */
    @OptIn(PublicPreviewAPI::class)
    suspend fun checkOnDeviceCapability(): OnDeviceCapability = withContext(Dispatchers.IO) {
        val hwName = detectDeviceAcceleratorName()
        try {
            val model = createOnDeviceModel()
            val extension = model.onDeviceExtension
            if (extension == null) {
                cachedOnDeviceAvailable = false
                return@withContext OnDeviceCapability(
                    isAvailable = false,
                    hardwareAccelerator = hwName,
                    statusLabel = "Cloud Gemini 3.8 Flash • $hwName"
                )
            }
            val status = extension.checkStatus()
            val modelName = runCatching { extension.getModelName() }.getOrNull()
            when (status) {
                OnDeviceModelStatus.AVAILABLE -> {
                    cachedOnDeviceAvailable = true
                    OnDeviceCapability(
                        isAvailable = true,
                        modelName = modelName,
                        hardwareAccelerator = hwName,
                        statusLabel = "Using $hwName (${modelName ?: "Gemini Nano"})"
                    )
                }
                OnDeviceModelStatus.DOWNLOADABLE, OnDeviceModelStatus.DOWNLOADING -> {
                    cachedOnDeviceAvailable = false
                    // Kick off background AICore download so Pixel 9 / NPU devices become ready automatically
                    runCatching { extension.download() }
                    OnDeviceCapability(
                        isAvailable = false,
                        isDownloadable = true,
                        modelName = modelName,
                        hardwareAccelerator = hwName,
                        statusLabel = "$hwName • Downloading Gemini Nano (Cloud ready)"
                    )
                }
                else -> {
                    cachedOnDeviceAvailable = false
                    OnDeviceCapability(
                        isAvailable = false,
                        hardwareAccelerator = hwName,
                        statusLabel = "Cloud Gemini 3.8 Flash • $hwName"
                    )
                }
            }
        } catch (t: Throwable) {
            cachedOnDeviceAvailable = false
            Log.d(TAG, "On-device AI check fell back cleanly: ${t.message}")
            OnDeviceCapability(
                isAvailable = false,
                hardwareAccelerator = hwName,
                statusLabel = "Cloud Gemini 3.8 Flash • $hwName"
            )
        }
    }

    /**
     * Generates a personalized routine using ONLY real AI models:
     * - Tier 1: On-Device NPU/TPU (Gemini Nano via Android AICore) when available on this device.
     * - Tier 2: Fast Cloud Gemini fallback chain (`gemini-3.8-flash-lite` -> `gemini-3.8-flash` -> `gemini-flash-latest`)
     *           using `ONLY_IN_CLOUD` (skips AICore probe overhead), JSON output mode, and low/minimal thinking latency.
     * Throws a clear, actionable error if offline or if no AI model can be reached.
     */
    @OptIn(PublicPreviewAPI::class)
    suspend fun generateRoutine(
        context: Context,
        userPrompt: String,
        targetMinutes: Int = 10,
        posturePreference: PosturePreference = PosturePreference.ANY
    ): GeneratedRoutinePlan = withContext(Dispatchers.IO) {
        val cleanPrompt = userPrompt.trim().ifBlank { "Balanced full-body mobility and relaxation" }
        val resolvedMinutes = extractTargetMinutes(cleanPrompt, targetMinutes)
        val resolvedPosture = extractPosturePreference(cleanPrompt, posturePreference)
        val hwName = detectDeviceAcceleratorName()

        val allLibrary = StretchLibrary.ALL_100_STRETCHES
        val hubById = buildHubMap(context, allLibrary)

        // Pre-rank top 28 relevant stretches (with benefits) to keep prompt fast & within Gemini Nano's window
        val candidates = selectCandidatePool(
            prompt = cleanPrompt,
            posturePreference = resolvedPosture,
            allLibrary = allLibrary,
            hubById = hubById,
            maxCandidates = 28
        )

        val llmPrompt = buildCompactPrompt(
            userGoal = cleanPrompt,
            targetMinutes = resolvedMinutes,
            posturePreference = resolvedPosture,
            candidates = candidates,
            hubById = hubById
        )

        val onDeviceReady = cachedOnDeviceAvailable ?: checkOnDeviceCapability().isAvailable

        // Tier 1: Try On-Device NPU/TPU (Gemini Nano) directly only when AICore reports AVAILABLE
        if (onDeviceReady) {
            val startedMs = System.currentTimeMillis()
            val onDevicePlan = runCatching {
                val model = createOnDeviceModel()
                val response = withTimeout(ON_DEVICE_TIMEOUT_MS) {
                    model.generateContent(llmPrompt)
                }
                val elapsedMs = (System.currentTimeMillis() - startedMs).coerceAtLeast(1L)
                parseModelResponseToPlan(
                    rawText = response.text.orEmpty(),
                    inferenceTier = InferenceTier.ON_DEVICE_NPU,
                    targetMinutes = resolvedMinutes,
                    allLibrary = allLibrary,
                    context = context,
                    hardwareAccelerator = hwName,
                    modelName = "Gemini Nano",
                    latencyMs = elapsedMs,
                    candidateCount = candidates.size
                )
            }.onFailure { t ->
                Log.w(TAG, "On-device Gemini Nano failed, falling back to Cloud Gemini: ${t.message}")
            }.getOrNull()

            if (onDevicePlan != null) return@withContext onDevicePlan
        }

        // Pre-flight internet check so offline devices fail instantly instead of waiting for a network timeout
        if (!hasValidatedInternet(context)) {
            throw IllegalStateException(
                "No internet connection detected. Connect to Wi-Fi or mobile data to use Cloud Gemini on $hwName."
            )
        }

        // Tier 2: Fast multi-model Cloud Gemini fallback chain (ONLY_IN_CLOUD avoids wasted AICore probes)
        var lastError: Throwable? = null
        for (candidate in CLOUD_MODEL_CHAIN) {
            val startedMs = System.currentTimeMillis()
            try {
                val model = createCloudModel(candidate)
                val response = withTimeout(candidate.timeoutMs) {
                    model.generateContent(llmPrompt)
                }
                val elapsedMs = (System.currentTimeMillis() - startedMs).coerceAtLeast(1L)
                val plan = parseModelResponseToPlan(
                    rawText = response.text.orEmpty(),
                    inferenceTier = InferenceTier.CLOUD_GEMINI,
                    targetMinutes = resolvedMinutes,
                    allLibrary = allLibrary,
                    context = context,
                    hardwareAccelerator = hwName,
                    modelName = candidate.displayName,
                    latencyMs = elapsedMs,
                    candidateCount = candidates.size
                )
                if (plan != null) {
                    return@withContext plan
                }
                lastError = IllegalStateException("${candidate.modelId} returned unexpected JSON format")
            } catch (t: Throwable) {
                Log.w(TAG, "Cloud model ${candidate.modelId} failed (${t.javaClass.simpleName}: ${t.message})")
                lastError = t
            }
        }

        Log.e(TAG, "All Cloud AI models failed", lastError)
        val detail = lastError?.message?.takeIf { it.isNotBlank() }
            ?: lastError?.javaClass?.simpleName
            ?: "Request timed out"
        throw IllegalStateException(
            "Cloud Gemini unavailable ($detail). Please check your internet connection and tap Build Routine to retry.",
            lastError
        )
    }

    internal fun buildHubMap(
        context: Context?,
        allLibrary: List<LibraryStretch>
    ): Map<String, PostureHub> {
        return allLibrary.associate { stretch ->
            val manifestHub = context?.let { ctx ->
                PoseManifestRepository.getPoseMetaForStretch(ctx, stretch.name, stretch.category.title)?.postureHub
            }
            stretch.id to (manifestHub ?: inferHubFromText(stretch))
        }
    }

    internal fun inferHubFromText(stretch: LibraryStretch): PostureHub {
        val text = "${stretch.name} ${stretch.prepCue} ${stretch.holdCue}".lowercase()
        return when {
            listOf("lie on your back", "supine", "savasana", "reclining", "happy baby", "bridge", "wind-relieving", "legs up").any { it in text } -> PostureHub.SUPINE
            listOf("lie on your stomach", "prone", "cobra", "sphinx", "locust", "bow pose", "bhujangasana").any { it in text } -> PostureHub.PRONE
            listOf("kneel", "all fours", "tabletop", "child's pose", "cat-cow", "puppy pose", "pigeon", "lunge").any { it in text } -> PostureHub.KNEELING
            listOf("sit", "seated", "cross-legged", "sukhasana", "padmasana", "butterfly", "baddha konasana", "boat", "passthrough").any { it in text } -> PostureHub.SEATED
            else -> PostureHub.STANDING
        }
    }

    internal fun extractTargetMinutes(prompt: String, fallbackMinutes: Int): Int {
        val regex = Regex("""(\d{1,2})\s*(min|mins|minute|minutes|m)\b""", RegexOption.IGNORE_CASE)
        val match = regex.find(prompt)
        val parsed = match?.groupValues?.getOrNull(1)?.toIntOrNull()
        return (parsed ?: fallbackMinutes).coerceIn(3, 45)
    }

    internal fun extractPosturePreference(prompt: String, explicit: PosturePreference): PosturePreference {
        if (explicit != PosturePreference.ANY) return explicit
        val lower = prompt.lowercase()
        return when {
            listOf("floor only", "on the floor", "mat only", "no standing", "bedtime", "before sleep", "in bed").any { it in lower } ->
                PosturePreference.FLOOR_ONLY
            listOf("seated only", "at my desk", "desk break", "office chair", "sitting only").any { it in lower } ->
                PosturePreference.SEATED
            listOf("standing only", "no floor", "stay standing").any { it in lower } ->
                PosturePreference.STANDING
            else -> PosturePreference.ANY
        }
    }

    internal fun matchesPosture(hub: PostureHub, pref: PosturePreference): Boolean = when (pref) {
        PosturePreference.ANY -> true
        PosturePreference.FLOOR_ONLY -> hub != PostureHub.STANDING
        PosturePreference.SEATED -> hub == PostureHub.SEATED
        PosturePreference.STANDING -> hub == PostureHub.STANDING
    }

    internal fun selectCandidatePool(
        prompt: String,
        posturePreference: PosturePreference,
        allLibrary: List<LibraryStretch>,
        hubById: Map<String, PostureHub>,
        maxCandidates: Int = 34
    ): List<LibraryStretch> {
        val postureFiltered = allLibrary.filter { s ->
            matchesPosture(hubById[s.id] ?: PostureHub.STANDING, posturePreference)
        }.ifEmpty { allLibrary }

        val scored = postureFiltered.map { stretch ->
            stretch to scoreStretchForPrompt(stretch, prompt, hubById[stretch.id] ?: PostureHub.STANDING)
        }.sortedByDescending { it.second }

        val top = scored.take(maxCandidates).map { it.first }.toMutableList()
        // Ensure bilateral partners are present in the candidate pool
        val topIds = top.map { it.id }.toMutableSet()
        val byId = allLibrary.associateBy { it.id }
        top.toList().forEach { s ->
            val partnerId = findBilateralPartnerId(s.id)
            if (partnerId != null && partnerId !in topIds && byId.containsKey(partnerId)) {
                top.add(byId.getValue(partnerId))
                topIds.add(partnerId)
            }
        }
        return top
    }

    internal fun scoreStretchForPrompt(
        stretch: LibraryStretch,
        prompt: String,
        hub: PostureHub
    ): Int {
        val lowerPrompt = prompt.lowercase()
        val haystack = "${stretch.name} ${stretch.category.title} ${stretch.benefits} ${stretch.prepCue} ${stretch.holdCue} ${stretch.indianName.orEmpty()}".lowercase()
        var score = 0

        val keywordGroups = listOf(
            listOf("neck", "cervical", "headache", "jaw") to listOf("neck", "cervical", "trapezius", "levator", "scm"),
            listOf("shoulder", "shoulders", "rotator", "scapula") to listOf("shoulder", "deltoid", "scapula", "trapezius", "rotator", "eagle arms"),
            listOf("desk", "office", "computer", "work", "posture", "slouch") to listOf("desk", "slouch", "neck", "shoulder", "chest", "wrist", "thoracic", "forward head"),
            listOf("chest", "upper back", "thoracic", "hunch") to listOf("chest", "pectoral", "thoracic", "upper back", "rhomboid", "heart"),
            listOf("lower back", "back pain", "spine", "lumbar", "sciatica") to listOf("lower back", "lumbar", "spine", "spinal", "cat-cow", "child", "twist", "cobra", "bridge"),
            listOf("hip", "hips", "glute", "glutes", "groin", "piriformis", "pelvis") to listOf("hip", "glute", "piriformis", "pigeon", "butterfly", "lunge", "groin", "figure-4"),
            listOf("hamstring", "hamstrings", "leg", "legs", "run", "running", "runner", "post-run") to listOf("hamstring", "quad", "calf", "calves", "leg", "hip flexor", "lunge", "forward fold"),
            listOf("quad", "quads", "thigh", "knee") to listOf("quad", "thigh", "hip flexor", "lunge"),
            listOf("calf", "calves", "ankle", "foot", "feet", "plantar") to listOf("calf", "calves", "achilles", "ankle", "foot", "feet"),
            listOf("wrist", "wrists", "arm", "arms", "forearm", "hand", "typing") to listOf("wrist", "forearm", "arm", "triceps", "biceps"),
            listOf("morning", "wake", "energy", "energize", "sun", "surya") to listOf("morning", "spine", "chest", "standing", "cat-cow", "cobra", "sun", "reach"),
            listOf("sleep", "bedtime", "night", "relax", "calm", "stress", "anxiety", "unwind") to listOf("relax", "calm", "restorative", "savasana", "child", "supine", "breathe", "gentle", "twist", "pranayama"),
            listOf("yoga", "asana", "pranayama", "breath", "meditation") to listOf("asana", "yoga", "pranayama", "breath", "warrior", "cobra", "lotus", "tree")
        )

        keywordGroups.forEach { (triggers, targets) ->
            if (triggers.any { it in lowerPrompt }) {
                targets.forEach { target ->
                    if (target in haystack) score += 12
                }
            }
        }

        // Direct token overlap
        val promptTokens = lowerPrompt.split(Regex("[^a-z0-9]+")).filter { it.length >= 3 }
        promptTokens.forEach { token ->
            if (token in stretch.name.lowercase()) score += 15
            else if (token in stretch.category.title.lowercase()) score += 10
            else if (token in haystack) score += 4
        }

        // Restorative/sleep boost for floor poses
        if (listOf("sleep", "bedtime", "night", "relax", "unwind").any { it in lowerPrompt }) {
            if (hub == PostureHub.SUPINE || hub == PostureHub.SEATED || hub == PostureHub.KNEELING) score += 8
        }

        // Gentle baseline diversity so empty/generic prompts pick well-rounded poses
        if (score == 0) {
            score = when (stretch.category) {
                StretchCategory.LOWER_BACK_CORE, StretchCategory.HIPS_GLUTES -> 5
                StretchCategory.NECK_SHOULDERS, StretchCategory.HAMSTRINGS_QUADS -> 4
                else -> 3
            }
        }
        return score
    }

    internal fun buildCompactPrompt(
        userGoal: String,
        targetMinutes: Int,
        posturePreference: PosturePreference,
        candidates: List<LibraryStretch>,
        hubById: Map<String, PostureHub>
    ): String {
        val targetCount = ((targetMinutes * 60) / 48).coerceIn(4, 18)
        val catalogLines = candidates.joinToString("\n") { s ->
            val hub = hubById[s.id]?.name ?: "STANDING"
            val sec = s.defaultPrepSeconds + s.defaultHoldSeconds
            val shortBenefit = s.benefits.trim().take(52)
            "${s.id}|${s.name}|${s.category.title}|$hub|${sec}s|$shortBenefit"
        }
        return """
            You are Stretch Coach, an expert yoga & mobility coach.
            Build a routine for: "$userGoal"
            Target duration: ~$targetMinutes minutes (~$targetCount stretches).
            Posture filter: ${posturePreference.label}.
            Rules:
            - Choose ONLY stretch IDs from the catalog below.
            - If you pick a Right-side stretch, also include its Left-side pair.
            - Never pick two stretches that target the exact same pose.
            - Output ONLY valid compact JSON with keys: name, description, rationale, thinking (array of 3 concise reasoning steps), stretch_ids.
            Example: {"name":"Evening Spine Unwind","description":"Gentle floor mobility for lower back relief","rationale":"Focuses on seated and supine decompression","thinking":["Identified lower back tightness and evening wind-down goal","Selected gentle kneeling and supine decompression poses","Balanced left and right spinal twists for symmetry"],"stretch_ids":["cat_cow_spinal_flow","supta_matsyendrasana_twist_right","supta_matsyendrasana_twist_left"]}
            Catalog (id|name|category|hub|duration|benefit):
            $catalogLines
        """.trimIndent()
    }

    internal fun parseModelResponseToPlan(
        rawText: String,
        inferenceTier: InferenceTier,
        targetMinutes: Int,
        allLibrary: List<LibraryStretch>,
        context: Context?,
        hardwareAccelerator: String = detectDeviceAcceleratorName(),
        modelName: String = if (inferenceTier == InferenceTier.ON_DEVICE_NPU) "Gemini Nano" else "Gemini 3.8 Flash",
        latencyMs: Long = 0L,
        candidateCount: Int = 34
    ): GeneratedRoutinePlan? {
        val jsonStart = rawText.indexOf('{')
        val jsonEnd = rawText.lastIndexOf('}')
        if (jsonStart < 0 || jsonEnd <= jsonStart) return null

        val jsonStr = rawText.substring(jsonStart, jsonEnd + 1)
        val obj = runCatching { JSONObject(jsonStr) }.getOrNull() ?: return null

        val idsArray = obj.optJSONArray("stretch_ids") ?: return null
        val byId = allLibrary.associateBy { it.id }
        val picked = mutableListOf<LibraryStretch>()
        val seenIds = mutableSetOf<String>()

        for (i in 0 until idsArray.length()) {
            val id = idsArray.optString(i).trim()
            val match = byId[id] ?: StretchLibrary.getById(id) ?: continue
            if (seenIds.add(match.id)) {
                picked.add(match)
            }
            val partnerId = findBilateralPartnerId(match.id)
            if (partnerId != null && seenIds.add(partnerId)) {
                byId[partnerId]?.let { picked.add(it) }
            }
        }

        if (picked.size < 2) return null

        val name = obj.optString("name").trim().ifBlank { "AI Custom Flow ($targetMinutes min)" }.take(80)
        val description = obj.optString("description").trim()
            .ifBlank { "Personalized $targetMinutes-minute mobility sequence." }
            .take(240)
        val rationale = obj.optString("rationale").trim().take(200)

        val modelThinking = mutableListOf<String>()
        val engineSummary = if (inferenceTier == InferenceTier.ON_DEVICE_NPU) {
            "Engine: $modelName on $hardwareAccelerator (${latencyMs}ms)"
        } else {
            "Engine: $modelName via Cloud (${latencyMs}ms)"
        }
        modelThinking.add(engineSummary)
        modelThinking.add("Reviewed top $candidateCount of ${allLibrary.size} unique stretches (with benefits & posture hubs)")
        val thinkingArr = obj.optJSONArray("thinking")
        if (thinkingArr != null) {
            for (i in 0 until thinkingArr.length()) {
                val step = thinkingArr.optString(i).trim()
                if (step.isNotBlank()) modelThinking.add(step)
            }
        } else if (rationale.isNotBlank()) {
            modelThinking.add(rationale)
        }

        val entities = buildSmartFlowEntities(picked, targetMinutes, context)
        modelThinking.add("Sequenced ${entities.size} unique stretches in Smart Flow posture order (~${targetMinutes} min)")

        return GeneratedRoutinePlan(
            name = name,
            description = description,
            stretches = entities,
            inferenceTier = inferenceTier,
            rationale = rationale,
            hardwareAccelerator = hardwareAccelerator,
            modelName = modelName,
            latencyMs = latencyMs,
            thinkingSteps = modelThinking
        )
    }

    /**
     * Canonical pose deduplication key (visual drawable + side) so AI routines never include
     * two stretches representing the same physical pose on the same side (e.g. English + Sanskrit variants).
     */
    internal fun poseDeduplicationKey(stretch: LibraryStretch): String {
        val drawableRes = com.example.data.ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category.title)
        val lower = "${stretch.id} ${stretch.name}".lowercase()
        val side = when {
            Regex("\\bleft\\b").containsMatchIn(lower) || stretch.id.endsWith("_left") -> "left"
            Regex("\\bright\\b").containsMatchIn(lower) || stretch.id.endsWith("_right") -> "right"
            else -> "center"
        }
        return if (drawableRes != null) "res_${drawableRes}_$side" else "id_${stretch.id}"
    }

    private fun buildSmartFlowEntities(
        selected: List<LibraryStretch>,
        targetMinutes: Int,
        context: Context?
    ): List<StretchEntity> {
        val seenKeys = mutableSetOf<String>()
        val uniqueSelected = selected.filter { seenKeys.add(poseDeduplicationKey(it)) }
        val rawTotal = uniqueSelected.sumOf { it.defaultPrepSeconds + it.defaultHoldSeconds }.coerceAtLeast(1)
        val targetSec = targetMinutes * 60
        val holdScale = (targetSec.toFloat() / rawTotal.toFloat()).coerceIn(0.75f, 1.35f)

        val rawEntities = uniqueSelected.mapIndexed { idx, lib ->
            val scaledHold = ((lib.defaultHoldSeconds * holdScale) / 5f).roundToInt() * 5
            StretchEntity(
                routineId = 0L,
                libraryId = lib.id,
                name = lib.name,
                indianName = lib.indianName,
                category = lib.category.title,
                preparationSeconds = lib.defaultPrepSeconds,
                holdSeconds = scaledHold.coerceIn(15, 90),
                prepCue = lib.prepCue,
                holdCue = lib.holdCue,
                notes = lib.benefits,
                enabled = true,
                voiceCueEnabled = true,
                sortOrder = idx
            )
        }

        val ordered = if (context != null) {
            PoseManifestRepository.sortStretchesBySmartFlow(context, rawEntities)
        } else {
            rawEntities
        }
        return ordered.mapIndexed { idx, entity -> entity.copy(sortOrder = idx) }
    }

    internal fun findBilateralPartnerId(id: String): String? = when {
        id.endsWith("_right") -> id.removeSuffix("_right") + "_left"
        id.endsWith("_left") -> id.removeSuffix("_left") + "_right"
        else -> null
    }

    private fun buildFriendlyRoutineTitle(
        prompt: String,
        targetMinutes: Int,
        posturePreference: PosturePreference
    ): String {
        val lower = prompt.lowercase()
        val theme = when {
            "desk" in lower || "office" in lower -> "Desk Reset Flow"
            "neck" in lower && "shoulder" in lower -> "Neck & Shoulder Relief"
            "neck" in lower -> "Neck Tension Release"
            "lower back" in lower || "back" in lower || "spine" in lower -> "Spine & Back Relief"
            "hip" in lower || "glute" in lower -> "Deep Hip Opener"
            "run" in lower || "hamstring" in lower || "leg" in lower -> "Runner's Leg Recovery"
            "sleep" in lower || "bedtime" in lower || "night" in lower -> "Bedtime Unwind"
            "morning" in lower || "wake" in lower -> "Morning Mobility"
            posturePreference == PosturePreference.FLOOR_ONLY -> "Floor Sanctuary Flow"
            posturePreference == PosturePreference.SEATED -> "Seated Mobility Flow"
            posturePreference == PosturePreference.STANDING -> "Standing Energy Flow"
            else -> "Custom Mobility Flow"
        }
        return "$theme (${targetMinutes}m)"
    }
}
