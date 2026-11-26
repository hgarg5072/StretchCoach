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
                id = 7L,
                name = "Nisha's Custom Stretch",
                description = "Complete 52-step morning full-body decompression, hip mobility, spine & neck reset.",
                isCustom = false
            ),
            RoutineEntity(
                id = 8L,
                name = "Surya Namaskar (सूर्य नमस्कार)",
                description = "Timer-free continuous Sun Salutation flow with adjustable Rounds (1–24) and playback speed.",
                isCustom = false
            )
        )

        fun createDefaultStretchesForRoutine(routineId: Long): List<StretchEntity> {
            if (routineId == 7L) {
                return createSubahSubahStretches(routineId)
            }
            if (routineId == 8L) {
                return listOf(
                    StretchEntity(
                        routineId = routineId,
                        libraryId = "surya_namaskar_flow",
                        name = "Surya Namaskar (Sun Salutation Flow)",
                        indianName = "सूर्य नमस्कार",
                        category = "Yoga Asanas",
                        preparationSeconds = 0,
                        holdSeconds = 60,
                        prepCue = "Stand at top of mat in Prayer Pose and prepare to flow smoothly with each breath.",
                        holdCue = "Flow continuously through reach, fold, lunge, plank, cobra, downward dog, and return.",
                        notes = "सूर्य नमस्कार • Continuous Flow Mode (adjustable rounds & playback speed)",
                        enabled = true,
                        voiceCueEnabled = true,
                        sortOrder = 0
                    )
                )
            }
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

        private fun createSubahSubahStretches(routineId: Long): List<StretchEntity> {
            // Exact 52-step sequence from user's morning routine recording
            // Format: (name, category, prepSec, holdSec, prepCue, holdCue, notes)
            data class StepSpec(
                val name: String,
                val category: String,
                val prep: Int,
                val hold: Int = 30,
                val prepCue: String = "",
                val holdCue: String = "",
                val notes: String = ""
            )

            val steps = listOf(
                // 1–5: Supine Wind-Relieving & Diagonal Decompression
                StepSpec("Ardha Pavanmuktasana (Left)", "Back & Spine", 5, 30, "Lie on your back and hug your left knee toward your chest.", "Keep right leg extended flat and breathe deeply.", "Decompresses lumbar spine and hip flexors."),
                StepSpec("Ardha Pavanmuktasana Diagonal (Left)", "Back & Spine", 5, 30, "Gently draw your left knee diagonally toward your right shoulder.", "Keep both shoulders grounded on the mat.", "Releases deep glutes and piriformis."),
                StepSpec("Ardha Pavanmuktasana (Right)", "Back & Spine", 5, 30, "Lie on your back and hug your right knee toward your chest.", "Keep left leg extended flat and breathe deeply.", "Decompresses lumbar spine and hip flexors."),
                StepSpec("Ardha Pavanmuktasana Diagonal (Right)", "Back & Spine", 5, 30, "Gently draw your right knee diagonally toward your left shoulder.", "Keep both shoulders grounded on the mat.", "Releases deep glutes and piriformis."),
                StepSpec("Pavanmuktasana", "Back & Spine", 5, 30, "Hug both knees gently into your chest.", "Relax your lower back and tailbone into the mat.", "Full lumbar decompression and digestive relief."),
                // 6–11: Supine Glutes, Hamstrings & Lower Back Twist
                StepSpec("Reclining Figure-4 Glute (Left)", "Hips & Glutes", 5, 30, "Cross left ankle over right knee, grasp behind right thigh, and draw legs toward chest.", "Relax shoulders and breathe into outer left hip.", "Releases gluteus medius and deep lateral hip rotators."),
                StepSpec("Reclining Figure-4 Glute (Right)", "Hips & Glutes", 5, 30, "Cross right ankle over left knee and reach through to hold left hamstring.", "Relax shoulders and breathe into outer right hip.", "Relieves piriformis tightness and sciatic tension."),
                StepSpec("Towel Hamstring Stretch (Left)", "Legs & Hamstrings", 5, 30, "Loop a towel around your left foot and extend the leg straight upward.", "Keep left knee soft and press heel gently toward the ceiling.", "Lengthens hamstrings safely with neutral spine."),
                StepSpec("Towel Hamstring Stretch (Right)", "Legs & Hamstrings", 5, 30, "Loop a towel around your right foot and extend the leg straight upward.", "Keep right knee soft and press heel gently toward the ceiling.", "Lengthens hamstrings safely with neutral spine."),
                StepSpec("Nutraj Lower Back Stretch (Right)", "Back & Spine", 5, 30, "Lie on your back and guide your right knee gently across to the left.", "Keep right shoulder grounded and breathe softly.", "Releases lumbar spine and SI joint tension."),
                StepSpec("Nutraj Lower Back Stretch (Left)", "Back & Spine", 5, 30, "Lie on your back and guide your left knee gently across to the right.", "Keep left shoulder grounded and breathe softly.", "Releases lumbar spine and SI joint tension."),
                // 12–17: Deep Hip Openers & World's Greatest Stretch
                StepSpec("Pigeon Pose (Left Shin Forward)", "Hips & Glutes", 10, 30, "Bring left knee forward, extend right leg straight back, keeping hips level.", "Breathe deeply into your outer left hip.", "Crucial hip mobility for runners and desk workers."),
                StepSpec("Pigeon Pose (Right Shin Forward)", "Hips & Glutes", 10, 30, "Bring right knee behind right wrist and slide left leg straight back behind you.", "Breathe deeply into your outer right hip.", "Deep fascial release for hips, IT band, and glutes."),
                StepSpec("Hip Flexor (Left)", "Hips & Glutes", 5, 30, "Step left foot forward into a kneeling lunge and gently press hips forward.", "Keep torso upright and core gently engaged.", "Opens tight psoas and anterior hip flexors."),
                StepSpec("Hip Flexor (Right)", "Hips & Glutes", 5, 30, "Step right foot forward into a kneeling lunge and gently press hips forward.", "Keep torso upright and core gently engaged.", "Opens tight psoas and anterior hip flexors."),
                StepSpec("World's Greatest Stretch (Left)", "Full Body", 5, 30, "Step left foot into a deep runner's lunge, plant right hand, and rotate left arm skyward.", "Open chest toward the sky and breathe steadily.", "All-in-one hip, hamstring, and thoracic mobility."),
                StepSpec("World's Greatest Stretch (Right)", "Full Body", 5, 30, "Step right foot into a deep runner's lunge, plant left hand, and rotate right arm skyward.", "Open chest toward the sky and breathe steadily.", "All-in-one hip, hamstring, and thoracic mobility."),
                // 18–23: Spine Mobilization, Cobra, Seated Hips & Seated Mountain
                StepSpec("Cat-Cow Spine Mobilizer", "Chest & Upper Back", 8, 30, "Start on all fours with wrists below shoulders and knees below hips.", "Flow slowly between arching and rounding your spine.", "Lubricates all 24 articulating vertebrae."),
                StepSpec("Bhujangasana", "Back & Spine", 5, 30, "Lie on your stomach, palms under shoulders, and gently lift your chest.", "Keep elbows soft and shoulders relaxed away from ears.", "Opens chest and strengthens spinal extensors."),
                StepSpec("Seated Butterfly Adductor Stretch", "Hips & Glutes", 5, 30, "Sit tall, bring soles of feet together, and let knees drop comfortably out to the sides.", "Lengthen spine and relax inner thighs.", "Increases groin flexibility and pelvic floor relaxation."),
                StepSpec("Pancake Stretch", "Hips & Glutes", 5, 30, "Sit with legs wide in a straddle and hinge gently forward from the hips.", "Keep spine long and knees pointing upward.", "Deep inner thigh and hamstring lengthening."),
                StepSpec("Spine Alignment", "Back & Spine", 5, 30, "Sit cross-legged tall and sweep both arms upward in a gentle alternating wave.", "Flow smoothly overhead and wide out to the sides.", "Aligns thoracic spine and frees shoulder mobility."),
                StepSpec("Parvatasana (Seated Mountain)", "Back & Spine", 6, 30, "Sit cross-legged tall and extend both arms straight overhead with palms together.", "Reach gently upward while relaxing your shoulders.", "Decompresses the spine and opens the ribcage."),
                // 24–34: Seated Twists, Scapula, Camel, Child's Pose & Shoulders
                StepSpec("Seated Spinal Twist (Left)", "Back & Spine", 5, 30, "Sit tall with legs crossed, place right hand on left knee, and twist gently left.", "Lengthen your spine on every inhale and soften into the twist.", "Mobilizes thoracic spine and relieves lower-back stiffness."),
                StepSpec("Seated Spinal Twist (Right)", "Back & Spine", 5, 30, "Sit tall with legs crossed, place left hand on right knee, and twist gently right.", "Lengthen your spine on every inhale and soften into the twist.", "Rotational release for mid-back, waist, and obliques."),
                StepSpec("Scapula Stretch (Left)", "Shoulders & Arms", 5, 30, "Sit cross-legged, pull left arm diagonally across with right hand, and fold torso forward.", "Let your left shoulder blade open wide as you exhale.", "Deep scapular protraction and rhomboid release."),
                StepSpec("Scapula Stretch (Right)", "Shoulders & Arms", 5, 30, "Sit cross-legged, pull right arm diagonally across with left hand, and fold torso forward.", "Let your right shoulder blade open wide as you exhale.", "Deep scapular protraction and rhomboid release."),
                StepSpec("Camel Stretch", "Chest & Upper Back", 5, 30, "Kneel tall, place hands on lower back or heels, and lift your chest upward.", "Open your heart gently without straining your neck.", "Deep front-body and hip flexor expansion."),
                StepSpec("Restorative Child's Pose", "Back & Spine", 5, 30, "Kneel wide, sink hips all the way back onto heels, and extend arms long in front.", "Rest forehead softly and breathe into your back.", "Full body grounding and spinal decompression."),
                StepSpec("Child's Pose with Lat Reach (Left)", "Back & Spine", 5, 30, "From Child's Pose, walk both hands over to the left side of your mat.", "Sink your right hip back to lengthen the right lat.", "Decompresses latissimus dorsi and thoracic spine."),
                StepSpec("Child's Pose with Lat Reach (Right)", "Back & Spine", 5, 30, "From Child's Pose, walk both hands over to the right side of your mat.", "Sink your left hip back to lengthen the left lat.", "Decompresses latissimus dorsi and thoracic spine."),
                StepSpec("Shoulder Stretches", "Shoulders & Arms", 6, 30, "Draw one arm gently across your chest or clasp hands behind your back.", "Keep shoulders relaxed away from your ears.", "Relieves posterior deltoid and anterior shoulder tightness."),
                StepSpec("Overhead Triceps & Lat Reach (Left)", "Shoulders & Arms", 5, 30, "Reach left hand behind your neck and gently guide your elbow with your right hand.", "Keep ribs softly drawn in and spine tall.", "Lengthens triceps brachii and latissimus dorsi."),
                StepSpec("Overhead Triceps & Lat Reach (Right)", "Shoulders & Arms", 5, 30, "Reach right hand behind your neck and gently guide your elbow with your left hand.", "Keep ribs softly drawn in and spine tall.", "Lengthens triceps brachii and latissimus dorsi."),
                // 35–43: Multi-Angle Cervical Neck Protocol
                StepSpec("Neck Side Stretch (Left)", "Neck & Shoulders", 6, 30, "Gently tilt your left ear toward your left shoulder while keeping shoulders relaxed.", "Breathe slowly and soften your jaw.", "Balances neck mobility and reduces headache tension."),
                StepSpec("Neck 0 degree (Left)", "Neck & Shoulders", 2, 30, "Pure lateral tilt to the left with chin neutral.", "Breathe deeply and hold steady.", "Targets lateral cervical fibers."),
                StepSpec("Neck 45 degree (Left)", "Neck & Shoulders", 2, 30, "Turn head 45 degrees left and tuck chin gently toward left armpit.", "Breathe deeply and hold steady.", "Targets left levator scapulae."),
                StepSpec("Neck 90 degree (Left)", "Neck & Shoulders", 2, 30, "Rotate chin smoothly 90 degrees over your left shoulder.", "Breathe deeply and hold steady.", "Targets cervical rotators and SCM."),
                StepSpec("Neck Side Stretch (Right)", "Neck & Shoulders", 2, 30, "Lower your right ear toward your right shoulder without lifting your left shoulder.", "Breathe slowly and soften your jaw.", "Relieves cervical neck tension and upper trapezius tightness."),
                StepSpec("Neck 0 degree (Right)", "Neck & Shoulders", 2, 30, "Pure lateral tilt to the right with chin neutral.", "Breathe deeply and hold steady.", "Targets lateral cervical fibers."),
                StepSpec("Neck 45 degree (Right)", "Neck & Shoulders", 2, 30, "Turn head 45 degrees right and tuck chin gently toward right armpit.", "Breathe deeply and hold steady.", "Targets right levator scapulae."),
                StepSpec("Neck 90 degree (Right)", "Neck & Shoulders", 2, 30, "Rotate chin smoothly 90 degrees over your right shoulder.", "Breathe deeply and hold steady.", "Targets cervical rotators and SCM."),
                StepSpec("Chin Tuck & Levator Stretch", "Neck & Shoulders", 2, 30, "Tuck your chin toward your collarbone and look gently toward your armpit.", "Hold gently without forcing the neck.", "Targets levator scapulae to eliminate shoulder knots."),
                // 44–52: Standing Cool-Down & Posture Openers
                StepSpec("Wall Calf Stretch (Left Leg Back)", "Legs & Hamstrings", 5, 30, "Step left foot back, plant the heel firmly into ground, and bend front knee forward.", "Keep back leg straight and lean gently into wall.", "Improves walking stride efficiency and reduces calf tightness."),
                StepSpec("Wall Calf Stretch (Right Leg Back)", "Legs & Hamstrings", 5, 30, "Step right foot straight back, press heel flat to floor, and lean hands against wall.", "Keep back leg straight and breathe steadily.", "Lengthens gastrocnemius to prevent Achilles tendinitis."),
                StepSpec("Standing Quad Stretch (Left Leg)", "Legs & Hamstrings", 5, 30, "Grasp your left ankle behind you, bringing heel toward glute. Use a wall if needed.", "Keep knees close together and stand tall.", "Promotes knee joint longevity and patellar tracking."),
                StepSpec("Standing Quad Stretch (Right Leg)", "Legs & Hamstrings", 6, 30, "Stand tall, hold your right ankle behind you with right hand, drawing heel to glute.", "Keep knees close together and hips neutral.", "Stretches rectus femoris and improves knee extension."),
                StepSpec("Standing Side-Bend Arc (Left)", "Back & Spine", 5, 30, "Stand tall, reach your right arm overhead, and side bend gently toward the left.", "Breathe into your open right ribcage.", "Stretches intercostal rib muscles and quadratus lumborum."),
                StepSpec("Standing Side-Bend Arc (Right)", "Back & Spine", 5, 30, "Stand tall, reach your left arm overhead, and side bend gently toward the right.", "Breathe into your open left ribcage.", "Stretches intercostal rib muscles and quadratus lumborum."),
                StepSpec("Doorway / Wall Pectoral Opener", "Chest & Upper Back", 10, 30, "Place your forearms flat against a wall or doorway at 90 degrees and step gently forward.", "Lift your chest and breathe deeply.", "Counters slouching and opens tight anterior shoulders."),
                StepSpec("Standing Side Lat Reach (Right)", "Back & Spine", 8, 30, "Clasp your hands overhead, turn palms to ceiling, and lean gently to the right.", "Lengthen through your left side body.", "Decompresses thoracic spine and opens tight shoulder girdle."),
                StepSpec("Standing Side Lat Reach (Left)", "Back & Spine", 8, 30, "Clasp your hands overhead, turn palms to ceiling, and lean gently to the left.", "Lengthen through your right side body.", "Decompresses thoracic spine and opens tight shoulder girdle.")
            )

            return steps.mapIndexed { index, spec ->
                StretchEntity(
                    routineId = routineId,
                    libraryId = "subah_subah_${index + 1}",
                    name = spec.name,
                    category = spec.category,
                    preparationSeconds = spec.prep,
                    holdSeconds = spec.hold,
                    prepCue = spec.prepCue,
                    holdCue = spec.holdCue,
                    notes = spec.notes,
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
