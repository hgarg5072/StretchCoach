package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechController
import com.example.data.AppDatabase
import com.example.data.AppTheme
import com.example.data.LibraryStretch
import com.example.data.LibraryStretchEntity
import com.example.data.RoutineEntity
import com.example.data.SettingsRepository
import com.example.data.StretchEntity
import com.example.data.StretchRepository
import com.example.data.UserSettings
import com.example.data.VoiceMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlayerState {
    IDLE,
    PREPARING,
    HOLDING,
    PAUSED,
    COMPLETE
}

data class PlayerUiState(
    val state: PlayerState = PlayerState.IDLE,
    val previousStateBeforePause: PlayerState = PlayerState.PREPARING,
    val routineName: String = "",
    val activeRoutine: List<StretchEntity> = emptyList(),
    val currentStretchIndex: Int = 0,
    val remainingSeconds: Int = 0,
    val totalSegmentSeconds: Int = 0,
    val activeTransitionVideoPath: String? = null,
    val activeTransitionCue: String? = null
) {
    val currentStretch: StretchEntity?
        get() = activeRoutine.getOrNull(currentStretchIndex)

    val nextStretch: StretchEntity?
        get() = activeRoutine.getOrNull(currentStretchIndex + 1)

    val progress: Float
        get() = if (totalSegmentSeconds > 0) {
            ((totalSegmentSeconds - remainingSeconds).toFloat() / totalSegmentSeconds).coerceIn(0f, 1f)
        } else 0f

    val totalStretches: Int
        get() = activeRoutine.size

    val isRunning: Boolean
        get() = state == PlayerState.PREPARING || state == PlayerState.HOLDING || state == PlayerState.PAUSED
}

@OptIn(ExperimentalCoroutinesApi::class)
class StretchViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val firestoreRepo = com.example.data.FirestoreRoutineRepository(application)
    val communityRepo = com.example.data.FirestoreCommunityRepository(application)
    val syncManager = com.example.data.FirestoreSyncManager(database.stretchDao(), firestoreRepo)
    val authManager = com.example.data.AuthManager()

    private val stretchRepository = StretchRepository(
        stretchDao = database.stretchDao(),
        context = application,
        syncManager = syncManager
    )
    private val settingsRepository = SettingsRepository(application)
    val speechController = SpeechController(application)
    val ambientAudioController = com.example.audio.AmbientAudioController()
    val voiceEngineInfo = speechController.voiceEngineInfo

    val currentUser = authManager.currentUser
    val syncState = syncManager.syncState
    val authLoading = authManager.isLoading
    val authError = authManager.authError

    private val _backupStatus = MutableStateFlow(com.example.data.BackupStatus())
    val backupStatus: StateFlow<com.example.data.BackupStatus> = _backupStatus.asStateFlow()

    // --- Free Online & Local Relaxation Audio Library ---
    private val _downloadedTracks = MutableStateFlow<List<com.example.audio.DownloadedAudioTrack>>(emptyList())
    val downloadedTracks: StateFlow<List<com.example.audio.DownloadedAudioTrack>> = _downloadedTracks.asStateFlow()

    private val _onlineAudioResults = MutableStateFlow<List<com.example.audio.OnlineAudioTrack>>(emptyList())
    val onlineAudioResults: StateFlow<List<com.example.audio.OnlineAudioTrack>> = _onlineAudioResults.asStateFlow()

    private val _isSearchingOnlineAudio = MutableStateFlow(false)
    val isSearchingOnlineAudio: StateFlow<Boolean> = _isSearchingOnlineAudio.asStateFlow()

    private val _downloadingTrackId = MutableStateFlow<String?>(null)
    val downloadingTrackId: StateFlow<String?> = _downloadingTrackId.asStateFlow()

    private val _onlineAudioMessage = MutableStateFlow<String?>(null)
    val onlineAudioMessage: StateFlow<String?> = _onlineAudioMessage.asStateFlow()

    // --- Community Feed & Creator Flows ---
    val publicRoutines: StateFlow<List<com.example.data.FirestorePublicRoutine>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                communityRepo.observePublicRoutines()
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val followedCreators: StateFlow<Set<String>> = currentUser
        .flatMapLatest { user ->
            if (user != null) {
                communityRepo.observeFollowedCreators()
            } else {
                flowOf(emptySet())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    private val _communityCategoryFilter = MutableStateFlow("All")
    val communityCategoryFilter: StateFlow<String> = _communityCategoryFilter.asStateFlow()

    private val _communitySearchQuery = MutableStateFlow("")
    val communitySearchQuery: StateFlow<String> = _communitySearchQuery.asStateFlow()

    val filteredCommunityRoutines: StateFlow<List<com.example.data.FirestorePublicRoutine>> = combine(
        publicRoutines,
        followedCreators,
        _communityCategoryFilter,
        _communitySearchQuery
    ) { routines, follows, filter, query ->
        routines.filter { routine ->
            val matchesCategory = when (filter) {
                "All" -> true
                "Followed" -> follows.contains(routine.authorId)
                else -> routine.category.equals(filter, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                routine.name.contains(query, ignoreCase = true) ||
                routine.description.contains(query, ignoreCase = true) ||
                routine.authorName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _communityActionMessage = MutableStateFlow<String?>(null)
    val communityActionMessage: StateFlow<String?> = _communityActionMessage.asStateFlow()

    fun setCommunityCategoryFilter(filter: String) {
        _communityCategoryFilter.value = filter
    }

    fun setCommunitySearchQuery(query: String) {
        _communitySearchQuery.value = query
    }

    fun clearCommunityActionMessage() {
        _communityActionMessage.value = null
    }

    val allRoutines: StateFlow<List<RoutineEntity>> = stretchRepository.allRoutines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedRoutineId = MutableStateFlow(1L)
    val selectedRoutineId: StateFlow<Long> = _selectedRoutineId.asStateFlow()

    val currentRoutineStretches: StateFlow<List<StretchEntity>> = _selectedRoutineId
        .flatMapLatest { routineId ->
            stretchRepository.getStretchesForRoutine(routineId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentRoutineEnabledStretches: StateFlow<List<StretchEntity>> = _selectedRoutineId
        .flatMapLatest { routineId ->
            stretchRepository.getEnabledStretchesForRoutine(routineId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val libraryStretches: StateFlow<List<LibraryStretchEntity>> = stretchRepository.allLibraryStretches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userSettings: StateFlow<UserSettings> = settingsRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState: StateFlow<PlayerUiState> = _playerUiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Routine, 1: Player
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private var timerJob: Job? = null
    private var pendingTransitionRoute: List<com.example.data.HubTransitionSpec> = emptyList()

    init {
        speechController.onSpeakingStateChanged = { isSpeaking ->
            ambientAudioController.setDucked(isSpeaking)
        }
        viewModelScope.launch {
            stretchRepository.checkAndSeedDefaults()
            refreshBackupStatus()
            refreshDownloadedTracks()
        }
        viewModelScope.launch {
            userSettings.collect { settings ->
                val state = _playerUiState.value.state
                val isActivelyPlaying = state == PlayerState.PREPARING || state == PlayerState.HOLDING
                ambientAudioController.updateSettings(
                    soundscape = settings.ambientSoundscape,
                    volume = settings.ambientVolume,
                    isSessionActive = isActivelyPlaying,
                    customFilePath = settings.customTrackPath
                )
            }
        }
    }

    fun attemptAutoSignIn(activity: android.app.Activity) {
        authManager.attemptAutoSignIn(activity, viewModelScope) {
            viewModelScope.launch {
                val restored = syncManager.syncOnSignIn()
                if (restored > 0) {
                    refreshBackupStatus()
                }
            }
        }
    }

    fun refreshVoiceEngine() {
        speechController.refreshVoices()
    }

    fun signInWithGoogle(activity: android.app.Activity, onDone: () -> Unit = {}) {
        authManager.signInWithGoogle(activity, viewModelScope) {
            viewModelScope.launch {
                syncManager.syncOnSignIn()
                refreshBackupStatus()
                onDone()
            }
        }
    }

    fun signOut(context: android.content.Context, onDone: () -> Unit = {}) {
        authManager.signOut(context, viewModelScope) {
            onDone()
        }
    }

    fun triggerCloudSync(onDone: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val count = syncManager.syncOnSignIn()
            refreshBackupStatus()
            onDone(count)
        }
    }

    fun clearAuthError() {
        authManager.clearError()
    }

    fun refreshBackupStatus() {
        _backupStatus.value = stretchRepository.getBackupStatus()
    }

    fun exportCustomRoutines(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = stretchRepository.exportCustomRoutinesJson()
            refreshBackupStatus()
            onResult(json)
        }
    }

    fun importCustomRoutines(json: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = stretchRepository.importCustomRoutinesJson(json)
            refreshBackupStatus()
            onResult(count)
        }
    }

    fun restoreFromAutoBackup(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = stretchRepository.restoreFromAutoBackup()
            refreshBackupStatus()
            onResult(count)
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun selectRoutine(routineId: Long) {
        _selectedRoutineId.value = routineId
    }

    // --- Routine Management ---

    fun createCustomRoutine(name: String, description: String = "", initialStretchIds: List<String> = emptyList()) {
        viewModelScope.launch {
            val newId = stretchRepository.createRoutine(name, description, initialStretchIds)
            _selectedRoutineId.value = newId
        }
    }

    fun duplicateRoutine(sourceRoutineId: Long, newName: String, newDescription: String = "") {
        viewModelScope.launch {
            val newId = stretchRepository.duplicateRoutine(sourceRoutineId, newName, newDescription)
            _selectedRoutineId.value = newId
        }
    }

    fun renameRoutine(routineId: Long, newName: String, newDescription: String) {
        viewModelScope.launch {
            stretchRepository.renameRoutine(routineId, newName, newDescription)
        }
    }

    fun deleteCustomRoutine(routineId: Long) {
        viewModelScope.launch {
            stretchRepository.deleteRoutine(routineId)
            // Switch to the first available routine
            val routines = allRoutines.value.filter { it.id != routineId }
            if (routines.isNotEmpty()) {
                _selectedRoutineId.value = routines.first().id
            } else {
                _selectedRoutineId.value = 1L
            }
        }
    }

    // --- Community Sharing & Creator Following Actions ---

    fun publishRoutineToCommunity(
        routineId: Long,
        category: String,
        customDescription: String = "",
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                val routine = allRoutines.value.find { it.id == routineId }
                    ?: throw IllegalArgumentException("Routine not found")
                val stretches = stretchRepository.getStretchesForRoutineSync(routineId)
                if (stretches.isEmpty()) {
                    val msg = "Cannot publish an empty routine. Add stretches first!"
                    _communityActionMessage.value = msg
                    onComplete(false, msg)
                    return@launch
                }
                val firestoreStretches = stretches.map { s ->
                    com.example.data.FirestoreStretchItem(
                        name = s.name,
                        category = s.category,
                        preparationSeconds = s.preparationSeconds,
                        holdSeconds = s.holdSeconds,
                        enabled = s.enabled,
                        voiceCueEnabled = s.voiceCueEnabled,
                        sortOrder = s.sortOrder,
                        libraryId = s.libraryId,
                        prepCue = s.prepCue,
                        holdCue = s.holdCue,
                        notes = s.notes
                    )
                }
                val desc = customDescription.ifBlank { routine.description }
                communityRepo.publishRoutine(
                    routineName = routine.name,
                    description = desc,
                    category = category,
                    stretches = firestoreStretches
                )
                val successMsg = "Published '${routine.name}' to Community!"
                _communityActionMessage.value = successMsg
                onComplete(true, successMsg)
            } catch (e: Exception) {
                val errorMsg = "Failed to publish routine: ${e.message}"
                _communityActionMessage.value = errorMsg
                onComplete(false, errorMsg)
            }
        }
    }

    fun unpublishCommunityRoutine(routineId: String, routineName: String) {
        viewModelScope.launch {
            try {
                communityRepo.unpublishRoutine(routineId)
                _communityActionMessage.value = "Removed '$routineName' from Community"
            } catch (e: Exception) {
                _communityActionMessage.value = "Could not remove routine: ${e.message}"
            }
        }
    }

    fun cloneCommunityRoutine(
        communityRoutine: com.example.data.FirestorePublicRoutine,
        onCloned: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // 1. Create a brand new local custom routine with all stretches atomically
                val baseName = communityRoutine.name.trim().take(93)
                val clonedName = "$baseName (Copy)"
                val clonedDesc = "Copied from ${communityRoutine.authorName}: ${communityRoutine.description}".trim().take(500)
                val stretchEntities = communityRoutine.stretches.mapIndexed { index, item ->
                    StretchEntity(
                        routineId = 0L,
                        name = item.name,
                        category = item.category,
                        preparationSeconds = item.preparationSeconds,
                        holdSeconds = item.holdSeconds,
                        sortOrder = index,
                        enabled = item.enabled,
                        voiceCueEnabled = item.voiceCueEnabled,
                        libraryId = item.libraryId,
                        prepCue = item.prepCue,
                        holdCue = item.holdCue,
                        notes = item.notes
                    )
                }
                val newRoutineId = stretchRepository.createRoutineWithStretches(
                    name = clonedName,
                    description = clonedDesc,
                    stretches = stretchEntities
                )
                refreshBackupStatus()

                // 2. Increment the clone count on the public routine in the cloud
                communityRepo.incrementCloneCount(communityRoutine.id)

                _selectedRoutineId.value = newRoutineId
                val msg = "Cloned '${communityRoutine.name}' to your Routines!"
                _communityActionMessage.value = msg
                onCloned(newRoutineId)
            } catch (e: Exception) {
                _communityActionMessage.value = "Failed to clone routine: ${e.message}"
            }
        }
    }

    fun toggleFollowCreator(creatorId: String, creatorName: String) {
        viewModelScope.launch {
            try {
                val isCurrentlyFollowing = followedCreators.value.contains(creatorId)
                communityRepo.toggleFollowCreator(creatorId, creatorName, isCurrentlyFollowing)
                val msg = if (isCurrentlyFollowing) "Unfollowed $creatorName" else "Following $creatorName"
                _communityActionMessage.value = msg
            } catch (e: Exception) {
                _communityActionMessage.value = "Failed to update follow: ${e.message}"
            }
        }
    }

    fun addLibraryStretch(libraryStretch: LibraryStretchEntity) {
        viewModelScope.launch {
            stretchRepository.addLibraryStretchToRoutine(_selectedRoutineId.value, libraryStretch)
        }
    }

    fun updateLibraryStretch(libraryStretch: LibraryStretchEntity) {
        viewModelScope.launch {
            stretchRepository.updateLibraryStretch(libraryStretch)
        }
    }

    fun addCustomLibraryStretch(
        name: String,
        category: String,
        defaultPrepSeconds: Int,
        defaultHoldSeconds: Int,
        prepCue: String,
        holdCue: String,
        benefits: String
    ) {
        viewModelScope.launch {
            val newId = stretchRepository.addCustomLibraryStretch(
                name = name,
                category = category,
                defaultPrepSeconds = defaultPrepSeconds,
                defaultHoldSeconds = defaultHoldSeconds,
                prepCue = prepCue,
                holdCue = holdCue,
                benefits = benefits
            )
            // Paid cloud image generation is disabled; custom stretches without a bundled
            // verified drawable cleanly omit artwork instead of calling paid APIs.
        }
    }

    fun deleteLibraryStretch(id: String) {
        viewModelScope.launch {
            stretchRepository.deleteLibraryStretch(id)
        }
    }

    fun saveStretch(
        id: Long,
        name: String,
        preparationSeconds: Int,
        holdSeconds: Int,
        notes: String?,
        voiceCueEnabled: Boolean = true,
        indianName: String? = null
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                stretchRepository.addCustomStretchToRoutine(
                    routineId = _selectedRoutineId.value,
                    name = name,
                    prepSeconds = preparationSeconds,
                    holdSeconds = holdSeconds,
                    notes = notes,
                    voiceCueEnabled = voiceCueEnabled,
                    indianName = indianName
                )
                // Paid cloud image generation is disabled; custom stretches without a bundled
                // verified drawable cleanly omit artwork instead of calling paid APIs.
            } else {
                val existing = currentRoutineStretches.value.find { it.id == id }
                if (existing != null) {
                    val updated = existing.copy(
                        name = name.trim(),
                        indianName = indianName?.trim()?.ifBlank { null },
                        preparationSeconds = preparationSeconds,
                        holdSeconds = holdSeconds,
                        notes = notes?.trim()?.ifBlank { null },
                        voiceCueEnabled = voiceCueEnabled
                    )
                    stretchRepository.updateStretch(updated)
                }
            }
        }
    }

    fun toggleStretchEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            stretchRepository.updateEnabled(id, enabled)
        }
    }

    fun toggleStretchVoiceCue(id: Long, voiceCueEnabled: Boolean) {
        viewModelScope.launch {
            stretchRepository.updateVoiceCueEnabled(id, voiceCueEnabled)
        }
    }

    fun deleteStretch(stretch: StretchEntity) {
        viewModelScope.launch {
            stretchRepository.deleteStretch(stretch)
        }
    }

    fun reorderStretches(orderedIds: List<Long>) {
        viewModelScope.launch {
            stretchRepository.updateSortOrders(orderedIds)
        }
    }

    // --- Settings Management ---

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setVoiceMode(voiceMode: VoiceMode) {
        viewModelScope.launch {
            settingsRepository.setVoiceMode(voiceMode)
        }
    }

    fun setSpokenCuesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSpokenCuesEnabled(enabled)
        }
    }

    fun setCoachingCuesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setCoachingCuesEnabled(enabled)
        }
    }

    fun setCountdownAnnouncements(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setCountdownAnnouncements(enabled)
        }
    }

    fun setHalfwayCue(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHalfwayCue(enabled)
        }
    }

    fun setNextStretchCue(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNextStretchCue(enabled)
        }
    }

    fun setAsanaPronunciationStyle(style: com.example.data.AsanaPronunciationStyle) {
        viewModelScope.launch {
            settingsRepository.setAsanaPronunciationStyle(style)
        }
    }

    fun setAmbientSoundscape(soundscape: com.example.data.AmbientSoundscape, previewIfIdle: Boolean = true) {
        viewModelScope.launch {
            settingsRepository.setAmbientSoundscape(soundscape)
            val state = _playerUiState.value.state
            val isActivelyPlaying = state == PlayerState.PREPARING || state == PlayerState.HOLDING
            val settings = userSettings.value
            if (isActivelyPlaying) {
                ambientAudioController.updateSettings(soundscape, settings.ambientVolume, true, settings.customTrackPath)
            } else if (previewIfIdle) {
                ambientAudioController.preview(soundscape, settings.ambientVolume, settings.customTrackPath)
            }
        }
    }

    fun setAmbientVolume(volume: Float) {
        viewModelScope.launch {
            val clamped = volume.coerceIn(0f, 1f)
            settingsRepository.setAmbientVolume(clamped)
            val state = _playerUiState.value.state
            val isActivelyPlaying = state == PlayerState.PREPARING || state == PlayerState.HOLDING
            ambientAudioController.updateSettings(
                soundscape = userSettings.value.ambientSoundscape,
                volume = clamped,
                isSessionActive = isActivelyPlaying,
                customFilePath = userSettings.value.customTrackPath
            )
        }
    }

    fun setTransitionChimesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setTransitionChimesEnabled(enabled)
            if (enabled) {
                ambientAudioController.playTransitionChime(isCompletion = false)
            }
        }
    }

    fun setStudioLightingMode(mode: com.example.data.StudioLightingMode) {
        viewModelScope.launch { settingsRepository.setStudioLightingMode(mode) }
    }

    fun setStudioAromaProp(prop: com.example.data.StudioAromaProp) {
        viewModelScope.launch { settingsRepository.setStudioAromaProp(prop) }
    }

    fun setCompanionDogEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setCompanionDogEnabled(enabled) }
    }

    fun setSwayingLeavesEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSwayingLeavesEnabled(enabled) }
    }

    fun setSunbeamsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSunbeamsEnabled(enabled) }
    }

    fun setHubTransitionsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHubTransitionsEnabled(enabled) }
    }

    /**
     * 1-tap Smart Flow Order: sorts the active routine's stretches by
     * STANDING -> KNEELING -> PRONE -> SUPINE -> SEATED to minimize awkward floor/standing hopping.
     */
    fun optimizeCurrentRoutineFlow() {
        viewModelScope.launch {
            val stretches = currentRoutineStretches.value
            if (stretches.size < 2) return@launch
            val sorted = com.example.data.PoseManifestRepository.sortStretchesBySmartFlow(getApplication(), stretches)
            stretchRepository.updateSortOrders(sorted.map { it.id })
        }
    }

    fun onTransitionClipFinished() {
        val current = _playerUiState.value
        if (current.state != PlayerState.PREPARING) {
            pendingTransitionRoute = emptyList()
            _playerUiState.update { it.copy(activeTransitionVideoPath = null, activeTransitionCue = null) }
            return
        }
        val nextHop = pendingTransitionRoute.firstOrNull()
        if (nextHop != null && current.remainingSeconds >= 2) {
            pendingTransitionRoute = pendingTransitionRoute.drop(1)
            _playerUiState.update {
                it.copy(
                    activeTransitionVideoPath = nextHop.assetPath,
                    activeTransitionCue = nextHop.cue
                )
            }
        } else {
            pendingTransitionRoute = emptyList()
            _playerUiState.update { it.copy(activeTransitionVideoPath = null, activeTransitionCue = null) }
        }
    }

    fun cycleAmbientSoundscape() {
        val hasCustomTrack = userSettings.value.customTrackPath.isNotBlank() &&
            java.io.File(userSettings.value.customTrackPath).isFile
        val values = com.example.data.AmbientSoundscape.values().filter {
            it != com.example.data.AmbientSoundscape.CUSTOM_TRACK || hasCustomTrack
        }
        val currentIdx = values.indexOf(userSettings.value.ambientSoundscape)
        val next = values[(currentIdx + 1) % values.size]
        setAmbientSoundscape(next, previewIfIdle = true)
    }

    fun refreshDownloadedTracks() {
        _downloadedTracks.value = com.example.audio.OnlineAudioLibrary.listDownloadedTracks(getApplication())
    }

    fun clearOnlineAudioMessage() {
        _onlineAudioMessage.value = null
    }

    fun searchOnlineAudio(query: String) {
        viewModelScope.launch {
            _isSearchingOnlineAudio.value = true
            _onlineAudioMessage.value = null
            val res = com.example.audio.OnlineAudioLibrary.searchInternetArchive(query)
            res.onSuccess { list ->
                _onlineAudioResults.value = list
                if (list.isEmpty()) {
                    _onlineAudioMessage.value = "No free audio found for '$query'. Try another keyword."
                }
            }.onFailure { err ->
                _onlineAudioMessage.value = "Search failed: ${err.message ?: "Check internet connection"}"
            }
            _isSearchingOnlineAudio.value = false
        }
    }

    fun downloadOnlineTrack(track: com.example.audio.OnlineAudioTrack) {
        if (_downloadingTrackId.value != null) return
        viewModelScope.launch {
            _downloadingTrackId.value = track.identifier
            _onlineAudioMessage.value = null
            val res = com.example.audio.OnlineAudioLibrary.downloadInternetArchiveTrack(getApplication(), track)
            res.onSuccess { downloaded ->
                refreshDownloadedTracks()
                selectCustomAudioTrack(downloaded)
                _onlineAudioMessage.value = "Downloaded '${downloaded.title}' (${downloaded.formattedSize})"
            }.onFailure { err ->
                _onlineAudioMessage.value = err.message ?: "Download failed"
            }
            _downloadingTrackId.value = null
        }
    }

    fun importLocalAudio(uri: android.net.Uri) {
        viewModelScope.launch {
            _onlineAudioMessage.value = null
            val res = com.example.audio.OnlineAudioLibrary.importLocalAudioUri(getApplication(), uri)
            res.onSuccess { imported ->
                refreshDownloadedTracks()
                selectCustomAudioTrack(imported)
                _onlineAudioMessage.value = "Imported '${imported.title}' (${imported.formattedSize})"
            }.onFailure { err ->
                _onlineAudioMessage.value = err.message ?: "Could not import audio file"
            }
        }
    }

    fun selectCustomAudioTrack(track: com.example.audio.DownloadedAudioTrack) {
        viewModelScope.launch {
            settingsRepository.setCustomTrack(track.filePath, track.title)
            val state = _playerUiState.value.state
            val isActivelyPlaying = state == PlayerState.PREPARING || state == PlayerState.HOLDING
            val vol = userSettings.value.ambientVolume
            if (isActivelyPlaying) {
                ambientAudioController.start(com.example.data.AmbientSoundscape.CUSTOM_TRACK, vol, track.filePath)
            } else {
                ambientAudioController.preview(com.example.data.AmbientSoundscape.CUSTOM_TRACK, vol, track.filePath, 4500L)
            }
        }
    }

    fun deleteDownloadedTrack(track: com.example.audio.DownloadedAudioTrack) {
        viewModelScope.launch {
            val wasActive = userSettings.value.customTrackPath == track.filePath
            com.example.audio.OnlineAudioLibrary.deleteDownloadedTrack(track.filePath)
            refreshDownloadedTracks()
            if (wasActive) {
                settingsRepository.setCustomTrack("", "")
                settingsRepository.setAmbientSoundscape(com.example.data.AmbientSoundscape.TANPURA_DRONE)
                ambientAudioController.stop()
            }
        }
    }

    fun testVoicePronunciation(
        samplePose: String = "Bhujangasana (Cobra Pose)",
        style: com.example.data.AsanaPronunciationStyle = userSettings.value.asanaPronunciationStyle
    ) {
        speechController.testSamplePronunciation(samplePose, style)
    }

    fun previewLibraryStretchVoice(stretch: LibraryStretchEntity) {
        val combinedName = if (!stretch.indianName.isNullOrBlank() &&
            !stretch.name.contains(stretch.indianName, ignoreCase = true)
        ) {
            "${stretch.name} (${stretch.indianName})"
        } else {
            stretch.name
        }
        speechController.testSamplePronunciation(
            poseName = combinedName,
            style = userSettings.value.asanaPronunciationStyle
        )
    }

    // --- Player Timer Engine ---

    fun startRoutine(startingIndex: Int = 0) {
        viewModelScope.launch {
            val enabledList = currentRoutineEnabledStretches.value
            if (enabledList.isEmpty()) {
                return@launch
            }

            val currentRoutine = allRoutines.value.find { it.id == _selectedRoutineId.value }
            val routineName = currentRoutine?.name ?: "Routine"

            val validIndex = startingIndex.coerceIn(0, enabledList.size - 1)
            _playerUiState.value = PlayerUiState(
                state = PlayerState.PREPARING,
                routineName = routineName,
                activeRoutine = enabledList,
                currentStretchIndex = validIndex,
                remainingSeconds = 0,
                totalSegmentSeconds = 0
            )
            _selectedTab.value = 1 // Switch to player tab

            val settings = userSettings.value
            ambientAudioController.start(settings.ambientSoundscape, settings.ambientVolume, settings.customTrackPath)

            startStretchAtIndex(validIndex, enabledList)
        }
    }

    private fun startStretchAtIndex(index: Int, routine: List<StretchEntity>, previousStretch: StretchEntity? = null) {
        timerJob?.cancel()
        pendingTransitionRoute = emptyList()
        val stretch = routine.getOrNull(index) ?: run {
            completeRoutine()
            return
        }

        // Resolve PREP-only 5-hub posture transition if transitioning from a different posture hub
        val transitionRoute = if (previousStretch != null && userSettings.value.hubTransitionsEnabled && stretch.preparationSeconds > 0) {
            val ctx = getApplication<Application>()
            val fromHub = com.example.data.PoseManifestRepository.getPoseMetaForStretch(ctx, previousStretch.name, previousStretch.category)?.postureHub
            val toHub = com.example.data.PoseManifestRepository.getPoseMetaForStretch(ctx, stretch.name, stretch.category)?.postureHub
            if (fromHub != null && toHub != null && fromHub != toHub) {
                com.example.data.PoseManifestRepository.resolveTransitionRoute(fromHub, toHub)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
        val firstTransition = transitionRoute.firstOrNull()
        pendingTransitionRoute = if (transitionRoute.size > 1) transitionRoute.drop(1) else emptyList()

        val prepTime = stretch.preparationSeconds
        if (prepTime > 0) {
            _playerUiState.update {
                it.copy(
                    state = PlayerState.PREPARING,
                    activeRoutine = routine,
                    currentStretchIndex = index,
                    remainingSeconds = prepTime,
                    totalSegmentSeconds = prepTime,
                    activeTransitionVideoPath = firstTransition?.assetPath,
                    activeTransitionCue = firstTransition?.cue
                )
            }

            var countdownStarted = false
            fun triggerCountdown() {
                val current = _playerUiState.value
                if (!countdownStarted && current.state == PlayerState.PREPARING && current.currentStretchIndex == index) {
                    countdownStarted = true
                    runPreparationTimer(index, routine, prepTime)
                }
            }

            // Speak stretch title at natural, unhurried pace first
            speechController.announcePreparation(stretch, userSettings.value) {
                // After speaking finishes, begin the preparation countdown
                viewModelScope.launch {
                    triggerCountdown()
                }
            }

            // Dynamic fallback watchdog: scales with stretch title length so long names aren't cut off early
            val watchdogTimeoutMs = speechController.estimateAnnouncementTimeoutMs(
                stretch.name,
                userSettings.value.asanaPronunciationStyle
            )
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                delay(watchdogTimeoutMs)
                triggerCountdown()
            }
        } else {
            startHoldPhase(index, routine, stretch.holdSeconds)
        }
    }

    private fun runPreparationTimer(index: Int, routine: List<StretchEntity>, initialSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = initialSeconds
            while (seconds > 0) {
                delay(1000L)
                seconds--
                _playerUiState.update { it.copy(remainingSeconds = seconds) }
                speechController.announceCountdown(seconds, initialSeconds, userSettings.value, isHoldPhase = false)
            }
            val stretch = routine.getOrNull(index)
            if (stretch != null) {
                startHoldPhase(index, routine, stretch.holdSeconds)
            } else {
                completeRoutine()
            }
        }
    }

    private fun startHoldPhase(index: Int, routine: List<StretchEntity>, holdSeconds: Int) {
        timerJob?.cancel()
        pendingTransitionRoute = emptyList()
        val stretch = routine.getOrNull(index) ?: return
        val validHold = holdSeconds.coerceAtLeast(1)

        _playerUiState.update {
            it.copy(
                state = PlayerState.HOLDING,
                currentStretchIndex = index,
                remainingSeconds = validHold,
                totalSegmentSeconds = validHold,
                activeTransitionVideoPath = null,
                activeTransitionCue = null
            )
        }

        if (userSettings.value.transitionChimesEnabled) {
            ambientAudioController.playTransitionChime(isCompletion = false)
        }
        speechController.announceHold(stretch, userSettings.value)

        timerJob = viewModelScope.launch {
            var seconds = validHold
            val halfwayMark = validHold / 2
            var halfwayAnnounced = false

            while (seconds > 0) {
                delay(1000L)
                seconds--
                _playerUiState.update { it.copy(remainingSeconds = seconds) }

                if (!halfwayAnnounced && seconds == halfwayMark && validHold >= 10) {
                    halfwayAnnounced = true
                    speechController.announceHalfway(userSettings.value)
                }

                speechController.announceCountdown(seconds, validHold, userSettings.value, isHoldPhase = true)
            }

            // Move to next stretch or complete
            if (index < routine.size - 1) {
                startStretchAtIndex(index + 1, routine, previousStretch = stretch)
            } else {
                completeRoutine()
            }
        }
    }

    fun pause() {
        if (_playerUiState.value.state == PlayerState.PREPARING || _playerUiState.value.state == PlayerState.HOLDING) {
            timerJob?.cancel()
            speechController.stop()
            ambientAudioController.pause()
            _playerUiState.update {
                it.copy(
                    previousStateBeforePause = it.state,
                    state = PlayerState.PAUSED
                )
            }
        }
    }

    fun resume() {
        val current = _playerUiState.value
        if (current.state != PlayerState.PAUSED) return

        val routine = current.activeRoutine
        val index = current.currentStretchIndex
        val stretch = routine.getOrNull(index) ?: return
        val remaining = current.remainingSeconds

        val settings = userSettings.value
        ambientAudioController.start(settings.ambientSoundscape, settings.ambientVolume, settings.customTrackPath)

        if (current.previousStateBeforePause == PlayerState.PREPARING) {
            _playerUiState.update { it.copy(state = PlayerState.PREPARING) }
            runPreparationTimer(index, routine, remaining)
        } else {
            _playerUiState.update { it.copy(state = PlayerState.HOLDING) }
            runHoldTimerResume(index, routine, remaining, current.totalSegmentSeconds)
        }
    }

    private fun runHoldTimerResume(index: Int, routine: List<StretchEntity>, remainingSeconds: Int, totalHold: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = remainingSeconds
            val halfwayMark = totalHold / 2

            while (seconds > 0) {
                delay(1000L)
                seconds--
                _playerUiState.update { it.copy(remainingSeconds = seconds) }

                if (seconds == halfwayMark && totalHold >= 10 && seconds > 3) {
                    speechController.announceHalfway(userSettings.value)
                }

                speechController.announceCountdown(seconds, totalHold, userSettings.value, isHoldPhase = true)
            }

            if (index < routine.size - 1) {
                startStretchAtIndex(index + 1, routine, previousStretch = routine.getOrNull(index))
            } else {
                completeRoutine()
            }
        }
    }

    fun skipNext() {
        val current = _playerUiState.value
        if (current.currentStretchIndex < current.activeRoutine.size - 1) {
            startStretchAtIndex(
                current.currentStretchIndex + 1,
                current.activeRoutine,
                previousStretch = current.currentStretch
            )
        } else {
            completeRoutine()
        }
    }

    fun skipPrevious() {
        val current = _playerUiState.value
        if (current.currentStretchIndex > 0) {
            startStretchAtIndex(
                current.currentStretchIndex - 1,
                current.activeRoutine,
                previousStretch = current.currentStretch
            )
        } else {
            restartCurrentStretch()
        }
    }

    fun restartCurrentStretch() {
        val current = _playerUiState.value
        if (current.activeRoutine.isNotEmpty()) {
            startStretchAtIndex(current.currentStretchIndex, current.activeRoutine)
        }
    }

    fun stopSession() {
        timerJob?.cancel()
        speechController.stop()
        ambientAudioController.stop()
        _playerUiState.update {
            it.copy(
                state = PlayerState.IDLE,
                remainingSeconds = 0,
                totalSegmentSeconds = 0
            )
        }
    }

    private fun completeRoutine() {
        timerJob?.cancel()
        ambientAudioController.stop()
        if (userSettings.value.transitionChimesEnabled) {
            ambientAudioController.playTransitionChime(isCompletion = true)
        }
        _playerUiState.update {
            it.copy(
                state = PlayerState.COMPLETE,
                remainingSeconds = 0,
                totalSegmentSeconds = 0
            )
        }
        speechController.announceComplete(userSettings.value)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        speechController.shutdown()
        ambientAudioController.shutdown()
    }
}
