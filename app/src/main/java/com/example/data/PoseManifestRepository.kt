package com.example.data

import android.content.Context
import android.util.Log
import org.json.JSONObject

enum class PostureHub(val flowRank: Int) {
    STANDING(0),
    KNEELING(1),
    PRONE(2),
    SUPINE(3),
    SEATED(4);

    companion object {
        fun fromString(raw: String?): PostureHub =
            values().firstOrNull { it.name.equals(raw?.trim(), ignoreCase = true) } ?: STANDING
    }
}

data class OverlayAnchorRect(
    val leftPct: Float? = null,
    val rightPct: Float? = null,
    val topPct: Float? = null,
    val bottomPct: Float? = null,
    val widthPct: Float = 16f,
    val visible: Boolean = true
)

data class PoseOverlayAnchors(
    val dog: OverlayAnchorRect = OverlayAnchorRect(leftPct = 2f, bottomPct = 1.5f, widthPct = 18f, visible = true),
    val floorProp: OverlayAnchorRect = OverlayAnchorRect(rightPct = 2f, bottomPct = 1.5f, widthPct = 15f, visible = true),
    val windChime: OverlayAnchorRect = OverlayAnchorRect(leftPct = 3f, topPct = 0f, widthPct = 12f, visible = true)
)

data class PoseManifestEntry(
    val id: String,
    val displayName: String,
    val postureHub: PostureHub,
    val category: String,
    val isUnilateral: Boolean,
    val roomLayout: String = "standard_center",
    val overlayAnchors: PoseOverlayAnchors = PoseOverlayAnchors()
) {
    val isRestorativeOrPranayama: Boolean
        get() = category.equals("restorative", ignoreCase = true) ||
            category.equals("pranayama", ignoreCase = true) ||
            category.equals("meditation", ignoreCase = true)
}

data class HubTransitionSpec(
    val fromHub: PostureHub,
    val toHub: PostureHub,
    val assetPath: String,
    val cue: String
)

/**
 * Loads and indexes assets/pose_manifest.json (117 poses + 12 posture-hub transitions).
 * Used for:
 * - PREP-only 5-hub posture transitions (1-hop direct + 2-hop bridge routing)
 * - Unilateral Left-side mirroring (scaleX = -1 on L0 only)
 * - Companion dog auto-nap detection on restorative/pranayama poses
 * - 1-tap "Optimize Flow" routine sorting
 */
object PoseManifestRepository {

    private const val TAG = "PoseManifestRepo"
    private const val MANIFEST_ASSET = "pose_manifest.json"
    private const val TRANSITIONS_DIR = "stretch_transitions"

    @Volatile
    private var loaded = false
    private var posesByStem: Map<String, PoseManifestEntry> = emptyMap()
    private var transitionsByKey: Map<String, HubTransitionSpec> = emptyMap()

    fun ensureLoaded(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            runCatching {
                val jsonText = context.assets.open(MANIFEST_ASSET).bufferedReader().use { it.readText() }
                parseManifestJson(jsonText)
                loaded = true
            }.onFailure { e ->
                Log.w(TAG, "Failed to load pose_manifest.json: ${e.message}")
            }
        }
    }

    internal fun parseManifestJson(jsonText: String) {
        val root = JSONObject(jsonText)

        // Parse 12 hub transitions
        val transMap = mutableMapOf<String, HubTransitionSpec>()
        val transObj = root.optJSONObject("hub_transitions")
        if (transObj != null) {
            val keys = transObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val item = transObj.optJSONObject(key) ?: continue
                val parts = key.split("->")
                if (parts.size != 2) continue
                val fromHub = PostureHub.fromString(parts[0])
                val toHub = PostureHub.fromString(parts[1])
                val file = item.optString("file").trim()
                val cue = item.optString("cue").trim()
                if (file.isNotBlank()) {
                    transMap[key] = HubTransitionSpec(
                        fromHub = fromHub,
                        toHub = toHub,
                        assetPath = "$TRANSITIONS_DIR/$file",
                        cue = cue
                    )
                }
            }
        }

        // Parse 117 poses indexed by drawable stem ("id")
        val poseMap = mutableMapOf<String, PoseManifestEntry>()
        val posesArr = root.optJSONArray("poses")
        if (posesArr != null) {
            for (i in 0 until posesArr.length()) {
                val p = posesArr.optJSONObject(i) ?: continue
                val id = p.optString("id").trim()
                if (id.isBlank()) continue
                val anchorsObj = p.optJSONObject("overlay_anchors")
                val parsedAnchors = if (anchorsObj != null) {
                    PoseOverlayAnchors(
                        dog = parseAnchor(anchorsObj.optJSONObject("dog"), PoseOverlayAnchors().dog),
                        floorProp = parseAnchor(anchorsObj.optJSONObject("floor_prop"), PoseOverlayAnchors().floorProp),
                        windChime = parseAnchor(anchorsObj.optJSONObject("wind_chime"), PoseOverlayAnchors().windChime)
                    )
                } else {
                    PoseOverlayAnchors()
                }
                poseMap[id] = PoseManifestEntry(
                    id = id,
                    displayName = p.optString("display_name").trim(),
                    postureHub = PostureHub.fromString(p.optString("posture_hub")),
                    category = p.optString("category").trim(),
                    isUnilateral = p.optBoolean("is_unilateral", false),
                    roomLayout = p.optString("room_layout", "standard_center").trim(),
                    overlayAnchors = parsedAnchors
                )
            }
        }

        transitionsByKey = transMap
        posesByStem = poseMap
    }

    private fun parseAnchor(obj: JSONObject?, fallback: OverlayAnchorRect): OverlayAnchorRect {
        if (obj == null) return fallback
        return OverlayAnchorRect(
            leftPct = if (obj.has("left_pct")) obj.optDouble("left_pct").toFloat() else null,
            rightPct = if (obj.has("right_pct")) obj.optDouble("right_pct").toFloat() else null,
            topPct = if (obj.has("top_pct")) obj.optDouble("top_pct").toFloat() else null,
            bottomPct = if (obj.has("bottom_pct")) obj.optDouble("bottom_pct").toFloat() else null,
            widthPct = if (obj.has("width_pct")) obj.optDouble("width_pct").toFloat() else fallback.widthPct,
            visible = obj.optBoolean("visible", fallback.visible)
        )
    }

    fun getPoseMetaByStem(context: Context, stem: String?): PoseManifestEntry? {
        if (stem.isNullOrBlank()) return null
        ensureLoaded(context)
        return posesByStem[stem]
    }

    fun getPoseMetaForStretch(context: Context, name: String, category: String = ""): PoseManifestEntry? {
        val drawableRes = ArtworkMapper.getDrawableForStretch(name, category) ?: return null
        val stem = runCatching { context.resources.getResourceEntryName(drawableRes) }.getOrNull() ?: return null
        return getPoseMetaByStem(context, stem)
    }

    fun totalLoadedPoses(): Int = posesByStem.size
    fun totalLoadedTransitions(): Int = transitionsByKey.size

    /**
     * Resolves 1-hop or 2-hop posture transition sequence between [fromHub] and [toHub].
     * Returns empty list if hubs are identical.
     */
    fun resolveTransitionRoute(fromHub: PostureHub, toHub: PostureHub): List<HubTransitionSpec> {
        if (fromHub == toHub) return emptyList()

        val directKey = "${fromHub.name}->${toHub.name}"
        transitionsByKey[directKey]?.let { return listOf(it) }

        // 2-hop routing for non-adjacent hubs (e.g., STANDING -> PRONE via KNEELING, STANDING -> SUPINE via SEATED)
        val bridgeHubs = listOf(PostureHub.KNEELING, PostureHub.SEATED, PostureHub.PRONE, PostureHub.SUPINE)
        for (mid in bridgeHubs) {
            if (mid == fromHub || mid == toHub) continue
            val hop1 = transitionsByKey["${fromHub.name}->${mid.name}"]
            val hop2 = transitionsByKey["${mid.name}->${toHub.name}"]
            if (hop1 != null && hop2 != null) {
                return listOf(hop1, hop2)
            }
        }
        return emptyList()
    }

    /**
     * Returns true when a stretch represents the Left side of a unilateral pose
     * so Layer 0 can be horizontally mirrored (scaleX = -1) while Layers 1..4 remain unmirrored.
     */
    fun shouldMirrorForLeftSide(context: Context, stretchName: String, category: String = ""): Boolean {
        val lower = stretchName.lowercase()
        val mentionsLeft = Regex("\\bleft\\b").containsMatchIn(lower) && !Regex("\\bright\\b").containsMatchIn(lower)
        if (!mentionsLeft) return false
        val meta = getPoseMetaForStretch(context, stretchName, category)
        return meta?.isUnilateral ?: true
    }

    /**
     * Sorts stretches into Smart Flow Order: STANDING -> KNEELING -> PRONE -> SUPINE -> SEATED,
     * preserving relative order and keeping symmetrical Right/Left pairs adjacent.
     */
    fun sortStretchesBySmartFlow(context: Context, stretches: List<StretchEntity>): List<StretchEntity> {
        ensureLoaded(context)
        return stretches
            .withIndex()
            .sortedWith(
                compareBy<IndexedValue<StretchEntity>> { item ->
                    val hub = getPoseMetaForStretch(context, item.value.name, item.value.category)?.postureHub
                        ?: PostureHub.STANDING
                    hub.flowRank
                }.thenBy { it.index }
            )
            .map { it.value }
    }
}
