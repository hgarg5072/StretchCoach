package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.PlayerState
import com.example.ui.StretchViewModel
import com.example.ui.components.HeaderBar
import com.example.ui.components.RoutineBackupDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.CoralHold
import com.example.ui.theme.CyanPrep

@Composable
fun MainScreen(
    viewModel: StretchViewModel,
    modifier: Modifier = Modifier
) {
    val currentEnabledStretches by viewModel.currentRoutineEnabledStretches.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val playerState by viewModel.playerUiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val voiceEngineInfo by viewModel.voiceEngineInfo.collectAsStateWithLifecycle()
    val backupStatus by viewModel.backupStatus.collectAsStateWithLifecycle()
    val downloadedTracks by viewModel.downloadedTracks.collectAsStateWithLifecycle()
    val onlineAudioResults by viewModel.onlineAudioResults.collectAsStateWithLifecycle()
    val isSearchingOnlineAudio by viewModel.isSearchingOnlineAudio.collectAsStateWithLifecycle()
    val downloadingTrackId by viewModel.downloadingTrackId.collectAsStateWithLifecycle()
    val onlineAudioMessage by viewModel.onlineAudioMessage.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Re-scan TTS voices when returning from Android TTS Settings
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshVoiceEngine()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    androidx.activity.compose.BackHandler(enabled = selectedTab == 1) {
        viewModel.selectTab(0)
    }

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showOnlineAudioDialog by remember { mutableStateOf(false) }

    val isTimerFullScreen = selectedTab == 1

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            if (!isTimerFullScreen) {
                HeaderBar(
                    onOpenSettings = { showSettingsDialog = true }
                )
            }
        },
        bottomBar = {
            if (!isTimerFullScreen) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    // Routine Tab
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.SelfImprovement else Icons.Outlined.SelfImprovement,
                                contentDescription = "Routine",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Routines",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_routine"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )

                    // Player Tab
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                                contentDescription = "Player",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Timer",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_player"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                1 -> {
                    PlayerScreen(
                        playerState = playerState,
                        enabledStretches = currentEnabledStretches,
                        onPlay = { viewModel.startRoutine(0) },
                        onPause = { viewModel.pause() },
                        onResume = { viewModel.resume() },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onRestart = { viewModel.restartCurrentStretch() },
                        onStop = { viewModel.stopSession() },
                        onOpenSettings = { showSettingsDialog = true },
                        onNavigateBack = { viewModel.selectTab(0) },
                        ambientSoundscape = userSettings.ambientSoundscape,
                        customTrackTitle = userSettings.customTrackTitle,
                        onCycleAmbientSoundscape = { viewModel.cycleAmbientSoundscape() },
                        studioLightingMode = userSettings.studioLightingMode,
                        studioAromaProp = userSettings.studioAromaProp,
                        companionPet = userSettings.companionPet,
                        swayingLeavesEnabled = userSettings.swayingLeavesEnabled,
                        sunbeamsEnabled = userSettings.sunbeamsEnabled,
                        onTransitionClipFinished = { viewModel.onTransitionClipFinished() },
                        onFlowRoundCompleted = { viewModel.onFlowRoundCompleted() },
                        onSetFlowTargetRounds = { viewModel.setFlowTargetRounds(it) },
                        onSetFlowPlaybackSpeed = { viewModel.setFlowPlaybackSpeed(it) }
                    )
                }
                else -> {
                    RoutineScreen(
                        viewModel = viewModel,
                        onStartRoutine = { startingIndex ->
                            viewModel.startRoutine(startingIndex)
                        }
                    )
                }
            }
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            settings = userSettings,
            voiceEngineInfo = voiceEngineInfo,
            backupStatus = backupStatus,
            currentUser = currentUser,
            syncState = syncState,
            authLoading = authLoading,
            authError = authError,
            onSignInGoogle = {
                activity?.let { act -> viewModel.signInWithGoogle(act) }
            },
            onSignOut = {
                viewModel.signOut(context)
            },
            onSyncNow = {
                viewModel.triggerCloudSync()
            },
            onOpenBackupSafeguard = {
                showBackupDialog = true
            },
            onSetTheme = { viewModel.setTheme(it) },
            onSetVoiceMode = { viewModel.setVoiceMode(it) },
            onSetAsanaPronunciationStyle = { viewModel.setAsanaPronunciationStyle(it) },
            onToggleSpokenCues = { viewModel.setSpokenCuesEnabled(it) },
            onToggleCoachingCues = { viewModel.setCoachingCuesEnabled(it) },
            onToggleCountdown = { viewModel.setCountdownAnnouncements(it) },
            onToggleHalfwayCue = { viewModel.setHalfwayCue(it) },
            onSetAmbientSoundscape = { viewModel.setAmbientSoundscape(it) },
            onSetAmbientVolume = { viewModel.setAmbientVolume(it) },
            onToggleTransitionChimes = { viewModel.setTransitionChimesEnabled(it) },
            onOpenOnlineAudioPicker = {
                viewModel.refreshDownloadedTracks()
                showOnlineAudioDialog = true
            },
            onSetStudioLightingMode = { viewModel.setStudioLightingMode(it) },
            onSetStudioAromaProp = { viewModel.setStudioAromaProp(it) },
            onSetCompanionPet = { viewModel.setCompanionPet(it) },
            onToggleCompanionDog = { viewModel.setCompanionDogEnabled(it) },
            onToggleSwayingLeaves = { viewModel.setSwayingLeavesEnabled(it) },
            onToggleSunbeams = { viewModel.setSunbeamsEnabled(it) },
            onToggleHubTransitions = { viewModel.setHubTransitionsEnabled(it) },
            onTestVoice = {
                viewModel.testVoicePronunciation(
                    "Bhujangasana (Cobra Pose)",
                    userSettings.asanaPronunciationStyle
                )
            },
            onDismiss = {
                viewModel.clearAuthError()
                showSettingsDialog = false
            }
        )
    }

    if (showOnlineAudioDialog) {
        com.example.ui.components.OnlineAudioPickerDialog(
            downloadedTracks = downloadedTracks,
            activeTrackPath = userSettings.customTrackPath,
            onlineResults = onlineAudioResults,
            isSearching = isSearchingOnlineAudio,
            downloadingTrackId = downloadingTrackId,
            statusMessage = onlineAudioMessage,
            onSearch = { viewModel.searchOnlineAudio(it) },
            onDownloadTrack = { viewModel.downloadOnlineTrack(it) },
            onSelectDownloaded = { viewModel.selectCustomAudioTrack(it) },
            onDeleteDownloaded = { viewModel.deleteDownloadedTrack(it) },
            onImportLocalUri = { viewModel.importLocalAudio(it) },
            onDismiss = {
                viewModel.clearOnlineAudioMessage()
                showOnlineAudioDialog = false
            }
        )
    }

    // Routine Safeguard & Backup Dialog
    if (showBackupDialog) {
        RoutineBackupDialog(
            backupStatus = backupStatus,
            onExport = { onResult ->
                viewModel.exportCustomRoutines(onResult)
            },
            onImport = { json, onResult ->
                viewModel.importCustomRoutines(json, onResult)
            },
            onRestoreAutoBackup = { onResult ->
                viewModel.restoreFromAutoBackup(onResult)
            },
            onDismiss = { showBackupDialog = false }
        )
    }
}
