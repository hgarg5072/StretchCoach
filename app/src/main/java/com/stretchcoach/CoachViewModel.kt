package com.stretchcoach

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stretchcoach.data.StretchDatabase
import com.stretchcoach.data.StretchEntity
import com.stretchcoach.data.StretchRepository
import com.stretchcoach.settings.AppSettings
import com.stretchcoach.settings.DarkModePreference
import com.stretchcoach.settings.SettingsRepository
import com.stretchcoach.settings.VoiceMode
import com.stretchcoach.tts.AndroidSpeechController
import com.stretchcoach.tts.SpeechController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlayerPhase {
    Idle,
    Preparing,
    Holding,
    Complete
}

data class PlayerState(
    val phase: PlayerPhase = PlayerPhase.Idle,
    val currentIndex: Int = -1,
    val routineSize: Int = 0,
    val secondsRemaining: Int = 0,
    val totalSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isRunning: Boolean = false,
    val message: String = "Ready",
    val currentStretch: StretchEntity? = null,
    val nextStretch: StretchEntity? = null
)

class CoachViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StretchRepository(
        StretchDatabase.getInstance(application).stretchDao()
    )
    private val settingsRepository = SettingsRepository(application)
    private val speech: SpeechController = AndroidSpeechController(application)
    private var playerJob: Job? = null

    val stretches: StateFlow<List<StretchEntity>> = repository.stretches.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val settings: StateFlow<AppSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppSettings()
    )

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

    init {
        viewModelScope.launch {
            repository.seedDefaultsIfEmpty()
        }
    }

    fun saveStretch(
        existing: StretchEntity?,
        name: String,
        preparationSeconds: Int,
        holdSeconds: Int,
        notes: String
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        viewModelScope.launch {
            val nextOrder = stretches.value.maxOfOrNull { it.sortOrder }?.plus(1) ?: 0
            repository.save(
                StretchEntity(
                    id = existing?.id ?: 0,
                    name = trimmedName,
                    preparationSeconds = preparationSeconds.coerceAtLeast(0),
                    holdSeconds = holdSeconds.coerceAtLeast(1),
                    notes = notes.trim().ifBlank { null },
                    enabled = existing?.enabled ?: true,
                    sortOrder = existing?.sortOrder ?: nextOrder
                )
            )
        }
    }

    fun deleteStretch(stretch: StretchEntity) {
        viewModelScope.launch {
            repository.delete(stretch)
            repository.reorder(stretches.value.filterNot { it.id == stretch.id }.map { it.id })
        }
    }

    fun setStretchEnabled(stretch: StretchEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.setEnabled(stretch.id, enabled)
        }
    }

    fun reorderStretches(ids: List<Long>) {
        viewModelScope.launch {
            repository.reorder(ids)
        }
    }

    fun startPlayer() {
        launchSession(startIndex = 0)
    }

    fun pausePlayer() {
        _playerState.update { state ->
            if (state.isRunning) state.copy(isPaused = true, message = "Paused") else state
        }
        speech.stop()
    }

    fun resumePlayer() {
        _playerState.update { state ->
            if (state.isRunning) state.copy(isPaused = false, message = "Resuming") else state
        }
        if (settings.value.spokenCuesEnabled) speech.speak("Resume")
    }

    fun skipStretch() {
        val next = (_playerState.value.currentIndex + 1).coerceAtLeast(0)
        launchSession(next)
    }

    fun previousStretch() {
        val previous = (_playerState.value.currentIndex - 1).coerceAtLeast(0)
        launchSession(previous)
    }

    fun restartStretch() {
        val current = _playerState.value.currentIndex.coerceAtLeast(0)
        launchSession(current)
    }

    fun stopPlayer() {
        playerJob?.cancel()
        playerJob = null
        speech.stop()
        _playerState.value = PlayerState()
    }

    fun setDarkModePreference(value: DarkModePreference) {
        viewModelScope.launch { settingsRepository.setDarkModePreference(value) }
    }

    fun setSpokenCuesEnabled(value: Boolean) {
        viewModelScope.launch { settingsRepository.setSpokenCuesEnabled(value) }
    }

    fun setVoiceMode(value: VoiceMode) {
        viewModelScope.launch { settingsRepository.setVoiceMode(value) }
    }

    fun setAnnounceCountdown(value: Boolean) {
        viewModelScope.launch { settingsRepository.setAnnounceCountdown(value) }
    }

    fun setHalfwayCueEnabled(value: Boolean) {
        viewModelScope.launch { settingsRepository.setHalfwayCue(value) }
    }

    fun setNextUpCueEnabled(value: Boolean) {
        viewModelScope.launch { settingsRepository.setNextUpCue(value) }
    }

    private fun launchSession(startIndex: Int) {
        playerJob?.cancel()
        speech.stop()
        playerJob = viewModelScope.launch {
            val routine = stretches.value.filter { it.enabled }
            if (routine.isEmpty()) {
                _playerState.value = PlayerState(message = "Enable at least one stretch")
                return@launch
            }
            var index = startIndex.coerceIn(0, routine.lastIndex)
            while (index in routine.indices) {
                val stretch = routine[index]
                val nextStretch = routine.getOrNull(index + 1)
                if (shouldSpeak()) {
                    speech.speak("Prepare for ${stretch.name}")
                    if (settings.value.voiceMode == VoiceMode.Coach) {
                        speech.speak("Set up safely. Move into position without forcing the stretch.")
                    }
                    speech.speak("Preparing for ${stretch.name}")
                }
                runCountdown(
                    phase = PlayerPhase.Preparing,
                    stretch = stretch,
                    nextStretch = nextStretch,
                    index = index,
                    routineSize = routine.size,
                    total = stretch.preparationSeconds,
                    prefix = "Prepare"
                )
                if (shouldSpeak()) {
                    speech.speak("Hold ${stretch.name}")
                    if (settings.value.voiceMode != VoiceMode.Quiet) {
                        speech.speak(holdInstruction(stretch))
                    }
                }
                runCountdown(
                    phase = PlayerPhase.Holding,
                    stretch = stretch,
                    nextStretch = nextStretch,
                    index = index,
                    routineSize = routine.size,
                    total = stretch.holdSeconds,
                    prefix = "Hold"
                )
                if (shouldSpeak() && settings.value.nextUpCueEnabled && nextStretch != null) {
                    speech.speak("Next up, ${nextStretch.name}")
                }
                index += 1
            }
            _playerState.value = PlayerState(
                phase = PlayerPhase.Complete,
                isRunning = false,
                message = "Routine complete"
            )
            if (settings.value.spokenCuesEnabled) speech.speak("Routine complete")
        }
    }

    private fun holdInstruction(stretch: StretchEntity): String =
        stretch.notes?.takeIf { it.isNotBlank() } ?: "Hold gently, breathe slowly, and ease off if anything feels sharp."

    private suspend fun runCountdown(
        phase: PlayerPhase,
        stretch: StretchEntity,
        nextStretch: StretchEntity?,
        index: Int,
        routineSize: Int,
        total: Int,
        prefix: String
    ) {
        var remaining = total
        if (remaining <= 0) return
        var halfwaySpoken = false
        while (remaining > 0) {
            awaitResume()
            _playerState.value = PlayerState(
                phase = phase,
                currentIndex = index,
                routineSize = routineSize,
                secondsRemaining = remaining,
                totalSeconds = total,
                isPaused = false,
                isRunning = true,
                message = "$prefix: ${stretch.name}",
                currentStretch = stretch,
                nextStretch = nextStretch
            )
            if (!halfwaySpoken && phase == PlayerPhase.Holding && settings.value.halfwayCueEnabled && remaining == total / 2) {
                if (shouldSpeak()) speech.speak("Halfway")
                halfwaySpoken = true
            }
            maybeSpeakCountdown(phase, remaining)
            delay(1_000)
            remaining -= 1
        }
    }

    private suspend fun awaitResume() {
        while (_playerState.value.isPaused) {
            delay(200)
        }
    }

    private fun maybeSpeakCountdown(phase: PlayerPhase, remaining: Int) {
        val currentSettings = settings.value
        if (!shouldSpeak() || !currentSettings.announceCountdown) return
        val shouldSpeak = when (currentSettings.voiceMode) {
            VoiceMode.Quiet -> remaining <= 3
            VoiceMode.Balanced -> remaining <= 5 || remaining % 20 == 0
            VoiceMode.Coach -> remaining <= 5 || remaining % 15 == 0
        }
        if (!shouldSpeak) return
        speech.speak(remaining.toString())
    }

    private fun shouldSpeak(): Boolean =
        settings.value.spokenCuesEnabled

    override fun onCleared() {
        speech.shutdown()
        super.onCleared()
    }
}
