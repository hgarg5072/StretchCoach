# Complete 150-Stretch Catalog, Symmetry & Canonical Pose Inventory
Generated directly from `app/src/main/java/com/example/data/StretchLibrary.kt` (items 1–100), `app/src/main/java/com/example/data/ExtraYogaAsanas.kt` (items 101–150), `app/src/main/java/com/example/data/ArtworkMapper.kt`, and `app/src/main/java/com/example/audio/SanskritPhonetics.kt`.
## Summary Metrics
- **Total stretches**: **150** (`StretchLibrary.ALL_100_STRETCHES`: 100 items [#1–100]; `ExtraYogaAsanas.EXTRA_50_ASANAS`: 50 items [#101–150])
- **Symmetrical Left/Right pairs**: **32 pairs** (64 stretches: 22 pairs in `StretchLibrary`, 10 pairs in `ExtraYogaAsanas`)
- **Standalone stretches (no L/R partner in catalog)**: **86 stretches** (56 in `StretchLibrary`, 30 in `ExtraYogaAsanas`)
- **Unique canonical poses (merging L/R pairs per R2)**: **118 canonical poses** (32 paired + 86 standalone)
- **Unique anatomical poses (also merging 9 cross-catalog identical pose groups)**: **108 anatomical poses** (saves 10 additional illustrations if cross-catalog identical poses share one illustration)

## Data Model & `indianName` Resolution Notes
- **In-memory catalog model**: `LibraryStretch` (`StretchLibrary.kt:3-13`) with fields `id`, `name`, `category`, `defaultPrepSeconds`, `defaultHoldSeconds`, `prepCue`, `holdCue`, `benefits`, `indianName: String? = null`.
- **Room library entity**: `LibraryStretchEntity` (`LibraryStretchEntity.kt:8-42`) adds `customImagePath` and `isCustom`.
- **Room routine item entity**: `StretchEntity` (`StretchEntity.kt:24-67`) stores `libraryId`, `name`, `indianName`, `customImagePath`, `category`, `prepCue`, `holdCue`, `notes`.
- **Crucial `indianName` distinction**: All 100 `LibraryStretch` entries in `StretchLibrary.kt` leave `indianName = null` in the source literal, whereas all 50 `ExtraYogaAsanas` entries explicitly define Devanagari `indianName`. At runtime, `StretchRepository.checkAndSeedDefaults()` (`StretchRepository.kt:39-40`) populates `LibraryStretchEntity.indianName` via `lib.indianName ?: SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari`, which resolves Devanagari for #35 and #51–100 (51 items in `StretchLibrary`) and leaves #1–34 and #36–50 as `null`. Both values are recorded below.

## Current `ArtworkMapper.kt` Drawable Distribution Across All 150 Stretches
| Drawable | Style Group | Mapped Count | Stretch #s |
|---|---|---:|---|
| `img_stretch_bridge` | Unified Reference (8) | 3 | 54, 106, 141 |
| `img_stretch_butterfly` | Legacy Mixed-Style (15) | 7 | 28, 63, 64, 69, 88, 98, 128 |
| `img_stretch_calf` | Legacy Mixed-Style (15) | 5 | 41, 42, 43, 44, 45 |
| `img_stretch_cat_cow` | Legacy Mixed-Style (15) | 3 | 13, 99, 100 |
| `img_stretch_chest` | Legacy Mixed-Style (15) | 5 | 9, 10, 59, 77, 79 |
| `img_stretch_child` | Unified Reference (8) | 5 | 11, 12, 50, 53, 131 |
| `img_stretch_cobra` | Legacy Mixed-Style (15) | 4 | 23, 51, 89, 90 |
| `img_stretch_downward_dog` | Unified Reference (8) | 1 | 52 |
| `img_stretch_figure_four` | Legacy Mixed-Style (15) | 3 | 26, 27, 48 |
| `img_stretch_floor` | Legacy Mixed-Style (15) | 9 | 17, 18, 22, 78, 82, 83, 109, 110, 111 |
| `img_stretch_hamstring` | Legacy Mixed-Style (15) | 15 | 33, 34, 35, 39, 40, 60, 61, 62, 70, 80, 81, 85, 91, 92, 132 |
| `img_stretch_lotus` | Unified Reference (8) | 12 | 101, 102, 103, 104, 105, 114, 115, 129, 137, 148, 149, 150 |
| `img_stretch_lunge` | Legacy Mixed-Style (15) | 8 | 24, 25, 31, 32, 75, 76, 135, 136 |
| `img_stretch_neck` | Unified Reference (8) | 4 | 1, 2, 3, 8 |
| `img_stretch_pigeon` | Legacy Mixed-Style (15) | 4 | 29, 30, 94, 95 |
| `img_stretch_quad` | Legacy Mixed-Style (15) | 3 | 36, 37, 38 |
| `img_stretch_side_bend` | Legacy Mixed-Style (15) | 6 | 16, 49, 55, 56, 120, 121 |
| `img_stretch_standing` | Unified Reference (8) | 17 | 84, 93, 108, 112, 113, 122, 123, 126, 127, 130, 133, 134, 140, 142, 143, 146, 147 |
| `img_stretch_tree` | Unified Reference (8) | 4 | 57, 58, 124, 125 |
| `img_stretch_twist` | Legacy Mixed-Style (15) | 11 | 14, 15, 19, 20, 21, 65, 66, 96, 97, 138, 139 |
| `img_stretch_upper` | Legacy Mixed-Style (15) | 9 | 4, 5, 6, 7, 67, 68, 86, 87, 107 |
| `img_stretch_warrior` | Unified Reference (8) | 10 | 71, 72, 73, 74, 116, 117, 118, 119, 144, 145 |
| `img_stretch_wrist` | Legacy Mixed-Style (15) | 2 | 46, 47 |

## Potential Matches to the 8 Unified-Style Reference Drawables (for Explorer 3 Cross-Check)
Based on direct visual inspection of the 8 reference `.jpg` files in `app/src/main/res/drawable/` compared against each stretch's `prepCue`/`holdCue`:

| Reference Drawable | Visual Pose Depicted | Exact / Strong Candidate Stretches | Potential / Caveated Candidates | False Positives Currently Mapped Here (Must Unmap) |
|---|---|---|---|---|
| `img_stretch_neck.jpg` | Seated cross-legged, head tilted to left shoulder with left hand gently resting on top/side of head, right fingertips on mat | **#1** `neck_side_right`, **#2** `neck_side_left` *(Caveat: cues do not specify seated or hand-assist, though pose is lateral neck tilt)* | None | **#3** `chin_tuck_levator` (chin tuck + look at armpit), **#8** `scm_neck_turn` (45° turn + upward tilt) |
| `img_stretch_standing.jpg` | Standing upright facing front, feet hip-width apart, arms relaxed at sides, palms slightly forward | **#84** `tadasana_mountain_alignment` (Mountain Pose) | None | **#93** Utkatasana Chair, **#108** Sirsasana Headstand, **#112** Bakasana Crow, **#113** Mayurasana Peacock, **#122–123** Hanumanasana Splits, **#126** Virasana Hero, **#127** Supta Virasana, **#130** Kurmasana Tortoise, **#133–134** Vasisthasana Side Plank, **#140** Upavistha Konasana, **#142–143** Parighasana Gate, **#146** Phalakasana Plank, **#147** Surya Namaskar Flow |
| `img_stretch_downward_dog.jpg` | Inverted-V Downward-Facing Dog, straight arms & legs, both heels reaching toward mat | **#52** `adho_mukha_svanasana_downward_dog` | **#44** `downward_dog_pedal` *(static image vs dynamic alternating heel-pedaling cue; currently mis-mapped to `img_stretch_calf`)* | None |
| `img_stretch_warrior.jpg` | Warrior II: wide stance, front knee bent 90°, back leg straight, torso upright facing side, arms parallel to floor, gaze over front hand | **#73** `virabhadrasana_two_right`, **#74** `virabhadrasana_two_left` | None | **#71–72** Virabhadrasana I, **#116–117** Ardha Chandrasana, **#118–119** Utthita Parsvakonasana, **#144–145** Virabhadrasana III |
| `img_stretch_lotus.jpg` | Seated full Lotus (both feet on opposite thighs soles up), hands on knees in Chin Mudra, eyes closed, upright spine | **#101** `padmasana_lotus` | **#149** `bhramari_pranayama`, **#150** `ujjayi_pranayama` *(cues say sit tall/upright with eyes closed; Lotus is more specific than "sit comfortably", so judge review needed)* | **#102** Ardha Padmasana, **#103** Vajrasana, **#104** Sukhasana, **#105** Siddhasana, **#114** Chaturanga Dandasana, **#115** Dandasana, **#129** Simhasana, **#137** Tolasana, **#148** Anulom Vilom |
| `img_stretch_tree.jpg` | Standing on one leg, opposite foot on inner thigh above knee, palms together in prayer at heart center | **#57** `vrikshasana_tree_right`, **#58** `vrikshasana_tree_left` | None | **#124–125** Natarajasana (Dancer Pose) |
| `img_stretch_bridge.jpg` | Supine, knees bent, feet hip-width flat on mat, hips lifted high, arms flat palms-down beside torso | **#54** `setu_bandhasana_bridge_pose` *(Minor caveat: #54 holdCue mentions interlacing fingers under back, while image shows arms flat on mat)* | None | **#106** Chakrasana (Wheel), **#141** Purvottanasana (Upward Plank) |
| `img_stretch_child.jpg` | Kneeling, hips resting on heels, torso folded forward, forehead on mat, arms extended straight forward | **#50** `child_pose_full_body`, **#53** `balasana_childs_pose`, **#131** `shashankasana_hare` | None | **#11** `child_pose_lat_reach` (hands walked to right side), **#12** `puppy_pose_melting_heart` (hips stacked over knees) |

## Complete 150-Stretch Catalog (Exact Catalog Order)
Columns:
- **# / Source**: 1–100 `StretchLibrary`, 101–150 `ExtraYogaAsanas`
- **ID & Name**: exact `id`, `name`, `category`
- **Indian / Sanskrit Name**: `explicit` (source `indianName` field) and `runtime` (`StretchRepository` + `SanskritPhonetics` Devanagari)
- **Cues**: exact `prepCue` and `holdCue`
- **Current Drawable**: output of `ArtworkMapper.getDrawableForStretch(name, category.title)`
- **Symmetry & Canonical Pose**: `L/R Pair` (with partner `#/id/name`) or `Standalone`, plus `canonicalPoseId` (and cross-catalog anatomical group if applicable)

### 1. Neck Side Stretch (Right)
- **Source**: `StretchLibrary` | **ID**: `neck_side_right` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lower your right ear toward your right shoulder without lifting your left shoulder.
- **holdCue**: Breathe gently into the left side of your neck. Let gravity do the work and relax your jaw.
- **Current Drawable**: `img_stretch_neck`
- **Symmetry**: L/R Pair (partner: **#2** `neck_side_left` — Neck Side Stretch (Left))
- **Canonical Pose ID**: `neck_side_stretch`

### 2. Neck Side Stretch (Left)
- **Source**: `StretchLibrary` | **ID**: `neck_side_left` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Gently tilt your left ear toward your left shoulder while keeping shoulders relaxed.
- **holdCue**: Feel the soothing release down the right side of your neck. Take smooth, calm breaths.
- **Current Drawable**: `img_stretch_neck`
- **Symmetry**: L/R Pair (partner: **#1** `neck_side_right` — Neck Side Stretch (Right))
- **Canonical Pose ID**: `neck_side_stretch`

### 3. Chin Tuck & Levator Stretch
- **Source**: `StretchLibrary` | **ID**: `chin_tuck_levator` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Tuck your chin toward your collarbone and look gently toward your right armpit.
- **holdCue**: Feel a deep release down the back corner of your neck. Keep your shoulders heavy and down.
- **Current Drawable**: `img_stretch_neck`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `chin_tuck_levator`

### 4. Cross-Body Shoulder Stretch (Right)
- **Source**: `StretchLibrary` | **ID**: `cross_body_shoulder_right` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Bring your right arm across your chest, supporting gently at the forearm with your left hand.
- **holdCue**: Keep your right shoulder dropped away from your ear. Breathe into the back of your shoulder.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: L/R Pair (partner: **#5** `cross_body_shoulder_left` — Cross-Body Shoulder Stretch (Left))
- **Canonical Pose ID**: `cross_body_shoulder_stretch`

### 5. Cross-Body Shoulder Stretch (Left)
- **Source**: `StretchLibrary` | **ID**: `cross_body_shoulder_left` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Draw your left arm straight across your chest and hold softly above the elbow.
- **holdCue**: Feel the broad stretch across your rear deltoid and upper back. Maintain relaxed posture.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: L/R Pair (partner: **#4** `cross_body_shoulder_right` — Cross-Body Shoulder Stretch (Right))
- **Canonical Pose ID**: `cross_body_shoulder_stretch`

### 6. Overhead Triceps & Lat Reach
- **Source**: `StretchLibrary` | **ID**: `overhead_triceps_lat` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Reach one hand behind your neck and gently guide your elbow with the opposite hand.
- **holdCue**: Lengthen through your ribs and side torso. Keep your head upright without tucking your chin.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `overhead_triceps_lat`

### 7. Eagle Arms Scapular Spread
- **Source**: `StretchLibrary` | **ID**: `eagle_arms_stretch` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Cross your elbows and intertwine forearms, bringing palms or back of hands together.
- **holdCue**: Lift elbows to shoulder height and gently press forearms forward to open between your shoulder blades.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `eagle_arms_stretch`

### 8. Sternocleidomastoid (SCM) Neck Stretch
- **Source**: `StretchLibrary` | **ID**: `scm_neck_turn` | **Category**: `Neck & Shoulders`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Turn your chin 45 degrees to the side and tilt your head slightly upward.
- **holdCue**: Feel the opening across the front diagonal band of your neck. Breathe slowly and evenly.
- **Current Drawable**: `img_stretch_neck`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `scm_neck_turn`

### 9. Doorway / Wall Pectoral Opener
- **Source**: `StretchLibrary` | **ID**: `doorway_chest_opener` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Place your forearm flat against a wall at 90 degrees and step gently forward.
- **holdCue**: Expand your chest with deep breaths. Feel the pectorals open without arching your lower back.
- **Current Drawable**: `img_stretch_chest`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `doorway_chest_opener`

### 10. Interlaced Chest Expansion
- **Source**: `StretchLibrary` | **ID**: `interlaced_chest_expansion` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Clasp your fingers behind your lower back and roll your shoulders open.
- **holdCue**: Gently lift your knuckles away from your hips. Keep your neck soft and ribs drawn in.
- **Current Drawable**: `img_stretch_chest`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `interlaced_chest_expansion`

### 11. Child's Pose with Lat Reach
- **Source**: `StretchLibrary` | **ID**: `child_pose_lat_reach` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Kneel, sink hips back to heels, and walk both hands over to the right side of your mat.
- **holdCue**: Press into your left palm and breathe into your left ribcage. Feel the full length of your side body.
- **Current Drawable**: `img_stretch_child`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `child_pose_lat_reach`

### 12. Puppy Pose (Melting Heart)
- **Source**: `StretchLibrary` | **ID**: `puppy_pose_melting_heart` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Keep hips stacked over knees while walking hands forward, melting your chest toward the floor.
- **holdCue**: Rest forehead or chin down. Allow thoracic spine and shoulders to soften toward the ground.
- **Current Drawable**: `img_stretch_child`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `puppy_pose_melting_heart`

### 13. Cat-Cow Spine Mobilizer
- **Source**: `StretchLibrary` | **ID**: `cat_cow_spinal_flow` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Start on all fours with wrists below shoulders and knees below hips.
- **holdCue**: Inhale to arch gently and lift gaze, exhale to round spine like a cat. Move slowly with breath.
- **Current Drawable**: `img_stretch_cat_cow`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `cat_cow_spinal_flow`

### 14. Thread the Needle (Right Side)
- **Source**: `StretchLibrary` | **ID**: `thread_the_needle_right` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: From tabletop, slide your right arm under your chest, resting your right shoulder on the mat.
- **holdCue**: Extend left arm forward or wrap it behind. Feel thoracic rotation and shoulder decompression.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#15** `thread_the_needle_left` — Thread the Needle (Left Side))
- **Canonical Pose ID**: `thread_the_needle`

### 15. Thread the Needle (Left Side)
- **Source**: `StretchLibrary` | **ID**: `thread_the_needle_left` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: From tabletop, slide your left arm under your chest, resting your left shoulder on the mat.
- **holdCue**: Let gravity twist your upper torso gently. Keep your hips centered over your knees.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#14** `thread_the_needle_right` — Thread the Needle (Right Side))
- **Canonical Pose ID**: `thread_the_needle`

### 16. Standing Side-Bend Arc
- **Source**: `StretchLibrary` | **ID**: `standing_side_arc` | **Category**: `Chest & Upper Back`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Stand tall, reach your right arm overhead, and side bend gently toward the left.
- **holdCue**: Anchor down through both feet. Feel the expansive stretch from hip bone to fingertips.
- **Current Drawable**: `img_stretch_side_bend`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `standing_side_arc`

### 17. Supine Double Knee Hug
- **Source**: `StretchLibrary` | **ID**: `supine_knees_to_chest` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lie on your back, bend knees, and wrap arms around shins.
- **holdCue**: Gently draw knees toward chest. Keep head and neck relaxed and let lower back flatten.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `supine_knees_to_chest` *(anatomical group: `supine_double_knee_hug_pavanamuktasana`)*

### 18. Single Knee-to-Chest Decompression
- **Source**: `StretchLibrary` | **ID**: `single_knee_to_chest` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lie flat, hug your right knee into your chest while extending your left leg straight.
- **holdCue**: Keep left thigh grounded. Feel the gentle hip flexion and lumbar release.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `single_knee_to_chest`

### 19. Reclining Spinal Twist (Right)
- **Source**: `StretchLibrary` | **ID**: `reclining_spinal_twist_right` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lie on your back, draw knees up, and let them fall to the right while gazing left.
- **holdCue**: Keep both shoulder blades pinned to the floor. Let gravity rinse out spinal tension.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#20** `reclining_spinal_twist_left` — Reclining Spinal Twist (Left))
- **Canonical Pose ID**: `reclining_spinal_twist`

### 20. Reclining Spinal Twist (Left)
- **Source**: `StretchLibrary` | **ID**: `reclining_spinal_twist_left` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Guide both knees softly over to the left side while extending your right arm out.
- **holdCue**: Breathe deeply into your right lower back and belly. Surrender weight into the mat.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#19** `reclining_spinal_twist_right` — Reclining Spinal Twist (Right))
- **Canonical Pose ID**: `reclining_spinal_twist`

### 21. Seated Torso Spinal Twist
- **Source**: `StretchLibrary` | **ID**: `seated_spinal_twist` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Sit tall with legs crossed or extended, place right hand behind you and rotate gently.
- **holdCue**: Inhale to lengthen your crown upward; exhale to deepen the rotation from your core.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `seated_spinal_twist`

### 22. Sphinx Pose Lumbar Extension
- **Source**: `StretchLibrary` | **ID**: `sphinx_pose_extension` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lie on your belly and prop onto your forearms with elbows directly under shoulders.
- **holdCue**: Gently draw your chest forward between your arms. Keep glutes and lower back relaxed.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `sphinx_pose_extension`

### 23. Gentle Cobra Pose
- **Source**: `StretchLibrary` | **ID**: `cobra_stretch_gentle` | **Category**: `Lower Back & Core`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Place palms beneath shoulders, press lightly into hands to lift head and chest.
- **holdCue**: Keep shoulders rolled back and down. Only lift as high as comfortable without pinching.
- **Current Drawable**: `img_stretch_cobra`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `cobra_stretch_gentle` *(anatomical group: `cobra_pose_bhujangasana`)*

### 24. Low Lunge Hip Flexor (Right Forward)
- **Source**: `StretchLibrary` | **ID**: `hip_flexor_lunge_right` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Step right foot forward into a low lunge, lowering your left knee onto the mat.
- **holdCue**: Tuck your tailbone slightly and sink hips gently forward. Feel the front left hip open.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: L/R Pair (partner: **#25** `hip_flexor_lunge_left` — Low Lunge Hip Flexor (Left Forward))
- **Canonical Pose ID**: `low_lunge_hip_flexor`

### 25. Low Lunge Hip Flexor (Left Forward)
- **Source**: `StretchLibrary` | **ID**: `hip_flexor_lunge_left` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Step left foot forward and lower right knee down. Keep torso upright and proud.
- **holdCue**: Squeeze your right glute slightly to deepen the front hip opening. Breathe steadily.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: L/R Pair (partner: **#24** `hip_flexor_lunge_right` — Low Lunge Hip Flexor (Right Forward))
- **Canonical Pose ID**: `low_lunge_hip_flexor`

### 26. Reclining Figure-4 Glute (Right)
- **Source**: `StretchLibrary` | **ID**: `figure_four_glute_right` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lie on your back, cross right ankle over left knee, and reach through to hold left hamstring.
- **holdCue**: Flex right foot to protect knee. Gently pull left thigh closer to feel right outer glute.
- **Current Drawable**: `img_stretch_figure_four`
- **Symmetry**: L/R Pair (partner: **#27** `figure_four_glute_left` — Reclining Figure-4 Glute (Left))
- **Canonical Pose ID**: `reclining_figure_four_glute`

### 27. Reclining Figure-4 Glute (Left)
- **Source**: `StretchLibrary` | **ID**: `figure_four_glute_left` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Cross left ankle over right knee, grasp behind right thigh, and draw legs toward chest.
- **holdCue**: Keep head and shoulders flat on mat. Maintain deep rhythmic breaths into left hip.
- **Current Drawable**: `img_stretch_figure_four`
- **Symmetry**: L/R Pair (partner: **#26** `figure_four_glute_right` — Reclining Figure-4 Glute (Right))
- **Canonical Pose ID**: `reclining_figure_four_glute`

### 28. Seated Butterfly Adductor Stretch
- **Source**: `StretchLibrary` | **ID**: `seated_butterfly` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Sit tall, bring soles of feet together, and let knees drop comfortably out to the sides.
- **holdCue**: Hold ankles, lengthen spine, and hinge forward from hips. Allow inner thighs to soften.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `seated_butterfly` *(anatomical group: `seated_butterfly_baddha_konasana`)*

### 29. Pigeon Pose (Right Shin Forward)
- **Source**: `StretchLibrary` | **ID**: `pigeon_pose_right` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Bring right knee behind right wrist, slide left leg straight back behind you.
- **holdCue**: Square your hips toward the mat. Lower to forearms if comfortable, breathing into right hip.
- **Current Drawable**: `img_stretch_pigeon`
- **Symmetry**: L/R Pair (partner: **#30** `pigeon_pose_left` — Pigeon Pose (Left Shin Forward))
- **Canonical Pose ID**: `pigeon_pose_sl` *(anatomical group: `pigeon_pose_prone`)*

### 30. Pigeon Pose (Left Shin Forward)
- **Source**: `StretchLibrary` | **ID**: `pigeon_pose_left` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Bring left knee forward, extend right leg straight back, keeping hips level.
- **holdCue**: Fold forward softly over your front leg. Let each exhale dissolve deep hip tension.
- **Current Drawable**: `img_stretch_pigeon`
- **Symmetry**: L/R Pair (partner: **#29** `pigeon_pose_right` — Pigeon Pose (Right Shin Forward))
- **Canonical Pose ID**: `pigeon_pose_sl` *(anatomical group: `pigeon_pose_prone`)*

### 31. Lizard Lunge Inside Hip Opener
- **Source**: `StretchLibrary` | **ID**: `lizard_lunge_hip` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: From a low lunge, place both hands inside your front foot and walk foot slightly wide.
- **holdCue**: Sink hips forward and down. Keep chest open and spine long.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `lizard_lunge_hip`

### 32. 90-90 Seated Hip Rotator Stretch
- **Source**: `StretchLibrary` | **ID**: `ninety_ninety_hip` | **Category**: `Hips & Glutes`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Sit with front leg bent 90 degrees in front and back leg bent 90 degrees to the side.
- **holdCue**: Sit upright with flat back and hinge forward over front shin to load external rotators.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `ninety_ninety_hip`

### 33. Standing Forward Hamstring Fold
- **Source**: `StretchLibrary` | **ID**: `standing_hamstring_fold` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Stand with feet hip-width, soften knees, and hinge at hips letting torso hang.
- **holdCue**: Let head and arms hang heavy. Feel hamstrings and calves lengthen with steady breathing.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `standing_hamstring_fold` *(anatomical group: `standing_forward_fold_uttanasana`)*

### 34. Single-Leg Heel Hamstring Stretch
- **Source**: `StretchLibrary` | **ID**: `single_leg_hamstring_heel` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Place one heel forward with toes pointed up, bend back knee and hinge hips back.
- **holdCue**: Keep spine straight and reach hands toward front toes. Feel focused hamstring stretch.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `single_leg_hamstring_heel`

### 35. Seated Single-Leg Fold (Janu Sirsasana)
- **Source**: `StretchLibrary` | **ID**: `seated_single_leg_fold` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`जानु शीर्षासन`
- **prepCue**: Extend one leg long, tuck opposite foot into inner thigh, and reach torso toward extended foot.
- **holdCue**: Lead with your sternum rather than rounding shoulders. Breathe deeply into the back of leg.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `seated_single_leg_fold` *(anatomical group: `head_to_knee_janu_sirsasana`)*

### 36. Standing Quad Stretch (Right Leg)
- **Source**: `StretchLibrary` | **ID**: `standing_quad_right` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Stand tall, hold your right ankle behind you with right hand, drawing heel to glute.
- **holdCue**: Keep both knees aligned and pelvis tucked under. Stand tall through your crown.
- **Current Drawable**: `img_stretch_quad`
- **Symmetry**: L/R Pair (partner: **#37** `standing_quad_left` — Standing Quad Stretch (Left Leg))
- **Canonical Pose ID**: `standing_quad_stretch`

### 37. Standing Quad Stretch (Left Leg)
- **Source**: `StretchLibrary` | **ID**: `standing_quad_left` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Grasp your left ankle behind you, bringing heel toward your glute. Use a wall for balance if needed.
- **holdCue**: Avoid overarching your lower back. Feel the smooth stretch down the front of left thigh.
- **Current Drawable**: `img_stretch_quad`
- **Symmetry**: L/R Pair (partner: **#36** `standing_quad_right` — Standing Quad Stretch (Right Leg))
- **Canonical Pose ID**: `standing_quad_stretch`

### 38. Side-Lying Quad & Hip Stretch
- **Source**: `StretchLibrary` | **ID**: `side_lying_quad` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Lie on your side, hold top ankle, and gently draw your knee back in line with torso.
- **holdCue**: Engage top glute slightly to intensify front thigh stretch while keeping lower back neutral.
- **Current Drawable**: `img_stretch_quad`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `side_lying_quad`

### 39. Wide-Legged Straddle Forward Fold
- **Source**: `StretchLibrary` | **ID**: `wide_legged_straddle_fold` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Step feet 3 to 4 feet apart with toes facing forward, hinge at hips and lower hands to mat.
- **holdCue**: Shift weight slightly toward balls of feet. Relax head and neck completely.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `wide_legged_straddle_fold` *(anatomical group: `wide_legged_forward_fold_prasarita`)*

### 40. Pyramid Pose Alignment Stretch
- **Source**: `StretchLibrary` | **ID**: `pyramid_pose_stretch` | **Category**: `Hamstrings & Quads`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Step one foot forward and one foot back, square hips forward, and fold over front thigh.
- **holdCue**: Keep front leg straight with a micro-bend. Feel the deep upper hamstring release.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `pyramid_pose_stretch` *(anatomical group: `pyramid_pose_parsvottanasana`)*

### 41. Wall Calf Stretch (Right Leg Back)
- **Source**: `StretchLibrary` | **ID**: `wall_calf_gastrocnemius_right` | **Category**: `Calves & Feet`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Step right foot straight back, press heel flat to floor, and lean hands against wall.
- **holdCue**: Keep right knee completely straight and hips facing forward. Feel the upper calf stretch.
- **Current Drawable**: `img_stretch_calf`
- **Symmetry**: L/R Pair (partner: **#42** `wall_calf_gastrocnemius_left` — Wall Calf Stretch (Left Leg Back))
- **Canonical Pose ID**: `wall_calf_gastrocnemius`

### 42. Wall Calf Stretch (Left Leg Back)
- **Source**: `StretchLibrary` | **ID**: `wall_calf_gastrocnemius_left` | **Category**: `Calves & Feet`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Step left foot back, plant the heel firmly into ground, and bend front knee forward.
- **holdCue**: Maintain a strong straight back leg. Breathe into the calf and Achilles.
- **Current Drawable**: `img_stretch_calf`
- **Symmetry**: L/R Pair (partner: **#41** `wall_calf_gastrocnemius_right` — Wall Calf Stretch (Right Leg Back))
- **Canonical Pose ID**: `wall_calf_gastrocnemius`

### 43. Soleus Bent-Knee Calf Stretch
- **Source**: `StretchLibrary` | **ID**: `wall_soleus_bent_knee` | **Category**: `Calves & Feet`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Stand close to wall, step one foot back, and bend both knees with back heel pressed down.
- **holdCue**: Feel the stretch shift deeper toward your lower calf and Achilles tendon.
- **Current Drawable**: `img_stretch_calf`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `wall_soleus_bent_knee`

### 44. Downward Dog Calf Pedaling
- **Source**: `StretchLibrary` | **ID**: `downward_dog_pedal` | **Category**: `Calves & Feet`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: From hands and knees, lift hips high into an inverted V shape with arms pressing away.
- **holdCue**: Alternate pressing each heel toward the floor in slow, rhythmic pedaling motions.
- **Current Drawable**: `img_stretch_calf`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `downward_dog_pedal`

### 45. Kneeling Toe Tuck Foot Stretch
- **Source**: `StretchLibrary` | **ID**: `plantar_fascia_toe_tuck` | **Category**: `Calves & Feet`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Kneel with all 10 toes tucked under, slowly sit weight back onto heels.
- **holdCue**: Keep spine upright. Support weight with hands on floor if sensation is too intense.
- **Current Drawable**: `img_stretch_calf`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `plantar_fascia_toe_tuck`

### 46. Wrist Flexor Stretch (Palm Up)
- **Source**: `StretchLibrary` | **ID**: `wrist_flexor_extension` | **Category**: `Arms & Wrists`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Extend one arm with elbow straight, palm facing up, and pull fingers back with opposite hand.
- **holdCue**: Keep shoulder relaxed down. Feel the clean opening along the inside of your forearm.
- **Current Drawable**: `img_stretch_wrist`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `wrist_flexor_extension`

### 47. Wrist Extensor Stretch (Palm Down)
- **Source**: `StretchLibrary` | **ID**: `wrist_extensor_flexion` | **Category**: `Arms & Wrists`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Extend arm straight with palm down, gently press fingers down and toward your body.
- **holdCue**: Feel the stretch over the top of your wrist and outer forearm. Breathe smoothly.
- **Current Drawable**: `img_stretch_wrist`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `wrist_extensor_flexion`

### 48. Wrist Figure-Eight Fluid Mobilizer
- **Source**: `StretchLibrary` | **ID**: `wrist_rotations_tendon` | **Category**: `Arms & Wrists`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Clasp your fingers together in front of your chest and relax your shoulders.
- **holdCue**: Roll your wrists in slow, smooth figure-eight circles. Switch rotation direction halfway.
- **Current Drawable**: `img_stretch_figure_four`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `wrist_rotations_tendon`

### 49. Standing Side Lat Reach
- **Source**: `StretchLibrary` | **ID**: `standing_side_reach_lat` | **Category**: `Arms & Wrists`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Clasp your hands overhead, turn palms to ceiling, and lean gently to one side.
- **holdCue**: Press up through palms while grounding through feet. Breathe into lateral ribs.
- **Current Drawable**: `img_stretch_side_bend`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `standing_side_reach_lat`

### 50. Restorative Child's Pose
- **Source**: `StretchLibrary` | **ID**: `child_pose_full_body` | **Category**: `Arms & Wrists`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`null`
- **prepCue**: Kneel wide, sink hips all the way back onto heels, and extend arms long in front.
- **holdCue**: Rest forehead on mat. Let your entire body, breath, and mind settle into deep relaxation.
- **Current Drawable**: `img_stretch_child`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `child_pose_full_body` *(anatomical group: `childs_pose_balasana_shashankasana`)*

### 51. Bhujangasana (Cobra Pose)
- **Source**: `StretchLibrary` | **ID**: `bhujangasana_cobra_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`भुजंगासन`
- **prepCue**: Lie flat on stomach with tops of feet pressed into mat. Place hands under shoulders with elbows tucked close.
- **holdCue**: Press pubic bone down, inhale and gently lift chest and heart space forward. Roll shoulders down and back.
- **Current Drawable**: `img_stretch_cobra`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `bhujangasana_cobra_pose` *(anatomical group: `cobra_pose_bhujangasana`)*

### 52. Adho Mukha Svanasana (Downward-Facing Dog)
- **Source**: `StretchLibrary` | **ID**: `adho_mukha_svanasana_downward_dog` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`अधो मुख श्वानासन`
- **prepCue**: From hands and knees, tuck toes, press through knuckles, and send hips high into an inverted V.
- **holdCue**: Lengthen spine, let head hang loose between biceps, and softly draw heels toward the earth.
- **Current Drawable**: `img_stretch_downward_dog`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `adho_mukha_svanasana_downward_dog`

### 53. Balasana (Child's Pose)
- **Source**: `StretchLibrary` | **ID**: `balasana_childs_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`बालासन`
- **prepCue**: Kneel with big toes touching, sit hips onto heels, and bow torso smoothly forward onto mat.
- **holdCue**: Rest forehead on floor. Extend arms long overhead or fold beside feet, surrendering into slow diaphragmatic breaths.
- **Current Drawable**: `img_stretch_child`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `balasana_childs_pose` *(anatomical group: `childs_pose_balasana_shashankasana`)*

### 54. Setu Bandhasana (Bridge Pose)
- **Source**: `StretchLibrary` | **ID**: `setu_bandhasana_bridge_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`सेतु बंधासन`
- **prepCue**: Lie on back with knees bent, feet hip-width flat on mat close to sit bones.
- **holdCue**: Drive through heels to lift hips skyward. Roll shoulders under, interlace fingers, and expand chest.
- **Current Drawable**: `img_stretch_bridge`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `setu_bandhasana_bridge_pose`

### 55. Trikonasana (Triangle Pose - Right)
- **Source**: `StretchLibrary` | **ID**: `trikonasana_triangle_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`त्रिकोणासन`
- **prepCue**: Step feet wide, turn right foot out 90 degrees, and extend arms parallel to floor.
- **holdCue**: Hinge from right hip, placing right hand on shin or ankle. Reach left arm high and rotate chest open.
- **Current Drawable**: `img_stretch_side_bend`
- **Symmetry**: L/R Pair (partner: **#56** `trikonasana_triangle_left` — Trikonasana (Triangle Pose - Left))
- **Canonical Pose ID**: `trikonasana_triangle`

### 56. Trikonasana (Triangle Pose - Left)
- **Source**: `StretchLibrary` | **ID**: `trikonasana_triangle_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`त्रिकोणासन`
- **prepCue**: Turn left foot out 90 degrees, keep right foot angled inward, arms extended wide.
- **holdCue**: Reach left arm forward, hinge down to left leg, and extend right fingers toward ceiling.
- **Current Drawable**: `img_stretch_side_bend`
- **Symmetry**: L/R Pair (partner: **#55** `trikonasana_triangle_right` — Trikonasana (Triangle Pose - Right))
- **Canonical Pose ID**: `trikonasana_triangle`

### 57. Vrikshasana (Tree Pose - Right)
- **Source**: `StretchLibrary` | **ID**: `vrikshasana_tree_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`वृक्षासन`
- **prepCue**: Ground left foot into mat. Place sole of right foot against left inner thigh or calf, avoiding the knee.
- **holdCue**: Bring palms together at heart center. Fix gaze on a still point in front of you and breathe with calm focus.
- **Current Drawable**: `img_stretch_tree`
- **Symmetry**: L/R Pair (partner: **#58** `vrikshasana_tree_left` — Vrikshasana (Tree Pose - Left))
- **Canonical Pose ID**: `vrikshasana_tree`

### 58. Vrikshasana (Tree Pose - Left)
- **Source**: `StretchLibrary` | **ID**: `vrikshasana_tree_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`वृक्षासन`
- **prepCue**: Root right foot firmly into the earth. Place sole of left foot on right inner thigh or calf.
- **holdCue**: Engage core, lengthen upward through crown of head, and feel steady rooted stability.
- **Current Drawable**: `img_stretch_tree`
- **Symmetry**: L/R Pair (partner: **#57** `vrikshasana_tree_right` — Vrikshasana (Tree Pose - Right))
- **Canonical Pose ID**: `vrikshasana_tree`

### 59. Ustrasana (Camel Pose)
- **Source**: `StretchLibrary` | **ID**: `ustrasana_camel_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`उष्ट्रासन`
- **prepCue**: Kneel with knees hip-width apart, hips over knees, hands supporting sacrum fingers downward.
- **holdCue**: Lift sternum high, gently arch backward, reaching fingertips to heels if comfortable. Keep hips pressing forward.
- **Current Drawable**: `img_stretch_chest`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `ustrasana_camel_pose`

### 60. Paschimottanasana (Seated Forward Bend)
- **Source**: `StretchLibrary` | **ID**: `paschimottanasana_seated_forward_fold` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`पश्चिमोत्तानासन`
- **prepCue**: Sit tall with legs straight forward, feet active and flexed. Inhale, lengthen spine overhead.
- **holdCue**: Hinge forward from hip creases, grasping shins, ankles, or feet. Lengthen chest forward with every breath.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `paschimottanasana_seated_forward_fold`

### 61. Janu Sirsasana (Head-to-Knee - Right)
- **Source**: `StretchLibrary` | **ID**: `janu_sirsasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`जानु शीर्षासन`
- **prepCue**: Extend right leg forward, bend left knee and rest sole of left foot against right inner thigh.
- **holdCue**: Square chest over right leg, hinge forward reaching for right foot with relaxed shoulders.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: L/R Pair (partner: **#62** `janu_sirsasana_left` — Janu Sirsasana (Head-to-Knee - Left))
- **Canonical Pose ID**: `janu_sirsasana` *(anatomical group: `head_to_knee_janu_sirsasana`)*

### 62. Janu Sirsasana (Head-to-Knee - Left)
- **Source**: `StretchLibrary` | **ID**: `janu_sirsasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`जानु शीर्षासन`
- **prepCue**: Extend left leg straight, bend right knee with sole against inner left thigh.
- **holdCue**: Inhale to elongate spine, exhale folding forward over left thigh with smooth, calm breathing.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: L/R Pair (partner: **#61** `janu_sirsasana_right` — Janu Sirsasana (Head-to-Knee - Right))
- **Canonical Pose ID**: `janu_sirsasana` *(anatomical group: `head_to_knee_janu_sirsasana`)*

### 63. Baddha Konasana (Bound Angle Butterfly)
- **Source**: `StretchLibrary` | **ID**: `baddha_konasana_butterfly` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`बद्ध कोणासन`
- **prepCue**: Sit upright, bring soles of feet together in front of pelvis, letting knees open wide to sides.
- **holdCue**: Hold feet with hands, lift through crown of head, and gently draw torso forward while relaxing groin.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `baddha_konasana_butterfly` *(anatomical group: `seated_butterfly_baddha_konasana`)*

### 64. Supta Baddha Konasana (Reclining Butterfly)
- **Source**: `StretchLibrary` | **ID**: `supta_baddha_konasana_reclining` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`सुप्त बद्ध कोणासन`
- **prepCue**: Lie on your back, bring soles of feet together, letting knees fall open supported by gravity.
- **holdCue**: Place one hand on heart, one on belly. Allow deep, slow diaphragmatic breaths to soften the pelvic floor.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `supta_baddha_konasana_reclining`

### 65. Ardha Matsyendrasana (Half Lord Twist - Right)
- **Source**: `StretchLibrary` | **ID**: `ardha_matsyendrasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`अर्ध मत्स्येन्द्रासन`
- **prepCue**: Seated, cross right foot over left thigh onto floor. Inhale left arm overhead.
- **holdCue**: Hook left elbow outside right knee, place right hand behind hip, and rotate chest smoothly to the right.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#66** `ardha_matsyendrasana_left` — Ardha Matsyendrasana (Half Lord Twist - Left))
- **Canonical Pose ID**: `ardha_matsyendrasana`

### 66. Ardha Matsyendrasana (Half Lord Twist - Left)
- **Source**: `StretchLibrary` | **ID**: `ardha_matsyendrasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`अर्ध मत्स्येन्द्रासन`
- **prepCue**: Cross left foot outside right knee, right leg bent on mat. Inhale right arm up.
- **holdCue**: Wrap right elbow around left knee, rotate ribcage to the left while keeping both sit bones grounded.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#65** `ardha_matsyendrasana_right` — Ardha Matsyendrasana (Half Lord Twist - Right))
- **Canonical Pose ID**: `ardha_matsyendrasana`

### 67. Gomukhasana (Cow Face Arms - Right)
- **Source**: `StretchLibrary` | **ID**: `gomukhasana_arms_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`गोमुखासन`
- **prepCue**: Reach right elbow up and drop right hand behind neck. Sweep left hand behind back toward shoulder blades.
- **holdCue**: Clasp fingertips or hold clothing. Keep chest broad, spine tall, and chin level with floor.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: L/R Pair (partner: **#68** `gomukhasana_arms_left` — Gomukhasana (Cow Face Arms - Left))
- **Canonical Pose ID**: `gomukhasana_arms`

### 68. Gomukhasana (Cow Face Arms - Left)
- **Source**: `StretchLibrary` | **ID**: `gomukhasana_arms_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`गोमुखासन`
- **prepCue**: Reach left elbow overhead, drop left palm down spine. Reach right hand up behind lower back.
- **holdCue**: Breathe deeply into ribcage and armpits. Keep neck elongated without hunching.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: L/R Pair (partner: **#67** `gomukhasana_arms_right` — Gomukhasana (Cow Face Arms - Right))
- **Canonical Pose ID**: `gomukhasana_arms`

### 69. Malasana (Yogi Squat)
- **Source**: `StretchLibrary` | **ID**: `malasana_garland_yogi_squat` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`मालासन`
- **prepCue**: Stand with feet wider than hips, toes angled out. Bend knees and descend into a deep squat.
- **holdCue**: Bring palms together at heart, gently press elbows against inner knees to broaden collarbones.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `malasana_garland_yogi_squat`

### 70. Uttanasana (Standing Forward Fold)
- **Source**: `StretchLibrary` | **ID**: `uttanasana_forward_fold` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`उत्तानासन`
- **prepCue**: Stand with feet hip-width apart. Exhale, hinge forward from hips, and fold torso down.
- **holdCue**: Keep knees softly bent if needed. Let crown of head and arms hang heavy like a pendulum.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `uttanasana_forward_fold` *(anatomical group: `standing_forward_fold_uttanasana`)*

### 71. Virabhadrasana I (Warrior I - Right)
- **Source**: `StretchLibrary` | **ID**: `virabhadrasana_one_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`वीरभद्रासन एक`
- **prepCue**: Step left foot back 3-4 feet at 45 degrees, bend right knee to 90 degrees over ankle. Square hips.
- **holdCue**: Sweep arms overhead with palms facing, root through outer edge of back heel, and lift chest.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#72** `virabhadrasana_one_left` — Virabhadrasana I (Warrior I - Left))
- **Canonical Pose ID**: `virabhadrasana_1`

### 72. Virabhadrasana I (Warrior I - Left)
- **Source**: `StretchLibrary` | **ID**: `virabhadrasana_one_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`वीरभद्रासन एक`
- **prepCue**: Step right foot back at 45 degrees, bend left knee over ankle, facing hips forward.
- **holdCue**: Press through back heel, reach fingertips high, and breathe with focused strength.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#71** `virabhadrasana_one_right` — Virabhadrasana I (Warrior I - Right))
- **Canonical Pose ID**: `virabhadrasana_1`

### 73. Virabhadrasana II (Warrior II - Right)
- **Source**: `StretchLibrary` | **ID**: `virabhadrasana_two_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`वीरभद्रासन दो`
- **prepCue**: Step feet wide, turn right foot out 90 degrees, bend right knee over ankle, arms parallel to floor.
- **holdCue**: Gaze over right fingertips with relaxed shoulders. Keep torso centered directly over hips.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#74** `virabhadrasana_two_left` — Virabhadrasana II (Warrior II - Left))
- **Canonical Pose ID**: `virabhadrasana_2`

### 74. Virabhadrasana II (Warrior II - Left)
- **Source**: `StretchLibrary` | **ID**: `virabhadrasana_two_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`वीरभद्रासन दो`
- **prepCue**: Turn left foot out 90 degrees, bend left knee, and extend arms wide at shoulder level.
- **holdCue**: Press into both feet evenly, sink hips, and cultivate inner grounding and resilience.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#73** `virabhadrasana_two_right` — Virabhadrasana II (Warrior II - Right))
- **Canonical Pose ID**: `virabhadrasana_2`

### 75. Anjaneyasana (Crescent Low Lunge - Right)
- **Source**: `StretchLibrary` | **ID**: `anjaneyasana_low_lunge_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`आञ्जनेयासन`
- **prepCue**: Step right foot forward between hands, lower left knee to mat, untucking left toes.
- **holdCue**: Lift torso, sweep arms overhead, and sink pelvis forward and down to release front of left hip.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: L/R Pair (partner: **#76** `anjaneyasana_low_lunge_left` — Anjaneyasana (Crescent Low Lunge - Left))
- **Canonical Pose ID**: `anjaneyasana_crescent_low_lunge`

### 76. Anjaneyasana (Crescent Low Lunge - Left)
- **Source**: `StretchLibrary` | **ID**: `anjaneyasana_low_lunge_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`आञ्जनेयासन`
- **prepCue**: Step left foot forward, place right knee down on mat, and lift torso upright.
- **holdCue**: Engage lower abdomen, lift heart upward, and feel the stretch through right anterior hip.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: L/R Pair (partner: **#75** `anjaneyasana_low_lunge_right` — Anjaneyasana (Crescent Low Lunge - Right))
- **Canonical Pose ID**: `anjaneyasana_crescent_low_lunge`

### 77. Matsyasana (Fish Pose)
- **Source**: `StretchLibrary` | **ID**: `matsyasana_fish_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`मत्स्यासन`
- **prepCue**: Lie on back, slide hands palms-down beneath buttocks with elbows close to sides.
- **holdCue**: Press forearms down to arch chest up, gently tilting head until crown rests lightly on floor.
- **Current Drawable**: `img_stretch_chest`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `matsyasana_fish_pose`

### 78. Halasana (Plow Pose)
- **Source**: `StretchLibrary` | **ID**: `halasana_plow_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`हलासन`
- **prepCue**: Lie on back, lift legs overhead supporting lower back with hands until toes reach floor behind head.
- **holdCue**: Keep weight on top of shoulders, never directly on neck. Breathe smoothly and maintain soft throat.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `halasana_plow_pose`

### 79. Dhanurasana (Bow Pose)
- **Source**: `StretchLibrary` | **ID**: `dhanurasana_bow_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`धनुरासन`
- **prepCue**: Lie on stomach, bend knees, and reach hands back to hold outer ankles.
- **holdCue**: Inhale, kick shins back into hands, lifting chest and knees off floor like a drawn bow.
- **Current Drawable**: `img_stretch_chest`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `dhanurasana_bow_pose`

### 80. Supta Padangusthasana (Reclining Leg - Right)
- **Source**: `StretchLibrary` | **ID**: `supta_padangusthasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`सुप्त पादांगुष्ठासन`
- **prepCue**: Lie on back, raise right leg straight up, looping hands or strap around calf or foot.
- **holdCue**: Keep left leg grounded flat, gently draw right leg toward chest while keeping tailbone rooted.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: L/R Pair (partner: **#81** `supta_padangusthasana_left` — Supta Padangusthasana (Reclining Leg - Left))
- **Canonical Pose ID**: `supta_padangusthasana`

### 81. Supta Padangusthasana (Reclining Leg - Left)
- **Source**: `StretchLibrary` | **ID**: `supta_padangusthasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`सुप्त पादांगुष्ठासन`
- **prepCue**: Lie supine, extend right leg along mat, raise left leg vertical and clasp around foot or calf.
- **holdCue**: Flex left foot, keep neck relaxed, and feel calm lengthening along posterior leg.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: L/R Pair (partner: **#80** `supta_padangusthasana_right` — Supta Padangusthasana (Reclining Leg - Right))
- **Canonical Pose ID**: `supta_padangusthasana`

### 82. Viparita Karani (Legs-Up-The-Wall)
- **Source**: `StretchLibrary` | **ID**: `viparita_karani_legs_wall` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`विपरीत करणी`
- **prepCue**: Sit sideways near wall or mat edge, roll onto back, and extend legs vertically up against wall.
- **holdCue**: Rest arms at sides with palms turned upward. Close eyes and let gravity promote lymphatic drainage.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `viparita_karani_legs_wall`

### 83. Shavasana (Corpse Restorative Pose)
- **Source**: `StretchLibrary` | **ID**: `shavasana_corpse_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`शवासन`
- **prepCue**: Lie comfortably flat on back, feet slightly apart falling outward naturally, arms relaxed beside body.
- **holdCue**: Release all muscular control, effort, and breath technique. Melt completely into profound silence and restorative peace.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `shavasana_corpse_pose`

### 84. Tadasana (Mountain Pose Alignment)
- **Source**: `StretchLibrary` | **ID**: `tadasana_mountain_alignment` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`ताड़ासन`
- **prepCue**: Stand tall with feet parallel and hip-width. Distribute weight evenly across both soles.
- **holdCue**: Engage thighs, softly tuck tailbone, roll shoulders down, and reach crown of head high.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `tadasana_mountain_alignment`

### 85. Prasarita Padottanasana (Wide-Leg Fold)
- **Source**: `StretchLibrary` | **ID**: `prasarita_padottanasana_wide_fold` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`प्रसारित पादोत्तानासन`
- **prepCue**: Stand with feet 3-4 feet apart with outer edges parallel to mat edges. Place hands on hips.
- **holdCue**: Hinge from hips to fold torso forward, placing palms on mat. Let crown of head descend smoothly.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `prasarita_padottanasana_wide_fold` *(anatomical group: `wide_legged_forward_fold_prasarita`)*

### 86. Garudasana (Eagle Pose - Right)
- **Source**: `StretchLibrary` | **ID**: `garudasana_eagle_pose_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`गरुड़ासन`
- **prepCue**: Bend knees, cross right thigh over left, and wrap right elbow under left with forearms bound.
- **holdCue**: Lift elbows to shoulder height, sink hips into a steady quarter squat, and maintain smooth breathing.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: L/R Pair (partner: **#87** `garudasana_eagle_pose_left` — Garudasana (Eagle Pose - Left))
- **Canonical Pose ID**: `garudasana_eagle`

### 87. Garudasana (Eagle Pose - Left)
- **Source**: `StretchLibrary` | **ID**: `garudasana_eagle_pose_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`गरुड़ासन`
- **prepCue**: Sink into a slight squat, wrap left thigh over right, and wrap left elbow under right with palms touching.
- **holdCue**: Fix your focus on a single point, keep spine tall, and feel tension release across upper back.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: L/R Pair (partner: **#86** `garudasana_eagle_pose_right` — Garudasana (Eagle Pose - Right))
- **Canonical Pose ID**: `garudasana_eagle`

### 88. Ananda Balasana (Happy Baby Pose)
- **Source**: `StretchLibrary` | **ID**: `ananda_balasana_happy_baby` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`आनंद बालासन`
- **prepCue**: Lie on back, bend knees toward armpits, flex feet, and hold outer edges of feet with hands.
- **holdCue**: Gently draw knees toward floor beside ribs while pressing sacrum and back flat against mat.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `ananda_balasana_happy_baby`

### 89. Salabhasana (Locust Pose)
- **Source**: `StretchLibrary` | **ID**: `salabhasana_locust_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`शलभासन`
- **prepCue**: Lie face down with arms alongside body, palms facing down, legs straight behind you.
- **holdCue**: Inhale, peel chest, arms, and legs upward off mat. Lengthen from crown to toes without neck strain.
- **Current Drawable**: `img_stretch_cobra`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `salabhasana_locust_pose`

### 90. Urdhva Mukha Svanasana (Upward Dog)
- **Source**: `StretchLibrary` | **ID**: `urdhva_mukha_svanasana_updog` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`ऊर्ध्व मुख श्वानासन`
- **prepCue**: Lie prone, hands flat on floor beside waist, tops of feet grounded.
- **holdCue**: Press through palms to straighten arms, lifting torso, hips, and knees off mat. Broaden chest.
- **Current Drawable**: `img_stretch_cobra`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `urdhva_mukha_svanasana_updog`

### 91. Parsvottanasana (Pyramid Pose - Right)
- **Source**: `StretchLibrary` | **ID**: `parsvottanasana_pyramid_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`पार्श्वोत्तानासन`
- **prepCue**: Step left foot back 3 feet at 45 degrees, both legs straight, square hips to front.
- **holdCue**: Inhale to lengthen spine, exhale folding torso smoothly forward over right leg toward shin.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: L/R Pair (partner: **#92** `parsvottanasana_pyramid_left` — Parsvottanasana (Pyramid Pose - Left))
- **Canonical Pose ID**: `parsvottanasana_pyramid` *(anatomical group: `pyramid_pose_parsvottanasana`)*

### 92. Parsvottanasana (Pyramid Pose - Left)
- **Source**: `StretchLibrary` | **ID**: `parsvottanasana_pyramid_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`पार्श्वोत्तानासन`
- **prepCue**: Step right foot back 3 feet, hips squared forward over straight left front leg.
- **holdCue**: Hinge at hips, drawing left hip gently back, and drape torso forward with relaxed breathing.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: L/R Pair (partner: **#91** `parsvottanasana_pyramid_right` — Parsvottanasana (Pyramid Pose - Right))
- **Canonical Pose ID**: `parsvottanasana_pyramid` *(anatomical group: `pyramid_pose_parsvottanasana`)*

### 93. Utkatasana (Chair Pose Stretch)
- **Source**: `StretchLibrary` | **ID**: `utkatasana_chair_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`उत्कटासन`
- **prepCue**: Stand with feet together. Bend knees deeply as if sinking onto an imaginary low stool.
- **holdCue**: Extend arms overhead alongside ears, sink weight back into heels, and keep chest lifted.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `utkatasana_chair_pose`

### 94. Eka Pada Rajakapotasana (Pigeon - Right)
- **Source**: `StretchLibrary` | **ID**: `eka_pada_rajakapotasana_pigeon_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`एक पाद राजकपोतासन`
- **prepCue**: From tabletop, bring right knee behind right wrist, right foot angled across mat.
- **holdCue**: Slide left leg straight back, square hips, and walk hands forward to lower chest toward floor.
- **Current Drawable**: `img_stretch_pigeon`
- **Symmetry**: L/R Pair (partner: **#95** `eka_pada_rajakapotasana_pigeon_left` — Eka Pada Rajakapotasana (Pigeon - Left))
- **Canonical Pose ID**: `eka_pada_rajakapotasana_pigeon` *(anatomical group: `pigeon_pose_prone`)*

### 95. Eka Pada Rajakapotasana (Pigeon - Left)
- **Source**: `StretchLibrary` | **ID**: `eka_pada_rajakapotasana_pigeon_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`एक पाद राजकपोतासन`
- **prepCue**: Place left knee behind left wrist, angle shin, and extend right leg long behind.
- **holdCue**: Square pelvis, rest forehead on folded arms, and breathe deeply into the outer left hip.
- **Current Drawable**: `img_stretch_pigeon`
- **Symmetry**: L/R Pair (partner: **#94** `eka_pada_rajakapotasana_pigeon_right` — Eka Pada Rajakapotasana (Pigeon - Right))
- **Canonical Pose ID**: `eka_pada_rajakapotasana_pigeon` *(anatomical group: `pigeon_pose_prone`)*

### 96. Supta Matsyendrasana (Supine Twist - Right)
- **Source**: `StretchLibrary` | **ID**: `supta_matsyendrasana_twist_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`सुप्त मत्स्येन्द्रासन`
- **prepCue**: Lie on back, hug right knee to chest, extend right arm out, and guide right knee across body to left.
- **holdCue**: Anchor both shoulders to mat, turn gaze softly to the right, and exhale into the gentle spinal twist.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#97** `supta_matsyendrasana_twist_left` — Supta Matsyendrasana (Supine Twist - Left))
- **Canonical Pose ID**: `supta_matsyendrasana_supine_twist`

### 97. Supta Matsyendrasana (Supine Twist - Left)
- **Source**: `StretchLibrary` | **ID**: `supta_matsyendrasana_twist_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`सुप्त मत्स्येन्द्रासन`
- **prepCue**: Lie on back, bring left knee to chest, extend left arm wide, and lower left knee toward the right floor.
- **holdCue**: Breathe into left ribcage and lateral waist. Allow gravity to softly deepen the twist.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#96** `supta_matsyendrasana_twist_right` — Supta Matsyendrasana (Supine Twist - Right))
- **Canonical Pose ID**: `supta_matsyendrasana_supine_twist`

### 98. Utkata Konasana (Goddess Pose)
- **Source**: `StretchLibrary` | **ID**: `goddess_pose_utkata_konasana` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`उत्कट कोणासन`
- **prepCue**: Step feet wide, turn toes out 45 degrees, bend knees into a wide squat with knees over ankles.
- **holdCue**: Bring arms into cactus shape at 90 degrees, keep spine upright, and open chest proudly.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `goddess_pose_utkata_konasana`

### 99. Marjaryasana (Cat Pose Spinal Flexion)
- **Source**: `StretchLibrary` | **ID**: `marjaryasana_cat_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`मार्जारी आसन`
- **prepCue**: Start on hands and knees with wrists under shoulders and knees directly below hips.
- **holdCue**: Exhale, press palms into mat, round spine toward ceiling, and let head relax down.
- **Current Drawable**: `img_stretch_cat_cow`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `marjaryasana_cat_pose`

### 100. Bitilasana (Cow Pose Spinal Extension)
- **Source**: `StretchLibrary` | **ID**: `bitilasana_cow_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`null`; runtime-resolved=`बिटिलासन`
- **prepCue**: From all fours on mat, prepare to mobilize spine with mindful breath.
- **holdCue**: Inhale, drop belly toward floor, lift chest and sitting bones high, gazing gently forward.
- **Current Drawable**: `img_stretch_cat_cow`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `bitilasana_cow_pose`

### 101. Padmasana (Lotus Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `padmasana_lotus` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`पद्मासन`; runtime-resolved=`पद्मासन`
- **prepCue**: Sit tall and gently place each foot onto the opposite thigh with soles facing upward.
- **holdCue**: Rest hands on knees in chin mudra, lengthen your spine, and breathe calmly.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `padmasana_lotus`

### 102. Ardha Padmasana (Half Lotus Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `ardha_padmasana_half_lotus` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`अर्ध पद्मासन`; runtime-resolved=`अर्ध पद्मासन`
- **prepCue**: Sit cross-legged and place one foot gently onto the opposite thigh.
- **holdCue**: Keep both sitting bones grounded, relax shoulders down, and lengthen through the crown.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `ardha_padmasana_half_lotus`

### 103. Vajrasana (Thunderbolt Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `vajrasana_thunderbolt` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`वज्रासन`; runtime-resolved=`वज्रासन`
- **prepCue**: Kneel on the mat with big toes touching and sit your hips back onto your heels.
- **holdCue**: Rest palms on thighs, keep your back upright, and breathe slowly into the belly.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `vajrasana_thunderbolt`

### 104. Sukhasana (Easy Seated Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `sukhasana_easy_pose` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`सुखासन`; runtime-resolved=`सुखासन`
- **prepCue**: Sit comfortably cross-legged with shins relaxed and pelvis neutral.
- **holdCue**: Soften your shoulders away from ears and take slow, steady nasal breaths.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `sukhasana_easy_pose`

### 105. Siddhasana (Accomplished Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `siddhasana_accomplished` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`सिद्धासन`; runtime-resolved=`सिद्धासन`
- **prepCue**: Sit with one heel close to the pelvis and the other tucked neatly in front.
- **holdCue**: Maintain an effortless upright spine and quiet rhythmic breathing.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `siddhasana_accomplished`

### 106. Chakrasana (Wheel Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `chakrasana_wheel` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`चक्रासन`; runtime-resolved=`चक्रासन`
- **prepCue**: Lie on your back, place feet near hips and palms beside ears, then press up into an arch.
- **holdCue**: Push evenly through palms and heels, open the chest, and breathe steadily.
- **Current Drawable**: `img_stretch_bridge`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `chakrasana_wheel`

### 107. Sarvangasana (Shoulder Stand)
- **Source**: `ExtraYogaAsanas` | **ID**: `sarvangasana_shoulderstand` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`सर्वांगासन`; runtime-resolved=`सर्वांगासन`
- **prepCue**: Lie supine, lift legs overhead, support your mid-back with palms, and extend legs skyward.
- **holdCue**: Keep weight on shoulders rather than neck, gaze softly at toes, and breathe calmly.
- **Current Drawable**: `img_stretch_upper`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `sarvangasana_shoulderstand`

### 108. Sirsasana (Supported Headstand)
- **Source**: `ExtraYogaAsanas` | **ID**: `sirsasana_headstand` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`शीर्षासन`; runtime-resolved=`शीर्षासन`
- **prepCue**: Interlace fingers on mat, cradle crown of head, engage forearms, and lift legs slowly.
- **holdCue**: Press forearms firmly into the mat, engage core, and keep shoulders lifted away from ears.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `sirsasana_headstand`

### 109. Naukasana (Boat Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `naukasana_boat` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`नौकासन`; runtime-resolved=`नौकासन`
- **prepCue**: Sit on sitting bones, lean torso back slightly, and lift legs and extended arms into a V.
- **holdCue**: Keep chest lifted, spine long, and breathe smoothly without tensing the neck.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `naukasana_boat`

### 110. Pavanamuktasana (Wind-Relieving Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `pavanamuktasana_wind_relieving` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`पवनमुक्तासन`; runtime-resolved=`पवनमुक्तासन`
- **prepCue**: Lie on your back and hug both knees firmly toward your chest with relaxed shoulders.
- **holdCue**: Gently press lower back into the mat and breathe deeply into the abdomen.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `pavanamuktasana_wind_relieving` *(anatomical group: `supine_double_knee_hug_pavanamuktasana`)*

### 111. Makarasana (Crocodile Relaxation Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `makarasana_crocodile` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`मकरासन`; runtime-resolved=`मकरासन`
- **prepCue**: Lie face down with toes turned outward and rest your forehead on stacked forearms.
- **holdCue**: Let the lower back soften completely with each slow belly exhale.
- **Current Drawable**: `img_stretch_floor`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `makarasana_crocodile`

### 112. Bakasana (Crow Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `bakasana_crow` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`बकासन`; runtime-resolved=`बकासन`
- **prepCue**: Squat low, plant palms shoulder-width, place knees high on upper arms, and shift forward.
- **holdCue**: Grip the mat with fingertips, draw heels toward glutes, and gaze slightly ahead.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `bakasana_crow`

### 113. Mayurasana (Peacock Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `mayurasana_peacock` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`मयूरासन`; runtime-resolved=`मयूरासन`
- **prepCue**: Kneel, turn fingertips back toward knees, tuck elbows against abdomen, and extend legs.
- **holdCue**: Engage core and glutes to hover parallel to the floor while breathing evenly.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `mayurasana_peacock`

### 114. Chaturanga Dandasana (Four-Limbed Staff)
- **Source**: `ExtraYogaAsanas` | **ID**: `chaturanga_dandasana` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`चतुरंग दंडासन`; runtime-resolved=`चतुरंग दंडासन`
- **prepCue**: From high plank, shift slightly forward and bend elbows to 90 degrees hugging ribs.
- **holdCue**: Keep body in one straight line from heels to crown with core firmly engaged.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `chaturanga_dandasana`

### 115. Dandasana (Staff Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `dandasana_staff` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`दंडासन`; runtime-resolved=`दंडासन`
- **prepCue**: Sit with legs extended straight forward, feet flexed, and palms beside your hips.
- **holdCue**: Press lightly through palms to lift your sternum tall and engage the quadriceps.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `dandasana_staff`

### 116. Ardha Chandrasana (Half Moon - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `ardha_chandrasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`अर्ध चन्द्रासन`; runtime-resolved=`अर्ध चन्द्रासन`
- **prepCue**: Balance on your right foot and right fingertips, lifting left leg parallel to floor.
- **holdCue**: Stack hips open, extend left arm skyward, and keep the lifted foot flexed.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#117** `ardha_chandrasana_left` — Ardha Chandrasana (Half Moon - Left))
- **Canonical Pose ID**: `ardha_chandrasana_half_moon`

### 117. Ardha Chandrasana (Half Moon - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `ardha_chandrasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`अर्ध चन्द्रासन`; runtime-resolved=`अर्ध चन्द्रासन`
- **prepCue**: Balance on your left foot and left fingertips, lifting right leg parallel to floor.
- **holdCue**: Stack hips open, reach right arm high, and breathe smoothly for stability.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#116** `ardha_chandrasana_right` — Ardha Chandrasana (Half Moon - Right))
- **Canonical Pose ID**: `ardha_chandrasana_half_moon`

### 118. Utthita Parsvakonasana (Extended Side Angle - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `utthita_parsvakonasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`उत्थित पार्श्वकोणासन`; runtime-resolved=`उत्थित पार्श्वकोणासन`
- **prepCue**: From Warrior II on the right, rest right forearm on thigh and sweep left arm overhead.
- **holdCue**: Create one long diagonal line from left heel to left fingertips, opening chest upward.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#119** `utthita_parsvakonasana_left` — Utthita Parsvakonasana (Extended Side Angle - Left))
- **Canonical Pose ID**: `utthita_parsvakonasana_extended_side_angle`

### 119. Utthita Parsvakonasana (Extended Side Angle - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `utthita_parsvakonasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`उत्थित पार्श्वकोणासन`; runtime-resolved=`उत्थित पार्श्वकोणासन`
- **prepCue**: From Warrior II on the left, rest left forearm on thigh and sweep right arm overhead.
- **holdCue**: Press outer edge of right foot into mat and lengthen through the right ribs.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#118** `utthita_parsvakonasana_right` — Utthita Parsvakonasana (Extended Side Angle - Right))
- **Canonical Pose ID**: `utthita_parsvakonasana_extended_side_angle`

### 120. Parivrtta Trikonasana (Revolved Triangle - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `parivrtta_trikonasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`परिवृत्त त्रिकोणासन`; runtime-resolved=`परिवृत्त त्रिकोणासन`
- **prepCue**: Step right foot forward, hinge with flat back, place left hand near right foot, and twist right.
- **holdCue**: Keep hips squared back, lengthen spine on inhales, and rotate chest on exhales.
- **Current Drawable**: `img_stretch_side_bend`
- **Symmetry**: L/R Pair (partner: **#121** `parivrtta_trikonasana_left` — Parivrtta Trikonasana (Revolved Triangle - Left))
- **Canonical Pose ID**: `parivrtta_trikonasana_revolved_triangle`

### 121. Parivrtta Trikonasana (Revolved Triangle - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `parivrtta_trikonasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`परिवृत्त त्रिकोणासन`; runtime-resolved=`परिवृत्त त्रिकोणासन`
- **prepCue**: Step left foot forward, hinge forward, place right hand near left foot, and twist left.
- **holdCue**: Draw left hip gently back, reach left arm upward, and keep breath steady.
- **Current Drawable**: `img_stretch_side_bend`
- **Symmetry**: L/R Pair (partner: **#120** `parivrtta_trikonasana_right` — Parivrtta Trikonasana (Revolved Triangle - Right))
- **Canonical Pose ID**: `parivrtta_trikonasana_revolved_triangle`

### 122. Hanumanasana (Monkey Split - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `hanumanasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`हनुमानासन`; runtime-resolved=`हनुमानासन`
- **prepCue**: From low lunge with right leg forward, slowly slide right heel forward to a comfortable depth.
- **holdCue**: Keep hips square, support yourself with fingertips on floor or blocks, and breathe gently.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: L/R Pair (partner: **#123** `hanumanasana_left` — Hanumanasana (Monkey Split - Left))
- **Canonical Pose ID**: `hanumanasana_monkey_split`

### 123. Hanumanasana (Monkey Split - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `hanumanasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`हनुमानासन`; runtime-resolved=`हनुमानासन`
- **prepCue**: From low lunge with left leg forward, slowly extend left heel forward within comfort.
- **holdCue**: Stay tall through the torso, avoid forcing, and soften tension on every exhale.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: L/R Pair (partner: **#122** `hanumanasana_right` — Hanumanasana (Monkey Split - Right))
- **Canonical Pose ID**: `hanumanasana_monkey_split`

### 124. Natarajasana (Dancer Pose - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `natarajasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`नटराजासन`; runtime-resolved=`नटराजासन`
- **prepCue**: Stand on your right leg, hold left ankle behind you, and kick gently into your hand.
- **holdCue**: Reach right arm forward, lift chest proudly, and fix gaze on a steady point.
- **Current Drawable**: `img_stretch_tree`
- **Symmetry**: L/R Pair (partner: **#125** `natarajasana_left` — Natarajasana (Dancer Pose - Left))
- **Canonical Pose ID**: `natarajasana_dancer`

### 125. Natarajasana (Dancer Pose - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `natarajasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`नटराजासन`; runtime-resolved=`नटराजासन`
- **prepCue**: Stand on your left leg, clasp right ankle behind you, and lift right thigh upward.
- **holdCue**: Keep standing knee soft, open right shoulder, and breathe smoothly.
- **Current Drawable**: `img_stretch_tree`
- **Symmetry**: L/R Pair (partner: **#124** `natarajasana_right` — Natarajasana (Dancer Pose - Right))
- **Canonical Pose ID**: `natarajasana_dancer`

### 126. Virasana (Hero Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `virasana_hero` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`वीरासन`; runtime-resolved=`वीरासन`
- **prepCue**: Kneel with knees together and feet slightly wider than hips, lowering seat between heels.
- **holdCue**: Sit evenly on the mat or a block, lengthen spine, and relax hands onto thighs.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `virasana_hero`

### 127. Supta Virasana (Reclining Hero Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `supta_virasana_reclining_hero` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`सुप्त वीरासन`; runtime-resolved=`सुप्त वीरासन`
- **prepCue**: From Hero Pose, walk hands back onto elbows or recline gently onto a bolster.
- **holdCue**: Keep knees comfortable, allow chest and thighs to open, and take slow breaths.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `supta_virasana_reclining_hero`

### 128. Mandukasana (Frog Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `mandukasana_frog` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`मंडूकासन`; runtime-resolved=`मंडूकासन`
- **prepCue**: From all fours, widen knees gently apart with shins parallel and rest on forearms.
- **holdCue**: Keep hips aligned with knees and exhale softly to release inner-thigh tension.
- **Current Drawable**: `img_stretch_butterfly`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `mandukasana_frog`

### 129. Simhasana (Lion Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `simhasana_lion` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`सिंहासन`; runtime-resolved=`सिंहासन`
- **prepCue**: Kneel or sit tall, spread fingers wide on knees, inhale deeply through the nose.
- **holdCue**: Exhale with a gentle sigh, relaxing jaw, tongue, face, and throat muscles.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `simhasana_lion`

### 130. Kurmasana (Tortoise Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `kurmasana_tortoise` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`कूर्मासन`; runtime-resolved=`कूर्मासन`
- **prepCue**: Sit with legs wide and knees slightly bent, slide arms under knees, and fold forward.
- **holdCue**: Let shoulders and upper back soften downward as you breathe into your back ribs.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `kurmasana_tortoise`

### 131. Shashankasana (Hare Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `shashankasana_hare` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`शशांकासन`; runtime-resolved=`शशांकासन`
- **prepCue**: From kneeling Vajrasana, reach arms up and fold forward resting forehead on the mat.
- **holdCue**: Relax shoulders, neck, and lower back completely with slow exhales.
- **Current Drawable**: `img_stretch_child`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `shashankasana_hare` *(anatomical group: `childs_pose_balasana_shashankasana`)*

### 132. Padahastasana (Hand-to-Foot Forward Fold)
- **Source**: `ExtraYogaAsanas` | **ID**: `padahastasana_hand_to_foot` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`पादहस्तासन`; runtime-resolved=`पादहस्तासन`
- **prepCue**: Fold forward from standing and slide palms underneath the soles of your feet.
- **holdCue**: Keep knees softly bent if needed, relax your head, and lengthen the back of legs.
- **Current Drawable**: `img_stretch_hamstring`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `padahastasana_hand_to_foot`

### 133. Vasisthasana (Side Plank - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `vasisthasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`वसिष्ठासन`; runtime-resolved=`वसिष्ठासन`
- **prepCue**: Shift onto right palm and outer right foot, stacking legs and lifting hips high.
- **holdCue**: Extend left arm toward ceiling, engage obliques, and keep shoulder strong.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: L/R Pair (partner: **#134** `vasisthasana_left` — Vasisthasana (Side Plank - Left))
- **Canonical Pose ID**: `vasisthasana_side_plank`

### 134. Vasisthasana (Side Plank - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `vasisthasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`वसिष्ठासन`; runtime-resolved=`वसिष्ठासन`
- **prepCue**: Shift onto left palm and outer left foot, stacking legs and lifting hips steadily.
- **holdCue**: Reach right arm skyward, lengthen waist, and breathe smoothly.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: L/R Pair (partner: **#133** `vasisthasana_right` — Vasisthasana (Side Plank - Right))
- **Canonical Pose ID**: `vasisthasana_side_plank`

### 135. Skandasana (Side Lunge - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `skandasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`स्कंदासन`; runtime-resolved=`स्कंदासन`
- **prepCue**: From a wide stance, bend deeply into your right knee while extending left leg straight.
- **holdCue**: Flex left toes toward ceiling, keep chest lifted, and feel the inner-thigh stretch.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: L/R Pair (partner: **#136** `skandasana_left` — Skandasana (Side Lunge - Left))
- **Canonical Pose ID**: `skandasana_side_lunge`

### 136. Skandasana (Side Lunge - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `skandasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`स्कंदासन`; runtime-resolved=`स्कंदासन`
- **prepCue**: Bend deeply into your left knee while extending right leg straight with toes pointed up.
- **holdCue**: Support with hands on mat or palms at heart, breathing smoothly into the hips.
- **Current Drawable**: `img_stretch_lunge`
- **Symmetry**: L/R Pair (partner: **#135** `skandasana_right` — Skandasana (Side Lunge - Right))
- **Canonical Pose ID**: `skandasana_side_lunge`

### 137. Tolasana (Scale Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `tolasana_scale` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`तोलासन`; runtime-resolved=`तोलासन`
- **prepCue**: Sit cross-legged or in lotus, plant palms beside hips, and press down to lift seat.
- **holdCue**: Draw knees upward with core strength and keep shoulders steady.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `tolasana_scale`

### 138. Bharadvajasana (Seated Sage Twist - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `bharadvajasana_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`भरद्वाजासन`; runtime-resolved=`भरद्वाजासन`
- **prepCue**: Sit with legs swept to the left, place left hand on right knee, and gently twist right.
- **holdCue**: Grow tall through the spine on inhales and soften into the rotation on exhales.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#139** `bharadvajasana_left` — Bharadvajasana (Seated Sage Twist - Left))
- **Canonical Pose ID**: `bharadvajasana_sage_twist`

### 139. Bharadvajasana (Seated Sage Twist - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `bharadvajasana_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`भरद्वाजासन`; runtime-resolved=`भरद्वाजासन`
- **prepCue**: Sit with legs swept to the right, place right hand on left knee, and gently twist left.
- **holdCue**: Keep shoulders relaxed and breath flowing evenly throughout the twist.
- **Current Drawable**: `img_stretch_twist`
- **Symmetry**: L/R Pair (partner: **#138** `bharadvajasana_right` — Bharadvajasana (Seated Sage Twist - Right))
- **Canonical Pose ID**: `bharadvajasana_sage_twist`

### 140. Upavistha Konasana (Seated Wide-Angle Fold)
- **Source**: `ExtraYogaAsanas` | **ID**: `upavistha_konasana_wide_angle` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`उपविष्ट कोणासन`; runtime-resolved=`उपविष्ट कोणासन`
- **prepCue**: Sit with legs spread comfortably wide, toes pointing up, and walk hands forward.
- **holdCue**: Hinge from the hips with a long spine and breathe into inner thighs and hamstrings.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `upavistha_konasana_wide_angle`

### 141. Purvottanasana (Upward Plank Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `purvottanasana_upward_plank` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`पूर्वोत्तानासन`; runtime-resolved=`पूर्वोत्तानासन`
- **prepCue**: Sit with legs straight, place palms behind hips, and press hips upward toward ceiling.
- **holdCue**: Open chest wide, reach toes toward floor, and keep neck comfortable.
- **Current Drawable**: `img_stretch_bridge`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `purvottanasana_upward_plank`

### 142. Parighasana (Gate Pose - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `parighasana_gate_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`परिघासन`; runtime-resolved=`परिघासन`
- **prepCue**: Kneel upright, extend right leg to the side, and arc left arm over toward right foot.
- **holdCue**: Open left ribcage toward ceiling and breathe deeply into the side body.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: L/R Pair (partner: **#143** `parighasana_gate_left` — Parighasana (Gate Pose - Left))
- **Canonical Pose ID**: `parighasana_gate`

### 143. Parighasana (Gate Pose - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `parighasana_gate_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`परिघासन`; runtime-resolved=`परिघासन`
- **prepCue**: Kneel upright, extend left leg to the side, and arc right arm over toward left foot.
- **holdCue**: Keep hips stacked over kneeling knee and lengthen through the right side waist.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: L/R Pair (partner: **#142** `parighasana_gate_right` — Parighasana (Gate Pose - Right))
- **Canonical Pose ID**: `parighasana_gate`

### 144. Virabhadrasana III (Warrior III - Right)
- **Source**: `ExtraYogaAsanas` | **ID**: `virabhadrasana_3_right` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`वीरभद्रासन तीन`; runtime-resolved=`वीरभद्रासन तीन`
- **prepCue**: Balance on your right foot, hinge torso forward, and lift left leg into a straight T.
- **holdCue**: Square hips toward floor, extend arms forward or to sides, and engage core.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#145** `virabhadrasana_3_left` — Virabhadrasana III (Warrior III - Left))
- **Canonical Pose ID**: `virabhadrasana_3`

### 145. Virabhadrasana III (Warrior III - Left)
- **Source**: `ExtraYogaAsanas` | **ID**: `virabhadrasana_3_left` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`वीरभद्रासन तीन`; runtime-resolved=`वीरभद्रासन तीन`
- **prepCue**: Balance on your left foot, hinge torso forward, and lift right leg parallel to mat.
- **holdCue**: Lengthen from back heel to crown and keep breath calm and steady.
- **Current Drawable**: `img_stretch_warrior`
- **Symmetry**: L/R Pair (partner: **#144** `virabhadrasana_3_right` — Virabhadrasana III (Warrior III - Right))
- **Canonical Pose ID**: `virabhadrasana_3`

### 146. Phalakasana (Plank Pose)
- **Source**: `ExtraYogaAsanas` | **ID**: `phalakasana_plank` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`फलकासन`; runtime-resolved=`फलकासन`
- **prepCue**: Place hands under shoulders and step feet back into a strong, level plank.
- **holdCue**: Press mat away, draw navel gently toward spine, and keep neck neutral.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `phalakasana_plank`

### 147. Surya Namaskar (Sun Salutation Flow)
- **Source**: `ExtraYogaAsanas` | **ID**: `surya_namaskar_flow` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`सूर्य नमस्कार`; runtime-resolved=`सूर्य नमस्कार`
- **prepCue**: Stand at top of mat in Prayer Pose and prepare to flow smoothly with each breath.
- **holdCue**: Move mindfully through reach, fold, lunge, plank, cobra, and downward dog.
- **Current Drawable**: `img_stretch_standing`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `surya_namaskar_flow`

### 148. Anulom Vilom (Alternate Nostril Breathing)
- **Source**: `ExtraYogaAsanas` | **ID**: `anulom_vilom_pranayama` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`अनुलोम विलोम प्राणायाम`; runtime-resolved=`अनुलोम विलोम प्राणायाम`
- **prepCue**: Sit comfortably tall with spine erect and bring right thumb and ring finger near nostrils.
- **holdCue**: Inhale slowly through left nostril, switch, exhale right, inhale right, exhale left.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `anulom_vilom_pranayama`

### 149. Bhramari Pranayama (Humming Bee Breath)
- **Source**: `ExtraYogaAsanas` | **ID**: `bhramari_pranayama` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`भ्रामरी प्राणायाम`; runtime-resolved=`भ्रामरी प्राणायाम`
- **prepCue**: Sit tall with eyes closed, relax jaw, and inhale deeply through the nose.
- **holdCue**: Exhale with a gentle, soothing humming sound, feeling calm vibration in the head.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `bhramari_pranayama`

### 150. Ujjayi Pranayama (Ocean Breath)
- **Source**: `ExtraYogaAsanas` | **ID**: `ujjayi_pranayama` | **Category**: `Yoga Asanas`
- **Indian/Sanskrit Name**: explicit=`उज्जायी प्राणायाम`; runtime-resolved=`उज्जायी प्राणायाम`
- **prepCue**: Sit comfortably upright, close eyes, and soften throat and shoulders.
- **holdCue**: Breathe slowly through the nose with a soft ocean-wave whisper at the back of throat.
- **Current Drawable**: `img_stretch_lotus`
- **Symmetry**: Standalone
- **Canonical Pose ID**: `ujjayi_pranayama`

## Canonical Pose Grouping Summary

### A. The 32 Symmetrical Left/Right Pairs (64 Stretches -> 32 Canonical Poses)

| Pair # | Canonical Pose ID | Right Stretch (#, ID, Name) | Left Stretch (#, ID, Name) | Source | Current Drawable |
|---:|---|---|---|---|---|
| 1 | `neck_side_stretch` | #1 `neck_side_right` (Neck Side Stretch (Right)) | #2 `neck_side_left` (Neck Side Stretch (Left)) | StretchLibrary | `img_stretch_neck` |
| 2 | `cross_body_shoulder_stretch` | #4 `cross_body_shoulder_right` (Cross-Body Shoulder Stretch (Right)) | #5 `cross_body_shoulder_left` (Cross-Body Shoulder Stretch (Left)) | StretchLibrary | `img_stretch_upper` |
| 3 | `thread_the_needle` | #14 `thread_the_needle_right` (Thread the Needle (Right Side)) | #15 `thread_the_needle_left` (Thread the Needle (Left Side)) | StretchLibrary | `img_stretch_twist` |
| 4 | `reclining_spinal_twist` | #19 `reclining_spinal_twist_right` (Reclining Spinal Twist (Right)) | #20 `reclining_spinal_twist_left` (Reclining Spinal Twist (Left)) | StretchLibrary | `img_stretch_twist` |
| 5 | `low_lunge_hip_flexor` | #24 `hip_flexor_lunge_right` (Low Lunge Hip Flexor (Right Forward)) | #25 `hip_flexor_lunge_left` (Low Lunge Hip Flexor (Left Forward)) | StretchLibrary | `img_stretch_lunge` |
| 6 | `reclining_figure_four_glute` | #26 `figure_four_glute_right` (Reclining Figure-4 Glute (Right)) | #27 `figure_four_glute_left` (Reclining Figure-4 Glute (Left)) | StretchLibrary | `img_stretch_figure_four` |
| 7 | `pigeon_pose_sl` | #29 `pigeon_pose_right` (Pigeon Pose (Right Shin Forward)) | #30 `pigeon_pose_left` (Pigeon Pose (Left Shin Forward)) | StretchLibrary | `img_stretch_pigeon` |
| 8 | `standing_quad_stretch` | #36 `standing_quad_right` (Standing Quad Stretch (Right Leg)) | #37 `standing_quad_left` (Standing Quad Stretch (Left Leg)) | StretchLibrary | `img_stretch_quad` |
| 9 | `wall_calf_gastrocnemius` | #41 `wall_calf_gastrocnemius_right` (Wall Calf Stretch (Right Leg Back)) | #42 `wall_calf_gastrocnemius_left` (Wall Calf Stretch (Left Leg Back)) | StretchLibrary | `img_stretch_calf` |
| 10 | `trikonasana_triangle` | #55 `trikonasana_triangle_right` (Trikonasana (Triangle Pose - Right)) | #56 `trikonasana_triangle_left` (Trikonasana (Triangle Pose - Left)) | StretchLibrary | `img_stretch_side_bend` |
| 11 | `vrikshasana_tree` | #57 `vrikshasana_tree_right` (Vrikshasana (Tree Pose - Right)) | #58 `vrikshasana_tree_left` (Vrikshasana (Tree Pose - Left)) | StretchLibrary | `img_stretch_tree` |
| 12 | `janu_sirsasana` | #61 `janu_sirsasana_right` (Janu Sirsasana (Head-to-Knee - Right)) | #62 `janu_sirsasana_left` (Janu Sirsasana (Head-to-Knee - Left)) | StretchLibrary | `img_stretch_hamstring` |
| 13 | `ardha_matsyendrasana` | #65 `ardha_matsyendrasana_right` (Ardha Matsyendrasana (Half Lord Twist - Right)) | #66 `ardha_matsyendrasana_left` (Ardha Matsyendrasana (Half Lord Twist - Left)) | StretchLibrary | `img_stretch_twist` |
| 14 | `gomukhasana_arms` | #67 `gomukhasana_arms_right` (Gomukhasana (Cow Face Arms - Right)) | #68 `gomukhasana_arms_left` (Gomukhasana (Cow Face Arms - Left)) | StretchLibrary | `img_stretch_upper` |
| 15 | `virabhadrasana_1` | #71 `virabhadrasana_one_right` (Virabhadrasana I (Warrior I - Right)) | #72 `virabhadrasana_one_left` (Virabhadrasana I (Warrior I - Left)) | StretchLibrary | `img_stretch_warrior` |
| 16 | `virabhadrasana_2` | #73 `virabhadrasana_two_right` (Virabhadrasana II (Warrior II - Right)) | #74 `virabhadrasana_two_left` (Virabhadrasana II (Warrior II - Left)) | StretchLibrary | `img_stretch_warrior` |
| 17 | `anjaneyasana_crescent_low_lunge` | #75 `anjaneyasana_low_lunge_right` (Anjaneyasana (Crescent Low Lunge - Right)) | #76 `anjaneyasana_low_lunge_left` (Anjaneyasana (Crescent Low Lunge - Left)) | StretchLibrary | `img_stretch_lunge` |
| 18 | `supta_padangusthasana` | #80 `supta_padangusthasana_right` (Supta Padangusthasana (Reclining Leg - Right)) | #81 `supta_padangusthasana_left` (Supta Padangusthasana (Reclining Leg - Left)) | StretchLibrary | `img_stretch_hamstring` |
| 19 | `garudasana_eagle` | #86 `garudasana_eagle_pose_right` (Garudasana (Eagle Pose - Right)) | #87 `garudasana_eagle_pose_left` (Garudasana (Eagle Pose - Left)) | StretchLibrary | `img_stretch_upper` |
| 20 | `parsvottanasana_pyramid` | #91 `parsvottanasana_pyramid_right` (Parsvottanasana (Pyramid Pose - Right)) | #92 `parsvottanasana_pyramid_left` (Parsvottanasana (Pyramid Pose - Left)) | StretchLibrary | `img_stretch_hamstring` |
| 21 | `eka_pada_rajakapotasana_pigeon` | #94 `eka_pada_rajakapotasana_pigeon_right` (Eka Pada Rajakapotasana (Pigeon - Right)) | #95 `eka_pada_rajakapotasana_pigeon_left` (Eka Pada Rajakapotasana (Pigeon - Left)) | StretchLibrary | `img_stretch_pigeon` |
| 22 | `supta_matsyendrasana_supine_twist` | #96 `supta_matsyendrasana_twist_right` (Supta Matsyendrasana (Supine Twist - Right)) | #97 `supta_matsyendrasana_twist_left` (Supta Matsyendrasana (Supine Twist - Left)) | StretchLibrary | `img_stretch_twist` |
| 23 | `ardha_chandrasana_half_moon` | #116 `ardha_chandrasana_right` (Ardha Chandrasana (Half Moon - Right)) | #117 `ardha_chandrasana_left` (Ardha Chandrasana (Half Moon - Left)) | ExtraYogaAsanas | `img_stretch_warrior` |
| 24 | `utthita_parsvakonasana_extended_side_angle` | #118 `utthita_parsvakonasana_right` (Utthita Parsvakonasana (Extended Side Angle - Right)) | #119 `utthita_parsvakonasana_left` (Utthita Parsvakonasana (Extended Side Angle - Left)) | ExtraYogaAsanas | `img_stretch_warrior` |
| 25 | `parivrtta_trikonasana_revolved_triangle` | #120 `parivrtta_trikonasana_right` (Parivrtta Trikonasana (Revolved Triangle - Right)) | #121 `parivrtta_trikonasana_left` (Parivrtta Trikonasana (Revolved Triangle - Left)) | ExtraYogaAsanas | `img_stretch_side_bend` |
| 26 | `hanumanasana_monkey_split` | #122 `hanumanasana_right` (Hanumanasana (Monkey Split - Right)) | #123 `hanumanasana_left` (Hanumanasana (Monkey Split - Left)) | ExtraYogaAsanas | `img_stretch_standing` |
| 27 | `natarajasana_dancer` | #124 `natarajasana_right` (Natarajasana (Dancer Pose - Right)) | #125 `natarajasana_left` (Natarajasana (Dancer Pose - Left)) | ExtraYogaAsanas | `img_stretch_tree` |
| 28 | `vasisthasana_side_plank` | #133 `vasisthasana_right` (Vasisthasana (Side Plank - Right)) | #134 `vasisthasana_left` (Vasisthasana (Side Plank - Left)) | ExtraYogaAsanas | `img_stretch_standing` |
| 29 | `skandasana_side_lunge` | #135 `skandasana_right` (Skandasana (Side Lunge - Right)) | #136 `skandasana_left` (Skandasana (Side Lunge - Left)) | ExtraYogaAsanas | `img_stretch_lunge` |
| 30 | `bharadvajasana_sage_twist` | #138 `bharadvajasana_right` (Bharadvajasana (Seated Sage Twist - Right)) | #139 `bharadvajasana_left` (Bharadvajasana (Seated Sage Twist - Left)) | ExtraYogaAsanas | `img_stretch_twist` |
| 31 | `parighasana_gate` | #142 `parighasana_gate_right` (Parighasana (Gate Pose - Right)) | #143 `parighasana_gate_left` (Parighasana (Gate Pose - Left)) | ExtraYogaAsanas | `img_stretch_standing` |
| 32 | `virabhadrasana_3` | #144 `virabhadrasana_3_right` (Virabhadrasana III (Warrior III - Right)) | #145 `virabhadrasana_3_left` (Virabhadrasana III (Warrior III - Left)) | ExtraYogaAsanas | `img_stretch_warrior` |

### B. The 86 Standalone Poses (No L/R Partner in Catalog)

| # | ID | Name | Source | Current Drawable | Cross-Catalog Anatomical Duplicate Group (if any) |
|---:|---|---|---|---|---|
| 3 | `chin_tuck_levator` | Chin Tuck & Levator Stretch | StretchLibrary | `img_stretch_neck` | — |
| 6 | `overhead_triceps_lat` | Overhead Triceps & Lat Reach | StretchLibrary | `img_stretch_upper` | — |
| 7 | `eagle_arms_stretch` | Eagle Arms Scapular Spread | StretchLibrary | `img_stretch_upper` | — |
| 8 | `scm_neck_turn` | Sternocleidomastoid (SCM) Neck Stretch | StretchLibrary | `img_stretch_neck` | — |
| 9 | `doorway_chest_opener` | Doorway / Wall Pectoral Opener | StretchLibrary | `img_stretch_chest` | — |
| 10 | `interlaced_chest_expansion` | Interlaced Chest Expansion | StretchLibrary | `img_stretch_chest` | — |
| 11 | `child_pose_lat_reach` | Child's Pose with Lat Reach | StretchLibrary | `img_stretch_child` | — |
| 12 | `puppy_pose_melting_heart` | Puppy Pose (Melting Heart) | StretchLibrary | `img_stretch_child` | — |
| 13 | `cat_cow_spinal_flow` | Cat-Cow Spine Mobilizer | StretchLibrary | `img_stretch_cat_cow` | — |
| 16 | `standing_side_arc` | Standing Side-Bend Arc | StretchLibrary | `img_stretch_side_bend` | — |
| 17 | `supine_knees_to_chest` | Supine Double Knee Hug | StretchLibrary | `img_stretch_floor` | `supine_double_knee_hug_pavanamuktasana` |
| 18 | `single_knee_to_chest` | Single Knee-to-Chest Decompression | StretchLibrary | `img_stretch_floor` | — |
| 21 | `seated_spinal_twist` | Seated Torso Spinal Twist | StretchLibrary | `img_stretch_twist` | — |
| 22 | `sphinx_pose_extension` | Sphinx Pose Lumbar Extension | StretchLibrary | `img_stretch_floor` | — |
| 23 | `cobra_stretch_gentle` | Gentle Cobra Pose | StretchLibrary | `img_stretch_cobra` | `cobra_pose_bhujangasana` |
| 28 | `seated_butterfly` | Seated Butterfly Adductor Stretch | StretchLibrary | `img_stretch_butterfly` | `seated_butterfly_baddha_konasana` |
| 31 | `lizard_lunge_hip` | Lizard Lunge Inside Hip Opener | StretchLibrary | `img_stretch_lunge` | — |
| 32 | `ninety_ninety_hip` | 90-90 Seated Hip Rotator Stretch | StretchLibrary | `img_stretch_lunge` | — |
| 33 | `standing_hamstring_fold` | Standing Forward Hamstring Fold | StretchLibrary | `img_stretch_hamstring` | `standing_forward_fold_uttanasana` |
| 34 | `single_leg_hamstring_heel` | Single-Leg Heel Hamstring Stretch | StretchLibrary | `img_stretch_hamstring` | — |
| 35 | `seated_single_leg_fold` | Seated Single-Leg Fold (Janu Sirsasana) | StretchLibrary | `img_stretch_hamstring` | `head_to_knee_janu_sirsasana` |
| 38 | `side_lying_quad` | Side-Lying Quad & Hip Stretch | StretchLibrary | `img_stretch_quad` | — |
| 39 | `wide_legged_straddle_fold` | Wide-Legged Straddle Forward Fold | StretchLibrary | `img_stretch_hamstring` | `wide_legged_forward_fold_prasarita` |
| 40 | `pyramid_pose_stretch` | Pyramid Pose Alignment Stretch | StretchLibrary | `img_stretch_hamstring` | `pyramid_pose_parsvottanasana` |
| 43 | `wall_soleus_bent_knee` | Soleus Bent-Knee Calf Stretch | StretchLibrary | `img_stretch_calf` | — |
| 44 | `downward_dog_pedal` | Downward Dog Calf Pedaling | StretchLibrary | `img_stretch_calf` | — |
| 45 | `plantar_fascia_toe_tuck` | Kneeling Toe Tuck Foot Stretch | StretchLibrary | `img_stretch_calf` | — |
| 46 | `wrist_flexor_extension` | Wrist Flexor Stretch (Palm Up) | StretchLibrary | `img_stretch_wrist` | — |
| 47 | `wrist_extensor_flexion` | Wrist Extensor Stretch (Palm Down) | StretchLibrary | `img_stretch_wrist` | — |
| 48 | `wrist_rotations_tendon` | Wrist Figure-Eight Fluid Mobilizer | StretchLibrary | `img_stretch_figure_four` | — |
| 49 | `standing_side_reach_lat` | Standing Side Lat Reach | StretchLibrary | `img_stretch_side_bend` | — |
| 50 | `child_pose_full_body` | Restorative Child's Pose | StretchLibrary | `img_stretch_child` | `childs_pose_balasana_shashankasana` |
| 51 | `bhujangasana_cobra_pose` | Bhujangasana (Cobra Pose) | StretchLibrary | `img_stretch_cobra` | `cobra_pose_bhujangasana` |
| 52 | `adho_mukha_svanasana_downward_dog` | Adho Mukha Svanasana (Downward-Facing Dog) | StretchLibrary | `img_stretch_downward_dog` | — |
| 53 | `balasana_childs_pose` | Balasana (Child's Pose) | StretchLibrary | `img_stretch_child` | `childs_pose_balasana_shashankasana` |
| 54 | `setu_bandhasana_bridge_pose` | Setu Bandhasana (Bridge Pose) | StretchLibrary | `img_stretch_bridge` | — |
| 59 | `ustrasana_camel_pose` | Ustrasana (Camel Pose) | StretchLibrary | `img_stretch_chest` | — |
| 60 | `paschimottanasana_seated_forward_fold` | Paschimottanasana (Seated Forward Bend) | StretchLibrary | `img_stretch_hamstring` | — |
| 63 | `baddha_konasana_butterfly` | Baddha Konasana (Bound Angle Butterfly) | StretchLibrary | `img_stretch_butterfly` | `seated_butterfly_baddha_konasana` |
| 64 | `supta_baddha_konasana_reclining` | Supta Baddha Konasana (Reclining Butterfly) | StretchLibrary | `img_stretch_butterfly` | — |
| 69 | `malasana_garland_yogi_squat` | Malasana (Yogi Squat) | StretchLibrary | `img_stretch_butterfly` | — |
| 70 | `uttanasana_forward_fold` | Uttanasana (Standing Forward Fold) | StretchLibrary | `img_stretch_hamstring` | `standing_forward_fold_uttanasana` |
| 77 | `matsyasana_fish_pose` | Matsyasana (Fish Pose) | StretchLibrary | `img_stretch_chest` | — |
| 78 | `halasana_plow_pose` | Halasana (Plow Pose) | StretchLibrary | `img_stretch_floor` | — |
| 79 | `dhanurasana_bow_pose` | Dhanurasana (Bow Pose) | StretchLibrary | `img_stretch_chest` | — |
| 82 | `viparita_karani_legs_wall` | Viparita Karani (Legs-Up-The-Wall) | StretchLibrary | `img_stretch_floor` | — |
| 83 | `shavasana_corpse_pose` | Shavasana (Corpse Restorative Pose) | StretchLibrary | `img_stretch_floor` | — |
| 84 | `tadasana_mountain_alignment` | Tadasana (Mountain Pose Alignment) | StretchLibrary | `img_stretch_standing` | — |
| 85 | `prasarita_padottanasana_wide_fold` | Prasarita Padottanasana (Wide-Leg Fold) | StretchLibrary | `img_stretch_hamstring` | `wide_legged_forward_fold_prasarita` |
| 88 | `ananda_balasana_happy_baby` | Ananda Balasana (Happy Baby Pose) | StretchLibrary | `img_stretch_butterfly` | — |
| 89 | `salabhasana_locust_pose` | Salabhasana (Locust Pose) | StretchLibrary | `img_stretch_cobra` | — |
| 90 | `urdhva_mukha_svanasana_updog` | Urdhva Mukha Svanasana (Upward Dog) | StretchLibrary | `img_stretch_cobra` | — |
| 93 | `utkatasana_chair_pose` | Utkatasana (Chair Pose Stretch) | StretchLibrary | `img_stretch_standing` | — |
| 98 | `goddess_pose_utkata_konasana` | Utkata Konasana (Goddess Pose) | StretchLibrary | `img_stretch_butterfly` | — |
| 99 | `marjaryasana_cat_pose` | Marjaryasana (Cat Pose Spinal Flexion) | StretchLibrary | `img_stretch_cat_cow` | — |
| 100 | `bitilasana_cow_pose` | Bitilasana (Cow Pose Spinal Extension) | StretchLibrary | `img_stretch_cat_cow` | — |
| 101 | `padmasana_lotus` | Padmasana (Lotus Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 102 | `ardha_padmasana_half_lotus` | Ardha Padmasana (Half Lotus Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 103 | `vajrasana_thunderbolt` | Vajrasana (Thunderbolt Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 104 | `sukhasana_easy_pose` | Sukhasana (Easy Seated Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 105 | `siddhasana_accomplished` | Siddhasana (Accomplished Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 106 | `chakrasana_wheel` | Chakrasana (Wheel Pose) | ExtraYogaAsanas | `img_stretch_bridge` | — |
| 107 | `sarvangasana_shoulderstand` | Sarvangasana (Shoulder Stand) | ExtraYogaAsanas | `img_stretch_upper` | — |
| 108 | `sirsasana_headstand` | Sirsasana (Supported Headstand) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 109 | `naukasana_boat` | Naukasana (Boat Pose) | ExtraYogaAsanas | `img_stretch_floor` | — |
| 110 | `pavanamuktasana_wind_relieving` | Pavanamuktasana (Wind-Relieving Pose) | ExtraYogaAsanas | `img_stretch_floor` | `supine_double_knee_hug_pavanamuktasana` |
| 111 | `makarasana_crocodile` | Makarasana (Crocodile Relaxation Pose) | ExtraYogaAsanas | `img_stretch_floor` | — |
| 112 | `bakasana_crow` | Bakasana (Crow Pose) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 113 | `mayurasana_peacock` | Mayurasana (Peacock Pose) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 114 | `chaturanga_dandasana` | Chaturanga Dandasana (Four-Limbed Staff) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 115 | `dandasana_staff` | Dandasana (Staff Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 126 | `virasana_hero` | Virasana (Hero Pose) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 127 | `supta_virasana_reclining_hero` | Supta Virasana (Reclining Hero Pose) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 128 | `mandukasana_frog` | Mandukasana (Frog Pose) | ExtraYogaAsanas | `img_stretch_butterfly` | — |
| 129 | `simhasana_lion` | Simhasana (Lion Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 130 | `kurmasana_tortoise` | Kurmasana (Tortoise Pose) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 131 | `shashankasana_hare` | Shashankasana (Hare Pose) | ExtraYogaAsanas | `img_stretch_child` | `childs_pose_balasana_shashankasana` |
| 132 | `padahastasana_hand_to_foot` | Padahastasana (Hand-to-Foot Forward Fold) | ExtraYogaAsanas | `img_stretch_hamstring` | — |
| 137 | `tolasana_scale` | Tolasana (Scale Pose) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 140 | `upavistha_konasana_wide_angle` | Upavistha Konasana (Seated Wide-Angle Fold) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 141 | `purvottanasana_upward_plank` | Purvottanasana (Upward Plank Pose) | ExtraYogaAsanas | `img_stretch_bridge` | — |
| 146 | `phalakasana_plank` | Phalakasana (Plank Pose) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 147 | `surya_namaskar_flow` | Surya Namaskar (Sun Salutation Flow) | ExtraYogaAsanas | `img_stretch_standing` | — |
| 148 | `anulom_vilom_pranayama` | Anulom Vilom (Alternate Nostril Breathing) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 149 | `bhramari_pranayama` | Bhramari Pranayama (Humming Bee Breath) | ExtraYogaAsanas | `img_stretch_lotus` | — |
| 150 | `ujjayi_pranayama` | Ujjayi Pranayama (Ocean Breath) | ExtraYogaAsanas | `img_stretch_lotus` | — |

### C. Cross-Catalog Identical Pose Groups (9 Groups: 22 Stretches / 19 L-R-Merged Canonical Poses -> 9 Anatomical Poses)

If cross-catalog identical poses also share one illustration (in addition to L/R pair merging), total unique poses drops from **118** to **108** (-10).

| Anatomical Group | Member Stretches (#, ID, Name) | Anatomical Match Notes |
|---|---|---|
| `cobra_pose_bhujangasana` | #23 `cobra_stretch_gentle`, #51 `bhujangasana_cobra_pose` | Both are prone Cobra Pose (lifting head & chest with palms under shoulders). |
| `childs_pose_balasana_shashankasana` | #50 `child_pose_full_body`, #53 `balasana_childs_pose`, #131 `shashankasana_hare` | All three kneel hips-to-heels, fold torso forward, forehead on mat, arms extended forward (`img_stretch_child.jpg`). |
| `seated_butterfly_baddha_konasana` | #28 `seated_butterfly`, #63 `baddha_konasana_butterfly` | Both sit with soles together, knees wide, holding feet/ankles, hinging forward. |
| `pigeon_pose_prone` | #29/#30 `pigeon_pose_right/left`, #94/#95 `eka_pada_rajakapotasana_pigeon_right/left` | Both fold forward over front bent shin with back leg extended straight back. |
| `standing_forward_fold_uttanasana` | #33 `standing_hamstring_fold`, #70 `uttanasana_forward_fold` | Both stand hip-width, hinge at hips, torso/head/arms hanging heavy. |
| `head_to_knee_janu_sirsasana` | #35 `seated_single_leg_fold`, #61/#62 `janu_sirsasana_right/left` | #35 is the single-entry version of #61/#62 Janu Sirsasana. |
| `wide_legged_forward_fold_prasarita` | #39 `wide_legged_straddle_fold`, #85 `prasarita_padottanasana_wide_fold` | Both stand 3–4 ft apart, hinge forward, place hands/palms on mat. |
| `pyramid_pose_parsvottanasana` | #40 `pyramid_pose_stretch`, #91/#92 `parsvottanasana_pyramid_right/left` | #40 is the single-entry version of #91/#92 Pyramid Pose. |
| `supine_double_knee_hug_pavanamuktasana` | #17 `supine_knees_to_chest`, #110 `pavanamuktasana_wind_relieving` | Both lie supine hugging both knees into chest. |
