package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RoutineEntity::class, StretchEntity::class, LibraryStretchEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stretchDao(): StretchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val DEFAULT_ROUTINES = listOf(
            RoutineEntity(
                id = 1L,
                name = "Morning Mobility",
                description = "Gentle head-to-toe morning wake up routine to boost circulation.",
                isCustom = false
            ),
            RoutineEntity(
                id = 2L,
                name = "Night Relaxation",
                description = "Calming restorative bedtime stretches for deeper sleep.",
                isCustom = false
            ),
            RoutineEntity(
                id = 3L,
                name = "Desk & Posture Reset",
                description = "Targeted relief for neck, shoulders, wrists, and hips after sitting.",
                isCustom = false
            ),
            RoutineEntity(
                id = 4L,
                name = "Full Body Flexibility",
                description = "Comprehensive mobility routine for all major muscle groups.",
                isCustom = false
            ),
            RoutineEntity(
                id = 5L,
                name = "Runner's Recovery",
                description = "Essential stretches for quads, hamstrings, hips, and calves.",
                isCustom = false
            )
        )

        fun createDefaultStretchesForRoutine(routineId: Long): List<StretchEntity> {
            val libraryIds = when (routineId) {
                1L -> listOf(
                    "neck_side_right",
                    "neck_side_left",
                    "cat_cow_spinal_flow",
                    "standing_hamstring_fold",
                    "hip_flexor_lunge_right",
                    "hip_flexor_lunge_left",
                    "standing_quad_right",
                    "standing_quad_left",
                    "standing_side_reach_lat"
                )
                2L -> listOf(
                    "supine_knees_to_chest",
                    "figure_four_glute_right",
                    "figure_four_glute_left",
                    "reclining_spinal_twist_right",
                    "reclining_spinal_twist_left",
                    "seated_butterfly",
                    "puppy_pose_melting_heart",
                    "child_pose_full_body"
                )
                3L -> listOf(
                    "chin_tuck_levator",
                    "doorway_chest_opener",
                    "cross_body_shoulder_right",
                    "cross_body_shoulder_left",
                    "wrist_flexor_extension",
                    "wrist_extensor_flexion",
                    "seated_spinal_twist",
                    "single_leg_hamstring_heel"
                )
                4L -> listOf(
                    "neck_side_right",
                    "doorway_chest_opener",
                    "overhead_triceps_lat",
                    "cat_cow_spinal_flow",
                    "hip_flexor_lunge_right",
                    "hip_flexor_lunge_left",
                    "pigeon_pose_right",
                    "standing_hamstring_fold",
                    "wall_calf_gastrocnemius_right",
                    "child_pose_full_body"
                )
                5L -> listOf(
                    "standing_quad_right",
                    "standing_quad_left",
                    "standing_hamstring_fold",
                    "hip_flexor_lunge_right",
                    "figure_four_glute_right",
                    "wall_calf_gastrocnemius_right",
                    "wall_calf_gastrocnemius_left",
                    "downward_dog_pedal"
                )
                else -> emptyList()
            }

            return libraryIds.mapIndexedNotNull { index, libId ->
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
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE stretches ADD COLUMN indian_name TEXT")
                db.execSQL("ALTER TABLE stretches ADD COLUMN custom_image_path TEXT")
                db.execSQL("ALTER TABLE library_stretches ADD COLUMN indian_name TEXT")
                db.execSQL("ALTER TABLE library_stretches ADD COLUMN custom_image_path TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stretch_coach_database"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
