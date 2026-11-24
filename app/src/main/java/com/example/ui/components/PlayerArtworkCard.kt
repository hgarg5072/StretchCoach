package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AmbientSoundscape
import com.example.data.ArtworkMapper
import com.example.data.StretchEntity
import com.example.ui.PlayerState
import com.example.ui.theme.CoralHold
import com.example.ui.theme.CyanPrep
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TealPrimaryLight
import java.io.File

@Composable
fun PlayerArtworkCard(
    currentStretch: StretchEntity?,
    state: PlayerState,
    currentIndex: Int,
    totalCount: Int,
    nextStretch: StretchEntity? = null,
    remainingSeconds: Int = 0,
    totalSegmentSeconds: Int = 0,
    progress: Float = 0f,
    fillAvailableHeight: Boolean = false,
    ambientSoundscape: AmbientSoundscape? = null,
    customTrackTitle: String = "",
    onCycleAmbientSoundscape: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    onToggleOrientation: (() -> Unit)? = null,
    showLandscapeHint: Boolean = false,
    onDismissLandscapeHint: (() -> Unit)? = null,
    showChrome: Boolean = true,
    onToggleChrome: (() -> Unit)? = null,
    activeTransitionVideoPath: String? = null,
    activeTransitionCue: String? = null,
    onTransitionFinished: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customFile = remember(currentStretch?.customImagePath) {
        currentStretch?.customImagePath?.let { File(it) }?.takeIf { it.exists() }
    }
    val artworkRes: Int? = currentStretch?.let {
        ArtworkMapper.getDrawableForStretch(it.name, it.category)
    }
    val videoAssetPath: String? = remember(currentStretch?.name, currentStretch?.category, customFile) {
        if (customFile != null) null
        else currentStretch?.let { ArtworkMapper.getVideoAssetPathForStretch(context, it.name, it.category) }
    }
    val isPlayingTransition = state == PlayerState.PREPARING && !activeTransitionVideoPath.isNullOrBlank()
    val effectiveVideoPath = if (isPlayingTransition) activeTransitionVideoPath else videoAssetPath
    val mirrorLeftSide = remember(currentStretch?.name, currentStretch?.category, isPlayingTransition) {
        if (isPlayingTransition || currentStretch == null) false
        else com.example.data.PoseManifestRepository.shouldMirrorForLeftSide(context, currentStretch.name, currentStretch.category)
    }
    val hasImage = customFile != null || artworkRes != null || isPlayingTransition

    // Subtle breathing pulse animation while running (PREPARING or HOLDING)
    val infiniteTransition = rememberInfiniteTransition(label = "player_artwork_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (hasImage && (state == PlayerState.PREPARING || state == PlayerState.HOLDING)) 1.03f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_anim"
    )

    // Continuous 60fps linear progress animation (zero 1-second stutter/jerkiness)
    val smoothProgress = remember { Animatable(progress.coerceIn(0f, 1f)) }
    LaunchedEffect(state, currentIndex, totalSegmentSeconds) {
        when (state) {
            PlayerState.PREPARING, PlayerState.HOLDING -> {
                if (totalSegmentSeconds > 0 && remainingSeconds > 0) {
                    val startFrac = ((totalSegmentSeconds - remainingSeconds).toFloat() / totalSegmentSeconds)
                        .coerceIn(0f, 1f)
                    // If resuming from pause in the same segment, keep current sub-second progress
                    if (kotlin.math.abs(smoothProgress.value - startFrac) > (1.5f / totalSegmentSeconds.coerceAtLeast(1))) {
                        smoothProgress.snapTo(startFrac)
                    }
                    smoothProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = (remainingSeconds * 1000).coerceAtLeast(1),
                            easing = LinearEasing
                        )
                    )
                } else {
                    smoothProgress.snapTo(0f)
                }
            }
            PlayerState.PAUSED -> {
                smoothProgress.stop()
            }
            PlayerState.COMPLETE -> {
                smoothProgress.snapTo(1f)
            }
            PlayerState.IDLE -> {
                smoothProgress.snapTo(0f)
            }
        }
    }
    val animatedProgress = smoothProgress.value

    val stepText = if (totalCount > 0 && state != PlayerState.IDLE && state != PlayerState.COMPLETE) {
        "STEP ${currentIndex + 1} OF $totalCount"
    } else if (state == PlayerState.COMPLETE) {
        "ALL COMPLETE"
    } else {
        "READY TO START"
    }

    val (phaseText, badgeColor) = when (state) {
        PlayerState.IDLE -> "READY" to MaterialTheme.colorScheme.primary
        PlayerState.PREPARING -> "PREPARING" to CyanPrep
        PlayerState.HOLDING -> "HOLDING" to CoralHold
        PlayerState.PAUSED -> "PAUSED" to Color(0xFFF59E0B)
        PlayerState.COMPLETE -> "COMPLETE" to EmeraldSuccess
    }
    val phaseTextColor = if (state == PlayerState.IDLE) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        Color.White
    }
    val isActivePhase =
        state == PlayerState.PREPARING || state == PlayerState.HOLDING || state == PlayerState.PAUSED

    val formattedTime = remember(remainingSeconds, state, currentStretch) {
        when (state) {
            PlayerState.IDLE -> {
                val previewSec = currentStretch?.holdSeconds ?: 0
                if (previewSec > 0) formatClock(previewSec) else "0:00"
            }
            PlayerState.COMPLETE -> "Done"
            else -> formatClock(remainingSeconds)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("player_artwork_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (hasImage) {
            val containerModifier = if (fillAvailableHeight) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
            }

            val isPortraitTall = fillAvailableHeight &&
                LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE

            Box(
                modifier = containerModifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE7DEC9), // Warm sunlit Ghibli studio wall
                                Color(0xFFEBE2D1),
                                Color(0xFFDCC8AB), // Warm wooden studio floor
                                Color(0xFFC9AE8B)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        RoundedCornerShape(24.dp)
                    )
                    .then(
                        if (onToggleChrome != null) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onToggleChrome
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                val mediaStageModifier = if (isPortraitTall) {
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .offset(y = (-14).dp)
                        .aspectRatio(1.18f)
                } else if (fillAvailableHeight) {
                    // Landscape full-screen: lock to 16:9 matching height so standing poses never chop the head on ultra-wide screens
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .aspectRatio(16f / 9f, matchHeightConstraintsFirst = true)
                } else {
                    Modifier.fillMaxSize()
                }

                Box(modifier = mediaStageModifier) {
                    if (customFile != null) {
                        AsyncImage(
                            model = customFile,
                            contentDescription = currentStretch?.name ?: "Stretch demonstration",
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(scale),
                            contentScale = ContentScale.Crop
                        )
                    } else if (artworkRes != null || effectiveVideoPath != null) {
                        if (artworkRes != null) {
                            Image(
                                painter = painterResource(id = artworkRes),
                                contentDescription = currentStretch?.name ?: "Stretch demonstration",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(
                                        scaleX = (if (effectiveVideoPath == null) scale else 1f) * (if (mirrorLeftSide) -1f else 1f),
                                        scaleY = if (effectiveVideoPath == null) scale else 1f
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (effectiveVideoPath != null) {
                            LoopingStretchVideo(
                                assetPath = effectiveVideoPath,
                                isPlaying = state != PlayerState.PAUSED,
                                isLooping = !isPlayingTransition,
                                mirrorHorizontal = mirrorLeftSide,
                                onCompleted = if (isPlayingTransition) onTransitionFinished else null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Soft feather top/bottom edges in Portrait so the 16:9 stage melts into the studio wall & wood floor
                    if (isPortraitTall) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFEBE2D1), Color.Transparent)
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xFFDCC8AB))
                                    )
                                )
                        )
                    }
                }

                // Top overlay scrim: Step badge + Phase badge + Next Up + Music pill (hides cleanly in Zen mode)
                androidx.compose.animation.AnimatedVisibility(
                    visible = showChrome,
                    enter = fadeIn(tween(220)),
                    exit = fadeOut(tween(220)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.62f),
                                    Color.Black.copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onNavigateBack != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.50f))
                                        .clickable(onClick = onNavigateBack)
                                        .testTag("player_back_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Routines",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.92f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.9.sp
                                    ),
                                    color = TealPrimaryLight
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(badgeColor)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = phaseText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.9.sp
                                    ),
                                    color = phaseTextColor
                                )
                            }

                            if (onToggleOrientation != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.50f))
                                        .clickable(onClick = {
                                            onDismissLandscapeHint?.invoke()
                                            onToggleOrientation()
                                        })
                                        .testTag("player_rotate_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ScreenRotation,
                                        contentDescription = "Switch Portrait / Landscape",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (onOpenSettings != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.50f))
                                        .clickable(onClick = onOpenSettings)
                                        .testTag("player_settings_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Second Top Row: Next Up pill (left) + Music switcher overlay pill (right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (nextStretch != null && state != PlayerState.COMPLETE) {
                            Row(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.52f))
                                    .border(
                                        width = 0.75.dp,
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .testTag("player_next_up_overlay"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next up",
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Next: ${nextStretch.name}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Color.White.copy(alpha = 0.95f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        if (ambientSoundscape != null && onCycleAmbientSoundscape != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            val musicLabel = when {
                                ambientSoundscape == AmbientSoundscape.OFF -> "Music: Off"
                                ambientSoundscape == AmbientSoundscape.CUSTOM_TRACK && customTrackTitle.isNotBlank() ->
                                    customTrackTitle.take(14)
                                else -> ambientSoundscape.displayName
                            }
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.56f))
                                    .border(
                                        width = 0.75.dp,
                                        color = Color.White.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable(onClick = onCycleAmbientSoundscape)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .testTag("player_ambient_pill"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (ambientSoundscape == AmbientSoundscape.OFF) {
                                        Icons.Default.MusicOff
                                    } else {
                                        Icons.Default.MusicNote
                                    },
                                    contentDescription = "Switch Music",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = musicLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // One-time Landscape Viewing Tip (auto-dismisses and never repeats)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showLandscapeHint,
                        enter = fadeIn(tween(240)),
                        exit = fadeOut(tween(240))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.70f))
                                .border(
                                    width = 0.75.dp,
                                    color = Color.White.copy(alpha = 0.28f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("landscape_hint_banner"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ScreenRotation,
                                contentDescription = null,
                                tint = Color(0xFF5EEAD4),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Use landscape mode for better viewing",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (onToggleOrientation != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF0D9488))
                                        .clickable {
                                            onDismissLandscapeHint?.invoke()
                                            onToggleOrientation()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                        .testTag("landscape_hint_switch_button")
                                ) {
                                    Text(
                                        text = "Switch",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                            if (onDismissLandscapeHint != null) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss tip",
                                    tint = Color.White.copy(alpha = 0.78f),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable(onClick = onDismissLandscapeHint)
                                )
                            }
                        }
                    }
                }
                }

                // Bottom overlay: Stretch name + cues (hidden in Zen mode) + Countdown Clock (always visible)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            if (showChrome) {
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.45f),
                                        Color.Black.copy(alpha = 0.82f)
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.28f)
                                    )
                                )
                            }
                        )
                        .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = showChrome,
                                enter = fadeIn(tween(220)),
                                exit = fadeOut(tween(220))
                            ) {
                                Column {
                                    if (currentStretch != null) {
                                        Text(
                                            text = currentStretch.name,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.3).sp
                                            ),
                                            color = Color.White,
                                            maxLines = 1,
                                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                                        )
                                        val cueText = when {
                                            isPlayingTransition && !activeTransitionCue.isNullOrBlank() ->
                                                activeTransitionCue
                                            state == PlayerState.PREPARING && currentStretch.effectivePrepCue.isNotBlank() ->
                                                currentStretch.effectivePrepCue
                                            state == PlayerState.HOLDING && currentStretch.effectiveHoldCue.isNotBlank() ->
                                                currentStretch.effectiveHoldCue
                                            !currentStretch.notes.isNullOrBlank() -> currentStretch.notes
                                            else -> ""
                                        }
                                        if (cueText.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = cueText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.88f),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Ready to stretch",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        if (fillAvailableHeight || isActivePhase || state == PlayerState.COMPLETE) {
                            Spacer(modifier = Modifier.width(12.dp))
                            OverlayClockRing(
                                timeText = formattedTime,
                                progress = if (isActivePhase) animatedProgress else if (state == PlayerState.COMPLETE) 1f else 0f,
                                ringColor = badgeColor,
                                onDarkScrim = true
                            )
                        }
                    }

                    if (isActivePhase && totalSegmentSeconds > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(999.dp)),
                            color = badgeColor,
                            trackColor = Color.White.copy(alpha = 0.25f),
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                }
            }
        } else {
            // Surface-themed card when no custom or verified artwork exists (never renders a fallback image)
            val noImgModifier = if (fillAvailableHeight) {
                Modifier.fillMaxSize()
            } else {
                Modifier.fillMaxWidth()
            }
            Column(
                modifier = noImgModifier
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onNavigateBack != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable(onClick = onNavigateBack)
                                        .testTag("player_back_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back to Routines",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(badgeColor)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = phaseText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = phaseTextColor
                                )
                            }
                            if (onToggleOrientation != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.50f))
                                        .clickable(onClick = {
                                            onDismissLandscapeHint?.invoke()
                                            onToggleOrientation()
                                        })
                                        .testTag("player_rotate_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ScreenRotation,
                                        contentDescription = "Switch Portrait / Landscape",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (onOpenSettings != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable(onClick = onOpenSettings)
                                        .testTag("player_settings_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (nextStretch != null && state != PlayerState.COMPLETE) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("player_next_up_overlay"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next up",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Next: ${nextStretch.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (fillAvailableHeight) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OverlayClockRing(
                            timeText = formattedTime,
                            progress = if (isActivePhase) animatedProgress else if (state == PlayerState.COMPLETE) 1f else 0f,
                            ringColor = badgeColor,
                            onDarkScrim = false
                        )
                    }
                }

                if (isActivePhase && currentStretch != null) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentStretch.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                        )
                        if (!currentStretch.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentStretch.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlayClockRing(
    timeText: String,
    progress: Float,
    ringColor: Color,
    onDarkScrim: Boolean
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
                if (onDarkScrim) Color.Black.copy(alpha = 0.56f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            )
            .testTag("player_timer_overlay"),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(68.dp),
            color = ringColor,
            trackColor = if (onDarkScrim) Color.White.copy(alpha = 0.22f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp
        )
        Text(
            text = timeText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = if (onDarkScrim) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatClock(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return if (mins > 0) String.format("%d:%02d", mins, secs) else "${secs}s"
}
