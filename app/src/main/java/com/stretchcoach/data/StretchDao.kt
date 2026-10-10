package com.stretchcoach.data

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
    @Query("SELECT * FROM stretches ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<StretchEntity>>

    @Query("SELECT * FROM stretches WHERE enabled = 1 ORDER BY sortOrder ASC, id ASC")
    fun observeEnabled(): Flow<List<StretchEntity>>

    @Query("SELECT COUNT(*) FROM stretches")
    suspend fun count(): Int

    @Query("SELECT name FROM stretches")
    suspend fun names(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stretch: StretchEntity): Long

    @Update
    suspend fun update(stretch: StretchEntity)

    @Delete
    suspend fun delete(stretch: StretchEntity)

    @Query("UPDATE stretches SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE stretches SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int)

    @Transaction
    suspend fun updateSortOrders(ids: List<Long>) {
        ids.forEachIndexed { index, id -> updateSortOrder(id, index) }
    }
}
