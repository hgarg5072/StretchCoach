package com.example.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class BackupStretchItem(
    val libraryId: String? = null,
    val name: String,
    val category: String = "General",
    val preparationSeconds: Int = 8,
    val holdSeconds: Int = 30,
    val prepCue: String? = null,
    val holdCue: String? = null,
    val notes: String? = null,
    val enabled: Boolean = true,
    val voiceCueEnabled: Boolean = true,
    val sortOrder: Int = 0
)

data class BackupRoutineItem(
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val stretches: List<BackupStretchItem> = emptyList()
)

data class BackupStatus(
    val totalRoutines: Int = 0,
    val lastBackupTimestamp: Long = 0L,
    val hasBackup: Boolean = false
)

object RoutineBackupManager {

    private const val TAG = "RoutineBackupManager"
    private const val PREFS_NAME = "stretch_coach_backup_prefs"
    private const val KEY_BACKUP_JSON = "custom_routines_backup_json"
    private const val KEY_LAST_BACKUP_TIME = "last_backup_time"
    private const val FILE_NAME = "custom_routines_backup.json"

    /**
     * Serializes a list of backup routine items into clean formatted JSON.
     */
    fun createBackupJson(routines: List<BackupRoutineItem>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("app", "StretchCoach")
        root.put("exportedAt", System.currentTimeMillis())

        val routinesArray = JSONArray()
        for (routine in routines) {
            val rObj = JSONObject()
            rObj.put("name", routine.name)
            rObj.put("description", routine.description)
            rObj.put("createdAt", routine.createdAt)

            val stretchesArray = JSONArray()
            for (s in routine.stretches) {
                val sObj = JSONObject()
                if (s.libraryId != null) sObj.put("libraryId", s.libraryId)
                sObj.put("name", s.name)
                sObj.put("category", s.category)
                sObj.put("preparationSeconds", s.preparationSeconds)
                sObj.put("holdSeconds", s.holdSeconds)
                if (s.prepCue != null) sObj.put("prepCue", s.prepCue)
                if (s.holdCue != null) sObj.put("holdCue", s.holdCue)
                if (s.notes != null) sObj.put("notes", s.notes)
                sObj.put("enabled", s.enabled)
                sObj.put("voiceCueEnabled", s.voiceCueEnabled)
                sObj.put("sortOrder", s.sortOrder)
                stretchesArray.put(sObj)
            }
            rObj.put("stretches", stretchesArray)
            routinesArray.put(rObj)
        }
        root.put("routines", routinesArray)
        return root.toString(2)
    }

    /**
     * Parses a JSON string into a list of BackupRoutineItems.
     */
    fun parseBackupJson(jsonString: String): List<BackupRoutineItem> {
        val routines = mutableListOf<BackupRoutineItem>()
        try {
            val root = JSONObject(jsonString)
            val routinesArray = root.optJSONArray("routines") ?: return emptyList()

            for (i in 0 until routinesArray.length()) {
                val rObj = routinesArray.getJSONObject(i)
                val name = rObj.optString("name", "").trim()
                if (name.isBlank()) continue

                val description = rObj.optString("description", "")
                val createdAt = rObj.optLong("createdAt", System.currentTimeMillis())

                val stretchesList = mutableListOf<BackupStretchItem>()
                val stretchesArray = rObj.optJSONArray("stretches")
                if (stretchesArray != null) {
                    for (j in 0 until stretchesArray.length()) {
                        val sObj = stretchesArray.getJSONObject(j)
                        val sName = sObj.optString("name", "").trim()
                        if (sName.isBlank()) continue

                        val libId = if (sObj.has("libraryId") && !sObj.isNull("libraryId")) {
                            sObj.getString("libraryId").takeIf { it.isNotBlank() }
                        } else null

                        stretchesList.add(
                            BackupStretchItem(
                                libraryId = libId,
                                name = sName,
                                category = sObj.optString("category", "General"),
                                preparationSeconds = sObj.optInt("preparationSeconds", 8),
                                holdSeconds = sObj.optInt("holdSeconds", 30),
                                prepCue = sObj.optString("prepCue").takeIf { it.isNotBlank() },
                                holdCue = sObj.optString("holdCue").takeIf { it.isNotBlank() },
                                notes = sObj.optString("notes").takeIf { it.isNotBlank() },
                                enabled = sObj.optBoolean("enabled", true),
                                voiceCueEnabled = sObj.optBoolean("voiceCueEnabled", true),
                                sortOrder = sObj.optInt("sortOrder", j)
                            )
                        )
                    }
                }

                routines.add(
                    BackupRoutineItem(
                        name = name,
                        description = description,
                        createdAt = createdAt,
                        stretches = stretchesList
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing backup JSON", e)
        }
        return routines
    }

    /**
     * Dual-layer persistence: Saves to both internal file storage and SharedPreferences.
     */
    fun saveAutoBackup(context: Context, routines: List<BackupRoutineItem>) {
        if (routines.isEmpty()) return
        try {
            val json = createBackupJson(routines)
            val now = System.currentTimeMillis()

            // 1. SharedPreferences
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_BACKUP_JSON, json)
                .putLong(KEY_LAST_BACKUP_TIME, now)
                .apply()

            // 2. Internal File Storage
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(json)
            Log.d(TAG, "Successfully auto-backed up ${routines.size} custom routines")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save auto-backup", e)
        }
    }

    /**
     * Loads the auto-backup from file or SharedPreferences fallback.
     */
    fun loadAutoBackup(context: Context): List<BackupRoutineItem> {
        try {
            // Check file first
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists() && file.length() > 0) {
                val content = file.readText()
                val parsed = parseBackupJson(content)
                if (parsed.isNotEmpty()) return parsed
            }

            // Fallback to SharedPreferences
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_BACKUP_JSON, null)
            if (!json.isNullOrBlank()) {
                return parseBackupJson(json)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load auto-backup", e)
        }
        return emptyList()
    }

    /**
     * Reads current backup status metadata.
     */
    fun getBackupStatus(context: Context): BackupStatus {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastTime = prefs.getLong(KEY_LAST_BACKUP_TIME, 0L)
        val file = File(context.filesDir, FILE_NAME)
        val hasFile = file.exists() && file.length() > 0
        val routines = loadAutoBackup(context)
        return BackupStatus(
            totalRoutines = routines.size,
            lastBackupTimestamp = lastTime,
            hasBackup = routines.isNotEmpty() || hasFile
        )
    }

    /**
     * Clears persisted auto-backup when the user intentionally deletes all custom routines.
     */
    fun clearAutoBackup(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .remove(KEY_BACKUP_JSON)
                .remove(KEY_LAST_BACKUP_TIME)
                .apply()

            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
            Log.d(TAG, "Cleared auto-backup because no custom routines remain")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear auto-backup", e)
        }
    }

    /**
     * Extracts all custom routines from Room DAO and saves an auto-backup snapshot.
     */
    suspend fun performDaoSnapshot(context: Context, dao: StretchDao): Int {
        return try {
            val customRoutines = dao.getCustomRoutinesSync()
            if (customRoutines.isEmpty()) {
                clearAutoBackup(context)
                return 0
            }

            val items = customRoutines.map { routine ->
                val stretches = dao.getStretchesForRoutineSync(routine.id)
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

            saveAutoBackup(context, items)
            items.size
        } catch (e: Exception) {
            Log.e(TAG, "Failed to snapshot custom routines", e)
            0
        }
    }

    /**
     * Restores backup items into Room. Ensures existing custom routines are not duplicated.
     */
    suspend fun restoreIntoDao(dao: StretchDao, items: List<BackupRoutineItem>): Int {
        if (items.isEmpty()) return 0
        val existingRoutines = dao.getAllRoutinesSync()
        var restoredCount = 0

        for (item in items) {
            val existing = existingRoutines.find {
                it.isCustom && it.name.equals(item.name.trim(), ignoreCase = true)
            }

            val routineId = if (existing == null) {
                val newRoutine = RoutineEntity(
                    name = item.name.trim(),
                    description = item.description.trim(),
                    isCustom = true,
                    createdAt = item.createdAt
                )
                val id = dao.insertRoutine(newRoutine)
                restoredCount++
                id
            } else {
                // If it already exists and has stretches, skip to avoid overwriting current state
                val count = dao.getStretchCountForRoutine(existing.id)
                if (count > 0) continue
                existing.id
            }

            if (item.stretches.isNotEmpty()) {
                val stretchEntities = item.stretches.mapIndexed { index, s ->
                    StretchEntity(
                        routineId = routineId,
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
                        sortOrder = if (s.sortOrder >= 0) s.sortOrder else index
                    )
                }
                dao.insertAll(stretchEntities)
            }
        }
        return restoredCount
    }
}
