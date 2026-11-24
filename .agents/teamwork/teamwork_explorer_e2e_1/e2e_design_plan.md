# E2E Testing Track & HTML Visual Gallery Specification (`stretch_gallery.html`)

**Author:** Explorer 3 (`teamwork_explorer_e2e_1`)  
**Date:** 2026-11-23  
**Scope:** E2E Testing Track (`TEST_INFRA.md`, `ArtworkE2ETest.kt`, `UiNoImageE2ETest.kt`, `tests/e2e/verify_stretch_coach.py`) and Milestone M6 HTML Gallery Specification (`stretch_gallery.html`).

---

## 1. Verified Codebase Findings (Evidence Summary)

### 1.1 Indian / Sanskrit Name Representation Across All 150 Stretches
Verified directly in `StretchLibrary.kt`, `ExtraYogaAsanas.kt`, `SanskritPhonetics.kt`, and `StretchRepository.kt`:
- **Catalog Size & Order**:
  - `StretchLibrary.ALL_100_STRETCHES` (`StretchLibrary.kt:27-1043`) defines **100** `LibraryStretch` items (`#1 neck_side_right` .. `#100 bitilasana_cow_pose`), concatenated with `ExtraYogaAsanas.EXTRA_50_ASANAS` (`ExtraYogaAsanas.kt:8-559`) containing **50** items (`#101 padmasana_lotus` .. `#150 ujjayi_pranayama`) = **150 total unique IDs**.
- **Explicit `indianName` vs Runtime `SanskritPhonetics` Resolution**:
  - **`StretchLibrary.kt` (`#1–#100`)**: `0 / 100` items pass an explicit `indianName` constructor argument (all default to `null` per `StretchLibrary.kt:12`).
    - **`#1–#34` and `#36–#50` (49 items)**: Purely English anatomical/stretch names (22 contain non-Sanskrit parenthetical qualifiers such as `(Right)`, `(Left)`, `(SCM)`, `(Melting Heart)`, `(Palm Up)`). `SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari` returns `null`.
    - **`#35` (`Seated Single-Leg Fold (Janu Sirsasana)`) and `#51–#100` (51 items)**: Embed Latin Sanskrit transliteration in `name` (`<English> (<Sanskrit>)` for `#35`, `<Sanskrit> (<English>)` for `#51–#100`). All 51 match entries in `SanskritPhonetics.SANSKRIT_TO_DEVANAGARI_MAP` (`SanskritPhonetics.kt:140-214`) and resolve authentic Devanagari via `buildSpokenPlan(lib.name).sanskritDevanagari` (`lines 374-451`).
  - **`ExtraYogaAsanas.kt` (`#101–#150`)**: `50 / 50` items explicitly set `indianName` in Devanagari script **and** include Latin Sanskrit transliteration in `name`.
  - **Runtime Room Seeding (`StretchRepository.kt:39-44`)**:
    ```kotlin
    val resolvedIndianName = lib.indianName
        ?: com.example.audio.SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari
    ```
    Thus, at runtime in `LibraryStretchEntity`, **101 / 150** stretches have non-null Devanagari `indianName` (`51` from `StretchLibrary` + `50` from `ExtraYogaAsanas`) and **49 / 150** have `indianName == null`.
  - **Routine Entity Note (`AppDatabase.kt:116-130` vs `StretchRepository.kt:249-264`)**: `AppDatabase.createDefaultStretchesForRoutine` leaves `StretchEntity.indianName` as `null`, whereas `StretchRepository.addLibraryStretchToRoutine` copies `libraryStretch.indianName`.

### 1.2 Drawable Assets & Style Metrics (`app/src/main/res/drawable/`)
Verified via `list_dir`, `sips`, and binary JPEG header inspection:
- **8 Unified Studio Reference Drawables** (`img_stretch_{bridge,child,downward_dog,lotus,neck,standing,tree,warrior}.jpg`):
  - All 8 are valid JPEG (`FF D8 FF` .. `FF D9`), **`1376 × 768`** px (16:9 editorial ink-and-watercolor).
- **15 Legacy Mixed-Style Drawables (to be deleted in M1)**:
  - `butterfly`, `calf`, `cat_cow`, `chest`, `figure_four`, `floor`, `hamstring`, `lunge`, `pigeon`, `quad`, `side_bend`, `twist`, `upper`, `wrist` (`1024 × 1024`), and `cobra` (`1408 × 768`).
- **Keep Untouched**: `img_app_icon.jpg` (used in `HeaderBar.kt:67`), `ic_launcher_background.xml`, `ic_launcher_foreground.xml`.

### 1.3 Robolectric & Compose UI Test Infrastructure
Verified in `app/build.gradle.kts`, `ExampleRobolectricTest.kt`, `CardLayoutScreenshotTest.kt`, `GreetingScreenshotTest.kt`, and `FirestoreEmulatorTestBase.kt`:
- **Test Configuration (`app/build.gradle.kts`)**:
  - `testOptions { unitTests { isIncludeAndroidResources = true } }` (`line 63`)
  - `tasks.withType<Test> { failFast = true }` (`lines 70-72`)
  - Dependencies available: `junit` (4.13.2), `androidx.test.ext:junit`, `androidx.test:core`, `robolectric` (`4.16.1`), `androidx.compose.ui:ui-test-junit4`, `roborazzi` (`1.59.0`), `kotlinx-coroutines-test`, plus `androidx.room` runtime/ktx (`2.7.0`) for in-memory DB testing.
- **Compose UI Testing Mechanics**:
  - Use `@RunWith(RobolectricTestRunner::class)`, `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, `@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])`, and `@get:Rule val composeTestRule = createComposeRule()`, wrapping composables in `StretchCoachTheme { Surface { ... } }`.
  - **Animation/Idleness Trap**: `PlayerArtworkCard.kt:66-75` runs `rememberInfiniteTransition` when `state == PlayerState.PREPARING || state == PlayerState.HOLDING`, and `StretchItemCard.kt:169` / `PlayerArtworkCard.kt:217` apply `Modifier.basicMarquee(iterations = Int.MAX_VALUE)`. In `UiNoImageE2ETest.kt`, tests exercising `PREPARING`/`HOLDING` or long titles should set `composeTestRule.mainClock.autoAdvance = false` and call `composeTestRule.mainClock.advanceTimeBy(100)` before assertions so `waitForIdle()` cannot stall on infinite animations.
  - **Opaque-Box UI Semantics (No Private TestTags Needed)**:
    - `StretchItemCard.kt`: `testTag("stretch_card_${stretch.id}")`; `Image` / `AsyncImage` uses `contentDescription = stretch.name`.
    - `PlayerArtworkCard.kt`: `testTag("player_artwork_card")`; `Image` / `AsyncImage` uses `contentDescription = currentStretch?.name ?: "Stretch demonstration"`. Displays Step badge (`"STEP X OF Y"` / `"READY TO START"` / `"ALL COMPLETE"`), Phase badge (`"READY"` / `"PREPARING"` / `"HOLDING"` / `"PAUSED"` / `"COMPLETE"`), `currentStretch.name`, and `currentStretch.notes`.
    - `AddFromLibraryDialog.kt`: `testTag("add_from_library_dialog")`, `testTag("library_item_${stretch.id}")`; thumbnail uses `contentDescription = stretch.name`.
    - `StretchDetailDialog.kt`: `testTag("stretch_detail_dialog")`, `testTag("stretch_detail_pager")`; artwork uses `contentDescription = stretch.name`.
    - Note: In Compose semantics, `Text` populates `SemanticsProperties.Text`, whereas `Image`/`AsyncImage` `contentDescription` populates `SemanticsProperties.ContentDescription`. Thus `onNodeWithContentDescription(stretch.name).assertDoesNotExist()` specifically asserts the absence of the image node even while `onNodeWithText(stretch.name).assertIsDisplayed()` passes!
- **Firebase Emulator Test Constraint**:
  - `firebase` CLI is NOT installed in this workspace. Unfiltered `./gradlew testDebugUnitTest` executes `FirestoreRoutineRepositoryRuleTest` (`FirestoreEmulatorTestBase.kt`), which attempts to connect to `127.0.0.1:8085` & `127.0.0.1:9099` and fails under `failFast = true` unless M1 adds an emulator availability `Assume` guard or `--tests` filtering is used.
- **Python Environment**:
  - `python3` is `3.9.6`. `PIL` (`Pillow`) and `pytest` are **not** installed; `/usr/bin/sips` and `/usr/bin/file` exist on macOS. Therefore, `tests/e2e/verify_stretch_coach.py` MUST be **100% pure Python 3.9 standard library** (including native JPEG SOF header parsing) so it executes anywhere with zero `pip install` steps.

---

## 2. `TEST_INFRA.md` Plan

When `teamwork_test_writer` sets up the E2E track, it will create `TEST_INFRA.md` at the repository root documenting:
1. **Test Suites & Locations**:
   - **Android / Robolectric E2E Suite**:
     - `app/src/test/java/com/example/ArtworkE2ETest.kt` (Tiers 1, 2, 3-pairs, 4-repo)
     - `app/src/test/java/com/example/UiNoImageE2ETest.kt` (Tier 3 Compose UI null-image & custom-image rendering across all 4 UI sites)
   - **Python Static/Asset/Gallery E2E Suite**:
     - `tests/e2e/verify_stretch_coach.py` (Tiers 1–4 cross-checking Kotlin sources, drawable JPEGs, and `stretch_gallery.html`)
2. **Exact Execution Commands**:
   - **Android Build + E2E + Unit Tests** (filtered to exclude offline Firebase emulator rule tests when emulators are not running; also verify unfiltered if M1 adds emulator skip/Assume):
     ```bash
     JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
     ANDROID_HOME="$HOME/Library/Android/sdk" \
     ./gradlew assembleDebug testDebugUnitTest \
       --tests "com.example.ExampleUnitTest" \
       --tests "com.example.ExampleRobolectricTest" \
       --tests "com.example.CardLayoutScreenshotTest" \
       --tests "com.example.GreetingScreenshotTest" \
       --tests "com.example.ArtworkE2ETest" \
       --tests "com.example.UiNoImageE2ETest" \
       --no-daemon
     ```
   - **Python E2E Suite** (supports `--phase m1` before M6 gallery exists and default `--phase final` after M6):
     ```bash
     python3 tests/e2e/verify_stretch_coach.py --phase final
     ```
3. **Environment Prerequisites & Constraints**:
   - JDK 21 + Android SDK paths; `isIncludeAndroidResources = true` enabled; pure Python 3.9 stdlib (`no PIL/pytest needed`); Firebase emulators (`127.0.0.1:8085`, `9099`) required only for `FirestoreRoutineRepositoryRuleTest`.
4. **Milestone Agnosticism Rule**:
   - Tests MUST NOT fail simply because a stretch is intentionally `null` (unmapped per R3/R4) or because M2–M5 batches have not yet been generated. All positive mapping assertions verify *structural and safety invariants* (non-legacy, valid `1376x768` JPEG resource, L/R symmetry, zero keyword traps) rather than demanding 150/150 non-null images.

---

## 3. Opaque-Box E2E Test Suite Specification (Tiers 1–4)

### 3.1 Android / Robolectric E2E Suite A: `app/src/test/java/com/example/ArtworkE2ETest.kt`
Uses `@RunWith(RobolectricTestRunner::class)` and `@Config(sdk = [34])` with `ApplicationProvider.getApplicationContext<Context>()`.

| Tier | Test Case Name | Requirement | Exact Inputs & Assertions |
|---|---|---|---|
| **T1** | `tier1_catalogCompletenessAndIntegrity` | R1, AC | Verify `StretchLibrary.ALL_100_STRETCHES.size == 150`; first 100 IDs distinct from `ExtraYogaAsanas.EXTRA_50_ASANAS` (`size == 50`); 150 unique IDs & unique trimmed names; all `prepCue`, `holdCue`, `benefits` non-blank; `defaultPrepSeconds > 0`, `defaultHoldSeconds > 0`; every `ExtraYogaAsanas` item has non-blank Devanagari `indianName` (`\u0900..\u097F`). |
| **T1** | `tier1_legacy15DrawablesEliminatedFromResourcesAndMapper` | R2, AC | For all 15 legacy resource names (`img_stretch_butterfly`, `calf`, `cat_cow`, `chest`, `cobra`, `figure_four`, `floor`, `hamstring`, `lunge`, `pigeon`, `quad`, `side_bend`, `twist`, `upper`, `wrist`): assert `context.resources.getIdentifier(name, "drawable", context.packageName) == 0` AND assert `ArtworkMapper.getDrawableForStretch(...)` over all 150 catalog items never returns any resource whose `getResourceEntryName(id)` is in the 15 legacy names. |
| **T1** | `tier1_allMappedDrawablesDecodeAsValidUnifiedDimensions` | R2, R4 | Iterate all 150 `StretchLibrary.ALL_100_STRETCHES`: whenever `ArtworkMapper.getDrawableForStretch(s.name, s.category)` returns non-null `resId`, assert `resId != 0`, `getResourceTypeName(resId) == "drawable"`, `getResourceEntryName(resId)` matches `^img_stretch_[a-z0-9_]+$`, and `BitmapFactory.Options(inJustDecodeBounds = true)` decodes `outWidth == 1376` and `outHeight == 768` (or 16:9 `abs(outWidth.toFloat()/outHeight - 16f/9f) < 0.05f`, strictly rejecting `1024x1024` / `1408x768`). |
| **T2** | `tier2_unknownCustomAndCategoryFallbacksReturnNull` | R3, AC | Assert `ArtworkMapper.getDrawableForStretch(...) == null` for: `("", "")`, `("   ", "Yoga Asanas")`, `("Unknown Custom Stretch XYZ", "Yoga Asanas")`, `("Morning Desk Flow", "Neck & Shoulders")`, `("My Custom Hamstring Flow", "Hamstrings & Quads")`, `("Hip Routine", "Hips & Glutes")`, `("Back Relief", "Lower Back & Core")`, `("Calf Mobility", "Calves & Feet")`, `("Chest Opening", "Chest & Upper Back")`, `("Arm Stretch", "Arms & Wrists")`, `("Standing Stretch", "General")`, `("Floor Stretch", "Lower Back & Core")`. |
| **T2** | `tier2_substringCollisionTrapsNeverMapToWrongDrawable` | R2, R3, R4 | Assert exact non-collision across every known substring trap (passes whether stretch is `null` or mapped to its true pose drawable):<br>- `"Chaturanga Dandasana (Four-Limbed Staff)"` -> entry name `!= "img_stretch_lotus"`<br>- `"Dandasana (Staff Pose)"`, `"Vajrasana (Thunderbolt Pose)"`, `"Tolasana (Scale Pose)"`, `"Simhasana (Lion Pose)"`, `"Anulom Vilom (Alternate Nostril Breathing)"` -> entry name `!= "img_stretch_lotus"`<br>- `"Wrist Figure-Eight Fluid Mobilizer"` -> entry name `!= "img_stretch_figure_four"`<br>- `"Skandasana (Side Lunge - Right)"` & `"(Left)"` -> entry name NOT in `{"img_stretch_lunge", "img_stretch_warrior"}`<br>- `"Downward Dog Calf Pedaling"` -> entry name `!= "img_stretch_calf"`<br>- `"Puppy Pose (Melting Heart)"` & `"Child's Pose with Lat Reach"` -> entry name `!= "img_stretch_child"`<br>- `"Ustrasana (Camel Pose)"`, `"Matsyasana (Fish Pose)"`, `"Dhanurasana (Bow Pose)"` -> entry name `!= "img_stretch_chest"`<br>- `"Natarajasana (Dancer Pose - Right/Left)"` -> entry name `!= "img_stretch_tree"`<br>- `"Chakrasana (Wheel Pose)"` & `"Purvottanasana (Upward Plank Pose)"` -> entry name `!= "img_stretch_bridge"`<br>- `"Virabhadrasana I (Warrior I - Right/Left)"`, `"Virabhadrasana III (Warrior III - Right/Left)"`, `"Ardha Chandrasana (Half Moon - Right/Left)"`, `"Utthita Parsvakonasana (Extended Side Angle - Right/Left)"` -> entry name `!= "img_stretch_warrior"`<br>- `"Sirsasana (Supported Headstand)"`, `"Bakasana (Crow Pose)"`, `"Phalakasana (Plank Pose)"`, `"Utkatasana (Chair Pose Stretch)"`, `"Surya Namaskar (Sun Salutation Flow)"` -> entry name `!= "img_stretch_standing"`<br>- `"Chin Tuck & Levator Stretch"`, `"Sternocleidomastoid (SCM) Neck Stretch"` -> entry name `!= "img_stretch_neck"` |
| **T2** | `tier2_caseAndWhitespaceNormalizationAndCategoryInvariance` | Contract | For every mapped stretch `s` where `ArtworkMapper.getDrawableForStretch(s.name, s.category) != null`: assert `getDrawableForStretch("  ${s.name.lowercase()}  ", "") == expected`, `getDrawableForStretch(s.name.uppercase(), "Completely Different Category") == expected`, and `getDrawableForStretch(s.name.replace(" ", "   "), s.category) == expected` (proving category does not hijack exact name lookup and whitespace/case are normalized). |
| **T3** | `tier3_symmetricalLeftRightPairsMapConsistently` | R2 | Test all 32 symmetrical L/R pairs (22 in `StretchLibrary` + 10 in `ExtraYogaAsanas`: `neck_side`, `cross_body_shoulder`, `thread_the_needle`, `reclining_spinal_twist`, `hip_flexor_lunge`, `figure_four_glute`, `pigeon_pose`, `standing_quad`, `wall_calf_gastrocnemius`, `trikonasana_triangle`, `vrikshasana_tree`, `janu_sirsasana`, `ardha_matsyendrasana`, `gomukhasana_arms`, `virabhadrasana_one`, `virabhadrasana_two`, `anjaneyasana_low_lunge`, `supta_padangusthasana`, `garudasana_eagle_pose`, `parsvottanasana_pyramid`, `eka_pada_rajakapotasana_pigeon`, `supta_matsyendrasana_twist`, `ardha_chandrasana`, `utthita_parsvakonasana`, `parivrtta_trikonasana`, `hanumanasana`, `natarajasana`, `vasisthasana`, `skandasana`, `bharadvajasana`, `parighasana_gate`, `virabhadrasana_3`). For each pair `(leftId, rightId)`: assert `getDrawableForStretch(left.name, left.category.title) == getDrawableForStretch(right.name, right.category.title)` (both same drawable OR both `null`). |
| **T4** | `tier4_repositorySeedingResolves150StretchesAnd101DevanagariNames` | R1, Data | Build in-memory `Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()`, run `StretchRepository(db.stretchDao(), context).checkAndSeedDefaults()`: assert `150` `LibraryStretchEntity` rows seeded; assert all `50` `ExtraYogaAsanas` rows match explicit Devanagari `indianName`; assert `#35` (`seated_single_leg_fold`) + `#51–#100` (`51` items) resolve non-blank Devanagari (`\u0900..\u097F`); assert remaining `49` items have `indianName == null`; assert all 5 default routines seed cleanly and their stretches never crash `ArtworkMapper`. |

---

### 3.2 Android / Robolectric Compose UI E2E Suite B: `app/src/test/java/com/example/UiNoImageE2ETest.kt`
Uses `@RunWith(RobolectricTestRunner::class)`, `@GraphicsMode(GraphicsMode.Mode.NATIVE)`, `@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])`, and `@get:Rule val composeTestRule = createComposeRule()`.

| Tier | Test Case Name | UI Site | Exact Setup & Assertions |
|---|---|---|---|
| **T3** | `stretchItemCard_nullArtwork_omitsImageAndShowsDetails` | `StretchItemCard` | Render `StretchItemCard` with unmapped `StretchEntity(id = 101L, name = "Custom Unmapped Mobility", indianName = "योग आसन", category = "Yoga Asanas", preparationSeconds = 8, holdSeconds = 30, customImagePath = null)`. Set `mainClock.autoAdvance = false; mainClock.advanceTimeBy(100)`. Assert `onNodeWithTag("stretch_card_101").assertIsDisplayed()`, `onNodeWithText("Custom Unmapped Mobility").assertIsDisplayed()`, `onNodeWithText("योग आसन").assertIsDisplayed()`, `onNodeWithText("Prep\u00A08s").assertIsDisplayed()`, `onNodeWithContentDescription("Custom Unmapped Mobility").assertDoesNotExist()`. |
| **T3** | `stretchItemCard_mappedOrCustomArtwork_rendersImage` | `StretchItemCard` | (a) Render with mapped `"Tadasana (Mountain Pose Alignment)"` -> assert `onNodeWithContentDescription("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()`. (b) Render with unmapped name + real temp JPEG file in `customImagePath` -> assert `onNodeWithContentDescription(stretch.name).assertExists()`. |
| **T3** | `playerArtworkCard_nullArtwork_omitsImageAndPreservesBadgesAndText` | `PlayerArtworkCard` | Test 3 states with `mainClock.autoAdvance = false; mainClock.advanceTimeBy(100)`:<br>1. `currentStretch = unmappedStretch(name = "Custom Breathing Flow", notes = "Breathe slowly")`, `state = PlayerState.PREPARING`, `currentIndex = 1`, `totalCount = 5`: assert `onNodeWithTag("player_artwork_card").assertIsDisplayed()`, `onNodeWithText("STEP 2 OF 5").assertIsDisplayed()`, `onNodeWithText("PREPARING").assertIsDisplayed()`, `onNodeWithText("Custom Breathing Flow").assertIsDisplayed()`, `onNodeWithText("Breathe slowly").assertIsDisplayed()`, `onNodeWithContentDescription("Custom Breathing Flow").assertDoesNotExist()`, `onNodeWithContentDescription("Stretch demonstration").assertDoesNotExist()`.<br>2. Same stretch in `PlayerState.HOLDING` -> `"HOLDING"` displayed, image node does not exist.<br>3. `currentStretch = null`, `state = PlayerState.IDLE`, `totalCount = 0`: assert `"READY TO START"` & `"READY"` displayed, `onNodeWithContentDescription("Stretch demonstration").assertDoesNotExist()` (verifying removal of `PlayerArtworkCard.kt:62` hardcoded `img_stretch_standing` fallback). |
| **T3** | `playerArtworkCard_mappedArtwork_rendersImageAndBadges` | `PlayerArtworkCard` | Render with `"Adho Mukha Svanasana (Downward-Facing Dog)"`, `PlayerState.PAUSED`, `currentIndex = 0`, `totalCount = 3`: assert `"STEP 1 OF 3"`, `"PAUSED"`, and `onNodeWithContentDescription("Adho Mukha Svanasana (Downward-Facing Dog)").assertIsDisplayed()`. |
| **T3** | `addFromLibraryDialog_mixedMappedAndNullItems_rendersCleanlyWithoutCrash` | `AddFromLibraryDialog` | Render `AddFromLibraryDialog` with 2 items: `mapped` (`tadasana_mountain_alignment`) and `unmapped` (`LibraryStretchEntity(id = "custom_unmapped_1", name = "Custom Unmapped Pose", indianName = "कस्टम आसन", category = "Yoga Asanas", ...)`). Assert `onNodeWithTag("add_from_library_dialog").assertIsDisplayed()`, `onNodeWithTag("library_item_custom_unmapped_1").assertIsDisplayed()`, `onNodeWithText("Custom Unmapped Pose").assertIsDisplayed()`, `onNodeWithText("कस्टम आसन").assertIsDisplayed()`, `onNodeWithContentDescription("Custom Unmapped Pose").assertDoesNotExist()`, and `onNodeWithContentDescription("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()`. |
| **T3** | `stretchDetailDialog_nullArtwork_omitsImageAndShowsCues` | `StretchDetailDialog` | Render `StretchDetailDialog` with `[unmappedLibraryStretch(id = "custom_detail_1", name = "Custom Unmapped Detail", indianName = "विस्तार आसन", prepCue = "Sit upright cleanly.", holdCue = "Hold steady breath.", benefits = "Improves focus.")]`. Assert `onNodeWithTag("stretch_detail_dialog").assertIsDisplayed()`, `onNodeWithText("Custom Unmapped Detail").assertIsDisplayed()`, `onNodeWithText("विस्तार आसन").assertIsDisplayed()`, `onNodeWithText("Sit upright cleanly.").assertIsDisplayed()`, `onNodeWithText("Hold steady breath.").assertIsDisplayed()`, `onNodeWithText("Improves focus.").assertIsDisplayed()`, `onNodeWithContentDescription("Custom Unmapped Detail").assertDoesNotExist()`. |

---

### 3.3 Python / CLI E2E Verification Suite: `tests/e2e/verify_stretch_coach.py`
Pure **Python 3.9+ standard library only** (`re`, `pathlib`, `struct`, `argparse`, `html.parser`, `sys`). Runs from repository root.

**CLI Arguments**:
- `--phase {m1,final}` (default: `final`). In `--phase m1`, checks catalog, legacy drawable deletion, `ArtworkMapper`/`ArtworkBatch` integrity, and JPEG specs while skipping `stretch_gallery.html` if M6 hasn't run yet. In `--phase final`, enforces **all** checks including `stretch_gallery.html`.

**Pure-Python JPEG Validator (`inspect_jpeg(path)`)**:
- Reads binary bytes, checks magic SOI `b'\xff\xd8\xff'` and EOI `b'\xff\xd9'`.
- Walks JPEG marker segments (`0xFF <marker> <2-byte length>`) to find Start-Of-Frame (`SOF0 = 0xC0` baseline, `SOF1 = 0xC1`, `SOF2 = 0xC2` progressive). Extracts `(height, width)` from bytes `[3:5]` and `[5:7]` of the SOF payload (`>HH`), and records `sof_type`.
- Asserts: valid JPEG, `sof_type == 0xC0` (baseline JPEG — or warns/fails on non-JPEG/corrupt/truncated), `width == 1376` and `height == 768` (exact unified studio spec; strictly rejects `1024x1024` and `1408x768`).

**Kotlin Source Parsers**:
1. **Catalog Parser**: Extracts ordered `LibraryStretch(...)` blocks from `StretchLibrary.kt` (must be `100`) and `ExtraYogaAsanas.kt` (must be `50`), validating unique IDs, non-empty `name`/`prepCue`/`holdCue`/`benefits`, and explicit `indianName` on all 50 `ExtraYogaAsanas` items.
2. **Sanskrit Devanagari Resolver**: Parses `SANSKRIT_TO_DEVANAGARI_MAP` from `SanskritPhonetics.kt` and reproduces `buildSpokenPlan(name).sanskritDevanagari` so Python knows the exact expected runtime Devanagari string for all 150 stretches (`101` non-null, `49` `null`).
3. **App Mapping Parser**:
   - Parses `ArtworkMapper.kt` and `ArtworkBatch1.kt`..`ArtworkBatch4.kt`:
     * Verifies `ArtworkMapper.getDrawableForStretch` return type is `Int?` (not `Int`).
     * Verifies zero category fallback patterns (`c.contains(`) and zero catch-all `else -> R.drawable.` in `ArtworkMapper.kt`.
     * Extracts all `key to R.drawable.<res>` mappings across `coreReferenceMap` (in `ArtworkMapper.kt`) and `ArtworkBatch1..4.kt`, verifying zero references to the 15 legacy drawables and verifying every referenced `R.drawable.<res>` exists in `app/src/main/res/drawable/<res>.jpg`.
     * Verifies `PlayerArtworkCard.kt` does NOT contain `R.drawable.img_stretch_standing` hardcoded fallback.

**Checks Executed by `verify_stretch_coach.py`**:
- **Tier 1 (Catalog & Drawable Hygiene)**:
  1. `100 + 50 = 150` catalog items, unique IDs, non-blank cues.
  2. None of the 15 legacy files (`img_stretch_{butterfly,calf,cat_cow,chest,cobra,figure_four,floor,hamstring,lunge,pigeon,quad,side_bend,twist,upper,wrist}.*`) exist anywhere in `app/src/main/res/`.
  3. Zero `.png` or `.webp` stretch drawables in `app/src/main/res/drawable*`; every `img_stretch_*.jpg` filename matches `^img_stretch_[a-z0-9_]+\.jpg$`, passes `inspect_jpeg()` as valid baseline JPEG with `1376x768` dimensions.
  4. `img_app_icon.jpg`, `ic_launcher_background.xml`, `ic_launcher_foreground.xml` remain present.
- **Tier 2 (Mapping Safety & Disjointness)**:
  5. Every `R.drawable.img_stretch_*` referenced in `ArtworkMapper.kt` and `ArtworkBatch1..4.kt` resolves to an existing `1376x768` file in `app/src/main/res/drawable/`.
  6. Zero legacy drawable references and zero substring/category fallback rules in `ArtworkMapper.kt` and `ArtworkBatch1..4.kt`.
  7. Every catalog stretch's normalized lookup key maps deterministically (no conflicting duplicate keys pointing to different drawables across batches).
- **Tier 3 (Symmetry & HTML Gallery Verification — `--phase final`)**:
  8. Symmetrical L/R pair consistency across all 32 L/R pairs (left & right map to same drawable or both `null`).
  9. `stretch_gallery.html` parses cleanly with `html.parser`:
     - Contains **150** stretch cards (`[data-index]` / `.stretch-card`), in **exact `#1–#150` catalog order** matching `(id, name)` 1:1.
     - Every stretch with an Indian/Sanskrit name (all 50 `ExtraYogaAsanas` explicit `indianName` + 51 `StretchLibrary` runtime Devanagari names = 101 items) displays its exact Devanagari string inside the card.
     - Every mapped stretch renders `<img src="app/src/main/res/drawable/<drawable>.jpg">` matching the app's `ArtworkMapper` + `ArtworkBatch1..4` assignment, and that relative path exists on disk and passes `inspect_jpeg()` (`1376x768`).
     - Zero `<img>` tags point to any of the 15 legacy drawables, external HTTP(S) URLs, or missing paths.
     - Every unmapped (`null`) stretch has **zero** `<img>` tags in its card and explicitly displays a `"No verified image assigned"` / `data-status="unmapped"` state.

---

## 4. Exact Specification for `stretch_gallery.html` (R1 — M6 Deliverable)

**File Path**: `/Users/hn/antigravity/Stretch Coach/stretch_gallery.html` (repository root; offline-capable, zero external CDN CSS/JS/fonts).

### 4.1 Static DOM & Machine-Verifiable Contract
All 150 cards MUST be present in the initial static HTML DOM (not injected solely by client-side JS from JSON) in exact `StretchLibrary.ALL_100_STRETCHES` (`#1–#100`) + `ExtraYogaAsanas.EXTRA_50_ASANAS` (`#101–#150`) order so both static parsers and browsers with or without JS render the complete sheet.

Each stretch card MUST use this semantic structure and `data-*` attributes:
```html
<article class="stretch-card"
         data-index="52"
         data-id="adho_mukha_svanasana_downward_dog"
         data-source="StretchLibrary"
         data-category="Yoga Asanas"
         data-drawable="img_stretch_downward_dog.jpg"
         data-status="mapped">
  <!-- Header: #52, source badge, category badge, stretch ID -->
  <!-- Title: Adho Mukha Svanasana (Downward-Facing Dog) -->
  <!-- Indian/Sanskrit Name: अधो मुख श्वानासन (when present; omitted or "—" if none) -->
  <!-- Preview area:
       If mapped:
         <a class="thumb-link" href="app/src/main/res/drawable/img_stretch_downward_dog.jpg" target="_blank">
           <img src="app/src/main/res/drawable/img_stretch_downward_dog.jpg"
                alt="Adho Mukha Svanasana (Downward-Facing Dog)" loading="lazy">
         </a>
         <div class="drawable-meta"><code>img_stretch_downward_dog.jpg</code></div>
       If unmapped (null):
         <div class="no-image-state">No verified image assigned</div>
  -->
  <!-- Cues: Prep Cue, Hold Cue, Benefits, Timings (Prep Xs / Hold Ys) -->
</article>
```
- When `data-status="unmapped"`, set `data-drawable=""` (empty) and render `.no-image-state` with explicit text `"No verified image assigned"` (NO `<img>` tag, NO placeholder stock image).
- When `data-status="mapped"`, `data-drawable` MUST equal `<filename>.jpg`, and `<img src="app/src/main/res/drawable/<filename>.jpg">` + `<a href="app/src/main/res/drawable/<filename>.jpg">` MUST both use relative paths from repo root.

### 4.2 Visual & Reviewer Ergonomics
- **Top Summary Header**: Title ("Stretch Coach — 150-Pose Visual Gallery & Audit Sheet"), live/static counts (`150 Total`, `Mapped: N`, `Unmapped (No Image): M`, `Unique Drawables: K`).
- **Interactive Client-Side Filters** (vanilla JS toggling `.is-hidden { display: none; }` without removing DOM nodes):
  - Text search input (filters by stretch name, ID, Indian/Sanskrit Devanagari name, cues, or drawable filename).
  - Category dropdown / filter chips (`All`, `Yoga Asanas`, `Neck & Shoulders`, `Chest & Upper Back`, `Lower Back & Core`, `Hips & Glutes`, `Hamstrings & Quads`, `Calves & Feet`, `Arms & Wrists`).
  - Status toggle (`All`, `Mapped Only`, `Unmapped / No Image Only`).
- **Card Grid Layout**: Responsive CSS Grid (`repeat(auto-fill, minmax(320px, 1fr))`), `aspect-ratio: 16 / 9` preview frame matching `1376×768` unified studio illustrations without cropping heads/feet, clean typography with Devanagari font fallback (`"Noto Sans Devanagari", "Kohinoor Devanagari", sans-serif`), and collapsible/cleanly styled `Prep` & `Hold` cues so reviewers can audit anatomical accuracy side-by-side with the pose illustration.
- **Generator Script Recommendation for M6**: M6 should generate `stretch_gallery.html` programmatically via a reproducible Python generator reading `StretchLibrary.kt`, `ExtraYogaAsanas.kt`, `SanskritPhonetics.kt`, `ArtworkMapper.kt`, and `ArtworkBatch1..4.kt` directly so names, Devanagari strings, cues, and drawable mappings can never drift out of sync.
