package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StretchDao {

    // --- Routines ---

    @Query("SELECT * FROM routines ORDER BY is_custom ASC, id ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines ORDER BY is_custom ASC, id ASC")
    suspend fun getAllRoutinesSync(): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE is_custom = 1 ORDER BY id ASC")
    suspend fun getCustomRoutinesSync(): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    suspend fun getRoutineById(id: Long): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>): List<Long>

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutineById(id: Long)

    // --- Stretches in Routine ---

    @Query("SELECT * FROM stretches WHERE routine_id = :routineId ORDER BY sort_order ASC, id ASC")
    fun getStretchesForRoutine(routineId: Long): Flow<List<StretchEntity>>

    @Query("SELECT * FROM stretches WHERE routine_id = :routineId AND enabled = 1 ORDER BY sort_order ASC, id ASC")
    fun getEnabledStretchesForRoutine(routineId: Long): Flow<List<StretchEntity>>

    @Query("SELECT * FROM stretches WHERE routine_id = :routineId ORDER BY sort_order ASC, id ASC")
    suspend fun getStretchesForRoutineSync(routineId: Long): List<StretchEntity>

    @Query("SELECT * FROM stretches WHERE id = :id LIMIT 1")
    suspend fun getStretchById(id: Long): StretchEntity?

    @Query("SELECT COUNT(*) FROM stretches WHERE routine_id = :routineId")
    suspend fun getStretchCountForRoutine(routineId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStretch(stretch: StretchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stretches: List<StretchEntity>)

    @Update
    suspend fun updateStretch(stretch: StretchEntity)

    @Delete
    suspend fun deleteStretch(stretch: StretchEntity)

    @Query("DELETE FROM stretches WHERE id = :id")
    suspend fun deleteStretchById(id: Long)

    @Query("DELETE FROM stretches WHERE routine_id = :routineId")
    suspend fun deleteAllStretchesForRoutine(routineId: Long)

    @Query("UPDATE stretches SET enabled = :enabled WHERE id = :id")
    suspend fun updateEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE stretches SET voice_cue_enabled = :voiceCueEnabled WHERE id = :id")
    suspend fun updateVoiceCueEnabled(id: Long, voiceCueEnabled: Boolean)

    @Query("UPDATE stretches SET sort_order = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int)

    @Query("UPDATE stretches SET custom_image_path = :imagePath WHERE id = :id")
    suspend fun updateStretchCustomImagePath(id: Long, imagePath: String)

    @Query("UPDATE library_stretches SET custom_image_path = :imagePath WHERE id = :id")
    suspend fun updateLibraryStretchCustomImagePath(id: String, imagePath: String)

    @Transaction
    suspend fun updateAllSortOrders(orderedIds: List<Long>) {
        orderedIds.forEachIndexed { index, id ->
            updateSortOrder(id, index)
        }
    }

    // --- Library Stretches (Persisted in Room) ---

    @Query("SELECT * FROM library_stretches ORDER BY category ASC, name ASC")
    fun getAllLibraryStretches(): Flow<List<LibraryStretchEntity>>

    @Query("SELECT * FROM library_stretches ORDER BY category ASC, name ASC")
    suspend fun getAllLibraryStretchesSync(): List<LibraryStretchEntity>

    @Query("SELECT * FROM library_stretches WHERE id = :id LIMIT 1")
    suspend fun getLibraryStretchById(id: String): LibraryStretchEntity?

    @Query("SELECT COUNT(*) FROM library_stretches")
    suspend fun getLibraryStretchCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraryStretch(stretch: LibraryStretchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLibraryStretches(stretches: List<LibraryStretchEntity>)

    @Update
    suspend fun updateLibraryStretch(stretch: LibraryStretchEntity)

    @Delete
    suspend fun deleteLibraryStretch(stretch: LibraryStretchEntity)

    @Query("DELETE FROM library_stretches WHERE id = :id")
    suspend fun deleteLibraryStretchById(id: String)
}
