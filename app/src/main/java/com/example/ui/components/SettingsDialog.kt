package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechController
import com.example.audio.VoiceEngineInfo
import com.example.data.AmbientSoundscape
import com.example.data.AppTheme
import com.example.data.AsanaPronunciationStyle
import com.example.data.BackupStatus
import com.example.data.StudioAromaProp
import com.example.data.StudioLightingMode
import com.example.data.UserSettings
import com.example.data.VoiceMode
import com.example.ui.theme.CyanPrep
import com.example.ui.theme.TealPrimaryDark
import com.example.ui.theme.TealPrimaryLight

import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.SyncState
import com.google.firebase.auth.FirebaseUser

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
    onToggleCompanionDog: (Boolean) -> Unit = {},
    onToggleSwayingLeaves: (Boolean) -> Unit = {},
    onToggleSunbeams: (Boolean) -> Unit = {},
    onToggleHubTransitions: (Boolean) -> Unit = {},
    onTestVoice: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Preferences & Voice",
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
                // Theme Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppTheme.values().forEach { themeOption ->
                            val isSelected = settings.theme == themeOption
                            val label = when (themeOption) {
                                AppTheme.SYSTEM -> "System"
                                AppTheme.LIGHT -> "Light"
                                AppTheme.DARK -> "Dark"
                            }
                            val icon = when (themeOption) {
                                AppTheme.SYSTEM -> Icons.Default.BrightnessAuto
                                AppTheme.LIGHT -> Icons.Default.LightMode
                                AppTheme.DARK -> Icons.Default.DarkMode
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent
                                    )
                                    .clickable { onSetTheme(themeOption) }
                                    .padding(vertical = 8.dp)
                                    .testTag("theme_btn_${themeOption.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Studio Background Audio & Singing Bowl Chimes
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (settings.ambientSoundscape == AmbientSoundscape.OFF) {
                                Icons.Default.MusicOff
                            } else {
                                Icons.Default.MusicNote
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Studio Background Audio",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AmbientSoundscape.values().forEach { soundscape ->
                            val isSelected = settings.ambientSoundscape == soundscape
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSetAmbientSoundscape(soundscape) }
                                    .testTag("ambient_btn_${soundscape.name}")
                            ) {
                                Text(
                                    text = soundscape.displayName,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = if (settings.ambientSoundscape == AmbientSoundscape.CUSTOM_TRACK && settings.customTrackTitle.isNotBlank()) {
                            "Playing: ${settings.customTrackTitle} • Auto-ducks when voice coach speaks"
                        } else {
                            "${settings.ambientSoundscape.subtitle} • Auto-ducks when voice coach speaks"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = onOpenOnlineAudioPicker,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = if (settings.customTrackTitle.isNotBlank()) {
                                "Browse / Change Downloaded Music"
                            } else {
                                "Browse & Download Free Indian Music"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    if (settings.ambientSoundscape != AmbientSoundscape.OFF) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ambient Volume",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${(settings.ambientVolume * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = settings.ambientVolume,
                                onValueChange = onSetAmbientVolume,
                                valueRange = 0.05f..1f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ambient_volume_slider")
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Singing Bowl Chimes",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Soft bell on hold start & completion",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.transitionChimesEnabled,
                            onCheckedChange = onToggleTransitionChimes,
                            modifier = Modifier.testTag("switch_transition_chimes")
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Studio Atmosphere, Lighting & Companion Dog
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Studio Atmosphere & Buddy",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Studio Lighting",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StudioLightingMode.values().forEach { mode ->
                            val isSelected = settings.studioLightingMode == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSetStudioLightingMode(mode) }
                                    .testTag("lighting_btn_${mode.name}")
                            ) {
                                Text(
                                    text = mode.displayName,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "Background Aroma Prop",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StudioAromaProp.values().forEach { prop ->
                            val isSelected = settings.studioAromaProp == prop
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSetStudioAromaProp(prop) }
                                    .testTag("aroma_btn_${prop.name}")
                            ) {
                                Text(
                                    text = prop.displayName,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    SettingToggleRow(
                        title = "Companion Yoga Dog",
                        subtitle = "Cozy buddy stretches on transitions & naps at night",
                        checked = settings.companionDogEnabled,
                        enabled = true,
                        onCheckedChange = onToggleCompanionDog,
                        tag = "toggle_companion_dog"
                    )
                    SettingToggleRow(
                        title = "Swaying Corner Leaves",
                        subtitle = "Subtle Ghibli studio foliage in top-left corner",
                        checked = settings.swayingLeavesEnabled,
                        enabled = true,
                        onCheckedChange = onToggleSwayingLeaves,
                        tag = "toggle_swaying_leaves"
                    )
                    SettingToggleRow(
                        title = "Sunbeams & Dust Motes",
                        subtitle = "Soft glowing light rays floating across studio",
                        checked = settings.sunbeamsEnabled,
                        enabled = true,
                        onCheckedChange = onToggleSunbeams,
                        tag = "toggle_sunbeams"
                    )
                    SettingToggleRow(
                        title = "Posture Transitions (Prep Only)",
                        subtitle = "Show transition clips when switching standing/floor",
                        checked = settings.hubTransitionsEnabled,
                        enabled = true,
                        onCheckedChange = onToggleHubTransitions,
                        tag = "toggle_hub_transitions"
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Voice Mode Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Voice Coach Mode",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        VoiceMode.values().forEach { modeOption ->
                            val isSelected = settings.voiceMode == modeOption
                            val label = when (modeOption) {
                                VoiceMode.QUIET -> "Quiet"
                                VoiceMode.BALANCED -> "Balanced"
                                VoiceMode.COACH -> "Coach"
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondary else androidx.compose.ui.graphics.Color.Transparent
                                    )
                                    .clickable { onSetVoiceMode(modeOption) }
                                    .padding(vertical = 8.dp)
                                    .testTag("voice_mode_${modeOption.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    // Voice Mode Explanation Box
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            val description = when (settings.voiceMode) {
                                VoiceMode.QUIET -> "Quiet: Major phase cues only. Countdown only for final 5 seconds. Minimal talk."
                                VoiceMode.BALANCED -> "Balanced: Countdown at final 5s and every 20s. Standard instructions."
                                VoiceMode.COACH -> "Coach: Rich voice guidance, setup safety reminders, and countdown every 15s."
                            }
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Indian English & Sanskrit Yoga Pronunciation Engine
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Asana Voice & Sanskrit Pronunciation",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Engine Status & Badge
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (voiceEngineInfo.hasSanskritDevanagariVoice) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (voiceEngineInfo.hasSanskritDevanagariVoice) Color(0xFF2E7D32) else Color(0xFFE65100),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (voiceEngineInfo.hasSanskritDevanagariVoice) {
                                        "Sanskrit Voice: Devanagari Active (${voiceEngineInfo.sanskritVoiceName.ifBlank { "hi-IN" }})"
                                    } else {
                                        "Sanskrit Voice: Phonetic Fallback (Install Hindi voice for Devanagari)"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (voiceEngineInfo.hasSanskritDevanagariVoice) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            }
                            Text(
                                text = "English Coach Voice: ${voiceEngineInfo.voiceName} (${voiceEngineInfo.localeTag})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!voiceEngineInfo.hasSanskritDevanagariVoice) {
                            Text(
                                text = "Install Hindi (India) in TTS Settings for native Devanagari Sanskrit.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Compact Voice Settings Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { SpeechController.launchInstallTtsData(context) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("install_voice_data_button"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Download, null, modifier = Modifier.size(14.dp))
                                    Text("Install Voice", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            OutlinedButton(
                                onClick = { SpeechController.launchTtsSettings(context) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("open_tts_settings_button"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Settings, null, modifier = Modifier.size(14.dp))
                                    Text("TTS Settings", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        // Pronunciation Style Selector
                        Text(
                            text = "Asana Name Pronunciation:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val styles = listOf(
                                AsanaPronunciationStyle.SANSKRIT_ONLY to "Indian / Sanskrit (भुजंगासन)",
                                AsanaPronunciationStyle.ENGLISH_ONLY to "English (Cobra Pose)",
                                AsanaPronunciationStyle.BILINGUAL to "Both (भुजंगासन + Cobra Pose)",
                                AsanaPronunciationStyle.PHONETIC_SANSKRIT to "Phonetic (Bhoo-jung-ah-suh-nuh)"
                            )

                            styles.forEach { (style, label) ->
                                val isSelected = settings.asanaPronunciationStyle == style
                                Surface(
                                    onClick = { onSetAsanaPronunciationStyle(style) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("asana_style_${style.name}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onSetAsanaPronunciationStyle(style) },
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Test Pronunciation Button
                        Button(
                            onClick = { onTestVoice() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("test_voice_pronunciation_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Test Pronunciation (${when (settings.asanaPronunciationStyle) {
                                        AsanaPronunciationStyle.BILINGUAL -> "Bhujangasana, Cobra"
                                        AsanaPronunciationStyle.ENGLISH_ONLY -> "Cobra Pose"
                                        AsanaPronunciationStyle.PHONETIC_SANSKRIT -> "Bhoo-jung-ah-suh-nuh"
                                        AsanaPronunciationStyle.SANSKRIT_ONLY -> "भुजंगासन"
                                    }})",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Spoken Cues Toggles
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Audio Cues",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Spoken cues master
                    SettingToggleRow(
                        title = "Spoken Voice Cues",
                        subtitle = "Enable Android Text-to-Speech coaching",
                        checked = settings.spokenCuesEnabled,
                        onCheckedChange = onToggleSpokenCues,
                        tag = "toggle_spoken_cues"
                    )

                    // Coaching Cues Form & Posture Guidance
                    SettingToggleRow(
                        title = "Coaching & Posture Guidance",
                        subtitle = "Speak anatomical alignment cues and breathing tips",
                        checked = settings.coachingCuesEnabled && settings.spokenCuesEnabled,
                        enabled = settings.spokenCuesEnabled,
                        onCheckedChange = onToggleCoachingCues,
                        tag = "toggle_coaching_cues"
                    )

                    // Countdown announcements
                    SettingToggleRow(
                        title = "Countdown Announcements",
                        subtitle = "Speak remaining seconds during hold and prep",
                        checked = settings.countdownAnnouncements && settings.spokenCuesEnabled,
                        enabled = settings.spokenCuesEnabled,
                        onCheckedChange = onToggleCountdown,
                        tag = "toggle_countdown"
                    )

                    // Halfway Cue
                    SettingToggleRow(
                        title = "Halfway Announcement",
                        subtitle = "Say \"Halfway\" when hold timer reaches 50%",
                        checked = settings.halfwayCue && settings.spokenCuesEnabled,
                        enabled = settings.spokenCuesEnabled,
                        onCheckedChange = onToggleHalfwayCue,
                        tag = "toggle_halfway"
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Custom Routines Safeguard Card
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Local Backup & Offline Export",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (backupStatus.totalRoutines > 0)
                                        "${backupStatus.totalRoutines} Custom Routine(s) Safeguarded"
                                    else
                                        "Auto-Safeguard Active",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Your custom routines are continuously mirrored to persistent storage and included in Android device backup. They survive APK updates automatically.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = onOpenBackupSafeguard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("manage_safeguard_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Backup, Export & Restore")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("close_settings_button")
            ) {
                Text("Done", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
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
