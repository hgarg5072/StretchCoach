# M4 Pose Prompt Specifications (`ArtworkBatch3.kt`, `img_stretch_b3_<key>.jpg`)

**Author**: Explorer 2 (`teamwork_explorer_i2_2`)  
**Milestone**: M4 (`StretchLibrary` Yoga Asanas `#55–#100` + `#54`)  
**Scope**: **25 canonical poses** covering **33 stretches** (`66` exact normalized `ArtworkBatch3` keys: lowercase trimmed `name` + `id` for 17 standalone poses + 8 L/R pairs). *(Note: `PROJECT.md` line 217 prose states "35 stretches", while the authoritative 150-stretch catalog table in `PROJECT.md` lines 113–159 assigns exactly 33 stretches to M4, summing `8+30+31+33+48 = 150`.)*

---

## 1. Summary & Coverage Table

| # | Cat # | Canonical Pose | `drawable_filename` | `ImagePaths` (2 refs) | Stretches Covered |
|---|---|---|---|---|---|
| M4-01 | #54 | `setu_bandhasana_interlaced` | `img_stretch_b3_setu_bandhasana_interlaced.jpg` | `bridge`, `child` | `Setu Bandhasana (Bridge Pose)` |
| M4-02 | #55, #56 | `trikonasana_triangle` | `img_stretch_b3_trikonasana_triangle.jpg` | `warrior`, `standing` | `Trikonasana (Triangle Pose - Right)` / `(Left)` |
| M4-03 | #59 | `ustrasana_camel_pose` | `img_stretch_b3_ustrasana_camel_pose.jpg` | `child`, `bridge` | `Ustrasana (Camel Pose)` |
| M4-04 | #60 | `paschimottanasana_seated_forward_fold` | `img_stretch_b3_paschimottanasana_seated_forward_fold.jpg` | `child`, `bridge` | `Paschimottanasana (Seated Forward Bend)` |
| M4-05 | #64 | `supta_baddha_konasana_reclining` | `img_stretch_b3_supta_baddha_konasana_reclining.jpg` | `bridge`, `lotus` | `Supta Baddha Konasana (Reclining Butterfly)` |
| M4-06 | #65, #66 | `ardha_matsyendrasana` | `img_stretch_b3_ardha_matsyendrasana.jpg` | `lotus`, `bridge` | `Ardha Matsyendrasana (Half Lord Twist - Right)` / `(Left)` |
| M4-07 | #67, #68 | `gomukhasana_arms` | `img_stretch_b3_gomukhasana_arms.jpg` | `lotus`, `tree` | `Gomukhasana (Cow Face Arms - Right)` / `(Left)` |
| M4-08 | #69 | `malasana_garland_yogi_squat` | `img_stretch_b3_malasana_garland_yogi_squat.jpg` | `tree`, `lotus` | `Malasana (Yogi Squat)` |
| M4-09 | #71, #72 | `virabhadrasana_1` | `img_stretch_b3_virabhadrasana_1.jpg` | `warrior`, `standing` | `Virabhadrasana I (Warrior I - Right)` / `(Left)` |
| M4-10 | #75, #76 | `anjaneyasana_crescent_low_lunge` | `img_stretch_b3_anjaneyasana_crescent_low_lunge.jpg` | `child`, `tree` | `Anjaneyasana (Crescent Low Lunge - Right)` / `(Left)` |
| M4-11 | #77 | `matsyasana_fish_pose` | `img_stretch_b3_matsyasana_fish_pose.jpg` | `bridge`, `child` | `Matsyasana (Fish Pose)` |
| M4-12 | #78 | `halasana_plow_pose` | `img_stretch_b3_halasana_plow_pose.jpg` | `bridge`, `child` | `Halasana (Plow Pose)` |
| M4-13 | #79 | `dhanurasana_bow_pose` | `img_stretch_b3_dhanurasana_bow_pose.jpg` | `child`, `bridge` | `Dhanurasana (Bow Pose)` |
| M4-14 | #80, #81 | `supta_padangusthasana` | `img_stretch_b3_supta_padangusthasana.jpg` | `bridge`, `child` | `Supta Padangusthasana (Reclining Leg - Right)` / `(Left)` |
| M4-15 | #82 | `viparita_karani_legs_wall` | `img_stretch_b3_viparita_karani_legs_wall.jpg` | `bridge`, `child` | `Viparita Karani (Legs-Up-The-Wall)` |
| M4-16 | #83 | `shavasana_corpse_pose` | `img_stretch_b3_shavasana_corpse_pose.jpg` | `bridge`, `lotus` | `Shavasana (Corpse Restorative Pose)` |
| M4-17 | #86, #87 | `garudasana_eagle` | `img_stretch_b3_garudasana_eagle.jpg` | `tree`, `lotus` | `Garudasana (Eagle Pose - Right)` / `(Left)` |
| M4-18 | #88 | `ananda_balasana_happy_baby` | `img_stretch_b3_ananda_balasana_happy_baby.jpg` | `bridge`, `child` | `Ananda Balasana (Happy Baby Pose)` |
| M4-19 | #89 | `salabhasana_locust_pose` | `img_stretch_b3_salabhasana_locust_pose.jpg` | `child`, `bridge` | `Salabhasana (Locust Pose)` |
| M4-20 | #90 | `urdhva_mukha_svanasana_updog` | `img_stretch_b3_urdhva_mukha_svanasana_updog.jpg` | `downward_dog`, `warrior` | `Urdhva Mukha Svanasana (Upward Dog)` |
| M4-21 | #93 | `utkatasana_chair_pose` | `img_stretch_b3_utkatasana_chair_pose.jpg` | `standing`, `warrior` | `Utkatasana (Chair Pose Stretch)` |
| M4-22 | #96, #97 | `supta_matsyendrasana_supine_twist` | `img_stretch_b3_supta_matsyendrasana_supine_twist.jpg` | `bridge`, `child` | `Supta Matsyendrasana (Supine Twist - Right)` / `(Left)` |
| M4-23 | #98 | `goddess_pose_utkata_konasana` | `img_stretch_b3_goddess_pose_utkata_konasana.jpg` | `standing`, `warrior` | `Utkata Konasana (Goddess Pose)` |
| M4-24 | #99 | `marjaryasana_cat_pose` | `img_stretch_b3_marjaryasana_cat_pose.jpg` | `child`, `bridge` | `Marjaryasana (Cat Pose Spinal Flexion)` |
| M4-25 | #100 | `bitilasana_cow_pose` | `img_stretch_b3_bitilasana_cow_pose.jpg` | `child`, `bridge` | `Bitilasana (Cow Pose Spinal Extension)` |

*(Reference path shorthand above: `<name>` expands to `app/src/main/res/drawable/img_stretch_<name>.jpg`.)*

---

## 2. Global Generation & Asset Rules (Mandatory for All 25 Poses)

1. **`generate_image` parameters**:
   - `AspectRatio`: `"16:9"`
   - `ImageName`: exact base filename without `.jpg` (e.g. `img_stretch_b3_setu_bandhasana_interlaced`).
   - `ImagePaths`: always pass the 2 exact absolute paths listed for that pose (selected from the same studio lighting variant — Variant A or Variant B — to prevent window/room collisions).
2. **Post-generation Android asset check**:
   - Save into `app/src/main/res/drawable/img_stretch_b3_<key>.jpg` as baseline JPEG (`1376x768`, quality 85, no alpha).
   - Every filename satisfies `^[a-z][a-z0-9_]*\.jpg$`.
3. **Unified Studio Art Style invariants baked into every prompt**:
   - Clean minimalist editorial ink-and-watercolor illustration matching the exact style of the reference images; crisp fine dark-brown/charcoal ink outlines on practitioner & mat, soft matte watercolor shading, softer lower-contrast watercolor washes in studio background.
   - Young adult woman with warm fair-to-light-olive skin, soft peach cheek blush, serene calm expression, dark brown hair with center part in a neat low bun at nape with subtle wisps.
   - Sage-olive sleeveless sports bra/crop top (bare midriff), high-waisted ankle-length dark charcoal-grey yoga leggings, barefoot.
   - Centered full-body on a rectangular beige woven jute yoga mat over light honey-oak wood plank flooring in a warm sunlit cream-plaster yoga studio with potted green plants (Monstera / ficus in terracotta pots) and natural window sunlight.
   - Single pose only, generous 16:9 framing margins (no cropped head, hands, or feet), **ZERO text, ZERO labels, ZERO watermarks, ZERO mat logos, ZERO split-panels, ZERO floating abstract pastel blobs**.

---

## 3. Pose-by-Pose Prompt Specifications (`M4-01` – `M4-25`)

---

### M4-01 — `#54` Setu Bandhasana (Bridge Pose)
- **Catalog #**: `54` | **Indian/Sanskrit (runtime)**: `सेतु बंधासन`
- **`drawable_filename`**: `img_stretch_b3_setu_bandhasana_interlaced.jpg`
- **`ImageName`**: `img_stretch_b3_setu_bandhasana_interlaced`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"setu bandhasana (bridge pose)"`
  - `"setu_bandhasana_bridge_pose"`
- **Source Cues (`StretchLibrary.kt:573-582`)**:
  - `prepCue`: `"Lie on back with knees bent, feet hip-width flat on mat close to sit bones."`
  - `holdCue`: `"Drive through heels to lift hips skyward. Roll shoulders under, interlace fingers, and expand chest."`
- **Anatomical Verification Checklist**:
  - Supine side profile; knees bent ~90° with feet flat hip-width on mat near sit bones.
  - Hips lifted high in a clean bridge line; chest visibly expanded upward.
  - **CRITICAL R4 FIX**: Unlike `img_stretch_bridge.jpg` (arms flat separately beside torso), shoulders MUST be visibly rolled/tucked underneath upper back, and both straight arms MUST meet directly underneath the elevated hips/pelvis with **fingers interlaced together resting on the jute mat**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Setu Bandhasana (Bridge Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene closed-eye expression, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. She lies supine on the mat (head to the left, feet to the right) with knees bent at 90 degrees and both feet planted flat hip-width apart close to her sit bones. Driving through her heels, her hips and pelvis are lifted high skyward into a strong diagonal bridge from shoulders to knees with her chest broadly expanded. Crucially, unlike the first reference image, both of her shoulders are rolled and tucked cleanly underneath her upper back, with both arms extended straight underneath her elevated torso and pelvis and her hands brought together with fingers tightly interlaced resting flat on the mat directly beneath her lifted hips. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-02 — `#55 / #56` Trikonasana (Triangle Pose - Right / Left)
- **Catalog #**: `55`, `56` | **Indian/Sanskrit (runtime)**: `त्रिकोणासन`
- **`drawable_filename`**: `img_stretch_b3_trikonasana_triangle.jpg`
- **`ImageName`**: `img_stretch_b3_trikonasana_triangle`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"trikonasana (triangle pose - right)"`
  - `"trikonasana_triangle_right"`
  - `"trikonasana (triangle pose - left)"`
  - `"trikonasana_triangle_left"`
- **Source Cues (`StretchLibrary.kt:583-602`)**:
  - `#55 prepCue`: `"Step feet wide, turn right foot out 90 degrees, and extend arms parallel to floor."`
  - `#55 holdCue`: `"Hinge from right hip, placing right hand on shin or ankle. Reach left arm high and rotate chest open."`
  - `#56 prepCue`: `"Turn left foot out 90 degrees, keep right foot angled inward, arms extended wide."`
  - `#56 holdCue`: `"Reach left arm forward, hinge down to left leg, and extend right fingers toward ceiling."`
- **Anatomical Verification Checklist**:
  - Wide standing stance along the mat; **both knees completely straight** (not bent).
  - Front foot (viewer's right) turned out 90° toward mat end; back foot angled slightly inward.
  - Torso hinged sideways over front leg; chest rotated open facing the camera; lower hand resting lightly on front shin/ankle; upper arm reaching straight vertically toward ceiling in one vertical line with lower arm; gaze softly upward toward raised fingers.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body front-facing view centered in a 16:9 frame of a young woman performing Trikonasana (Triangle Pose) lengthwise on a beige woven jute yoga mat with short side fringe. She has warm fair-to-light-olive skin, soft peach cheek blush, calm focused expression, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Her feet are stepped wide apart along the mat with both legs completely straight: her front foot (to the viewer's right) turns out 90 degrees toward the end of the mat, and her back foot (to the viewer's left) is angled slightly inward with outer edge grounded. Her torso hinges laterally from the front hip directly over the straight front leg, with her chest rotated open toward the viewer. Her lower hand rests lightly on her front shin near the ankle, while her upper arm reaches straight vertically toward the ceiling in a single vertical line with her lower arm and shoulders, head turned gently upward gazing toward her raised fingertips. She stands on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with a sunlit window on the left wall, a high wooden floating shelf with small ceramics, and a potted Monstera plant on the floor left. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around head, raised fingers, and feet, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-03 — `#59` Ustrasana (Camel Pose)
- **Catalog #**: `59` | **Indian/Sanskrit (runtime)**: `उष्ट्रासन`
- **`drawable_filename`**: `img_stretch_b3_ustrasana_camel_pose.jpg`
- **`ImageName`**: `img_stretch_b3_ustrasana_camel_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"ustrasana (camel pose)"`
  - `"ustrasana_camel_pose"`
- **Source Cues (`StretchLibrary.kt:623-632`)**:
  - `prepCue`: `"Kneel with knees hip-width apart, hips over knees, hands supporting sacrum fingers downward."`
  - `holdCue`: `"Lift sternum high, gently arch backward, reaching fingertips to heels if comfortable. Keep hips pressing forward."`
- **Anatomical Verification Checklist**:
  - Side-profile kneeling on shins with knees hip-width apart, tops of feet flat on mat.
  - Thighs vertical with hips stacked over knees and pressing forward.
  - Deep smooth thoracic backbend with sternum lifted high, shoulders rolled open, both arms reaching backward with hands/fingertips resting cleanly on her heels, head gently tilted back in line with upper spine.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Ustrasana (Camel Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene peaceful expression, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. She kneels upright on her shins near the center of the mat with knees hip-width apart and the tops of her feet resting flat on the mat behind her. Her thighs remain vertical with hips stacked directly over her knees and gently pressing forward, while her sternum lifts high and her spine arches smoothly backward into a graceful open-chest backbend. Both arms reach cleanly back and down so her hands and fingertips rest firmly on her heels, shoulders rolled open, throat long, and head gently tilted back in alignment with her upper spine. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-04 — `#60` Paschimottanasana (Seated Forward Bend)
- **Catalog #**: `60` | **Indian/Sanskrit (runtime)**: `पश्चिमोत्तानासन`
- **`drawable_filename`**: `img_stretch_b3_paschimottanasana_seated_forward_fold.jpg`
- **`ImageName`**: `img_stretch_b3_paschimottanasana_seated_forward_fold`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"paschimottanasana (seated forward bend)"`
  - `"paschimottanasana_seated_forward_fold"`
- **Source Cues (`StretchLibrary.kt:633-642`)**:
  - `prepCue`: `"Sit tall with legs straight forward, feet active and flexed. Inhale, lengthen spine overhead."`
  - `holdCue`: `"Hinge forward from hip creases, grasping shins, ankles, or feet. Lengthen chest forward with every breath."`
- **Anatomical Verification Checklist**:
  - Side-profile seated on sit bones with both legs completely straight forward together on the mat.
  - Feet active and flexed vertically (toes pointing straight up).
  - Torso hinged deeply forward from the hip creases with lengthened chest/spine reaching toward shins; both arms extended forward with hands grasping the outer edges of the flexed feet/ankles.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Paschimottanasana (Seated Forward Bend) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene closed-eye expression, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Seated on the mat facing right, both of her legs extend straight forward together flat along the mat with ankles together and feet actively flexed so toes point straight upward. She hinges deeply forward from her hip creases with a long, lengthened spine and open chest draping over her thighs, head bowed peacefully toward her shins, and both arms reaching straight forward with her hands grasping the outer edges of her flexed feet. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-05 — `#64` Supta Baddha Konasana (Reclining Butterfly)
- **Catalog #**: `64` | **Indian/Sanskrit (runtime)**: `सुप्त बद्ध कोणासन`
- **`drawable_filename`**: `img_stretch_b3_supta_baddha_konasana_reclining.jpg`
- **`ImageName`**: `img_stretch_b3_supta_baddha_konasana_reclining`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"supta baddha konasana (reclining butterfly)"`
  - `"supta_baddha_konasana_reclining"`
- **Source Cues (`StretchLibrary.kt:673-682`)**:
  - `prepCue`: `"Lie on your back, bring soles of feet together, letting knees fall open supported by gravity."`
  - `holdCue`: `"Place one hand on heart, one on belly. Allow deep, slow diaphragmatic breaths to soften the pelvic floor."`
- **Anatomical Verification Checklist**:
  - Slightly elevated three-quarter front-diagonal supine view along the mat so leg diamond and hand placement are both clearly legible.
  - Back and head resting flat on mat; eyes closed peacefully.
  - Soles of both feet pressed together near pelvis with knees bent and falling symmetrically wide open toward the mat.
  - **One hand resting gently over her heart/chest and the other hand resting gently on her lower belly**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body low three-quarter diagonal view centered in a 16:9 frame of a young woman performing Supta Baddha Konasana (Reclining Butterfly Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene peaceful closed-eye expression, and dark brown hair parted in the center in a neat low bun resting at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. She lies flat on her back centered along the mat with her head angled slightly toward the upper left and feet toward the lower right so her full pose is clearly visible: the soles of both bare feet are pressed together close to her pelvis while both knees fall wide open symmetrically toward the mat in a relaxed diamond shape. Her left hand rests gently flat over the center of her heart/upper chest and her right hand rests softly over her lower belly. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-06 — `#65 / #66` Ardha Matsyendrasana (Half Lord Twist - Right / Left)
- **Catalog #**: `65`, `66` | **Indian/Sanskrit (runtime)**: `अर्ध मत्स्येन्द्रासन`
- **`drawable_filename`**: `img_stretch_b3_ardha_matsyendrasana.jpg`
- **`ImageName`**: `img_stretch_b3_ardha_matsyendrasana`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"ardha matsyendrasana (half lord twist - right)"`
  - `"ardha_matsyendrasana_right"`
  - `"ardha matsyendrasana (half lord twist - left)"`
  - `"ardha_matsyendrasana_left"`
- **Source Cues (`StretchLibrary.kt:683-702`)**:
  - `#65 prepCue`: `"Seated, cross right foot over left thigh onto floor. Inhale left arm overhead."`
  - `#65 holdCue`: `"Hook left elbow outside right knee, place right hand behind hip, and rotate chest smoothly to the right."`
  - `#66 prepCue`: `"Cross left foot outside right knee, right leg bent on mat. Inhale right arm up."`
  - `#66 holdCue`: `"Wrap right elbow around left knee, rotate ribcage to the left while keeping both sit bones grounded."`
- **Anatomical Verification Checklist**:
  - Seated upright on mat with both sit bones grounded.
  - Bottom leg bent flat along mat with foot near opposite outer hip; top knee pointing vertically upright with foot planted flat on the mat outside the bottom knee/thigh.
  - Opposite elbow hooked cleanly outside the upright top knee; other arm behind the hip with palm planted on the mat for upright spinal support; chest and gaze rotated smoothly toward the twisted side.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body front three-quarter view centered in a 16:9 frame of a young woman seated performing Ardha Matsyendrasana (Half Lord of the Fishes Spinal Twist) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm serene expression, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Sitting tall with both sit bones grounded on the mat, her left leg is bent flat on the mat with her left heel tucked beside her right outer hip, and her right leg crosses over so her right knee points upward and her right bare foot is planted flat on the mat outside her left thigh. Her spine is tall and upright as she twists her ribcage smoothly to her right: her left elbow hooks cleanly outside her upright right knee with forearm pointing upward, while her right arm reaches behind her back with her right palm planted flat on the mat behind her hip for support, and her head turns gently to gaze over her right shoulder. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-07 — `#67 / #68` Gomukhasana (Cow Face Arms - Right / Left)
- **Catalog #**: `67`, `68` | **Indian/Sanskrit (runtime)**: `गोमुखासन`
- **`drawable_filename`**: `img_stretch_b3_gomukhasana_arms.jpg`
- **`ImageName`**: `img_stretch_b3_gomukhasana_arms`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"gomukhasana (cow face arms - right)"`
  - `"gomukhasana_arms_right"`
  - `"gomukhasana (cow face arms - left)"`
  - `"gomukhasana_arms_left"`
- **Source Cues (`StretchLibrary.kt:703-722`)**:
  - `#67 prepCue`: `"Reach right elbow up and drop right hand behind neck. Sweep left hand behind back toward shoulder blades."`
  - `#67 holdCue`: `"Clasp fingertips or hold clothing. Keep chest broad, spine tall, and chin level with floor."`
  - `#68 prepCue`: `"Reach left elbow overhead, drop left palm down spine. Reach right hand up behind lower back."`
  - `#68 holdCue`: `"Breathe deeply into ribcage and armpits. Keep neck elongated without hunching."`
- **Anatomical Verification Checklist**:
  - **Rear three-quarter view** seated upright on the mat so the signature Cow Face Arms clasp behind her upper back is 100% visible!
  - One elbow reaching straight upward beside her head with forearm dropping down between shoulder blades; opposite arm reaching from lower back upward along the spine so **fingertips clasp cleanly between her shoulder blades**.
  - Spine tall, neck elongated, chin level, shoulders broad without hunching; low bun and sage-olive racerback sports bra + charcoal leggings clearly recognizable.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body rear three-quarter view centered in a 16:9 frame of a young woman seated upright performing Gomukhasana (Cow Face Arms Pose) on a rectangular beige woven jute yoga mat, viewed from behind at a slight three-quarter angle so her clasped hands behind her back are completely visible. She has warm fair-to-light-olive skin, dark brown hair in a neat low bun at the nape of her neck, and her head upright with chin level in calm partial profile. She wears a sleeveless sage-olive green racerback sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Seated tall on the mat with legs crossed comfortably underneath her and spine long without hunching, her right arm bends straight upward so her right elbow points toward the ceiling beside her head and her right hand drops down behind her neck between her shoulder blades; simultaneously, her left arm sweeps behind her lower back reaching upward so her left and right fingertips clasp cleanly together at the center of her upper back. Her chest and shoulders remain broad and open. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above the raised elbow and around the mat, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-08 — `#69` Malasana (Yogi Squat)
- **Catalog #**: `69` | **Indian/Sanskrit (runtime)**: `मालासन`
- **`drawable_filename`**: `img_stretch_b3_malasana_garland_yogi_squat.jpg`
- **`ImageName`**: `img_stretch_b3_malasana_garland_yogi_squat`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"malasana (yogi squat)"`
  - `"malasana_garland_yogi_squat"`
- **Source Cues (`StretchLibrary.kt:723-732`)**:
  - `prepCue`: `"Stand with feet wider than hips, toes angled out. Bend knees and descend into a deep squat."`
  - `holdCue`: `"Bring palms together at heart, gently press elbows against inner knees to broaden collarbones."`
- **Anatomical Verification Checklist**:
  - Front-facing view centered on mat.
  - Deep low yogi squat with feet planted flat wider than hips, toes turned outward ~45°, knees wide over toes, hips hovering low between heels, torso upright and chest broad.
  - Palms pressed together in prayer at heart center with **elbows pressing gently outward against the inner knees**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body front-facing view centered in a 16:9 frame of a young woman performing Malasana (Yogi Squat) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm serene expression gazing straight ahead, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Her feet are planted flat on the mat wider than hip-width with toes angled outward at 45 degrees, knees bent deeply and open wide in a low yogi squat with her hips resting low between her heels. Her spine is upright and collarbones broadened as she brings both palms together in prayer (Anjali Mudra) at heart center, with her elbows resting gently against the insides of her knees to press them softly outward. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-09 — `#71 / #72` Virabhadrasana I (Warrior I - Right / Left)
- **Catalog #**: `71`, `72` | **Indian/Sanskrit (runtime)**: `वीरभद्रासन एक`
- **`drawable_filename`**: `img_stretch_b3_virabhadrasana_1.jpg`
- **`ImageName`**: `img_stretch_b3_virabhadrasana_1`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"virabhadrasana i (warrior i - right)"`
  - `"virabhadrasana_one_right"`
  - `"virabhadrasana i (warrior i - left)"`
  - `"virabhadrasana_one_left"`
- **Source Cues (`StretchLibrary.kt:743-762`)**:
  - `#71 prepCue`: `"Step left foot back 3-4 feet at 45 degrees, bend right knee to 90 degrees over ankle. Square hips."`
  - `#71 holdCue`: `"Sweep arms overhead with palms facing, root through outer edge of back heel, and lift chest."`
  - `#72 prepCue`: `"Step right foot back at 45 degrees, bend left knee over ankle, facing hips forward."`
  - `#72 holdCue`: `"Press through back heel, reach fingertips high, and breathe with focused strength."`
- **Anatomical Verification Checklist**:
  - Side-three-quarter lunge stance facing toward the front (right) end of the mat.
  - **Hips and chest squared forward** toward the front leg (NOT turned open to the side like Warrior II).
  - Front knee bent 90° directly over front ankle; back leg straight with **back foot angled ~45° and back heel firmly grounded flat on the mat** (not lifted on toes).
  - **Both arms swept straight overhead alongside the ears with palms facing inward toward each other**, chest lifted, gaze forward.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side three-quarter view centered in a 16:9 frame of a young woman performing Virabhadrasana I (Warrior I Pose) lengthwise on a beige woven jute yoga mat with short side fringe. She has warm fair-to-light-olive skin, soft peach cheek blush, calm focused expression gazing forward toward the right, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Facing toward the right end of the mat, her front right foot is planted forward with her right knee bent at 90 degrees directly over her right ankle, while her back left leg is extended straight behind her 3 to 4 feet back with her left foot angled outward at 45 degrees and her back heel rooted firmly flat onto the mat. Unlike Warrior II, her pelvis, hips, and chest are squared squarely forward toward the right front leg with her chest lifted proudly. Both of her arms are swept straight overhead alongside her ears reaching high toward the ceiling with palms facing each other and fingers extended. She stands on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with a sunlit window on the left wall, a high wooden floating shelf with small ceramics, and a potted Monstera plant on the floor left. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above her raised fingertips and around the mat, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-10 — `#75 / #76` Anjaneyasana (Crescent Low Lunge - Right / Left)
- **Catalog #**: `75`, `76` | **Indian/Sanskrit (runtime)**: `आञ्जनेयासन`
- **`drawable_filename`**: `img_stretch_b3_anjaneyasana_crescent_low_lunge.jpg`
- **`ImageName`**: `img_stretch_b3_anjaneyasana_crescent_low_lunge`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"anjaneyasana (crescent low lunge - right)"`
  - `"anjaneyasana_low_lunge_right"`
  - `"anjaneyasana (crescent low lunge - left)"`
  - `"anjaneyasana_low_lunge_left"`
- **Source Cues (`StretchLibrary.kt:783-802`)**:
  - `#75 prepCue`: `"Step right foot forward between hands, lower left knee to mat, untucking left toes."`
  - `#75 holdCue`: `"Lift torso, sweep arms overhead, and sink pelvis forward and down to release front of left hip."`
  - `#76 prepCue`: `"Step left foot forward, place right knee down on mat, and lift torso upright."`
  - `#76 holdCue`: `"Engage lower abdomen, lift heart upward, and feel the stretch through right anterior hip."`
- **Anatomical Verification Checklist**:
  - Side-profile low lunge facing right on the mat.
  - Front foot planted flat with front knee bent ~90° over ankle; **back knee, shin, and untucked top of back foot resting directly down on the mat**.
  - Pelvis sinking softly forward and down; torso upright with slight heart lift; **both arms extended overhead with palms facing inward**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Anjaneyasana (Crescent Low Lunge) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm serene expression gazing gently forward, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Facing right along the mat, her front foot is planted flat ahead with her front knee bent 90 degrees over her ankle, while her back knee rests down on the mat with her back shin and untucked top of her back foot resting flat on the mat behind her. Her pelvis sinks gently forward and downward to open the front of her back hip, while her torso lifts upright with an open heart and slight upward chest lift. Both arms sweep straight overhead alongside her ears reaching high with palms facing each other. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above her raised hands and around the mat, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-11 — `#77` Matsyasana (Fish Pose)
- **Catalog #**: `77` | **Indian/Sanskrit (runtime)**: `मत्स्यासन`
- **`drawable_filename`**: `img_stretch_b3_matsyasana_fish_pose.jpg`
- **`ImageName`**: `img_stretch_b3_matsyasana_fish_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"matsyasana (fish pose)"`
  - `"matsyasana_fish_pose"`
- **Source Cues (`StretchLibrary.kt:803-812`)**:
  - `prepCue`: `"Lie on back, slide hands palms-down beneath buttocks with elbows close to sides."`
  - `holdCue`: `"Press forearms down to arch chest up, gently tilting head until crown rests lightly on floor."`
- **Anatomical Verification Checklist**:
  - Side-profile supine view on mat (head left, legs straight together extending right).
  - Hands tucked palms-down underneath the buttocks with elbows close to torso and forearms pressed onto the mat.
  - Upper back and chest arched high off the mat, head tilted back so the **crown of the head rests lightly on the mat**, eyes closed.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Matsyasana (Fish Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene closed-eye expression, and dark brown hair parted in the center in a neat low bun tucked slightly to the side/nape. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Lying on her back along the mat (head to the left, feet to the right), both of her legs are extended straight together resting flat on the mat. Her arms are tucked close beside her torso with her hands slid palms-down beneath her buttocks and her elbows and forearms pressing firmly into the mat. Pushing through her forearms, her chest and upper thoracic spine arch high upward off the mat while her head tilts gently backward so the crown of her head rests lightly on the mat, opening her chest and throat. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-12 — `#78` Halasana (Plow Pose)
- **Catalog #**: `78` | **Indian/Sanskrit (runtime)**: `हलासन`
- **`drawable_filename`**: `img_stretch_b3_halasana_plow_pose.jpg`
- **`ImageName`**: `img_stretch_b3_halasana_plow_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"halasana (plow pose)"`
  - `"halasana_plow_pose"`
- **Source Cues (`StretchLibrary.kt:813-822`)**:
  - `prepCue`: `"Lie on back, lift legs overhead supporting lower back with hands until toes reach floor behind head."`
  - `holdCue`: `"Keep weight on top of shoulders, never directly on neck. Breathe smoothly and maintain soft throat."`
- **Anatomical Verification Checklist**:
  - Side-profile inversion on mat; weight supported on upper shoulders with back of head resting naturally and neck free of compression.
  - Upper arms resting on mat with **elbows bent and both palms supporting her lower/mid back**.
  - Hips lifted high over shoulders and **both straight legs extended overhead so her bare toes touch down lightly on the mat behind her head**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Halasana (Plow Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm closed-eye expression, and dark brown hair parted in the center in a neat low bun tucked cleanly at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Resting securely on the tops of her shoulders with her neck soft and clear of weight, her spine and hips lift vertically upward while both straight legs sweep overhead toward the left so the tips of her bare toes touch lightly down onto the yoga mat behind her head. Her upper arms rest on the mat with elbows bent and both hands gently supporting her lower back for stability. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the lifted hips and extended toes, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-13 — `#79` Dhanurasana (Bow Pose)
- **Catalog #**: `79` | **Indian/Sanskrit (runtime)**: `धनुरासन`
- **`drawable_filename`**: `img_stretch_b3_dhanurasana_bow_pose.jpg`
- **`ImageName`**: `img_stretch_b3_dhanurasana_bow_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"dhanurasana (bow pose)"`
  - `"dhanurasana_bow_pose"`
- **Source Cues (`StretchLibrary.kt:823-832`)**:
  - `prepCue`: `"Lie on stomach, bend knees, and reach hands back to hold outer ankles."`
  - `holdCue`: `"Inhale, kick shins back into hands, lifting chest and knees off floor like a drawn bow."`
- **Anatomical Verification Checklist**:
  - Side-profile prone backbend centered on mat; lower abdomen/pelvis resting on mat.
  - Knees bent, **both arms reaching straight back with hands firmly holding the outer ankles**.
  - **Both chest/shoulders AND thighs/knees clearly lifted off the mat** in a balanced curved bow shape.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Dhanurasana (Bow Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm focused expression gazing gently forward, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Balancing on her abdomen and pelvis centered on the mat (facing right), both of her knees are bent upward and both arms reach straight back behind her so her hands firmly clasp her outer ankles. By kicking her shins back into her hands, both her chest/upper torso in front and her thighs/knees in back are lifted visibly high off the mat, forming a clean, taut drawn-bow arc. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-14 — `#80 / #81` Supta Padangusthasana (Reclining Leg - Right / Left)
- **Catalog #**: `80`, `81` | **Indian/Sanskrit (runtime)**: `सुप्त पादांगुष्ठासन`
- **`drawable_filename`**: `img_stretch_b3_supta_padangusthasana.jpg`
- **`ImageName`**: `img_stretch_b3_supta_padangusthasana`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"supta padangusthasana (reclining leg - right)"`
  - `"supta_padangusthasana_right"`
  - `"supta padangusthasana (reclining leg - left)"`
  - `"supta_padangusthasana_left"`
- **Source Cues (`StretchLibrary.kt:833-852`)**:
  - `#80 prepCue`: `"Lie on back, raise right leg straight up, looping hands or strap around calf or foot."`
  - `#80 holdCue`: `"Keep left leg grounded flat, gently draw right leg toward chest while keeping tailbone rooted."`
  - `#81 prepCue`: `"Lie supine, extend right leg along mat, raise left leg vertical and clasp around foot or calf."`
  - `#81 holdCue`: `"Flex left foot, keep neck relaxed, and feel calm lengthening along posterior leg."`
- **Anatomical Verification Checklist**:
  - Side-profile supine view on mat (head left, bottom leg extended straight right along mat).
  - Tailbone, back, shoulders, and head grounded flat on mat with neck relaxed.
  - One leg grounded straight and flat along the mat; the other leg raised straight vertically ~90° toward the ceiling with foot flexed; **both hands reaching up to clasp around the raised calf/ankle**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Supta Padangusthasana (Reclining Hand-to-Leg Stretch) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene relaxed expression, and dark brown hair parted in the center in a neat low bun resting at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Lying flat on her back along the mat (head to the left, grounded leg to the right) with her tailbone, lower back, shoulders, and head resting flat on the mat, one leg stays completely straight and grounded flat along the mat while her other leg is raised straight vertically toward the ceiling with the raised foot flexed. Both of her arms reach gently upward so her hands clasp around the calf and ankle of the raised vertical leg, drawing it softly toward her chest while keeping her shoulders and neck relaxed. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above the raised vertical foot and around the mat, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-15 — `#82` Viparita Karani (Legs-Up-The-Wall)
- **Catalog #**: `82` | **Indian/Sanskrit (runtime)**: `विपरीत करणी`
- **`drawable_filename`**: `img_stretch_b3_viparita_karani_legs_wall.jpg`
- **`ImageName`**: `img_stretch_b3_viparita_karani_legs_wall`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"viparita karani (legs-up-the-wall)"`
  - `"viparita_karani_legs_wall"`
- **Source Cues (`StretchLibrary.kt:853-862`)**:
  - `prepCue`: `"Sit sideways near wall or mat edge, roll onto back, and extend legs vertically up against wall."`
  - `holdCue`: `"Rest arms at sides with palms turned upward. Close eyes and let gravity promote lymphatic drainage."`
- **Anatomical Verification Checklist**:
  - **CRITICAL R4 REQUIREMENT**: Must clearly show a **smooth warm cream-plaster studio wall directly supporting the backs of her vertically extended legs**!
  - Side-profile view: mat short edge meets the base of the left cream-plaster wall (with baseboard trim); buttocks/hips tucked right against the wall; both straight legs resting vertically upward flat against the cream studio wall; torso and head resting flat on the mat extending rightward; **both arms resting relaxed at her sides on the mat/floor with palms turned upward**; eyes closed peacefully.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Viparita Karani (Legs-Up-The-Wall Pose) on a rectangular beige woven jute yoga mat positioned perpendicular against a smooth warm cream-plaster studio wall on the left side of the room. She has warm fair-to-light-olive skin, soft peach cheek blush, serene closed-eye expression, and dark brown hair parted in the center in a neat low bun resting at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. The short left edge of the jute mat meets the baseboard of the vertical cream-plaster wall on the left; she lies flat on her back along the mat with her hips and sit bones tucked right up against the base of the wall, and both of her straight legs extend vertically straight up at 90 degrees resting their backs and heels gently against the vertical cream studio wall. Her torso and head rest flat on the mat extending toward the right, with both arms resting comfortably relaxed beside her body slightly away from her sides and her palms turned upward. Light honey-oak wood plank flooring covers the studio floor, with warm natural sunlight entering from a window on the right wall, a potted Monstera near the back wall, and a potted ficus plant near the right window. Fine dark-brown ink outlines on the foreground figure, mat, and wall edge, soft matte watercolor shading, lower-contrast washes in the background, generous margins above her raised feet and around the full figure, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-16 — `#83` Shavasana (Corpse Restorative Pose)
- **Catalog #**: `83` | **Indian/Sanskrit (runtime)**: `शवासन`
- **`drawable_filename`**: `img_stretch_b3_shavasana_corpse_pose.jpg`
- **`ImageName`**: `img_stretch_b3_shavasana_corpse_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"shavasana (corpse restorative pose)"`
  - `"shavasana_corpse_pose"`
- **Source Cues (`StretchLibrary.kt:863-872`)**:
  - `prepCue`: `"Lie comfortably flat on back, feet slightly apart falling outward naturally, arms relaxed beside body."`
  - `holdCue`: `"Release all muscular control, effort, and breath technique. Melt completely into profound silence and restorative peace."`
- **Anatomical Verification Checklist**:
  - Low three-quarter diagonal supine view along the mat so full flat-on-back posture, separated relaxed feet, and palms-up arms are all visible.
  - Legs completely flat and straight, **feet slightly apart and falling naturally outward**.
  - **Arms resting loosely alongside the body slightly angled away from hips with palms turned upward**; eyes gently closed in complete stillness.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body low three-quarter diagonal view centered in a 16:9 frame of a young woman resting in Shavasana (Corpse Restorative Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, deeply peaceful closed-eye expression, and dark brown hair parted in the center in a neat low bun resting at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Lying completely flat and still on her back along the center of the mat (head toward the upper left, feet toward the lower right), her legs are extended straight and relaxed with her bare feet spaced slightly apart and falling naturally outward to the sides. Both arms rest loosely beside her body slightly angled away from her hips with soft relaxed fingers and palms facing upward toward the ceiling. She rests on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the entire mat and body, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-17 — `#86 / #87` Garudasana (Eagle Pose - Right / Left)
- **Catalog #**: `86`, `87` | **Indian/Sanskrit (runtime)**: `गरुड़ासन`
- **`drawable_filename`**: `img_stretch_b3_garudasana_eagle.jpg`
- **`ImageName`**: `img_stretch_b3_garudasana_eagle`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_tree.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_lotus.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"garudasana (eagle pose - right)"`
  - `"garudasana_eagle_pose_right"`
  - `"garudasana (eagle pose - left)"`
  - `"garudasana_eagle_pose_left"`
- **Source Cues (`StretchLibrary.kt:893-912`)**:
  - `#86 prepCue`: `"Bend knees, cross right thigh over left, and wrap right elbow under left with forearms bound."`
  - `#86 holdCue`: `"Lift elbows to shoulder height, sink hips into a steady quarter squat, and maintain smooth breathing."`
  - `#87 prepCue`: `"Sink into a slight squat, wrap left thigh over right, and wrap left elbow under right with palms touching."`
  - `#87 holdCue`: `"Fix your focus on a single point, keep spine tall, and feel tension release across upper back."`
- **Anatomical Verification Checklist**:
  - Front-facing single-leg standing balance centered on mat.
  - Knees bent in a steady quarter-squat; one thigh crossed tightly over the standing thigh with top foot hooked behind the standing calf.
  - Arms intertwined in front of chest (one elbow wrapped under the other), **elbows lifted to shoulder height**, forearms vertical and twisted with **palms pressed together**; spine upright, focused gaze straight ahead.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body front-facing view centered in a 16:9 frame of a young woman performing Garudasana (Eagle Pose) standing on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm focused expression gazing straight ahead, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Balancing steadily on her left foot planted flat on the mat, she bends both knees into a balanced quarter squat, crossing her right thigh tightly over her left thigh and hooking her right toes behind her left calf. In front of her upright torso, her arms are intertwined with her right elbow wrapped beneath her left elbow, elbows lifted to shoulder height, forearms twisted vertically together, and palms pressed together cleanly in front of her face/chest without obscuring her focused eyes. She stands on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above her head and below her standing foot, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-18 — `#88` Ananda Balasana (Happy Baby Pose)
- **Catalog #**: `88` | **Indian/Sanskrit (runtime)**: `आनंद बालासन`
- **`drawable_filename`**: `img_stretch_b3_ananda_balasana_happy_baby.jpg`
- **`ImageName`**: `img_stretch_b3_ananda_balasana_happy_baby`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"ananda balasana (happy baby pose)"`
  - `"ananda_balasana_happy_baby"`
- **Source Cues (`StretchLibrary.kt:913-922`)**:
  - `prepCue`: `"Lie on back, bend knees toward armpits, flex feet, and hold outer edges of feet with hands."`
  - `holdCue`: `"Gently draw knees toward floor beside ribs while pressing sacrum and back flat against mat."`
- **Anatomical Verification Checklist**:
  - Three-quarter side-front supine view on mat so wide-open knees, vertical shins, and hands gripping outer edges of flexed feet are clearly readable.
  - Entire back, shoulders, head, and sacrum pressed flat against the mat.
  - Knees bent deeply wide toward armpits beside ribs, shins vertical, **feet flexed flat toward ceiling**, **hands gripping outer edges of both bare feet**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body three-quarter side view centered in a 16:9 frame of a young woman performing Ananda Balasana (Happy Baby Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene peaceful closed-eye expression, and dark brown hair parted in the center in a neat low bun resting at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Lying flat on her back along the mat (head toward the left, pelvis toward the right) with her entire spine, shoulders, and sacrum pressed flat against the mat, she bends both knees wide apart drawing them toward her armpits beside her ribcage, with both shins vertical and the soles of her flexed feet facing straight up toward the ceiling. Both arms reach up inside/along her knees so her hands firmly grasp the outer edges of her bare feet. She rests on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above her raised feet and around the mat, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-19 — `#89` Salabhasana (Locust Pose)
- **Catalog #**: `89` | **Indian/Sanskrit (runtime)**: `शलभासन`
- **`drawable_filename`**: `img_stretch_b3_salabhasana_locust_pose.jpg`
- **`ImageName`**: `img_stretch_b3_salabhasana_locust_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"salabhasana (locust pose)"`
  - `"salabhasana_locust_pose"`
- **Source Cues (`StretchLibrary.kt:923-932`)**:
  - `prepCue`: `"Lie face down with arms alongside body, palms facing down, legs straight behind you."`
  - `holdCue`: `"Inhale, peel chest, arms, and legs upward off mat. Lengthen from crown to toes without neck strain."`
- **Anatomical Verification Checklist**:
  - Side-profile prone back extension on mat; pelvis/belly resting on mat.
  - **Chest, both straight arms (alongside torso, palms facing down), AND both straight legs lifted cleanly upward off the mat**.
  - Head/neck elongated gently forward without cranking neck upward.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Salabhasana (Locust Pose) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, calm focused expression with neck long and gaze gently forward-downward, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Lying prone on her belly and pelvis centered on the mat (facing right), she peels her upper chest, shoulders, both straight arms, and both straight legs cleanly upward off the mat in a long graceful arc from crown to toes. Her straight arms hover lifted alongside her sides and hips with palms facing downward, and her straight legs are lifted together off the mat behind her with toes extended back. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-20 — `#90` Urdhva Mukha Svanasana (Upward Dog)
- **Catalog #**: `90` | **Indian/Sanskrit (runtime)**: `ऊर्ध्व मुख श्वानासन`
- **`drawable_filename`**: `img_stretch_b3_urdhva_mukha_svanasana_updog.jpg`
- **`ImageName`**: `img_stretch_b3_urdhva_mukha_svanasana_updog`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"urdhva mukha svanasana (upward dog)"`
  - `"urdhva_mukha_svanasana_updog"`
- **Source Cues (`StretchLibrary.kt:933-942`)**:
  - `prepCue`: `"Lie prone, hands flat on floor beside waist, tops of feet grounded."`
  - `holdCue`: `"Press through palms to straighten arms, lifting torso, hips, and knees off mat. Broaden chest."`
- **Anatomical Verification Checklist**:
  - Side-profile view on mat; **arms completely straight and vertical**, palms flat directly beneath shoulders.
  - **Tops of both feet (insteps) pressed flat onto the mat**; **torso, hips, thighs, and knees visibly lifted completely off the mat** (clear daylight/space between thighs/knees and mat — distinct from Cobra!).
  - Chest broadened forward, shoulders pressed down away from ears, gaze forward.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Urdhva Mukha Svanasana (Upward-Facing Dog Pose) on a beige woven jute yoga mat with short side fringe. She has warm fair-to-light-olive skin, soft peach cheek blush, calm serene expression gazing gently forward, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Facing toward the left or right along the mat, she supports her entire body weight exclusively on her palms and the tops of her untucked bare feet: both arms are completely straight and vertical with palms planted flat directly under her shoulders, and her chest, torso, pelvis, hips, thighs, and knees are all lifted cleanly in the air completely off the yoga mat with visible open space beneath her hips and thighs. Her shoulders draw down and back away from her ears and her chest broadens forward in a strong open backbend. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with a sunlit window on the left wall, a high wooden floating shelf with small ceramics, and a potted Monstera plant on the floor left. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-21 — `#93` Utkatasana (Chair Pose Stretch)
- **Catalog #**: `93` | **Indian/Sanskrit (runtime)**: `उत्कटासन`
- **`drawable_filename`**: `img_stretch_b3_utkatasana_chair_pose.jpg`
- **`ImageName`**: `img_stretch_b3_utkatasana_chair_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"utkatasana (chair pose stretch)"`
  - `"utkatasana_chair_pose"`
- **Source Cues (`StretchLibrary.kt:963-972`)**:
  - `prepCue`: `"Stand with feet together. Bend knees deeply as if sinking onto an imaginary low stool."`
  - `holdCue`: `"Extend arms overhead alongside ears, sink weight back into heels, and keep chest lifted."`
- **Anatomical Verification Checklist**:
  - Side-three-quarter view (approx 45° angle) centered on mat so both "feet together" and "deep squat sitting onto imaginary stool" are unmistakable.
  - Feet together flat on mat, knees bent deeply, hips seated back over heels, chest lifted with long diagonal torso, **both straight arms extended overhead alongside the ears with palms facing inward**.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body three-quarter side-front view centered in a 16:9 frame of a young woman performing Utkatasana (Chair Pose) standing on a beige woven jute yoga mat with short side fringe. She has warm fair-to-light-olive skin, soft peach cheek blush, calm determined expression gazing softly forward, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Standing centered on the mat at a 45-degree three-quarter angle facing right-forward, her bare feet and knees are pressed close together while she bends her knees deeply and sinks her hips backward as if sitting onto an imaginary low chair, weight rooted into her heels. Her chest stays lifted with a long torso angled slightly forward, and both arms extend straight diagonally upward alongside her ears in line with her spine with palms facing each other and fingers extended. She stands on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with a sunlit window on the left wall, a high wooden floating shelf with small ceramics, and a potted Monstera plant on the floor left. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins above her raised hands and below her feet, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-22 — `#96 / #97` Supta Matsyendrasana (Supine Twist - Right / Left)
- **Catalog #**: `96`, `97` | **Indian/Sanskrit (runtime)**: `सुप्त मत्स्येन्द्रासन`
- **`drawable_filename`**: `img_stretch_b3_supta_matsyendrasana_supine_twist.jpg`
- **`ImageName`**: `img_stretch_b3_supta_matsyendrasana_supine_twist`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"supta matsyendrasana (supine twist - right)"`
  - `"supta_matsyendrasana_twist_right"`
  - `"supta matsyendrasana (supine twist - left)"`
  - `"supta_matsyendrasana_twist_left"`
- **Source Cues (`StretchLibrary.kt:993-1012`)**:
  - `#96 prepCue`: `"Lie on back, hug right knee to chest, extend right arm out, and guide right knee across body to left."`
  - `#96 holdCue`: `"Anchor both shoulders to mat, turn gaze softly to the right, and exhale into the gentle spinal twist."`
  - `#97 prepCue`: `"Lie on back, bring left knee to chest, extend left arm wide, and lower left knee toward the right floor."`
  - `#97 holdCue`: `"Breathe into left ribcage and lateral waist. Allow gravity to softly deepen the twist."`
- **Anatomical Verification Checklist**:
  - Elevated three-quarter supine view along mat so single crossed bent knee, straight grounded bottom leg, pinned shoulders, extended arm, and turned head are all visible.
  - Bottom leg extended straight along mat; top knee bent ~90° and draped across torso toward opposite side of floor.
  - Both shoulder blades anchored flat on mat; one arm extended straight out to side on floor at shoulder height; opposite hand resting lightly on outer crossed knee; head turned gently toward the extended arm.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body slightly elevated three-quarter view centered in a 16:9 frame of a young woman performing Supta Matsyendrasana (Supine Spinal Twist) on a rectangular beige woven jute yoga mat. She has warm fair-to-light-olive skin, soft peach cheek blush, serene closed-eye expression, and dark brown hair parted in the center in a neat low bun at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Lying on her back along the mat, her bottom left leg remains extended straight along the mat while her top right knee is bent 90 degrees and guided across her torso toward the floor on her left side in a gentle spinal twist. Both of her shoulder blades remain anchored flat against the mat: her right arm extends straight out to her right side resting on the floor at shoulder level with palm up, her left hand rests softly on the outside of her crossed right knee, and her head turns gently toward her extended right arm. She rests on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose and extended arm, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-23 — `#98` Utkata Konasana (Goddess Pose)
- **Catalog #**: `98` | **Indian/Sanskrit (runtime)**: `उत्कट कोणासन`
- **`drawable_filename`**: `img_stretch_b3_goddess_pose_utkata_konasana.jpg`
- **`ImageName`**: `img_stretch_b3_goddess_pose_utkata_konasana`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_standing.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_warrior.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"utkata konasana (goddess pose)"`
  - `"goddess_pose_utkata_konasana"`
- **Source Cues (`StretchLibrary.kt:1013-1022`)**:
  - `prepCue`: `"Step feet wide, turn toes out 45 degrees, bend knees into a wide squat with knees over ankles."`
  - `holdCue`: `"Bring arms into cactus shape at 90 degrees, keep spine upright, and open chest proudly."`
- **Anatomical Verification Checklist**:
  - Front-facing view centered on mat.
  - Wide squat stance: feet planted wide, toes turned outward 45°, knees bent deeply tracking out directly over ankles, spine completely upright.
  - **Arms bent symmetrically in 90-degree cactus / goalpost shape** (upper arms horizontal at shoulder height, elbows bent 90°, forearms vertical, palms facing forward); chest proud and open.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body front-facing view centered in a 16:9 frame of a young woman performing Utkata Konasana (Goddess Pose) on a beige woven jute yoga mat with short side fringe. She has warm fair-to-light-olive skin, soft peach cheek blush, calm confident expression gazing straight ahead, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. Standing centered along the mat facing directly forward, her feet are stepped wide apart with her bare toes turned outward 45 degrees, and her knees are bent deeply into a symmetrical wide squat with knees stacked directly over her ankles. Her spine is tall and upright with an open proud chest, and both arms are held in a symmetrical 90-degree cactus (goalpost) shape with upper arms horizontal at shoulder level, elbows bent 90 degrees, forearms vertical, and palms open facing forward. She stands on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with a sunlit window on the left wall, a high wooden floating shelf with small ceramics, and a potted Monstera plant on the floor left. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-24 — `#99` Marjaryasana (Cat Pose Spinal Flexion)
- **Catalog #**: `99` | **Indian/Sanskrit (runtime)**: `मार्जारी आसन`
- **`drawable_filename`**: `img_stretch_b3_marjaryasana_cat_pose.jpg`
- **`ImageName`**: `img_stretch_b3_marjaryasana_cat_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"marjaryasana (cat pose spinal flexion)"`
  - `"marjaryasana_cat_pose"`
- **Source Cues (`StretchLibrary.kt:1023-1032`)**:
  - `prepCue`: `"Start on hands and knees with wrists under shoulders and knees directly below hips."`
  - `holdCue`: `"Exhale, press palms into mat, round spine toward ceiling, and let head relax down."`
- **Anatomical Verification Checklist**:
  - **CRITICAL DISTINCTION FROM #100**: Single pose ONLY (Cat Pose flexion — NO split panel, NO Cow Pose).
  - Side-profile tabletop on hands and knees; wrists directly under straight-arm shoulders, knees directly under hips, tops of feet flat on mat.
  - **Spine visibly rounded upward in a high convex dome toward the ceiling**, tailbone tucked down, head relaxed hanging down between arms gazing toward navel/thighs.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Marjaryasana (Cat Pose) alone on a rectangular beige woven jute yoga mat — showing only Cat Pose spinal flexion in a single unified scene. She has warm fair-to-light-olive skin, soft peach cheek blush, calm expression, and dark brown hair parted in the center in a neat low bun at the back of her head. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. On all fours facing right along the mat, her arms are straight with palms planted flat shoulder-width apart and wrists directly beneath her shoulders, and her knees are positioned directly below her hips with shins and tops of feet flat on the mat. Pressing firmly through her palms, she rounds her entire spine high upward toward the ceiling into a pronounced convex cat arch with her shoulder blades broadened, belly drawn up, tailbone tucked downward, and head dropped relaxed downward between her arms gazing softly toward her thighs. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

### M4-25 — `#100` Bitilasana (Cow Pose Spinal Extension)
- **Catalog #**: `100` | **Indian/Sanskrit (runtime)**: `बिटिलासन`
- **`drawable_filename`**: `img_stretch_b3_bitilasana_cow_pose.jpg`
- **`ImageName`**: `img_stretch_b3_bitilasana_cow_pose`
- **`AspectRatio`**: `16:9`
- **`ImagePaths`**:
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_child.jpg`
  - `/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_bridge.jpg`
- **Normalized `ArtworkBatch3.kt` Keys**:
  - `"bitilasana (cow pose spinal extension)"`
  - `"bitilasana_cow_pose"`
- **Source Cues (`StretchLibrary.kt:1033-1042`)**:
  - `prepCue`: `"From all fours on mat, prepare to mobilize spine with mindful breath."`
  - `holdCue`: `"Inhale, drop belly toward floor, lift chest and sitting bones high, gazing gently forward."`
- **Anatomical Verification Checklist**:
  - **CRITICAL DISTINCTION FROM #99**: Single pose ONLY (Cow Pose extension — NO split panel, NO Cat Pose).
  - Side-profile tabletop on hands and knees; wrists directly under straight-arm shoulders, knees directly under hips, tops of feet flat on mat.
  - **Belly visibly dropped/dipped low toward the mat in a pronounced concave spinal extension**, sitting bones/tailbone tilted high upward, chest lifted forward through the arms, head raised gazing gently forward.
- **Exact `generate_image` Prompt**:
```text
Clean minimalist editorial ink-and-watercolor illustration matching the exact character design, linework, and sunlit studio aesthetic of the reference images. Full-body side-profile view centered in a 16:9 frame of a young woman performing Bitilasana (Cow Pose) alone on a rectangular beige woven jute yoga mat — showing only Cow Pose spinal extension in a single unified scene. She has warm fair-to-light-olive skin, soft peach cheek blush, serene peaceful expression, and dark brown hair parted in the center in a neat low bun at the nape of her neck. She wears a sleeveless sage-olive green sports bra crop top, high-waisted ankle-length dark charcoal-grey yoga leggings, and is barefoot. On all fours facing right along the mat, her arms are straight with palms planted flat shoulder-width apart and wrists directly beneath her shoulders, and her knees are positioned directly below her hips with shins and tops of feet flat on the mat. In clear contrast to a rounded Cat Pose, she drops her belly deeply downward toward the mat into a smooth concave spinal extension, tilting her sitting bones and tailbone high upward while drawing her shoulders back, lifting her chest forward between her arms, and lifting her head to gaze gently forward. She is on light honey-oak wood plank flooring inside a warm cream-plaster minimalist yoga studio with natural sunlight from a window on the right, a potted Monstera in the back left, and a potted ficus plant in the back right. Fine dark-brown ink outlines on the foreground figure and mat, soft matte watercolor shading, lower-contrast washes in the background, generous margins around the full pose, single pose only, zero text, zero watermarks, zero mat logos, zero split-panels, zero abstract blobs.
```

---

## 4. Complete `ArtworkBatch3.kt` Key Mapping Verification (`66` Keys -> `25` Drawables)

```kotlin
// All 66 normalized keys for M4 (33 stretches x 2 keys each: name + id):
"setu bandhasana (bridge pose)" to R.drawable.img_stretch_b3_setu_bandhasana_interlaced,
"setu_bandhasana_bridge_pose" to R.drawable.img_stretch_b3_setu_bandhasana_interlaced,
"trikonasana (triangle pose - right)" to R.drawable.img_stretch_b3_trikonasana_triangle,
"trikonasana_triangle_right" to R.drawable.img_stretch_b3_trikonasana_triangle,
"trikonasana (triangle pose - left)" to R.drawable.img_stretch_b3_trikonasana_triangle,
"trikonasana_triangle_left" to R.drawable.img_stretch_b3_trikonasana_triangle,
"ustrasana (camel pose)" to R.drawable.img_stretch_b3_ustrasana_camel_pose,
"ustrasana_camel_pose" to R.drawable.img_stretch_b3_ustrasana_camel_pose,
"paschimottanasana (seated forward bend)" to R.drawable.img_stretch_b3_paschimottanasana_seated_forward_fold,
"paschimottanasana_seated_forward_fold" to R.drawable.img_stretch_b3_paschimottanasana_seated_forward_fold,
"supta baddha konasana (reclining butterfly)" to R.drawable.img_stretch_b3_supta_baddha_konasana_reclining,
"supta_baddha_konasana_reclining" to R.drawable.img_stretch_b3_supta_baddha_konasana_reclining,
"ardha matsyendrasana (half lord twist - right)" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
"ardha_matsyendrasana_right" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
"ardha matsyendrasana (half lord twist - left)" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
"ardha_matsyendrasana_left" to R.drawable.img_stretch_b3_ardha_matsyendrasana,
"gomukhasana (cow face arms - right)" to R.drawable.img_stretch_b3_gomukhasana_arms,
"gomukhasana_arms_right" to R.drawable.img_stretch_b3_gomukhasana_arms,
"gomukhasana (cow face arms - left)" to R.drawable.img_stretch_b3_gomukhasana_arms,
"gomukhasana_arms_left" to R.drawable.img_stretch_b3_gomukhasana_arms,
"malasana (yogi squat)" to R.drawable.img_stretch_b3_malasana_garland_yogi_squat,
"malasana_garland_yogi_squat" to R.drawable.img_stretch_b3_malasana_garland_yogi_squat,
"virabhadrasana i (warrior i - right)" to R.drawable.img_stretch_b3_virabhadrasana_1,
"virabhadrasana_one_right" to R.drawable.img_stretch_b3_virabhadrasana_1,
"virabhadrasana i (warrior i - left)" to R.drawable.img_stretch_b3_virabhadrasana_1,
"virabhadrasana_one_left" to R.drawable.img_stretch_b3_virabhadrasana_1,
"anjaneyasana (crescent low lunge - right)" to R.drawable.img_stretch_b3_anjaneyasana_crescent_low_lunge,
"anjaneyasana_low_lunge_right" to R.drawable.img_stretch_b3_anjaneyasana_crescent_low_lunge,
"anjaneyasana (crescent low lunge - left)" to R.drawable.img_stretch_b3_anjaneyasana_crescent_low_lunge,
"anjaneyasana_low_lunge_left" to R.drawable.img_stretch_b3_anjaneyasana_crescent_low_lunge,
"matsyasana (fish pose)" to R.drawable.img_stretch_b3_matsyasana_fish_pose,
"matsyasana_fish_pose" to R.drawable.img_stretch_b3_matsyasana_fish_pose,
"halasana (plow pose)" to R.drawable.img_stretch_b3_halasana_plow_pose,
"halasana_plow_pose" to R.drawable.img_stretch_b3_halasana_plow_pose,
"dhanurasana (bow pose)" to R.drawable.img_stretch_b3_dhanurasana_bow_pose,
"dhanurasana_bow_pose" to R.drawable.img_stretch_b3_dhanurasana_bow_pose,
"supta padangusthasana (reclining leg - right)" to R.drawable.img_stretch_b3_supta_padangusthasana,
"supta_padangusthasana_right" to R.drawable.img_stretch_b3_supta_padangusthasana,
"supta padangusthasana (reclining leg - left)" to R.drawable.img_stretch_b3_supta_padangusthasana,
"supta_padangusthasana_left" to R.drawable.img_stretch_b3_supta_padangusthasana,
"viparita karani (legs-up-the-wall)" to R.drawable.img_stretch_b3_viparita_karani_legs_wall,
"viparita_karani_legs_wall" to R.drawable.img_stretch_b3_viparita_karani_legs_wall,
"shavasana (corpse restorative pose)" to R.drawable.img_stretch_b3_shavasana_corpse_pose,
"shavasana_corpse_pose" to R.drawable.img_stretch_b3_shavasana_corpse_pose,
"garudasana (eagle pose - right)" to R.drawable.img_stretch_b3_garudasana_eagle,
"garudasana_eagle_pose_right" to R.drawable.img_stretch_b3_garudasana_eagle,
"garudasana (eagle pose - left)" to R.drawable.img_stretch_b3_garudasana_eagle,
"garudasana_eagle_pose_left" to R.drawable.img_stretch_b3_garudasana_eagle,
"ananda balasana (happy baby pose)" to R.drawable.img_stretch_b3_ananda_balasana_happy_baby,
"ananda_balasana_happy_baby" to R.drawable.img_stretch_b3_ananda_balasana_happy_baby,
"salabhasana (locust pose)" to R.drawable.img_stretch_b3_salabhasana_locust_pose,
"salabhasana_locust_pose" to R.drawable.img_stretch_b3_salabhasana_locust_pose,
"urdhva mukha svanasana (upward dog)" to R.drawable.img_stretch_b3_urdhva_mukha_svanasana_updog,
"urdhva_mukha_svanasana_updog" to R.drawable.img_stretch_b3_urdhva_mukha_svanasana_updog,
"utkatasana (chair pose stretch)" to R.drawable.img_stretch_b3_utkatasana_chair_pose,
"utkatasana_chair_pose" to R.drawable.img_stretch_b3_utkatasana_chair_pose,
"supta matsyendrasana (supine twist - right)" to R.drawable.img_stretch_b3_supta_matsyendrasana_supine_twist,
"supta_matsyendrasana_twist_right" to R.drawable.img_stretch_b3_supta_matsyendrasana_supine_twist,
"supta matsyendrasana (supine twist - left)" to R.drawable.img_stretch_b3_supta_matsyendrasana_supine_twist,
"supta_matsyendrasana_twist_left" to R.drawable.img_stretch_b3_supta_matsyendrasana_supine_twist,
"utkata konasana (goddess pose)" to R.drawable.img_stretch_b3_goddess_pose_utkata_konasana,
"goddess_pose_utkata_konasana" to R.drawable.img_stretch_b3_goddess_pose_utkata_konasana,
"marjaryasana (cat pose spinal flexion)" to R.drawable.img_stretch_b3_marjaryasana_cat_pose,
"marjaryasana_cat_pose" to R.drawable.img_stretch_b3_marjaryasana_cat_pose,
"bitilasana (cow pose spinal extension)" to R.drawable.img_stretch_b3_bitilasana_cow_pose,
"bitilasana_cow_pose" to R.drawable.img_stretch_b3_bitilasana_cow_pose
```
