# Handoff Report — Reviewer 1 (`teamwork_reviewer_m1_1`)

## Review Summary

**Verdict**: **APPROVE**

All Milestone M1 deliverables (`ArtworkMapper`, `ArtworkBatch1..4` stubs, 15 legacy drawable deletions, 4 UI composables, unit/UI tests, `FirestoreEmulatorTestBase`) and the E2E test suite (`TEST_INFRA.md`, `TEST_READY.md`, `ArtworkE2ETest.kt`, `UiNoImageE2ETest.kt`, `tests/e2e/verify_stretch_coach.py`) satisfy `PROJECT.md` and `ORIGINAL_REQUEST.md` requirements with zero integrity violations.

---

## 1. Observation

1. **Drawables (`app/src/main/res/drawable/`)**:
   - `list_dir` confirmed exactly 11 files present: `ic_launcher_background.xml`, `ic_launcher_foreground.xml`, `img_app_icon.jpg`, `img_stretch_bridge.jpg`, `img_stretch_child.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_lotus.jpg`, `img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_tree.jpg`, `img_stretch_warrior.jpg`.
   - None of the 15 legacy mixed-style drawables (`butterfly`, `calf`, `cat_cow`, `chest`, `cobra`, `figure_four`, `floor`, `hamstring`, `lunge`, `pigeon`, `quad`, `side_bend`, `twist`, `upper`, `wrist`) exist on disk or are referenced anywhere under `app/src/main/java/`.
   - Visual inspection via `view_file` of all 5 mapped M1 JPEGs (`img_stretch_child.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_tree.jpg`, `img_stretch_warrior.jpg`, `img_stretch_standing.jpg`) confirms unified studio art style (sage-olive top, charcoal leggings, low bun, barefoot, woven jute mat, honey-oak planks, sunlit cream studio, zero text/artifacts) and exact anatomical match to `#53`/`#131`, `#52`, `#57`/`#58`, `#73`/`#74`, and `#84` (and confirmed `img_stretch_child.jpg` depicts knees together, excluding `#50` `"Restorative Child's Pose"` wide-knees cue per R4).

2. **`ArtworkMapper.kt` & `ArtworkBatch1..4.kt`**:
   - `app/src/main/java/com/example/data/ArtworkMapper.kt:45`: `@DrawableRes fun getDrawableForStretch(name: String, @Suppress("UNUSED_PARAMETER") category: String = ""): Int?`
   - Lines 13–14: `normalizeKey(raw)` trims, lowercases, and collapses whitespace (`Regex("\\s+")`).
   - Lines 16–42: `coreReferenceMap` maps only the 5 strictly R4-verified M1 drawables (`img_stretch_downward_dog`, `img_stretch_child`, `img_stretch_tree`, `img_stretch_warrior`, `img_stretch_standing`) across 16 normalized keys (exact names + IDs for `#52`, `#53`, `#131`, `#57`, `#58`, `#73`, `#74`, `#84`).
   - Lines 49–53: Lookup checks `coreReferenceMap[key]` then `ArtworkBatch1..4.exactNameMap[key]`, returning `null` for blank or unmatched keys. Zero substring (`contains`), category, or `else` fallbacks exist.
   - `ArtworkBatch1.kt`, `ArtworkBatch2.kt`, `ArtworkBatch3.kt`, `ArtworkBatch4.kt` under `app/src/main/java/com/example/data/artwork/` each define `val exactNameMap: Map<String, Int> = emptyMap()`.

3. **UI Composables (4 sites + codebase-wide `painterResource`/`customImagePath` audit)**:
   - `grep_search` across `app/src/main/java` confirmed `getDrawableForStretch` and `customImagePath` are only used in the 4 expected composables (plus `StretchEntity`, `LibraryStretchEntity`, `StretchRepository`); the only other `painterResource` call is `HeaderBar.kt:67` (`R.drawable.img_app_icon`).
   - `StretchItemCard.kt:83-86,132-158`: Uses `artworkRes: Int?`; wraps both `Box(size(50.dp))` and `Spacer(width(10.dp))` in `if (customFile != null || artworkRes != null)`; renders `AsyncImage` if `customFile != null` else `Image` if `artworkRes != null`.
   - `AddFromLibraryDialog.kt:358-411`: Uses `artworkRes: Int?`; wraps both `Box(size(48.dp))` and `Spacer(width(10.dp))` in `if (customFile != null || artworkRes != null)`; preserves `customFile` precedence.
   - `StretchDetailDialog.kt:178-218`: Uses `artworkRes: Int?`; wraps both `Box(aspectRatio(16f / 10f))` and `Spacer(height(14.dp))` in `if (customFile != null || artworkRes != null)`; preserves `customFile` precedence.
   - `PlayerArtworkCard.kt:62-68,90-101,116-313`: Removed `R.drawable.img_stretch_standing` fallback and `import com.example.R`. When `hasImage == true`, Step pill uses `TealPrimaryLight` on `Color.White.copy(alpha = 0.92f)` (fixing Dark-mode contrast) and `IDLE` Phase pill uses `MaterialTheme.colorScheme.onPrimary` on `MaterialTheme.colorScheme.primary`. When `hasImage == false`, renders a compact `surface` header (`primaryContainer`/`onPrimaryContainer` Step pill, `badgeColor`/`phaseTextColor` Phase pill, `onSurface` stretch name, `onSurfaceVariant` notes) with zero image box or dark scrims.

4. **Test Infrastructure & E2E Suites**:
   - `FirestoreEmulatorTestBase.kt:72-102`: Probes `127.0.0.1:9099` and `127.0.0.1:8085` (300ms socket timeout) and uses `Assume.assumeTrue(...)` unless `-DrequireFirebaseEmulator=true`. `FirestoreRoutineRepositoryRuleTest.kt` is intact and still runs `unauthenticatedUser_cannotAccessRoutines` offline.
   - `TEST_INFRA.md` and `TEST_READY.md` accurately document all test suites, prerequisites, milestone-agnosticism rules, and exact execution commands.
   - `ArtworkE2ETest.kt` (8 tests) covers Tiers 1–4: 150-item catalog integrity, 50 explicit Devanagari `ExtraYogaAsanas`, legacy-15 exclusion, `1376x768` JPEG decoding via `BitmapFactory`, null fallback traps (12 cases), 34 substring collision traps, case/whitespace/category invariance, all 32 L/R pairs, and Room `StretchRepository.checkAndSeedDefaults()` seeding 150 library rows (101 Devanagari, 49 `null`) + 5 default routines.
   - `UiNoImageE2ETest.kt` (9 tests) disables `mainClock.autoAdvance` (`advanceTimeBy(100)`) to handle `rememberInfiniteTransition`/`basicMarquee` and tests all 4 composables (Light/Dark, `IDLE`/`PREPARING`/`HOLDING`/`PAUSED`, `null`, verified drawable, and temp-JPEG `customImagePath`).
   - `tests/e2e/verify_stretch_coach.py` (pure Python 3.9+ stdlib) verifies catalog, Devanagari parity, JPEG `SOF0` `1376x768` headers, `ArtworkMapper`/`ArtworkBatch1..4` guardrails, L/R symmetry (`--phase m1`), and `stretch_gallery.html` (`--phase final`).

5. **Independent Verification Execution**:
   - `python3 tests/e2e/verify_stretch_coach.py --phase m1` → **Exit 0**:
     `[PASS] verify_stretch_coach.py --phase m1: 150 catalog items (101 Devanagari), 8 unified 1376x768 JPEGs, 0 legacy drawables, 32 L/R pairs consistent, app mappings verified (8 mapped, 142 unmapped; gallery skipped in m1).`
   - `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew clean assembleDebug testDebugUnitTest --no-daemon` → **Exit 0 (`BUILD SUCCESSFUL in 12s`)**.
   - Fresh uncached test execution (`./gradlew assembleDebug testDebugUnitTest --tests "com.example.*" --no-build-cache --no-daemon`) → **Exit 0 (`BUILD SUCCESSFUL in 17s`, `:app:testDebugUnitTest` executed)**.
   - JUnit XML outputs (`app/build/test-results/testDebugUnitTest/TEST-*.xml`, timestamps `2026-11-23T14:34:08Z`–`14:34:14Z`):
     - `ArtworkE2ETest`: 8 tests, 0 failures, 0 errors, 0 skipped
     - `UiNoImageE2ETest`: 9 tests, 0 failures, 0 errors, 0 skipped
     - `ArtworkUiNullHandlingTest`: 6 tests, 0 failures, 0 errors, 0 skipped
     - `ExampleUnitTest`: 4 tests, 0 failures, 0 errors, 0 skipped
     - `ExampleRobolectricTest`: 2 tests, 0 failures, 0 errors, 0 skipped
     - `CardLayoutScreenshotTest`: 1 test, 0 failures, 0 errors, 0 skipped
     - `GreetingScreenshotTest`: 1 test, 0 failures, 0 errors, 0 skipped
     - `FirestoreRoutineRepositoryRuleTest`: 3 tests, 1 passed, 2 skipped (`Assume.assumeTrue`), 0 failures, 0 errors
     - **Total**: **34 tests, 32 passed, 2 skipped (emulator-only), 0 failed, 0 errors**.

---

## 2. Logic Chain

1. Observation 1 + Observation 2 establish that all 15 legacy drawables are removed, no legacy or fallback path exists in `ArtworkMapper.kt` or `ArtworkBatch1..4.kt`, and only the 5 R4-verified unified drawables (covering 8 catalog entries) are mapped in M1.
2. Observation 3 confirms all 4 UI rendering sites (and no other composables in the app) consume `getDrawableForStretch` as `Int?`, preserve `customFile` precedence, and cleanly omit both the image `Box` and adjacent `Spacer` when unmapped, with Light/Dark contrast preserved in `PlayerArtworkCard`.
3. Observation 4 + Observation 5 confirm the E2E and unit test suites genuinely exercise Tiers 1–4 without hardcoded shortcuts or self-certifying fakes, and both the Python E2E script (`--phase m1`) and fresh uncached Gradle build/test suite pass 100% of active tests.

---

## 3. Adversarial Challenge & Findings

**Overall Risk Assessment**: **LOW** (No Critical or Major findings; 3 Minor non-blocking observations for future milestones)

### [Minor] Finding 1 — Pre-existing contrast on `PlayerArtworkCard` `PAUSED` badge
- **What**: `PlayerArtworkCard.kt:94` pairs `Color(0xFFF59E0B)` (Amber-500) with `Color.White` text (`~2.1:1` contrast ratio).
- **Where**: `app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt:94-101`.
- **Why**: Pre-existing palette choice (present in both image and non-image modes), not introduced by M1; M1 properly fixed `IDLE` (`onPrimary`) and `hasImage` Step badge (`TealPrimaryLight` on white).
- **Suggestion**: Consider darkening `PAUSED` badge text to dark slate or darkening the amber fill in a future polish pass if desired.

### [Minor] Finding 2 — Regex comment sensitivity in `verify_stretch_coach.py`
- **What**: `parse_artwork_mappings()` (`tests/e2e/verify_stretch_coach.py:487`) scans `ArtworkMapper.kt` and `ArtworkBatch1..4.kt` with `pair_re` without stripping Kotlin comments (`// ...`, `/* ... */`).
- **Where**: `tests/e2e/verify_stretch_coach.py:487-513`.
- **Why**: Clean in M1 because no comments contain `"..." to R.drawable....`. In M2–M5, if a batch author leaves a commented-out example mapping, the regex would parse it.
- **Suggestion**: Keep `ArtworkBatch1..4.kt` free of commented-out `"..." to R.drawable....` lines during M2–M5 (or strip comments in `verify_stretch_coach.py` if needed).

### [Minor] Finding 3 — Gradle `--rerun-tasks` + Configuration Cache KSP quirk
- **What**: Running `./gradlew assembleDebug testDebugUnitTest --rerun-tasks --no-daemon` with reused configuration cache failed in `:app:kspDebugKotlin` (`app/build/kspCaches/debug/symbols (No such file or directory)`), whereas `./gradlew clean assembleDebug testDebugUnitTest --no-daemon` and `./gradlew assembleDebug testDebugUnitTest --tests "com.example.*" --no-build-cache --no-daemon` succeeded cleanly.
- **Where**: KSP + Gradle 9.3.1 build configuration (pre-existing environment behavior).
- **Suggestion**: Use `clean` or `--tests "com.example.*" --no-build-cache` instead of `--rerun-tasks` when forcing fresh test runs.

### Verified Claims
- 15 legacy drawables deleted, 11 files remain in `app/src/main/res/drawable/` → verified via `list_dir` and `verify_stretch_coach.py --phase m1` → **PASS**
- 5 M1 drawables match R4 pose cues & unified style → verified via `view_file` visual inspection → **PASS**
- `ArtworkMapper` returns `Int?`, zero fallbacks, maps only 5 drawables (8 stretches) → verified via `view_file` + `ArtworkE2ETest` + `ExampleUnitTest` → **PASS**
- All 4 UI composables omit `Box` + `Spacer` when null, preserve `customFile` precedence, Light/Dark contrast on `PlayerArtworkCard` → verified via `view_file` + `grep_search` + `UiNoImageE2ETest` + `ArtworkUiNullHandlingTest` → **PASS**
- `TEST_INFRA.md`, `TEST_READY.md`, `FirestoreEmulatorTestBase.kt` accurate → verified via `view_file` + fresh test run → **PASS**

### Coverage Gaps
- Live Firebase Auth/Firestore emulator execution (`127.0.0.1:9099`, `8085`) — risk level: **low** (emulators offline in host; offline rule test runs and `Assume.assumeTrue` skip verified; `-DrequireFirebaseEmulator=true` escape hatch exists).
- `stretch_gallery.html` (`verify_stretch_coach.py --phase final`) — risk level: **low** (explicitly M6 scope per `PROJECT.md`).

---

## 4. Caveats

- Two tests in `FirestoreRoutineRepositoryRuleTest` (`authenticatedUser_canSaveAndFetchRoutines`, `authenticatedUser_canDeleteRoutine`) were skipped via `Assume.assumeTrue` because local Firebase Auth/Firestore emulators (`127.0.0.1:9099`, `8085`) are not running in this environment.
- `stretch_gallery.html` does not yet exist (M6 deliverable), so only `--phase m1` of `verify_stretch_coach.py` applies at M1.

---

## 5. Conclusion

**Verdict: APPROVE.** Milestone M1 (Core Artwork Architecture, Legacy Drawable Cleanup, Null-Image UI across all 4 composables, Unit/UI tests, and Emulator reachability guard) and the Opaque-Box E2E Test Suite (`TEST_INFRA.md`, `TEST_READY.md`, `ArtworkE2ETest.kt`, `UiNoImageE2ETest.kt`, `verify_stretch_coach.py`) are complete, verified, and ready to proceed to M2–M5 artwork generation batches.

---

## 6. Verification Method

From `/Users/hn/antigravity/Stretch Coach`:
1. `python3 tests/e2e/verify_stretch_coach.py --phase m1` (expect exit `0`, `150 catalog items (101 Devanagari), 8 unified 1376x768 JPEGs, 0 legacy drawables, 32 L/R pairs consistent, app mappings verified (8 mapped, 142 unmapped; gallery skipped in m1)`).
2. `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew assembleDebug testDebugUnitTest --tests "com.example.*" --no-build-cache --no-daemon` (expect `BUILD SUCCESSFUL` and `34` tests: `32` passed, `2` emulator-skipped, `0` failed, `0` errors in `app/build/test-results/testDebugUnitTest/TEST-*.xml`).
3. Invalidation conditions: any legacy drawable restored, any non-null return from `ArtworkMapper.getDrawableForStretch` outside the 5 M1 drawables on the 8 verified stretches, any fallback image in `PlayerArtworkCard`, or any unit/E2E test failure.
