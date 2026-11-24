package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "stretch_coach_settings")

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

enum class VoiceMode {
    QUIET,
    BALANCED,
    COACH
}

enum class AsanaPronunciationStyle {
    BILINGUAL,          // e.g. "Bhujangasana, Cobra Pose"
    ENGLISH_ONLY,       // e.g. "Cobra Pose" (crystal clear on all devices)
    PHONETIC_SANSKRIT,  // e.g. "Bhoo-jung Aasan" (phonetically tuned for English TTS)
    SANSKRIT_ONLY       // e.g. "Bhujang asana"
}

enum class AmbientSoundscape(val displayName: String, val subtitle: String) {
    OFF("Off", "Silent session"),
    TANPURA_DRONE("Tanpura", "Warm yoga studio harmonic drone"),
    SOFT_RAIN("Rain", "Gentle rainfall outside the studio"),
    MORNING_BREEZE("Breeze", "Soft wind & distant chimes"),
    SINGING_BOWLS("Bowls", "Deep resonant Tibetan singing bowls"),
    CUSTOM_TRACK("My Music", "Downloaded or imported relaxation track")
}

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val voiceMode: VoiceMode = VoiceMode.BALANCED,
    val asanaPronunciationStyle: AsanaPronunciationStyle = AsanaPronunciationStyle.BILINGUAL,
    val spokenCuesEnabled: Boolean = true,
    val coachingCuesEnabled: Boolean = true,
    val countdownAnnouncements: Boolean = true,
    val halfwayCue: Boolean = true,
    val nextStretchCue: Boolean = true,
    val ambientSoundscape: AmbientSoundscape = AmbientSoundscape.TANPURA_DRONE,
    val ambientVolume: Float = 0.35f,
    val transitionChimesEnabled: Boolean = true,
    val customTrackPath: String = "",
    val customTrackTitle: String = ""
)

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("app_theme")
        val VOICE_MODE = stringPreferencesKey("voice_mode")
        val ASANA_PRONUNCIATION_STYLE = stringPreferencesKey("asana_pronunciation_style")
        val SPOKEN_CUES_ENABLED = booleanPreferencesKey("spoken_cues_enabled")
        val COACHING_CUES_ENABLED = booleanPreferencesKey("coaching_cues_enabled")
        val COUNTDOWN_ANNOUNCEMENTS = booleanPreferencesKey("countdown_announcements")
        val HALFWAY_CUE = booleanPreferencesKey("halfway_cue")
        val NEXT_STRETCH_CUE = booleanPreferencesKey("next_stretch_cue")
        val AMBIENT_SOUNDSCAPE = stringPreferencesKey("ambient_soundscape")
        val AMBIENT_VOLUME = floatPreferencesKey("ambient_volume")
        val TRANSITION_CHIMES_ENABLED = booleanPreferencesKey("transition_chimes_enabled")
        val CUSTOM_TRACK_PATH = stringPreferencesKey("custom_track_path")
        val CUSTOM_TRACK_TITLE = stringPreferencesKey("custom_track_title")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeString = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
        val theme = runCatching { AppTheme.valueOf(themeString) }.getOrDefault(AppTheme.SYSTEM)

        val voiceModeString = preferences[PreferencesKeys.VOICE_MODE] ?: VoiceMode.BALANCED.name
        val voiceMode = runCatching { VoiceMode.valueOf(voiceModeString) }.getOrDefault(VoiceMode.BALANCED)

        val asanaStyleString = preferences[PreferencesKeys.ASANA_PRONUNCIATION_STYLE] ?: AsanaPronunciationStyle.BILINGUAL.name
        val asanaStyle = runCatching { AsanaPronunciationStyle.valueOf(asanaStyleString) }.getOrDefault(AsanaPronunciationStyle.BILINGUAL)

        val spokenCuesEnabled = preferences[PreferencesKeys.SPOKEN_CUES_ENABLED] ?: true
        val coachingCuesEnabled = preferences[PreferencesKeys.COACHING_CUES_ENABLED] ?: true
        val countdownAnnouncements = preferences[PreferencesKeys.COUNTDOWN_ANNOUNCEMENTS] ?: true
        val halfwayCue = preferences[PreferencesKeys.HALFWAY_CUE] ?: true
        val nextStretchCue = preferences[PreferencesKeys.NEXT_STRETCH_CUE] ?: true

        val soundscapeString = preferences[PreferencesKeys.AMBIENT_SOUNDSCAPE] ?: AmbientSoundscape.TANPURA_DRONE.name
        val ambientSoundscape = runCatching { AmbientSoundscape.valueOf(soundscapeString) }
            .getOrDefault(AmbientSoundscape.TANPURA_DRONE)
        val ambientVolume = (preferences[PreferencesKeys.AMBIENT_VOLUME] ?: 0.35f).coerceIn(0f, 1f)
        val transitionChimesEnabled = preferences[PreferencesKeys.TRANSITION_CHIMES_ENABLED] ?: true
        val customTrackPath = preferences[PreferencesKeys.CUSTOM_TRACK_PATH] ?: ""
        val customTrackTitle = preferences[PreferencesKeys.CUSTOM_TRACK_TITLE] ?: ""

        UserSettings(
            theme = theme,
            voiceMode = voiceMode,
            asanaPronunciationStyle = asanaStyle,
            spokenCuesEnabled = spokenCuesEnabled,
            coachingCuesEnabled = coachingCuesEnabled,
            countdownAnnouncements = countdownAnnouncements,
            halfwayCue = halfwayCue,
            nextStretchCue = nextStretchCue,
            ambientSoundscape = ambientSoundscape,
            ambientVolume = ambientVolume,
            transitionChimesEnabled = transitionChimesEnabled,
            customTrackPath = customTrackPath,
            customTrackTitle = customTrackTitle
        )
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setVoiceMode(voiceMode: VoiceMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOICE_MODE] = voiceMode.name
        }
    }

    suspend fun setAsanaPronunciationStyle(style: AsanaPronunciationStyle) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ASANA_PRONUNCIATION_STYLE] = style.name
        }
    }

    suspend fun setSpokenCuesEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SPOKEN_CUES_ENABLED] = enabled
        }
    }

    suspend fun setCoachingCuesEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COACHING_CUES_ENABLED] = enabled
        }
    }

    suspend fun setCountdownAnnouncements(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNTDOWN_ANNOUNCEMENTS] = enabled
        }
    }

    suspend fun setHalfwayCue(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HALFWAY_CUE] = enabled
        }
    }

    suspend fun setNextStretchCue(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NEXT_STRETCH_CUE] = enabled
        }
    }

    suspend fun setAmbientSoundscape(soundscape: AmbientSoundscape) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AMBIENT_SOUNDSCAPE] = soundscape.name
        }
    }

    suspend fun setAmbientVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AMBIENT_VOLUME] = volume.coerceIn(0f, 1f)
        }
    }

    suspend fun setTransitionChimesEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TRANSITION_CHIMES_ENABLED] = enabled
        }
    }

    suspend fun setCustomTrack(path: String, title: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_TRACK_PATH] = path
            preferences[PreferencesKeys.CUSTOM_TRACK_TITLE] = title
            if (path.isNotBlank()) {
                preferences[PreferencesKeys.AMBIENT_SOUNDSCAPE] = AmbientSoundscape.CUSTOM_TRACK.name
            }
        }
    }
}
