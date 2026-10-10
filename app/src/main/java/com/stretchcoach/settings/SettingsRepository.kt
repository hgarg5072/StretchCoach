package com.stretchcoach.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("stretch_coach_settings")

enum class DarkModePreference {
    System,
    Light,
    Dark
}

enum class VoiceMode {
    Quiet,
    Balanced,
    Coach
}

data class AppSettings(
    val darkModePreference: DarkModePreference = DarkModePreference.System,
    val spokenCuesEnabled: Boolean = true,
    val announceCountdown: Boolean = true,
    val halfwayCueEnabled: Boolean = true,
    val nextUpCueEnabled: Boolean = true,
    val voiceMode: VoiceMode = VoiceMode.Coach
)

class SettingsRepository(private val context: Context) {
    private val darkModeKey = stringPreferencesKey("dark_mode")
    private val voiceModeKey = stringPreferencesKey("voice_mode")
    private val spokenCuesKey = booleanPreferencesKey("spoken_cues")
    private val announceCountdownKey = booleanPreferencesKey("announce_countdown")
    private val halfwayCueKey = booleanPreferencesKey("halfway_cue")
    private val nextUpCueKey = booleanPreferencesKey("next_up_cue")

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            darkModePreference = preferences[darkModeKey]
                ?.let { runCatching { DarkModePreference.valueOf(it) }.getOrNull() }
                ?: DarkModePreference.System,
            voiceMode = preferences[voiceModeKey]
                ?.let { runCatching { VoiceMode.valueOf(it) }.getOrNull() }
                ?: VoiceMode.Coach,
            spokenCuesEnabled = preferences[spokenCuesKey] ?: true,
            announceCountdown = preferences[announceCountdownKey] ?: true,
            halfwayCueEnabled = preferences[halfwayCueKey] ?: true,
            nextUpCueEnabled = preferences[nextUpCueKey] ?: true
        )
    }

    suspend fun setDarkModePreference(value: DarkModePreference) {
        context.settingsDataStore.edit { it[darkModeKey] = value.name }
    }

    suspend fun setSpokenCuesEnabled(value: Boolean) {
        context.settingsDataStore.edit { it[spokenCuesKey] = value }
    }

    suspend fun setVoiceMode(value: VoiceMode) {
        context.settingsDataStore.edit { it[voiceModeKey] = value.name }
    }

    suspend fun setAnnounceCountdown(value: Boolean) {
        context.settingsDataStore.edit { it[announceCountdownKey] = value }
    }

    suspend fun setHalfwayCue(value: Boolean) {
        context.settingsDataStore.edit { it[halfwayCueKey] = value }
    }

    suspend fun setNextUpCue(value: Boolean) {
        context.settingsDataStore.edit { it[nextUpCueKey] = value }
    }
}
