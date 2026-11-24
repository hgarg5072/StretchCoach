package com.example.ui.components

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.PoseOverlayAnchors
import com.example.data.StudioAromaProp
import com.example.data.StudioLightingMode
import com.example.ui.PlayerState
import kotlin.math.cos
import kotlin.math.sin

/**
 * Persistent Studio Ghibli Multi-Layer Atmosphere Overlay (IMPLEMENTER_SPEC.md v2):
 *
 * - Layer 1A: Window Wind Chime (Day/Sunset) + subtle Corner Leaves
 * - Layer 1B: Studio Floor Prop (Auto-syncs Tea Set in Day, Clay Candle Warmer in Sunset,
 *             Japanese Washi Lantern in Night — or manual Tea/Candle/Lantern/Diffuser/Incense)
 *             positioned via per-pose `overlay_anchors` with 450ms smooth interpolation
 * - Layer 1C: 8-Action Golden Retriever Puppy Companion (idle_rest, playful_stretch, nap_curled,
 *             celebrate, head_tilt_curious, yawn_sleepy, belly_roll, paw_wave)
 * - Layer 2: Unified Day / Sunset / Night Studio Lighting Wash
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
    dogEnabled: Boolean,
    leavesEnabled: Boolean,
    sunbeamsEnabled: Boolean,
    poseAnchors: PoseOverlayAnchors = PoseOverlayAnchors(),
    stretchIndex: Int = 0,
    overlayAlpha: Float = 1f,
    modifier: Modifier = Modifier
) {
    val resolvedLighting = lightingMode.resolveForCurrentHour()
    val isPlaying = playerState != PlayerState.PAUSED

    // Persistent Companion Dog state machine (avoids re-preparing MediaPlayer on every stretch so overlays appear instantaneously)
    val dogAssetPath = remember(playerState, isTransitioning, isRestorativePose, resolvedLighting) {
        when {
            playerState == PlayerState.COMPLETE ->
                "stretch_overlays/overlay_dog_celebrate_chromakey.mp4"
            isRestorativePose || resolvedLighting == StudioLightingMode.NIGHT ->
                "stretch_overlays/overlay_dog_nap_curled_chromakey.mp4"
            isTransitioning ->
                "stretch_overlays/overlay_dog_playful_stretch_chromakey.mp4"
            else ->
                "stretch_overlays/overlay_dog_idle_rest_chromakey.mp4"
        }
    }

    // Studio Floor Prop (Defaults to glowing Clay Candle Aroma Warmer; Auto syncs Candle / Washi Lantern)
    val resolvedPropSpec: Pair<String, OverlayKeyMode>? = remember(aromaProp, resolvedLighting) {
        val effectiveProp = when (aromaProp) {
            StudioAromaProp.AUTO_GHIBLI -> when (resolvedLighting) {
                StudioLightingMode.NIGHT -> StudioAromaProp.WASHI_LANTERN
                else -> StudioAromaProp.CANDLE_WARMER
            }
            else -> aromaProp
        }
        when (effectiveProp) {
            StudioAromaProp.TEA_SET ->
                "stretch_overlays/overlay_ghibli_tea_set_chromakey.mp4" to OverlayKeyMode.BLUE_KEY
            StudioAromaProp.CANDLE_WARMER ->
                "stretch_overlays/overlay_ghibli_candle_warmer_chromakey.mp4" to OverlayKeyMode.BLUE_KEY
            StudioAromaProp.WASHI_LANTERN ->
                "stretch_overlays/overlay_ghibli_washi_lantern_chromakey.mp4" to OverlayKeyMode.BLUE_KEY
            StudioAromaProp.DIFFUSER ->
                "stretch_overlays/overlay_aroma_diffuser_mist_chromakey.mp4" to OverlayKeyMode.GREEN_KEY
            StudioAromaProp.INCENSE ->
                "stretch_overlays/overlay_incense_holder_smoke_chromakey.mp4" to OverlayKeyMode.GREEN_KEY
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
        val portraitStageHeight = stageWidth / 1.18f

        // Anchor overlays strictly to the centered 16:9 videoRect in Landscape so tapping to hide/show
        // the right-hand controls rail NEVER shifts the dog, aroma prop, or leaves!
        val videoWidth = if (isPortrait) stageWidth else (stageHeight * (16f / 9f)).coerceAtMost(stageWidth)
        val videoLeft = ((stageWidth - videoWidth) / 2f).coerceAtLeast(0.dp)

        // --- LAYER 1A: Swaying Corner Leaves (top-left window frame; wind chime omitted per user preference) ---
        if (leavesEnabled) {
            val leavesModifier = if (isPortrait) {
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-8).dp, y = -(portraitStageHeight * 0.42f) - 14.dp)
                    .width((stageWidth * 0.30f).coerceIn(96.dp, 136.dp))
                    .aspectRatio(16f / 9f)
            } else {
                Modifier
                    .align(Alignment.TopStart)
                    .offset(x = videoLeft - 8.dp, y = (-6).dp)
                    .width((videoWidth * 0.21f).coerceIn(66.dp, 132.dp))
                    .aspectRatio(16f / 9f)
            }
            ChromaKeyVideoOverlay(
                assetPath = "stretch_overlays/overlay_leaves_sway_corner_chromakey.mp4",
                keyMode = OverlayKeyMode.BLUE_KEY,
                lightingMode = resolvedLighting,
                opacity = 0.72f * overlayAlpha,
                isPlaying = isPlaying,
                modifier = leavesModifier
            )
        }

        // --- LAYER 1B: Studio Floor Prop (Candle Warmer / Diffuser / Tea / Lantern / Incense) ---
        // Anchored strictly to `videoRect` and pulled closer inward onto the wooden studio floor
        if (resolvedPropSpec != null && poseAnchors.floorProp.visible) {
            val propAnchor = poseAnchors.floorProp
            val propOnLeftWall = propAnchor.leftPct != null && propAnchor.rightPct == null

            val targetPropWidth: Dp = if (isPortrait) {
                (stageWidth * 0.22f).coerceIn(74.dp, 96.dp)
            } else {
                (videoWidth * 0.16f).coerceIn(72.dp, 108.dp)
            }
            val animPropWidth by animateDpAsState(targetPropWidth, tween(320), label = "prop_w")

            val targetPropX: Dp = if (propOnLeftWall) {
                val leftFrac = (propAnchor.leftPct ?: 20f).coerceAtLeast(20f) / 100f
                videoLeft + (videoWidth * leftFrac)
            } else {
                val rightMargin = if (isPortrait) {
                    (stageWidth * 0.10f).coerceIn(34.dp, 50.dp)
                } else {
                    (videoWidth * 0.105f).coerceIn(58.dp, 92.dp)
                }
                (videoLeft + videoWidth - animPropWidth - rightMargin).coerceAtLeast(videoLeft + 8.dp)
            }
            val animPropX by animateDpAsState(targetPropX, tween(320), label = "prop_x")

            val targetPropY: Dp = if (isPortrait) {
                (portraitStageHeight * 0.29f) - 14.dp
            } else {
                // Grounded on the wooden studio floor just above the circular timer ring
                if (propOnLeftWall) (stageHeight * 0.24f) else 48.dp
            }
            val animPropY by animateDpAsState(targetPropY, tween(320), label = "prop_y")

            ChromaKeyVideoOverlay(
                assetPath = resolvedPropSpec.first,
                keyMode = resolvedPropSpec.second,
                lightingMode = resolvedLighting,
                opacity = 0.92f * overlayAlpha,
                isPlaying = isPlaying,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = animPropX, y = animPropY)
                    .width(animPropWidth)
                    .aspectRatio(1f)
            )
        }

        // --- LAYER 1C: Companion Dog grounded on wooden studio floor (larger & anchored to videoRect) ---
        if (dogEnabled && poseAnchors.dog.visible) {
            val targetDogWidth: Dp = if (isPortrait) {
                (stageWidth * 0.29f).coerceIn(100.dp, 128.dp)
            } else {
                (videoWidth * 0.21f).coerceIn(88.dp, 134.dp)
            }
            val animDogWidth by animateDpAsState(targetDogWidth, tween(320), label = "dog_w")

            val dogLeftDp = if (isPortrait) {
                (stageWidth * 0.07f).coerceIn(24.dp, 42.dp)
            } else {
                videoLeft + (videoWidth * 0.065f).coerceIn(34.dp, 58.dp)
            }
            val animDogX by animateDpAsState(dogLeftDp, tween(320), label = "dog_x")

            val dogModifier = if (isPortrait) {
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = animDogX, y = (portraitStageHeight * 0.42f) - 14.dp)
                    .width(animDogWidth)
                    .aspectRatio(1f)
            } else {
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = animDogX)
                    .padding(bottom = 44.dp)
                    .width(animDogWidth)
                    .aspectRatio(1f)
            }

            ChromaKeyVideoOverlay(
                assetPath = dogAssetPath,
                keyMode = OverlayKeyMode.BLUE_KEY,
                lightingMode = resolvedLighting,
                opacity = 0.96f * overlayAlpha,
                isPlaying = isPlaying,
                modifier = dogModifier
            )
        }

        // --- LAYER 2: Unified Day / Sunset / Night Lighting Shader Wash ---
        if (resolvedLighting != StudioLightingMode.DAY && overlayAlpha > 0.01f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                when (resolvedLighting) {
                    StudioLightingMode.SUNSET -> {
                        drawRect(color = Color(0x28FF8C32).copy(alpha = 0.16f * overlayAlpha))
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x38FFAF50).copy(alpha = 0.22f * overlayAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.15f, size.height * 0.25f),
                                radius = size.maxDimension * 0.8f
                            )
                        )
                    }
                    StudioLightingMode.NIGHT -> {
                        drawRect(color = Color(0x5A0F1D3A).copy(alpha = 0.35f * overlayAlpha))
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x45FFBE64).copy(alpha = 0.27f * overlayAlpha),
                                    Color(0x181E293B).copy(alpha = 0.09f * overlayAlpha),
                                    Color(0x400A1020).copy(alpha = 0.25f * overlayAlpha)
                                ),
                                center = Offset(size.width * 0.50f, size.height * 0.28f),
                                radius = size.maxDimension * 0.75f
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
                    .aspectRatio(1.18f)
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

/**
 * 0 MB 60fps procedural atmospheric air layer ported from IMPLEMENTER_SPEC.md Option B+C:
 * - Day / Sunset: 18 sparse warm sunlit dust motes drifting softly upward.
 * - Night: 9 soft pulsing warm golden fireflies orbiting gently in the studio corners.
 */
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
