package com.example.ui.screens

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
import com.example.data.RoutineEntity
import com.example.data.StretchEntity
import com.example.ui.StretchViewModel
import com.example.ui.components.AddFromLibraryDialog
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
    val libraryStretches by viewModel.libraryStretches.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showCreateRoutineDialog by remember { mutableStateOf(false) }
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Routines Selector Horizontal Pills (long-press any pill for actions)
        if (routines.isNotEmpty()) {
            RoutineSelectorRow(
                routines = routines,
                selectedRoutineId = selectedRoutineId,
                onSelectRoutine = { viewModel.selectRoutine(it) },
                onCreateNewRoutine = { showCreateRoutineDialog = true },
                onDeleteRoutine = { routineToDelete = it },
                onDuplicateRoutine = { routineToDuplicate = it },
                onLongPressRoutine = { longPressedRoutine = it }
            )
        }

        // Slim 1-line Cloud Sync chip if user has custom routines and isn't signed in
        val context = LocalContext.current
        val activity = context as? Activity
        var dismissedBanner by remember { mutableStateOf(false) }
        val hasCustomRoutines = routines.any { it.isCustom }

        if (hasCustomRoutines && currentUser == null && !dismissedBanner) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .testTag("cloud_sync_prompt_banner"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Sign in to sync custom routines",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    TextButton(
                        onClick = { activity?.let { act -> viewModel.signInWithGoogle(act) } },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("banner_sign_in_button")
                    ) {
                        Text("Sign In", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    IconButton(
                        onClick = { dismissedBanner = true },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Summary Card for the current routine
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
            onShareToCommunity = if (activeRoutine != null && activeRoutine.isCustom) {
                { routineToPublish = activeRoutine }
            } else null
        )

        // Stretch list
        if (localStretches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("stretch_list"),
                contentPadding = PaddingValues(top = 4.dp, bottom = 76.dp)
            ) {
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
        }

        // Thumb-friendly Floating Bottom Action Bar: Library, + Custom, Copy
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showLibraryDialog = true },
                    modifier = Modifier
                        .weight(1.15f)
                        .height(40.dp)
                        .testTag("open_library_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text("Library", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false)
                }

                OutlinedButton(
                    onClick = {
                        editingStretch = null
                        showEditDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("add_custom_stretch_button"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Custom", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false)
                }

                OutlinedButton(
                    onClick = { activeRoutine?.let { routineToDuplicate = it } },
                    modifier = Modifier
                        .weight(0.88f)
                        .height(40.dp)
                        .testTag("copy_routine_button"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy & Edit Routine", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.size(3.dp))
                    Text("Copy", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false)
                }

                OutlinedButton(
                    onClick = { viewModel.optimizeCurrentRoutineFlow() },
                    enabled = localStretches.size >= 2,
                    modifier = Modifier
                        .weight(0.88f)
                        .height(40.dp)
                        .testTag("optimize_flow_button"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Text("Flow", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false)
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
                                routineToPublish = routine
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Share to Community")
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

