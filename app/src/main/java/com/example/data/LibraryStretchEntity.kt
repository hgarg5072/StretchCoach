package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_stretches")
data class LibraryStretchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "indian_name")
    val indianName: String? = null,

    @ColumnInfo(name = "custom_image_path")
    val customImagePath: String? = null,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "default_prep_seconds")
    val defaultPrepSeconds: Int = 8,

    @ColumnInfo(name = "default_hold_seconds")
    val defaultHoldSeconds: Int = 30,

    @ColumnInfo(name = "prep_cue")
    val prepCue: String = "",

    @ColumnInfo(name = "hold_cue")
    val holdCue: String = "",

    @ColumnInfo(name = "benefits")
    val benefits: String = "",

    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false
)
