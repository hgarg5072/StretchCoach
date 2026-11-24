package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stretches",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["routine_id"]),
        Index(value = ["routine_id", "sort_order"])
    ]
)
data class StretchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "routine_id")
    val routineId: Long = 1L,
    @ColumnInfo(name = "library_id")
    val libraryId: String? = null,
    val name: String,
    @ColumnInfo(name = "indian_name")
    val indianName: String? = null,
    @ColumnInfo(name = "custom_image_path")
    val customImagePath: String? = null,
    val category: String = "General",
    @ColumnInfo(name = "preparation_seconds")
    val preparationSeconds: Int = 8,
    @ColumnInfo(name = "hold_seconds")
    val holdSeconds: Int = 30,
    @ColumnInfo(name = "prep_cue")
    val prepCue: String? = null,
    @ColumnInfo(name = "hold_cue")
    val holdCue: String? = null,
    val notes: String? = null,
    val enabled: Boolean = true,
    @ColumnInfo(name = "voice_cue_enabled")
    val voiceCueEnabled: Boolean = true,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
) {
    val totalSeconds: Int
        get() = preparationSeconds + holdSeconds

    val effectiveHoldCue: String
        get() = when {
            !notes.isNullOrBlank() -> notes.trim()
            !holdCue.isNullOrBlank() -> holdCue.trim()
            else -> "Breathe smoothly, maintain your alignment, and ease off if anything feels sharp."
        }

    val effectivePrepCue: String
        get() = when {
            !prepCue.isNullOrBlank() -> prepCue.trim()
            else -> "Move gently into position and prepare for $name."
        }
}
