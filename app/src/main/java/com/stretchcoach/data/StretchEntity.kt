package com.stretchcoach.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stretches")
data class StretchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val preparationSeconds: Int,
    val holdSeconds: Int,
    val notes: String?,
    val enabled: Boolean,
    val sortOrder: Int
)
