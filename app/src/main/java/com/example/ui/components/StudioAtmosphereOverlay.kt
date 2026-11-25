package com.example.ui.components

import android.app.ActivityManager
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.CompanionPet
import com.example.data.PoseManifestRepository
import com.example.data.PoseOverlayAnchors
import com.example.data.PoseRoamConfig
import com.example.data.RoamLane
import com.example.data.StudioAromaProp
import com.example.data.StudioLightingMode
import com.example.ui.PlayerState
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Persistent Studio Ghibli Multi-Layer Atmosphere Overlay (STRETCHCOACH_UPGRADE_AND_BATCH5_SPEC.md):
 *
 * - Layer 1A: Glass Furin Wind Chime by top-left window (Day/Sunset; skipped on low-RAM devices)
 * - Layer 1B: Studio Floor Prop (AUTO_GHIBLI -> Tea Set in Day, Clay Candle Warmer in Sunset,
 *             Japanese Washi Lantern in Night — or manual override) anchored via `poseAnchors.floorProp`
 * - Layer 1C: All 8 Golden Retriever Puppy animations (`paw_wave`, `playful_stretch`, `belly_roll`,
 *             `head_tilt_curious`, `yawn_sleepy`, `nap_curled`, `idle_rest`, `celebrate`)
 * - Layer 2: Unified Day / Sunset / Night Studio Lighting Wash with lantern radial glow anchored to floor prop
 * - Layer 3: 0 MB Procedural Air Canvas Layer (18 warm floating dust motes in Day/Sunset,
 *            9 soft pulsing golden corner fireflies in Night)
 */
@Composable
fun StudioAtmosphereOverlay(
    playerState: PlayerState,
    isTransitioning: Boolean,
    isRestorativePose: Boolean,
    lightingMode: StudioLightingMode,
    aromaProp: StudioAromaProp,
    companionPet: CompanionPet = CompanionPet.COZY_MAT_PUPPY,
    leavesEnabled: Boolean,
    sunbeamsEnabled: Boolean,
    poseAnchors: PoseOverlayAnchors = PoseOverlayAnchors(),
    roamConfig: PoseRoamConfig = PoseManifestRepository.resolveRoamConfig(null),
    stretchIndex: Int = 0,
    totalStretches: Int = 0,
    isWidePose: Boolean = false,
    overlayAlpha: Float = 1f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLowRamDevice = remember(context) {
        runCatching {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am?.isLowRamDevice == true
        }.getOrDefault(false)
    }

    val resolvedLighting = lightingMode.resolveForCurrentHour()
    val isPlaying = playerState != PlayerState.PAUSED && playerState != PlayerState.IDLE

    // Studio Floor Prop (AUTO_GHIBLI -> Tea Set in Day, Clay Candle Warmer in Sunset, Washi Lantern in Night)
    val resolvedPropAsset: String? = remember(aromaProp, resolvedLighting) {
        val effectiveProp = when (aromaProp) {
            StudioAromaProp.AUTO_GHIBLI -> when (resolvedLighting) {
                StudioLightingMode.SUNSET -> StudioAromaProp.CANDLE_WARMER
                StudioLightingMode.NIGHT -> StudioAromaProp.WASHI_LANTERN
                else -> StudioAromaProp.TEA_SET
            }
            else -> aromaProp
        }
        when (effectiveProp) {
            StudioAromaProp.TEA_SET -> "stretch_overlays/overlay_ghibli_tea_set_chromakey.mp4"
            StudioAromaProp.CANDLE_WARMER -> "stretch_overlays/overlay_ghibli_candle_warmer_chromakey.mp4"
            StudioAromaProp.WASHI_LANTERN -> "stretch_overlays/overlay_ghibli_washi_lantern_chromakey.mp4"
            StudioAromaProp.NONE, StudioAromaProp.AUTO_GHIBLI -> null
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
    ) {
        val stageWidth = maxWidth
        val stageHeight = maxHeight
        val isPortrait = stageHeight > stageWidth
        val portraitAspect = if (isWidePose) (16f / 9f) else 1.18f
        val portraitStageHeight = stageWidth / portraitAspect

        // Anchor overlays strictly to the centered 16:9 videoRect in Landscape so tapping to hide/show
        // the right-hand controls rail NEVER shifts the dog, aroma prop, or wind chime
        val videoWidth = if (isPortrait) stageWidth else (stageHeight * (16f / 9f)).coerceAtMost(stageWidth)
        val videoLeft = ((stageWidth - videoWidth) / 2f).coerceAtLeast(0.dp)

        // --- LAYER 1A: Glass Furin Wind Chime (Day & Sunset; disabled on low-RAM devices to cap decoders <= 2) ---
        if (leavesEnabled && !isLowRamDevice && resolvedLighting != StudioLightingMode.NIGHT && poseAnchors.windChime.visible) {
            val chimeLeftFrac = (poseAnchors.windChime.leftPct ?: 2.5f).coerceIn(1f, 12f) / 100f
            val chimeModifier = if (isPortrait) {
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(
                        x = (stageWidth * chimeLeftFrac).coerceAtLeast(10.dp),
                        y = -(portraitStageHeight * 0.40f) - 14.dp
                    )
                    .width((stageWidth * 0.14f).coerceIn(44.dp, 62.dp))
                    .aspectRatio(0.85f)
            } else {
                Modifier
                    .align(Alignment.TopStart)
                    .offset(x = videoLeft + (videoWidth * chimeLeftFrac) + 8.dp, y = 4.dp)
                    .width((videoWidth * 0.095f).coerceIn(42.dp, 68.dp))
                    .aspectRatio(0.85f)
            }
            ChromaKeyVideoOverlay(
                assetPath = "stretch_overlays/overlay_ghibli_wind_chime_chromakey.mp4",
                keyMode = OverlayKeyMode.BLUE_KEY,
                lightingMode = resolvedLighting,
                opacity = 0.92f * overlayAlpha,
                isPlaying = isPlaying,
                modifier = chimeModifier
            )
        }

        // --- LAYER 1B: Studio Floor Prop (Tea Set / Candle Warmer / Washi Lantern) ---
        // Positioned strictly via per-pose `poseAnchors.floorProp` inside `videoRect`
        val propAnchor = poseAnchors.floorProp
        val propOnLeftWall = propAnchor.leftPct != null && propAnchor.rightPct == null
        val propRightFrac = (propAnchor.rightPct ?: 2.5f).coerceIn(1.5f, 8f) / 100f
        val propLeftFrac = (propAnchor.leftPct ?: 18f).coerceIn(10f, 30f) / 100f

        if (resolvedPropAsset != null && propAnchor.visible) {
            val anchorWidthFrac = (propAnchor.widthPct / 100f).coerceIn(0.11f, 0.16f)
            val targetPropWidth: Dp = if (isPortrait) {
                (stageWidth * (anchorWidthFrac * 1.35f).coerceIn(0.16f, 0.21f)).coerceIn(60.dp, 84.dp)
            } else {
                (videoWidth * anchorWidthFrac).coerceIn(58.dp, 92.dp)
            }
            val animPropWidth by animateDpAsState(targetPropWidth, tween(360), label = "prop_w")

            val targetPropX: Dp = if (propOnLeftWall) {
                videoLeft + (videoWidth * propLeftFrac)
            } else {
                val rightMargin = if (isPortrait) 8.dp else (videoWidth * propRightFrac).coerceIn(10.dp, 28.dp)
                (videoLeft + videoWidth - animPropWidth - rightMargin).coerceAtLeast(videoLeft + 6.dp)
            }
            val animPropX by animateDpAsState(targetPropX, tween(360), label = "prop_x")

            val targetPropY: Dp = if (isPortrait) {
                (portraitStageHeight * 0.44f) - 14.dp
            } else {
                if (propOnLeftWall) (stageHeight * 0.24f) else 54.dp
            }
            val animPropY by animateDpAsState(targetPropY, tween(360), label = "prop_y")

            ChromaKeyVideoOverlay(
                assetPath = resolvedPropAsset,
                keyMode = OverlayKeyMode.BLUE_KEY,
                lightingMode = resolvedLighting,
                opacity = 1.0f * overlayAlpha,
                isPlaying = isPlaying,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = animPropX, y = animPropY)
                    .width(animPropWidth)
                    .aspectRatio(1f)
            )
        }

        // --- LAYER 1C: Companion Pet (Cozy Mat Puppy OR 12-Action Roaming Puppy / Calico Cat) ---
        if (companionPet == CompanionPet.COZY_MAT_PUPPY && poseAnchors.dog.visible) {
            val cozyDogAssetPath = remember(
                playerState, isTransitioning, isRestorativePose, resolvedLighting, stretchIndex, totalStretches
            ) {
                val isPenultimate = totalStretches >= 3 && stretchIndex == totalStretches - 2
                when {
                    playerState == PlayerState.COMPLETE ->
                        "stretch_overlays/overlay_dog_celebrate_chromakey.mp4"
                    playerState == PlayerState.PREPARING && stretchIndex == 0 ->
                        "stretch_overlays/overlay_dog_paw_wave_chromakey.mp4"
                    isTransitioning || playerState == PlayerState.PREPARING ->
                        if (stretchIndex % 2 == 1) "stretch_overlays/overlay_dog_belly_roll_chromakey.mp4"
                        else "stretch_overlays/overlay_dog_playful_stretch_chromakey.mp4"
                    isRestorativePose || resolvedLighting == StudioLightingMode.NIGHT ->
                        "stretch_overlays/overlay_dog_nap_curled_chromakey.mp4"
                    isPenultimate ->
                        "stretch_overlays/overlay_dog_yawn_sleepy_chromakey.mp4"
                    stretchIndex % 3 == 1 ->
                        "stretch_overlays/overlay_dog_head_tilt_curious_chromakey.mp4"
                    else ->
                        "stretch_overlays/overlay_dog_idle_rest_chromakey.mp4"
                }
            }
            val dogAnchor = poseAnchors.dog
            val dogWidthFrac = ((dogAnchor.widthPct / 100f) * 1.15f).coerceIn(0.14f, 0.21f)
            val targetDogWidth: Dp = if (isPortrait) {
                (stageWidth * 0.29f).coerceIn(100.dp, 132.dp)
            } else {
                (videoWidth * dogWidthFrac).coerceIn(86.dp, 132.dp)
            }
            val animDogWidth by animateDpAsState(targetDogWidth, tween(360), label = "cozy_dog_w")

            val dogLeftFrac = (dogAnchor.leftPct ?: 2f).coerceIn(1f, 6f) / 100f
            val dogLeftDp = if (isPortrait) {
                8.dp
            } else {
                videoLeft + (videoWidth * dogLeftFrac).coerceIn(8.dp, 24.dp)
            }
            val animDogX by animateDpAsState(dogLeftDp, tween(360), label = "cozy_dog_x")

            val cozyDogModifier = if (isPortrait) {
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = animDogX, y = (portraitStageHeight * 0.44f) - 14.dp)
                    .width(animDogWidth)
                    .aspectRatio(1f)
            } else {
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = animDogX)
                    .padding(bottom = 16.dp)
                    .width(animDogWidth)
                    .aspectRatio(1f)
            }

            ChromaKeyVideoOverlay(
                assetPath = cozyDogAssetPath,
                keyMode = OverlayKeyMode.BLUE_KEY,
                lightingMode = resolvedLighting,
                opacity = 1.0f * overlayAlpha,
                isPlaying = isPlaying,
                modifier = cozyDogModifier
            )
        } else {
            val petVisibleForOrientation = if (isPortrait) roamConfig.visiblePortrait else roamConfig.visibleLandscape
            val canRoamForOrientation = petVisibleForOrientation &&
                (if (isPortrait) roamConfig.canRoamPortrait else roamConfig.canRoamLandscape)
            val lanes = if (isPortrait) roamConfig.portraitLanes else roamConfig.landscapeLanes
            // Always show the chosen pet: roam along exact lane when open, or rest quietly in bottom-left corner on wide poses
            val fallbackCornerLane = RoamLane(
                minXPct = 1.5f,
                maxXPct = 1.5f,
                bottomPct = 2.0f,
                widthPct = if (isPortrait) 19f else 14f,
                side = "left"
            )
            val activeLane = if (petVisibleForOrientation) {
                lanes.firstOrNull() ?: fallbackCornerLane
            } else {
                fallbackCornerLane
            }

            if (companionPet != CompanionPet.OFF && (petVisibleForOrientation || poseAnchors.dog.visible)) {
                val isCat = companionPet == CompanionPet.STUDIO_CAT
                val petKeyMode = if (isCat) OverlayKeyMode.GREEN_KEY else OverlayKeyMode.BLUE_KEY
                val walkClip = if (isCat) "stretch_overlays/overlay_cat_walk_roam_chromakey.mp4"
                else "stretch_overlays/overlay_dog_roam_walk_chromakey.mp4"
                val napClip = if (isCat) "stretch_overlays/overlay_cat_nap_curled_chromakey.mp4"
                else "stretch_overlays/overlay_dog_roam_nap_curled_chromakey.mp4"
                val celebrateClip = if (isCat) "stretch_overlays/overlay_cat_celebrate_chromakey.mp4"
                else "stretch_overlays/overlay_dog_roam_celebrate_chromakey.mp4"
                val transitionClip = if (isCat) "stretch_overlays/overlay_cat_big_stretch_chromakey.mp4"
                else "stretch_overlays/overlay_dog_roam_play_bow_chromakey.mp4"
                val sunsetClip = if (isCat) "stretch_overlays/overlay_cat_yawn_sleepy_chromakey.mp4"
                else "stretch_overlays/overlay_dog_roam_yawn_stretch_chromakey.mp4"

                val activeIdlePool = remember(isCat) {
                    if (isCat) listOf(
                        "stretch_overlays/overlay_cat_idle_loaf_chromakey.mp4",
                        "stretch_overlays/overlay_cat_paw_groom_chromakey.mp4",
                        "stretch_overlays/overlay_cat_curious_tilt_chromakey.mp4",
                        "stretch_overlays/overlay_cat_playful_bat_chromakey.mp4",
                        "stretch_overlays/overlay_cat_chase_butterfly_chromakey.mp4",
                        "stretch_overlays/overlay_cat_knead_paws_chromakey.mp4",
                        "stretch_overlays/overlay_cat_belly_flop_chromakey.mp4",
                        "stretch_overlays/overlay_cat_big_stretch_chromakey.mp4",
                        "stretch_overlays/overlay_cat_yawn_sleepy_chromakey.mp4"
                    ) else listOf(
                        "stretch_overlays/overlay_dog_roam_idle_sit_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_play_bow_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_belly_roll_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_sniff_explore_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_tail_chase_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_ear_scratch_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_head_tilt_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_paw_shake_chromakey.mp4",
                        "stretch_overlays/overlay_dog_roam_yawn_stretch_chromakey.mp4"
                    )
                }

            val petXPct = remember(companionPet) { Animatable(activeLane.minXPct) }
            var petAction by remember(companionPet) { mutableStateOf(activeIdlePool.first()) }
            var petMirrorX by remember(companionPet) { mutableStateOf(false) }

            // Re-clamp immediately when orientation or pose changes so the pet never overlaps a new person_bbox
            LaunchedEffect(activeLane) {
                val clamped = petXPct.value.coerceIn(activeLane.minXPct, activeLane.maxXPct)
                if (clamped != petXPct.value) {
                    petXPct.animateTo(clamped, tween(durationMillis = 350))
                }
            }

            LaunchedEffect(
                companionPet, playerState, isTransitioning, isRestorativePose,
                resolvedLighting, activeLane, canRoamForOrientation, stretchIndex
            ) {
                when {
                    playerState == PlayerState.COMPLETE -> {
                        petAction = celebrateClip
                        return@LaunchedEffect
                    }
                    isRestorativePose || resolvedLighting == StudioLightingMode.NIGHT -> {
                        petAction = napClip
                        return@LaunchedEffect
                    }
                    isTransitioning -> {
                        petAction = transitionClip
                        return@LaunchedEffect
                    }
                    playerState == PlayerState.PAUSED || playerState == PlayerState.IDLE -> {
                        return@LaunchedEffect
                    }
                }

                var variationIdx = stretchIndex % activeIdlePool.size
                while (true) {
                    // 1. Settle in the next idle variation for ~8 seconds
                    petAction = if (resolvedLighting == StudioLightingMode.SUNSET && variationIdx % 2 == 1) {
                        sunsetClip
                    } else {
                        activeIdlePool[variationIdx % activeIdlePool.size]
                    }
                    variationIdx++
                    delay(8_000L)

                    // 2. If roaming is allowed and lane has room, stroll to the opposite waypoint
                    if (canRoamForOrientation && activeLane.spanPct >= 8f) {
                        val currentX = petXPct.value
                        val mid = (activeLane.minXPct + activeLane.maxXPct) / 2f
                        val targetX = if (currentX <= mid) activeLane.maxXPct else activeLane.minXPct
                        val distancePct = abs(targetX - currentX)
                        if (distancePct >= 6f) {
                            petMirrorX = targetX < currentX
                            petAction = walkClip
                            // Gentle pet stroll speed: ~5.5% of stage width per second
                            val durationMs = ((distancePct / 5.5f) * 1000f).toInt().coerceIn(1_400, 9_500)
                            petXPct.animateTo(
                                targetValue = targetX,
                                animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
                            )
                        }
                    }
                }
            }

            val petWidthDp: Dp = if (isPortrait) {
                (stageWidth * ((activeLane.widthPct * 1.17f) / 100f)).coerceIn(88.dp, 132.dp)
            } else {
                (videoWidth * ((activeLane.widthPct * 1.17f) / 100f)).coerceIn(82.dp, 136.dp)
            }
            val maxSafeVideoX = (videoLeft + videoWidth - petWidthDp - 4.dp).coerceAtLeast(videoLeft + 4.dp)
            val petXOffset = (videoLeft + (videoWidth * (petXPct.value / 100f))).coerceIn(videoLeft + 2.dp, maxSafeVideoX)
            val petBottomPad = (stageHeight * (activeLane.bottomPct / 100f)).coerceIn(4.dp, 20.dp)

            val petModifier = if (isPortrait) {
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(
                        x = petXOffset.coerceIn(4.dp, (stageWidth - petWidthDp - 4.dp).coerceAtLeast(4.dp)),
                        y = (portraitStageHeight * 0.44f) - 14.dp
                    )
                    .width(petWidthDp)
                    .aspectRatio(1f)
            } else {
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = petXOffset)
                    .padding(bottom = petBottomPad)
                    .width(petWidthDp)
                    .aspectRatio(1f)
            }

            androidx.compose.runtime.key(companionPet) {
                ChromaKeyVideoOverlay(
                    assetPath = petAction,
                    keyMode = petKeyMode,
                    lightingMode = resolvedLighting,
                    opacity = 1.0f * overlayAlpha,
                    isPlaying = isPlaying,
                    mirrorX = petMirrorX,
                    modifier = petModifier
                )
            }
            }
        }

        // --- LAYER 2: Unified Day / Sunset / Night Lighting Shader Wash ---
        // Night mode centers a warm amber lantern bloom on `poseAnchors.floorProp` with a subtle indigo wash
        if (resolvedLighting != StudioLightingMode.DAY && overlayAlpha > 0.01f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                when (resolvedLighting) {
                    StudioLightingMode.SUNSET -> {
                        drawRect(color = Color(0xFFFF8C32).copy(alpha = 0.14f * overlayAlpha))
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFAF50).copy(alpha = 0.22f * overlayAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.15f, size.height * 0.25f),
                                radius = size.maxDimension * 0.8f
                            )
                        )
                    }
                    StudioLightingMode.NIGHT -> {
                        // Subtle twilight indigo wash so Night feels warm & candlelit rather than dim blue
                        drawRect(color = Color(0xFF0A1436).copy(alpha = 0.22f * overlayAlpha))
                        // Warm lantern glow centered on poseAnchors.floorProp
                        val propWFrac = (propAnchor.widthPct / 100f).coerceIn(0.10f, 0.18f)
                        val propBFrac = (propAnchor.bottomPct ?: 4f).coerceIn(1f, 15f) / 100f
                        val glowX = if (propOnLeftWall) {
                            size.width * (propLeftFrac + propWFrac * 0.5f)
                        } else {
                            size.width * (1f - propRightFrac - propWFrac * 0.5f)
                        }
                        val glowY = size.height * (1f - propBFrac - propWFrac * 0.6f).coerceIn(0.55f, 0.88f)
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFA53B).copy(alpha = 0.28f * overlayAlpha),
                                    Color(0xFFFFBE64).copy(alpha = 0.10f * overlayAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(glowX, glowY),
                                radius = size.maxDimension * 0.55f
                            )
                        )
                    }
                    else -> Unit
                }
            }
        }

        // --- LAYER 3: 0 MB Procedural Air Layer (18 Floating Warm Dust Motes in Day/Sunset, 9 Corner Fireflies in Night) ---
        if (sunbeamsEnabled && overlayAlpha > 0.01f) {
            val airModifier = if (isPortrait) {
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .offset(y = (-14).dp)
                    .aspectRatio(portraitAspect)
            } else {
                Modifier.fillMaxSize()
            }
            ProceduralStudioAirCanvas(
                lightingMode = resolvedLighting,
                isPlaying = isPlaying,
                overlayAlpha = overlayAlpha,
                modifier = airModifier
            )
        }
    }
}

private data class AirMoteSeed(
    val x0: Float,
    val y0: Float,
    val r: Float,
    val vx: Float,
    val vy: Float,
    val phase: Float
)

private data class FireflySeed(
    val bx: Float,
    val by: Float,
    val rx: Float,
    val ry: Float,
    val phase: Float
)

@Composable
private fun ProceduralStudioAirCanvas(
    lightingMode: StudioLightingMode,
    isPlaying: Boolean,
    overlayAlpha: Float,
    modifier: Modifier = Modifier
) {
    val motes = remember {
        List(18) { i ->
            val f = (i * 0.6180339f) % 1f
            val g = ((i * 17 + 5) % 23) / 23f
            AirMoteSeed(
                x0 = f,
                y0 = g * 0.66f,
                r = 1.6f + (i % 4) * 0.75f,
                vx = 0.008f + (i % 5) * 0.0025f,
                vy = -0.004f - (i % 3) * 0.0025f,
                phase = i * 0.93f
            )
        }
    }
    val fireflies = remember {
        listOf(
            FireflySeed(0.135f, 0.26f, 0.020f, 0.022f, 0.0f),
            FireflySeed(0.220f, 0.46f, 0.024f, 0.026f, 1.1f),
            FireflySeed(0.095f, 0.57f, 0.018f, 0.020f, 2.2f),
            FireflySeed(0.270f, 0.19f, 0.026f, 0.024f, 3.3f),
            FireflySeed(0.855f, 0.22f, 0.022f, 0.025f, 4.4f),
            FireflySeed(0.915f, 0.42f, 0.020f, 0.022f, 5.5f),
            FireflySeed(0.800f, 0.35f, 0.025f, 0.020f, 6.6f),
            FireflySeed(0.885f, 0.62f, 0.019f, 0.026f, 7.7f),
            FireflySeed(0.760f, 0.19f, 0.024f, 0.022f, 8.8f)
        )
    }

    var timeSec by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        var lastNs = withFrameNanos { it }
        while (true) {
            val nowNs = withFrameNanos { it }
            val dt = ((nowNs - lastNs) / 1_000_000_000f).coerceIn(0f, 0.05f)
            lastNs = nowNs
            timeSec += dt
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 1f || h <= 1f) return@Canvas

        if (lightingMode == StudioLightingMode.NIGHT) {
            for (f in fireflies) {
                val cx = (f.bx + sin(timeSec * 0.8f + f.phase) * f.rx) * w
                val cy = (f.by + cos(timeSec * 0.6f + f.phase) * f.ry) * h
                val glow = (0.25f + 0.55f * (0.5f + 0.5f * sin(timeSec * 1.8f + f.phase))) * overlayAlpha
                val radius = 14.dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFE88C).copy(alpha = glow),
                            Color(0xFFFFC346).copy(alpha = glow * 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(cx, cy)
                )
            }
        } else {
            val baseColor = if (lightingMode == StudioLightingMode.SUNSET) {
                Color(0xFFFFE2A8)
            } else {
                Color(0xFFFFFAE0)
            }
            for (m in motes) {
                val nx = ((m.x0 + timeSec * m.vx) % 1f + 1f) % 1f
                val ny = ((m.y0 + timeSec * m.vy) % 0.68f + 0.68f) % 0.68f
                val alpha = (0.18f + 0.24f * (0.5f + 0.5f * sin(timeSec * 1.4f + m.phase))) * overlayAlpha
                drawCircle(
                    color = baseColor.copy(alpha = alpha),
                    radius = m.r.dp.toPx(),
                    center = Offset(nx * w, ny * h)
                )
            }
        }
    }
}
