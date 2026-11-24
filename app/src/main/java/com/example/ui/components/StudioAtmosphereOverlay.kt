package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.StudioAromaProp
import com.example.data.StudioLightingMode
import com.example.ui.PlayerState

/**
 * Persistent Layers L1 (Companion Dog, Background Diffuser/Incense, Corner Leaves),
 * L2 (Unified Day/Sunset/Night Lighting Wash & Warm Lamp Bloom), and
 * L3 (Sunbeams & Dust Motes Screen Blend).
 *
 * Mounted OUTSIDE the pose-switching AnimatedContent container so overlays never
 * reset or blink when transitioning between poses.
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
    modifier: Modifier = Modifier
) {
    val resolvedLighting = lightingMode.resolveForCurrentHour()
    val isPlaying = playerState != PlayerState.PAUSED

    // Smart Dog State Machine
    val dogAssetPath = when {
        playerState == PlayerState.COMPLETE -> "stretch_overlays/overlay_dog_celebrate_chromakey.mp4"
        isTransitioning -> "stretch_overlays/overlay_dog_playful_stretch_chromakey.mp4"
        isRestorativePose || resolvedLighting == StudioLightingMode.NIGHT ->
            "stretch_overlays/overlay_dog_nap_curled_chromakey.mp4"
        else -> "stretch_overlays/overlay_dog_idle_rest_chromakey.mp4"
    }

    val aromaAssetPath = when (aromaProp) {
        StudioAromaProp.DIFFUSER -> "stretch_overlays/overlay_aroma_diffuser_mist_chromakey.mp4"
        StudioAromaProp.INCENSE -> "stretch_overlays/overlay_incense_holder_smoke_chromakey.mp4"
        StudioAromaProp.NONE -> null
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val stageWidth = maxWidth

        // --- LAYER 1: Persistent Studio Overlays ---

        // 1A. Swaying Corner Leaves (top-left, blue screen key — tucked gently into corner so Back HUD stays crisp)
        if (leavesEnabled) {
            ChromaKeyVideoOverlay(
                assetPath = "stretch_overlays/overlay_leaves_sway_corner_chromakey.mp4",
                keyMode = OverlayKeyMode.BLUE_KEY,
                lightingMode = resolvedLighting,
                opacity = 0.78f,
                isPlaying = isPlaying,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-10).dp, y = (-6).dp)
                    .width((stageWidth * 0.22f).coerceIn(68.dp, 138.dp))
                    .aspectRatio(16f / 9f)
            )
        }

        // 1B. Aroma Diffuser / Incense tucked into the BACK-RIGHT studio wall/corner
        // (smaller scale & elevated above the mat/clock so it sits naturally in the background)
        if (aromaAssetPath != null) {
            ChromaKeyVideoOverlay(
                assetPath = aromaAssetPath,
                keyMode = OverlayKeyMode.GREEN_KEY,
                lightingMode = resolvedLighting,
                opacity = 0.86f,
                isPlaying = isPlaying,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-6).dp, y = 26.dp)
                    .width((stageWidth * 0.13f).coerceIn(48.dp, 92.dp))
                    .aspectRatio(1f)
            )
        }

        // 1C. Companion Dog (bottom-left above the bottom HUD bar)
        if (dogEnabled) {
            ChromaKeyVideoOverlay(
                assetPath = dogAssetPath,
                keyMode = OverlayKeyMode.GREEN_KEY,
                lightingMode = resolvedLighting,
                opacity = 0.96f,
                isPlaying = isPlaying,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 92.dp)
                    .width((stageWidth * 0.21f).coerceIn(68.dp, 132.dp))
                    .aspectRatio(16f / 9f)
            )
        }

        // --- LAYER 2: Unified Day / Sunset / Night Lighting Shader Wash ---
        if (resolvedLighting != StudioLightingMode.DAY) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                when (resolvedLighting) {
                    StudioLightingMode.SUNSET -> {
                        // Warm golden-hour amber wash + soft window glow
                        drawRect(color = Color(0x28FF8C32))
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x38FFAF50),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.15f, size.height * 0.25f),
                                radius = size.maxDimension * 0.8f
                            )
                        )
                    }
                    StudioLightingMode.NIGHT -> {
                        // Cool twilight indigo wash + warm overhead studio lamp bloom
                        drawRect(color = Color(0x5A0F1D3A))
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x45FFBE64),
                                    Color(0x181E293B),
                                    Color(0x400A1020)
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

        // --- LAYER 3: Sunbeams & Dust Motes (Screen/Additive Blend on Black) ---
        if (sunbeamsEnabled) {
            ChromaKeyVideoOverlay(
                assetPath = "stretch_overlays/overlay_sunbeams_motes_screen_blend.mp4",
                keyMode = OverlayKeyMode.SCREEN_BLACK,
                lightingMode = resolvedLighting,
                opacity = if (resolvedLighting == StudioLightingMode.NIGHT) 0.24f else 0.42f,
                isPlaying = isPlaying,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
