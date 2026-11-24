package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.LibraryStretchEntity
import com.example.ui.theme.CoralHold
import com.example.ui.theme.CyanPrep

@Composable
fun EditLibraryStretchDialog(
    stretch: LibraryStretchEntity?,
    onDismiss: () -> Unit,
    onSave: (LibraryStretchEntity) -> Unit
) {
    val isNew = stretch == null
    var name by remember { mutableStateOf(stretch?.name ?: "") }
    var category by remember { mutableStateOf(stretch?.category ?: "Custom") }
    var prepSeconds by remember { mutableIntStateOf(stretch?.defaultPrepSeconds ?: 8) }
    var prepInputText by remember { mutableStateOf((stretch?.defaultPrepSeconds ?: 8).toString()) }

    var holdSeconds by remember { mutableIntStateOf(stretch?.defaultHoldSeconds ?: 30) }
    var holdInputText by remember { mutableStateOf((stretch?.defaultHoldSeconds ?: 30).toString()) }

    var prepCue by remember { mutableStateOf(stretch?.prepCue ?: "") }
    var holdCue by remember { mutableStateOf(stretch?.holdCue ?: "") }
    var benefits by remember { mutableStateOf(stretch?.benefits ?: "") }
    var isNameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (isNew) "Add to Library" else "Edit Library Stretch",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) isNameError = false
                    },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g. child custom pose") },
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
                        .testTag("edit_library_name_input")
                )

                // Category
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    placeholder = { Text("e.g. Lower Back & Core") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_library_category_input")
                )

                // Preparation Time Section
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Default Preparation",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

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
                                    textStyle = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = CyanPrep
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .width(64.dp)
                                        .height(44.dp)
                                )
                                Text("sec", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyanPrep)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        prepSeconds = (prepSeconds - 5).coerceAtLeast(0)
                                        prepInputText = prepSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("-5s", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                IconButton(
                                    onClick = {
                                        prepSeconds = (prepSeconds - 1).coerceAtLeast(0)
                                        prepInputText = prepSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "-1s", modifier = Modifier.size(16.dp))
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        prepSeconds = (prepSeconds + 1).coerceAtMost(300)
                                        prepInputText = prepSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "+1s", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        prepSeconds = (prepSeconds + 5).coerceAtMost(300)
                                        prepInputText = prepSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("+5s", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                // Hold Duration Section
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Default Hold Time",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

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
                                    textStyle = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = CoralHold
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .width(64.dp)
                                        .height(44.dp)
                                )
                                Text("sec", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CoralHold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        holdSeconds = (holdSeconds - 5).coerceAtLeast(1)
                                        holdInputText = holdSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("-5s", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                IconButton(
                                    onClick = {
                                        holdSeconds = (holdSeconds - 1).coerceAtLeast(1)
                                        holdInputText = holdSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "-1s", modifier = Modifier.size(16.dp))
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        holdSeconds = (holdSeconds + 1).coerceAtMost(600)
                                        holdInputText = holdSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "+1s", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = {
                                        holdSeconds = (holdSeconds + 5).coerceAtMost(600)
                                        holdInputText = holdSeconds.toString()
                                    },
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("+5s", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                // Voice Coaching Instruction
                OutlinedTextField(
                    value = holdCue,
                    onValueChange = { holdCue = it },
                    label = { Text("Voice Coaching / Form Guidance") },
                    placeholder = { Text("e.g. Sink hips back onto heels, reach arms forward, breathe gently.") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Anatomical Benefits
                OutlinedTextField(
                    value = benefits,
                    onValueChange = { benefits = it },
                    label = { Text("Benefits & Target Areas") },
                    placeholder = { Text("e.g. Opens lumbar spine, relaxes shoulders and calms central nervous system.") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
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
                        val entity = LibraryStretchEntity(
                            id = stretch?.id ?: "custom_${System.currentTimeMillis()}",
                            name = name.trim(),
                            category = category.trim().ifBlank { "Custom" },
                            defaultPrepSeconds = finalPrep,
                            defaultHoldSeconds = finalHold,
                            prepCue = prepCue.trim().ifBlank { "Prepare for ${name.trim()}." },
                            holdCue = holdCue.trim().ifBlank { "Breathe deeply and hold steady." },
                            benefits = benefits.trim(),
                            isCustom = stretch?.isCustom ?: true
                        )
                        onSave(entity)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("save_library_stretch_button")
            ) {
                Text(if (isNew) "Add to Library" else "Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
