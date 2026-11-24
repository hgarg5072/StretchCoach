package com.example.ui.screens

import android.app.Activity
import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.StretchEntity
import com.example.ui.PlayerState
import com.example.ui.PlayerUiState
import com.example.ui.components.PlayerArtworkCard
import com.example.ui.components.PlayerControlsRow

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
        val heroArtwork: @Composable (Modifier) -> Unit = { heroMod ->
            AnimatedContent(
                targetState = Pair(playerState.currentStretchIndex, currentStretch?.id),
                transitionSpec = {
                    val dir = if (targetState.first >= initialState.first && slideDirection >= 0) 1 else -1
                    (slideInHorizontally(animationSpec = tween(280)) { width -> dir * (width / 2) } +
                        fadeIn(animationSpec = tween(240))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(240)) { width -> -dir * (width / 2) } +
                            fadeOut(animationSpec = tween(200)))
                },
                label = "player_stretch_transition",
                modifier = heroMod
            ) { _ ->
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
                    showChrome = showChrome,
                    onToggleChrome = { showChrome = !showChrome },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = swipeOffsetX * 0.35f
                        }
                )
            }
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
