# M5 Pose Prompt Specifications (`ExtraYogaAsanas` #101–#150, excl. #110 & #131)

**Target Milestone**: M5 (`ArtworkBatch4.kt` + `app/src/main/res/drawable/img_stretch_b4_*.jpg`)  
**Total Canonical Poses**: **38** (split cleanly into **Part A: 19 poses** `#101–#125` excl. `#110`, and **Part B: 19 poses** `#126–#150` excl. `#131`)  
**Total Catalog Stretches Covered**: **48** stretches (24 in Part A, 24 in Part B; **47 mapped stretches** + **37 generated drawables** when `#147` remains `null` under strict R4)  
**Cross-Milestone Exclusions (DO NOT register in `ArtworkBatch4.kt`)**:
- `#110` `pavanamuktasana_wind_relieving` → assigned to **M2** (`supine_double_knee_hug`)
- `#131` `shashankasana_hare` → assigned to **M1** (`coreReferenceMap` → `img_stretch_child.jpg`)

---

## 1. Global Unified Studio Style & Technical Parameters (Mandatory for All `generate_image` Calls)

- **Tool Parameters**:
  - `AspectRatio`: `"16:9"` (produces `1376x768` widescreen matching all 8 reference drawables)
  - `ImageName`: `img_stretch_b4_<key>` (save final verified JPEG as `app/src/main/res/drawable/img_stretch_b4_<key>.jpg`)
  - `ImagePaths`: Exact 2 reference paths listed per pose from `app/src/main/res/drawable/img_stretch_*.jpg`
- **Unified Studio Art Style Clause (Embedded in Every Prompt)**:
  - **Medium & Linework**: Clean minimalist editorial ink-and-watercolor illustration matching the reference images; crisp fine dark-brown/charcoal ink outlines on the foreground practitioner and woven mat, soft matte watercolor shading, softer lower-contrast watercolor washes in the studio background.
  - **Practitioner**: Young woman with warm fair-to-light-olive skin, subtle soft peach cheek blush, calm serene expression, dark brown hair with a center part pulled back into a neat low bun at the nape, sage-olive sleeveless scoop-neck sports bra/crop top, dark charcoal-grey high-waisted ankle-length yoga leggings, always barefoot.
  - **Environment & Framing**: Centered full-body on a rectangular beige woven jute yoga mat over light honey-oak wood plank flooring in a warm sunlit cream-plaster minimalist yoga studio with potted green plants (Monstera/ficus in terracotta pots) and soft morning window sunlight. Generous margin on all sides (no cropped head, hands, or feet), **zero text, zero labels, zero watermarks, zero mat logos ("OM"/"CALM"), zero floating pastel blobs**.
- **Key Normalization Rule (`ArtworkMapper.normalizeKey`)**:
  - `raw.trim().lowercase().replace(Regex("\\s+"), " ")`
  - Every mapped stretch in `ArtworkBatch4.exactNameMap` MUST register **both** its exact normalized `name` and exact `id` (verified by `ExampleUnitTest` contract: `byName == byId`).

---

## 2. Explicit R4 Audit: `#147 Surya Namaskar` & `#148–#150` Pranayama

### 2.1 `#147` `surya_namaskar_flow` — Surya Namaskar (Sun Salutation Flow) (`सूर्य नमस्कार`)
- **Source Cues (`ExtraYogaAsanas.kt:522-523`)**:
  - `prepCue`: `"Stand at top of mat in Prayer Pose and prepare to flow smoothly with each breath."`
  - `holdCue`: `"Move mindfully through reach, fold, lunge, plank, cobra, and downward dog."`
- **Strict R4 Verdict**: **RECOMMEND UNMAPPED (`null`) — DO NOT GENERATE OR MAP.**
- **Rationale**:
  1. `holdCue` describes a **6-pose dynamic vinyasa sequence** (`reach, fold, lunge, plank, cobra, and downward dog`) spanning a 60-second hold. A single static frame cannot faithfully depict a 6-pose sequential flow.
  2. Multi-panel collage strips or directional arrows violate the single-scene Unified Studio Art Style and trigger R4 text/artifact rejection.
  3. Generating only the `prepCue` starting position (Prayer Pose / *Pranamasana*) depicts only the 8-second setup—failing any independent R4 rubric checking the 60-second `holdCue` ("reach, fold, lunge, plank, cobra, and downward dog") and appearing nearly identical to `Tadasana` (`img_stretch_standing.jpg`).
  4. `PROJECT.md:218` explicitly specifies M5 covers **47 stretches** (`48 M5 stretches - #147 = 47`), aligning with leaving `#147` unmapped (`null`) to exercise R3's clean no-image UI and gallery state.

### 2.2 `#148–#150` Pranayama Visual Distinction Matrix (Cue-Faithful & Non-Overlapping)
| Cat # | Stretch | `prepCue` / `holdCue` Anchor | Visual Differentiator (No Fake Props/Icons) |
|---:|---|---|---|
| **#148** | `anulom_vilom_pranayama` | *"bring right thumb and ring finger near nostrils"* | Front-facing Sukhasana; left hand on left knee; **right arm raised cleanly with right hand in Vishnu/Nasagra Mudra** (index & middle fingers curled to palm, thumb beside right nostril, ring finger near left nostril), eyes closed. |
| **#149** | `bhramari_pranayama` | *"Sit tall with eyes closed, relax jaw... gentle, soothing humming sound"* | Front-facing Sukhasana (Variant B window right); **both hands resting palms-down on knees**, eyes softly closed, **visibly relaxed jaw and soft serene lips** (no hands on ears because cues do NOT instruct Shanmukhi Mudra; zero sound-wave graphics). |
| **#150** | `ujjayi_pranayama` | *"Sit comfortably upright, close eyes, and soften throat and shoulders... nasal ocean-wave whisper"* | Subtle 3/4 seated Sukhasana (Variant A window left); **hands cupped lightly in lap (Dhyana Mudra)**, **visibly dropped relaxed shoulders, lengthened open throat**, eyes closed, lips gently sealed (zero wave/water graphics). |

---

## 3. Part A — 19 Canonical Poses (`#101–#125`, excl. `#110` — 24 Stretches)

### Pose A1 (`#101`): Padmasana (Lotus Pose) — `padmasana_lotus_full`
- **Cat # / Stretch**: `#101` `Padmasana (Lotus Pose)` (`id`: `padmasana_lotus`, Indian: `पद्मासन`)
- **Drawable Filename**: `img_stretch_b4_padmasana_lotus_full.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"padmasana (lotus pose)"`
  - `"padmasana_lotus"`
- **Cues**:
  - `prepCue`: `"Sit tall and gently place each foot onto the opposite thigh with soles facing upward."`
  - `holdCue`: `"Rest hands on knees in chin mudra, lengthen your spine, and breathe calmly."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits tall facing the camera centered on a beige woven jute yoga mat in Full Lotus Pose (Padmasana). CRITICAL ANATOMY: BOTH bare feet are crossed symmetrically and placed high atop the opposite thighs near the hip creases with BOTH bare soles clearly turned upward (neither foot tucked underneath on the mat). Her knees rest low toward the mat, spine is long and upright, shoulders relaxed, eyes gently closed with a serene expression, and both hands rest directly on top of her knees palms-up in Chin Mudra (tips of thumb and index finger touching, remaining three fingers softly extended). Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window on the right, Monstera and ficus plants in terracotta pots, soft morning shadows, fine dark ink outlines on practitioner and mat with soft matte watercolor washes, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A2 (`#102`): Ardha Padmasana (Half Lotus Pose) — `ardha_padmasana_half_lotus`
- **Cat # / Stretch**: `#102` `Ardha Padmasana (Half Lotus Pose)` (`id`: `ardha_padmasana_half_lotus`, Indian: `अर्ध पद्मासन`)
- **Drawable Filename**: `img_stretch_b4_ardha_padmasana_half_lotus.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"ardha padmasana (half lotus pose)"`
  - `"ardha_padmasana_half_lotus"`
- **Cues**:
  - `prepCue`: `"Sit cross-legged and place one foot gently onto the opposite thigh."`
  - `holdCue`: `"Keep both sitting bones grounded, relax shoulders down, and lengthen through the crown."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_neck.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits facing the camera centered on a beige woven jute yoga mat in Half Lotus Pose (Ardha Padmasana). One foot (right foot) is tucked comfortably underneath the opposite calf resting on the mat, while ONLY the other bare foot (left foot) is placed gently atop the opposite right thigh with the sole angled upward. Both sitting bones are evenly grounded on the mat, spine lengthened upward through the crown, shoulders relaxed down away from ears, both hands resting calmly palms-down on her knees, serene neutral expression with soft forward gaze. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants in terracotta pots, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A3 (`#103`): Vajrasana (Thunderbolt Pose) — `vajrasana_thunderbolt`
- **Cat # / Stretch**: `#103` `Vajrasana (Thunderbolt Pose)` (`id`: `vajrasana_thunderbolt`, Indian: `वज्रासन`)
- **Drawable Filename**: `img_stretch_b4_vajrasana_thunderbolt.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"vajrasana (thunderbolt pose)"`
  - `"vajrasana_thunderbolt"`
- **Cues**:
  - `prepCue`: `"Kneel on the mat with big toes touching and sit your hips back onto your heels."`
  - `holdCue`: `"Rest palms on thighs, keep your back upright, and breathe slowly into the belly."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_child.jpg`
  2. `app/src/main/res/drawable/img_stretch_lotus.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, kneels in side profile facing right centered on a beige woven jute yoga mat in Thunderbolt Pose (Vajrasana). Her knees and shins are together flat along the mat, tops of her bare feet rest flat on the mat with big toes touching behind, and her hips sit squarely back directly atop her heels. Her torso and spine are completely upright and vertical, shoulders relaxed, head facing calmly forward, and both palms rest flat face-down on top of her mid-thighs with elbows soft. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window on the right, Monstera and ficus plants in terracotta pots, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A4 (`#104`): Sukhasana (Easy Seated Pose) — `sukhasana_easy_pose`
- **Cat # / Stretch**: `#104` `Sukhasana (Easy Seated Pose)` (`id`: `sukhasana_easy_pose`, Indian: `सुखासन`)
- **Drawable Filename**: `img_stretch_b4_sukhasana_easy_pose.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"sukhasana (easy seated pose)"`
  - `"sukhasana_easy_pose"`
- **Cues**:
  - `prepCue`: `"Sit comfortably cross-legged with shins relaxed and pelvis neutral."`
  - `holdCue`: `"Soften your shoulders away from ears and take slow, steady nasal breaths."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_neck.jpg`
  2. `app/src/main/res/drawable/img_stretch_lotus.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits facing the camera centered on a beige woven jute yoga mat in Easy Seated Pose (Sukhasana). Her legs are comfortably crossed at mid-shin with BOTH bare feet tucked naturally underneath the opposite shins resting flat on the mat (neither foot placed on top of a thigh), pelvis neutral, spine tall, shoulders visibly softened down away from her ears, both hands resting lightly palms-down on her knees, serene peaceful expression with eyes softly open gazing calmly forward. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit left window with potted plants and floating shelf, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A5 (`#105`): Siddhasana (Accomplished Pose) — `siddhasana_accomplished`
- **Cat # / Stretch**: `#105` `Siddhasana (Accomplished Pose)` (`id`: `siddhasana_accomplished`, Indian: `सिद्धासन`)
- **Drawable Filename**: `img_stretch_b4_siddhasana_accomplished.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"siddhasana (accomplished pose)"`
  - `"siddhasana_accomplished"`
- **Cues**:
  - `prepCue`: `"Sit with one heel close to the pelvis and the other tucked neatly in front."`
  - `holdCue`: `"Maintain an effortless upright spine and quiet rhythmic breathing."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_neck.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits facing the camera centered on a beige woven jute yoga mat in Accomplished Pose (Siddhasana). Her knees drop symmetrically wide toward the mat with one bare heel drawn in close directly against the center of the pelvis on the mat and the second bare heel tucked neatly directly in front of the first ankle along the midline (neither foot resting atop a thigh). Spine is effortlessly upright and tall, shoulders relaxed, hands resting lightly palms-down on her knees, eyes calmly closed with serene posture. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window on the right, Monstera and ficus plants in terracotta pots, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A6 (`#106`): Chakrasana (Wheel Pose) — `chakrasana_wheel`
- **Cat # / Stretch**: `#106` `Chakrasana (Wheel Pose)` (`id`: `chakrasana_wheel`, Indian: `चक्रासन`)
- **Drawable Filename**: `img_stretch_b4_chakrasana_wheel.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"chakrasana (wheel pose)"`
  - `"chakrasana_wheel"`
- **Cues**:
  - `prepCue`: `"Lie on your back, place feet near hips and palms beside ears, then press up into an arch."`
  - `holdCue`: `"Push evenly through palms and heels, open the chest, and breathe steadily."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, performs full Wheel Pose (Chakrasana) in clean side profile centered on a beige woven jute yoga mat. Both bare feet are planted flat hip-width apart on the right side of the mat with knees bent; both palms are planted flat on the left side of the mat under her shoulders with fingers pointing toward her feet and arms pressed straight; her torso, chest, and hips are lifted high into a smooth full-body upward arch with chest open and head hanging naturally relaxed between her arms. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted Monstera and ficus plants, fine dark ink outlines and soft matte watercolor shading, generous headroom above the arch with full body comfortably framed, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A7 (`#107`): Sarvangasana (Shoulder Stand) — `sarvangasana_shoulderstand`
- **Cat # / Stretch**: `#107` `Sarvangasana (Shoulder Stand)` (`id`: `sarvangasana_shoulderstand`, Indian: `सर्वांगासन`)
- **Drawable Filename**: `img_stretch_b4_sarvangasana_shoulderstand.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"sarvangasana (shoulder stand)"`
  - `"sarvangasana_shoulderstand"`
- **Cues**:
  - `prepCue`: `"Lie supine, lift legs overhead, support your mid-back with palms, and extend legs skyward."`
  - `holdCue`: `"Keep weight on shoulders rather than neck, gaze softly at toes, and breathe calmly."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Shoulder Stand (Sarvangasana) in clean side profile centered on a beige woven jute yoga mat. Back of her head, shoulders, and upper arms rest firmly on the mat with weight supported across her shoulders rather than her neck; elbows are planted on the mat with both palms supporting her mid-back; her torso, hips, and straight legs extend vertically straight upward toward the ceiling with bare feet together and gaze directed softly up toward her toes. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, generous margin above raised toes so full body is completely inside frame, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A8 (`#108`): Sirsasana (Supported Headstand) — `sirsasana_headstand`
- **Cat # / Stretch**: `#108` `Sirsasana (Supported Headstand)` (`id`: `sirsasana_headstand`, Indian: `शीर्षासन`)
- **Drawable Filename**: `img_stretch_b4_sirsasana_headstand.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"sirsasana (supported headstand)"`
  - `"sirsasana_headstand"`
- **Cues**:
  - `prepCue`: `"Interlace fingers on mat, cradle crown of head, engage forearms, and lift legs slowly."`
  - `holdCue`: `"Press forearms firmly into the mat, engage core, and keep shoulders lifted away from ears."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair neatly secured at the back of her head, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Supported Headstand (Sirsasana) in clean three-quarter side profile centered on a beige woven jute yoga mat. Both forearms press firmly flat onto the mat in a stable V-shape with fingers interlaced cradling the back and crown of her head lightly on the mat; shoulders are actively lifted away from her ears; her torso, core, and straight legs are stacked completely vertical upside down toward the ceiling with bare feet together. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, generous margin above raised feet so full body is completely framed, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A9 (`#109`): Naukasana (Boat Pose) — `naukasana_boat`
- **Cat # / Stretch**: `#109` `Naukasana (Boat Pose)` (`id`: `naukasana_boat`, Indian: `नौकासन`)
- **Drawable Filename**: `img_stretch_b4_naukasana_boat.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"naukasana (boat pose)"`
  - `"naukasana_boat"`
- **Cues**:
  - `prepCue`: `"Sit on sitting bones, lean torso back slightly, and lift legs and extended arms into a V."`
  - `holdCue`: `"Keep chest lifted, spine long, and breathe smoothly without tensing the neck."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `app/src/main/res/drawable/img_stretch_warrior.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Boat Pose (Naukasana) in clean side profile facing right centered on a beige woven jute yoga mat. She balances steadily on her sitting bones forming a crisp V-shape: her straight spine and lifted chest lean back ~45 degrees with relaxed neck and forward gaze, both straight legs are lifted together at ~45 degrees with bare feet active, and both arms extend straight forward parallel to the mat alongside her legs with palms facing inward. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants in terracotta pots, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A10 (`#111`): Makarasana (Crocodile Relaxation Pose) — `makarasana_crocodile`
- **Cat # / Stretch**: `#111` `Makarasana (Crocodile Relaxation Pose)` (`id`: `makarasana_crocodile`, Indian: `मकरासन`)
- **Drawable Filename**: `img_stretch_b4_makarasana_crocodile.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"makarasana (crocodile relaxation pose)"`
  - `"makarasana_crocodile"`
- **Cues**:
  - `prepCue`: `"Lie face down with toes turned outward and rest your forehead on stacked forearms."`
  - `holdCue`: `"Let the lower back soften completely with each slow belly exhale."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_child.jpg`
  2. `app/src/main/res/drawable/img_stretch_bridge.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, lies completely prone face down centered on a beige woven jute yoga mat in Crocodile Relaxation Pose (Makarasana), viewed from a slightly elevated side-three-quarter angle so her full body alignment is clear. Her belly and chest rest flat on the mat, legs extend straight behind slightly wider than hips with heels turned gently inward and bare toes turned outward, elbows bent out to the sides with forearms stacked one atop the other on the mat, and her forehead rests peacefully down on her stacked forearms with eyes closed and shoulders completely relaxed. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted Monstera and ficus plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A11 (`#112`): Bakasana (Crow Pose) — `bakasana_crow`
- **Cat # / Stretch**: `#112` `Bakasana (Crow Pose)` (`id`: `bakasana_crow`, Indian: `बकासन`)
- **Drawable Filename**: `img_stretch_b4_bakasana_crow.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"bakasana (crow pose)"`
  - `"bakasana_crow"`
- **Cues**:
  - `prepCue`: `"Squat low, plant palms shoulder-width, place knees high on upper arms, and shift forward."`
  - `holdCue`: `"Grip the mat with fingertips, draw heels toward glutes, and gaze slightly ahead."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `app/src/main/res/drawable/img_stretch_child.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Crow Pose (Bakasana) in three-quarter side profile facing right centered on a beige woven jute yoga mat. Both palms are planted flat shoulder-width apart on the mat with fingers spread gripping lightly and elbows slightly bent; her knees are placed high against the backs of her upper arms; her entire body is balanced on her hands with BOTH bare feet lifted completely off the mat and heels tucked close toward her glutes, torso shifted slightly forward, gazing calmly slightly ahead on the mat. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A12 (`#113`): Mayurasana (Peacock Pose) — `mayurasana_peacock`
- **Cat # / Stretch**: `#113` `Mayurasana (Peacock Pose)` (`id`: `mayurasana_peacock`, Indian: `मयूरासन`)
- **Drawable Filename**: `img_stretch_b4_mayurasana_peacock.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"mayurasana (peacock pose)"`
  - `"mayurasana_peacock"`
- **Cues**:
  - `prepCue`: `"Kneel, turn fingertips back toward knees, tuck elbows against abdomen, and extend legs."`
  - `holdCue`: `"Engage core and glutes to hover parallel to the floor while breathing evenly."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `app/src/main/res/drawable/img_stretch_warrior.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Peacock Pose (Mayurasana) in clean side profile facing right centered on a beige woven jute yoga mat. Both palms are planted flat on the mat beneath her center of gravity with fingers clearly turned backward pointing toward her feet, elbows bent and tucked firmly against her abdomen; her torso and straight legs are held horizontally parallel to the mat hovering cleanly in mid-air with bare feet together suspended off the floor and head gazing calmly forward. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A13 (`#114`): Chaturanga Dandasana (Four-Limbed Staff) — `chaturanga_dandasana`
- **Cat # / Stretch**: `#114` `Chaturanga Dandasana (Four-Limbed Staff)` (`id`: `chaturanga_dandasana`, Indian: `चतुरंग दंडासन`)
- **Drawable Filename**: `img_stretch_b4_chaturanga_dandasana.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"chaturanga dandasana (four-limbed staff)"`
  - `"chaturanga_dandasana"`
- **Cues**:
  - `prepCue`: `"From high plank, shift slightly forward and bend elbows to 90 degrees hugging ribs."`
  - `holdCue`: `"Keep body in one straight line from heels to crown with core firmly engaged."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `app/src/main/res/drawable/img_stretch_bridge.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Chaturanga Dandasana (Four-Limbed Staff Pose) in clean side profile facing left centered on a beige woven jute yoga mat. Both palms are planted flat on the mat beside her lower chest with elbows clearly bent to 90 degrees hugging tightly against the sides of her ribcage; her body hovers parallel to and a few inches above the mat in one completely straight level line from tucked bare toes and heels through core and shoulders to the crown of her head, neck neutral gazing down at the mat. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A14 (`#115`): Dandasana (Staff Pose) — `dandasana_staff`
- **Cat # / Stretch**: `#115` `Dandasana (Staff Pose)` (`id`: `dandasana_staff`, Indian: `दंडासन`)
- **Drawable Filename**: `img_stretch_b4_dandasana_staff.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"dandasana (staff pose)"`
  - `"dandasana_staff"`
- **Cues**:
  - `prepCue`: `"Sit with legs extended straight forward, feet flexed, and palms beside your hips."`
  - `holdCue`: `"Press lightly through palms to lift your sternum tall and engage the quadriceps."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits in Staff Pose (Dandasana) in clean side profile facing right centered on a beige woven jute yoga mat. Both legs are extended completely straight forward together flat along the mat with bare feet actively flexed (toes pointing straight upward); her spine is upright at a crisp 90-degree L-shape with sternum lifted tall; both arms hang straight down with palms resting flat on the mat directly beside her hips (fingers facing forward), gazing calmly straight ahead. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A15 (`#116` & `#117`): Ardha Chandrasana (Half Moon - Right / Left) — `ardha_chandrasana_half_moon`
- **Cat # / Stretches (L/R Pair)**:
  - `#116` `Ardha Chandrasana (Half Moon - Right)` (`id`: `ardha_chandrasana_right`, Indian: `अर्ध चन्द्रासन`)
  - `#117` `Ardha Chandrasana (Half Moon - Left)` (`id`: `ardha_chandrasana_left`, Indian: `अर्ध चन्द्रासन`)
- **Drawable Filename**: `img_stretch_b4_ardha_chandrasana_half_moon.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"ardha chandrasana (half moon - right)"`
  - `"ardha_chandrasana_right"`
  - `"ardha chandrasana (half moon - left)"`
  - `"ardha_chandrasana_left"`
- **Cues**:
  - `prepCue`: `"Balance on your right foot and right fingertips, lifting left leg parallel to floor."`
  - `holdCue`: `"Stack hips open, extend left arm skyward, and keep the lifted foot flexed."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_tree.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, balances gracefully in Half Moon Pose (Ardha Chandrasana) centered on a beige woven jute yoga mat with her chest and stacked hips facing the camera. She balances on one straight grounded bare foot with the matching bottom arm straight down and fingertips resting lightly on the mat slightly ahead of the standing foot; her top leg is lifted straight and horizontal parallel to the floor with the bare foot strongly flexed; her top arm extends straight vertically upward toward the ceiling in line with her shoulders, calm serene gaze. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins on all sides, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A16 (`#118` & `#119`): Utthita Parsvakonasana (Extended Side Angle - Right / Left) — `utthita_parsvakonasana_extended_side_angle`
- **Cat # / Stretches (L/R Pair)**:
  - `#118` `Utthita Parsvakonasana (Extended Side Angle - Right)` (`id`: `utthita_parsvakonasana_right`, Indian: `उत्थित पार्श्वकोणासन`)
  - `#119` `Utthita Parsvakonasana (Extended Side Angle - Left)` (`id`: `utthita_parsvakonasana_left`, Indian: `उत्थित पार्श्वकोणासन`)
- **Drawable Filename**: `img_stretch_b4_utthita_parsvakonasana_extended_side_angle.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"utthita parsvakonasana (extended side angle - right)"`
  - `"utthita_parsvakonasana_right"`
  - `"utthita parsvakonasana (extended side angle - left)"`
  - `"utthita_parsvakonasana_left"`
- **Cues**:
  - `prepCue`: `"From Warrior II on the right, rest right forearm on thigh and sweep left arm overhead."`
  - `holdCue`: `"Create one long diagonal line from left heel to left fingertips, opening chest upward."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Extended Side Angle Pose (Utthita Parsvakonasana) centered on a beige woven jute yoga mat with torso facing camera. From a wide Warrior II lunge stance (front knee bent 90 degrees over ankle, back leg straight with outer bare heel pressed flat into the mat), she rests her front forearm lightly across her bent front thigh and sweeps her top arm straight diagonally overhead past her ear, creating one crisp continuous diagonal line from her grounded back heel through her side waist to her extended fingertips, chest rotated open upward. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit left window, potted Monstera and floating shelf, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A17 (`#120` & `#121`): Parivrtta Trikonasana (Revolved Triangle - Right / Left) — `parivrtta_trikonasana_revolved_triangle`
- **Cat # / Stretches (L/R Pair)**:
  - `#120` `Parivrtta Trikonasana (Revolved Triangle - Right)` (`id`: `parivrtta_trikonasana_right`, Indian: `परिवृत्त त्रिकोणासन`)
  - `#121` `Parivrtta Trikonasana (Revolved Triangle - Left)` (`id`: `parivrtta_trikonasana_left`, Indian: `परिवृत्त त्रिकोणासन`)
- **Drawable Filename**: `img_stretch_b4_parivrtta_trikonasana_revolved_triangle.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"parivrtta trikonasana (revolved triangle - right)"`
  - `"parivrtta_trikonasana_right"`
  - `"parivrtta trikonasana (revolved triangle - left)"`
  - `"parivrtta_trikonasana_left"`
- **Cues**:
  - `prepCue`: `"Step right foot forward, hinge with flat back, place left hand near right foot, and twist right."`
  - `holdCue`: `"Keep hips squared back, lengthen spine on inhales, and rotate chest on exhales."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Revolved Triangle Pose (Parivrtta Trikonasana) in three-quarter side view centered on a beige woven jute yoga mat. Both bare feet are planted in a wide pyramid stance with BOTH legs straight and hips squared; she hinges forward with a long flat spine and twists her torso across toward the front leg so her OPPOSITE hand plants flat on the mat beside the front foot while the front-leg-side arm reaches straight upward toward the ceiling, chest rotated open toward the camera, calm steady gaze. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A18 (`#122` & `#123`): Hanumanasana (Monkey Split - Right / Left) — `hanumanasana_monkey_split`
- **Cat # / Stretches (L/R Pair)**:
  - `#122` `Hanumanasana (Monkey Split - Right)` (`id`: `hanumanasana_right`, Indian: `हनुमानासन`)
  - `#123` `Hanumanasana (Monkey Split - Left)` (`id`: `hanumanasana_left`, Indian: `हनुमानासन`)
- **Drawable Filename**: `img_stretch_b4_hanumanasana_monkey_split.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"hanumanasana (monkey split - right)"`
  - `"hanumanasana_right"`
  - `"hanumanasana (monkey split - left)"`
  - `"hanumanasana_left"`
- **Cues**:
  - `prepCue`: `"From low lunge with right leg forward, slowly slide right heel forward to a comfortable depth."`
  - `holdCue`: `"Keep hips square, support yourself with fingertips on floor or blocks, and breathe gently."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Monkey Split (Hanumanasana) in clean side profile facing right centered on a beige woven jute yoga mat. Her legs are extended in a full front-to-back split along the mat—front leg straight forward with bare heel grounded and toes flexed upward, back leg straight behind with top of shin and bare foot resting on the mat, hips squared forward, torso upright and tall, and BOTH arms extended down at her sides with fingertips resting lightly on the mat beside her hips for support, calm serene expression. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose A19 (`#124` & `#125`): Natarajasana (Dancer Pose - Right / Left) — `natarajasana_dancer`
- **Cat # / Stretches (L/R Pair)**:
  - `#124` `Natarajasana (Dancer Pose - Right)` (`id`: `natarajasana_right`, Indian: `नटराजासन`)
  - `#125` `Natarajasana (Dancer Pose - Left)` (`id`: `natarajasana_left`, Indian: `नटराजासन`)
- **Drawable Filename**: `img_stretch_b4_natarajasana_dancer.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"natarajasana (dancer pose - right)"`
  - `"natarajasana_right"`
  - `"natarajasana (dancer pose - left)"`
  - `"natarajasana_left"`
- **Cues**:
  - `prepCue`: `"Stand on your right leg, hold left ankle behind you, and kick gently into your hand."`
  - `holdCue`: `"Reach right arm forward, lift chest proudly, and fix gaze on a steady point."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_tree.jpg`
  2. `app/src/main/res/drawable/img_stretch_warrior.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Dancer Pose (Natarajasana) in clean side profile facing right centered on a beige woven jute yoga mat. She balances steadily on one straight standing leg with bare foot flat on the mat; her opposite leg bends upward behind her as the same-side hand clasps the inner rear ankle from behind, lifting the back thigh into a graceful arch; her chest is lifted proudly forward and her opposite arm reaches straight gracefully forward at eye level, gazing steadily ahead. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

---

## 4. Part B — 19 Canonical Poses (`#126–#150`, excl. `#131` — 24 Stretches; 18 Generated + `#147` R4 Null)

### Pose B1 (`#126`): Virasana (Hero Pose) — `virasana_hero`
- **Cat # / Stretch**: `#126` `Virasana (Hero Pose)` (`id`: `virasana_hero`, Indian: `वीरासन`)
- **Drawable Filename**: `img_stretch_b4_virasana_hero.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"virasana (hero pose)"`
  - `"virasana_hero"`
- **Cues**:
  - `prepCue`: `"Kneel with knees together and feet slightly wider than hips, lowering seat between heels."`
  - `holdCue`: `"Sit evenly on the mat or a block, lengthen spine, and relax hands onto thighs."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_child.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits in Hero Pose (Virasana) viewed from a front three-quarter angle centered on a beige woven jute yoga mat. Her knees are brought together in front while her shins and bare feet flare slightly wider than her hips with tops of feet flat on the mat beside her outer hips, and her seat rests evenly directly on the mat BETWEEN her heels (NOT sitting on top of the heels). Her spine is tall and upright, shoulders relaxed, and both palms rest softly face-down on her thighs, calm forward gaze. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B2 (`#127`): Supta Virasana (Reclining Hero Pose) — `supta_virasana_reclining_hero`
- **Cat # / Stretch**: `#127` `Supta Virasana (Reclining Hero Pose)` (`id`: `supta_virasana_reclining_hero`, Indian: `सुप्त वीरासन`)
- **Drawable Filename**: `img_stretch_b4_supta_virasana_reclining_hero.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"supta virasana (reclining hero pose)"`
  - `"supta_virasana_reclining_hero"`
- **Cues**:
  - `prepCue`: `"From Hero Pose, walk hands back onto elbows or recline gently onto a bolster."`
  - `holdCue`: `"Keep knees comfortable, allow chest and thighs to open, and take slow breaths."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `app/src/main/res/drawable/img_stretch_child.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Reclining Hero Pose (Supta Virasana) in clean side profile centered on a beige woven jute yoga mat. Kneeling with knees together in front and lower legs/bare feet folded back alongside her outer hips (tops of feet flat on the mat, hips resting between heels), she reclines her torso backward onto her elbows and forearms planted behind her on the mat, opening her chest and front thighs comfortably with neck neutral and eyes softly closed. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B3 (`#128`): Mandukasana (Frog Pose) — `mandukasana_frog`
- **Cat # / Stretch**: `#128` `Mandukasana (Frog Pose)` (`id`: `mandukasana_frog`, Indian: `मंडूकासन`)
- **Drawable Filename**: `img_stretch_b4_mandukasana_frog.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"mandukasana (frog pose)"`
  - `"mandukasana_frog"`
- **Cues**:
  - `prepCue`: `"From all fours, widen knees gently apart with shins parallel and rest on forearms."`
  - `holdCue`: `"Keep hips aligned with knees and exhale softly to release inner-thigh tension."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_child.jpg`
  2. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Frog Pose (Mandukasana) viewed from a front three-quarter angle centered on a beige woven jute yoga mat. Her knees are spread wide apart to the sides on the floor with thighs extending laterally at 90 degrees from her hips, shins parallel pointing straight backward, inner edges of ankles and flexed bare feet resting on the floor, and hips aligned cleanly in line with her knees; her upper body lowers onto both forearms and palms flat on the mat in front with spine long and gaze softly down. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B4 (`#129`): Simhasana (Lion Pose) — `simhasana_lion`
- **Cat # / Stretch**: `#129` `Simhasana (Lion Pose)` (`id`: `simhasana_lion`, Indian: `सिंहासन`)
- **Drawable Filename**: `img_stretch_b4_simhasana_lion.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"simhasana (lion pose)"`
  - `"simhasana_lion"`
- **Cues**:
  - `prepCue`: `"Kneel or sit tall, spread fingers wide on knees, inhale deeply through the nose."`
  - `holdCue`: `"Exhale with a gentle sigh, relaxing jaw, tongue, face, and throat muscles."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_neck.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, kneels upright facing the camera centered on a beige woven jute yoga mat in Lion Pose (Simhasana). Seated tall on her heels with knees comfortably apart, she presses both hands firmly onto her knees with straight arms and all ten fingers spread wide open like lion's paws; her mouth is gently opened with her tongue softly extended downward relaxing her jaw and throat, eyes open and serene in gentle editorial line art. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B5 (`#130`): Kurmasana (Tortoise Pose) — `kurmasana_tortoise`
- **Cat # / Stretch**: `#130` `Kurmasana (Tortoise Pose)` (`id`: `kurmasana_tortoise`, Indian: `कूर्मासन`)
- **Drawable Filename**: `img_stretch_b4_kurmasana_tortoise.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"kurmasana (tortoise pose)"`
  - `"kurmasana_tortoise"`
- **Cues**:
  - `prepCue`: `"Sit with legs wide and knees slightly bent, slide arms under knees, and fold forward."`
  - `holdCue`: `"Let shoulders and upper back soften downward as you breathe into your back ribs."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_child.jpg`
  2. `app/src/main/res/drawable/img_stretch_lotus.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Tortoise Pose (Kurmasana) viewed from a front three-quarter angle centered on a beige woven jute yoga mat. Seated with legs spread wide and knees slightly bent, she folds her torso deeply forward between her thighs, sliding BOTH arms completely underneath her bent knees outward/backward along the mat with palms resting flat on the floor, letting her head, shoulders, and upper back soften peacefully downward toward the mat. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B6 (`#132`): Padahastasana (Hand-to-Foot Forward Fold) — `padahastasana_hand_to_foot`
- **Cat # / Stretch**: `#132` `Padahastasana (Hand-to-Foot Forward Fold)` (`id`: `padahastasana_hand_to_foot`, Indian: `पादहस्तासन`)
- **Drawable Filename**: `img_stretch_b4_padahastasana_hand_to_foot.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"padahastasana (hand-to-foot forward fold)"`
  - `"padahastasana_hand_to_foot"`
- **Cues**:
  - `prepCue`: `"Fold forward from standing and slide palms underneath the soles of your feet."`
  - `holdCue`: `"Keep knees softly bent if needed, relax your head, and lengthen the back of legs."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_standing.jpg`
  2. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Hand-to-Foot Forward Fold (Padahastasana) in clean side profile centered on a beige woven jute yoga mat. Standing with feet hip-width apart and a subtle soft bend in her knees, she hinges deeply forward from the hips letting her head and neck relax heavy toward her shins, and slides BOTH hands palms-up completely underneath the soles of her bare feet so her toes rest on her inner wrists. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B7 (`#133` & `#134`): Vasisthasana (Side Plank - Right / Left) — `vasisthasana_side_plank`
- **Cat # / Stretches (L/R Pair)**:
  - `#133` `Vasisthasana (Side Plank - Right)` (`id`: `vasisthasana_right`, Indian: `वसिष्ठासन`)
  - `#134` `Vasisthasana (Side Plank - Left)` (`id`: `vasisthasana_left`, Indian: `वसिष्ठासन`)
- **Drawable Filename**: `img_stretch_b4_vasisthasana_side_plank.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"vasisthasana (side plank - right)"`
  - `"vasisthasana_right"`
  - `"vasisthasana (side plank - left)"`
  - `"vasisthasana_left"`
- **Cues**:
  - `prepCue`: `"Shift onto right palm and outer right foot, stacking legs and lifting hips high."`
  - `holdCue`: `"Extend left arm toward ceiling, engage obliques, and keep shoulder strong."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Side Plank Pose (Vasisthasana) with chest open toward the camera centered on a beige woven jute yoga mat. She balances on one straight supporting arm (palm planted flat directly beneath her shoulder) and the outer edge of her bottom bare foot, with both straight legs neatly stacked one directly atop the other, hips lifted high forming one straight diagonal plank line from ankles to head, and her top arm extended straight vertically upward toward the ceiling. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B8 (`#135` & `#136`): Skandasana (Side Lunge - Right / Left) — `skandasana_side_lunge`
- **Cat # / Stretches (L/R Pair)**:
  - `#135` `Skandasana (Side Lunge - Right)` (`id`: `skandasana_right`, Indian: `स्कंदासन`)
  - `#136` `Skandasana (Side Lunge - Left)` (`id`: `skandasana_left`, Indian: `स्कंदासन`)
- **Drawable Filename**: `img_stretch_b4_skandasana_side_lunge.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"skandasana (side lunge - right)"`
  - `"skandasana_right"`
  - `"skandasana (side lunge - left)"`
  - `"skandasana_left"`
- **Cues**:
  - `prepCue`: `"From a wide stance, bend deeply into your right knee while extending left leg straight."`
  - `holdCue`: `"Flex left toes toward ceiling, keep chest lifted, and feel the inner-thigh stretch."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_tree.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Side Lunge Pose (Skandasana) facing the camera centered on a beige woven jute yoga mat. She squats deeply into one bent knee with that bare foot flat on the mat while extending her opposite leg completely straight out to the side with the heel resting on the mat and bare toes flexed straight upward toward the ceiling; her chest is upright and lifted with palms pressed together in prayer at heart center, serene calm gaze. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B9 (`#137`): Tolasana (Scale Pose) — `tolasana_scale`
- **Cat # / Stretch**: `#137` `Tolasana (Scale Pose)` (`id`: `tolasana_scale`, Indian: `तोलासन`)
- **Drawable Filename**: `img_stretch_b4_tolasana_scale.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"tolasana (scale pose)"`
  - `"tolasana_scale"`
- **Cues**:
  - `prepCue`: `"Sit cross-legged or in lotus, plant palms beside hips, and press down to lift seat."`
  - `holdCue`: `"Draw knees upward with core strength and keep shoulders steady."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_standing.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Scale Pose (Tolasana) viewed from a front three-quarter angle centered on a beige woven jute yoga mat. With legs crossed in Lotus/cross-legged position, she plants BOTH palms flat on the mat directly beside her hips with straight strong arms, pressing down to lift her entire seat, hips, crossed legs, knees, and bare feet clearly several inches off the mat in mid-air with visible daylight and soft shadow underneath on the mat, spine tall and core engaged. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B10 (`#138` & `#139`): Bharadvajasana (Seated Sage Twist - Right / Left) — `bharadvajasana_sage_twist`
- **Cat # / Stretches (L/R Pair)**:
  - `#138` `Bharadvajasana (Seated Sage Twist - Right)` (`id`: `bharadvajasana_right`, Indian: `भरद्वाजासन`)
  - `#139` `Bharadvajasana (Seated Sage Twist - Left)` (`id`: `bharadvajasana_left`, Indian: `भरद्वाजासन`)
- **Drawable Filename**: `img_stretch_b4_bharadvajasana_sage_twist.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"bharadvajasana (seated sage twist - right)"`
  - `"bharadvajasana_right"`
  - `"bharadvajasana (seated sage twist - left)"`
  - `"bharadvajasana_left"`
- **Cues**:
  - `prepCue`: `"Sit with legs swept to the left, place left hand on right knee, and gently twist right."`
  - `holdCue`: `"Grow tall through the spine on inhales and soften into the rotation on exhales."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_neck.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, performs Seated Sage Twist (Bharadvajasana) viewed from a front three-quarter angle centered on a beige woven jute yoga mat. Sitting on her right hip with BOTH knees bent and shins/bare feet swept neatly together to her left side beside her outer left hip, she lengthens her spine tall and rotates her upper torso gently to the right, resting her left hand lightly on the outside of her right knee while her right hand rests on the mat behind her for upright support, head turned calmly toward her right shoulder. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B11 (`#140`): Upavistha Konasana (Seated Wide-Angle Fold) — `upavistha_konasana_wide_angle`
- **Cat # / Stretch**: `#140` `Upavistha Konasana (Seated Wide-Angle Fold)` (`id`: `upavistha_konasana_wide_angle`, Indian: `उपविष्ट कोणासन`)
- **Drawable Filename**: `img_stretch_b4_upavistha_konasana_wide_angle.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"upavistha konasana (seated wide-angle fold)"`
  - `"upavistha_konasana_wide_angle"`
- **Cues**:
  - `prepCue`: `"Sit with legs spread comfortably wide, toes pointing up, and walk hands forward."`
  - `holdCue`: `"Hinge from the hips with a long spine and breathe into inner thighs and hamstrings."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_child.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Seated Wide-Angle Fold (Upavistha Konasana) facing the camera centered on a beige woven jute yoga mat. Seated on her sitting bones with both straight legs spread comfortably wide in a symmetrical V along the mat and bare toes pointing straight upward, she hinges forward from her hips with a long flat spine and walks both hands forward onto the mat in front between her legs, gazing calmly forward toward the floor. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B12 (`#141`): Purvottanasana (Upward Plank Pose) — `purvottanasana_upward_plank`
- **Cat # / Stretch**: `#141` `Purvottanasana (Upward Plank Pose)` (`id`: `purvottanasana_upward_plank`, Indian: `पूर्वोत्तानासन`)
- **Drawable Filename**: `img_stretch_b4_purvottanasana_upward_plank.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"purvottanasana (upward plank pose)"`
  - `"purvottanasana_upward_plank"`
- **Cues**:
  - `prepCue`: `"Sit with legs straight, place palms behind hips, and press hips upward toward ceiling."`
  - `holdCue`: `"Open chest wide, reach toes toward floor, and keep neck comfortable."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_bridge.jpg`
  2. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Upward Plank Pose (Purvottanasana) in clean side profile centered on a beige woven jute yoga mat. Both arms are straight beneath her shoulders with palms planted flat on the mat (fingers pointing forward toward her feet); her legs are completely straight and together reaching forward with bare toes pressing downward toward the mat; her hips and chest lift high forming one straight upward diagonal plane from toes to shoulders, chest opened wide and neck held neutral and comfortable. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B13 (`#142` & `#143`): Parighasana (Gate Pose - Right / Left) — `parighasana_gate`
- **Cat # / Stretches (L/R Pair)**:
  - `#142` `Parighasana (Gate Pose - Right)` (`id`: `parighasana_gate_right`, Indian: `परिघासन`)
  - `#143` `Parighasana (Gate Pose - Left)` (`id`: `parighasana_gate_left`, Indian: `परिघासन`)
- **Drawable Filename**: `img_stretch_b4_parighasana_gate.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"parighasana (gate pose - right)"`
  - `"parighasana_gate_right"`
  - `"parighasana (gate pose - left)"`
  - `"parighasana_gate_left"`
- **Cues**:
  - `prepCue`: `"Kneel upright, extend right leg to the side, and arc left arm over toward right foot."`
  - `holdCue`: `"Open left ribcage toward ceiling and breathe deeply into the side body."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_tree.jpg`
  2. `app/src/main/res/drawable/img_stretch_warrior.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Gate Pose (Parighasana) facing the camera centered on a beige woven jute yoga mat. Kneeling upright on one knee with her hip stacked directly over that kneeling knee, she extends her opposite straight leg out to the side along the mat with bare foot resting on the mat; she arcs her torso into a graceful side bend toward the extended leg, resting her lower hand lightly along the extended shin while sweeping her top arm long overhead toward the extended foot, opening her top ribcage upward. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B14 (`#144` & `#145`): Virabhadrasana III (Warrior III - Right / Left) — `virabhadrasana_3`
- **Cat # / Stretches (L/R Pair)**:
  - `#144` `Virabhadrasana III (Warrior III - Right)` (`id`: `virabhadrasana_3_right`, Indian: `वीरभद्रासन तीन`)
  - `#145` `Virabhadrasana III (Warrior III - Left)` (`id`: `virabhadrasana_3_left`, Indian: `वीरभद्रासन तीन`)
- **Drawable Filename**: `img_stretch_b4_virabhadrasana_3.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"virabhadrasana iii (warrior iii - right)"`
  - `"virabhadrasana_3_right"`
  - `"virabhadrasana iii (warrior iii - left)"`
  - `"virabhadrasana_3_left"`
- **Cues**:
  - `prepCue`: `"Balance on your right foot, hinge torso forward, and lift left leg into a straight T."`
  - `holdCue`: `"Square hips toward floor, extend arms forward or to sides, and engage core."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_warrior.jpg`
  2. `app/src/main/res/drawable/img_stretch_tree.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds Warrior III (Virabhadrasana III) in clean side profile facing right centered on a beige woven jute yoga mat. She balances steadily on one straight standing leg with bare foot planted flat on the mat; her torso hinges horizontally forward parallel to the mat and her opposite leg lifts straight behind parallel to the mat with hips squared toward the floor and lifted bare foot flexed, forming a crisp level T-shape; both arms extend straight forward alongside her ears in one continuous straight horizontal line from back heel to fingertips, gazing calmly down toward the mat. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body centered with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B15 (`#146`): Phalakasana (Plank Pose) — `phalakasana_plank`
- **Cat # / Stretch**: `#146` `Phalakasana (Plank Pose)` (`id`: `phalakasana_plank`, Indian: `फलकासन`)
- **Drawable Filename**: `img_stretch_b4_phalakasana_plank.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"phalakasana (plank pose)"`
  - `"phalakasana_plank"`
- **Cues**:
  - `prepCue`: `"Place hands under shoulders and step feet back into a strong, level plank."`
  - `holdCue`: `"Press mat away, draw navel gently toward spine, and keep neck neutral."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_downward_dog.jpg`
  2. `app/src/main/res/drawable/img_stretch_bridge.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, holds High Plank Pose (Phalakasana) in clean side profile facing left centered on a beige woven jute yoga mat. Both arms are completely straight and vertical with palms planted flat on the mat directly underneath her shoulders; her legs are straight back with bare toes tucked onto the mat; her entire body forms one strong, level straight diagonal line from heels through hips and torso to crown of head (no sagging hips or arched back), neck neutral gazing softly down between her hands. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit window, potted green plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos.

### Pose B16 (`#147`): Surya Namaskar (Sun Salutation Flow) — **R4 UNMAPPED (`null`)**
- **Cat # / Stretch**: `#147` `Surya Namaskar (Sun Salutation Flow)` (`id`: `surya_namaskar_flow`, Indian: `सूर्य नमस्कार`)
- **Drawable Filename**: **NONE — DO NOT GENERATE (`null` in `ArtworkBatch4.kt`)**
- **Normalized Keys (`ArtworkBatch4.kt`)**: **OMIT** (explicitly returns `null` via `ArtworkMapper.getDrawableForStretch(...)` per R3 & R4).
- **Cues**:
  - `prepCue`: `"Stand at top of mat in Prayer Pose and prepare to flow smoothly with each breath."`
  - `holdCue`: `"Move mindfully through reach, fold, lunge, plank, cobra, and downward dog."`
- **R4 Assessment**: Do NOT generate a multi-panel flow strip or a standalone *Pranamasana* Prayer Pose image. A static single-frame illustration cannot verify accuracy against a 6-pose dynamic vinyasa flow (`reach, fold, lunge, plank, cobra, and downward dog`), and leaving `#147` unmapped (`null`) preserves strict R4 fidelity while keeping M5's exact 47-stretch target from `PROJECT.md:218`.

### Pose B17 (`#148`): Anulom Vilom (Alternate Nostril Breathing) — `anulom_vilom_pranayama`
- **Cat # / Stretch**: `#148` `Anulom Vilom (Alternate Nostril Breathing)` (`id`: `anulom_vilom_pranayama`, Indian: `अनुलोम विलोम प्राणायाम`)
- **Drawable Filename**: `img_stretch_b4_anulom_vilom_pranayama.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"anulom vilom (alternate nostril breathing)"`
  - `"anulom_vilom_pranayama"`
- **Cues**:
  - `prepCue`: `"Sit comfortably tall with spine erect and bring right thumb and ring finger near nostrils."`
  - `holdCue`: `"Inhale slowly through left nostril, switch, exhale right, inhale right, exhale left."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_neck.jpg`
  2. `app/src/main/res/drawable/img_stretch_lotus.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits upright facing the camera centered on a beige woven jute yoga mat practicing Alternate Nostril Breathing (Anulom Vilom Pranayama). She sits comfortably cross-legged with spine tall and erect; her left hand rests calmly on her left knee; her right arm is raised gently so her right hand forms Vishnu/Nasagra Mudra right near her nose—index and middle fingers softly curled inward toward her palm, with her right thumb poised lightly beside her right nostril and her ring finger poised lightly near her left nostril without blocking her serene face, eyes gently closed. Warm sunlit cream-plaster minimalist yoga studio with light honey-oak wood plank floor, sunlit left window with potted plants, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos, zero breathing arrows or wave graphics.

### Pose B18 (`#149`): Bhramari Pranayama (Humming Bee Breath) — `bhramari_pranayama`
- **Cat # / Stretch**: `#149` `Bhramari Pranayama (Humming Bee Breath)` (`id`: `bhramari_pranayama`, Indian: `भ्रामरी प्राणायाम`)
- **Drawable Filename**: `img_stretch_b4_bhramari_pranayama.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"bhramari pranayama (humming bee breath)"`
  - `"bhramari_pranayama"`
- **Cues**:
  - `prepCue`: `"Sit tall with eyes closed, relax jaw, and inhale deeply through the nose."`
  - `holdCue`: `"Exhale with a gentle, soothing humming sound, feeling calm vibration in the head."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_lotus.jpg`
  2. `app/src/main/res/drawable/img_stretch_neck.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits tall facing the camera centered on a beige woven jute yoga mat practicing Bhramari Pranayama. Seated comfortably cross-legged with bare feet tucked naturally underneath her shins, her spine is upright, shoulders soft, and BOTH hands rest calmly palms-down on her knees; her eyes are softly closed and her jaw, cheeks, and softly closed lips look deeply relaxed and serene in quiet humming stillness. Warm sunlit cream-plaster minimalist yoga studio (Variant B with sunlit window on the right, Monstera and ficus plants in terracotta pots) with light honey-oak wood plank floor, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos, zero sound waves or bee icons.

### Pose B19 (`#150`): Ujjayi Pranayama (Ocean Breath) — `ujjayi_pranayama`
- **Cat # / Stretch**: `#150` `Ujjayi Pranayama (Ocean Breath)` (`id`: `ujjayi_pranayama`, Indian: `उज्जायी प्राणायाम`)
- **Drawable Filename**: `img_stretch_b4_ujjayi_pranayama.jpg`
- **Normalized Keys (`ArtworkBatch4.kt`)**:
  - `"ujjayi pranayama (ocean breath)"`
  - `"ujjayi_pranayama"`
- **Cues**:
  - `prepCue`: `"Sit comfortably upright, close eyes, and soften throat and shoulders."`
  - `holdCue`: `"Breathe slowly through the nose with a soft ocean-wave whisper at the back of throat."`
- **`ImagePaths`**:
  1. `app/src/main/res/drawable/img_stretch_neck.jpg`
  2. `app/src/main/res/drawable/img_stretch_lotus.jpg`
- **`generate_image` Prompt**:
  > Clean minimalist editorial ink-and-watercolor illustration matching the reference studio art style. A calm barefoot young woman with warm fair-to-light-olive skin, subtle peach cheek blush, dark brown hair in a center-parted neat low bun, wearing a sage-olive sleeveless sports bra and dark charcoal-grey high-waisted ankle-length leggings, sits comfortably upright at a subtle three-quarter front angle centered on a beige woven jute yoga mat practicing Ujjayi Pranayama. Seated cross-legged with relaxed shins, her hands rest gently cupped one inside the other in her lap (Dhyana Mudra), her shoulders are visibly dropped and softened down away from her lengthened open throat, eyes peacefully closed, and lips softly sealed for slow mindful nasal breathing. Warm sunlit cream-plaster minimalist yoga studio (Variant A with sunlit window on the left, potted Monstera and wall shelf) with light honey-oak wood plank floor, fine dark ink outlines and soft matte watercolor shading, full body framed with generous margins, 16:9 widescreen, zero text, zero watermarks, zero mat logos, zero wave graphics.
