package com.example.data.artwork

import com.example.R

/**
 * Modular artwork registry for Batch 1 (Upper body, neck, chest, core, back, arms, wrists + #50).
 * Keys must be normalized lowercase single-spaced strings matching ArtworkMapper.normalizeKey().
 */
object ArtworkBatch1 {
    val exactNameMap: Map<String, Int> = mapOf(
        // #1 Neck Side Stretch (Right)
        "neck side stretch (right)" to R.drawable.img_stretch_b1_neck_side,
        "neck_side_right" to R.drawable.img_stretch_b1_neck_side,
        // #2 Neck Side Stretch (Left)
        "neck side stretch (left)" to R.drawable.img_stretch_b1_neck_side,
        "neck_side_left" to R.drawable.img_stretch_b1_neck_side,
        // #3 Chin Tuck & Levator Stretch
        "chin tuck & levator stretch" to R.drawable.img_stretch_b1_chin_tuck_levator,
        "chin_tuck_levator" to R.drawable.img_stretch_b1_chin_tuck_levator,
        // #4 Cross-Body Shoulder Stretch (Right)
        "cross-body shoulder stretch (right)" to R.drawable.img_stretch_b1_cross_body_shoulder,
        "cross_body_shoulder_right" to R.drawable.img_stretch_b1_cross_body_shoulder,
        // #5 Cross-Body Shoulder Stretch (Left)
        "cross-body shoulder stretch (left)" to R.drawable.img_stretch_b1_cross_body_shoulder,
        "cross_body_shoulder_left" to R.drawable.img_stretch_b1_cross_body_shoulder,
        // #6 Overhead Triceps & Lat Reach
        "overhead triceps & lat reach" to R.drawable.img_stretch_b1_overhead_triceps_lat,
        "overhead_triceps_lat" to R.drawable.img_stretch_b1_overhead_triceps_lat,
        // #7 Eagle Arms Scapular Spread
        "eagle arms scapular spread" to R.drawable.img_stretch_b1_eagle_arms,
        "eagle_arms_stretch" to R.drawable.img_stretch_b1_eagle_arms,
        // #8 Sternocleidomastoid (SCM) Neck Stretch
        "sternocleidomastoid (scm) neck stretch" to R.drawable.img_stretch_b1_scm_neck_turn,
        "scm_neck_turn" to R.drawable.img_stretch_b1_scm_neck_turn,
        // #9 Doorway / Wall Pectoral Opener
        "doorway / wall pectoral opener" to R.drawable.img_stretch_b1_doorway_chest_opener,
        "doorway_chest_opener" to R.drawable.img_stretch_b1_doorway_chest_opener,
        // #10 Interlaced Chest Expansion
        "interlaced chest expansion" to R.drawable.img_stretch_b1_interlaced_chest_expansion,
        "interlaced_chest_expansion" to R.drawable.img_stretch_b1_interlaced_chest_expansion,
        // #11 Child's Pose with Lat Reach
        "child's pose with lat reach" to R.drawable.img_stretch_b1_child_pose_lat_reach,
        "child_pose_lat_reach" to R.drawable.img_stretch_b1_child_pose_lat_reach,
        // #12 Puppy Pose (Melting Heart)
        "puppy pose (melting heart)" to R.drawable.img_stretch_b1_puppy_pose_melting_heart,
        "puppy_pose_melting_heart" to R.drawable.img_stretch_b1_puppy_pose_melting_heart,
        // #14 Thread the Needle (Right Side)
        "thread the needle (right side)" to R.drawable.img_stretch_b1_thread_the_needle,
        "thread_the_needle_right" to R.drawable.img_stretch_b1_thread_the_needle,
        // #15 Thread the Needle (Left Side)
        "thread the needle (left side)" to R.drawable.img_stretch_b1_thread_the_needle,
        "thread_the_needle_left" to R.drawable.img_stretch_b1_thread_the_needle,
        // #16 Standing Side-Bend Arc
        "standing side-bend arc" to R.drawable.img_stretch_b1_standing_side_arc,
        "standing_side_arc" to R.drawable.img_stretch_b1_standing_side_arc,
        // #17 Supine Double Knee Hug
        "supine double knee hug" to R.drawable.img_stretch_b1_supine_knees_to_chest,
        "supine_knees_to_chest" to R.drawable.img_stretch_b1_supine_knees_to_chest,
        // #18 Single Knee-to-Chest Decompression
        "single knee-to-chest decompression" to R.drawable.img_stretch_b1_single_knee_to_chest,
        "single_knee_to_chest" to R.drawable.img_stretch_b1_single_knee_to_chest,
        // #19 Reclining Spinal Twist (Right)
        "reclining spinal twist (right)" to R.drawable.img_stretch_b1_reclining_spinal_twist,
        "reclining_spinal_twist_right" to R.drawable.img_stretch_b1_reclining_spinal_twist,
        // #20 Reclining Spinal Twist (Left)
        "reclining spinal twist (left)" to R.drawable.img_stretch_b1_reclining_spinal_twist,
        "reclining_spinal_twist_left" to R.drawable.img_stretch_b1_reclining_spinal_twist,
        // #21 Seated Torso Spinal Twist
        "seated torso spinal twist" to R.drawable.img_stretch_b1_seated_spinal_twist,
        "seated_spinal_twist" to R.drawable.img_stretch_b1_seated_spinal_twist,
        // #22 Sphinx Pose Lumbar Extension
        "sphinx pose lumbar extension" to R.drawable.img_stretch_b1_sphinx_pose,
        "sphinx_pose_extension" to R.drawable.img_stretch_b1_sphinx_pose,
        // #23 Gentle Cobra Pose
        "gentle cobra pose" to R.drawable.img_stretch_b1_cobra_gentle,
        "cobra_stretch_gentle" to R.drawable.img_stretch_b1_cobra_gentle,
        // #46 Wrist Flexor Stretch (Palm Up)
        "wrist flexor stretch (palm up)" to R.drawable.img_stretch_b1_wrist_flexor,
        "wrist_flexor_extension" to R.drawable.img_stretch_b1_wrist_flexor,
        // #47 Wrist Extensor Stretch (Palm Down)
        "wrist extensor stretch (palm down)" to R.drawable.img_stretch_b1_wrist_extensor,
        "wrist_extensor_flexion" to R.drawable.img_stretch_b1_wrist_extensor,
        // #48 Wrist Figure-Eight Fluid Mobilizer
        "wrist figure-eight fluid mobilizer" to R.drawable.img_stretch_b1_wrist_rotations,
        "wrist_rotations_tendon" to R.drawable.img_stretch_b1_wrist_rotations,
        // #49 Standing Side Lat Reach
        "standing side lat reach" to R.drawable.img_stretch_b1_standing_side_reach_lat,
        "standing_side_reach_lat" to R.drawable.img_stretch_b1_standing_side_reach_lat,
        // #50 Restorative Child's Pose
        "restorative child's pose" to R.drawable.img_stretch_b1_restorative_child_pose,
        "child_pose_full_body" to R.drawable.img_stretch_b1_restorative_child_pose,
        // #13 Cat-Cow Spine Mobilizer
        "cat-cow spine mobilizer" to R.drawable.img_stretch_b1_cat_cow_flow,
        "cat_cow_spinal_flow" to R.drawable.img_stretch_b1_cat_cow_flow
    )
}
