package com.example.data

data class LibraryStretch(
    val id: String,
    val name: String,
    val category: StretchCategory,
    val defaultPrepSeconds: Int,
    val defaultHoldSeconds: Int,
    val prepCue: String,
    val holdCue: String,
    val benefits: String,
    val indianName: String? = null
)

enum class StretchCategory(val title: String) {
    YOGA_ASANAS("Yoga Asanas"),
    NECK_SHOULDERS("Neck & Shoulders"),
    CHEST_UPPER_BACK("Chest & Upper Back"),
    LOWER_BACK_CORE("Lower Back & Core"),
    HIPS_GLUTES("Hips & Glutes"),
    HAMSTRINGS_QUADS("Hamstrings & Quads"),
    CALVES_FEET("Calves & Feet"),
    ARMS_WRISTS("Arms & Wrists")
}

object StretchLibrary {
    val ALL_100_STRETCHES: List<LibraryStretch> = listOf(
        // --- 1. NECK & SHOULDERS (1-8) ---
        LibraryStretch(
            id = "neck_side_right",
            name = "Neck Side Stretch (Right)",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 25,
            prepCue = "Lower your right ear toward your right shoulder without lifting your left shoulder.",
            holdCue = "Breathe gently into the left side of your neck. Let gravity do the work and relax your jaw.",
            benefits = "Relieves cervical neck tension and upper trapezius tightness from desk work."
        ),
        LibraryStretch(
            id = "neck_side_left",
            name = "Neck Side Stretch (Left)",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 25,
            prepCue = "Gently tilt your left ear toward your left shoulder while keeping shoulders relaxed.",
            holdCue = "Feel the soothing release down the right side of your neck. Take smooth, calm breaths.",
            benefits = "Balances neck mobility and reduces headache tension."
        ),
        LibraryStretch(
            id = "chin_tuck_levator",
            name = "Chin Tuck & Levator Stretch",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Tuck your chin toward your collarbone and look gently toward your right armpit.",
            holdCue = "Feel a deep release down the back corner of your neck. Keep your shoulders heavy and down.",
            benefits = "Targets the levator scapulae muscle to eliminate shoulder knot soreness."
        ),
        LibraryStretch(
            id = "cross_body_shoulder_right",
            name = "Cross-Body Shoulder Stretch (Right)",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Bring your right arm across your chest, supporting gently at the forearm with your left hand.",
            holdCue = "Keep your right shoulder dropped away from your ear. Breathe into the back of your shoulder.",
            benefits = "Improves posterior shoulder capsule flexibility and arm reach."
        ),
        LibraryStretch(
            id = "cross_body_shoulder_left",
            name = "Cross-Body Shoulder Stretch (Left)",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Draw your left arm straight across your chest and hold softly above the elbow.",
            holdCue = "Feel the broad stretch across your rear deltoid and upper back. Maintain relaxed posture.",
            benefits = "Restores shoulder symmetry and eases upper body stiffness."
        ),
        LibraryStretch(
            id = "overhead_triceps_lat",
            name = "Overhead Triceps & Lat Reach",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Reach one hand behind your neck and gently guide your elbow with the opposite hand.",
            holdCue = "Lengthen through your ribs and side torso. Keep your head upright without tucking your chin.",
            benefits = "Lengthens triceps brachii and latissimus dorsi for full overhead mobility."
        ),
        LibraryStretch(
            id = "eagle_arms_stretch",
            name = "Eagle Arms Scapular Spread",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 30,
            prepCue = "Cross your elbows and intertwine forearms, bringing palms or back of hands together.",
            holdCue = "Lift elbows to shoulder height and gently press forearms forward to open between your shoulder blades.",
            benefits = "Spreads the rhomboids and mid-trapezius to release mid-back stiffness."
        ),
        LibraryStretch(
            id = "scm_neck_turn",
            name = "Sternocleidomastoid (SCM) Neck Stretch",
            category = StretchCategory.NECK_SHOULDERS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 25,
            prepCue = "Turn your chin 45 degrees to the side and tilt your head slightly upward.",
            holdCue = "Feel the opening across the front diagonal band of your neck. Breathe slowly and evenly.",
            benefits = "Combats forward head posture caused by phone and computer use."
        ),

        // --- 2. CHEST & UPPER BACK (9-16) ---
        LibraryStretch(
            id = "doorway_chest_opener",
            name = "Doorway / Wall Pectoral Opener",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 35,
            prepCue = "Place your forearm flat against a wall at 90 degrees and step gently forward.",
            holdCue = "Expand your chest with deep breaths. Feel the pectorals open without arching your lower back.",
            benefits = "Counters slouching and opens restricted anterior shoulder ligaments."
        ),
        LibraryStretch(
            id = "interlaced_chest_expansion",
            name = "Interlaced Chest Expansion",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Clasp your fingers behind your lower back and roll your shoulders open.",
            holdCue = "Gently lift your knuckles away from your hips. Keep your neck soft and ribs drawn in.",
            benefits = "Broadens collarbones, improves lung capacity, and corrects rounded shoulders."
        ),
        LibraryStretch(
            id = "child_pose_lat_reach",
            name = "Child's Pose with Lat Reach",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 40,
            prepCue = "Kneel, sink hips back to heels, and walk both hands over to the right side of your mat.",
            holdCue = "Press into your left palm and breathe into your left ribcage. Feel the full length of your side body.",
            benefits = "Decompresses the entire latissimus dorsi and thoracic spine."
        ),
        LibraryStretch(
            id = "puppy_pose_melting_heart",
            name = "Puppy Pose (Melting Heart)",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 40,
            prepCue = "Keep hips stacked over knees while walking hands forward, melting your chest toward the floor.",
            holdCue = "Rest forehead or chin down. Allow thoracic spine and shoulders to soften toward the ground.",
            benefits = "Deeply opens the thoracic spine and front shoulders."
        ),
        LibraryStretch(
            id = "cat_cow_spinal_flow",
            name = "Cat-Cow Spine Mobilizer",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 45,
            prepCue = "Start on all fours with wrists below shoulders and knees below hips.",
            holdCue = "Inhale to arch gently and lift gaze, exhale to round spine like a cat. Move slowly with breath.",
            benefits = "Lubricates all 24 articulating vertebrae and improves spinal fluid circulation."
        ),
        LibraryStretch(
            id = "thread_the_needle_right",
            name = "Thread the Needle (Right Side)",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 35,
            prepCue = "From tabletop, slide your right arm under your chest, resting your right shoulder on the mat.",
            holdCue = "Extend left arm forward or wrap it behind. Feel thoracic rotation and shoulder decompression.",
            benefits = "Restores rotational mobility to the mid-back and ribs."
        ),
        LibraryStretch(
            id = "thread_the_needle_left",
            name = "Thread the Needle (Left Side)",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "From tabletop, slide your left arm under your chest, resting your left shoulder on the mat.",
            holdCue = "Let gravity twist your upper torso gently. Keep your hips centered over your knees.",
            benefits = "Relieves chronic knots between the scapula and spine."
        ),
        LibraryStretch(
            id = "standing_side_arc",
            name = "Standing Side-Bend Arc",
            category = StretchCategory.CHEST_UPPER_BACK,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Stand tall, reach your right arm overhead, and side bend gently toward the left.",
            holdCue = "Anchor down through both feet. Feel the expansive stretch from hip bone to fingertips.",
            benefits = "Stretches intercostal rib muscles and quadratus lumborum."
        ),

        // --- 3. LOWER BACK & CORE (17-23) ---
        LibraryStretch(
            id = "single_knee_to_chest",
            name = "Single Knee-to-Chest Decompression",
            category = StretchCategory.LOWER_BACK_CORE,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Lie flat, hug your right knee into your chest while extending your left leg straight.",
            holdCue = "Keep left thigh grounded. Feel the gentle hip flexion and lumbar release.",
            benefits = "Isolates unilateral lower back tension and calms sacroiliac joint discomfort."
        ),
        LibraryStretch(
            id = "sphinx_pose_extension",
            name = "Sphinx Pose Lumbar Extension",
            category = StretchCategory.LOWER_BACK_CORE,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 40,
            prepCue = "Lie on your belly and prop onto your forearms with elbows directly under shoulders.",
            holdCue = "Gently draw your chest forward between your arms. Keep glutes and lower back relaxed.",
            benefits = "Restores natural lumbar lordosis and combats sitting-induced disc pressure."
        ),

        // --- 4. HIPS & GLUTES (24-32) ---
        LibraryStretch(
            id = "figure_four_glute_right",
            name = "Reclining Figure-4 Glute (Right)",
            category = StretchCategory.HIPS_GLUTES,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 40,
            prepCue = "Lie on your back, cross right ankle over left knee, and reach through to hold left hamstring.",
            holdCue = "Flex right foot to protect knee. Gently pull left thigh closer to feel right outer glute.",
            benefits = "Relieves piriformis tightness and sciatic nerve compression."
        ),
        LibraryStretch(
            id = "figure_four_glute_left",
            name = "Reclining Figure-4 Glute (Left)",
            category = StretchCategory.HIPS_GLUTES,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 40,
            prepCue = "Cross left ankle over right knee, grasp behind right thigh, and draw legs toward chest.",
            holdCue = "Keep head and shoulders flat on mat. Maintain deep rhythmic breaths into left hip.",
            benefits = "Releases gluteus medius and deep lateral hip rotators."
        ),
        LibraryStretch(
            id = "lizard_lunge_hip",
            name = "Lizard Lunge Inside Hip Opener",
            category = StretchCategory.HIPS_GLUTES,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 35,
            prepCue = "From a low lunge, place both hands inside your front foot and walk foot slightly wide.",
            holdCue = "Sink hips forward and down. Keep chest open and spine long.",
            benefits = "Combines hip flexor elongation with adductor release."
        ),
        LibraryStretch(
            id = "ninety_ninety_hip",
            name = "90-90 Seated Hip Rotator Stretch",
            category = StretchCategory.HIPS_GLUTES,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 35,
            prepCue = "Sit with front leg bent 90 degrees in front and back leg bent 90 degrees to the side.",
            holdCue = "Sit upright with flat back and hinge forward over front shin to load external rotators.",
            benefits = "Enhances internal and external hip rotational range of motion."
        ),

        // --- 5. HAMSTRINGS & QUADS (33-40) ---
        LibraryStretch(
            id = "single_leg_hamstring_heel",
            name = "Single-Leg Heel Hamstring Stretch",
            category = StretchCategory.HAMSTRINGS_QUADS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Place one heel forward with toes pointed up, bend back knee and hinge hips back.",
            holdCue = "Keep spine straight and reach hands toward front toes. Feel focused hamstring stretch.",
            benefits = "Isolates hamstring bellies without putting strain on lumbar discs."
        ),
        LibraryStretch(
            id = "standing_quad_right",
            name = "Standing Quad Stretch (Right Leg)",
            category = StretchCategory.HAMSTRINGS_QUADS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Stand tall, hold your right ankle behind you with right hand, drawing heel to glute.",
            holdCue = "Keep both knees aligned and pelvis tucked under. Stand tall through your crown.",
            benefits = "Stretches rectus femoris and improves knee extensor flexibility."
        ),
        LibraryStretch(
            id = "standing_quad_left",
            name = "Standing Quad Stretch (Left Leg)",
            category = StretchCategory.HAMSTRINGS_QUADS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 35,
            prepCue = "Grasp your left ankle behind you, bringing heel toward your glute. Use a wall for balance if needed.",
            holdCue = "Avoid overarching your lower back. Feel the smooth stretch down the front of left thigh.",
            benefits = "Promotes knee joint longevity and patellar tracking health."
        ),
        LibraryStretch(
            id = "side_lying_quad",
            name = "Side-Lying Quad & Hip Stretch",
            category = StretchCategory.HAMSTRINGS_QUADS,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 35,
            prepCue = "Lie on your side, hold top ankle, and gently draw your knee back in line with torso.",
            holdCue = "Engage top glute slightly to intensify front thigh stretch while keeping lower back neutral.",
            benefits = "Zero-gravity quad stretch ideal for sensitive knees or poor balance."
        ),

        // --- 6. CALVES & FEET (41-45) ---
        LibraryStretch(
            id = "wall_calf_gastrocnemius_right",
            name = "Wall Calf Stretch (Right Leg Back)",
            category = StretchCategory.CALVES_FEET,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Step right foot straight back, press heel flat to floor, and lean hands against wall.",
            holdCue = "Keep right knee completely straight and hips facing forward. Feel the upper calf stretch.",
            benefits = "Lengthens gastrocnemius to prevent Achilles tendonitis and shin splints."
        ),
        LibraryStretch(
            id = "wall_calf_gastrocnemius_left",
            name = "Wall Calf Stretch (Left Leg Back)",
            category = StretchCategory.CALVES_FEET,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 35,
            prepCue = "Step left foot back, plant the heel firmly into ground, and bend front knee forward.",
            holdCue = "Maintain a strong straight back leg. Breathe into the calf and Achilles.",
            benefits = "Improves walking stride efficiency and reduces lower leg cramps."
        ),
        LibraryStretch(
            id = "wall_soleus_bent_knee",
            name = "Soleus Bent-Knee Calf Stretch",
            category = StretchCategory.CALVES_FEET,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Stand close to wall, step one foot back, and bend both knees with back heel pressed down.",
            holdCue = "Feel the stretch shift deeper toward your lower calf and Achilles tendon.",
            benefits = "Targets the deeper postural soleus muscle for ankle dorsiflexion."
        ),
        LibraryStretch(
            id = "downward_dog_pedal",
            name = "Downward Dog Calf Pedaling",
            category = StretchCategory.CALVES_FEET,
            defaultPrepSeconds = 10,
            defaultHoldSeconds = 40,
            prepCue = "From hands and knees, lift hips high into an inverted V shape with arms pressing away.",
            holdCue = "Alternate pressing each heel toward the floor in slow, rhythmic pedaling motions.",
            benefits = "Dynamic stretch for calves, hamstrings, and thoracic shoulders."
        ),
        LibraryStretch(
            id = "plantar_fascia_toe_tuck",
            name = "Kneeling Toe Tuck Foot Stretch",
            category = StretchCategory.CALVES_FEET,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Kneel with all 10 toes tucked under, slowly sit weight back onto heels.",
            holdCue = "Keep spine upright. Support weight with hands on floor if sensation is too intense.",
            benefits = "Stimulates plantar fascia, strengthens foot arches, and prevents heel spurs."
        ),

        // --- 7. ARMS & WRISTS (46-50) ---
        LibraryStretch(
            id = "wrist_flexor_extension",
            name = "Wrist Flexor Stretch (Palm Up)",
            category = StretchCategory.ARMS_WRISTS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 25,
            prepCue = "Extend one arm with elbow straight, palm facing up, and pull fingers back with opposite hand.",
            holdCue = "Keep shoulder relaxed down. Feel the clean opening along the inside of your forearm.",
            benefits = "Prevents carpal tunnel syndrome and forearm tightness from typing or lifting."
        ),
        LibraryStretch(
            id = "wrist_extensor_flexion",
            name = "Wrist Extensor Stretch (Palm Down)",
            category = StretchCategory.ARMS_WRISTS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 25,
            prepCue = "Extend arm straight with palm down, gently press fingers down and toward your body.",
            holdCue = "Feel the stretch over the top of your wrist and outer forearm. Breathe smoothly.",
            benefits = "Alleviates tennis elbow symptoms and relieves repetitive strain."
        ),
        LibraryStretch(
            id = "wrist_rotations_tendon",
            name = "Wrist Figure-Eight Fluid Mobilizer",
            category = StretchCategory.ARMS_WRISTS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Clasp your fingers together in front of your chest and relax your shoulders.",
            holdCue = "Roll your wrists in slow, smooth figure-eight circles. Switch rotation direction halfway.",
            benefits = "Lubricates wrist carpal joints and relieves typing fatigue."
        ),
        LibraryStretch(
            id = "standing_side_reach_lat",
            name = "Standing Side Lat Reach",
            category = StretchCategory.ARMS_WRISTS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Clasp your hands overhead, turn palms to ceiling, and lean gently to one side.",
            holdCue = "Press up through palms while grounding through feet. Breathe into lateral ribs.",
            benefits = "Decompresses thoracic spine and opens tight shoulder girdles."
        ),

        // --- 8. AUTHENTIC YOGA ASANAS & RESTORATIVE PRACTICES (51-100) ---
        LibraryStretch(
            id = "bhujangasana_cobra_pose",
            name = "Bhujangasana (Cobra Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Lie flat on stomach with tops of feet pressed into mat. Place hands under shoulders with elbows tucked close.",
            holdCue = "Press pubic bone down, inhale and gently lift chest and heart space forward. Roll shoulders down and back.",
            benefits = "Classic Hatha Yoga back extension. Rejuvenates spinal column, stimulates digestive organs, and opens chest."
        ),
        LibraryStretch(
            id = "adho_mukha_svanasana_downward_dog",
            name = "Adho Mukha Svanasana (Downward-Facing Dog)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "From hands and knees, tuck toes, press through knuckles, and send hips high into an inverted V.",
            holdCue = "Lengthen spine, let head hang loose between biceps, and softly draw heels toward the earth.",
            benefits = "Energizing full-body inversion that elongates hamstrings, calves, and decompresses lumbar spine."
        ),
        LibraryStretch(
            id = "balasana_childs_pose",
            name = "Balasana (Child's Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 45,
            prepCue = "Kneel with big toes touching, sit hips onto heels, and bow torso smoothly forward onto mat.",
            holdCue = "Rest forehead on floor. Extend arms long overhead or fold beside feet, surrendering into slow diaphragmatic breaths.",
            benefits = "Profoundly soothing restorative posture. Calms central nervous system and stretches hips and spine."
        ),
        LibraryStretch(
            id = "setu_bandhasana_bridge_pose",
            name = "Setu Bandhasana (Bridge Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Lie on back with knees bent, feet hip-width flat on mat close to sit bones.",
            holdCue = "Drive through heels to lift hips skyward. Roll shoulders under, interlace fingers, and expand chest.",
            benefits = "Strengthens glutes and hamstrings while expanding ribcage, shoulders, and thyroid gland."
        ),
        LibraryStretch(
            id = "trikonasana_triangle_right",
            name = "Trikonasana (Triangle Pose - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Step feet wide, turn right foot out 90 degrees, and extend arms parallel to floor.",
            holdCue = "Hinge from right hip, placing right hand on shin or ankle. Reach left arm high and rotate chest open.",
            benefits = "Deeply opens hamstrings and hips, improves balance, and expands chest cavity."
        ),
        LibraryStretch(
            id = "trikonasana_triangle_left",
            name = "Trikonasana (Triangle Pose - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Turn left foot out 90 degrees, keep right foot angled inward, arms extended wide.",
            holdCue = "Reach left arm forward, hinge down to left leg, and extend right fingers toward ceiling.",
            benefits = "Creates lateral torso symmetry and relieves lower back tightness."
        ),
        LibraryStretch(
            id = "vrikshasana_tree_right",
            name = "Vrikshasana (Tree Pose - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Ground left foot into mat. Place sole of right foot against left inner thigh or calf, avoiding the knee.",
            holdCue = "Bring palms together at heart center. Fix gaze on a still point in front of you and breathe with calm focus.",
            benefits = "Develops mental equanimity, strengthens ankle stabilizers, and opens inner hip adductors."
        ),
        LibraryStretch(
            id = "vrikshasana_tree_left",
            name = "Vrikshasana (Tree Pose - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Root right foot firmly into the earth. Place sole of left foot on right inner thigh or calf.",
            holdCue = "Engage core, lengthen upward through crown of head, and feel steady rooted stability.",
            benefits = "Restores neuromuscular balance, strengthens arches of the foot, and tones core."
        ),
        LibraryStretch(
            id = "ustrasana_camel_pose",
            name = "Ustrasana (Camel Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 25,
            prepCue = "Kneel with knees hip-width apart, hips over knees, hands supporting sacrum fingers downward.",
            holdCue = "Lift sternum high, gently arch backward, reaching fingertips to heels if comfortable. Keep hips pressing forward.",
            benefits = "Deep heart-opening backbend that opens entire front body, quadriceps, and relieves slumped posture."
        ),
        LibraryStretch(
            id = "paschimottanasana_seated_forward_fold",
            name = "Paschimottanasana (Seated Forward Bend)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 40,
            prepCue = "Sit tall with legs straight forward, feet active and flexed. Inhale, lengthen spine overhead.",
            holdCue = "Hinge forward from hip creases, grasping shins, ankles, or feet. Lengthen chest forward with every breath.",
            benefits = "Massages abdominal viscera, stretches entire posterior spine and hamstrings, and promotes inner calm."
        ),
        LibraryStretch(
            id = "janu_sirsasana_right",
            name = "Janu Sirsasana (Head-to-Knee - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Extend right leg forward, bend left knee and rest sole of left foot against right inner thigh.",
            holdCue = "Square chest over right leg, hinge forward reaching for right foot with relaxed shoulders.",
            benefits = "Isolates right hamstring and stretches lower back, flank, and latissimus dorsi."
        ),
        LibraryStretch(
            id = "janu_sirsasana_left",
            name = "Janu Sirsasana (Head-to-Knee - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Extend left leg straight, bend right knee with sole against inner left thigh.",
            holdCue = "Inhale to elongate spine, exhale folding forward over left thigh with smooth, calm breathing.",
            benefits = "Releases asymmetrical lower back tension and safely lengthens hamstring tendons."
        ),
        LibraryStretch(
            id = "baddha_konasana_butterfly",
            name = "Baddha Konasana (Bound Angle Butterfly)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Sit upright, bring soles of feet together in front of pelvis, letting knees open wide to sides.",
            holdCue = "Hold feet with hands, lift through crown of head, and gently draw torso forward while relaxing groin.",
            benefits = "Stimulates pelvic organs, improves hip joint mobility, and relieves tightness from sitting."
        ),
        LibraryStretch(
            id = "supta_baddha_konasana_reclining",
            name = "Supta Baddha Konasana (Reclining Butterfly)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 45,
            prepCue = "Lie on your back, bring soles of feet together, letting knees fall open supported by gravity.",
            holdCue = "Place one hand on heart, one on belly. Allow deep, slow diaphragmatic breaths to soften the pelvic floor.",
            benefits = "Deeply restorative asana. Decreases sympathetic nervous stress and gently opens inner thighs."
        ),
        LibraryStretch(
            id = "ardha_matsyendrasana_right",
            name = "Ardha Matsyendrasana (Half Lord Twist - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Seated, cross right foot over left thigh onto floor. Inhale left arm overhead.",
            holdCue = "Hook left elbow outside right knee, place right hand behind hip, and rotate chest smoothly to the right.",
            benefits = "Detoxifying spinal twist that stimulates digestion (agni) and preserves thoracic rotational mobility."
        ),
        LibraryStretch(
            id = "ardha_matsyendrasana_left",
            name = "Ardha Matsyendrasana (Half Lord Twist - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Cross left foot outside right knee, right leg bent on mat. Inhale right arm up.",
            holdCue = "Wrap right elbow around left knee, rotate ribcage to the left while keeping both sit bones grounded.",
            benefits = "Restores rotational balance to vertebral columns and eases lumbar compression."
        ),
        LibraryStretch(
            id = "gomukhasana_arms_right",
            name = "Gomukhasana (Cow Face Arms - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Reach right elbow up and drop right hand behind neck. Sweep left hand behind back toward shoulder blades.",
            holdCue = "Clasp fingertips or hold clothing. Keep chest broad, spine tall, and chin level with floor.",
            benefits = "Simultaneously opens internal and external shoulder rotators, counteracting desk hunch."
        ),
        LibraryStretch(
            id = "gomukhasana_arms_left",
            name = "Gomukhasana (Cow Face Arms - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Reach left elbow overhead, drop left palm down spine. Reach right hand up behind lower back.",
            holdCue = "Breathe deeply into ribcage and armpits. Keep neck elongated without hunching.",
            benefits = "Balances rotator cuff muscles and relieves tightness throughout upper back and triceps."
        ),
        LibraryStretch(
            id = "malasana_garland_yogi_squat",
            name = "Malasana (Yogi Squat)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Stand with feet wider than hips, toes angled out. Bend knees and descend into a deep squat.",
            holdCue = "Bring palms together at heart, gently press elbows against inner knees to broaden collarbones.",
            benefits = "Releases pelvic floor, opens deep hip flexors, and stretches calves and Achilles tendons."
        ),
        LibraryStretch(
            id = "uttanasana_forward_fold",
            name = "Uttanasana (Standing Forward Fold)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Stand with feet hip-width apart. Exhale, hinge forward from hips, and fold torso down.",
            holdCue = "Keep knees softly bent if needed. Let crown of head and arms hang heavy like a pendulum.",
            benefits = "Calms the brain, lowers heart rate, and lengthens calves, hamstrings, and thoracolumbar spine."
        ),
        LibraryStretch(
            id = "virabhadrasana_one_right",
            name = "Virabhadrasana I (Warrior I - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Step left foot back 3-4 feet at 45 degrees, bend right knee to 90 degrees over ankle. Square hips.",
            holdCue = "Sweep arms overhead with palms facing, root through outer edge of back heel, and lift chest.",
            benefits = "Builds strength in lower extremities while stretching the posterior hip flexor (psoas) and calf."
        ),
        LibraryStretch(
            id = "virabhadrasana_one_left",
            name = "Virabhadrasana I (Warrior I - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Step right foot back at 45 degrees, bend left knee over ankle, facing hips forward.",
            holdCue = "Press through back heel, reach fingertips high, and breathe with focused strength.",
            benefits = "Improves posture, strengthens thighs and back extensors, and opens groin."
        ),
        LibraryStretch(
            id = "virabhadrasana_two_right",
            name = "Virabhadrasana II (Warrior II - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Step feet wide, turn right foot out 90 degrees, bend right knee over ankle, arms parallel to floor.",
            holdCue = "Gaze over right fingertips with relaxed shoulders. Keep torso centered directly over hips.",
            benefits = "Opens hips and chest, strengthens thighs and pelvic floor, and enhances stamina."
        ),
        LibraryStretch(
            id = "virabhadrasana_two_left",
            name = "Virabhadrasana II (Warrior II - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Turn left foot out 90 degrees, bend left knee, and extend arms wide at shoulder level.",
            holdCue = "Press into both feet evenly, sink hips, and cultivate inner grounding and resilience.",
            benefits = "Stretches adductors, chest, and shoulders while improving postural stability."
        ),
        LibraryStretch(
            id = "anjaneyasana_low_lunge_right",
            name = "Anjaneyasana (Crescent Low Lunge - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Step right foot forward between hands, lower left knee to mat, untucking left toes.",
            holdCue = "Lift torso, sweep arms overhead, and sink pelvis forward and down to release front of left hip.",
            benefits = "Intensely opens iliopsoas and rectus femoris, relieving stiffness from prolonged seated work."
        ),
        LibraryStretch(
            id = "anjaneyasana_low_lunge_left",
            name = "Anjaneyasana (Crescent Low Lunge - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Step left foot forward, place right knee down on mat, and lift torso upright.",
            holdCue = "Engage lower abdomen, lift heart upward, and feel the stretch through right anterior hip.",
            benefits = "Releases lumbar shear stress and restores healthy hip extension range of motion."
        ),
        LibraryStretch(
            id = "matsyasana_fish_pose",
            name = "Matsyasana (Fish Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Lie on back, slide hands palms-down beneath buttocks with elbows close to sides.",
            holdCue = "Press forearms down to arch chest up, gently tilting head until crown rests lightly on floor.",
            benefits = "Expands thoracic lungs, opens throat and pectoral muscles, and stimulates thymus."
        ),
        LibraryStretch(
            id = "halasana_plow_pose",
            name = "Halasana (Plow Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Lie on back, lift legs overhead supporting lower back with hands until toes reach floor behind head.",
            holdCue = "Keep weight on top of shoulders, never directly on neck. Breathe smoothly and maintain soft throat.",
            benefits = "Lengthens the entire spinal column, massages internal organs, and fosters deep introspection."
        ),
        LibraryStretch(
            id = "dhanurasana_bow_pose",
            name = "Dhanurasana (Bow Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 25,
            prepCue = "Lie on stomach, bend knees, and reach hands back to hold outer ankles.",
            holdCue = "Inhale, kick shins back into hands, lifting chest and knees off floor like a drawn bow.",
            benefits = "Revitalizes entire nervous system, strengthens back muscles, and opens entire anterior chain."
        ),
        LibraryStretch(
            id = "supta_padangusthasana_right",
            name = "Supta Padangusthasana (Reclining Leg - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Lie on back, raise right leg straight up, looping hands or strap around calf or foot.",
            holdCue = "Keep left leg grounded flat, gently draw right leg toward chest while keeping tailbone rooted.",
            benefits = "Safely lengthens hamstring without spinal flexion, preventing lower back disc stress."
        ),
        LibraryStretch(
            id = "supta_padangusthasana_left",
            name = "Supta Padangusthasana (Reclining Leg - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Lie supine, extend right leg along mat, raise left leg vertical and clasp around foot or calf.",
            holdCue = "Flex left foot, keep neck relaxed, and feel calm lengthening along posterior leg.",
            benefits = "Alleviates sciatic pain, relieves calf tightness, and balances pelvic symmetry."
        ),
        LibraryStretch(
            id = "viparita_karani_legs_wall",
            name = "Viparita Karani (Legs-Up-The-Wall)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 45,
            prepCue = "Sit sideways near wall or mat edge, roll onto back, and extend legs vertically up against wall.",
            holdCue = "Rest arms at sides with palms turned upward. Close eyes and let gravity promote lymphatic drainage.",
            benefits = "Restorative inversion that boosts venous return, calms anxiety, and relieves tired legs."
        ),
        LibraryStretch(
            id = "shavasana_corpse_pose",
            name = "Shavasana (Corpse Restorative Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 60,
            prepCue = "Lie comfortably flat on back, feet slightly apart falling outward naturally, arms relaxed beside body.",
            holdCue = "Release all muscular control, effort, and breath technique. Melt completely into profound silence and restorative peace.",
            benefits = "Integrates all physiological gains, resets the autonomic nervous system, and cultivates deep stillness."
        ),
        LibraryStretch(
            id = "tadasana_mountain_alignment",
            name = "Tadasana (Mountain Pose Alignment)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Stand tall with feet parallel and hip-width. Distribute weight evenly across both soles.",
            holdCue = "Engage thighs, softly tuck tailbone, roll shoulders down, and reach crown of head high.",
            benefits = "Foundational standing asana that improves posture, balance, and spatial awareness."
        ),
        LibraryStretch(
            id = "prasarita_padottanasana_wide_fold",
            name = "Prasarita Padottanasana (Wide-Leg Fold)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Stand with feet 3-4 feet apart with outer edges parallel to mat edges. Place hands on hips.",
            holdCue = "Hinge from hips to fold torso forward, placing palms on mat. Let crown of head descend smoothly.",
            benefits = "Decompresses lumbar spine, lengthens inner adductor muscles, and relaxes cranial nerves."
        ),
        LibraryStretch(
            id = "garudasana_eagle_pose_right",
            name = "Garudasana (Eagle Pose - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Bend knees, cross right thigh over left, and wrap right elbow under left with forearms bound.",
            holdCue = "Lift elbows to shoulder height, sink hips into a steady quarter squat, and maintain smooth breathing.",
            benefits = "Compresses and opens major joints, expanding scapular rhomboids and hip joint capsule."
        ),
        LibraryStretch(
            id = "garudasana_eagle_pose_left",
            name = "Garudasana (Eagle Pose - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Sink into a slight squat, wrap left thigh over right, and wrap left elbow under right with palms touching.",
            holdCue = "Fix your focus on a single point, keep spine tall, and feel tension release across upper back.",
            benefits = "Builds balance, stabilizes ankles, and decompresses middle and upper spine."
        ),
        LibraryStretch(
            id = "ananda_balasana_happy_baby",
            name = "Ananda Balasana (Happy Baby Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Lie on back, bend knees toward armpits, flex feet, and hold outer edges of feet with hands.",
            holdCue = "Gently draw knees toward floor beside ribs while pressing sacrum and back flat against mat.",
            benefits = "Soothes lower back, releases inner groin and hips, and realigns sacral joints."
        ),
        LibraryStretch(
            id = "salabhasana_locust_pose",
            name = "Salabhasana (Locust Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 25,
            prepCue = "Lie face down with arms alongside body, palms facing down, legs straight behind you.",
            holdCue = "Inhale, peel chest, arms, and legs upward off mat. Lengthen from crown to toes without neck strain.",
            benefits = "Tones posterior spinal extensors, strengthens glutes, and counteracts forward head posture."
        ),
        LibraryStretch(
            id = "urdhva_mukha_svanasana_updog",
            name = "Urdhva Mukha Svanasana (Upward Dog)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 25,
            prepCue = "Lie prone, hands flat on floor beside waist, tops of feet grounded.",
            holdCue = "Press through palms to straighten arms, lifting torso, hips, and knees off mat. Broaden chest.",
            benefits = "Strengthens arms and wrists, relieves sciatica, and expands lung capacity."
        ),
        LibraryStretch(
            id = "parsvottanasana_pyramid_right",
            name = "Parsvottanasana (Pyramid Pose - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Step left foot back 3 feet at 45 degrees, both legs straight, square hips to front.",
            holdCue = "Inhale to lengthen spine, exhale folding torso smoothly forward over right leg toward shin.",
            benefits = "Intensely stretches right hamstring and calf while training pelvic alignment and core stability."
        ),
        LibraryStretch(
            id = "parsvottanasana_pyramid_left",
            name = "Parsvottanasana (Pyramid Pose - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Step right foot back 3 feet, hips squared forward over straight left front leg.",
            holdCue = "Hinge at hips, drawing left hip gently back, and drape torso forward with relaxed breathing.",
            benefits = "Relieves hamstring stiffness and balances postural flexibility between legs."
        ),
        LibraryStretch(
            id = "utkatasana_chair_pose",
            name = "Utkatasana (Chair Pose Stretch)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Stand with feet together. Bend knees deeply as if sinking onto an imaginary low stool.",
            holdCue = "Extend arms overhead alongside ears, sink weight back into heels, and keep chest lifted.",
            benefits = "Builds endurance in quads and ankles while stretching thoracic spine and shoulder girdle."
        ),
        LibraryStretch(
            id = "eka_pada_rajakapotasana_pigeon_right",
            name = "Eka Pada Rajakapotasana (Pigeon - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 40,
            prepCue = "From tabletop, bring right knee behind right wrist, right foot angled across mat.",
            holdCue = "Slide left leg straight back, square hips, and walk hands forward to lower chest toward floor.",
            benefits = "Supreme hip opener that releases deep gluteal tension, piriformis, and sciatica."
        ),
        LibraryStretch(
            id = "eka_pada_rajakapotasana_pigeon_left",
            name = "Eka Pada Rajakapotasana (Pigeon - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 40,
            prepCue = "Place left knee behind left wrist, angle shin, and extend right leg long behind.",
            holdCue = "Square pelvis, rest forehead on folded arms, and breathe deeply into the outer left hip.",
            benefits = "Balances hip mobility, eases lower back stiffness, and calms emotional tension."
        ),
        LibraryStretch(
            id = "supta_matsyendrasana_twist_right",
            name = "Supta Matsyendrasana (Supine Twist - Right)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 35,
            prepCue = "Lie on back, hug right knee to chest, extend right arm out, and guide right knee across body to left.",
            holdCue = "Anchor both shoulders to mat, turn gaze softly to the right, and exhale into the gentle spinal twist.",
            benefits = "Relieves lumbar muscular tightness, massages abdominal organs, and eases stress."
        ),
        LibraryStretch(
            id = "supta_matsyendrasana_twist_left",
            name = "Supta Matsyendrasana (Supine Twist - Left)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 35,
            prepCue = "Lie on back, bring left knee to chest, extend left arm wide, and lower left knee toward the right floor.",
            holdCue = "Breathe into left ribcage and lateral waist. Allow gravity to softly deepen the twist.",
            benefits = "Promotes spinal hydration and balances flexibility between both sides of the back."
        ),
        LibraryStretch(
            id = "goddess_pose_utkata_konasana",
            name = "Utkata Konasana (Goddess Pose)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Step feet wide, turn toes out 45 degrees, bend knees into a wide squat with knees over ankles.",
            holdCue = "Bring arms into cactus shape at 90 degrees, keep spine upright, and open chest proudly.",
            benefits = "Strengthens inner thighs and glutes while opening hips and building lower body endurance."
        ),
        LibraryStretch(
            id = "marjaryasana_cat_pose",
            name = "Marjaryasana (Cat Pose Spinal Flexion)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Start on hands and knees with wrists under shoulders and knees directly below hips.",
            holdCue = "Exhale, press palms into mat, round spine toward ceiling, and let head relax down.",
            benefits = "Releases upper and middle back tension, mobilizes vertebrae, and stretches neck."
        ),
        LibraryStretch(
            id = "bitilasana_cow_pose",
            name = "Bitilasana (Cow Pose Spinal Extension)",
            category = StretchCategory.YOGA_ASANAS,
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "From all fours on mat, prepare to mobilize spine with mindful breath.",
            holdCue = "Inhale, drop belly toward floor, lift chest and sitting bones high, gazing gently forward.",
            benefits = "Restores healthy lordotic lumbar curve, opens chest, and stimulates digestive organs."
        )
    ) + ExtraYogaAsanas.EXTRA_50_ASANAS

    // Legacy English IDs merged into canonical bilingual Sanskrit+English entries
    val LEGACY_DUPLICATE_ID_ALIASES: Map<String, String> = mapOf(
        "supine_knees_to_chest" to "pavanamuktasana_wind_relieving",
        "reclining_spinal_twist_right" to "supta_matsyendrasana_twist_right",
        "reclining_spinal_twist_left" to "supta_matsyendrasana_twist_left",
        "seated_spinal_twist" to "ardha_matsyendrasana_right",
        "cobra_stretch_gentle" to "bhujangasana_cobra_pose",
        "hip_flexor_lunge_right" to "anjaneyasana_low_lunge_right",
        "hip_flexor_lunge_left" to "anjaneyasana_low_lunge_left",
        "seated_butterfly" to "baddha_konasana_butterfly",
        "pigeon_pose_right" to "eka_pada_rajakapotasana_pigeon_right",
        "pigeon_pose_left" to "eka_pada_rajakapotasana_pigeon_left",
        "standing_hamstring_fold" to "uttanasana_forward_fold",
        "seated_single_leg_fold" to "janu_sirsasana_right",
        "wide_legged_straddle_fold" to "prasarita_padottanasana_wide_fold",
        "pyramid_pose_stretch" to "parsvottanasana_pyramid_right",
        "child_pose_full_body" to "balasana_childs_pose"
    )

    // Backwards compatibility aliases
    val ALL_STRETCHES: List<LibraryStretch> = ALL_100_STRETCHES
    val ALL_50_STRETCHES: List<LibraryStretch> = ALL_100_STRETCHES

    fun getById(id: String): LibraryStretch? {
        val canonicalId = LEGACY_DUPLICATE_ID_ALIASES[id] ?: id
        return ALL_100_STRETCHES.find { it.id == canonicalId }
    }

    fun search(query: String, category: StretchCategory? = null): List<LibraryStretch> {
        val q = query.trim().lowercase()
        return ALL_100_STRETCHES.filter { stretch ->
            val matchesCategory = category == null || stretch.category == category
            val matchesQuery = q.isEmpty() ||
                    stretch.name.lowercase().contains(q) ||
                    (stretch.indianName?.lowercase()?.contains(q) == true) ||
                    stretch.category.title.lowercase().contains(q) ||
                    stretch.benefits.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }
}
