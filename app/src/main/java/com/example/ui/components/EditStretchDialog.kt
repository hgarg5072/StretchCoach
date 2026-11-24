package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StretchEntity
import com.example.ui.theme.CoralHold
import com.example.ui.theme.CyanPrep

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditStretchDialog(
    stretch: StretchEntity?,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, prepSeconds: Int, holdSeconds: Int, notes: String?, voiceCueEnabled: Boolean, indianName: String?) -> Unit
) {
    val isNew = stretch == null
    var name by remember { mutableStateOf(stretch?.name ?: "") }
    var indianName by remember { mutableStateOf(stretch?.indianName ?: "") }
    var prepSeconds by remember { mutableIntStateOf(stretch?.preparationSeconds ?: 8) }
    var prepInputText by remember { mutableStateOf((stretch?.preparationSeconds ?: 8).toString()) }

    var holdSeconds by remember { mutableIntStateOf(stretch?.holdSeconds ?: 30) }
    var holdInputText by remember { mutableStateOf((stretch?.holdSeconds ?: 30).toString()) }

    var notes by remember { mutableStateOf(stretch?.effectiveHoldCue ?: "") }
    var voiceCueEnabled by remember { mutableStateOf(stretch?.voiceCueEnabled ?: true) }
    var isNameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AvTimer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (isNew) "Add Custom Stretch" else "Edit Stretch & Timer",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) isNameError = false
                    },
                    label = { Text("Stretch / Asana Name") },
                    placeholder = { Text("e.g. Cobra Pose or Bhujangasana") },
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text("Name cannot be empty", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_stretch_name_input")
                )

                // Optional Indian / Sanskrit Name field
                OutlinedTextField(
                    value = indianName,
                    onValueChange = { indianName = it },
                    label = { Text("Indian / Sanskrit Name (Optional)") },
                    placeholder = { Text("e.g. भुजंगासन or Bhujangasana") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_stretch_indian_name_input")
                )

                // Preparation Time Section (Fully customizable)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrep.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "Preparation Time",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Get in position before hold",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Custom Direct Editable Time Input Field
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                OutlinedTextField(
                                    value = prepInputText,
                                    onValueChange = { input ->
                                        val filtered = input.filter { it.isDigit() }.take(3)
                                        prepInputText = filtered
                                        val parsed = filtered.toIntOrNull() ?: 0
                                        prepSeconds = parsed.coerceIn(0, 300)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = CyanPrep
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .width(72.dp)
                                        .height(48.dp)
                                        .testTag("prep_time_text_input")
                                )
                                Text(
                                    text = "sec",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CyanPrep
                                )
                            }
                        }

                        // Evenly distributed stepper row: -5s, -1s, +1s, +5s
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    prepSeconds = (prepSeconds - 5).coerceAtLeast(0)
                                    prepInputText = prepSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("prep_minus_5_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("-5s", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Surface(
                                onClick = {
                                    prepSeconds = (prepSeconds - 1).coerceAtLeast(0)
                                    prepInputText = prepSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("prep_minus_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease Prep 1s", modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                onClick = {
                                    prepSeconds = (prepSeconds + 1).coerceAtMost(300)
                                    prepInputText = prepSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("prep_plus_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase Prep 1s", modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                onClick = {
                                    prepSeconds = (prepSeconds + 5).coerceAtMost(300)
                                    prepInputText = prepSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("prep_plus_5_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+5s", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        // Presets (FlowRow wraps cleanly on all screen widths)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(0, 5, 8, 10, 15, 20).forEach { s ->
                                val isSelected = prepSeconds == s
                                Surface(
                                    onClick = {
                                        prepSeconds = s
                                        prepInputText = s.toString()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyanPrep else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) CyanPrep else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.testTag("prep_preset_${s}s")
                                ) {
                                    Text(
                                        text = "${s}s",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Hold Duration Section (Fully customizable to any seconds e.g. 37s)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralHold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "Hold Duration",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Active stretch hold time",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Custom Direct Editable Time Input Field
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                OutlinedTextField(
                                    value = holdInputText,
                                    onValueChange = { input ->
                                        val filtered = input.filter { it.isDigit() }.take(3)
                                        holdInputText = filtered
                                        val parsed = filtered.toIntOrNull() ?: 1
                                        holdSeconds = parsed.coerceIn(1, 600)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = CoralHold
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .width(72.dp)
                                        .height(48.dp)
                                        .testTag("hold_time_text_input")
                                )
                                Text(
                                    text = "sec",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CoralHold
                                )
                            }
                        }

                        // Evenly distributed stepper row: -5s, -1s, +1s, +5s
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    holdSeconds = (holdSeconds - 5).coerceAtLeast(1)
                                    holdInputText = holdSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("hold_minus_5_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("-5s", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Surface(
                                onClick = {
                                    holdSeconds = (holdSeconds - 1).coerceAtLeast(1)
                                    holdInputText = holdSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("hold_minus_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease Hold 1s", modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                onClick = {
                                    holdSeconds = (holdSeconds + 1).coerceAtMost(600)
                                    holdInputText = holdSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("hold_plus_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase Hold 1s", modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                onClick = {
                                    holdSeconds = (holdSeconds + 5).coerceAtMost(600)
                                    holdInputText = holdSeconds.toString()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("hold_plus_5_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+5s", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        // Presets (FlowRow wraps cleanly on all screen widths)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(15, 25, 30, 37, 45, 60, 90).forEach { s ->
                                val isSelected = holdSeconds == s
                                Surface(
                                    onClick = {
                                        holdSeconds = s
                                        holdInputText = s.toString()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CoralHold else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) CoralHold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.testTag("hold_preset_${s}s")
                                ) {
                                    Text(
                                        text = "${s}s",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Per-Stretch Audio Cue Toggle
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (voiceCueEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = if (voiceCueEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Column {
                                Text(
                                    text = "Spoken Coaching Cues",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (voiceCueEnabled) "Detailed verbal guidance will play" else "Only timer beeps, instructions muted",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = voiceCueEnabled,
                            onCheckedChange = { voiceCueEnabled = it },
                            modifier = Modifier.testTag("dialog_voice_cue_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                // Spoken Guidance / Notes Field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Spoken Coaching Guidance & Notes") },
                    placeholder = { Text("e.g. Keep shoulders relaxed, breathe into the stretch...") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_stretch_notes_input"),
                    supportingText = {
                        Text("This text is spoken aloud during the stretch hold when voice cues are enabled.")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        val finalPrep = prepInputText.toIntOrNull() ?: prepSeconds
                        val finalHold = (holdInputText.toIntOrNull() ?: holdSeconds).coerceAtLeast(1)
                        onSave(
                            stretch?.id ?: 0L,
                            name.trim(),
                            finalPrep,
                            finalHold,
                            notes.trim().ifBlank { null },
                            voiceCueEnabled,
                            indianName.trim().ifBlank { null }
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_stretch_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isNew) "Add to Routine" else "Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_edit_button")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
