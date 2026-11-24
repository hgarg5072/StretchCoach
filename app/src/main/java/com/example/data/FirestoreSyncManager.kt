package com.example.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Synced(val routineCount: Int, val message: String = "") : SyncState()
    data class Error(val message: String) : SyncState()
}

class FirestoreSyncManager(
    private val stretchDao: StretchDao,
    private val firestoreRepo: FirestoreRoutineRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    suspend fun syncOnSignIn(): Int = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: return@withContext 0
        _syncState.value = SyncState.Syncing
        try {
            // 1. Fetch remote custom routines
            val remoteRoutines = firestoreRepo.fetchUserRoutines()
            val localCustom = stretchDao.getCustomRoutinesSync()
            var restoredCount = 0

            // 2. Restore any remote routines missing locally
            for (remote in remoteRoutines) {
                val existsLocally = localCustom.any {
                    it.name.equals(remote.name.trim(), ignoreCase = true)
                }
                if (!existsLocally) {
                    val newRoutine = RoutineEntity(
                        name = remote.name,
                        description = remote.description,
                        isCustom = true,
                        createdAt = remote.createdAt?.toDate()?.time ?: System.currentTimeMillis()
                    )
                    val newId = stretchDao.insertRoutine(newRoutine)
                    if (remote.stretches.isNotEmpty()) {
                        val stretchEntities = remote.stretches.mapIndexed { idx, s ->
                            StretchEntity(
                                routineId = newId,
                                libraryId = s.libraryId,
                                name = s.name,
                                category = s.category,
                                preparationSeconds = s.preparationSeconds,
                                holdSeconds = s.holdSeconds,
                                prepCue = s.prepCue,
                                holdCue = s.holdCue,
                                notes = s.notes,
                                enabled = s.enabled,
                                voiceCueEnabled = s.voiceCueEnabled,
                                sortOrder = if (s.sortOrder >= 0) s.sortOrder else idx
                            )
                        }
                        stretchDao.insertAll(stretchEntities)
                    }
                    restoredCount++
                }
            }

            // 3. Upload any local custom routines missing from cloud
            for (local in localCustom) {
                val existsRemotely = remoteRoutines.any {
                    it.name.equals(local.name.trim(), ignoreCase = true)
                }
                if (!existsRemotely) {
                    val stretches = stretchDao.getStretchesForRoutineSync(local.id)
                    val firestoreStretches = stretches.map { s ->
                        FirestoreStretchItem(
                            libraryId = s.libraryId,
                            name = s.name,
                            category = s.category,
                            preparationSeconds = s.preparationSeconds,
                            holdSeconds = s.holdSeconds,
                            prepCue = s.prepCue,
                            holdCue = s.holdCue,
                            notes = s.notes,
                            enabled = s.enabled,
                            voiceCueEnabled = s.voiceCueEnabled,
                            sortOrder = s.sortOrder
                        )
                    }
                    firestoreRepo.saveRoutine(
                        routineId = "",
                        name = local.name,
                        description = local.description,
                        stretches = firestoreStretches,
                        isCustom = true
                    )
                }
            }

            val finalCount = stretchDao.getCustomRoutinesSync().size
            _syncState.value = SyncState.Synced(finalCount, "Synced $finalCount custom routines with cloud")
            restoredCount
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error during cloud sync", e)
            _syncState.value = SyncState.Error(e.localizedMessage ?: "Sync error")
            0
        }
    }

    suspend fun syncRoutineUpsert(routineId: Long, previousName: String? = null) = withContext(Dispatchers.IO) {
        if (auth.currentUser == null) return@withContext
        try {
            val routine = stretchDao.getRoutineById(routineId) ?: return@withContext
            if (!routine.isCustom) return@withContext

            val stretches = stretchDao.getStretchesForRoutineSync(routineId)
            val firestoreStretches = stretches.map { s ->
                FirestoreStretchItem(
                    libraryId = s.libraryId,
                    name = s.name,
                    category = s.category,
                    preparationSeconds = s.preparationSeconds,
                    holdSeconds = s.holdSeconds,
                    prepCue = s.prepCue,
                    holdCue = s.holdCue,
                    notes = s.notes,
                    enabled = s.enabled,
                    voiceCueEnabled = s.voiceCueEnabled,
                    sortOrder = s.sortOrder
                )
            }

            val remoteRoutines = firestoreRepo.fetchUserRoutines()
            val existingRemote = remoteRoutines.find {
                it.name.equals(routine.name.trim(), ignoreCase = true) ||
                    (previousName != null && it.name.equals(previousName.trim(), ignoreCase = true))
            }

            firestoreRepo.saveRoutine(
                routineId = existingRemote?.id ?: "",
                name = routine.name,
                description = routine.description,
                stretches = firestoreStretches,
                isCustom = true,
                existingCreatedAt = existingRemote?.createdAt
            )
            _syncState.value = SyncState.Synced(remoteRoutines.size, "Routine synced to cloud")
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to sync routine upsert", e)
        }
    }

    suspend fun syncRoutineDelete(routineName: String) = withContext(Dispatchers.IO) {
        if (auth.currentUser == null) return@withContext
        try {
            val remoteRoutines = firestoreRepo.fetchUserRoutines()
            val existingRemote = remoteRoutines.find {
                it.name.equals(routineName.trim(), ignoreCase = true)
            }
            if (existingRemote != null && existingRemote.id.isNotBlank()) {
                firestoreRepo.deleteRoutine(existingRemote.id)
            }
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to sync routine delete", e)
        }
    }
}
