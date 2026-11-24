package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.VoiceEngineInfo
import com.example.data.AmbientSoundscape
import com.example.data.AppTheme
import com.example.data.AsanaPronunciationStyle
import com.example.data.BackupStatus
import com.example.data.CompanionPet
import com.example.data.StudioAromaProp
import com.example.data.StudioLightingMode
import com.example.data.SyncState
import com.example.data.UserSettings
import com.example.data.VoiceMode
import com.google.firebase.auth.FirebaseUser
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    settings: UserSettings,
    voiceEngineInfo: VoiceEngineInfo = VoiceEngineInfo(),
    backupStatus: BackupStatus = BackupStatus(),
    currentUser: FirebaseUser? = null,
    syncState: SyncState = SyncState.Idle,
    authLoading: Boolean = false,
    authError: String? = null,
    onSignInGoogle: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onSyncNow: () -> Unit = {},
    onOpenBackupSafeguard: () -> Unit = {},
    onSetTheme: (AppTheme) -> Unit,
    onSetVoiceMode: (VoiceMode) -> Unit,
    onSetAsanaPronunciationStyle: (AsanaPronunciationStyle) -> Unit = {},
    onToggleSpokenCues: (Boolean) -> Unit,
    onToggleCoachingCues: (Boolean) -> Unit,
    onToggleCountdown: (Boolean) -> Unit,
    onToggleHalfwayCue: (Boolean) -> Unit,
    onToggleNextStretchCue: (Boolean) -> Unit = {},
    onSetAmbientSoundscape: (AmbientSoundscape) -> Unit = {},
    onSetAmbientVolume: (Float) -> Unit = {},
    onToggleTransitionChimes: (Boolean) -> Unit = {},
    onOpenOnlineAudioPicker: () -> Unit = {},
    onSetStudioLightingMode: (StudioLightingMode) -> Unit = {},
    onSetStudioAromaProp: (StudioAromaProp) -> Unit = {},
    onSetCompanionPet: (CompanionPet) -> Unit = {},
    onToggleCompanionDog: (Boolean) -> Unit = {},
    onToggleSwayingLeaves: (Boolean) -> Unit = {},
    onToggleSunbeams: (Boolean) -> Unit = {},
    onToggleHubTransitions: (Boolean) -> Unit = {},
    onTestVoice: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Studio, 1 = Sound & Voice, 2 = Backup

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .heightIn(max = (screenHeightDp * 0.86f).dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Customize your studio, music & voice",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                            .testTag("close_settings_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Clean Category Tabs
                val tabs = listOf("Studio", "Sound & Voice", "Backup")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { idx, title ->
                        val selected = activeTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { activeTab = idx }
                                .padding(vertical = 9.dp)
                                .testTag("settings_tab_$idx"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (activeTab) {
                        0 -> StudioSettingsTab(
                            settings = settings,
                            onSetStudioLightingMode = onSetStudioLightingMode,
                            onSetStudioAromaProp = onSetStudioAromaProp,
                            onSetCompanionPet = onSetCompanionPet,
                            onToggleSwayingLeaves = onToggleSwayingLeaves,
                            onToggleSunbeams = onToggleSunbeams,
                            onToggleHubTransitions = onToggleHubTransitions,
                            onSetTheme = onSetTheme
                        )

                        1 -> SoundAndVoiceSettingsTab(
                            settings = settings,
                            voiceEngineInfo = voiceEngineInfo,
                            onSetAmbientSoundscape = onSetAmbientSoundscape,
                            onSetAmbientVolume = onSetAmbientVolume,
                            onToggleTransitionChimes = onToggleTransitionChimes,
                            onOpenOnlineAudioPicker = onOpenOnlineAudioPicker,
                            onSetVoiceMode = onSetVoiceMode,
                            onSetAsanaPronunciationStyle = onSetAsanaPronunciationStyle,
                            onToggleSpokenCues = onToggleSpokenCues,
                            onToggleCoachingCues = onToggleCoachingCues,
                            onToggleCountdown = onToggleCountdown,
                            onToggleHalfwayCue = onToggleHalfwayCue,
                            onTestVoice = onTestVoice,
                            onOpenTtsSettings = {
                                runCatching {
                                    context.startActivity(Intent("com.android.settings.TTS_SETTINGS"))
                                }
                            }
                        )

                        else -> BackupSettingsTab(
                            backupStatus = backupStatus,
                            onOpenBackupSafeguard = onOpenBackupSafeguard
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clean Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("close_settings_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudioSettingsTab(
    settings: UserSettings,
    onSetStudioLightingMode: (StudioLightingMode) -> Unit,
    onSetStudioAromaProp: (StudioAromaProp) -> Unit,
    onSetCompanionPet: (CompanionPet) -> Unit,
    onToggleSwayingLeaves: (Boolean) -> Unit,
    onToggleSunbeams: (Boolean) -> Unit,
    onToggleHubTransitions: (Boolean) -> Unit,
    onSetTheme: (AppTheme) -> Unit
) {
    // 1. Studio Lighting Card
    SettingsSectionCard(title = "Studio Lighting") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StudioLightingMode.values().forEach { mode ->
                val selected = settings.studioLightingMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onSetStudioLightingMode(mode) }
                        .padding(vertical = 8.dp)
                        .testTag("lighting_btn_${mode.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }

    // 2. Room Prop Card
    SettingsSectionCard(
        title = "Studio Floor Prop",
        subtitle = "Choose a calming prop beside your yoga mat"
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StudioAromaProp.values().forEach { prop ->
                val selected = settings.studioAromaProp == prop
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSetStudioAromaProp(prop) }
                        .testTag("aroma_btn_${prop.name}")
                ) {
                    Text(
                        text = prop.displayName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // 3. Companion Pet Selector Card (Off / Golden Puppy / Calico Cat)
    SettingsSectionCard(title = "Studio Companion") {
        Text(
            text = "Golden Puppy rests beside your mat, or Calico Cat strolls safely across open studio floor",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CompanionPet.values().forEach { pet ->
                val selected = settings.companionPet == pet
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onSetCompanionPet(pet) }
                        .padding(vertical = 8.dp)
                        .testTag("companion_pet_${pet.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pet.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    // 4. Atmosphere Toggles Card
    SettingsSectionCard(title = "Atmosphere & Flow") {
        SettingToggleRow(
            title = "Glass Furin Wind Chime",
            subtitle = "Gentle swaying Studio Ghibli glass chime by the window",
            checked = settings.swayingLeavesEnabled,
            enabled = true,
            onCheckedChange = onToggleSwayingLeaves,
            tag = "toggle_swaying_leaves"
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        SettingToggleRow(
            title = "Floating Light & Fireflies",
            subtitle = "Warm dust motes by day, soft fireflies at night",
            checked = settings.sunbeamsEnabled,
            enabled = true,
            onCheckedChange = onToggleSunbeams,
            tag = "toggle_sunbeams"
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        SettingToggleRow(
            title = "Guided Posture Transitions",
            subtitle = "Shows how to move between standing & floor poses",
            checked = settings.hubTransitionsEnabled,
            enabled = true,
            onCheckedChange = onToggleHubTransitions,
            tag = "toggle_hub_transitions"
        )
    }

    // 4. App Appearance Card
    SettingsSectionCard(title = "App Appearance") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppTheme.values().forEach { themeOption ->
                val selected = settings.theme == themeOption
                val (label, icon) = when (themeOption) {
                    AppTheme.SYSTEM -> "System" to Icons.Default.BrightnessAuto
                    AppTheme.LIGHT -> "Light" to Icons.Default.LightMode
                    AppTheme.DARK -> "Dark" to Icons.Default.DarkMode
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onSetTheme(themeOption) }
                        .padding(vertical = 8.dp)
                        .testTag("theme_option_${themeOption.name}"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SoundAndVoiceSettingsTab(
    settings: UserSettings,
    voiceEngineInfo: VoiceEngineInfo,
    onSetAmbientSoundscape: (AmbientSoundscape) -> Unit,
    onSetAmbientVolume: (Float) -> Unit,
    onToggleTransitionChimes: (Boolean) -> Unit,
    onOpenOnlineAudioPicker: () -> Unit,
    onSetVoiceMode: (VoiceMode) -> Unit,
    onSetAsanaPronunciationStyle: (AsanaPronunciationStyle) -> Unit,
    onToggleSpokenCues: (Boolean) -> Unit,
    onToggleCoachingCues: (Boolean) -> Unit,
    onToggleCountdown: (Boolean) -> Unit,
    onToggleHalfwayCue: (Boolean) -> Unit,
    onTestVoice: () -> Unit,
    onOpenTtsSettings: () -> Unit
) {
    // 1. Background Music Card
    SettingsSectionCard(
        title = "Background Music",
        subtitle = if (settings.ambientSoundscape == AmbientSoundscape.CUSTOM_TRACK && settings.customTrackTitle.isNotBlank())
            "Playing: ${settings.customTrackTitle}"
        else
            settings.ambientSoundscape.subtitle
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val available = AmbientSoundscape.values().filter { scape ->
                scape != AmbientSoundscape.CUSTOM_TRACK || settings.customTrackPath.isNotBlank()
            }
            available.forEach { scape ->
                val selected = settings.ambientSoundscape == scape
                val chipLabel = if (scape == AmbientSoundscape.CUSTOM_TRACK && settings.customTrackTitle.isNotBlank()) {
                    settings.customTrackTitle.take(14)
                } else {
                    scape.displayName
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSetAmbientSoundscape(scape) }
                        .testTag("ambient_btn_${scape.name}")
                ) {
                    Text(
                        text = chipLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (settings.ambientSoundscape != AmbientSoundscape.OFF) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Slider(
                    value = settings.ambientVolume,
                    onValueChange = onSetAmbientVolume,
                    valueRange = 0.05f..1f,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${(settings.ambientVolume * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OutlinedButton(
            onClick = onOpenOnlineAudioPicker,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("open_online_audio_picker_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text("Import or Download Music", style = MaterialTheme.typography.labelLarge)
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

        SettingToggleRow(
            title = "Transition Singing Bowl",
            subtitle = "Gentle chime between stretches",
            checked = settings.transitionChimesEnabled,
            enabled = true,
            onCheckedChange = onToggleTransitionChimes,
            tag = "toggle_transition_chimes"
        )
    }

    // 2. Voice Coach Card
    SettingsSectionCard(title = "Voice Coach") {
        SettingToggleRow(
            title = "Voice Guidance",
            subtitle = "Announces poses, holds & countdowns",
            checked = settings.spokenCuesEnabled,
            enabled = true,
            onCheckedChange = onToggleSpokenCues,
            tag = "toggle_spoken_cues"
        )

        if (settings.spokenCuesEnabled) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                VoiceMode.values().forEach { modeOption ->
                    val selected = settings.voiceMode == modeOption
                    val label = when (modeOption) {
                        VoiceMode.QUIET -> "Quiet"
                        VoiceMode.BALANCED -> "Balanced"
                        VoiceMode.COACH -> "Coach"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selected) MaterialTheme.colorScheme.secondary else Color.Transparent)
                            .clickable { onSetVoiceMode(modeOption) }
                            .padding(vertical = 8.dp)
                            .testTag("voice_mode_${modeOption.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            SettingToggleRow(
                title = "Pose Tips & Cues",
                subtitle = "Spoken posture alignment reminders",
                checked = settings.coachingCuesEnabled,
                enabled = settings.voiceMode != VoiceMode.QUIET,
                onCheckedChange = onToggleCoachingCues,
                tag = "toggle_coaching_cues"
            )
            SettingToggleRow(
                title = "Final 5s Countdown",
                subtitle = "Counts down 5, 4, 3, 2, 1 before switching",
                checked = settings.countdownAnnouncements,
                enabled = true,
                onCheckedChange = onToggleCountdown,
                tag = "toggle_countdown"
            )
            SettingToggleRow(
                title = "Halfway Reminder",
                subtitle = "Gentle 'Halfway' cue on longer holds",
                checked = settings.halfwayCue,
                enabled = true,
                onCheckedChange = onToggleHalfwayCue,
                tag = "toggle_halfway"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            Text(
                text = "Asana Name Style",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AsanaPronunciationStyle.values().forEach { styleOption ->
                    val selected = settings.asanaPronunciationStyle == styleOption
                    val label = when (styleOption) {
                        AsanaPronunciationStyle.ENGLISH_ONLY -> "English"
                        AsanaPronunciationStyle.SANSKRIT_ONLY -> "Sanskrit"
                        AsanaPronunciationStyle.BILINGUAL -> "Both"
                        AsanaPronunciationStyle.PHONETIC_SANSKRIT -> "Phonetic"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onSetAsanaPronunciationStyle(styleOption) }
                            .padding(vertical = 7.dp)
                            .testTag("asana_style_${styleOption.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTestVoice,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("test_voice_button"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.size(5.dp))
                    Text("Test Voice", style = MaterialTheme.typography.labelMedium)
                }
                TextButton(
                    onClick = onOpenTtsSettings,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Phone Voice", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun BackupSettingsTab(
    backupStatus: BackupStatus,
    onOpenBackupSafeguard: () -> Unit
) {
    SettingsSectionCard(
        title = "Your Custom Routines",
        subtitle = if (backupStatus.totalRoutines > 0)
            "${backupStatus.totalRoutines} custom routine(s) safely backed up"
        else
            "Automatic local safeguard is active"
    ) {
        Text(
            text = "Your custom routines and timings are saved safely on your device and can be exported or restored anytime.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = onOpenBackupSafeguard,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("manage_safeguard_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text("Backup, Export & Restore", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.testTag(tag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
