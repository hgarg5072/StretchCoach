package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AmbientSoundscape
import com.example.data.PoseManifestRepository
import com.example.data.StretchEntity
import com.example.data.StudioAromaProp
import com.example.data.StudioLightingMode
import com.example.ui.PlayerState
import com.example.ui.PlayerUiState
import com.example.ui.components.PlayerArtworkCard
import com.example.ui.components.PlayerControlsRow
import com.example.ui.components.StudioAtmosphereOverlay

@Composable
fun PlayerScreen(
    playerState: PlayerUiState,
    enabledStretches: List<StretchEntity>,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    ambientSoundscape: AmbientSoundscape = AmbientSoundscape.OFF,
    customTrackTitle: String = "",
    onCycleAmbientSoundscape: () -> Unit = {},
    studioLightingMode: StudioLightingMode = StudioLightingMode.AUTO,
    studioAromaProp: StudioAromaProp = StudioAromaProp.AUTO_GHIBLI,
    companionPet: com.example.data.CompanionPet = com.example.data.CompanionPet.COZY_MAT_PUPPY,
    swayingLeavesEnabled: Boolean = true,
    sunbeamsEnabled: Boolean = true,
    onTransitionClipFinished: () -> Unit = {},
    onFlowRoundCompleted: () -> Unit = {},
    onSetFlowTargetRounds: (Int) -> Unit = {},
    onSetFlowPlaybackSpeed: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isRunning = playerState.isRunning

    // Keep screen awake while a session is active or paused
    DisposableEffect(isRunning) {
        val window = (context as? Activity)?.window
        if (isRunning) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Restore normal auto-rotate when leaving the Player screen
    DisposableEffect(Unit) {
        onDispose {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val toggleOrientation: () -> Unit = {
        val act = context as? Activity
        if (act != null) {
            act.requestedOrientation = if (isLandscape) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
    }

    // One-time tip in Portrait encouraging landscape mode for better viewing (shown once only)
    var showLandscapeHint by remember { mutableStateOf(false) }
    LaunchedEffect(isLandscape) {
        if (isLandscape) {
            showLandscapeHint = false
        } else {
            val prefs = context.getSharedPreferences("stretch_coach_prefs", Context.MODE_PRIVATE)
            if (!prefs.getBoolean("landscape_view_hint_shown", false)) {
                prefs.edit().putBoolean("landscape_view_hint_shown", true).apply()
                showLandscapeHint = true
                delay(7000L)
                showLandscapeHint = false
            }
        }
    }

    val currentStretch = playerState.currentStretch ?: enabledStretches.firstOrNull()
    val nextStretch = playerState.nextStretch

    val effectiveTotalCount = if (playerState.state == PlayerState.IDLE) {
        enabledStretches.size
    } else {
        playerState.totalStretches.ifZero(enabledStretches.size)
    }

    // Track swipe/button direction so transitions visibly slide forward (+1) or backward (-1)
    var slideDirection by remember { mutableIntStateOf(1) }
    var swipeOffsetX by remember { mutableFloatStateOf(0f) }
    var showChrome by remember { mutableStateOf(true) }
    val swipeThresholdPx = with(LocalDensity.current) { 72.dp.toPx() }

    val handleSkipNext = {
        slideDirection = 1
        onSkipNext()
    }
    val handleSkipPrevious = {
        slideDirection = -1
        onSkipPrevious()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .pointerInput(isRunning) {
                if (!isRunning) return@pointerInput
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        swipeOffsetX = (swipeOffsetX + dragAmount).coerceIn(-260f, 260f)
                    },
                    onDragEnd = {
                        when {
                            swipeOffsetX < -swipeThresholdPx -> handleSkipNext()
                            swipeOffsetX > swipeThresholdPx -> handleSkipPrevious()
                        }
                        swipeOffsetX = 0f
                    },
                    onDragCancel = {
                        swipeOffsetX = 0f
                    }
                )
            }
            .testTag("player_screen")
    ) {
        val currentPoseMeta = remember(currentStretch?.name, currentStretch?.category) {
            currentStretch?.let {
                PoseManifestRepository.getPoseMetaForStretch(context, it.name, it.category)
            }
        }
        val isRestorativePose = currentPoseMeta?.isRestorativeOrPranayama == true
        val isWidePose = currentPoseMeta?.postureHub == com.example.data.PostureHub.SUPINE ||
            currentPoseMeta?.postureHub == com.example.data.PostureHub.PRONE
        val mirroredLeft = remember(currentStretch?.name, currentStretch?.category) {
            currentStretch?.let {
                PoseManifestRepository.shouldMirrorForLeftSide(context, it.name, it.category)
            } ?: false
        }
        val poseAnchors = currentPoseMeta?.anchorsFor(
            isPortrait = !isLandscape,
            mirroredLeft = mirroredLeft
        ) ?: com.example.data.PoseOverlayAnchors()
        val hasVideoForStretch = remember(currentStretch?.name, currentStretch?.category) {
            currentStretch?.let {
                com.example.data.ArtworkMapper.getVideoAssetPathForStretch(context, it.name, it.category) != null
            } ?: false
        }
        val isEmptySanctuary = currentStretch != null && !hasVideoForStretch && currentStretch.customImagePath.isNullOrBlank()
        val roamConfig = remember(currentPoseMeta, isEmptySanctuary, mirroredLeft, studioAromaProp, poseAnchors) {
            PoseManifestRepository.resolveRoamConfig(
                entry = currentPoseMeta,
                isEmptySanctuary = isEmptySanctuary,
                mirroredLeft = mirroredLeft,
                hasActiveFloorProp = studioAromaProp != StudioAromaProp.NONE && poseAnchors.floorProp.visible,
                propRightPct = poseAnchors.floorProp.rightPct ?: 2f,
                propWidthPct = poseAnchors.floorProp.widthPct
            )
        }

        // Hide studio overlays briefly during stretch slide animations or active horizontal swipe gestures
        val stretchSlideKey = Pair(playerState.currentStretchIndex, currentStretch?.id)
        var lastSlideKey by remember { mutableStateOf(stretchSlideKey) }
        var isSlidingStretch by remember { mutableStateOf(false) }
        LaunchedEffect(stretchSlideKey) {
            if (stretchSlideKey != lastSlideKey) {
                lastSlideKey = stretchSlideKey
                isSlidingStretch = true
                delay(200L)
                isSlidingStretch = false
            }
        }
        val hideOverlaysForSlide = isSlidingStretch || abs(swipeOffsetX) > 6f
        val overlayAlpha by animateFloatAsState(
            targetValue = if (hideOverlaysForSlide) 0f else 1f,
            animationSpec = tween(durationMillis = if (hideOverlaysForSlide) 50 else 110),
            label = "studio_overlay_slide_alpha"
        )

        val heroArtwork: @Composable (Modifier) -> Unit = { heroMod ->
            PlayerArtworkCard(
                currentStretch = currentStretch,
                state = playerState.state,
                currentIndex = playerState.currentStretchIndex,
                totalCount = effectiveTotalCount,
                nextStretch = nextStretch,
                remainingSeconds = playerState.remainingSeconds,
                totalSegmentSeconds = playerState.totalSegmentSeconds,
                progress = playerState.progress,
                fillAvailableHeight = true,
                ambientSoundscape = ambientSoundscape,
                customTrackTitle = customTrackTitle,
                onCycleAmbientSoundscape = onCycleAmbientSoundscape,
                onNavigateBack = onNavigateBack,
                onOpenSettings = onOpenSettings,
                onToggleOrientation = toggleOrientation,
                showLandscapeHint = showLandscapeHint && !isLandscape,
                onDismissLandscapeHint = { showLandscapeHint = false },
                showChrome = showChrome,
                onToggleChrome = { showChrome = !showChrome },
                activeTransitionVideoPath = playerState.activeTransitionVideoPath,
                activeTransitionCue = playerState.activeTransitionCue,
                onTransitionFinished = onTransitionClipFinished,
                slideDirection = slideDirection,
                swipeOffsetX = swipeOffsetX,
                isWidePose = isWidePose,
                isFlowMode = playerState.isFlowMode,
                flowCurrentRound = playerState.flowCurrentRound,
                flowTargetRounds = playerState.flowTargetRounds,
                flowPlaybackSpeed = playerState.flowPlaybackSpeed,
                onFlowRoundCompleted = onFlowRoundCompleted,
                onSetFlowTargetRounds = onSetFlowTargetRounds,
                onSetFlowPlaybackSpeed = onSetFlowPlaybackSpeed,
                atmosphereOverlay = {
                    StudioAtmosphereOverlay(
                        playerState = playerState.state,
                        isTransitioning = playerState.state == PlayerState.PREPARING &&
                            !playerState.activeTransitionVideoPath.isNullOrBlank(),
                        isRestorativePose = isRestorativePose,
                        lightingMode = studioLightingMode,
                        aromaProp = studioAromaProp,
                        companionPet = companionPet,
                        leavesEnabled = swayingLeavesEnabled,
                        sunbeamsEnabled = sunbeamsEnabled,
                        poseAnchors = poseAnchors,
                        roamConfig = roamConfig,
                        stretchIndex = playerState.currentStretchIndex,
                        totalStretches = effectiveTotalCount,
                        isWidePose = isWidePose,
                        overlayAlpha = overlayAlpha,
                        modifier = Modifier.fillMaxSize()
                    )
                },
                modifier = heroMod
            )
        }

        if (isLandscape) {
            // Landscape: Full-height widescreen 16:9 video on left + compact vertical control rail on right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                heroArtwork(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                AnimatedVisibility(
                    visible = showChrome,
                    enter = fadeIn(tween(220)) + expandHorizontally(tween(240)),
                    exit = fadeOut(tween(200)) + shrinkHorizontally(tween(220))
                ) {
                    PlayerControlsRow(
                        state = playerState.state,
                        hasStretches = enabledStretches.isNotEmpty(),
                        onPlay = onPlay,
                        onPause = onPause,
                        onResume = onResume,
                        onSkipNext = handleSkipNext,
                        onSkipPrevious = handleSkipPrevious,
                        onRestart = onRestart,
                        onStop = onStop,
                        onOpenSettings = onOpenSettings,
                        vertical = true
                    )
                }
            }
        } else {
            // Portrait: Maximized full-screen video stage + collapsible bottom controls
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                heroArtwork(Modifier.weight(1f))

                AnimatedVisibility(
                    visible = showChrome,
                    enter = fadeIn(tween(220)) + expandVertically(tween(240)),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(220))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(6.dp))
                        PlayerControlsRow(
                            state = playerState.state,
                            hasStretches = enabledStretches.isNotEmpty(),
                            onPlay = onPlay,
                            onPause = onPause,
                            onResume = onResume,
                            onSkipNext = handleSkipNext,
                            onSkipPrevious = handleSkipPrevious,
                            onRestart = onRestart,
                            onStop = onStop,
                            onOpenSettings = onOpenSettings,
                            vertical = false
                        )
                    }
                }
            }
        }
    }
}

private fun Int.ifZero(default: Int): Int = if (this == 0) default else this
