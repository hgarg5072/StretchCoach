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
) {
    /** Mirrors horizontal anchor edges when Layer 0 is flipped (`scaleX = -1`) for Left-side unilateral poses. */
    fun mirroredHorizontally(): PoseOverlayAnchors {
        fun OverlayAnchorRect.flipX(): OverlayAnchorRect = copy(
            leftPct = rightPct,
            rightPct = leftPct
        )
        return copy(
            dog = dog.flipX(),
            floorProp = floorProp.flipX()
        )
    }
}

data class RoamLane(
    val minXPct: Float,     // Left edge of pet's anchor in % of videoWidth (0..100)
    val maxXPct: Float,     // Right edge of pet's anchor in % of videoWidth (0..100)
    val bottomPct: Float,   // Distance from bottom of video stage in %
    val widthPct: Float,    // Pet sprite width in % of videoWidth
    val side: String = "left"
) {
    val spanPct: Float get() = (maxXPct - minXPct).coerceAtLeast(0f)

    fun mirroredHorizontally(): RoamLane {
        val newMin = (100f - maxXPct - widthPct).coerceIn(1f, 95f)
        val newMax = (100f - minXPct - widthPct).coerceIn(newMin, 96f)
        val flippedSide = when (side.lowercase()) {
            "left" -> "right"
            "right" -> "left"
            else -> side
        }
        return copy(minXPct = newMin, maxXPct = newMax, side = flippedSide)
    }
}

data class PoseRoamConfig(
    val landscapeLanes: List<RoamLane>,
    val portraitLanes: List<RoamLane>,
    val canRoamLandscape: Boolean = true,
    val canRoamPortrait: Boolean = true,
    val visibleLandscape: Boolean = true,
    val visiblePortrait: Boolean = true
) {
    val canRoam: Boolean get() = canRoamLandscape || canRoamPortrait
}

data class PoseManifestEntry(
    val id: String,
    val displayName: String,
    val postureHub: PostureHub,
    val category: String,
    val isUnilateral: Boolean,
    val roomLayout: String = "standard_center",
    val overlayAnchors: PoseOverlayAnchors = PoseOverlayAnchors(),
    val portraitOverlayAnchors: PoseOverlayAnchors? = null,
    val isFlowMode: Boolean = false,
    val defaultRounds: Int = 6,
    val defaultPlaybackSpeed: Float = 0.75f,
    val customRoamConfig: PoseRoamConfig? = null
) {
    val isRestorativeOrPranayama: Boolean
        get() = category.equals("restorative", ignoreCase = true) ||
            category.equals("pranayama", ignoreCase = true) ||
            category.equals("meditation", ignoreCase = true)

    fun anchorsFor(isPortrait: Boolean, mirroredLeft: Boolean = false): PoseOverlayAnchors {
        val base = if (isPortrait) (portraitOverlayAnchors ?: overlayAnchors) else overlayAnchors
        return if (mirroredLeft && isUnilateral && portraitOverlayAnchors != null) {
            base.mirroredHorizontally()
        } else {
            base
        }
    }
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
    private const val ROAM_LANES_ASSET = "pet_roam_lanes.json"
    private const val TRANSITIONS_DIR = "stretch_transitions"

    @Volatile
    private var loaded = false
    private var posesByStem: Map<String, PoseManifestEntry> = emptyMap()
    private var transitionsByKey: Map<String, HubTransitionSpec> = emptyMap()
    private var roamLanesByStem: Map<String, PoseRoamConfig> = emptyMap()

    fun ensureLoaded(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            runCatching {
                val jsonText = context.assets.open(MANIFEST_ASSET).bufferedReader().use { it.readText() }
                parseManifestJson(jsonText)
                runCatching {
                    val roamText = context.assets.open(ROAM_LANES_ASSET).bufferedReader().use { it.readText() }
                    parsePetRoamLanesJson(roamText)
                }.onFailure { e ->
                    Log.w(TAG, "Failed to load pet_roam_lanes.json: ${e.message}")
                }
                loaded = true
            }.onFailure { e ->
                Log.w(TAG, "Failed to load pose_manifest.json: ${e.message}")
            }
        }
    }

    internal fun parsePetRoamLanesJson(jsonText: String) {
        val root = JSONObject(jsonText)
        val posesObj = root.optJSONObject("poses") ?: return
        val map = mutableMapOf<String, PoseRoamConfig>()
        val keys = posesObj.keys()
        while (keys.hasNext()) {
            val stem = keys.next()
            val item = posesObj.optJSONObject(stem) ?: continue
            map[stem] = parseRoamConfig(item)
        }
        roamLanesByStem = map
    }

    fun totalLoadedRoamLanes(): Int = roamLanesByStem.size

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
                val landscapeObj = anchorsObj?.optJSONObject("landscape") ?: anchorsObj
                val portraitObj = anchorsObj?.optJSONObject("portrait")
                val parsedAnchors = if (landscapeObj != null) {
                    PoseOverlayAnchors(
                        dog = parseAnchor(landscapeObj.optJSONObject("dog"), PoseOverlayAnchors().dog),
                        floorProp = parseAnchor(landscapeObj.optJSONObject("floor_prop"), PoseOverlayAnchors().floorProp),
                        windChime = parseAnchor(landscapeObj.optJSONObject("wind_chime"), PoseOverlayAnchors().windChime)
                    )
                } else {
                    PoseOverlayAnchors()
                }
                val parsedPortraitAnchors = portraitObj?.let { pObj ->
                    PoseOverlayAnchors(
                        dog = parseAnchor(pObj.optJSONObject("dog"), parsedAnchors.dog),
                        floorProp = parseAnchor(pObj.optJSONObject("floor_prop"), parsedAnchors.floorProp),
                        windChime = parseAnchor(pObj.optJSONObject("wind_chime"), parsedAnchors.windChime)
                    )
                }
                val roamObj = p.optJSONObject("safe_roam_lanes")
                val customRoam = roamObj?.let { parseRoamConfig(it) }
                poseMap[id] = PoseManifestEntry(
                    id = id,
                    displayName = p.optString("display_name").trim(),
                    postureHub = PostureHub.fromString(p.optString("posture_hub")),
                    category = p.optString("category").trim(),
                    isUnilateral = p.optBoolean("is_unilateral", false),
                    roomLayout = p.optString("room_layout", "standard_center").trim(),
                    overlayAnchors = parsedAnchors,
                    portraitOverlayAnchors = parsedPortraitAnchors,
                    isFlowMode = p.optBoolean("is_flow_mode", false),
                    defaultRounds = p.optInt("default_rounds", 6).coerceIn(1, 24),
                    defaultPlaybackSpeed = p.optDouble("default_playback_speed", 0.75).toFloat().coerceIn(0.5f, 1.5f),
                    customRoamConfig = customRoam
                )
            }
        }

        transitionsByKey = transMap
        posesByStem = poseMap
    }

    private fun parseAnchor(obj: JSONObject?, fallback: OverlayAnchorRect): OverlayAnchorRect {
        if (obj == null) return fallback
        fun readPct(key: String): Float? {
            if (!obj.has(key)) return null
            val raw = obj.optDouble(key).toFloat()
            return if (raw > 0f && raw <= 1f) raw * 100f else raw
        }
        return OverlayAnchorRect(
            leftPct = readPct("left_pct"),
            rightPct = readPct("right_pct"),
            topPct = readPct("top_pct"),
            bottomPct = readPct("bottom_pct"),
            widthPct = readPct("width_pct") ?: fallback.widthPct,
            visible = obj.optBoolean("visible", fallback.visible)
        )
    }

    private fun parseRoamConfig(obj: JSONObject): PoseRoamConfig {
        fun parseLaneArray(vararg keys: String): List<RoamLane> {
            val arr = keys.firstNotNullOfOrNull { obj.optJSONArray(it) } ?: return emptyList()
            val out = mutableListOf<RoamLane>()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i) ?: continue
                out.add(
                    RoamLane(
                        minXPct = item.optDouble("min_x_pct", 3.0).toFloat(),
                        maxXPct = item.optDouble("max_x_pct", 16.0).toFloat(),
                        bottomPct = item.optDouble("bottom_pct", 2.5).toFloat(),
                        widthPct = item.optDouble("width_pct", 18.0).toFloat(),
                        side = item.optString("side", "left").trim()
                    )
                )
            }
            return out
        }
        val landscape = parseLaneArray("landscape_lanes", "landscape")
        val portrait = parseLaneArray("portrait_lanes", "portrait")
        val legacyCanRoam = obj.optBoolean("can_roam", true)
        return PoseRoamConfig(
            landscapeLanes = landscape,
            portraitLanes = portrait,
            canRoamLandscape = obj.optBoolean("can_roam_landscape", legacyCanRoam),
            canRoamPortrait = obj.optBoolean("can_roam_portrait", legacyCanRoam),
            visibleLandscape = obj.optBoolean("visible_landscape", landscape.isNotEmpty()),
            visiblePortrait = obj.optBoolean("visible_portrait", portrait.isNotEmpty())
        )
    }

    /**
     * Resolves exact pixel-measured person-safe roaming corridors from `pet_roam_lanes.json`
     * (falling back to layout/hub rules), clamping right/full lanes against the active floor prop
     * and mirroring horizontal coordinates when Layer 0 is flipped for Left-side unilateral poses.
     */
    fun resolveRoamConfig(
        entry: PoseManifestEntry?,
        isEmptySanctuary: Boolean = false,
        mirroredLeft: Boolean = false,
        hasActiveFloorProp: Boolean = false,
        propRightPct: Float = 2f,
        propWidthPct: Float = 14f
    ): PoseRoamConfig {
        val exactFromJson = when {
            isEmptySanctuary -> roamLanesByStem["img_stretch_empty_studio_sanctuary"]
            entry != null -> roamLanesByStem[entry.id]
            else -> null
        }

        val base = exactFromJson ?: when {
            isEmptySanctuary -> PoseRoamConfig(
                landscapeLanes = listOf(RoamLane(minXPct = 3f, maxXPct = 76f, bottomPct = 2.5f, widthPct = 18f, side = "full")),
                portraitLanes = listOf(RoamLane(minXPct = 3f, maxXPct = 70f, bottomPct = 2.5f, widthPct = 24f, side = "full")),
                canRoamLandscape = true,
                canRoamPortrait = true,
                visibleLandscape = true,
                visiblePortrait = true
            )
            entry?.customRoamConfig != null -> entry.customRoamConfig
            entry == null -> PoseRoamConfig(
                landscapeLanes = listOf(RoamLane(minXPct = 4f, maxXPct = 16f, bottomPct = 2.5f, widthPct = 18f)),
                portraitLanes = listOf(RoamLane(minXPct = 4f, maxXPct = 14f, bottomPct = 2.5f, widthPct = 24f))
            )
            entry.roomLayout.equals("wide_floor", ignoreCase = true) -> PoseRoamConfig(
                landscapeLanes = listOf(RoamLane(minXPct = 2f, maxXPct = 9f, bottomPct = 2f, widthPct = 15f)),
                portraitLanes = listOf(RoamLane(minXPct = 3f, maxXPct = 3f, bottomPct = 2f, widthPct = 20f)),
                canRoamLandscape = false,
                canRoamPortrait = false
            )
            entry.roomLayout.equals("right_wall", ignoreCase = true) -> PoseRoamConfig(
                landscapeLanes = listOf(RoamLane(minXPct = 4f, maxXPct = 34f, bottomPct = 2.5f, widthPct = 18f)),
                portraitLanes = listOf(RoamLane(minXPct = 4f, maxXPct = 26f, bottomPct = 2.5f, widthPct = 24f))
            )
            entry.postureHub == PostureHub.STANDING -> PoseRoamConfig(
                landscapeLanes = listOf(RoamLane(minXPct = 3f, maxXPct = 26f, bottomPct = 2.5f, widthPct = 18f)),
                portraitLanes = listOf(RoamLane(minXPct = 3f, maxXPct = 16f, bottomPct = 2.5f, widthPct = 24f))
            )
            else -> PoseRoamConfig(
                landscapeLanes = listOf(RoamLane(minXPct = 4f, maxXPct = 16f, bottomPct = 2.5f, widthPct = 18f)),
                portraitLanes = listOf(RoamLane(minXPct = 4f, maxXPct = 14f, bottomPct = 2.5f, widthPct = 24f))
            )
        }

        val shouldMirror = mirroredLeft && (
            exactFromJson != null ||
                entry?.roomLayout.equals("right_wall", ignoreCase = true) ||
                entry?.customRoamConfig != null
        )

        fun adjustLane(lane: RoamLane): RoamLane {
            val oriented = if (shouldMirror) lane.mirroredHorizontally() else lane
            if (!hasActiveFloorProp) return oriented
            val needsRightClamp = oriented.side.equals("right", ignoreCase = true) ||
                oriented.side.equals("full", ignoreCase = true)
            if (!needsRightClamp) return oriented
            val maxSafeX = (100f - propRightPct - propWidthPct - oriented.widthPct - 2f)
                .coerceAtLeast(oriented.minXPct)
            return oriented.copy(maxXPct = oriented.maxXPct.coerceAtMost(maxSafeX))
        }

        return base.copy(
            landscapeLanes = base.landscapeLanes.map(::adjustLane),
            portraitLanes = base.portraitLanes.map(::adjustLane)
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
        // If a dedicated `_left.mp4` video is bundled (e.g. Child's Pose Lat Reach Left), do not double-mirror it
        val videoPath = ArtworkMapper.getVideoAssetPathForStretch(context, stretchName, category)
        if (videoPath != null && videoPath.endsWith("_left.mp4")) return false
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
