# Handoff Report — Explorer 3 (`teamwork_explorer_survey_3`)

**Handoff Type**: Hard (Task complete)  
**Detailed Audit Report**: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_survey_3/drawables_audit.md`

---

## 1. Observation

1. **Drawable & Resource Inventory**:
   - `app/src/main/res/drawable/` contains **26 files** (verified via `list_dir` and `file` + `sips`):
     - **8 unified-style reference JPGs** (`img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_warrior.jpg`, `img_stretch_lotus.jpg`, `img_stretch_tree.jpg`, `img_stretch_bridge.jpg`, `img_stretch_child.jpg`): all baseline JPEG, **1376 × 768** (~1.79:1), total **2,252,327 B** (avg **281,540 B**).
     - **15 older mixed-style stretch JPGs**: 14 are **1024 × 1024** (`img_stretch_butterfly.jpg`, `calf`, `cat_cow`, `chest`, `figure_four`, `floor`, `hamstring`, `lunge`, `pigeon`, `quad`, `side_bend`, `twist`, `upper`, `wrist`) and 1 is **1408 × 768** (`img_stretch_cobra.jpg`), total **6,779,508 B** (avg **451,967 B**).
     - **3 app/launcher assets**: `img_app_icon.jpg` (1024 × 1024 JPEG, 441,151 B), `ic_launcher_background.xml` (890 B), `ic_launcher_foreground.xml` (280 B).
   - No other `res/drawable*` directories exist; `app/src/main/assets/` does not exist (repo-root `assets/` contains only `.aistudio/.gitignore`).
   - `app/src/main/res/mipmap-*` contains `ic_launcher*.xml` and `ic_launcher*.webp`.

2. **Visual Inspection of All 8 Unified Drawables (`view_file`)**:
   - Unified style: editorial ink-and-watercolor illustration of a barefoot young woman with dark brown hair in a low bun (center part, subtle face-framing wisps), warm fair-to-light-olive skin with soft peach cheek blush, wearing a **sage-olive sports bra/crop top** and **charcoal high-waisted leggings**, on a **beige woven jute yoga mat** over **light honey-oak wood planks** in a warm sunlit cream-plaster studio with potted plants (two minor room-angle variants: left-window with wooden shelf + fringed mat vs. right-window with two corner plants + unfringed mat). Zero text/watermarks.
   - Exact poses observed:
     - `img_stretch_neck.jpg`: Seated cross-legged lateral neck tilt (left hand gently over head, right fingertips on mat).
     - `img_stretch_standing.jpg`: Standing Tadasana (feet parallel, arms down at sides, palms slightly forward).
     - `img_stretch_downward_dog.jpg`: Side-profile facing left in Adho Mukha Svanasana (inverted V).
     - `img_stretch_warrior.jpg`: Virabhadrasana II (Warrior II, left knee bent right, right leg extended left, T-arms, gazing over front hand).
     - `img_stretch_lotus.jpg`: Seated Padmasana/Ardha Padmasana facing forward, hands in Chin Mudra on knees.
     - `img_stretch_tree.jpg`: Standing Vrikshasana balancing on right leg, left foot on inner right thigh, palms in Anjali Mudra at heart.
     - `img_stretch_bridge.jpg`: Supine side-profile Setu Bandhasana (head left, feet right, arms flat on mat alongside body, hips lifted).
     - `img_stretch_child.jpg`: Side-profile facing right in Balasana (hips on heels, forehead down, arms extended forward).

3. **Visual Inspection of the 15 Older Mixed-Style Drawables (`view_file`)**:
   - Fourteen are 1:1 (`1024×1024`) in heterogeneous styles: flat lineless/vector cartoons (`butterfly`, `calf` with sneakers & faceless, `figure_four`, `hamstring`, `lunge` with pixie hair, `side_bend`, `wrist` with pixie hair), pastel blob backgrounds (`quad` with long hair & no mat, `upper` with `"CALM"` mat text), `"OM"` logo on mat (`twist`), mismatched outfit/room (`cat_cow`, `chest`, `floor`, `pigeon`).
   - `img_stretch_upper.jpg` actually depicts a **seated neck side stretch** (not upper back/shoulder).
   - `img_stretch_floor.jpg` actually depicts **Child's Pose** (not supine/floor poses).
   - `img_stretch_cobra.jpg` (`1408×768`) has solid beige floor, ponytail, bench with towels, and depicts **Upward-Facing Dog** (straight arms, thighs/hips lifted off mat), not Cobra Pose.

4. **Codebase References & `ArtworkMapper.kt` Audit (`grep_search`, `view_file`, Python audit over all 150 stretches)**:
   - `ArtworkMapper.getDrawableForStretch` (`app/src/main/java/com/example/data/ArtworkMapper.kt:8-135`) returns non-null `@DrawableRes Int` and maps all 150 library stretches (100 in `StretchLibrary.kt` + 50 in `ExtraYogaAsanas.kt`): 52 to the 8 unified drawables and 98 to the 15 deprecated drawables.
   - Over-broad mappings on the 8 unified drawables:
     - `img_stretch_standing` (17 mapped; 16 wrong): `Utkatasana`, `Surya Namaskar`, + **14 `ExtraYogaAsanas` via `c.contains("yoga")`** (Headstand, Crow, Peacock, Hanumanasana, Hero, Reclining Hero, Tortoise, Side Plank, Wide-Angle Fold, Gate, Plank).
     - `img_stretch_lotus` (12 mapped; 6 clearly wrong): `Chaturanga Dandasana` (matches `"dandasana"` substring at line 103), `Dandasana`, `Vajrasana`, `Tolasana`, `Anulom Vilom`, `Simhasana` (plus approximate matches `Sukhasana`/`Siddhasana`/`Bhramari`/`Ujjayi`).
     - `img_stretch_warrior` (10 mapped; 8 wrong): `Warrior I (L/R)`, `Warrior III (L/R)`, `Half Moon (L/R)`, `Extended Side Angle (L/R)`.
     - `img_stretch_child` (5 mapped; 2 different): `Puppy Pose`, `Child's Pose with Lat Reach`.
     - `img_stretch_tree` (4 mapped; 2 wrong): `Natarajasana (Dancer - L/R)`.
     - `img_stretch_neck` (4 mapped; 2 wrong): `Chin Tuck & Levator Stretch`, `SCM Neck Stretch` (+ `c.contains("neck")` fallback).
     - `img_stretch_bridge` (3 mapped; 2 wrong): `Chakrasana (Wheel)`, `Purvottanasana (Upward Plank)`.
     - `img_stretch_downward_dog` (1 mapped directly; `Downward Dog Calf Pedaling` is hijacked to `img_stretch_calf` by `"pedal"` at line 45).
   - Callers of `ArtworkMapper`: `PlayerArtworkCard.kt:60` (also hardcoded `R.drawable.img_stretch_standing` at line 62 when `currentStretch == null`), `StretchItemCard.kt:83`, `AddFromLibraryDialog.kt:358`, `StretchDetailDialog.kt:178`, `ExampleUnitTest.kt:128-189`, and indirectly `CardLayoutScreenshotTest.kt:34` (`Bhujangasana (Cobra Pose)`).
   - `HeaderBar.kt:67` and `ic_launcher_foreground.xml:6` reference `img_app_icon` (branding only, keep untouched).

5. **Android Build/Resource Constraints (`app/build.gradle.kts`, UI components)**:
   - `minSdk = 26`, `isCrunchPngs = false` in release.
   - Resource naming: `^[a-z][a-z0-9_]*$`, no duplicate base names across extensions.
   - UI containers: `16:10` (`PlayerArtworkCard.kt:91`, `StretchDetailDialog.kt:193`) and `1:1` thumbnails (`StretchItemCard.kt:134`, `AddFromLibraryDialog.kt:387`), all using `ContentScale.Crop`.
   - `res/drawable/` defaults to `mdpi` (160 dpi), causing `BitmapFactory` (via `painterResource`) to upscale `1376×768` bitmaps 3×–4× in memory on `xxhdpi`/`xxxhdpi` devices unless placed in `res/drawable-nodpi/` or downsampled via Coil.

---

## 2. Logic Chain

1. Because 15 of the 23 stretch drawables differ in aspect ratio (`1024×1024` or `1408×768` vs `1376×768`), rendering style (flat vector/cartoon vs ink-and-watercolor), attire/hair/studio props, and even depict wrong poses (`upper` = neck stretch, `floor` = child's pose, `cobra` = upward dog) or watermarks (`"CALM"`, `"OM"`), all 15 must be removed and replaced per R2.
2. Because 7 of the 8 unified drawables are mapped via overly broad substring rules and category/else fallbacks in `ArtworkMapper.kt` (returning non-null `@DrawableRes Int`), keeping only accurate mappings requires:
   - Restricting each of the 8 unified drawables strictly to the exact anatomical pose it depicts (including symmetrical Left/Right pairs),
   - Changing `ArtworkMapper.getDrawableForStretch` to return `@DrawableRes Int?` (`null` when unverified/unmapped), and
   - Updating `PlayerArtworkCard`, `StretchItemCard`, `StretchDetailDialog`, and `AddFromLibraryDialog` to omit the image area cleanly on `null` (and removing `PlayerArtworkCard.kt:62`'s hardcoded `img_stretch_standing` fallback).
3. For ~90–120 new studio drawables, PNGs (~110–240 MB) would bloat the APK unacceptably, whereas baseline JPEGs (~20–30 MB at ~180–250 KB/image) or lossy WebP (~13–20 MB, supported natively on `minSdk = 26`) keep APK size reasonable while preserving 1376×768 centered framing for `16:10` and `1:1` `ContentScale.Crop` slots. Moving stretch drawables to `res/drawable-nodpi/` prevents 3×–4× density upscaling OOMs in `LazyColumn` (`AddFromLibraryDialog` / `StretchItemCard`).

---

## 3. Caveats

- Read-only Survey phase: did not run Gradle build/tests or generate/modify any code or drawables.
- `ArtworkMapper` mapping distribution across the 150 items was verified by reproducing `ArtworkMapper.kt`'s exact `when` branch order in Python against `StretchLibrary.kt` and `ExtraYogaAsanas.kt` rather than executing JVM unit tests; line-by-line inspection confirms 1:1 parity.
- Exact target count of unique canonical pose images needed across the 150 items (accounting for Left/Right pairs and near-identical poses) should be cross-referenced with the stretch-catalog Explorer's breakdown.

---

## 4. Conclusion

1. **Keep (strictly scoped) 8 unified drawables** (`1376×768` JPGs): `img_stretch_neck` (Neck Side Stretch R/L only), `img_stretch_standing` (Tadasana only), `img_stretch_downward_dog` (Downward-Facing Dog, optionally Downward Dog Calf Pedaling if not separately generated), `img_stretch_warrior` (Warrior II R/L only), `img_stretch_lotus` (Padmasana; optionally Ardha Padmasana/Siddhasana if strictness allows, NOT Chaturanga/Dandasana/Vajrasana/Tolasana/Anulom Vilom/Simhasana), `img_stretch_tree` (Tree Pose R/L only), `img_stretch_bridge` (Bridge Pose only), `img_stretch_child` (Balasana / Restorative Child's Pose / Shashankasana only).
2. **Delete/replace all 15 deprecated drawables** (`butterfly`, `calf`, `cat_cow`, `chest`, `cobra`, `figure_four`, `floor`, `hamstring`, `lunge`, `pigeon`, `quad`, `side_bend`, `twist`, `upper`, `wrist`) and fix all `ArtworkMapper` substring bugs & UI null-handling.
3. **Preserve `img_app_icon.jpg`**, `ic_launcher_background.xml`, and `ic_launcher_foreground.xml` (app launcher & `HeaderBar` branding).
4. **Target `1376×768` centered composition** in `[a-z][a-z0-9_]*` optimized `.jpg` (or `.webp`), ideally stored in `res/drawable-nodpi/` (or loaded with downsampling for 48–50dp thumbnails) to prevent OOMs.

---

## 5. Verification Method

- **List & measure drawables**:
  `for f in "app/src/main/res/drawable/"*; do wc -c "$f"; sips -g pixelWidth -g pixelHeight "$f"; done`
- **Verify `ArtworkMapper` & UI references**:
  Inspect `app/src/main/java/com/example/data/ArtworkMapper.kt`, `PlayerArtworkCard.kt`, `StretchItemCard.kt`, `AddFromLibraryDialog.kt`, `StretchDetailDialog.kt`, `HeaderBar.kt`, `ExampleUnitTest.kt`, and `CardLayoutScreenshotTest.kt`.
- **Verify build & tests (post-implementation)**:
  `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew assembleDebug testDebugUnitTest --no-daemon`
- **Invalidation conditions**: Any new drawable found outside `app/src/main/res/drawable/`, or changes to `ArtworkMapper.kt` / `StretchLibrary.kt` / `ExtraYogaAsanas.kt` after this audit.
