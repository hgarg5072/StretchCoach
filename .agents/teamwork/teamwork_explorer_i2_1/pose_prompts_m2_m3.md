# Pose Prompt Specification — Milestone M2 (24 Poses / 30 Stretches) & Milestone M3 (17 Poses / 31 Stretches)

**Author**: Explorer 1 (`teamwork_explorer_i2_1`)  
**Scope**: Read-only specification for **41 canonical poses** covering **61 catalog stretches** (`#1–#51`, `#61–#63`, `#70`, `#85`, `#91–#92`, `#94–#95`, `#110`) across `ArtworkBatch1.kt` (M2) and `ArtworkBatch2.kt` (M3).

> **Count Note vs. `PROJECT.md`:** `PROJECT.md` line 217 summarizes M3 as *"17 canonical poses (covering 30 stretches)"*, whereas the per-row assignment table in `PROJECT.md` lines 83–104 and 120–154 assigns **31 stretches** to M3 (`#24–#45` [22] + `#61, #62, #63, #70, #85, #91, #92, #94, #95` [9] = **31**). All 31 stretches are specified below so none are omitted.

---

## 1. Global Generation & Asset Standards

### 1.1 `generate_image` Technical Rules
- **AspectRatio**: Always `"16:9"`.
- **Output Format & Dimensions**: Convert/save generated image to **1376 × 768 px baseline JPEG** (`.jpg`, quality ~85, EXIF/DPI stripped) matching the 8 unified reference drawables in `app/src/main/res/drawable/`.
- **Filename Regex**: `^[a-z][a-z0-9_]*\.jpg$` — strictly `img_stretch_b1_<key>.jpg` for M2 and `img_stretch_b2_<key>.jpg` for M3.
- **ImagePaths**: Exactly **2** existing reference files from `app/src/main/res/drawable/img_stretch_*.jpg` per pose, selected to match studio lighting and posture orientation (avoiding conflicting limb/hand biases — e.g., `#1/#2` avoids `img_stretch_neck.jpg` because that reference places a hand on the head).

### 1.2 Unified Studio Art Style Clause (Embedded in Every Prompt)
Every prompt enforces the verified studio design from `drawables_audit.md §2`:
- **Medium**: Clean minimalist editorial ink-and-watercolor illustration with fine dark-brown/charcoal ink outlines on foreground practitioner and mat, soft matte watercolor shading, and softer lower-contrast watercolor washes in the studio background.
- **Practitioner**: Young woman with warm fair-to-light-olive skin, subtle soft peach cheek blush, calm serene expression, dark espresso-brown hair with a clean center part pulled into a neat low bun at the nape with subtle wisps, wearing a sleeveless sage-olive sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, always barefoot.
- **Environment**: Centered full-body on a beige woven jute yoga mat over light honey-oak wood plank flooring in a warm sunlit cream-plaster minimalist yoga studio with natural window sunlight and terracotta potted green plants (Monstera / ficus).
- **Framing & Purity**: 16:9 widescreen composition, full body centered with generous margins on all sides (no cropped head, hands, or feet), single static frame, zero text, zero labels, zero watermarks, zero mat logos, zero split-panels, zero arrows or motion lines, zero abstract floating blobs.

### 1.3 Kotlin Registry Key Normalization (`ArtworkMapper.normalizeKey`)
`ArtworkMapper.kt:13-14` normalizes lookups via `raw.trim().lowercase().replace(Regex("\\s+"), " ")`. Every `ArtworkBatch1` / `ArtworkBatch2` entry must register **both** the exact lowercase trimmed `name` and the `id` for every stretch mapped to that canonical pose.

---

## 2. R4 Static-Depiction Flags (Requires Judge / Reviewer Attention)

Per Requirement **R4**, every stretch below was audited for whether a single static illustration can truthfully depict both `prepCue` and `holdCue`. **3 canonical poses (3 stretches)** have dynamic/alternating motion cues that a single static JPEG cannot fully animate:

| Cat # | Stretch ID | Stretch Name | Batch / Pose | R4 Flag | Static Mitigation & Judge Decision Point |
|---:|---|---|---|---|---|
| **13** | `cat_cow_spinal_flow` | Cat-Cow Spine Mobilizer | M2 / `M2-11` | `FLAG_R4_DYNAMIC_FLOW` | `holdCue` instructs alternating inhale arch (Cow) and exhale round (Cat). Single-frame prompt depicts the Cow spinal arch with wrists under shoulders and knees under hips (zero split frames/ghost silhouettes/arrows). Judge must confirm whether a single-phase tabletop frame passes R4 or whether `#13` should remain unmapped (`null`) given `#99` (Cat) and `#100` (Cow) exist separately in M4. |
| **44** | `downward_dog_pedal` | Downward Dog Calf Pedaling | M3 / `M3-16` | `FLAG_R4_ALTERNATING_MOTION` | `holdCue` instructs alternating heel-pressing pedaling motions. Single-frame prompt depicts an unmistakable alternating-pedal stride (one leg straight with heel pressed flat onto mat, opposite knee visibly bent forward with heel lifted high on ball of foot). Judge verifies whether this snapshot clearly communicates pedaling under R4. |
| **48** | `wrist_rotations_tendon` | Wrist Figure-Eight Fluid Mobilizer | M2 / `M2-22` | `FLAG_R4_DYNAMIC_ROTATION` | `holdCue` instructs rolling clasped wrists in figure-eight circles and reversing halfway. Static frame accurately shows the `prepCue` posture (fingers clasped in front of chest, relaxed shoulders, wrists softly angled mid-roll, zero arrows/motion blur). Judge must confirm whether static clasped-hand posture satisfies R4 or should remain `null`. |

Additional **Cue-Discrepancy Fixes** explicitly enforced in prompts (no R4 failure if generated per spec):
- **#1 / #2 (`M2-01`)**: Hands-free gravity-only lateral neck tilt (**NO hand on head**; both hands resting peacefully on knees).
- **#9 (`M2-07`)**: Forearm flat at 90° against the cream plaster studio wall / wooden doorframe, stepping gently forward on the mat.
- **#19 / #20 (`M2-16`)**: **Both** knees drawn up and stacked together falling to one side (distinct from M4's single-knee `Supta Matsyendrasana`).
- **#23 / #51 (`M2-19`)**: Gentle Cobra (`Bhujangasana`) with **elbows slightly bent tucked close to ribs and pelvis/pubic bone/hips grounded** on the mat (NOT straight-arm lifted-hip Upward Dog).
- **#50 (`M2-24`)**: Restorative Child's Pose with **KNEES SPREAD WIDE** (not narrow knees together as in `img_stretch_child.jpg`).
- **#41 / #42 (`M3-14`)**: Barefoot wall calf stretch with back knee completely straight and heel flat (replaces sneakered legacy `img_stretch_calf.jpg`).
- **#43 (`M3-15`)**: Soleus bent-knee wall calf stretch close to wall with **both knees visibly bent** and back heel pressed flat.

---

## 3. Part A — Milestone M2 (`ArtworkBatch1.kt`, 24 Canonical Poses, 30 Stretches)

### 3.1 M2 Summary Index
| Pose | `drawable_filename` | Cat #s | Normalized Keys (`id` & `name`) | R4 Status |
|---|---|---|---|---|
| `M2-01` | `img_stretch_b1_neck_side_stretch.jpg` | #1, #2 | `neck_side_right`, `neck side stretch (right)`, `neck_side_left`, `neck side stretch (left)` | OK (hands-free fix) |
| `M2-02` | `img_stretch_b1_chin_tuck_levator.jpg` | #3 | `chin_tuck_levator`, `chin tuck & levator stretch` | OK |
| `M2-03` | `img_stretch_b1_cross_body_shoulder.jpg` | #4, #5 | `cross_body_shoulder_right`, `cross-body shoulder stretch (right)`, `cross_body_shoulder_left`, `cross-body shoulder stretch (left)` | OK |
| `M2-04` | `img_stretch_b1_overhead_triceps_lat.jpg` | #6 | `overhead_triceps_lat`, `overhead triceps & lat reach` | OK |
| `M2-05` | `img_stretch_b1_eagle_arms_stretch.jpg` | #7 | `eagle_arms_stretch`, `eagle arms scapular spread` | OK |
| `M2-06` | `img_stretch_b1_scm_neck_turn.jpg` | #8 | `scm_neck_turn`, `sternocleidomastoid (scm) neck stretch` | OK |
| `M2-07` | `img_stretch_b1_doorway_chest_opener.jpg` | #9 | `doorway_chest_opener`, `doorway / wall pectoral opener` | OK |
| `M2-08` | `img_stretch_b1_interlaced_chest_expansion.jpg` | #10 | `interlaced_chest_expansion`, `interlaced chest expansion` | OK |
| `M2-09` | `img_stretch_b1_child_pose_lat_reach.jpg` | #11 | `child_pose_lat_reach`, `child's pose with lat reach` | OK |
| `M2-10` | `img_stretch_b1_puppy_pose_melting_heart.jpg` | #12 | `puppy_pose_melting_heart`, `puppy pose (melting heart)` | OK |
| `M2-11` | `img_stretch_b1_cat_cow_spinal_flow.jpg` | #13 | `cat_cow_spinal_flow`, `cat-cow spine mobilizer` | **FLAG_R4_DYNAMIC_FLOW** |
| `M2-12` | `img_stretch_b1_thread_the_needle.jpg` | #14, #15 | `thread_the_needle_right`, `thread the needle (right side)`, `thread_the_needle_left`, `thread the needle (left side)` | OK |
| `M2-13` | `img_stretch_b1_standing_side_arc.jpg` | #16 | `standing_side_arc`, `standing side-bend arc` | OK |
| `M2-14` | `img_stretch_b1_supine_double_knee_hug.jpg` | #17, #110 | `supine_knees_to_chest`, `supine double knee hug`, `pavanamuktasana_wind_relieving`, `pavanamuktasana (wind-relieving pose)` | OK |
| `M2-15` | `img_stretch_b1_single_knee_to_chest.jpg` | #18 | `single_knee_to_chest`, `single knee-to-chest decompression` | OK |
| `M2-16` | `img_stretch_b1_reclining_spinal_twist.jpg` | #19, #20 | `reclining_spinal_twist_right`, `reclining spinal twist (right)`, `reclining_spinal_twist_left`, `reclining spinal twist (left)` | OK |
| `M2-17` | `img_stretch_b1_seated_spinal_twist.jpg` | #21 | `seated_spinal_twist`, `seated torso spinal twist` | OK |
| `M2-18` | `img_stretch_b1_sphinx_pose_extension.jpg` | #22 | `sphinx_pose_extension`, `sphinx pose lumbar extension` | OK |
| `M2-19` | `img_stretch_b1_cobra_pose_bhujangasana.jpg` | #23, #51 | `cobra_stretch_gentle`, `gentle cobra pose`, `bhujangasana_cobra_pose`, `bhujangasana (cobra pose)` | OK |
| `M2-20` | `img_stretch_b1_wrist_flexor_extension.jpg` | #46 | `wrist_flexor_extension`, `wrist flexor stretch (palm up)` | OK |
| `M2-21` | `img_stretch_b1_wrist_extensor_flexion.jpg` | #47 | `wrist_extensor_flexion`, `wrist extensor stretch (palm down)` | OK |
| `M2-22` | `img_stretch_b1_wrist_rotations_tendon.jpg` | #48 | `wrist_rotations_tendon`, `wrist figure-eight fluid mobilizer` | **FLAG_R4_DYNAMIC_ROTATION** |
| `M2-23` | `img_stretch_b1_standing_side_reach_lat.jpg` | #49 | `standing_side_reach_lat`, `standing side lat reach` | OK |
| `M2-24` | `img_stretch_b1_restorative_child_pose_wide_knees.jpg` | #50 | `child_pose_full_body`, `restorative child's pose` | OK (wide knees fix) |

---

### 3.2 M2 Pose-by-Pose Prompt Specifications

#### M2-01 — Neck Side Stretch (Right / Left) [#1, #2]
- **drawable_filename**: `img_stretch_b1_neck_side_stretch.jpg`
- **ImageName**: `img_stretch_b1_neck_side_stretch`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"neck_side_right"`
  - `"neck side stretch (right)"`
  - `"neck_side_left"`
  - `"neck side stretch (left)"`
- **Source Cues**:
  - #1 `prepCue`: "Lower your right ear toward your right shoulder without lifting your left shoulder." / `holdCue`: "Breathe gently into the left side of your neck. Let gravity do the work and relax your jaw."
  - #2 `prepCue`: "Gently tilt your left ear toward your left shoulder while keeping shoulders relaxed." / `holdCue`: "Feel the soothing release down the right side of your neck. Take smooth, calm breaths."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  *(Note: Intentionally omits `img_stretch_neck.jpg` so the generator does NOT copy its hand-on-head posture.)*
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman performing a hands-free lateral neck side stretch, seated cross-legged facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. She tilts her right ear gently sideways toward her right shoulder using only gravity, keeping both shoulders level, relaxed, and dropped down. Both arms hang relaxed with both hands resting calmly palms-down on her knees—strictly NO hand touching or pulling her head. Her eyes are peacefully closed and jaw relaxed. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair with a clean center part in a neat low bun with subtle wisps, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window sunlight and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins on all sides, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Resolves the hand-on-head defect of `img_stretch_neck.jpg`. Verify generated image has **no hand on head** and both shoulders remain level.

---

#### M2-02 — Chin Tuck & Levator Stretch [#3]
- **drawable_filename**: `img_stretch_b1_chin_tuck_levator.jpg`
- **ImageName**: `img_stretch_b1_chin_tuck_levator`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"chin_tuck_levator"`
  - `"chin tuck & levator stretch"`
- **Source Cues**:
  - `prepCue`: "Tuck your chin toward your collarbone and look gently toward your right armpit."
  - `holdCue`: "Feel a deep release down the back corner of your neck. Keep your shoulders heavy and down."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman performing a hands-free chin tuck and levator scapulae neck stretch, seated upright cross-legged facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. She tucks her chin down toward her collarbone and turns her head diagonally downward, gazing gently toward her right armpit to lengthen the back-left corner of her neck. Both shoulders stay heavy, level, and dropped away from her ears, with both hands resting calmly on her knees and no hand touching her head. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair with a center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Pass if chin is tucked downward and rotated diagonally toward the armpit with shoulders relaxed down and hands free.

---

#### M2-03 — Cross-Body Shoulder Stretch (Right / Left) [#4, #5]
- **drawable_filename**: `img_stretch_b1_cross_body_shoulder.jpg`
- **ImageName**: `img_stretch_b1_cross_body_shoulder`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"cross_body_shoulder_right"`
  - `"cross-body shoulder stretch (right)"`
  - `"cross_body_shoulder_left"`
  - `"cross-body shoulder stretch (left)"`
- **Source Cues**:
  - #4 `prepCue`: "Bring your right arm across your chest, supporting gently at the forearm with your left hand." / `holdCue`: "Keep your right shoulder dropped away from your ear. Breathe into the back of your shoulder."
  - #5 `prepCue`: "Draw your left arm straight across your chest and hold softly above the elbow." / `holdCue`: "Feel the broad stretch across your rear deltoid and upper back. Maintain relaxed posture."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing upright facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a cross-body shoulder stretch. Her feet are parallel hip-width apart and barefoot. She brings her straight right arm horizontally across her chest at shoulder height, with her right shoulder dropped away from her ear, while her left arm bends upward so her left hand gently cradles and supports the right forearm/elbow area. Spine tall, calm peaceful expression gazing forward. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair with a center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window sunlight and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Symmetrical L/R pair. Verify extended arm crosses chest horizontally with shoulder dropped and opposite hand supporting forearm/above elbow.

---

#### M2-04 — Overhead Triceps & Lat Reach [#6]
- **drawable_filename**: `img_stretch_b1_overhead_triceps_lat.jpg`
- **ImageName**: `img_stretch_b1_overhead_triceps_lat`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"overhead_triceps_lat"`
  - `"overhead triceps & lat reach"`
- **Source Cues**:
  - `prepCue`: "Reach one hand behind your neck and gently guide your elbow with the opposite hand."
  - `holdCue`: "Lengthen through your ribs and side torso. Keep your head upright without tucking your chin."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing upright facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing an overhead triceps and lat stretch. Feet hip-width apart and barefoot. She raises her right arm overhead, bending the right elbow straight upward so her right hand rests down behind her neck/upper back, while her left arm reaches overhead so her left hand gently grasps and guides the pointed right elbow. Her head stays upright and level facing forward without tucking her chin, lengthening tall through her ribs and side torso. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins above the raised elbow and below the feet, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify head remains upright (chin not tucked), raised elbow not cropped at top margin.

---

#### M2-05 — Eagle Arms Scapular Spread [#7]
- **drawable_filename**: `img_stretch_b1_eagle_arms_stretch.jpg`
- **ImageName**: `img_stretch_b1_eagle_arms_stretch`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"eagle_arms_stretch"`
  - `"eagle arms scapular spread"`
- **Source Cues**:
  - `prepCue`: "Cross your elbows and intertwine forearms, bringing palms or back of hands together."
  - `holdCue`: "Lift elbows to shoulder height and gently press forearms forward to open between your shoulder blades."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing tall in a slight three-quarter front view centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Eagle Arms scapular spread. Feet hip-width apart and barefoot. She crosses one elbow snugly over the other in front of her chest and intertwines both forearms vertically with palms pressed together, lifting her elbows to shoulder height and pressing her forearms slightly forward away from her face so the intertwined arms are clearly visible in silhouette, opening across her upper back and shoulder blades. Warm fair-to-light-olive skin, soft peach cheek blush, calm expression, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: 3/4 view ensures intertwined forearms and lifted elbows at shoulder height are anatomically clear and not flattened into a single line.

---

#### M2-06 — Sternocleidomastoid (SCM) Neck Stretch [#8]
- **drawable_filename**: `img_stretch_b1_scm_neck_turn.jpg`
- **ImageName**: `img_stretch_b1_scm_neck_turn`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"scm_neck_turn"`
  - `"sternocleidomastoid (scm) neck stretch"`
- **Source Cues**:
  - `prepCue`: "Turn your chin 45 degrees to the side and tilt your head slightly upward."
  - `holdCue`: "Feel the opening across the front diagonal band of your neck. Breathe slowly and evenly."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman seated cross-legged facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Sternocleidomastoid (SCM) neck stretch. With an upright spine and both shoulders relaxed and level, she turns her head and chin 45 degrees to her left side and tilts her chin slightly upward, visibly lengthening the front diagonal band of the right side of her neck. Both arms rest calmly with hands resting lightly on her knees, no hands near her face or head. Warm fair-to-light-olive skin, soft peach cheek blush, serene peaceful face, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from `#1/#2` (lateral ear-to-shoulder) and `#3` (downward chin tuck); verify 45° head turn + slight upward chin tilt.

---

#### M2-07 — Doorway / Wall Pectoral Opener [#9]
- **drawable_filename**: `img_stretch_b1_doorway_chest_opener.jpg`
- **ImageName**: `img_stretch_b1_doorway_chest_opener`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"doorway_chest_opener"`
  - `"doorway / wall pectoral opener"`
- **Source Cues**:
  - `prepCue`: "Place your forearm flat against a wall at 90 degrees and step gently forward."
  - `holdCue`: "Expand your chest with deep breaths. Feel the pectorals open without arching your lower back."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman performing a wall pectoral chest opener in a sunlit yoga studio, standing barefoot on a beige woven jute yoga mat placed alongside the warm cream-plaster studio wall and wooden doorway trim on the right side of the room over light honey-oak wood plank flooring. Viewed in three-quarter profile, her right arm is bent at 90 degrees at the elbow with her upper arm parallel to the floor and her entire right forearm and palm pressed flat against the vertical studio wall/doorway frame behind/beside her. She steps her front foot gently forward in a subtle staggered stance, expanding her chest open with a neutral, non-arched lower back and left arm relaxed at her side. Warm fair-to-light-olive skin, soft peach cheek blush, serene gaze forward, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Sunlit cream-plaster studio with natural window light and a terracotta potted Monstera plant on the left. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, full-body centered 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify forearm is clearly flat against the wall/doorframe at a 90° elbow bend, staggered step forward, neutral lower back, barefoot on mat.

---

#### M2-08 — Interlaced Chest Expansion [#10]
- **drawable_filename**: `img_stretch_b1_interlaced_chest_expansion.jpg`
- **ImageName**: `img_stretch_b1_interlaced_chest_expansion`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"interlaced_chest_expansion"`
  - `"interlaced chest expansion"`
- **Source Cues**:
  - `prepCue`: "Clasp your fingers behind your lower back and roll your shoulders open."
  - `holdCue`: "Gently lift your knuckles away from your hips. Keep your neck soft and ribs drawn in."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring in a three-quarter rear-side view, performing an interlaced chest expansion stretch. Her feet are hip-width apart and torso upright with ribs drawn gently in and neck soft. Both arms reach behind her lower back with fingers clasped and interlaced together, elbows straight, rolling her shoulders back and lifting her clasped knuckles noticeably away from her hips to broaden her collarbones and chest. Warm fair-to-light-olive skin, soft peach cheek blush, calm expression, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: 3/4 rear-side profile makes clasped hands lifted away from hips unmistakably visible.

---

#### M2-09 — Child's Pose with Lat Reach [#11]
- **drawable_filename**: `img_stretch_b1_child_pose_lat_reach.jpg`
- **ImageName**: `img_stretch_b1_child_pose_lat_reach`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"child_pose_lat_reach"`
  - `"child's pose with lat reach"`
- **Source Cues**:
  - `prepCue`: "Kneel, sink hips back to heels, and walk both hands over to the right side of your mat."
  - `holdCue`: "Press into your left palm and breathe into your left ribcage. Feel the full length of your side body."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from an elevated three-quarter front-side angle of a young woman performing Child's Pose with a lateral lat reach on a beige woven jute yoga mat over light honey-oak wood plank flooring. Kneeling barefoot with her hips sunk all the way back onto her heels, her torso folds forward over her thighs and curves gracefully toward the right side of the mat; both arms extend long forward and diagonally off to the right edge of the mat with palms flat on the mat/floor, pressing gently into her left palm to lengthen her entire left ribcage and side body while her forehead rests low near the mat. Warm fair-to-light-olive skin, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from `img_stretch_child.jpg` by clearly showing the diagonal lateral reach of both arms to the right side of the mat and side-body C-curve.

---

#### M2-10 — Puppy Pose (Melting Heart) [#12]
- **drawable_filename**: `img_stretch_b1_puppy_pose_melting_heart.jpg`
- **ImageName**: `img_stretch_b1_puppy_pose_melting_heart`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"puppy_pose_melting_heart"`
  - `"puppy pose (melting heart)"`
- **Source Cues**:
  - `prepCue`: "Keep hips stacked over knees while walking hands forward, melting your chest toward the floor."
  - `holdCue`: "Rest forehead or chin down. Allow thoracic spine and shoulders to soften toward the ground."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing Puppy Pose (Melting Heart / Anahatasana) centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. Kneeling barefoot with shins and tops of feet flat on the mat, her thighs are completely vertical at 90 degrees so her hips stay stacked high directly over her knees (NOT sitting back on her heels). Both arms extend straight forward along the mat with palms flat, while her upper chest melts deeply downward toward the mat and her forehead rests gently on the mat, creating a long gentle thoracic opening. Warm fair-to-light-olive skin, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: CRITICAL distinction from Child's Pose: thighs vertical, hips stacked directly above knees, chest melted down.

---

#### M2-11 — Cat-Cow Spine Mobilizer [#13] — `FLAG_R4_DYNAMIC_FLOW`
- **drawable_filename**: `img_stretch_b1_cat_cow_spinal_flow.jpg`
- **ImageName**: `img_stretch_b1_cat_cow_spinal_flow`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"cat_cow_spinal_flow"`
  - `"cat-cow spine mobilizer"`
- **Source Cues**:
  - `prepCue`: "Start on all fours with wrists below shoulders and knees below hips."
  - `holdCue`: "Inhale to arch gently and lift gaze, exhale to round spine like a cat. Move slowly with breath."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman in a single continuous tabletop pose on all fours centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing the arched extension phase of Cat-Cow spine mobilizer. Her wrists are stacked directly beneath her shoulders with palms flat and fingers spread, knees stacked directly beneath her hips with shins and tops of bare feet resting on the mat. Her belly drops gently toward the mat into a smooth controlled spinal arch, chest opening forward and head lifted softly gazing ahead with a relaxed neck. Single figure only—no split panels, no ghosted second posture, no arrows. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos.
- **R4 Notes**: **`FLAG_R4_DYNAMIC_FLOW`**: `holdCue` describes alternating inhale arch (Cow) and exhale round (Cat). Single static frame depicts the exact tabletop alignment and arched Cow phase cleanly without illegal split-panels or arrows. M2/Final visual judge should confirm acceptance or mark `#13` `null` if strict dual-phase animation/illustration is required under R4.

---

#### M2-12 — Thread the Needle (Right / Left Side) [#14, #15]
- **drawable_filename**: `img_stretch_b1_thread_the_needle.jpg`
- **ImageName**: `img_stretch_b1_thread_the_needle`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"thread_the_needle_right"`
  - `"thread the needle (right side)"`
  - `"thread_the_needle_left"`
  - `"thread the needle (left side)"`
- **Source Cues**:
  - #14 `prepCue`: "From tabletop, slide your right arm under your chest, resting your right shoulder on the mat." / `holdCue`: "Extend left arm forward or wrap it behind. Feel thoracic rotation and shoulder decompression."
  - #15 `prepCue`: "From tabletop, slide your left arm under your chest, resting your left shoulder on the mat." / `holdCue`: "Let gravity twist your upper torso gently. Keep your hips centered over your knees."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a three-quarter front-side view of a young woman performing Thread the Needle pose centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. Starting from kneeling tabletop with her hips centered high directly over her knees and shins/tops of bare feet resting on the mat, she threads her right arm straight underneath her chest across the mat so her right shoulder, outer right arm (palm facing up), and the right side of her head rest comfortably on the mat. Her left arm extends straight forward along the mat with left palm planted flat for gentle support as her upper torso twists softly open. Warm fair-to-light-olive skin, calm serene face, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Symmetrical L/R pair. Verify hips remain elevated over knees (not collapsed onto heels) and threaded shoulder/temple rest on mat.

---

#### M2-13 — Standing Side-Bend Arc [#16]
- **drawable_filename**: `img_stretch_b1_standing_side_arc.jpg`
- **ImageName**: `img_stretch_b1_standing_side_arc`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"standing_side_arc"`
  - `"standing side-bend arc"`
- **Source Cues**:
  - `prepCue`: "Stand tall, reach your right arm overhead, and side bend gently toward the left."
  - `holdCue`: "Anchor down through both feet. Feel the expansive stretch from hip bone to fingertips."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a standing side-bend arc. Both bare feet are firmly anchored hip-width apart flat on the mat with legs straight. She reaches her right arm straight overhead and bends her torso smoothly sideways in a graceful C-arc toward her left side (viewer's right), while her left arm relaxes downward along her left thigh/hip. Her chest stays square facing forward without twisting, stretching cleanly from hip bone to fingertips. Warm fair-to-light-olive skin, soft peach cheek blush, serene face, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins around fingertips and feet, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from `#49` (`standing_side_reach_lat`) where BOTH hands are clasped overhead; here only the right arm arcs overhead while the left arm stays down by the side.

---

#### M2-14 — Supine Double Knee Hug / Pavanamuktasana [#17, #110]
- **drawable_filename**: `img_stretch_b1_supine_double_knee_hug.jpg`
- **ImageName**: `img_stretch_b1_supine_double_knee_hug`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"supine_knees_to_chest"`
  - `"supine double knee hug"`
  - `"pavanamuktasana_wind_relieving"`
  - `"pavanamuktasana (wind-relieving pose)"`
- **Source Cues**:
  - #17 `prepCue`: "Lie on your back, bend knees, and wrap arms around shins." / `holdCue`: "Gently draw knees toward chest. Keep head and neck relaxed and let lower back flatten."
  - #110 `prepCue`: "Lie on your back and hug both knees firmly toward your chest with relaxed shoulders." / `holdCue`: "Gently press lower back into the mat and breathe deeply into the abdomen."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman lying supine on her back centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Supine Double Knee Hug (Pavanamuktasana). The back of her head, relaxed shoulders, and entire lower back/sacrum rest flat against the mat with eyes peacefully closed. Both knees are bent deeply and drawn up toward her chest, with both arms wrapped gently around her shins and bare feet relaxed. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair in a neat low bun resting comfortably on the mat, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared cross-catalog pose for `#17` and `#110`. Verify head/shoulders stay flat on mat (not lifted into a crunch, matching #17 "Keep head and neck relaxed" and #110 "with relaxed shoulders").

---

#### M2-15 — Single Knee-to-Chest Decompression [#18]
- **drawable_filename**: `img_stretch_b1_single_knee_to_chest.jpg`
- **ImageName**: `img_stretch_b1_single_knee_to_chest`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"single_knee_to_chest"`
  - `"single knee-to-chest decompression"`
- **Source Cues**:
  - `prepCue`: "Lie flat, hug your right knee into your chest while extending your left leg straight."
  - `holdCue`: "Keep left thigh grounded. Feel the gentle hip flexion and lumbar release."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman lying supine on her back centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Single Knee-to-Chest Decompression. The back of her head, shoulders, and lower back rest flat on the mat with eyes softly closed. Her right knee is bent deeply and hugged snugly into her chest with both hands clasped around her right shin/knee, while her left leg is extended completely straight and flat with the back of her left thigh and calf grounded along the mat and bare foot relaxed. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify extended leg is completely straight and grounded flat on the mat while opposite knee is hugged to chest.

---

#### M2-16 — Reclining Spinal Twist (Right / Left) [#19, #20]
- **drawable_filename**: `img_stretch_b1_reclining_spinal_twist.jpg`
- **ImageName**: `img_stretch_b1_reclining_spinal_twist`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"reclining_spinal_twist_right"`
  - `"reclining spinal twist (right)"`
  - `"reclining_spinal_twist_left"`
  - `"reclining spinal twist (left)"`
- **Source Cues**:
  - #19 `prepCue`: "Lie on your back, draw knees up, and let them fall to the right while gazing left." / `holdCue`: "Keep both shoulder blades pinned to the floor. Let gravity rinse out spinal tension."
  - #20 `prepCue`: "Guide both knees softly over to the left side while extending your right arm out." / `holdCue`: "Breathe deeply into your right lower back and belly. Surrender weight into the mat."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from an elevated three-quarter side-above angle of a young woman lying supine on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a double-knee Reclining Spinal Twist. Both shoulder blades remain pinned flat against the mat with both arms extended outward to the sides in a relaxed T shape, palms facing up. Both knees are bent at 90 degrees and stacked together, lowered smoothly together to one side toward the floor while her head turns gently to gaze toward the opposite extended hand, twisting through her lower waist. Warm fair-to-light-olive skin, calm serene face, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: CRITICAL: Show **BOTH** knees bent and stacked together falling to one side (NOT single-knee extended twist, which belongs to M4's `#96/#97` Supta Matsyendrasana).

---

#### M2-17 — Seated Torso Spinal Twist [#21]
- **drawable_filename**: `img_stretch_b1_seated_spinal_twist.jpg`
- **ImageName**: `img_stretch_b1_seated_spinal_twist`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"seated_spinal_twist"`
  - `"seated torso spinal twist"`
- **Source Cues**:
  - `prepCue`: "Sit tall with legs crossed or extended, place right hand behind you and rotate gently."
  - `holdCue`: "Inhale to lengthen your crown upward; exhale to deepen the rotation from your core."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_neck.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a three-quarter front-side view of a young woman seated cross-legged with a tall upright spine centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Seated Torso Spinal Twist. Her legs are comfortably crossed flat on the mat (not stepped over the knee). She places her right hand lightly on the mat behind her right hip for support, rests her left hand gently on the outside of her right knee/thigh, and rotates her torso, chest, and gaze smoothly toward the right while keeping her crown lengthening upward and shoulders relaxed. Warm fair-to-light-olive skin, soft peach cheek blush, serene face, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from M4's `#65/#66` Ardha Matsyendrasana (where one foot steps across the opposite thigh and the elbow hooks outside an upright knee).

---

#### M2-18 — Sphinx Pose Lumbar Extension [#22]
- **drawable_filename**: `img_stretch_b1_sphinx_pose_extension.jpg`
- **ImageName**: `img_stretch_b1_sphinx_pose_extension`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"sphinx_pose_extension"`
  - `"sphinx pose lumbar extension"`
- **Source Cues**:
  - `prepCue`: "Lie on your belly and prop onto your forearms with elbows directly under shoulders."
  - `holdCue`: "Gently draw your chest forward between your arms. Keep glutes and lower back relaxed."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman lying prone on her belly centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Sphinx Pose. Her legs extend straight back together along the mat with thighs, pelvis, and tops of bare feet resting flat on the mat. She is propped up on her forearms with elbows stacked directly beneath her shoulders, forearms parallel on the mat with palms flat down, drawing her chest gently forward between her upper arms with relaxed shoulders and serene forward gaze. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify **forearms** rest flat on the mat with elbows directly under shoulders (distinct from Cobra `#23/#51` where palms are under shoulders and forearms are lifted).

---

#### M2-19 — Gentle Cobra Pose / Bhujangasana [#23, #51]
- **drawable_filename**: `img_stretch_b1_cobra_pose_bhujangasana.jpg`
- **ImageName**: `img_stretch_b1_cobra_pose_bhujangasana`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"cobra_stretch_gentle"`
  - `"gentle cobra pose"`
  - `"bhujangasana_cobra_pose"`
  - `"bhujangasana (cobra pose)"`
- **Source Cues**:
  - #23 `prepCue`: "Place palms beneath shoulders, press lightly into hands to lift head and chest." / `holdCue`: "Keep shoulders rolled back and down. Only lift as high as comfortable without pinching."
  - #51 `prepCue`: "Lie flat on stomach with tops of feet pressed into mat. Place hands under shoulders with elbows tucked close." / `holdCue`: "Press pubic bone down, inhale and gently lift chest and heart space forward. Roll shoulders down and back."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman lying prone on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Gentle Cobra Pose (Bhujangasana). Her legs are extended straight back with tops of her bare feet, thighs, hips, and pubic bone resting completely grounded on the mat (hips and thighs MUST touch the mat—NOT lifted into Upward Dog). Both palms are planted flat on the mat beneath her shoulders with elbows noticeably bent and tucked close beside her ribcage, gently pressing into her hands to lift her chest and head forward into a modest, comfortable low cobra curve with shoulders rolled down and back away from her ears. Warm fair-to-light-olive skin, soft peach cheek blush, calm expression, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted green plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared cross-catalog pose for `#23` and `#51`. CRITICAL: Reject if arms are locked straight or hips/thighs lift off the mat (which would be Upward Dog `#90`). Elbows must have a gentle bend tucked close, pelvis/thighs grounded.

---

#### M2-20 — Wrist Flexor Stretch (Palm Up) [#46]
- **drawable_filename**: `img_stretch_b1_wrist_flexor_extension.jpg`
- **ImageName**: `img_stretch_b1_wrist_flexor_extension`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"wrist_flexor_extension"`
  - `"wrist flexor stretch (palm up)"`
- **Source Cues**:
  - `prepCue`: "Extend one arm with elbow straight, palm facing up, and pull fingers back with opposite hand."
  - `holdCue`: "Keep shoulder relaxed down. Feel the clean opening along the inside of your forearm."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a clear three-quarter front-side view of a young woman standing upright barefoot centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Wrist Flexor Stretch (Palm Up). Her right arm is extended straight forward at chest height with the elbow completely straight, inner forearm facing upward and palm turned up/forward with fingers pointing downward, while her left hand gently grasps her extended right fingers and pulls them back toward her body to open the inner forearm and wrist. Both shoulders stay relaxed down away from her ears. Warm fair-to-light-olive skin, soft peach cheek blush, serene face, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with clear hand/wrist readability and generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: 3/4 view prevents foreshortening of the extended arm so the straight elbow, palm-up wrist extension, and opposite hand pulling fingers back are crisp and unambiguous.

---

#### M2-21 — Wrist Extensor Stretch (Palm Down) [#47]
- **drawable_filename**: `img_stretch_b1_wrist_extensor_flexion.jpg`
- **ImageName**: `img_stretch_b1_wrist_extensor_flexion`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"wrist_extensor_flexion"`
  - `"wrist extensor stretch (palm down)"`
- **Source Cues**:
  - `prepCue`: "Extend arm straight with palm down, gently press fingers down and toward your body."
  - `holdCue`: "Feel the stretch over the top of your wrist and outer forearm. Breathe smoothly."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a clear three-quarter front-side view of a young woman standing upright barefoot centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Wrist Extensor Stretch (Palm Down). Her right arm is extended straight forward at chest height with the elbow completely straight and palm facing downward/back toward her body, wrist flexed downward at 90 degrees so the back of her hand faces forward, while her left hand gently presses against the back of her right hand/fingers downward toward her body to stretch the top of the wrist and outer forearm. Shoulders relaxed down. Warm fair-to-light-olive skin, soft peach cheek blush, serene face, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with clear hand/wrist readability and generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from `#46` (palm up); verify back of extended hand faces forward/wrist flexes downward.

---

#### M2-22 — Wrist Figure-Eight Fluid Mobilizer [#48] — `FLAG_R4_DYNAMIC_ROTATION`
- **drawable_filename**: `img_stretch_b1_wrist_rotations_tendon.jpg`
- **ImageName**: `img_stretch_b1_wrist_rotations_tendon`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"wrist_rotations_tendon"`
  - `"wrist figure-eight fluid mobilizer"`
- **Source Cues**:
  - `prepCue`: "Clasp your fingers together in front of your chest and relax your shoulders."
  - `holdCue`: "Roll your wrists in slow, smooth figure-eight circles. Switch rotation direction halfway."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing upright facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a gentle clasped-wrist mobilizer. Barefoot with feet hip-width apart and shoulders relaxed down, her elbows bend softly at her sides as she clasps both hands with fingers interlaced gently in front of the center of her chest, wrists angled softly in a smooth relaxed wrist-roll position. Single clean static pose—strictly NO motion lines, NO arrows, NO blur. Warm fair-to-light-olive skin, soft peach cheek blush, calm serene expression, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: **`FLAG_R4_DYNAMIC_ROTATION`**: Static illustration accurately depicts `prepCue` (fingers clasped in front of chest, shoulders relaxed), while continuous figure-eight rotation & direction switch (`holdCue`) cannot be depicted in a static image without forbidden motion arrows. Judge decides whether static clasped-wrist pose is acceptable under R4 or if `#48` should return `null`.

---

#### M2-23 — Standing Side Lat Reach [#49]
- **drawable_filename**: `img_stretch_b1_standing_side_reach_lat.jpg`
- **ImageName**: `img_stretch_b1_standing_side_reach_lat`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"standing_side_reach_lat"`
  - `"standing side lat reach"`
- **Source Cues**:
  - `prepCue`: "Clasp your hands overhead, turn palms to ceiling, and lean gently to one side."
  - `holdCue`: "Press up through palms while grounding through feet. Breathe into lateral ribs."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration of a young woman standing facing forward centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Standing Side Lat Reach. Both bare feet are grounded hip-width apart with legs straight. Both arms reach straight overhead together with fingers interlaced/clasped and palms turned upward facing the ceiling, while she leans her torso smoothly to one side in a gentle lateral stretch, opening her side ribs without twisting. Warm fair-to-light-olive skin, soft peach cheek blush, calm expression, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins above raised palms and below feet, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from `#16` (`standing_side_arc`): both hands MUST be clasped overhead with palms facing upward toward the ceiling.

---

#### M2-24 — Restorative Child's Pose (Wide Knees) [#50]
- **drawable_filename**: `img_stretch_b1_restorative_child_pose_wide_knees.jpg`
- **ImageName**: `img_stretch_b1_restorative_child_pose_wide_knees`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"child_pose_full_body"`
  - `"restorative child's pose"`
- **Source Cues**:
  - `prepCue`: "Kneel wide, sink hips all the way back onto heels, and extend arms long in front."
  - `holdCue`: "Rest forehead on mat. Let your entire body, breath, and mind settle into deep relaxation."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from an elevated three-quarter front-side angle of a young woman performing Restorative Child's Pose with knees spread wide, centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. Her knees are separated wide apart toward the outer edges of the mat while her big toes touch behind her and her hips sink all the way back onto her heels. Her torso rests comfortably down between her wide-opened thighs with her forehead resting softly on the mat and both arms extended long straight forward along the mat, palms down. Warm fair-to-light-olive skin, calm serene face, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Explicitly resolves the wide-knee requirement (`Kneel wide`) that `img_stretch_child.jpg` (narrow-knee Balasana `#53` / `#131`) does not depict.

---

## 4. Part B — Milestone M3 (`ArtworkBatch2.kt`, 17 Canonical Poses, 31 Stretches)

### 4.1 M3 Summary Index
| Pose | `drawable_filename` | Cat #s | Normalized Keys (`id` & `name`) | R4 Status |
|---|---|---|---|---|
| `M3-01` | `img_stretch_b2_low_lunge_hip_flexor.jpg` | #24, #25 | `hip_flexor_lunge_right`, `low lunge hip flexor (right forward)`, `hip_flexor_lunge_left`, `low lunge hip flexor (left forward)` | OK |
| `M3-02` | `img_stretch_b2_reclining_figure_four_glute.jpg` | #26, #27 | `figure_four_glute_right`, `reclining figure-4 glute (right)`, `figure_four_glute_left`, `reclining figure-4 glute (left)` | OK |
| `M3-03` | `img_stretch_b2_seated_butterfly_baddha_konasana.jpg` | #28, #63 | `seated_butterfly`, `seated butterfly adductor stretch`, `baddha_konasana_butterfly`, `baddha konasana (bound angle butterfly)` | OK |
| `M3-04` | `img_stretch_b2_pigeon_pose_prone.jpg` | #29, #30, #94, #95 | `pigeon_pose_right`, `pigeon pose (right shin forward)`, `pigeon_pose_left`, `pigeon pose (left shin forward)`, `eka_pada_rajakapotasana_pigeon_right`, `eka pada rajakapotasana (pigeon - right)`, `eka_pada_rajakapotasana_pigeon_left`, `eka pada rajakapotasana (pigeon - left)` | OK |
| `M3-05` | `img_stretch_b2_lizard_lunge_hip.jpg` | #31 | `lizard_lunge_hip`, `lizard lunge inside hip opener` | OK |
| `M3-06` | `img_stretch_b2_ninety_ninety_hip.jpg` | #32 | `ninety_ninety_hip`, `90-90 seated hip rotator stretch` | OK |
| `M3-07` | `img_stretch_b2_standing_forward_fold_uttanasana.jpg` | #33, #70 | `standing_hamstring_fold`, `standing forward hamstring fold`, `uttanasana_forward_fold`, `uttanasana (standing forward fold)` | OK |
| `M3-08` | `img_stretch_b2_single_leg_hamstring_heel.jpg` | #34 | `single_leg_hamstring_heel`, `single-leg heel hamstring stretch` | OK |
| `M3-09` | `img_stretch_b2_head_to_knee_janu_sirsasana.jpg` | #35, #61, #62 | `seated_single_leg_fold`, `seated single-leg fold (janu sirsasana)`, `janu_sirsasana_right`, `janu sirsasana (head-to-knee - right)`, `janu_sirsasana_left`, `janu sirsasana (head-to-knee - left)` | OK |
| `M3-10` | `img_stretch_b2_standing_quad_stretch.jpg` | #36, #37 | `standing_quad_right`, `standing quad stretch (right leg)`, `standing_quad_left`, `standing quad stretch (left leg)` | OK |
| `M3-11` | `img_stretch_b2_side_lying_quad.jpg` | #38 | `side_lying_quad`, `side-lying quad & hip stretch` | OK |
| `M3-12` | `img_stretch_b2_wide_legged_forward_fold_prasarita.jpg` | #39, #85 | `wide_legged_straddle_fold`, `wide-legged straddle forward fold`, `prasarita_padottanasana_wide_fold`, `prasarita padottanasana (wide-leg fold)` | OK |
| `M3-13` | `img_stretch_b2_pyramid_pose_parsvottanasana.jpg` | #40, #91, #92 | `pyramid_pose_stretch`, `pyramid pose alignment stretch`, `parsvottanasana_pyramid_right`, `parsvottanasana (pyramid pose - right)`, `parsvottanasana_pyramid_left`, `parsvottanasana (pyramid pose - left)` | OK |
| `M3-14` | `img_stretch_b2_wall_calf_gastrocnemius.jpg` | #41, #42 | `wall_calf_gastrocnemius_right`, `wall calf stretch (right leg back)`, `wall_calf_gastrocnemius_left`, `wall calf stretch (left leg back)` | OK (barefoot fix) |
| `M3-15` | `img_stretch_b2_wall_soleus_bent_knee.jpg` | #43 | `wall_soleus_bent_knee`, `soleus bent-knee calf stretch` | OK |
| `M3-16` | `img_stretch_b2_downward_dog_pedal.jpg` | #44 | `downward_dog_pedal`, `downward dog calf pedaling` | **FLAG_R4_ALTERNATING_MOTION** |
| `M3-17` | `img_stretch_b2_plantar_fascia_toe_tuck.jpg` | #45 | `plantar_fascia_toe_tuck`, `kneeling toe tuck foot stretch` | OK |

---

### 4.2 M3 Pose-by-Pose Prompt Specifications

#### M3-01 — Low Lunge Hip Flexor (Right / Left Forward) [#24, #25]
- **drawable_filename**: `img_stretch_b2_low_lunge_hip_flexor.jpg`
- **ImageName**: `img_stretch_b2_low_lunge_hip_flexor`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"hip_flexor_lunge_right"`
  - `"low lunge hip flexor (right forward)"`
  - `"hip_flexor_lunge_left"`
  - `"low lunge hip flexor (left forward)"`
- **Source Cues**:
  - #24 `prepCue`: "Step right foot forward into a low lunge, lowering your left knee onto the mat." / `holdCue`: "Tuck your tailbone slightly and sink hips gently forward. Feel the front left hip open."
  - #25 `prepCue`: "Step left foot forward and lower right knee down. Keep torso upright and proud." / `holdCue`: "Squeeze your right glute slightly to deepen the front hip opening. Breathe steadily."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing a Low Lunge Hip Flexor Stretch centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. Her front right foot is planted flat on the mat with her front knee bent 90 degrees over her ankle, while her back left knee, shin, and top of bare foot rest comfortably on the mat behind her. Her hips sink gently forward and down to open the front of her back hip, while her torso stays upright and proud with hands resting lightly on her front thigh/hips (arms NOT raised overhead). Warm fair-to-light-olive skin, soft peach cheek blush, calm gaze forward, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Distinct from M4's `#75/#76` Anjaneyasana Crescent Low Lunge (where arms sweep overhead); here torso is upright with hands on front thigh/hips matching `#25` ("Keep torso upright and proud").

---

#### M3-02 — Reclining Figure-4 Glute (Right / Left) [#26, #27]
- **drawable_filename**: `img_stretch_b2_reclining_figure_four_glute.jpg`
- **ImageName**: `img_stretch_b2_reclining_figure_four_glute`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"figure_four_glute_right"`
  - `"reclining figure-4 glute (right)"`
  - `"figure_four_glute_left"`
  - `"reclining figure-4 glute (left)"`
- **Source Cues**:
  - #26 `prepCue`: "Lie on your back, cross right ankle over left knee, and reach through to hold left hamstring." / `holdCue`: "Flex right foot to protect knee. Gently pull left thigh closer to feel right outer glute."
  - #27 `prepCue`: "Cross left ankle over right knee, grasp behind right thigh, and draw legs toward chest." / `holdCue`: "Keep head and shoulders flat on mat. Maintain deep rhythmic breaths into left hip."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from an elevated three-quarter side angle of a young woman lying supine on her back centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Reclining Figure-4 Glute Stretch. The back of her head, shoulders, and upper back rest completely flat on the mat. Her left thigh is lifted and bent toward her chest while her right ankle crosses cleanly over the top of her left knee/thigh in an open figure-4 triangle shape with her right foot flexed. Both hands reach through to clasp behind her left hamstring/thigh, gently drawing the legs toward her chest. Warm fair-to-light-olive skin, soft peach cheek blush, eyes peacefully closed, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: 3/4 side-elevated view makes the crossed ankle-over-knee triangle and hands clasping behind the supporting thigh clearly legible.

---

#### M3-03 — Seated Butterfly / Baddha Konasana [#28, #63]
- **drawable_filename**: `img_stretch_b2_seated_butterfly_baddha_konasana.jpg`
- **ImageName**: `img_stretch_b2_seated_butterfly_baddha_konasana`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"seated_butterfly"`
  - `"seated butterfly adductor stretch"`
  - `"baddha_konasana_butterfly"`
  - `"baddha konasana (bound angle butterfly)"`
- **Source Cues**:
  - #28 `prepCue`: "Sit tall, bring soles of feet together, and let knees drop comfortably out to the sides." / `holdCue`: "Hold ankles, lengthen spine, and hinge forward from hips. Allow inner thighs to soften."
  - #63 `prepCue`: "Sit upright, bring soles of feet together in front of pelvis, letting knees open wide to sides." / `holdCue`: "Hold feet with hands, lift through crown of head, and gently draw torso forward while relaxing groin."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_neck.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration facing front in a slight high eye-level view of a young woman seated centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Seated Butterfly (Baddha Konasana). She sits barefoot with the soles of both feet pressed together cleanly in front of her pelvis and both knees dropped comfortably wide out to the sides. Both hands reach down to hold her ankles/feet, with her spine lengthened tall and hinging gently forward slightly from the hips with a serene calm expression. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared cross-catalog pose (`#28` + `#63`). Verify soles of feet touch together (NOT cross-legged Lotus) and hands hold ankles/feet.

---

#### M3-04 — Pigeon Pose (Right / Left Shin Forward & Eka Pada Rajakapotasana) [#29, #30, #94, #95]
- **drawable_filename**: `img_stretch_b2_pigeon_pose_prone.jpg`
- **ImageName**: `img_stretch_b2_pigeon_pose_prone`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"pigeon_pose_right"`
  - `"pigeon pose (right shin forward)"`
  - `"pigeon_pose_left"`
  - `"pigeon pose (left shin forward)"`
  - `"eka_pada_rajakapotasana_pigeon_right"`
  - `"eka pada rajakapotasana (pigeon - right)"`
  - `"eka_pada_rajakapotasana_pigeon_left"`
  - `"eka pada rajakapotasana (pigeon - left)"`
- **Source Cues**:
  - #29: "Bring right knee behind right wrist, slide left leg straight back behind you." / "Square your hips toward the mat. Lower to forearms if comfortable, breathing into right hip."
  - #30: "Bring left knee forward, extend right leg straight back, keeping hips level." / "Fold forward softly over your front leg. Let each exhale dissolve deep hip tension."
  - #94: "From tabletop, bring right knee behind right wrist, right foot angled across mat." / "Slide left leg straight back, square hips, and walk hands forward to lower chest toward floor."
  - #95: "Place left knee behind left wrist, angle shin, and extend right leg long behind." / "Square pelvis, rest forehead on folded arms, and breathe deeply into the outer left hip."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in three-quarter side profile of a young woman performing Sleeping/Forearm Pigeon Pose (Eka Pada Rajakapotasana) centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. Her front right knee is bent forward behind her right wrist with her right shin angled across the mat and hips squared level toward the mat, while her back left leg extends straight back along the mat with the top of her bare left foot flat. Her torso folds softly forward over her front shin, supported smoothly on both forearms resting flat on the mat with hands/forearms folded slightly in front of her and neck relaxed. Warm fair-to-light-olive skin, calm serene face, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared across all 4 pigeon entries (`#29`, `#30`, `#94`, `#95`). Lowered forearm-fold position matches all 4 cues cleanly (neither upright king-pigeon backbend nor ambiguous).

---

#### M3-05 — Lizard Lunge Inside Hip Opener [#31]
- **drawable_filename**: `img_stretch_b2_lizard_lunge_hip.jpg`
- **ImageName**: `img_stretch_b2_lizard_lunge_hip`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"lizard_lunge_hip"`
  - `"lizard lunge inside hip opener"`
- **Source Cues**:
  - `prepCue`: "From a low lunge, place both hands inside your front foot and walk foot slightly wide."
  - `holdCue`: "Sink hips forward and down. Keep chest open and spine long."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a three-quarter front-side view of a young woman performing Lizard Lunge Inside Hip Opener centered on a beige woven jute yoga mat over light honey-oak wood plank flooring. She is in a deep low lunge with her front right foot stepped wide toward the outer right edge of the mat (knee over ankle), her back left leg extended long behind her with the left knee and top of bare foot resting on the mat. Both straight arms reach down to plant both palms flat side-by-side on the mat directly to the INSIDE of her front right foot, while her hips sink forward and down and her chest stays open with a long straight spine. Warm fair-to-light-olive skin, soft peach cheek blush, calm gaze slightly ahead, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify BOTH hands are planted clearly to the INSIDE of the wide front foot (not straddling it).

---

#### M3-06 — 90-90 Seated Hip Rotator Stretch [#32]
- **drawable_filename**: `img_stretch_b2_ninety_ninety_hip.jpg`
- **ImageName**: `img_stretch_b2_ninety_ninety_hip`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"ninety_ninety_hip"`
  - `"90-90 seated hip rotator stretch"`
- **Source Cues**:
  - `prepCue`: "Sit with front leg bent 90 degrees in front and back leg bent 90 degrees to the side."
  - `holdCue`: "Sit upright with flat back and hinge forward over front shin to load external rotators."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from an elevated three-quarter front-side angle of a young woman seated on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing the 90-90 Seated Hip Rotator Stretch. Her front right leg rests flat on the mat directly in front of her pelvis with a clear 90-degree bend at the knee (thigh straight ahead, shin parallel to the front edge of the mat), while her back left leg is positioned to the side with a clear 90-degree bend at the knee (thigh out to the left side, shin angled straight back). With a tall flat back, she hinges slightly forward over her front shin, fingertips resting lightly on the mat on either side of her front shin for balance. Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Elevated 3/4 view ensures BOTH 90° leg angles (front shin across, back shin backward) and flat-back forward hinge are unmistakable.

---

#### M3-07 — Standing Forward Hamstring Fold / Uttanasana [#33, #70]
- **drawable_filename**: `img_stretch_b2_standing_forward_fold_uttanasana.jpg`
- **ImageName**: `img_stretch_b2_standing_forward_fold_uttanasana`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"standing_hamstring_fold"`
  - `"standing forward hamstring fold"`
  - `"uttanasana_forward_fold"`
  - `"uttanasana (standing forward fold)"`
- **Source Cues**:
  - #33: "Stand with feet hip-width, soften knees, and hinge at hips letting torso hang." / "Let head and arms hang heavy. Feel hamstrings and calves lengthen with steady breathing."
  - #70: "Stand with feet hip-width apart. Exhale, hinge forward from hips, and fold torso down." / "Keep knees softly bent if needed. Let crown of head and arms hang heavy like a pendulum."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman standing barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Standing Forward Fold (Uttanasana). Her feet are parallel hip-width apart and flat on the mat with a very gentle, soft micro-bend in her knees. She hinges deeply forward from her hips, draping her torso down close to her thighs with her head and neck completely relaxed (crown pointing toward the floor) and both arms hanging loose and heavy downward like a pendulum with fingertips hovering near her ankles/mat. Warm fair-to-light-olive skin, dark espresso-brown hair in a neat low bun with a couple of soft strands, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared cross-catalog pose (`#33` + `#70`). Distinct from M5's `#132` Padahastasana (where palms slide underneath the soles of the feet); here arms hang heavy like a pendulum.

---

#### M3-08 — Single-Leg Heel Hamstring Stretch [#34]
- **drawable_filename**: `img_stretch_b2_single_leg_hamstring_heel.jpg`
- **ImageName**: `img_stretch_b2_single_leg_hamstring_heel`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"single_leg_hamstring_heel"`
  - `"single-leg heel hamstring stretch"`
- **Source Cues**:
  - `prepCue`: "Place one heel forward with toes pointed up, bend back knee and hinge hips back."
  - `holdCue`: "Keep spine straight and reach hands toward front toes. Feel focused hamstring stretch."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing a Single-Leg Heel Hamstring Stretch centered barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring. Standing on her back left foot (planted flat with back left knee visibly bent as she shifts her hips backward), she extends her front right leg straight forward resting only her right heel on the mat with her right toes flexed straight upward toward the ceiling. Keeping her spine completely straight and flat, she hinges from her hips and reaches both hands forward toward her lifted front right toes/shin. Warm fair-to-light-olive skin, soft peach cheek blush, calm focus, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify front heel is down with toes pointed straight UP, back knee is bent, spine is straight (not rounded), hands reaching toward front toes.

---

#### M3-09 — Seated Single-Leg Fold / Janu Sirsasana [#35, #61, #62]
- **drawable_filename**: `img_stretch_b2_head_to_knee_janu_sirsasana.jpg`
- **ImageName**: `img_stretch_b2_head_to_knee_janu_sirsasana`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"seated_single_leg_fold"`
  - `"seated single-leg fold (janu sirsasana)"`
  - `"janu_sirsasana_right"`
  - `"janu sirsasana (head-to-knee - right)"`
  - `"janu_sirsasana_left"`
  - `"janu sirsasana (head-to-knee - left)"`
- **Source Cues**:
  - #35: "Extend one leg long, tuck opposite foot into inner thigh, and reach torso toward extended foot." / "Lead with your sternum rather than rounding shoulders. Breathe deeply into the back of leg."
  - #61: "Extend right leg forward, bend left knee and rest sole of left foot against right inner thigh." / "Square chest over right leg, hinge forward reaching for right foot with relaxed shoulders."
  - #62: "Extend left leg straight, bend right knee with sole against inner left thigh." / "Inhale to elongate spine, exhale folding forward over left thigh with smooth, calm breathing."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from a three-quarter front-side angle of a young woman seated centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Janu Sirsasana (Seated Single-Leg Forward Fold). Her right leg extends straight forward along the mat with the right foot flexed upward, while her left knee bends outward laterally onto the mat with the sole of her bare left foot tucked snugly against her right inner thigh. Squaring her torso over her straight right leg and leading forward with her sternum with a long unrounded spine and relaxed shoulders, she hinges forward from the hips and reaches both hands smoothly to hold around her right foot/ankle. Warm fair-to-light-olive skin, calm serene face, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared across `#35`, `#61`, `#62`. 3/4 view clearly distinguishes the one bent leg tucked into inner thigh from M4's `#60` Paschimottanasana (both legs straight).

---

#### M3-10 — Standing Quad Stretch (Right / Left Leg) [#36, #37]
- **drawable_filename**: `img_stretch_b2_standing_quad_stretch.jpg`
- **ImageName**: `img_stretch_b2_standing_quad_stretch`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"standing_quad_right"`
  - `"standing quad stretch (right leg)"`
  - `"standing_quad_left"`
  - `"standing quad stretch (left leg)"`
- **Source Cues**:
  - #36: "Stand tall, hold your right ankle behind you with right hand, drawing heel to glute." / "Keep both knees aligned and pelvis tucked under. Stand tall through your crown."
  - #37: "Grasp your left ankle behind you, bringing heel toward your glute. Use a wall for balance if needed." / "Avoid overarching your lower back. Feel the smooth stretch down the front of left thigh."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a three-quarter side-front view of a young woman standing upright barefoot centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Standing Quad Stretch. She balances steadily on her straight left leg, while bending her right knee behind her and holding her right ankle with her right hand, drawing her right heel smoothly toward her glute. Both knees remain aligned closely side by side (thigh pointing straight down, NOT flared out or lifted back like Dancer Pose), torso completely upright with neutral tucked pelvis and no lower-back arch, left arm relaxed cleanly at her side. Warm fair-to-light-olive skin, soft peach cheek blush, calm forward gaze, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify standing upright (no forward lean / backbend) with knees aligned side-by-side so it stays distinct from M5's `#124/#125` Natarajasana (Dancer Pose).

---

#### M3-11 — Side-Lying Quad & Hip Stretch [#38]
- **drawable_filename**: `img_stretch_b2_side_lying_quad.jpg`
- **ImageName**: `img_stretch_b2_side_lying_quad`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"side_lying_quad"`
  - `"side-lying quad & hip stretch"`
- **Source Cues**:
  - `prepCue`: "Lie on your side, hold top ankle, and gently draw your knee back in line with torso."
  - `holdCue`: "Engage top glute slightly to intensify front thigh stretch while keeping lower back neutral."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration from a front-facing floor-level angle of a young woman lying horizontally on her side centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing a Side-Lying Quad & Hip Stretch. Resting comfortably on her left side facing toward the viewer, her lower left arm is bent lightly under her head as a pillow and her bottom left leg lies straight (or softly bent) along the mat. Her top right knee bends behind her as her top right hand reaches back to grasp her right ankle, gently drawing her right thigh and knee back in line with her neutral torso while keeping her lower back neutral (not arched). Warm fair-to-light-olive skin, soft peach cheek blush, calm peaceful expression, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Verify top knee is drawn back in line with torso (not flaring high upward) and lower back remains neutral.

---

#### M3-12 — Wide-Legged Straddle Forward Fold / Prasarita Padottanasana [#39, #85]
- **drawable_filename**: `img_stretch_b2_wide_legged_forward_fold_prasarita.jpg`
- **ImageName**: `img_stretch_b2_wide_legged_forward_fold_prasarita`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"wide_legged_straddle_fold"`
  - `"wide-legged straddle forward fold"`
  - `"prasarita_padottanasana_wide_fold"`
  - `"prasarita padottanasana (wide-leg fold)"`
- **Source Cues**:
  - #39: "Step feet 3 to 4 feet apart with toes facing forward, hinge at hips and lower hands to mat." / "Shift weight slightly toward balls of feet. Relax head and neck completely."
  - #85: "Stand with feet 3-4 feet apart with outer edges parallel to mat edges. Place hands on hips." / "Hinge from hips to fold torso forward, placing palms on mat. Let crown of head descend smoothly."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration facing straight front of a young woman standing wide-legged centered on a beige woven jute yoga mat over light honey-oak wood plank flooring, performing Wide-Legged Straddle Forward Fold (Prasarita Padottanasana). Her bare feet are planted 3 to 4 feet apart along the length of the mat with outer foot edges parallel and toes pointing straight forward, legs straight. She hinges deeply forward from her hips, folding her torso straight down between her legs with both palms planted flat on the mat shoulder-width apart directly beneath her shoulders, letting her head and neck relax completely so the crown of her head descends smoothly toward the mat. Warm fair-to-light-olive skin, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared cross-catalog pose (`#39` + `#85`). Both cues end with palms/hands lowered onto the mat and head hanging relaxed downward.

---

#### M3-13 — Pyramid Pose / Parsvottanasana [#40, #91, #92]
- **drawable_filename**: `img_stretch_b2_pyramid_pose_parsvottanasana.jpg`
- **ImageName**: `img_stretch_b2_pyramid_pose_parsvottanasana`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"pyramid_pose_stretch"`
  - `"pyramid pose alignment stretch"`
  - `"parsvottanasana_pyramid_right"`
  - `"parsvottanasana (pyramid pose - right)"`
  - `"parsvottanasana_pyramid_left"`
  - `"parsvottanasana (pyramid pose - left)"`
- **Source Cues**:
  - #40: "Step one foot forward and one foot back, square hips forward, and fold over front thigh." / "Keep front leg straight with a micro-bend. Feel the deep upper hamstring release."
  - #91: "Step left foot back 3 feet at 45 degrees, both legs straight, square hips to front." / "Inhale to lengthen spine, exhale folding torso smoothly forward over right leg toward shin."
  - #92: "Step right foot back 3 feet, hips squared forward over straight left front leg." / "Hinge at hips, drawing left hip gently back, and drape torso forward with relaxed breathing."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing Pyramid Pose (Parsvottanasana) centered barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring. Her feet are staggered about 3 feet apart front-to-back with both heels firmly grounded flat on the mat—front right foot pointing straight ahead, back left foot angled slightly outward at 45 degrees—with both legs straight (a tiny soft micro-bend in the front knee) and hips squared straight forward. She hinges smoothly from her hips, draping her torso forward over her front right thigh toward her shin, with her hands resting lightly on her front shin/ankles (or fingertips touching the mat beside her front foot) and neck relaxed. Warm fair-to-light-olive skin, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Shared across `#40`, `#91`, `#92`. Verify both heels are grounded flat on the mat and both legs are straight (distinct from low lunge).

---

#### M3-14 — Wall Calf Stretch (Right / Left Leg Back) [#41, #42]
- **drawable_filename**: `img_stretch_b2_wall_calf_gastrocnemius.jpg`
- **ImageName**: `img_stretch_b2_wall_calf_gastrocnemius`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"wall_calf_gastrocnemius_right"`
  - `"wall calf stretch (right leg back)"`
  - `"wall_calf_gastrocnemius_left"`
  - `"wall calf stretch (left leg back)"`
- **Source Cues**:
  - #41: "Step right foot straight back, press heel flat to floor, and lean hands against wall." / "Keep right knee completely straight and hips facing forward. Feel the upper calf stretch."
  - #42: "Step left foot back, plant the heel firmly into ground, and bend front knee forward." / "Maintain a strong straight back leg. Breathe into the calf and Achilles."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing a Wall Calf Stretch (Gastrocnemius) barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring, facing a warm cream-plaster studio wall on the right edge of the room. Standing at arm's length from the wall, she leans forward with both arms extended and both palms pressed flat against the vertical studio wall at shoulder height. Her front left knee is bent forward over her planted left foot, while her back right leg is stepped straight back with the back right knee completely straight and locked-out long and the back right heel pressed completely flat down onto the mat, hips squared toward the wall. Barefoot (strictly NO shoes or socks). Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Sunlit minimalist yoga studio with natural window light on the left and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: Replaces sneakered legacy `img_stretch_calf.jpg`. CRITICAL: Must be barefoot, back knee **completely straight**, back heel **flat on mat**, palms against studio wall.

---

#### M3-15 — Soleus Bent-Knee Calf Stretch [#43]
- **drawable_filename**: `img_stretch_b2_wall_soleus_bent_knee.jpg`
- **ImageName**: `img_stretch_b2_wall_soleus_bent_knee`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"wall_soleus_bent_knee"`
  - `"soleus bent-knee calf stretch"`
- **Source Cues**:
  - `prepCue`: "Stand close to wall, step one foot back, and bend both knees with back heel pressed down."
  - `holdCue`: "Feel the stretch shift deeper toward your lower calf and Achilles tendon."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing a Soleus Bent-Knee Wall Calf Stretch barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring, standing closer to a warm cream-plaster studio wall on the right side of the room. Both elbows are slightly bent with both palms resting flat against the vertical wall at chest/shoulder height. She steps one foot slightly back behind the other and visibly bends BOTH her front knee AND her back knee downward into a low ankle-dorsiflexion crouch while keeping her back heel firmly pressed flat down onto the mat (NOT lifting the back heel). Barefoot (strictly NO shoes or socks). Warm fair-to-light-olive skin, soft peach cheek blush, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Sunlit minimalist yoga studio with natural window light on the left and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: CRITICAL distinction from `#41/#42`: standing closer to wall with **BOTH knees distinctly bent** and back heel still flat on the mat.

---

#### M3-16 — Downward Dog Calf Pedaling [#44] — `FLAG_R4_ALTERNATING_MOTION`
- **drawable_filename**: `img_stretch_b2_downward_dog_pedal.jpg`
- **ImageName**: `img_stretch_b2_downward_dog_pedal`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"downward_dog_pedal"`
  - `"downward dog calf pedaling"`
- **Source Cues**:
  - `prepCue`: "From hands and knees, lift hips high into an inverted V shape with arms pressing away."
  - `holdCue`: "Alternate pressing each heel toward the floor in slow, rhythmic pedaling motions."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in clean side profile of a young woman performing Downward Dog Calf Pedaling centered barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring. Her hands are planted shoulder-width apart with arms straight pressing her hips high into an inverted-V Downward-Facing Dog, head relaxed between her biceps. Crucially, her legs show the asymmetric calf-pedaling position: her back/left leg is completely straight with its heel pressed flat all the way down onto the mat, while her opposite/right knee is clearly bent forward with its right heel lifted high off the mat balancing on the ball of the right foot and toes. Single static pose—strictly NO motion lines, NO arrows. Warm fair-to-light-olive skin, dark espresso-brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera plant in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: **`FLAG_R4_ALTERNATING_MOTION`**: Static snapshot shows the signature one-heel-down / one-knee-bent-heel-up pedaling stride (visually distinct from `#52` symmetric `img_stretch_downward_dog.jpg`), though continuous alternating motion cannot be animated in a static image.

---

#### M3-17 — Kneeling Toe Tuck Foot Stretch [#45]
- **drawable_filename**: `img_stretch_b2_plantar_fascia_toe_tuck.jpg`
- **ImageName**: `img_stretch_b2_plantar_fascia_toe_tuck`
- **AspectRatio**: `16:9`
- **Normalized Keys**:
  - `"plantar_fascia_toe_tuck"`
  - `"kneeling toe tuck foot stretch"`
- **Source Cues**:
  - `prepCue`: "Kneel with all 10 toes tucked under, slowly sit weight back onto heels."
  - `holdCue`: "Keep spine upright. Support weight with hands on floor if sensation is too intense."
- **ImagePaths**:
  1. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  2. `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration in a clean side profile (slightly angled three-quarter view) of a young woman performing a Kneeling Toe Tuck Foot Stretch centered barefoot on a beige woven jute yoga mat over light honey-oak wood plank flooring. Kneeling with knees together on the mat, all ten toes are clearly tucked forward/underneath onto the balls of her feet with her heels raised vertically off the mat, sitting her hips straight back upright onto her raised heels. Her spine is upright and tall with relaxed shoulders and both hands resting gently palms-down on her thighs. Warm fair-to-light-olive skin, soft peach cheek blush, calm expression, dark espresso-brown hair with center part in a neat low bun, wearing a sage-olive sleeveless sports bra crop top and high-waisted ankle-length dark charcoal-grey yoga leggings. Warm sunlit cream-plaster minimalist yoga studio with natural window light and terracotta potted Monstera and ficus plants in soft watercolor wash. Fine dark ink outlines on practitioner and mat with soft matte watercolor shading, centered full-body 16:9 composition with generous margins, zero text, zero watermarks, zero mat logos, zero split frames.
- **R4 Notes**: CRITICAL: Unlike Vajrasana (`#103`, where tops of feet lie flat), here all toes MUST be tucked under against the mat with heels vertical supporting the sit bones.
