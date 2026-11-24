package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.PlayerState
import com.example.ui.theme.CoralHold

@Composable
fun PlayerControlsRow(
    state: PlayerState,
    hasStretches: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit,
    onOpenSettings: () -> Unit,
    vertical: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isActive = state == PlayerState.PREPARING || state == PlayerState.HOLDING || state == PlayerState.PAUSED
    val (mainIcon, mainClick, mainTag) = when (state) {
        PlayerState.IDLE -> Triple(Icons.Default.PlayArrow, onPlay, "player_main_play")
        PlayerState.PREPARING, PlayerState.HOLDING -> Triple(Icons.Default.Pause, onPause, "player_main_pause")
        PlayerState.PAUSED -> Triple(Icons.Default.PlayArrow, onResume, "player_main_resume")
        PlayerState.COMPLETE -> Triple(Icons.Default.Replay, onPlay, "player_main_replay")
    }

    val controlsContent: @Composable () -> Unit = {
        val secondarySize = if (vertical) 38.dp else 42.dp
        val skipSize = if (vertical) 42.dp else 46.dp
        val primarySize = if (vertical) 58.dp else 66.dp

        // Stop button
        IconButton(
            onClick = onStop,
            enabled = state != PlayerState.IDLE,
            modifier = Modifier
                .size(secondarySize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                .testTag("player_stop_button")
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                tint = if (state != PlayerState.IDLE) CoralHold else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(18.dp)
            )
        }

        // Previous button
        IconButton(
            onClick = onSkipPrevious,
            enabled = isActive,
            modifier = Modifier
                .size(skipSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                .testTag("player_prev_button")
        ) {
            Icon(
                imageVector = Icons.Default.FastRewind,
                contentDescription = "Previous Stretch",
                tint = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        // Primary Play / Pause / Resume / Replay Button
        FilledIconButton(
            onClick = mainClick,
            enabled = hasStretches,
            modifier = Modifier
                .size(primarySize)
                .then(
                    if (hasStretches) {
                        Modifier.shadow(elevation = 6.dp, shape = CircleShape, ambientColor = MaterialTheme.colorScheme.primary)
                    } else {
                        Modifier
                    }
                )
                .testTag(mainTag),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            )
        ) {
            Icon(
                imageVector = mainIcon,
                contentDescription = "Playback Control",
                modifier = Modifier.size(if (vertical) 30.dp else 34.dp)
            )
        }

        // Next button
        IconButton(
            onClick = onSkipNext,
            enabled = isActive,
            modifier = Modifier
                .size(skipSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                .testTag("player_next_button")
        ) {
            Icon(
                imageVector = Icons.Default.FastForward,
                contentDescription = "Next Stretch",
                tint = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        // Restart stretch button
        IconButton(
            onClick = onRestart,
            enabled = isActive,
            modifier = Modifier
                .size(secondarySize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                .testTag("player_restart_button")
        ) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = "Restart Current Stretch",
                tint = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (vertical) {
        Surface(
            modifier = modifier
                .fillMaxHeight()
                .width(78.dp)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                controlsContent()
            }
        }
    } else {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                controlsContent()
            }
        }
    }
}
