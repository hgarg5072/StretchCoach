package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.data.ArtworkMapper
import com.example.data.StretchEntity
import com.example.ui.theme.CoralHold
import com.example.ui.theme.CyanPrep
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StretchItemCard(
    stretch: StretchEntity,
    index: Int,
    totalCount: Int,
    onToggleEnabled: (Boolean) -> Unit,
    onToggleVoiceCue: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPlaySingle: () -> Unit,
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    dragOffsetY: Float = 0f,
    onDragStart: () -> Unit = {},
    onDrag: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) {
    val artworkRes: Int? = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)
    val customFile = remember(stretch.customImagePath) {
        stretch.customImagePath?.let { File(it) }?.takeIf { it.exists() }
    }
    val cardAlpha = if (stretch.enabled) 1.0f else 0.55f
    var showLongPressMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .zIndex(if (isDragging) 10f else 1f)
                .graphicsLayer {
                    if (isDragging) {
                        translationY = dragOffsetY
                        scaleX = 1.03f
                        scaleY = 1.03f
                        shadowElevation = 16.dp.toPx()
                    }
                }
                .alpha(cardAlpha)
                .animateContentSize()
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = onEdit,
                    onLongClick = { showLongPressMenu = true }
                )
                .testTag("stretch_card_${stretch.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isDragging) 12.dp else if (stretch.enabled) 1.dp else 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isDragging) 2.dp else 1.dp,
                        color = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail (only rendered when custom image or verified studio artwork exists)
                if (customFile != null || artworkRes != null) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    ) {
                        if (customFile != null) {
                            AsyncImage(
                                model = customFile,
                                contentDescription = stretch.name,
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else if (artworkRes != null) {
                            Image(
                                painter = painterResource(id = artworkRes),
                                contentDescription = stretch.name,
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Title, optional Indian/Sanskrit subtitle, and compact timer pills
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stretch.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                    )

                    if (!stretch.indianName.isNullOrBlank()) {
                        Text(
                            text = stretch.indianName,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(CyanPrep.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Prep\u00A0${stretch.preparationSeconds}s",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrep,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(CoralHold.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Hold\u00A0${stretch.holdSeconds}s",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CoralHold,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        if (!stretch.voiceCueEnabled) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "Voice muted",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Checkbox toggle enabled
                Checkbox(
                    checked = stretch.enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("toggle_enabled_${stretch.id}")
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Drag handle for reordering
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        )
                        .pointerInput(stretch.id) {
                            detectVerticalDragGestures(
                                onDragStart = { onDragStart() },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount)
                                },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragCancel() }
                            )
                        }
                        .testTag("drag_handle_${stretch.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Drag to reorder",
                        tint = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Long-Hold Quick Actions Menu (keeps cards uncluttered!)
        DropdownMenu(
            expanded = showLongPressMenu,
            onDismissRequest = { showLongPressMenu = false }
        ) {
            if (stretch.enabled) {
                DropdownMenuItem(
                    text = { Text("Play from here") },
                    leadingIcon = { Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = {
                        showLongPressMenu = false
                        onPlaySingle()
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Edit timer & details") },
                leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                onClick = {
                    showLongPressMenu = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text(if (stretch.voiceCueEnabled) "Mute voice cue" else "Enable voice cue") },
                leadingIcon = {
                    Icon(
                        if (stretch.voiceCueEnabled) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        null
                    )
                },
                onClick = {
                    showLongPressMenu = false
                    onToggleVoiceCue(!stretch.voiceCueEnabled)
                }
            )
            DropdownMenuItem(
                text = { Text("Remove stretch", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                    showLongPressMenu = false
                    onDelete()
                }
            )
        }
    }
}
