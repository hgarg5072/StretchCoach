package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StretchRepository(
    private val stretchDao: StretchDao,
    private val context: Context? = null,
    private val syncManager: FirestoreSyncManager? = null
) {

    val allRoutines: Flow<List<RoutineEntity>> = stretchDao.getAllRoutines()
    val allLibraryStretches: Flow<List<LibraryStretchEntity>> = stretchDao.getAllLibraryStretches()

    fun getStretchesForRoutine(routineId: Long): Flow<List<StretchEntity>> {
        return stretchDao.getStretchesForRoutine(routineId)
    }

    suspend fun getStretchesForRoutineSync(routineId: Long): List<StretchEntity> = withContext(Dispatchers.IO) {
        stretchDao.getStretchesForRoutineSync(routineId)
    }

    fun getEnabledStretchesForRoutine(routineId: Long): Flow<List<StretchEntity>> {
        return stretchDao.getEnabledStretchesForRoutine(routineId)
    }

    suspend fun checkAndSeedDefaults() = withContext(Dispatchers.IO) {
        // 1. Seed and sync Library Stretches (all 150 items, preserving user-created custom items)
        val existingLibrary = stretchDao.getAllLibraryStretchesSync()
        val existingCustomIds = existingLibrary.filter { it.isCustom }.map { it.id }.toSet()
        val existingNonCustomCount = existingLibrary.count { !it.isCustom }
        if (existingNonCustomCount < StretchLibrary.ALL_100_STRETCHES.size) {
            val libraryEntities = StretchLibrary.ALL_100_STRETCHES
                .filter { it.id !in existingCustomIds }
                .map { lib ->
                    val resolvedIndianName = lib.indianName
                        ?: com.example.audio.SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari
                    LibraryStretchEntity(
                        id = lib.id,
                        name = lib.name,
                        indianName = resolvedIndianName,
                        category = lib.category.title,
                        defaultPrepSeconds = lib.defaultPrepSeconds,
                        defaultHoldSeconds = lib.defaultHoldSeconds,
                        prepCue = lib.prepCue,
                        holdCue = lib.holdCue,
                        benefits = lib.benefits,
                        isCustom = false
                    )
                }
            stretchDao.insertAllLibraryStretches(libraryEntities)
        }

        // 2. Seed default routines
        val existingRoutines = stretchDao.getAllRoutinesSync()
        if (existingRoutines.isEmpty()) {
            // Seed all default routines
            AppDatabase.DEFAULT_ROUTINES.forEach { routine ->
                val routineId = stretchDao.insertRoutine(routine)
                val stretches = AppDatabase.createDefaultStretchesForRoutine(routineId)
                if (stretches.isNotEmpty()) {
                    stretchDao.insertAll(stretches)
                }
            }
        } else {
            // Check if any default routine is missing or missing stretches
            AppDatabase.DEFAULT_ROUTINES.forEach { defaultRoutine ->
                val existing = existingRoutines.find { it.name.equals(defaultRoutine.name, ignoreCase = true) }
                if (existing == null) {
                    val insertedId = stretchDao.insertRoutine(defaultRoutine)
                    val stretches = AppDatabase.createDefaultStretchesForRoutine(defaultRoutine.id)
                        .map { it.copy(routineId = insertedId) }
                    if (stretches.isNotEmpty()) {
                        stretchDao.insertAll(stretches)
                    }
                } else {
                    val count = stretchDao.getStretchCountForRoutine(existing.id)
                    if (count == 0) {
                        val stretches = AppDatabase.createDefaultStretchesForRoutine(defaultRoutine.id)
                            .map { it.copy(routineId = existing.id) }
                        if (stretches.isNotEmpty()) {
                            stretchDao.insertAll(stretches)
                        }
                    }
                }
            }
        }

        // 3. Safeguard and verify custom routines across updates
        context?.let { ctx ->
            val customRoutines = stretchDao.getCustomRoutinesSync()
            if (customRoutines.isEmpty()) {
                val backupItems = RoutineBackupManager.loadAutoBackup(ctx)
                if (backupItems.isNotEmpty()) {
                    val restored = RoutineBackupManager.restoreIntoDao(stretchDao, backupItems)
                    Log.i("StretchRepository", "Safeguard: Restored $restored custom routines from persistent backup!")
                }
            } else {
                // Refresh persistent backup snapshot with current state
                RoutineBackupManager.performDaoSnapshot(ctx, stretchDao)
            }
        }
    }

    private suspend fun triggerAutoBackup() {
        context?.let { ctx ->
            RoutineBackupManager.performDaoSnapshot(ctx, stretchDao)
        }
    }

    suspend fun updateLibraryStretch(stretch: LibraryStretchEntity) = withContext(Dispatchers.IO) {
        stretchDao.updateLibraryStretch(stretch)
    }

    suspend fun addCustomLibraryStretch(
        name: String,
        category: String,
        defaultPrepSeconds: Int,
        defaultHoldSeconds: Int,
        prepCue: String,
        holdCue: String,
        benefits: String
    ): String = withContext(Dispatchers.IO) {
        val id = "custom_${System.currentTimeMillis()}"
        val entity = LibraryStretchEntity(
            id = id,
            name = name.trim(),
            category = category.trim().ifBlank { "Custom" },
            defaultPrepSeconds = defaultPrepSeconds,
            defaultHoldSeconds = defaultHoldSeconds,
            prepCue = prepCue.trim(),
            holdCue = holdCue.trim(),
            benefits = benefits.trim(),
            isCustom = true
        )
        stretchDao.insertLibraryStretch(entity)
        id
    }

    suspend fun deleteLibraryStretch(id: String) = withContext(Dispatchers.IO) {
        stretchDao.deleteLibraryStretchById(id)
    }

    suspend fun createRoutine(name: String, description: String = "", initialStretchIds: List<String> = emptyList()): Long = withContext(Dispatchers.IO) {
        val routine = RoutineEntity(
            name = name.trim(),
            description = description.trim(),
            isCustom = true
        )
        val routineId = stretchDao.insertRoutine(routine)

        if (initialStretchIds.isNotEmpty()) {
            val stretches = initialStretchIds.mapIndexedNotNull { index, libId ->
                val lib = StretchLibrary.getById(libId) ?: return@mapIndexedNotNull null
                StretchEntity(
                    routineId = routineId,
                    libraryId = lib.id,
                    name = lib.name,
                    category = lib.category.title,
                    preparationSeconds = lib.defaultPrepSeconds,
                    holdSeconds = lib.defaultHoldSeconds,
                    prepCue = lib.prepCue,
                    holdCue = lib.holdCue,
                    notes = lib.benefits,
                    enabled = true,
                    voiceCueEnabled = true,
                    sortOrder = index
                )
            }
            stretchDao.insertAll(stretches)
        }
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(routineId)
        routineId
    }

    suspend fun duplicateRoutine(sourceRoutineId: Long, newName: String, newDescription: String = ""): Long = withContext(Dispatchers.IO) {
        val sourceRoutine = stretchDao.getRoutineById(sourceRoutineId)
        val sourceStretches = stretchDao.getStretchesForRoutineSync(sourceRoutineId)

        val newRoutine = RoutineEntity(
            name = newName.trim(),
            description = newDescription.trim().ifBlank { sourceRoutine?.description ?: "" },
            isCustom = true
        )
        val newRoutineId = stretchDao.insertRoutine(newRoutine)

        if (sourceStretches.isNotEmpty()) {
            val clonedStretches = sourceStretches.map { stretch ->
                stretch.copy(
                    id = 0L, // Auto-generate new primary key
                    routineId = newRoutineId
                )
            }
            stretchDao.insertAll(clonedStretches)
        }
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(newRoutineId)
        newRoutineId
    }

    suspend fun updateRoutine(routine: RoutineEntity) = withContext(Dispatchers.IO) {
        stretchDao.updateRoutine(routine)
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(routine.id)
    }

    suspend fun renameRoutine(routineId: Long, newName: String, newDescription: String) = withContext(Dispatchers.IO) {
        val routine = stretchDao.getRoutineById(routineId)
        if (routine != null) {
            val previousName = routine.name
            stretchDao.updateRoutine(routine.copy(name = newName.trim(), description = newDescription.trim()))
            triggerAutoBackup()
            syncManager?.syncRoutineUpsert(routineId, previousName = previousName)
        }
    }

    suspend fun createRoutineWithStretches(
        name: String,
        description: String,
        stretches: List<StretchEntity>
    ): Long = withContext(Dispatchers.IO) {
        val routine = RoutineEntity(
            name = name.trim().take(100),
            description = description.trim().take(500),
            isCustom = true
        )
        val routineId = stretchDao.insertRoutine(routine)
        if (stretches.isNotEmpty()) {
            val entities = stretches.mapIndexed { index, s ->
                s.copy(id = 0L, routineId = routineId, sortOrder = index)
            }
            stretchDao.insertAll(entities)
        }
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(routineId)
        routineId
    }

    suspend fun deleteRoutine(routineId: Long) = withContext(Dispatchers.IO) {
        val routine = stretchDao.getRoutineById(routineId)
        stretchDao.deleteAllStretchesForRoutine(routineId)
        stretchDao.deleteRoutineById(routineId)
        triggerAutoBackup()
        if (routine != null) {
            syncManager?.syncRoutineDelete(routine.name)
        }
    }

    suspend fun addLibraryStretchToRoutine(routineId: Long, libraryStretch: LibraryStretchEntity): Long = withContext(Dispatchers.IO) {
        val existing = stretchDao.getStretchesForRoutineSync(routineId)
        val nextSort = (existing.maxOfOrNull { it.sortOrder } ?: -1) + 1

        val newStretch = StretchEntity(
            routineId = routineId,
            libraryId = libraryStretch.id,
            name = libraryStretch.name,
            indianName = libraryStretch.indianName,
            customImagePath = libraryStretch.customImagePath,
            category = libraryStretch.category,
            preparationSeconds = libraryStretch.defaultPrepSeconds,
            holdSeconds = libraryStretch.defaultHoldSeconds,
            prepCue = libraryStretch.prepCue,
            holdCue = libraryStretch.holdCue,
            notes = libraryStretch.benefits,
            enabled = true,
            voiceCueEnabled = true,
            sortOrder = nextSort
        )
        val id = stretchDao.insertStretch(newStretch)
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(routineId)
        id
    }

    suspend fun addCustomStretchToRoutine(
        routineId: Long,
        name: String,
        prepSeconds: Int,
        holdSeconds: Int,
        notes: String?,
        voiceCueEnabled: Boolean = true,
        indianName: String? = null
    ): Long = withContext(Dispatchers.IO) {
        val existing = stretchDao.getStretchesForRoutineSync(routineId)
        val nextSort = (existing.maxOfOrNull { it.sortOrder } ?: -1) + 1

        val newStretch = StretchEntity(
            routineId = routineId,
            libraryId = null,
            name = name.trim(),
            indianName = indianName?.trim()?.ifBlank { null },
            category = "Custom",
            preparationSeconds = prepSeconds,
            holdSeconds = holdSeconds,
            prepCue = "Prepare for ${name.trim()}.",
            holdCue = notes?.trim() ?: "Breathe deeply and hold steadily.",
            notes = notes?.trim()?.ifBlank { null },
            enabled = true,
            voiceCueEnabled = voiceCueEnabled,
            sortOrder = nextSort
        )
        val id = stretchDao.insertStretch(newStretch)
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(routineId)
        id
    }

    suspend fun updateStretchCustomImagePath(stretchId: Long, imagePath: String) = withContext(Dispatchers.IO) {
        stretchDao.updateStretchCustomImagePath(stretchId, imagePath)
    }

    suspend fun updateLibraryStretchCustomImagePath(libraryId: String, imagePath: String) = withContext(Dispatchers.IO) {
        stretchDao.updateLibraryStretchCustomImagePath(libraryId, imagePath)
    }

    suspend fun updateStretch(stretch: StretchEntity) = withContext(Dispatchers.IO) {
        stretchDao.updateStretch(stretch)
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(stretch.routineId)
    }

    suspend fun deleteStretch(stretch: StretchEntity) = withContext(Dispatchers.IO) {
        stretchDao.deleteStretch(stretch)
        triggerAutoBackup()
        syncManager?.syncRoutineUpsert(stretch.routineId)
    }

    suspend fun deleteStretchById(id: Long) = withContext(Dispatchers.IO) {
        val stretch = stretchDao.getStretchById(id)
        stretchDao.deleteStretchById(id)
        triggerAutoBackup()
        if (stretch != null) {
            syncManager?.syncRoutineUpsert(stretch.routineId)
        }
    }

    suspend fun updateEnabled(id: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        val stretch = stretchDao.getStretchById(id)
        stretchDao.updateEnabled(id, enabled)
        triggerAutoBackup()
        if (stretch != null) {
            syncManager?.syncRoutineUpsert(stretch.routineId)
        }
    }

    suspend fun updateVoiceCueEnabled(id: Long, voiceCueEnabled: Boolean) = withContext(Dispatchers.IO) {
        val stretch = stretchDao.getStretchById(id)
        stretchDao.updateVoiceCueEnabled(id, voiceCueEnabled)
        triggerAutoBackup()
        if (stretch != null) {
            syncManager?.syncRoutineUpsert(stretch.routineId)
        }
    }

    suspend fun updateSortOrders(orderedIds: List<Long>) = withContext(Dispatchers.IO) {
        val firstId = orderedIds.firstOrNull()
        val stretch = if (firstId != null) stretchDao.getStretchById(firstId) else null
        stretchDao.updateAllSortOrders(orderedIds)
        triggerAutoBackup()
        if (stretch != null) {
            syncManager?.syncRoutineUpsert(stretch.routineId)
        }
    }

    // --- Export, Import & Backup Methods ---

    suspend fun exportCustomRoutinesJson(): String = withContext(Dispatchers.IO) {
        val customRoutines = stretchDao.getCustomRoutinesSync()
        val items = customRoutines.map { routine ->
            val stretches = stretchDao.getStretchesForRoutineSync(routine.id)
            BackupRoutineItem(
                name = routine.name,
                description = routine.description,
                createdAt = routine.createdAt,
                stretches = stretches.map { s ->
                    BackupStretchItem(
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
            )
        }
        RoutineBackupManager.createBackupJson(items)
    }

    suspend fun importCustomRoutinesJson(json: String): Int = withContext(Dispatchers.IO) {
        val items = RoutineBackupManager.parseBackupJson(json)
        val restored = RoutineBackupManager.restoreIntoDao(stretchDao, items)
        if (restored > 0) {
            triggerAutoBackup()
        }
        restored
    }

    suspend fun restoreFromAutoBackup(): Int = withContext(Dispatchers.IO) {
        val ctx = context ?: return@withContext 0
        val items = RoutineBackupManager.loadAutoBackup(ctx)
        val restored = RoutineBackupManager.restoreIntoDao(stretchDao, items)
        if (restored > 0) {
            triggerAutoBackup()
        }
        restored
    }

    fun getBackupStatus(): BackupStatus {
        val ctx = context ?: return BackupStatus()
        return RoutineBackupManager.getBackupStatus(ctx)
    }
}
