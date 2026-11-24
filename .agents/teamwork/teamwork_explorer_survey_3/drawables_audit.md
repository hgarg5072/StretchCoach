# Drawables & Artwork Mapping Audit — Stretch Coach

**Auditor**: Explorer 3 (`teamwork_explorer_survey_3`)  
**Scope**: Read-only inventory and visual audit of `app/src/main/res/drawable/`, other `res/` and `assets/` directories, `ArtworkMapper.kt`, UI/test references, and Android drawable technical constraints.

---

## 1. Complete File Inventory (`app/src/main/res/` & `assets/`)

### 1.1 `app/src/main/res/drawable/` (26 files total)
No other `res/drawable*` directories (e.g., `drawable-nodpi`, `drawable-hdpi`, `drawable-night`) exist.

#### A. Unified Studio Reference Drawables (8 files)
All 8 are JFIF baseline JPEG (3 RGB components, Exif TIFF 1-dir metadata), **1376 × 768 px** (aspect ratio **1.7917:1**, ~16:9 widescreen). Total size: **2,252,327 bytes** (~2.15 MiB; avg **281,540 B** / 275 KiB).

| Filename | Size (Bytes) | Dimensions | Aspect Ratio | Studio Variant |
|---|---:|---|---|---|
| `img_stretch_bridge.jpg` | 239,357 | 1376 × 768 | 1.79:1 | Variant B (window right) |
| `img_stretch_child.jpg` | 239,649 | 1376 × 768 | 1.79:1 | Variant B (window right) |
| `img_stretch_downward_dog.jpg` | 321,551 | 1376 × 768 | 1.79:1 | Variant A (window left) |
| `img_stretch_lotus.jpg` | 244,803 | 1376 × 768 | 1.79:1 | Variant B (window right) |
| `img_stretch_neck.jpg` | 324,783 | 1376 × 768 | 1.79:1 | Variant A (window left) |
| `img_stretch_standing.jpg` | 320,659 | 1376 × 768 | 1.79:1 | Variant A (window left) |
| `img_stretch_tree.jpg` | 241,371 | 1376 × 768 | 1.79:1 | Variant B (window right) |
| `img_stretch_warrior.jpg` | 320,154 | 1376 × 768 | 1.79:1 | Variant A (window left) |

#### B. Older Mixed-Style / Deprecated Stretch Drawables (15 files)
Total size: **6,779,508 bytes** (~6.47 MiB; avg **451,967 B** / 441 KiB).

| Filename | Size (Bytes) | Dimensions | Format / Aspect |
|---|---:|---|---|
| `img_stretch_butterfly.jpg` | 338,496 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_calf.jpg` | 275,141 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_cat_cow.jpg` | 460,861 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_chest.jpg` | 558,881 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_cobra.jpg` | 638,860 | 1408 × 768 | JPEG, 1.83:1, 300 DPI |
| `img_stretch_figure_four.jpg` | 279,627 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_floor.jpg` | 483,417 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_hamstring.jpg` | 330,956 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_lunge.jpg` | 331,688 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_pigeon.jpg` | 524,855 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_quad.jpg` | 518,438 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_side_bend.jpg` | 446,661 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_twist.jpg` | 581,295 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_upper.jpg` | 527,120 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |
| `img_stretch_wrist.jpg` | 483,212 | 1024 × 1024 | JPEG, 1:1 square, 300 DPI |

#### C. App Icon & Launcher Drawables (3 files in `res/drawable/`)
| Filename | Size (Bytes) | Dimensions / Type | Usage |
|---|---:|---|---|
| `img_app_icon.jpg` | 441,151 | 1024 × 1024 JPEG | Dark navy neon cyan-coral glowing yoga figure silhouette inside rounded square border. Used in `HeaderBar.kt:67` and `ic_launcher_foreground.xml:6`. |
| `ic_launcher_background.xml` | 890 | 108dp vector | Diagonal teal-to-slate linear gradient (`#0F172A` -> `#134E4A` -> `#0D9488`). Used by `mipmap-anydpi-v26/ic_launcher*.xml`. |
| `ic_launcher_foreground.xml` | 280 | layer-list XML | Centers `68dp × 68dp` `@drawable/img_app_icon`. Used by `mipmap-anydpi-v26/ic_launcher*.xml`. |

### 1.2 Other `res/` and `assets/` Directories
- `app/src/main/res/mipmap-anydpi-v26/`: `ic_launcher.xml`, `ic_launcher_round.xml` (both reference `@drawable/ic_launcher_background` and `@drawable/ic_launcher_foreground`).
- `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/`: `ic_launcher.webp`, `ic_launcher_round.webp`.
- `app/src/main/res/values/`: `colors.xml`, `firebase_applet_config.xml`, `strings.xml`, `themes.xml`.
- `app/src/main/res/xml/`: `backup_rules.xml`, `data_extraction_rules.xml`.
- `app/src/main/assets/`: **Does not exist.**
- `<project_root>/assets/`: Contains only `.aistudio/.gitignore` (not bundled in APK).
- `app/src/test/screenshots/greeting.png`: Roborazzi test screenshot artifact.

---

## 2. Visual Specification of the 8 Unified Studio Reference Drawables

### 2.1 Exact Unified Studio Art Style
1. **Rendering & Linework**: Clean minimalist editorial ink-and-watercolor illustration. Foreground practitioner and woven yoga mat have crisp, consistent, fine dark-brown/charcoal ink outlines (`~1.5–2px` visual weight) with soft matte watercolor/cel shading. Background architecture, plants, and shadows use softer, lower-contrast watercolor washes with minimal or faint outlines to create natural depth of field.
2. **Practitioner Character Design**:
   - **Appearance**: Young adult woman with warm fair-to-light-olive skin, subtle soft peach cheek blush, natural anatomical shading on shoulders, collarbones, elbows, knees, and feet.
   - **Face & Expression**: Calm, serene, neutral/peaceful expression; delicate dark eyebrows, small nose, soft closed or forward-gazing eyes, relaxed lips.
   - **Hair**: Dark brown / espresso hair with a clean center part, pulled back into a neat **low bun** at the back/nape of the head with a few subtle wispy strands near the ears/neck.
   - **Attire**: Sleeveless **sage-olive green** (`~#949E76`) scoop/racerback sports bra / crop top (bare midriff) and high-waisted, ankle-length **dark charcoal-grey** (`~#4B4E52`) yoga leggings. **Always barefoot** (no socks or sneakers).
3. **Floor & Yoga Mat**:
   - **Floor**: Light warm honey-oak wood plank flooring with subtle wood grain and perspective plank lines receding toward the back wall.
   - **Mat**: Rectangular **beige/tan woven jute yoga mat** centered horizontally on the floor, showing fine horizontal weave/stitch texture and soft contact shadows beneath the body. (Variant A adds short dark fringe tassels on the left and right ends; Variant B has clean finished borders.)
4. **Studio Environment (Two Closely Matched Camera Angles)**:
   - **Walls**: Warm cream/off-white plaster walls with subtle warm watercolor washes and light baseboard trim.
   - **Variant A (Window Left)** — `neck`, `standing`, `downward_dog`, `warrior`: Tall sunlit window on the left wall; small terracotta potted plant on the windowsill; wooden floating shelf high on the back-left wall holding ceramic bowls, vase, and small plant; larger Monstera plant in a terracotta pot on the floor left; warm wood door/wall trim on the far right edge.
   - **Variant B (Window Right)** — `lotus`, `tree`, `bridge`, `child`: Sunlit window on the right wall casting soft diagonal window-frame sunlight across the back wall and floor; Monstera plant in a beige-terracotta pot in the back-left corner; tall broad-leaf ficus/rubber plant in a terracotta pot in the back-right corner near the window; clean plaster back wall (no shelf).
5. **Lighting, Framing & Cleanliness**:
   - **Lighting**: Warm, diffused morning sunlight entering from the side window, casting soft diagonal light patches and gentle shadows across the wood planks and mat.
   - **Camera & Framing**: Straight-on, eye-level/low-studio-height perspective centered on the mat; full body is always comfortably framed inside the 1376 × 768 canvas with generous margin on all sides (no cropped limbs/head).
   - **No Artifacts**: Zero text, zero watermarks, zero mat logos ("OM"/"CALM"), zero floating abstract pastel blobs.

*(Note: `CustomAsanaImageGenerator.kt:48-54` already encodes most of this style in its Imagen prompt, though its prompt mentions "softly blurred potted green plant" and doesn't specify the exact low-bun hairstyle or warm oak planks vs cream plaster details.)*

### 2.2 Exact Anatomical Pose Shown in Each of the 8 Reference Drawables

| Drawable | Anatomical Pose Observed (Directly Verified) | Facing / Orientation | Accurate For |
|---|---|---|---|
| `img_stretch_neck.jpg` | **Seated Lateral Neck Stretch**: Practitioner sits cross-legged facing camera, eyes closed. Head tilts gently to her **left** (viewer's right); left arm reaches overhead with hand resting lightly over the right side of her head; right arm extends diagonally down with fingertips touching the mat beside her right hip. | Front-facing, seated | `Neck Side Stretch (Right)` / `(Left)` (symmetric pair). |
| `img_stretch_standing.jpg` | **Mountain Pose (Tadasana)**: Standing upright, feet parallel about hip-width apart on the mat, knees straight, spine tall, arms relaxed straight down at sides with palms turned slightly forward and fingers extended gently. Eyes open, gazing forward. | Front-facing, standing | `Tadasana (Mountain Pose Alignment)`. |
| `img_stretch_downward_dog.jpg` | **Downward-Facing Dog (Adho Mukha Svanasana)**: Inverted-V shape in side profile facing **left**. Palms planted flat shoulder-width apart, arms straight in line with lengthened spine, hips peaked high, legs straight, heels reaching toward mat (slightly hovering), head relaxed between biceps. | Side-profile facing left | `Adho Mukha Svanasana (Downward-Facing Dog)`, `Downward Dog Calf Pedaling` (if static DD frame is acceptable, though pedaling alternates heels). |
| `img_stretch_warrior.jpg` | **Warrior II (Virabhadrasana II)**: Wide stance facing camera with torso upright. Practitioner's **left** knee (viewer's right) is bent ~90° over the ankle with toes pointing toward viewer's right; **right** leg (viewer's left) is extended straight back with outer foot grounded. Arms extended horizontally at shoulder height in a straight T-line, palms down; head turned to gaze over front (viewer's right) fingertips. | Torso facing camera, lunge aligned left-right | `Virabhadrasana II (Warrior II - Right)` / `(Left)`. |
| `img_stretch_lotus.jpg` | **Lotus Pose (Padmasana) in Chin Mudra**: Seated facing camera with eyes closed and upright spine. Legs crossed in Lotus/Half-Lotus (feet resting upturned on opposite thighs/calves). Arms angled straight down to knees, hands resting palms-up in Chin/Jnana Mudra (thumb and index finger touching). | Front-facing, seated | `Padmasana (Lotus Pose)`; closely approximates `Ardha Padmasana`, `Siddhasana`, `Sukhasana` (though Sukhasana has ankles uncrossed), `Bhramari`/`Ujjayi Pranayama` (if seated meditation is acceptable, though dedicated pranayama/sukhasana poses are cleaner). |
| `img_stretch_tree.jpg` | **Tree Pose (Vrikshasana)**: Standing upright facing camera, eyes closed. Balancing on straight **right** leg (viewer's left); **left** knee bent out laterally with left foot sole pressed against inner right thigh (toes downward). Palms pressed together in Anjali Mudra at heart center. | Front-facing, standing | `Vrikshasana (Tree Pose - Right)` / `(Left)`. |
| `img_stretch_bridge.jpg` | **Bridge Pose (Setu Bandhasana)**: Supine side profile (head left, feet right), eyes closed. Back of head, shoulders, and extended arms/palms rest flat on the mat; knees bent ~90° with feet planted flat hip-width apart; hips and trunk lifted into a straight diagonal bridge from shoulders to knees. | Side-profile, supine | `Setu Bandhasana (Bridge Pose)`. |
| `img_stretch_child.jpg` | **Child's Pose (Balasana)**: Kneeling side profile facing **right**. Shins and tops of feet rest flat on mat, hips tucked back onto heels; torso folded forward over thighs with forehead resting down toward mat; arms extended straight forward along the mat, palms flat. | Side-profile facing right | `Balasana (Child's Pose)`, `Restorative Child's Pose`, `Shashankasana (Hare Pose)` (cue matches kneeling Vajrasana fold forward). |

---

## 3. Audit of the 15 Older Mixed-Style Drawables & Codebase References

### 3.1 Why Each of the 15 Older Drawables Must Be Replaced

| Filename | Dimensions & Size | Visual Description & Specific Style/Pose Defects |
|---|---|---|
| `img_stretch_butterfly.jpg` | 1024 × 1024 (338 KB) | **Flat 2D vector cartoon** on plain off-white background (no studio room/wood floor). Grey tank top, bright teal rectangular mat, simplified dot eyes. Pose: seated Baddha Konasana holding feet. |
| `img_stretch_calf.jpg` | 1024 × 1024 (275 KB) | **Flat lineless corporate vector** on plain white void. Faceless woman with ponytail, teal sports bra, **wearing grey sneakers**, pushing against a grey block wall on a tan rectangle. |
| `img_stretch_cat_cow.jpg` | 1024 × 1024 (461 KB) | **1:1 square**, monochrome grey long-sleeve crop top & grey leggings on plain cream mat, empty sunlit room with blurred window, shows only Cat Pose (rounded spine), not Cow. |
| `img_stretch_chest.jpg` | 1024 × 1024 (559 KB) | **1:1 square**, thick uniform vector outline, sage t-shirt (not sports bra), standing in an open grey doorway with a small white-potted plant, no mat. |
| `img_stretch_cobra.jpg` | 1408 × 768 (639 KB) | **Off-spec background/character & wrong pose**: 1408×768 (wider than 1376×768), beige solid floor (no oak planks), ponytail instead of low bun, wooden bench with folded towels, blurred greenery outside windows. **Pose is Upward-Facing Dog** (arms completely straight, thighs/hips lifted off mat), NOT Cobra Pose (gentle bend / hips grounded). |
| `img_stretch_figure_four.jpg` | 1024 × 1024 (280 KB) | **Flat lineless vector** on stark white background. Teal tank top, navy leggings, light grey mat, no studio interior. Pose: supine Figure-4 holding behind thigh. |
| `img_stretch_floor.jpg` | 1024 × 1024 (483 KB) | **1:1 square + wrong outfit/mat + actually depicts Child's Pose**: Dark teal long-sleeve shirt, bright teal mat, empty room with single small vase. Shows Balasana (Child's Pose), NOT supine knee-to-chest / Shavasana / floor stretches! |
| `img_stretch_hamstring.jpg` | 1024 × 1024 (331 KB) | **Flat 2D vector** on plain beige/off-white background. Bright teal t-shirt, light blue mat, simplified cartoon face. Pose: seated Janu Sirsasana (single-leg fold). |
| `img_stretch_lunge.jpg` | 1024 × 1024 (332 KB) | **Flat 2D vector** on plain grey/white background. **Short pixie-cut hair**, bright teal tank top, light blue mat, no studio room. Pose: kneeling low lunge with hands on front knee. |
| `img_stretch_pigeon.jpg` | 1024 × 1024 (525 KB) | **1:1 square**, dark charcoal tank top (not sage-olive), thin cream mat, mandala wall medallion, curtains, hanging wall shelves. Close in ink/wash feel but wrong aspect ratio, outfit, and studio set. |
| `img_stretch_quad.jpg` | 1024 × 1024 (518 KB) | **1:1 square, flat modern vector with floating abstract pastel blobs** (tan, olive, terracotta ovals) and botanical twig floating behind character; **long half-up wavy brown hair**; **no yoga mat** on floor. Pose: standing quad stretch. |
| `img_stretch_side_bend.jpg` | 1024 × 1024 (447 KB) | **1:1 square, thick-line vector**, dark teal matching tank & leggings, no studio wall details (plain cream wall), cream mat. Pose: standing side bend. |
| `img_stretch_twist.jpg` | 1024 × 1024 (581 KB) | **1:1 square**, **light blonde/taupe hair**, arched window, folded towels, and **"OM" text/symbol watermark** printed on the right corner of the yoga mat! Pose: seated Ardha Matsyendrasana twist. |
| `img_stretch_upper.jpg` | 1024 × 1024 (527 KB) | **CRITICAL MISMATCH + WATERMARK**: 1:1 square, pastel cyan tank + olive leggings, **floating pastel circles/arch**, **"CALM" text watermark** printed on mat, AND **anatomical pose is a Seated Neck Side Stretch**, NOT an upper-back/shoulder stretch! |
| `img_stretch_wrist.jpg` | 1024 × 1024 (483 KB) | **1:1 square**, **short pixie-cut hair**, light grey long-sleeve shirt, plain grey wall/cream mat. Pose: standing wrist flexor/extensor stretch. |

### 3.2 All Codebase References to Drawables & `ArtworkMapper`
No XML layouts (`res/layout` does not exist; app is 100% Jetpack Compose), AndroidManifest, or instrumented tests reference `img_stretch_*`. Verified references:

1. `app/src/main/java/com/example/data/ArtworkMapper.kt` — lines 12–134: references all 23 `R.drawable.img_stretch_*` drawables (including all 15 deprecated ones).
2. `app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt`:
   - Line 60: `ArtworkMapper.getDrawableForStretch(currentStretch.name, currentStretch.category)`
   - Line 62: fallback `R.drawable.img_stretch_standing` when `currentStretch == null`.
3. `app/src/main/java/com/example/ui/components/StretchItemCard.kt` — line 83: calls `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` (always renders a `50.dp` thumbnail box).
4. `app/src/main/java/com/example/ui/components/AddFromLibraryDialog.kt` — line 358: calls `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` (always renders a `48.dp` thumbnail box).
5. `app/src/main/java/com/example/ui/components/StretchDetailDialog.kt` — line 178: calls `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` (always renders a `16:10` hero box).
6. `app/src/test/java/com/example/ExampleUnitTest.kt` — lines 128–189 (`testArtworkMapper()`): asserts mappings against `img_stretch_neck`, `img_stretch_hamstring`, `img_stretch_calf`, `img_stretch_lunge`, `img_stretch_figure_four`, `img_stretch_pigeon`, `img_stretch_quad`, `img_stretch_cat_cow`, `img_stretch_chest`, `img_stretch_wrist`, `img_stretch_side_bend`, `img_stretch_floor`.
7. `app/src/test/java/com/example/CardLayoutScreenshotTest.kt` — line 34: renders `StretchItemCard` with `"Bhujangasana (Cobra Pose)"` (currently triggers `img_stretch_cobra` via `ArtworkMapper`).
8. `app/src/main/java/com/example/ui/components/HeaderBar.kt` (line 67) & `app/src/main/res/drawable/ic_launcher_foreground.xml` (line 6): reference `R.drawable.img_app_icon` / `@drawable/img_app_icon` (app branding icon, NOT a stretch illustration — keep untouched).

---

## 4. Audit of `ArtworkMapper.kt` Over-Broad & Wrong Mappings

Programmatic evaluation of `ArtworkMapper.getDrawableForStretch(name, category)` across all **150** library items (100 in `StretchLibrary` + 50 in `ExtraYogaAsanas`) shows every item is forced into one of the 23 drawables, with **all 8 unified drawables except `downward_dog` over-broadly mapped to wrong poses**, plus severe keyword/fallback collisions:

### 4.1 Over-Broad / Wrong Mappings on the 8 Unified Drawables

| Unified Drawable | Currently Mapped Count | Accurate Pose Match(es) | Wrong / Over-Broad Mappings Found |
|---|---:|---|---|
| `img_stretch_standing` | **17** (3 name + 14 `c.contains("yoga")` fallback) | `Tadasana (Mountain Pose Alignment)` (1) | **16 wrong**: `Utkatasana (Chair Pose)` (squat), `Surya Namaskar` (flow), plus **14 `ExtraYogaAsanas` via `c.contains("yoga")` fallback**: `Sirsasana (Headstand)`, `Bakasana (Crow)`, `Mayurasana (Peacock)`, `Hanumanasana (Right/Left Split)`, `Virasana (Hero)`, `Supta Virasana (Reclining Hero)`, `Kurmasana (Tortoise)`, `Vasisthasana (Right/Left Side Plank)`, `Upavistha Konasana (Wide-Angle Fold)`, `Parighasana (Right/Left Gate)`, `Phalakasana (Plank)`. |
| `img_stretch_lotus` | **12** | `Padmasana (Lotus Pose)` (1). *Approximate*: `Ardha Padmasana`, `Siddhasana`, `Sukhasana` (uncrossed shins), `Bhramari`/`Ujjayi Pranayama` (seated meditation). | **6 clearly wrong**: `Chaturanga Dandasana` (matches `"dandasana"` substring! — low plank), `Dandasana (Staff Pose)` (legs straight forward), `Vajrasana (Thunderbolt)` (kneeling on heels), `Tolasana (Scale Pose)` (arms lifting body off floor), `Anulom Vilom` (hand pinching nostrils), `Simhasana (Lion Pose)` (kneeling/mouth open). |
| `img_stretch_warrior` | **10** | `Virabhadrasana II (Warrior II - Right/Left)` (2) | **8 wrong**: `Virabhadrasana I (Warrior I - Right/Left)` (hips forward, arms overhead), `Virabhadrasana III (Warrior III - Right/Left)` (single-leg T-balance), `Ardha Chandrasana (Half Moon - Right/Left)`, `Utthita Parsvakonasana (Extended Side Angle - Right/Left)`. *(Note: `Skandasana` rule at line 96 is shadowed by `"lunge"` at line 54.)* |
| `img_stretch_child` | **5** | `Balasana (Child's Pose)`, `Restorative Child's Pose`, `Shashankasana (Hare Pose)` (3) | **2 different poses**: `Puppy Pose (Melting Heart)` (hips high over knees, NOT sitting on heels), `Child's Pose with Lat Reach` (arms walked off to right side). |
| `img_stretch_tree` | **4** | `Vrikshasana (Tree Pose - Right/Left)` (2) | **2 wrong**: `Natarajasana (Dancer Pose - Right/Left)` (standing backbend grabbing rear ankle). |
| `img_stretch_neck` | **4** | `Neck Side Stretch (Right/Left)` (2) | **2 wrong**: `Chin Tuck & Levator Stretch` (chin tucked down toward armpit, not lateral hand-on-head tilt), `Sternocleidomastoid (SCM) Neck Stretch` (45° rotation + upward tilt). Also `c.contains("neck")` category fallback. |
| `img_stretch_bridge` | **3** | `Setu Bandhasana (Bridge Pose)` (1) | **2 wrong**: `Chakrasana (Wheel Pose)` (full backbend arch on hands & feet), `Purvottanasana (Upward Plank)` (straight-leg reverse plank). |
| `img_stretch_downward_dog` | **1** | `Adho Mukha Svanasana (Downward-Facing Dog)` (1) | None of its matched items are wrong, though `Downward Dog Calf Pedaling` is hijacked to `img_stretch_calf` by rule 7 (`"pedal"`). |

### 4.2 Other Notable Keyword Collisions & Catch-All Fallbacks in `ArtworkMapper.kt`
- **All 15 deprecated drawables** account for the remaining **98/150** stretches (`hamstring`: 15, `twist`: 11, `floor`: 9, `upper`: 9, `lunge`: 8, `butterfly`: 7, `side_bend`: 6, `calf`: 5, `chest`: 5, `cobra`: 4, `pigeon`: 4, `cat_cow`: 3, `figure_four`: 3, `quad`: 3, `wrist`: 2).
- **Substring collision bugs**:
  - `"Wrist Figure-Eight Fluid Mobilizer"` matches `n.contains("figure")` (line 22) → `img_stretch_figure_four` (glute stretch)!
  - `"Sarvangasana (Shoulder Stand)"` matches `n.contains("shoulder")` (line 88) → `img_stretch_upper` (which itself is a neck stretch!).
  - `"Chaturanga Dandasana"` matches `n.contains("dandasana")` (line 103) → `img_stretch_lotus`!
  - `"90-90 Seated Hip Rotator Stretch"` matches `"90-90"`/`"hip"` (line 55) → `img_stretch_lunge`!
  - `"Malasana (Yogi Squat)"`, `"Happy Baby"`, `"Goddess Pose"`, `"Mandukasana (Frog)"` all map to `img_stretch_butterfly`.
  - `"Ustrasana (Camel)"`, `"Matsyasana (Fish)"`, `"Dhanurasana (Bow)"` all map to `img_stretch_chest` (doorway stretch).
  - `"Halasana (Plow)"`, `"Viparita Karani (Legs-Up-The-Wall)"`, `"Shavasana"`, `"Sphinx"`, `"Naukasana (Boat)"`, `"Makarasana (Crocodile)"`, `"Pavanamuktasana"` all map to `img_stretch_floor` (which actually depicts Child's Pose!).
- **Catch-all fallbacks**: Lines 124–133 (`c.contains("yoga") -> img_stretch_standing`, `else -> img_stretch_floor`, etc.) guarantee a non-null `@DrawableRes Int` even for completely unrelated or custom stretches, violating R3.

---

## 5. Technical Constraints for Android Drawables (~90–120 Images)

1. **Android Resource Naming Rules**:
   - Filenames in `res/drawable/` (excluding extension) MUST match `^[a-z][a-z0-9_]*$` (lowercase letters, digits, underscores only; cannot start with a digit; no hyphens, spaces, uppercase, or extra dots).
   - **No duplicate base names**: Android treats `foo.jpg`, `foo.png`, and `foo.webp` as the same resource ID (`R.drawable.foo`) and fails AAPT2 compilation if two extensions share a base name.
   - Recommend consistent prefix `img_pose_<canonical_pose>` or `img_stretch_<canonical_pose>` (e.g., `img_stretch_chin_tuck.jpg`).

2. **Image Format (`.jpg` vs `.webp` vs `.png`) & APK Size Budget**:
   - `app/build.gradle.kts` sets `minSdk = 26` (Android 8.0) and `isCrunchPngs = false` in `release`.
   - **Avoid `.png`** for opaque full-scene studio illustrations: a 1376×768 PNG is ~1.2–2.0 MB → **~110–240 MB** for 90–120 images, bloating the APK excessively.
   - **`.jpg` (baseline, quality 80–85, EXIF/DPI stripped)**: Current 8 unified `.jpg`s at 1376×768 average **281.5 KB** (range 239–325 KB).
     - At 281 KB avg × 90–120 images = **~25.3–33.8 MB** in APK.
     - Re-encoding/generating at `1280×720` or `1376×768` JPEG quality ~82–85 typically yields ~160–230 KB per image → **~14.4–27.6 MB** for 90–120 images.
     - Works seamlessly in both Android `res/drawable` and the local HTML visual gallery across all browsers/OS viewers.
   - **`.webp` (lossy, quality 82–85)**: Native support since API 18 (`minSdk = 26` well above). Typically 25–35% smaller than JPEG (~120–170 KB each → **~10.8–20.4 MB** for 90–120 images), also supported by modern browsers for HTML gallery.
   - **Recommendation**: Standardize either on optimized baseline **`.jpg`** (matches existing 8 reference files without re-encoding them, ~20–30 MB for ~100 images) or **`.webp`** if aggressive APK size reduction is desired; never mix extensions for the same pose name.

3. **Resolution, Aspect Ratio & `drawable/` vs `drawable-nodpi/` Memory Constraint (Important!)**:
   - The 8 unified reference files are **1376 × 768 px** (~1.79:1). Keep all newly generated drawables at **1376 × 768** (or consistent 16:9 e.g. `1376×768` / `1280×720`) with the practitioner centered horizontally so:
     - `PlayerArtworkCard.kt:91` & `StretchDetailDialog.kt:193` (`aspectRatio(16f / 10f)`, `ContentScale.Crop`) crop only ~10.7% of the side background edges, keeping the full pose visible.
     - `StretchItemCard.kt:134` (`50.dp` square) & `AddFromLibraryDialog.kt:387` (`48.dp` square) crop symmetrically around the centered character.
   - **Density-bucket upscaling pitfall**: Currently all images sit in `res/drawable/` (which Android treats as `mdpi` = 160 dpi). When `painterResource(id = ...)` decodes a `res/drawable/` raster via `BitmapFactory.decodeResource` on a `xxhdpi` (480 dpi, 3×) or `xxxhdpi` (640 dpi, 4×) device, Android upscales `1376×768` to **`4128×2304` (38 MB uncompressed ARGB_8888 RAM)** or **`5504×3072` (67 MB RAM)** per image! In `AddFromLibraryDialog` (`LazyColumn` of 150 items) or `StretchDetailDialog` (`HorizontalPager`), decoding multiple full-size bitmaps synchronously via `painterResource` risks jank or `OutOfMemoryError` on mid-range phones.
   - **Mitigation note for Implementers**: Placing raster artwork in **`res/drawable-nodpi/`** prevents Android from 3×–4× upscaling `1376×768` bitmaps (keeping decoded size at 1376×768 = ~4.2 MB each, or even better, loading via Coil `AsyncImage` / downsampling for 48–50dp thumbnails). Note that `R.drawable.<name>` works identically whether a file is in `res/drawable/` or `res/drawable-nodpi/` (just don't keep copies in both!).

4. **ArtworkMapper Nullability & UI Cleanliness (R3)**:
   - `ArtworkMapper.getDrawableForStretch` currently returns `@DrawableRes Int` (non-null). To support showing **no image at all** when unverified/unmapped, its signature must change to `@DrawableRes Int?` (returning `null` instead of catch-all fallbacks), and `PlayerArtworkCard.kt`, `StretchItemCard.kt`, `StretchDetailDialog.kt`, and `AddFromLibraryDialog.kt` must handle `artworkRes == null` by omitting the image container cleanly (or falling back to `customFile` only when present).
