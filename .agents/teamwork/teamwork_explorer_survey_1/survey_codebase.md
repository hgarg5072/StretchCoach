# Stretch Coach Codebase Survey

**Summary:** Stretch Coach is a single-module Kotlin/Jetpack Compose Android application (`com.example`, applicationId `com.aistudio.stretchcoach.strchq`) containing a 150-item stretch/asana catalog (`StretchLibrary` + `ExtraYogaAsanas`), a centralized non-nullable `ArtworkMapper.getDrawableForStretch(name, category): Int`, and 4 UI composables that render stretch artwork (`StretchItemCard`, `PlayerArtworkCard`, `AddFromLibraryDialog`, `StretchDetailDialog`). All 8 non-emulator unit/Robolectric/Roborazzi tests pass, while `FirestoreRoutineRepositoryRuleTest` requires the Firebase Local Emulator Suite (ports 8085/9099) and fails when unstarted due to `failFast = true`.

---

## 1. Repository & Android App Codebase Structure

### Root Configuration & Metadata
| Path | Purpose |
|---|---|
| `README.md` | AI Studio Android project instructions (API key in `.env`, debug signing note). |
| `.gitignore` | Standard Android ignores (`.gradle`, `.kotlin`, `/local.properties`, `/build`, `.env`, `debug.keystore`, etc.). Note: `*.html` is NOT gitignored. |
| `build.gradle.kts` | Top-level Gradle plugin declarations (`android.application`, `kotlin.compose`, `ksp`, `roborazzi`, `secrets`, `google.services`). |
| `settings.gradle.kts` | Single module `include(":app")`, `rootProject.name = "Stretch Coach"`. |
| `gradle/libs.versions.toml` | Version catalog: AGP `9.1.1`, Kotlin `2.2.10`, Compose BOM `2024.09.00`, Room `2.7.0`, Coil Compose `2.7.0`, Robolectric `4.16.1`, Roborazzi `1.59.0`, Firebase BOM `34.17.0`. |
| `app/build.gradle.kts` | Namespace `com.example`, `compileSdk = 36`, `minSdk = 26`, `targetSdk = 36`, Java 11, Compose + BuildConfig enabled, `unitTests.isIncludeAndroidResources = true`, `tasks.withType<Test> { failFast = true }` (line 71). |
| `firebase.json` | Defines Firestore database ID and emulator hosts/ports (`127.0.0.1:8085` Firestore, `127.0.0.1:9099` Auth). |
| `assets/` | Exists at project root, currently empty (0 files). |
| `metadata.json` | AI Studio project metadata (`Stretch Coach`). |

### Source Tree (`app/src/main/java/com/example/`)
- **Entry Point**: `MainActivity.kt` (sets `StretchCoachTheme`, hosts `MainScreen(viewModel)`).
- **Audio / Sanskrit**:
  - `audio/SanskritPhonetics.kt` — Maps Sanskrit asana names to Devanagari (`sanskritDevanagari`), English-TTS-safe phonetics (`sanskritPhonetic`), and English names.
  - `audio/SpeechController.kt` — Android TTS coaching engine.
- **Data Layer (`data/`)**:
  - `StretchLibrary.kt` — Defines `LibraryStretch` data class, `StretchCategory` enum (8 categories), and `ALL_100_STRETCHES` (`List<LibraryStretch>` of 100 items + `ExtraYogaAsanas.EXTRA_50_ASANAS` = 150 total; lines 26–1044).
  - `ExtraYogaAsanas.kt` — Defines `EXTRA_50_ASANAS` (50 classic Indian Yogasanas/Pranayama, each with explicit `indianName` in Devanagari; lines 7–560).
  - `ArtworkMapper.kt` — Maps `(name, category)` to `@DrawableRes Int` (lines 6–136).
  - `CustomAsanaImageGenerator.kt` — Uses Firebase AI (`imagen-3.0-generate-002`, 16:9 JPEG) to generate unified-style studio images for user-created custom stretches into `context.filesDir/custom_asana_images/`.
  - `StretchEntity.kt`, `LibraryStretchEntity.kt`, `RoutineEntity.kt` — Room entities (`version = 4` in `AppDatabase.kt`). Both `StretchEntity` and `LibraryStretchEntity` have `indianName: String?` and `customImagePath: String?`.
  - `StretchDao.kt`, `AppDatabase.kt`, `StretchRepository.kt` — Room DAO, default routine seeding (`DEFAULT_ROUTINES`), and repository. Note `StretchRepository.checkAndSeedDefaults()` (lines 39–40) populates `LibraryStretchEntity.indianName` via `lib.indianName ?: SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari`.
  - `RoutineBackupManager.kt`, `SettingsRepository.kt`, `AuthManager.kt`, `Firestore*.kt` — Local JSON backup, DataStore settings, Firebase Auth/Firestore sync & community feed.
- **UI Layer (`ui/`)**:
  - `StretchViewModel.kt` — StateFlows for routines, stretches, library, settings, player timer state (`PlayerUiState`), and community feed.
  - `screens/`: `MainScreen.kt` (3-tab Scaffold: Routines, Timer, Community), `RoutineScreen.kt`, `PlayerScreen.kt`, `CommunityScreen.kt`.
  - `components/`: `HeaderBar.kt`, `PlayerArtworkCard.kt`, `PlayerTimerCard.kt`, `PlayerControlsRow.kt`, `StretchItemCard.kt`, `StretchDetailDialog.kt`, `AddFromLibraryDialog.kt`, `RoutineSummaryCard.kt`, `RoutineSelectorRow.kt`, `EditStretchDialog.kt`, `EditLibraryStretchDialog.kt`, `CreateRoutineDialog.kt`, `DuplicateRoutineDialog.kt`, `EditRoutineDialog.kt`, `PublishRoutineDialog.kt`, `RoutineBackupDialog.kt`, `SettingsDialog.kt`.
  - `theme/`: `Color.kt`, `Theme.kt`, `Type.kt`.

### Drawable Resources (`app/src/main/res/drawable/`)
26 files total:
- 2 launcher XMLs (`ic_launcher_background.xml`, `ic_launcher_foreground.xml`)
- 1 app icon (`img_app_icon.jpg`, used in `HeaderBar.kt:67` and `ic_launcher_foreground.xml:6`)
- **8 unified studio reference drawables** (all `1376 x 768` px landscape ~16:9):
  - `img_stretch_bridge.jpg` (239,357 B)
  - `img_stretch_child.jpg` (239,649 B)
  - `img_stretch_downward_dog.jpg` (321,551 B)
  - `img_stretch_lotus.jpg` (244,803 B)
  - `img_stretch_neck.jpg` (324,783 B)
  - `img_stretch_standing.jpg` (320,659 B)
  - `img_stretch_tree.jpg` (241,371 B)
  - `img_stretch_warrior.jpg` (320,154 B)
- **15 older mixed-style drawables** (`1024 x 1024` px square, except `img_stretch_cobra.jpg` which is `1408 x 768`):
  - `img_stretch_butterfly.jpg`, `img_stretch_calf.jpg`, `img_stretch_cat_cow.jpg`, `img_stretch_chest.jpg`, `img_stretch_cobra.jpg`, `img_stretch_figure_four.jpg`, `img_stretch_floor.jpg`, `img_stretch_hamstring.jpg`, `img_stretch_lunge.jpg`, `img_stretch_pigeon.jpg`, `img_stretch_quad.jpg`, `img_stretch_side_bend.jpg`, `img_stretch_twist.jpg`, `img_stretch_upper.jpg`, `img_stretch_wrist.jpg`.

---

## 2. Complete Inventory of Artwork Mapping, Lookup & Rendering Sites

### A. Artwork Mapping
1. **`app/src/main/java/com/example/data/ArtworkMapper.kt`**
   - Signature: `@DrawableRes fun getDrawableForStretch(name: String, category: String = ""): Int` (lines 7–8)
   - Current behavior:
     - Normalizes `name` and `category` to lowercase trimmed strings (lines 9–10).
     - Runs a broad substring-matching `when` cascade (lines 12–134) mapping keywords and Sanskrit terms to the 23 `R.drawable.img_stretch_*` drawables (8 unified + 15 older).
     - **Broad/inaccurate shared buckets** currently exist (e.g., `hip` -> `img_stretch_lunge`; `padmasana`/`lotus`/`sukhasana`/`vajrasana`/`siddhasana`/`pranayama`/`dandasana` -> `img_stretch_lotus`; `halasana`/`viparita`/`shavasana`/`sphinx`/`sarvangasana`/`naukasana`/`makarasana`/`pavanamuktasana` -> `img_stretch_floor`; `setu bandha`/`bridge`/`chakrasana`/`wheel`/`purvottanasana` -> `img_stretch_bridge`, etc.).
     - **Category fallback** (lines 124–131): matches `c.contains("yoga")`, `"neck"`, `"calf"/"feet"`, `"hamstring"`, `"chest"`, `"hip"`, `"spine"/"back"`, `"shoulder"/"arm"`.
     - **Catch-all fallback** (line 133): `else -> R.drawable.img_stretch_floor`.

### B. Custom AI Artwork Generation (Runtime, User-Created Custom Stretches Only)
2. **`app/src/main/java/com/example/data/CustomAsanaImageGenerator.kt`**
   - `suspend fun generateAndSave(context: Context, uniqueKey: String, asanaName: String, indianName: String? = null, notes: String? = null): String?` (lines 62–102)
   - Invoked only in `StretchViewModel.addCustomLibraryStretch` (lines 493–503) and `StretchViewModel.saveStretch` when `id == 0L` (lines 534–545). Does NOT affect the 150 pre-fed library items unless a user creates a custom item.
   - Notably, `buildUnifiedPrompt` (lines 48–54) documents the studio style: *"Clean minimalist editorial line-art illustration... barefoot on a beige woven jute yoga mat... Warm serene sunlit minimalist yoga studio interior with soft cream plaster walls, light wooden floor, natural window sunlight, and a softly blurred potted green plant in background. Practitioner wearing sage-olive sports top and charcoal leggings. Fine dark ink outlines with soft matte watercolor shading, full body centered, 16:9 widescreen, no text, no labels, no watermarks."*

### C. UI Lookup & Rendering Sites (All 4 Stretch-Artwork Composables + 1 App Logo)
Every stretch artwork rendering site in the app follows the same two-tier pattern: check `customImagePath` (`File(it).takeIf { it.exists() }`), else call `ArtworkMapper.getDrawableForStretch(name, category)` and render `Image(painter = painterResource(id = artworkRes), ...)`.

| Component | File & Lines | Lookup | Current Rendering & Container | Null-Image Impact / Edge Cases |
|---|---|---|---|---|
| **Routine Stretch Card** | `app/src/main/java/com/example/ui/components/StretchItemCard.kt` (L83–86, L131–156) | `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` | Always renders `Box(size(50.dp), clip(10.dp), bg(surfaceVariant), border)` + `AsyncImage`/`Image` + `Spacer(width(10.dp))` before `Column(Modifier.weight(1f))`. | Must omit **both** the `50.dp` `Box` and trailing `Spacer(10.dp)` when `customFile == null && artworkRes == null`, so text `Column(weight(1f))` cleanly aligns to the card's left padding. Covered by `CardLayoutScreenshotTest`. |
| **Player Artwork Card** | `app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt` (L59–63, L77–233) | `if (currentStretch != null) ArtworkMapper.getDrawableForStretch(currentStretch.name, currentStretch.category) else R.drawable.img_stretch_standing` | Renders `Card` -> `Box(fillMaxWidth().aspectRatio(16f / 10f))` containing: (1) `AsyncImage`/`Image` (L96–114), (2) Top gradient scrim + **Step badge** (`STEP X OF Y` / `READY TO START` / `ALL COMPLETE`) + **Phase badge** (`READY`/`PREPARING`/`HOLDING`/`PAUSED`/`COMPLETE`) (L117–188), (3) Bottom gradient scrim + `currentStretch.name` + `currentStretch.notes` in `Color.White` (L191–231). | **High-impact edge case:** (a) L62 hardcodes `R.drawable.img_stretch_standing` fallback when `currentStretch == null`; (b) Step badge, Phase badge, and active stretch name/notes live *inside* the `aspectRatio(16f / 10f)` Box with white text over dark scrims. If `customFile == null && artworkRes == null`, we must NOT leave an empty 16:10 image frame or white-on-light text; instead, omit the 16:10 image frame while preserving Step/Phase badges and `currentStretch.name`/`notes` in a compact surface-themed header (or render those badges cleanly without an image area). |
| **Library Browser List Row** | `app/src/main/java/com/example/ui/components/AddFromLibraryDialog.kt` (L358–361, L384–409) | `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` | Inside `LazyColumn`, always renders `Box(size(48.dp), clip(10.dp), bg(surface), border)` + `AsyncImage`/`Image` + `Spacer(width(10.dp))` before `Column(Modifier.weight(1f))`. | Must omit **both** the `48.dp` `Box` and trailing `Spacer(10.dp)` when `customFile == null && artworkRes == null`. |
| **Library Detail Pager Sheet** | `app/src/main/java/com/example/ui/components/StretchDetailDialog.kt` (L178–181, L189–216) | `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` | Inside `HorizontalPager`, always renders `Box(fillMaxWidth().aspectRatio(16f / 10f), clip(18.dp), bg(surfaceVariant), border)` + `AsyncImage`/`Image` + `Spacer(height(14.dp))` above Title/Indian name/Timing chips/Cue cards. | Must omit **both** the `16:10` `Box` and trailing `Spacer(14.dp)` when `customFile == null && artworkRes == null` so Title and Cue cards start cleanly at the top. |
| **Top App Bar Logo** | `app/src/main/java/com/example/ui/components/HeaderBar.kt` (L66–73) | `R.drawable.img_app_icon` | Renders app logo icon (`42.dp`). | Not stretch artwork; keep untouched. |

Note: `CommunityScreen.kt` (including `CommunityRoutinePreviewDialog`) and `PlayerTimerCard.kt` (`Next Up` chip uses `Icons.Default.SelfImprovement`) do NOT look up or render stretch artwork. `StretchViewModel.kt` does NOT call `ArtworkMapper`.

---

## 3. Unit Tests & Baseline Build Verification

### Test Inventory (`app/src/test/java/com/example/`)
1. **`ExampleUnitTest.kt`** (4 tests):
   - `testStretchLibrarySizeAndCategories()` (lines 15–37): asserts `StretchLibrary.ALL_100_STRETCHES.size == 150`, all `StretchCategory` values present, 150 unique IDs, non-blank cues/benefits, positive prep/hold seconds.
   - `testSanskritPhonetics()` (lines 40–113).
   - `testStretchSearchAndFilter()` (lines 116–125).
   - `testArtworkMapper()` (lines 128–189): asserts exact drawable IDs for 18 sample inputs, many of which currently assert the 15 older drawables (`img_stretch_hamstring`, `img_stretch_calf`, `img_stretch_lunge`, `img_stretch_figure_four`, `img_stretch_pigeon`, `img_stretch_quad`, `img_stretch_cat_cow`, `img_stretch_chest`, `img_stretch_wrist`, `img_stretch_side_bend`, `img_stretch_floor`). This test will need updating once `ArtworkMapper` returns `Int?` (`null` for unmapped, and updated/replaced drawables).
2. **`ExampleRobolectricTest.kt`** (2 tests): context string check + `RoutineBackupManager` JSON serialization/persistence.
3. **`CardLayoutScreenshotTest.kt`** (1 test): Roborazzi Pixel 8 screenshot test rendering `StretchItemCard` with `Bhujangasana (Cobra Pose)` (`src/test/screenshots/stretch_card_pixel8.png`). Note: `Bhujangasana` currently maps to `R.drawable.img_stretch_cobra`, one of the 15 older drawables.
4. **`GreetingScreenshotTest.kt`** (1 test): Roborazzi Pixel 8 screenshot test (`src/test/screenshots/greeting.png`).
5. **`FirestoreRoutineRepositoryRuleTest.kt`** (3 tests) extending `base/FirestoreEmulatorTestBase.kt`: requires Firebase Firestore (`127.0.0.1:8085`) and Auth (`127.0.0.1:9099`) emulators running locally.

### Baseline Build & Test Commands Executed
1. Full baseline command requested:
   `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew assembleDebug testDebugUnitTest --no-daemon`
   - **Exit code:** `1`
   - `:app:assembleDebug` **SUCCEEDED**.
   - `:app:testDebugUnitTest`: `11 tests completed, 1 failed, 1 skipped`. Failure: `FirestoreRoutineRepositoryRuleTest > authenticatedUser_canDeleteRoutine FAILED` (`com.google.firebase.FirebaseException` at `firebase-auth`, because local Firebase Auth/Firestore emulators at `127.0.0.1:9099` and `127.0.0.1:8085` are not running). Because `app/build.gradle.kts:71` configures `tasks.withType<Test> { failFast = true }`, the remaining 1 test in `FirestoreRoutineRepositoryRuleTest` was skipped and the task exited non-zero.
2. Targeted non-emulator test run:
   `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew testDebugUnitTest --tests "com.example.ExampleUnitTest" --tests "com.example.ExampleRobolectricTest" --tests "com.example.CardLayoutScreenshotTest" --tests "com.example.GreetingScreenshotTest" --no-daemon`
   - **Exit code:** `0` (`BUILD SUCCESSFUL`).
   - Verified in `app/build/reports/tests/testDebugUnitTest/index.html`: **8 tests, 0 failures, 0 skipped, 100% pass** (`CardLayoutScreenshotTest`: 1, `ExampleRobolectricTest`: 2, `ExampleUnitTest`: 4, `GreetingScreenshotTest`: 1).

---

## 4. Existing HTML Gallery Files & Documentation Conventions

- **No existing HTML gallery files** exist anywhere in the repository (`find_by_name` for `*.html`/`*.htm` only found generated Gradle/test reports under `build/` and `app/build/`).
- **Documentation**: Only `README.md` (AI Studio default template) and `firebase-blueprint.json` exist at root.
- **Note on relative image links for a root-level HTML gallery**: Drawable files live at `app/src/main/res/drawable/<name>.jpg`. A local HTML gallery file at repo root (e.g. `stretch_gallery.html`) can link directly via relative paths `app/src/main/res/drawable/<name>.jpg`.
- **Note on Indian/Sanskrit names in the 150-item catalog**:
  - `ExtraYogaAsanas.kt` (50 items) defines explicit `indianName = "..."` (Devanagari) on all 50 items.
  - `StretchLibrary.kt` (100 items) has `indianName = null` in source (`StretchLibrary.ALL_100_STRETCHES`), though several items embed Sanskrit names in `name` (e.g. `"Baddha Konasana (Butterfly Pose)"`, `"Adho Mukha Svanasana (Downward Dog Pedal)"`, `"Marjaryasana (Cat Pose Spinal Flexion)"`), and `StretchRepository.kt:39–40` resolves `indianName` at runtime via `SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari`.

---

## 5. Key Constraints & Edge Cases Summary

1. **Return type change (`Int` -> `Int?`)**: Changing `ArtworkMapper.getDrawableForStretch` to `@DrawableRes fun getDrawableForStretch(...): Int?` requires updating all 4 UI call sites and `ExampleUnitTest.testArtworkMapper()`, because `painterResource(id = ...)` requires a non-null `@DrawableRes Int`.
2. **Conditional layout omission**:
   - In `StretchItemCard` and `AddFromLibraryDialog`, omit both the thumbnail `Box` and the `Spacer(10.dp)` when `customFile == null && artworkRes == null`.
   - In `StretchDetailDialog`, omit both the `16:10` `Box` and the `Spacer(14.dp)` when `customFile == null && artworkRes == null`.
   - In `PlayerArtworkCard`, remove the `else { R.drawable.img_stretch_standing }` fallback and handle `customFile == null && artworkRes == null` without losing the Step badge, Phase badge, or current stretch name/notes (and without rendering white text on light surface).
3. **`failFast = true` & Firebase Emulator**: `app/build.gradle.kts:71` fails the full `testDebugUnitTest` task on `FirestoreRoutineRepositoryRuleTest` unless emulators (`127.0.0.1:8085` & `9099`) are running or non-emulator test classes are targeted.
4. **Roborazzi screenshot test (`CardLayoutScreenshotTest`)**: Renders `StretchItemCard` for `"Bhujangasana (Cobra Pose)"` (`R.drawable.img_stretch_cobra` currently). Modifying `StretchItemCard` or replacing `img_stretch_cobra.jpg` changes the rendered card; verify `CardLayoutScreenshotTest` behavior.
5. **Android resource naming constraints**: Drawable filenames in `app/src/main/res/drawable/` must be lowercase `[a-z0-9_.]` only (no hyphens, spaces, or uppercase), and unified reference images are `1376 x 768` (~16:9) JPEGs.
