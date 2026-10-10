package com.stretchcoach.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StretchEntity::class], version = 1, exportSchema = false)
abstract class StretchDatabase : RoomDatabase() {
    abstract fun stretchDao(): StretchDao

    companion object {
        @Volatile
        private var instance: StretchDatabase? = null

        fun getInstance(context: Context): StretchDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StretchDatabase::class.java,
                    "stretch_coach.db"
                ).build().also { instance = it }
            }
    }
}
