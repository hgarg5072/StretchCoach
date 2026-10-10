package com.stretchcoach

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stretchcoach.data.StretchEntity
import com.stretchcoach.settings.AppSettings
import com.stretchcoach.settings.DarkModePreference
import com.stretchcoach.settings.VoiceMode
import com.stretchcoach.ui.theme.StretchCoachTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CoachViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            StretchCoachTheme(settings.darkModePreference) {
                StretchCoachApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StretchCoachApp(viewModel: CoachViewModel) {
    val stretches by viewModel.stretches.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var editorStretch by remember { mutableStateOf<StretchEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val activity = androidx.compose.ui.platform.LocalContext.current as? ComponentActivity

    DisposableEffect(playerState.isRunning) {
        val window = activity?.window
        if (playerState.isRunning) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BrandMark(Modifier.size(40.dp))
                        Column {
                            Text("Stretch Coach", fontWeight = FontWeight.Bold)
                            Text(
                                "Mobility routines with voice cues",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = {
                    editorStretch = null
                    showEditor = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add stretch")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
                        )
                    )
                )
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Routine") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Player") }
                )
            }

            if (selectedTab == 0) {
                RoutineScreen(
                    stretches = stretches,
                    onEdit = {
                        editorStretch = it
                        showEditor = true
                    },
                    onDelete = viewModel::deleteStretch,
                    onEnabledChange = viewModel::setStretchEnabled,
                    onReorder = viewModel::reorderStretches
                )
            } else {
                PlayerScreen(
                    stretches = stretches,
                    state = playerState,
                    onStart = viewModel::startPlayer,
                    onPause = viewModel::pausePlayer,
                    onResume = viewModel::resumePlayer,
                    onSkip = viewModel::skipStretch,
                    onPrevious = viewModel::previousStretch,
                    onRestart = viewModel::restartStretch,
                    onStop = viewModel::stopPlayer,
                    onSettings = { showSettings = true }
                )
            }
        }
    }

    if (showEditor) {
        StretchEditorDialog(
            stretch = editorStretch,
            onDismiss = { showEditor = false },
            onSave = { name, prep, hold, notes ->
                viewModel.saveStretch(editorStretch, name, prep, hold, notes)
                showEditor = false
            }
        )
    }

    if (showSettings) {
        SettingsDialog(
            settings = settings,
            onDismiss = { showSettings = false },
            onDarkModePreference = viewModel::setDarkModePreference,
            onSpokenCues = viewModel::setSpokenCuesEnabled,
            onCountdown = viewModel::setAnnounceCountdown,
            onHalfwayCue = viewModel::setHalfwayCueEnabled,
            onNextUpCue = viewModel::setNextUpCueEnabled,
            onVoiceMode = viewModel::setVoiceMode
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoutineScreen(
    stretches: List<StretchEntity>,
    onEdit: (StretchEntity) -> Unit,
    onDelete: (StretchEntity) -> Unit,
    onEnabledChange: (StretchEntity, Boolean) -> Unit,
    onReorder: (List<Long>) -> Unit
) {
    val localItems = remember { mutableStateListOf<StretchEntity>() }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    var dragDelta by remember { mutableStateOf(0f) }

    LaunchedEffect(stretches, draggingId) {
        if (draggingId == null) {
            localItems.clear()
            localItems.addAll(stretches)
        }
    }

    if (stretches.isEmpty()) {
        EmptyState("Add your first stretch to build a routine.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            RoutineSummary(stretches = stretches)
        }
        itemsIndexed(localItems, key = { _, stretch -> stretch.id }) { index, stretch ->
            StretchRow(
                stretch = stretch,
                modifier = Modifier
                    .animateItem()
                    .alpha(if (stretch.enabled) 1f else 0.55f)
                    .pointerInput(stretch.id, index) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingId = stretch.id
                                dragDelta = 0f
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragDelta += amount.y
                                val moveBy = (dragDelta / 112.dp.toPx()).toInt()
                                if (moveBy != 0) {
                                    val from = localItems.indexOfFirst { it.id == stretch.id }
                                    val to = (from + moveBy).coerceIn(0, localItems.lastIndex)
                                    if (from >= 0 && from != to) {
                                        localItems.add(to, localItems.removeAt(from))
                                    }
                                    dragDelta = 0f
                                }
                            },
                            onDragEnd = {
                                draggingId = null
                                onReorder(localItems.map { it.id })
                            },
                            onDragCancel = {
                                draggingId = null
                            }
                        )
                    },
                isDragging = draggingId == stretch.id,
                onEdit = { onEdit(stretch) },
                onDelete = { onDelete(stretch) },
                onEnabledChange = { onEnabledChange(stretch, it) }
            )
        }
    }
}

@Composable
private fun RoutineSummary(stretches: List<StretchEntity>) {
    val enabled = stretches.count { it.enabled }
    val totalSeconds = stretches.filter { it.enabled }.sumOf { it.preparationSeconds + it.holdSeconds }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "Today routine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "$enabled enabled stretches - ${totalSeconds / 60} min ${totalSeconds % 60}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                )
            }
        }
    }
}

@Composable
private fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ExerciseArtwork(
    stretchName: String,
    animated: Boolean,
    modifier: Modifier = Modifier
) {
    val image = ImageBitmap.imageResource(R.drawable.exercise_sprite_sheet)
    val transition = rememberInfiniteTransition(label = "exercise-artwork")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pose"
    )
    val scale = if (animated) 1.02f + pulse * 0.045f else 1f
    val index = exerciseImageIndex(stretchName)
    val background = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.74f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.52f)
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val columns = 5
            val rows = 2
            val cellWidth = image.width / columns
            val cellHeight = image.height / rows
            val sourceColumn = index % columns
            val sourceRow = index / columns
            val destinationWidth = size.width * scale
            val destinationHeight = size.height * scale
            val offsetX = ((size.width - destinationWidth) / 2f).toInt()
            val offsetY = ((size.height - destinationHeight) / 2f).toInt()
            clipRect {
                drawImage(
                    image = image,
                    srcOffset = IntOffset(sourceColumn * cellWidth, sourceRow * cellHeight),
                    srcSize = IntSize(cellWidth, cellHeight),
                    dstOffset = IntOffset(offsetX, offsetY),
                    dstSize = IntSize(destinationWidth.toInt(), destinationHeight.toInt())
                )
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f)
                        )
                    ),
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height)
                )
            }
        }
    }
}

private fun exerciseImageIndex(stretchName: String): Int {
    val name = stretchName.lowercase()
    return when {
        "neck" in name -> 0
        "hamstring" in name || "fold" in name -> 1
        "hip" in name || "lunge" in name -> 2
        "child" in name -> 3
        "cat" in name || "cow" in name -> 4
        "spinal" in name || "twist" in name -> 5
        "quad" in name -> 6
        "calf" in name || "wall" in name -> 7
        "shoulder" in name || "cross-body" in name -> 8
        "butterfly" in name -> 9
        else -> 0
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StretchRow(
    stretch: StretchEntity,
    modifier: Modifier,
    isDragging: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DragHandle,
                        contentDescription = "Drag to reorder",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Text(
                    text = stretch.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = stretch.enabled,
                    onCheckedChange = onEnabledChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseArtwork(
                    stretchName = stretch.name,
                    animated = false,
                    modifier = Modifier.size(width = 112.dp, height = 86.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            border = null,
                            label = { Text("Prep ${stretch.preparationSeconds}s", maxLines = 1) }
                        )
                        AssistChip(
                            onClick = {},
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            border = null,
                            label = { Text("Hold ${stretch.holdSeconds}s", maxLines = 1) }
                        )
                    }
                    AnimatedVisibility(stretch.notes?.isNotBlank() == true) {
                        Text(
                            text = stretch.notes.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEdit,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit stretch")
                }
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete stretch")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayerScreen(
    stretches: List<StretchEntity>,
    state: PlayerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkip: () -> Unit,
    onPrevious: () -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit,
    onSettings: () -> Unit
) {
    val enabledCount = stretches.count { it.enabled }
    val displayStretch = state.currentStretch ?: stretches.firstOrNull { it.enabled }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < 760.dp
        val imageHeight = if (compact) 244.dp else 332.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = if (compact) 12.dp else 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.tertiaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                ExerciseArtwork(
                    stretchName = displayStretch?.name ?: "ready",
                    animated = state.isRunning,
                    modifier = Modifier.fillMaxSize().padding(if (compact) 10.dp else 16.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (state.phase == PlayerPhase.Idle) "$enabledCount" else "${state.secondsRemaining}",
                        style = if (compact) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Column(Modifier.weight(1f)) {
                        val routinePosition = if (state.routineSize > 0) {
                            "${(state.currentIndex + 1).coerceIn(1, state.routineSize)} of ${state.routineSize}"
                        } else {
                            ""
                        }
                        Text(
                            text = if (state.phase == PlayerPhase.Idle) {
                                "stretches enabled"
                            } else {
                                listOf(state.phase.name.lowercase(), routinePosition)
                                    .filter { it.isNotBlank() }
                                    .joinToString(" - ")
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = state.currentStretch?.name ?: "Ready when you are",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (state.nextStretch != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        border = null,
                        label = { Text("Next: ${state.nextStretch.name}", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            val progress = if (state.totalSeconds > 0) {
                1f - (state.secondsRemaining.toFloat() / state.totalSeconds.toFloat())
            } else {
                0f
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(if (compact) 14.dp else 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = state.currentStretch?.name ?: state.message,
                        style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = state.currentStretch?.notes ?: "Create a routine, press play, and follow the spoken cues.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onPrevious,
                    enabled = state.isRunning,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                FilledIconButton(onClick = if (state.isRunning && !state.isPaused) onPause else if (state.isPaused) onResume else onStart) {
                    Icon(
                        imageVector = if (state.isRunning && !state.isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isRunning && !state.isPaused) "Pause" else "Play"
                    )
                }
                IconButton(
                    onClick = onSkip,
                    enabled = state.isRunning,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip")
                }
                IconButton(
                    onClick = onRestart,
                    enabled = state.isRunning,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Restart")
                }
                IconButton(
                    onClick = onStop,
                    enabled = state.isRunning || state.phase == PlayerPhase.Complete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                }
                IconButton(
                    onClick = onSettings,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Player settings")
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun StretchEditorDialog(
    stretch: StretchEntity?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, String) -> Unit
) {
    var name by remember(stretch) { mutableStateOf(stretch?.name.orEmpty()) }
    var prep by remember(stretch) { mutableStateOf((stretch?.preparationSeconds ?: 10).toString()) }
    var hold by remember(stretch) { mutableStateOf((stretch?.holdSeconds ?: 30).toString()) }
    var notes by remember(stretch) { mutableStateOf(stretch?.notes.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (stretch == null) "Add stretch" else "Edit stretch") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = prep,
                        onValueChange = { prep = it.filter(Char::isDigit).take(3) },
                        label = { Text("Prep seconds") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hold,
                        onValueChange = { hold = it.filter(Char::isDigit).take(3) },
                        label = { Text("Hold seconds") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name,
                        prep.toIntOrNull() ?: 0,
                        hold.toIntOrNull() ?: 1,
                        notes
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDialog(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onDarkModePreference: (DarkModePreference) -> Unit,
    onSpokenCues: (Boolean) -> Unit,
    onCountdown: (Boolean) -> Unit,
    onHalfwayCue: (Boolean) -> Unit,
    onNextUpCue: (Boolean) -> Unit,
    onVoiceMode: (VoiceMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("Theme", style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    DarkModePreference.entries.forEachIndexed { index, preference ->
                        SegmentedButton(
                            selected = settings.darkModePreference == preference,
                            onClick = { onDarkModePreference(preference) },
                            shape = SegmentedButtonDefaults.itemShape(index, DarkModePreference.entries.size),
                            label = { Text(preference.name) }
                        )
                    }
                }
                Text("Voice", style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    VoiceMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = settings.voiceMode == mode,
                            onClick = { onVoiceMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index, VoiceMode.entries.size),
                            label = { Text(mode.name) }
                        )
                    }
                }
                SettingsSwitchRow(
                    title = "Spoken cues",
                    checked = settings.spokenCuesEnabled,
                    onCheckedChange = onSpokenCues
                )
                SettingsSwitchRow(
                    title = "Countdown announcements",
                    checked = settings.announceCountdown,
                    onCheckedChange = onCountdown
                )
                SettingsSwitchRow(
                    title = "Halfway cue",
                    checked = settings.halfwayCueEnabled,
                    onCheckedChange = onHalfwayCue
                )
                SettingsSwitchRow(
                    title = "Next stretch cue",
                    checked = settings.nextUpCueEnabled,
                    onCheckedChange = onNextUpCue
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
