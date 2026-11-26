package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoutineEntity
import com.example.data.StretchEntity
import com.example.ui.StretchViewModel
import com.example.ui.components.AddFromLibraryDialog
import com.example.ui.components.AiRoutineBuilderDialog
import com.example.ui.components.CreateRoutineDialog
import com.example.ui.components.DuplicateRoutineDialog
import com.example.ui.components.EditRoutineDialog
import com.example.ui.components.EditStretchDialog
import com.example.ui.components.RoutineSelectorRow
import com.example.ui.components.RoutineSummaryCard
import com.example.ui.components.StretchItemCard

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

@Composable
fun RoutineScreen(
    viewModel: StretchViewModel,
    onStartRoutine: (startingIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val routines by viewModel.allRoutines.collectAsState()
    val selectedRoutineId by viewModel.selectedRoutineId.collectAsState()
    val stretches by viewModel.currentRoutineStretches.collectAsState()
    val smartFlowActiveRoutineIds by viewModel.smartFlowActiveRoutineIds.collectAsState()
    val libraryStretches by viewModel.libraryStretches.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val aiRoutineState by viewModel.aiRoutineBuilderState.collectAsState()
    val onDeviceAiCapability by viewModel.onDeviceAiCapability.collectAsState()

    var showCreateRoutineDialog by remember { mutableStateOf(false) }
    var showAiRoutineDialog by remember { mutableStateOf(false) }
    var showLibraryDialog by remember { mutableStateOf(false) }
    var editingStretch by remember { mutableStateOf<StretchEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var routineToDelete by remember { mutableStateOf<RoutineEntity?>(null) }
    var routineToDuplicate by remember { mutableStateOf<RoutineEntity?>(null) }
    var routineToEdit by remember { mutableStateOf<RoutineEntity?>(null) }
    var routineToPublish by remember { mutableStateOf<RoutineEntity?>(null) }
    var longPressedRoutine by remember { mutableStateOf<RoutineEntity?>(null) }

    // Local state for smooth drag and drop shuffling
    var localStretches by remember(stretches) { mutableStateOf(stretches) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val itemThresholdPx = with(density) { 80.dp.toPx() }

    val activeRoutine = routines.find { it.id == selectedRoutineId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("routine_screen")
    ) {
        // Unified scrollable Routines feed so both Portrait & Landscape stay airy and uncluttered
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("stretch_list"),
            contentPadding = PaddingValues(top = 2.dp, bottom = 84.dp)
        ) {
            // 1. Horizontal Routine Selector Pills
            if (routines.isNotEmpty()) {
                item(key = "routine_selector_header") {
                    RoutineSelectorRow(
                        routines = routines,
                        selectedRoutineId = selectedRoutineId,
                        onSelectRoutine = { viewModel.selectRoutine(it) },
                        onCreateNewRoutine = { showCreateRoutineDialog = true },
                        onOpenAiRoutineBuilder = {
                            viewModel.resetAiRoutineBuilder()
                            showAiRoutineDialog = true
                        },
                        onDeleteRoutine = { routineToDelete = it },
                        onDuplicateRoutine = { routineToDuplicate = it },
                        onLongPressRoutine = { longPressedRoutine = it }
                    )
                }
            }

            // 2. Hero Summary Card (Start Routine + Duration + compact Flow & Copy chips)
            item(key = "routine_summary_header") {
                RoutineSummaryCard(
                    routine = activeRoutine,
                    stretches = localStretches,
                    isCloudSynced = currentUser != null,
                    onStartRoutine = { onStartRoutine(0) },
                    onOpenLibrary = { showLibraryDialog = true },
                    onAddCustomStretch = {
                        editingStretch = null
                        showEditDialog = true
                    },
                    onDuplicateRoutine = {
                        activeRoutine?.let { routineToDuplicate = it }
                    },
                    onEditRoutine = {
                        activeRoutine?.let { routineToEdit = it }
                    },
                    onOptimizeFlow = if (localStretches.size >= 2) {
                        { viewModel.optimizeCurrentRoutineFlow() }
                    } else null,
                    isFlowOptimized = activeRoutine?.id in smartFlowActiveRoutineIds
                )
            }

            // 3. Stretch list or empty state
            if (localStretches.isEmpty()) {
                item(key = "routine_empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "This routine has no stretches yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Select from 150 guided stretches & Indian asanas or add your own.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Button(
                                onClick = { showLibraryDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("empty_state_browse_library")
                            ) {
                                Text("Browse 150 Asanas")
                            }
                        }
                    }
                }
            } else {
                item(key = "stretches_section_label") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STRETCH SEQUENCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Drag handle to reorder",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                        )
                    }
                }

                itemsIndexed(localStretches, key = { _, item -> item.id }) { index, stretch ->
                    val isDragging = draggedIndex == index

                    StretchItemCard(
                        stretch = stretch,
                        index = index,
                        totalCount = localStretches.size,
                        isDragging = isDragging,
                        dragOffsetY = if (isDragging) dragOffsetY else 0f,
                        onDragStart = {
                            draggedIndex = index
                            dragOffsetY = 0f
                        },
                        onDrag = { dy ->
                            dragOffsetY += dy
                            val curr = draggedIndex ?: return@StretchItemCard
                            if (dragOffsetY > itemThresholdPx && curr < localStretches.size - 1) {
                                val mutable = localStretches.toMutableList()
                                val temp = mutable[curr]
                                mutable[curr] = mutable[curr + 1]
                                mutable[curr + 1] = temp
                                localStretches = mutable
                                draggedIndex = curr + 1
                                dragOffsetY -= itemThresholdPx
                            } else if (dragOffsetY < -itemThresholdPx && curr > 0) {
                                val mutable = localStretches.toMutableList()
                                val temp = mutable[curr]
                                mutable[curr] = mutable[curr - 1]
                                mutable[curr - 1] = temp
                                localStretches = mutable
                                draggedIndex = curr - 1
                                dragOffsetY += itemThresholdPx
                            }
                        },
                        onDragEnd = {
                            val finalIdx = draggedIndex
                            if (finalIdx != null) {
                                viewModel.reorderStretches(localStretches.map { it.id })
                            }
                            draggedIndex = null
                            dragOffsetY = 0f
                        },
                        onDragCancel = {
                            localStretches = stretches
                            draggedIndex = null
                            dragOffsetY = 0f
                        },
                        onToggleEnabled = { enabled ->
                            viewModel.toggleStretchEnabled(stretch.id, enabled)
                        },
                        onToggleVoiceCue = { voiceEnabled ->
                            viewModel.toggleStretchVoiceCue(stretch.id, voiceEnabled)
                        },
                        onEdit = {
                            editingStretch = stretch
                            showEditDialog = true
                        },
                        onDelete = {
                            viewModel.deleteStretch(stretch)
                        },
                        onPlaySingle = {
                            val enabledIndex = localStretches.filter { it.enabled }.indexOfFirst { it.id == stretch.id }
                            if (enabledIndex >= 0) {
                                onStartRoutine(enabledIndex)
                            }
                        }
                    )
                }
            }
        }

        // Prominent WhatsApp Meta AI–style floating AI Coach button on bottom-right
        Surface(
            onClick = {
                viewModel.resetAiRoutineBuilder()
                showAiRoutineDialog = true
            },
            shape = androidx.compose.foundation.shape.CircleShape,
            shadowElevation = 10.dp,
            tonalElevation = 6.dp,
            color = androidx.compose.ui.graphics.Color.Transparent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 74.dp)
                .size(62.dp)
                .testTag("floating_ai_coach_fab")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color(0xFF0EA5E9), // Vibrant Sky
                                androidx.compose.ui.graphics.Color(0xFF14B8A6), // Teal
                                androidx.compose.ui.graphics.Color(0xFF8B5CF6)  // Iris Violet
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .padding(3.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Routine Coach",
                            tint = androidx.compose.ui.graphics.Color(0xFF0D9488),
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = androidx.compose.ui.graphics.Color(0xFF0D9488)
                        )
                    }
                }
            }
        }

        // Sleek compact floating bottom pill: Add from Library + Custom Stretch
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 10.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showLibraryDialog = true },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("open_library_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        "Add from Library",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                OutlinedButton(
                    onClick = {
                        editingStretch = null
                        showEditDialog = true
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("add_custom_stretch_button"),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        "Custom",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }

    // Create Routine Dialog
    if (showCreateRoutineDialog) {
        CreateRoutineDialog(
            onDismiss = { showCreateRoutineDialog = false },
            onCreate = { name, description ->
                viewModel.createCustomRoutine(name, description)
            }
        )
    }

    // 3-Tier Hybrid AI Routine Builder Dialog (On-Device NPU -> Cloud Gemini -> Offline Smart Flow)
    if (showAiRoutineDialog) {
        AiRoutineBuilderDialog(
            state = aiRoutineState,
            capability = onDeviceAiCapability,
            onGenerate = { prompt, targetMinutes, posture ->
                viewModel.generateAiRoutine(prompt, targetMinutes, posture)
            },
            onSavePlan = { plan ->
                viewModel.saveGeneratedAiRoutine(plan) {
                    showAiRoutineDialog = false
                }
            },
            onReset = { viewModel.resetAiRoutineBuilder() },
            onDismiss = {
                viewModel.resetAiRoutineBuilder()
                showAiRoutineDialog = false
            }
        )
    }

    // Add From 50 Stretches Library Dialog
    if (showLibraryDialog) {
        AddFromLibraryDialog(
            libraryStretches = libraryStretches,
            onDismiss = { showLibraryDialog = false },
            onAddStretch = { libStretch ->
                viewModel.addLibraryStretch(libStretch)
            },
            onUpdateLibraryStretch = { updatedLibStretch ->
                viewModel.updateLibraryStretch(updatedLibStretch)
            },
            onAddNewLibraryStretch = { newLibStretch ->
                viewModel.addCustomLibraryStretch(
                    name = newLibStretch.name,
                    category = newLibStretch.category,
                    defaultPrepSeconds = newLibStretch.defaultPrepSeconds,
                    defaultHoldSeconds = newLibStretch.defaultHoldSeconds,
                    prepCue = newLibStretch.prepCue,
                    holdCue = newLibStretch.holdCue,
                    benefits = newLibStretch.benefits
                )
            },
            onDeleteLibraryStretch = { id ->
                viewModel.deleteLibraryStretch(id)
            },
            onPreviewVoice = { libStretch ->
                viewModel.previewLibraryStretchVoice(libStretch)
            },
            existingStretchNames = stretches.map { it.name.lowercase().trim() }.toSet()
        )
    }

    // Edit/Add Stretch Dialog
    if (showEditDialog) {
        EditStretchDialog(
            stretch = editingStretch,
            onDismiss = {
                showEditDialog = false
                editingStretch = null
            },
            onSave = { id, name, prep, hold, notes, voiceCueEnabled, indianName ->
                viewModel.saveStretch(id, name, prep, hold, notes, voiceCueEnabled, indianName)
            }
        )
    }

    // Confirm Delete Routine Dialog
    routineToDelete?.let { routine ->
        AlertDialog(
            onDismissRequest = { routineToDelete = null },
            title = { Text("Delete Routine", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${routine.name}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomRoutine(routine.id)
                        routineToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.testTag("confirm_delete_routine_button")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { routineToDelete = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Duplicate Routine Dialog
    routineToDuplicate?.let { routine ->
        val count = if (routine.id == selectedRoutineId) localStretches.size else -1
        DuplicateRoutineDialog(
            sourceRoutine = routine,
            stretchCount = count,
            onDismiss = { routineToDuplicate = null },
            onDuplicate = { newName, newDescription ->
                viewModel.duplicateRoutine(routine.id, newName, newDescription)
                routineToDuplicate = null
            }
        )
    }

    // Edit / Rename Routine Dialog
    routineToEdit?.let { routine ->
        EditRoutineDialog(
            routine = routine,
            onDismiss = { routineToEdit = null },
            onSave = { newName, newDescription ->
                viewModel.renameRoutine(routine.id, newName, newDescription)
                routineToEdit = null
            }
        )
    }

    // Long-Hold Routine Actions Dialog
    longPressedRoutine?.let { routine ->
        AlertDialog(
            onDismissRequest = { longPressedRoutine = null },
            title = { Text(routine.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            longPressedRoutine = null
                            routineToDuplicate = routine
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Copy & Edit Routine")
                    }
                    if (routine.isCustom) {
                        TextButton(
                            onClick = {
                                longPressedRoutine = null
                                routineToEdit = routine
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Rename Routine")
                        }
                        TextButton(
                            onClick = {
                                longPressedRoutine = null
                                routineToDelete = routine
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete Routine", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { longPressedRoutine = null }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Publish to Community Dialog
    routineToPublish?.let { routine ->
        val count = if (routine.id == selectedRoutineId) localStretches.size else stretches.size
        com.example.ui.components.PublishRoutineDialog(
            routine = routine,
            stretchCount = count,
            onPublish = { category, description ->
                viewModel.publishRoutineToCommunity(routine.id, category, description)
                routineToPublish = null
            },
            onDismiss = { routineToPublish = null }
        )
    }
}

