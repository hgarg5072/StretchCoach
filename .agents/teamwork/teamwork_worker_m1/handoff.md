# Milestone M1 Handoff Report (`teamwork_worker_m1`)

## 1. Observation

1. **Drawable Cleanup (`app/src/main/res/drawable/`)**:
   - Deleted the 15 legacy mixed-style drawables:
     - `img_stretch_butterfly.jpg`, `img_stretch_calf.jpg`, `img_stretch_cat_cow.jpg`, `img_stretch_chest.jpg`, `img_stretch_cobra.jpg`, `img_stretch_figure_four.jpg`, `img_stretch_floor.jpg`, `img_stretch_hamstring.jpg`, `img_stretch_lunge.jpg`, `img_stretch_pigeon.jpg`, `img_stretch_quad.jpg`, `img_stretch_side_bend.jpg`, `img_stretch_twist.jpg`, `img_stretch_upper.jpg`, `img_stretch_wrist.jpg`
   - Verified via `ls -1 app/src/main/res/drawable/` that the exact 11 files remaining are:
     - `ic_launcher_background.xml`, `ic_launcher_foreground.xml`, `img_app_icon.jpg`, `img_stretch_bridge.jpg`, `img_stretch_child.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_lotus.jpg`, `img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_tree.jpg`, `img_stretch_warrior.jpg`.

2. **Modular Artwork Batch Stubs & `ArtworkMapper.kt`**:
   - Created 4 modular registry stubs under `app/src/main/java/com/example/data/artwork/`:
     - `ArtworkBatch1.kt` (`package com.example.data.artwork`, `object ArtworkBatch1 { val exactNameMap: Map<String, Int> = emptyMap() }`)
     - `ArtworkBatch2.kt`
     - `ArtworkBatch3.kt`
     - `ArtworkBatch4.kt`
   - Refactored `app/src/main/java/com/example/data/ArtworkMapper.kt`:
     - Signature: `@DrawableRes fun getDrawableForStretch(name: String, @Suppress("UNUSED_PARAMETER") category: String = ""): Int?`
     - Normalization: `internal fun normalizeKey(raw: String): String = raw.trim().lowercase().replace(whitespaceRegex, " ")`
     - `coreReferenceMap` maps only the 5 strictly R4-verified drawables covering the 8 catalog stretches (`#52` `adho_mukha_svanasana_downward_dog`, `#53` `balasana_childs_pose`, `#131` `shashankasana_hare`, `#57` `vrikshasana_tree_right`, `#58` `vrikshasana_tree_left`, `#73` `virabhadrasana_two_right`, `#74` `virabhadrasana_two_left`, `#84` `tadasana_mountain_alignment`), explicitly excluding `#50` (`child_pose_full_body` / `"Restorative Child's Pose"`), `#1/#2`, `#54`, and `#101`.
     - Lookup checks `coreReferenceMap[key]` then `ArtworkBatch1..4.exactNameMap[key]`, returning `null` on blank or missing keys with zero `contains()` and zero category/`else` fallbacks.

3. **UI Composables Updated (4 files)**:
   - `app/src/main/java/com/example/ui/components/StretchItemCard.kt`: Typed `artworkRes: Int?`; wrapped the `50.dp` thumbnail `Box` and `Spacer(width = 10.dp)` in `if (customFile != null || artworkRes != null)`; used `else if (artworkRes != null)`.
   - `app/src/main/java/com/example/ui/components/AddFromLibraryDialog.kt`: Typed `artworkRes: Int?`; wrapped the `48.dp` thumbnail `Box` and `Spacer(width = 10.dp)` in `if (customFile != null || artworkRes != null)`; used `else if (artworkRes != null)`.
   - `app/src/main/java/com/example/ui/components/StretchDetailDialog.kt`: Typed `artworkRes: Int?`; wrapped the `16:10` artwork `Box` and `Spacer(height = 14.dp)` in `if (customFile != null || artworkRes != null)`; used `else if (artworkRes != null)`.
   - `app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt`: Removed `import com.example.R` and `R.drawable.img_stretch_standing` fallback; checks `customFile` then `ArtworkMapper.getDrawableForStretch(...)`; renders the `16:10` image frame with scrims only when `hasImage == true` (with `TealPrimaryLight` text on the white Step pill and `onPrimary` on `IDLE` phase badge); when `hasImage == false`, renders a compact surface-themed header (`primaryContainer`/`onPrimaryContainer` Step pill, `badgeColor`/`phaseTextColor` Phase pill, `onSurface` stretch name, `onSurfaceVariant` notes) with zero image box or dark scrims.

4. **Unit Tests, UI Tests & Emulator Reachability Guard**:
   - `app/src/test/java/com/example/ExampleUnitTest.kt`: Replaced `testArtworkMapper()` with assertions for all M1 verified exact matches (by name and ID), case/whitespace normalization, null/no-fallback cases (custom names, category-only matches, blank strings, and M1-deferred poses including `#50` `"Restorative Child's Pose"`, `#1/#2`, `#54`, `#101`), and an exhaustive loop over all 150 items in `StretchLibrary.ALL_100_STRETCHES` confirming exactly 8 stretches map in M1 and only to the 5 verified M1 drawables.
   - `app/src/test/java/com/example/base/FirestoreEmulatorTestBase.kt`: Kept `FirestoreRoutineRepositoryRuleTest.kt` completely intact; added `isEmulatorReachable(port)` TCP socket check (`127.0.0.1:9099` and `127.0.0.1:8085`, `300ms` timeout) and `Assume.assumeTrue(...)` inside `signInTestUser(...)` (plus `-DrequireFirebaseEmulator=true` strict opt-in).
   - `app/src/test/java/com/example/ArtworkUiNullHandlingTest.kt`: Added 6 Robolectric Compose UI tests exercising `StretchItemCard`, `PlayerArtworkCard` (Light + Dark modes, `IDLE` + `HOLDING`, null + verified artwork), `AddFromLibraryDialog`, and `StretchDetailDialog`.

5. **Build, Lint & Test Execution Results**:
   - Build & Unit Test command:
     ```
     JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew assembleDebug testDebugUnitTest --no-daemon
     ```
     Result: **`BUILD SUCCESSFUL in 25s`** (exit code `0`). Zero Kotlin compiler warnings in any M1-modified file.
   - Verbatim JUnit XML results (`app/build/test-results/testDebugUnitTest/TEST-*.xml`):
     - `com.example.ArtworkUiNullHandlingTest`: `tests="6" skipped="0" failures="0" errors="0"`
     - `com.example.CardLayoutScreenshotTest`: `tests="1" skipped="0" failures="0" errors="0"` (`verify_stretch_card_layout_proportions` passed)
     - `com.example.ExampleRobolectricTest`: `tests="2" skipped="0" failures="0" errors="0"`
     - `com.example.ExampleUnitTest`: `tests="4" skipped="0" failures="0" errors="0"`
     - `com.example.GreetingScreenshotTest`: `tests="1" skipped="0" failures="0" errors="0"`
     - `com.example.data.FirestoreRoutineRepositoryRuleTest`: `tests="3" skipped="2" failures="0" errors="0"` (`unauthenticatedUser_cannotAccessRoutines` passed; `authenticatedUser_canDeleteRoutine` and `authenticatedUser_canSaveAndFetchRoutines` skipped cleanly via `org.junit.AssumptionViolatedException: Firebase Auth/Firestore emulators are not running on 127.0.0.1:9099/8085; skipping emulator test`)
     - **Totals**: **17 tests, 15 passed, 2 skipped (emulator-only), 0 failed, 0 errors**.
   - Lint command:
     ```
     JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew lintDebug --no-daemon
     ```
     Result: **`BUILD SUCCESSFUL in 1m 13s`** (exit code `0`). Zero lint issues on modified Kotlin source files; only `UnusedResources` notices on the 3 intentionally retained unmapped unified reference drawables (`img_stretch_bridge`, `img_stretch_lotus`, `img_stretch_neck`).

---

## 2. Logic Chain

1. Deleting the 15 legacy drawables and removing all substring/category/`else` rules from `ArtworkMapper` guarantees compile-time and runtime impossibility of rendering any legacy or fallback illustration (Observations 1, 2).
2. Typing `getDrawableForStretch(...)` as `@DrawableRes Int?` and gating image `Box` + `Spacer` rendering on `if (customFile != null || artworkRes != null)` in `StretchItemCard`, `AddFromLibraryDialog`, `StretchDetailDialog`, and `PlayerArtworkCard` ensures unmapped stretches omit image containers cleanly without crashes or layout gaps, while preserving `customImagePath` precedence (Observation 3).
3. Adding `Assume.assumeTrue` inside `signInTestUser(...)` in `FirestoreEmulatorTestBase` keeps offline security-rule verification (`unauthenticatedUser_cannotAccessRoutines`) active while skipping only emulator-dependent auth/Firestore tests when ports `9099`/`8085` are closed (Observations 4, 5).
4. Both `ExampleUnitTest.testArtworkMapper()` (exhaustive 150-item invariant check) and `ArtworkUiNullHandlingTest` (6 Compose UI behavioral checks) confirm exact M1 behavior and clean `assembleDebug` + `testDebugUnitTest` + `lintDebug` passage (Observation 5).

---

## 3. Caveats

- Note on count wording in dispatch vs. catalog: The 5 strictly R4-verified drawables cover **8** stretches in `StretchLibrary.ALL_100_STRETCHES` (`#52` `adho_mukha_svanasana_downward_dog`, `#53` `balasana_childs_pose`, `#131` `shashankasana_hare`, `#57` `vrikshasana_tree_right`, `#58` `vrikshasana_tree_left`, `#73` `virabhadrasana_two_right`, `#74` `virabhadrasana_two_left`, `#84` `tadasana_mountain_alignment`), excluding `#50` (`child_pose_full_body` / `"Restorative Child's Pose"`), `#1/#2`, `#54`, and `#101`.
- Emulator-dependent tests in `FirestoreRoutineRepositoryRuleTest` (`authenticatedUser_canSaveAndFetchRoutines`, `authenticatedUser_canDeleteRoutine`) were skipped because Firebase Auth/Firestore emulators are not running on `127.0.0.1:9099/8085` in this host environment; they remain intact and will execute automatically when emulators are started.
- `app/src/main/res/drawable/img_stretch_bridge.jpg`, `img_stretch_lotus.jpg`, and `img_stretch_neck.jpg` remain on disk unmapped (as required reference resources until regenerated in M2/M4/M5), producing expected `UnusedResources` lint notes.

---

## 4. Conclusion

Milestone M1 is **COMPLETE** and verified:
- All 15 legacy mixed-style drawables are removed; all 8 unified studio references + app icon/launcher assets remain on disk.
- `ArtworkMapper` returns `@DrawableRes Int?` via exact normalized lookup across `coreReferenceMap` (5 verified drawables covering `#52`, `#53`, `#131`, `#57/#58`, `#73/#74`, `#84`) and `ArtworkBatch1..4` stubs.
- All 4 UI components cleanly omit image containers when `customFile == null && artworkRes == null`, with Light/Dark contrast fixes in `PlayerArtworkCard`.
- Build, unit/Compose/Roborazzi tests, and `lintDebug` all pass cleanly.

---

## 5. Verification Method

Run from `/Users/hn/antigravity/Stretch Coach`:
```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew assembleDebug testDebugUnitTest --no-daemon
```
And optionally lint:
```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew lintDebug --no-daemon
```
Inspect:
- Remaining drawables: `ls -1 app/src/main/res/drawable/` (expect 11 files, zero legacy names)
- `app/src/main/java/com/example/data/ArtworkMapper.kt` and `app/src/main/java/com/example/data/artwork/ArtworkBatch{1..4}.kt`
- `app/src/main/java/com/example/ui/components/{StretchItemCard,AddFromLibraryDialog,StretchDetailDialog,PlayerArtworkCard}.kt`
- `app/src/test/java/com/example/{ExampleUnitTest,ArtworkUiNullHandlingTest}.kt` and `app/src/test/java/com/example/base/FirestoreEmulatorTestBase.kt`
- Test XMLs: `app/build/test-results/testDebugUnitTest/TEST-*.xml` (17 tests: 15 passed, 2 skipped, 0 failed, 0 errors).

Invalidation conditions: any legacy drawable present, any `ArtworkMapper` return outside the 5 M1 drawables across the 150-item catalog, any fallback image in `PlayerArtworkCard`, or any unit/UI test failure.
