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
            ),
            RoutineEntity(
                id = 6L,
                name = "Quick Verify (30)",
                description = "Fast 30-pose showcase (5s prep / 6s hold) covering all 5 posture hubs, L/R mirroring, transitions, and restorative poses.",
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
                6L -> listOf(
                    // Standing + L/R unilateral mirroring
                    "tadasana_mountain_alignment",
                    "virabhadrasana_two_right",
                    "virabhadrasana_two_left",
                    "trikonasana_triangle_right",
                    "trikonasana_triangle_left",
                    "vrikshasana_tree_right",
                    "vrikshasana_tree_left",
                    "standing_hamstring_fold",
                    // 2-hop STANDING -> PRONE (via Kneeling)
                    "bhujangasana_cobra_pose",
                    "dhanurasana_bow_pose",
                    "salabhasana_locust_pose",
                    // PRONE -> KNEELING
                    "cat_cow_spinal_flow",
                    "marjaryasana_cat_pose",
                    "puppy_pose_melting_heart",
                    "ustrasana_camel_pose",
                    "hip_flexor_lunge_right",
                    "hip_flexor_lunge_left",
                    // KNEELING -> SEATED
                    "seated_butterfly",
                    "paschimottanasana_seated_forward_fold",
                    "janu_sirsasana_right",
                    "janu_sirsasana_left",
                    "ardha_matsyendrasana_right",
                    "ardha_matsyendrasana_left",
                    "naukasana_boat",
                    // SEATED -> SUPINE
                    "setu_bandhasana_bridge_pose",
                    "supta_matsyendrasana_twist_right",
                    "supta_matsyendrasana_twist_left",
                    "ananda_balasana_happy_baby",
                    // Restorative & Pranayama (triggers Dog nap state!)
                    "shavasana_corpse_pose",
                    "anulom_vilom_pranayama"
                )
                else -> emptyList()
            }

            val isQuickVerify = routineId == 6L
            return libraryIds.mapIndexedNotNull { index, libId ->
                val lib = StretchLibrary.getById(libId) ?: return@mapIndexedNotNull null
                StretchEntity(
                    routineId = routineId,
                    libraryId = lib.id,
                    name = lib.name,
                    indianName = lib.indianName,
                    category = lib.category.title,
                    preparationSeconds = if (isQuickVerify) 5 else lib.defaultPrepSeconds,
                    holdSeconds = if (isQuickVerify) 6 else lib.defaultHoldSeconds,
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
