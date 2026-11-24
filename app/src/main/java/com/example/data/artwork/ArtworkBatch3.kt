package com.example.data.artwork

import com.example.R

/**
 * Modular artwork registry for Batch 3 (Yoga Asanas #51-#100).
 * Keys must be normalized lowercase single-spaced strings matching ArtworkMapper.normalizeKey().
 */
object ArtworkBatch3 {
    val exactNameMap: Map<String, Int> = mapOf(
        // #51 Bhujangasana (Cobra Pose)
        "bhujangasana (cobra pose)" to R.drawable.img_stretch_b3_bhujangasana_cobra,
        "bhujangasana_cobra_pose" to R.drawable.img_stretch_b3_bhujangasana_cobra,
        // #54 Setu Bandhasana (Bridge Pose)
        "setu bandhasana (bridge pose)" to R.drawable.img_stretch_b3_setu_bandhasana_bridge,
        "setu_bandhasana_bridge_pose" to R.drawable.img_stretch_b3_setu_bandhasana_bridge,
        // #55 Trikonasana (Triangle Pose - Right)
        "trikonasana (triangle pose - right)" to R.drawable.img_stretch_b3_trikonasana_triangle,
        "trikonasana_triangle_right" to R.drawable.img_stretch_b3_trikonasana_triangle,
        // #56 Trikonasana (Triangle Pose - Left)
        "trikonasana (triangle pose - left)" to R.drawable.img_stretch_b3_trikonasana_triangle,
        "trikonasana_triangle_left" to R.drawable.img_stretch_b3_trikonasana_triangle,
        // #59 Ustrasana (Camel Pose)
        "ustrasana (camel pose)" to R.drawable.img_stretch_b3_ustrasana_camel,
        "ustrasana_camel_pose" to R.drawable.img_stretch_b3_ustrasana_camel,
        // #60 Paschimottanasana (Seated Forward Bend)
        "paschimottanasana (seated forward bend)" to R.drawable.img_stretch_b3_paschimottanasana_forward_fold,
        "paschimottanasana_seated_forward_fold" to R.drawable.img_stretch_b3_paschimottanasana_forward_fold,
        // #61 Janu Sirsasana (Head-to-Knee - Right)
        "janu sirsasana (head-to-knee - right)" to R.drawable.img_stretch_b3_janu_sirsasana,
        "janu_sirsasana_right" to R.drawable.img_stretch_b3_janu_sirsasana,
        // #62 Janu Sirsasana (Head-to-Knee - Left)
        "janu sirsasana (head-to-knee - left)" to R.drawable.img_stretch_b3_janu_sirsasana,
        "janu_sirsasana_left" to R.drawable.img_stretch_b3_janu_sirsasana,
        // #63 Baddha Konasana (Bound Angle Butterfly)
        "baddha konasana (bound angle butterfly)" to R.drawable.img_stretch_b3_baddha_konasana_butterfly,
        "baddha_konasana_butterfly" to R.drawable.img_stretch_b3_baddha_konasana_butterfly,
        // #64 Supta Baddha Konasana (Reclining Butterfly)
        "supta baddha konasana (reclining butterfly)" to R.drawable.img_stretch_b3_supta_baddha_konasana,
        "supta_baddha_konasana_reclining" to R.drawable.img_stretch_b3_supta_baddha_konasana,
        // #65 Ardha Matsyendrasana (Half Lord Twist - Right)
        "ardha matsyendrasana (half lord twist - right)" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
        "ardha_matsyendrasana_right" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
        // #66 Ardha Matsyendrasana (Half Lord Twist - Left)
        "ardha matsyendrasana (half lord twist - left)" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
        "ardha_matsyendrasana_left" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
        // #67 Gomukhasana (Cow Face Arms - Right)
        "gomukhasana (cow face arms - right)" to R.drawable.img_stretch_b3_gomukhasana_arms,
        "gomukhasana_arms_right" to R.drawable.img_stretch_b3_gomukhasana_arms,
        // #68 Gomukhasana (Cow Face Arms - Left)
        "gomukhasana (cow face arms - left)" to R.drawable.img_stretch_b3_gomukhasana_arms,
        "gomukhasana_arms_left" to R.drawable.img_stretch_b3_gomukhasana_arms,
        // #69 Malasana (Yogi Squat)
        "malasana (yogi squat)" to R.drawable.img_stretch_b3_malasana_squat,
        "malasana_garland_yogi_squat" to R.drawable.img_stretch_b3_malasana_squat,
        // #70 Uttanasana (Standing Forward Fold)
        "uttanasana (standing forward fold)" to R.drawable.img_stretch_b3_uttanasana_forward_fold,
        "uttanasana_forward_fold" to R.drawable.img_stretch_b3_uttanasana_forward_fold,
        // #71 Virabhadrasana I (Warrior I - Right)
        "virabhadrasana i (warrior i - right)" to R.drawable.img_stretch_b3_virabhadrasana_one,
        "virabhadrasana_one_right" to R.drawable.img_stretch_b3_virabhadrasana_one,
        // #72 Virabhadrasana I (Warrior I - Left)
        "virabhadrasana i (warrior i - left)" to R.drawable.img_stretch_b3_virabhadrasana_one,
        "virabhadrasana_one_left" to R.drawable.img_stretch_b3_virabhadrasana_one,
        // #75 Anjaneyasana (Crescent Low Lunge - Right)
        "anjaneyasana (crescent low lunge - right)" to R.drawable.img_stretch_b3_anjaneyasana_low_lunge,
        "anjaneyasana_low_lunge_right" to R.drawable.img_stretch_b3_anjaneyasana_low_lunge,
        // #76 Anjaneyasana (Crescent Low Lunge - Left)
        "anjaneyasana (crescent low lunge - left)" to R.drawable.img_stretch_b3_anjaneyasana_low_lunge,
        "anjaneyasana_low_lunge_left" to R.drawable.img_stretch_b3_anjaneyasana_low_lunge,
        // #77 Matsyasana (Fish Pose)
        "matsyasana (fish pose)" to R.drawable.img_stretch_b3_matsyasana_fish,
        "matsyasana_fish_pose" to R.drawable.img_stretch_b3_matsyasana_fish,
        // #78 Halasana (Plow Pose)
        "halasana (plow pose)" to R.drawable.img_stretch_b3_halasana_plow,
        "halasana_plow_pose" to R.drawable.img_stretch_b3_halasana_plow,
        // #79 Dhanurasana (Bow Pose)
        "dhanurasana (bow pose)" to R.drawable.img_stretch_b3_dhanurasana_bow,
        "dhanurasana_bow_pose" to R.drawable.img_stretch_b3_dhanurasana_bow,
        // #80 Supta Padangusthasana (Reclining Leg - Right)
        "supta padangusthasana (reclining leg - right)" to R.drawable.img_stretch_b3_supta_padangusthasana,
        "supta_padangusthasana_right" to R.drawable.img_stretch_b3_supta_padangusthasana,
        // #81 Supta Padangusthasana (Reclining Leg - Left)
        "supta padangusthasana (reclining leg - left)" to R.drawable.img_stretch_b3_supta_padangusthasana,
        "supta_padangusthasana_left" to R.drawable.img_stretch_b3_supta_padangusthasana,
        // #82 Viparita Karani (Legs-Up-The-Wall)
        "viparita karani (legs-up-the-wall)" to R.drawable.img_stretch_b3_viparita_karani,
        "viparita_karani_legs_wall" to R.drawable.img_stretch_b3_viparita_karani,
        // #83 Shavasana (Corpse Restorative Pose)
        "shavasana (corpse restorative pose)" to R.drawable.img_stretch_b3_shavasana_corpse,
        "shavasana_corpse_pose" to R.drawable.img_stretch_b3_shavasana_corpse,
        // #85 Prasarita Padottanasana (Wide-Leg Fold)
        "prasarita padottanasana (wide-leg fold)" to R.drawable.img_stretch_b3_prasarita_padottanasana,
        "prasarita_padottanasana_wide_fold" to R.drawable.img_stretch_b3_prasarita_padottanasana,
        // #86 Garudasana (Eagle Pose - Right)
        "garudasana (eagle pose - right)" to R.drawable.img_stretch_b3_garudasana_eagle,
        "garudasana_eagle_pose_right" to R.drawable.img_stretch_b3_garudasana_eagle,
        // #87 Garudasana (Eagle Pose - Left)
        "garudasana (eagle pose - left)" to R.drawable.img_stretch_b3_garudasana_eagle,
        "garudasana_eagle_pose_left" to R.drawable.img_stretch_b3_garudasana_eagle,
        // #88 Ananda Balasana (Happy Baby Pose)
        "ananda balasana (happy baby pose)" to R.drawable.img_stretch_b3_ananda_balasana_happy_baby,
        "ananda_balasana_happy_baby" to R.drawable.img_stretch_b3_ananda_balasana_happy_baby,
        // #89 Salabhasana (Locust Pose)
        "salabhasana (locust pose)" to R.drawable.img_stretch_b3_salabhasana_locust,
        "salabhasana_locust_pose" to R.drawable.img_stretch_b3_salabhasana_locust,
        // #90 Urdhva Mukha Svanasana (Upward Dog)
        "urdhva mukha svanasana (upward dog)" to R.drawable.img_stretch_b3_urdhva_mukha_svanasana,
        "urdhva_mukha_svanasana_updog" to R.drawable.img_stretch_b3_urdhva_mukha_svanasana,
        // #91 Parsvottanasana (Pyramid Pose - Right)
        "parsvottanasana (pyramid pose - right)" to R.drawable.img_stretch_b3_parsvottanasana_pyramid,
        "parsvottanasana_pyramid_right" to R.drawable.img_stretch_b3_parsvottanasana_pyramid,
        // #92 Parsvottanasana (Pyramid Pose - Left)
        "parsvottanasana (pyramid pose - left)" to R.drawable.img_stretch_b3_parsvottanasana_pyramid,
        "parsvottanasana_pyramid_left" to R.drawable.img_stretch_b3_parsvottanasana_pyramid,
        // #93 Utkatasana (Chair Pose Stretch)
        "utkatasana (chair pose stretch)" to R.drawable.img_stretch_b3_utkatasana_chair,
        "utkatasana_chair_pose" to R.drawable.img_stretch_b3_utkatasana_chair,
        // #96 Supta Matsyendrasana (Supine Twist - Right)
        "supta matsyendrasana (supine twist - right)" to R.drawable.img_stretch_b3_supta_matsyendrasana,
        "supta_matsyendrasana_twist_right" to R.drawable.img_stretch_b3_supta_matsyendrasana,
        // #97 Supta Matsyendrasana (Supine Twist - Left)
        "supta matsyendrasana (supine twist - left)" to R.drawable.img_stretch_b3_supta_matsyendrasana,
        "supta_matsyendrasana_twist_left" to R.drawable.img_stretch_b3_supta_matsyendrasana,
        // #98 Utkata Konasana (Goddess Pose)
        "utkata konasana (goddess pose)" to R.drawable.img_stretch_b3_goddess_pose,
        "goddess_pose_utkata_konasana" to R.drawable.img_stretch_b3_goddess_pose,
        // #100 Bitilasana (Cow Pose Spinal Extension)
        "bitilasana (cow pose spinal extension)" to R.drawable.img_stretch_b3_bitilasana_cow,
        "bitilasana_cow_pose" to R.drawable.img_stretch_b3_bitilasana_cow,
        // #94 Eka Pada Rajakapotasana (Pigeon - Right)
        "eka pada rajakapotasana (pigeon - right)" to R.drawable.img_stretch_b2_pigeon_pose,
        "eka_pada_rajakapotasana_pigeon_right" to R.drawable.img_stretch_b2_pigeon_pose,
        // #95 Eka Pada Rajakapotasana (Pigeon - Left)
        "eka pada rajakapotasana (pigeon - left)" to R.drawable.img_stretch_b2_pigeon_pose,
        "eka_pada_rajakapotasana_pigeon_left" to R.drawable.img_stretch_b2_pigeon_pose,
        // #99 Marjaryasana (Cat Pose Spinal Flexion)
        "marjaryasana (cat pose spinal flexion)" to R.drawable.img_stretch_b3_marjaryasana_cat_pose,
        "marjaryasana_cat_pose" to R.drawable.img_stretch_b3_marjaryasana_cat_pose
    )
}
