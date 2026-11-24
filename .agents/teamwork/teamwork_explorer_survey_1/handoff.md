# Handoff Report — Explorer 1 (`teamwork_explorer_survey_1`)

**Type:** Hard Handoff  
**Date:** 2026-11-23T14:06:00Z  
**Detailed Report:** `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_survey_1/survey_codebase.md`

---

## 1. Observation

1. **Repository & Gradle configuration:**
   - Single-module Android project (`settings.gradle.kts:27`: `include(":app")`; `app/build.gradle.kts:13-20`: `namespace = "com.example"`, `applicationId = "com.aistudio.stretchcoach.strchq"`, `minSdk = 26`, `targetSdk = 36`).
   - `app/build.gradle.kts:70-72` sets `tasks.withType<Test> { failFast = true }`.
   - `.gitignore` does not exclude `*.html`. `assets/` exists at repo root and is empty.
   - `find_by_name` for `html`/`htm` found 0 user/documentation HTML gallery files (only Gradle/test report HTMLs under `build/` and `app/build/`).
2. **Stretch catalog & drawables:**
   - `app/src/main/java/com/example/data/StretchLibrary.kt:27-1043` defines 100 `LibraryStretch` items + `ExtraYogaAsanas.EXTRA_50_ASANAS` (`app/src/main/java/com/example/data/ExtraYogaAsanas.kt:8-560`, 50 items) = 150 total. All 50 `ExtraYogaAsanas` items have explicit `indianName` (Devanagari); the 100 `StretchLibrary` items have `indianName = null` in source, though `StretchRepository.kt:39-40` populates `LibraryStretchEntity.indianName` via `SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari`.
   - `app/src/main/res/drawable/` contains 23 `img_stretch_*.jpg` files + `img_app_icon.jpg` + 2 launcher XMLs:
     - 8 unified studio reference drawables (`img_stretch_bridge.jpg`, `img_stretch_child.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_lotus.jpg`, `img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_tree.jpg`, `img_stretch_warrior.jpg`): all `1376 x 768` px (~16:9).
     - 15 older mixed-style drawables: 14 are `1024 x 1024` px and `img_stretch_cobra.jpg` is `1408 x 768` px.
3. **Artwork mapping & generation:**
   - `app/src/main/java/com/example/data/ArtworkMapper.kt:6-136`: `@DrawableRes fun getDrawableForStretch(name: String, category: String = ""): Int` maps name/category substrings to the 23 drawables with category fallbacks (`lines 124-131`) and a catch-all `else -> R.drawable.img_stretch_floor` (`line 133`).
   - `app/src/main/java/com/example/data/CustomAsanaImageGenerator.kt:27-103`: generates 16:9 JPEGs at runtime via Firebase AI (`imagen-3.0-generate-002`) for user-created custom stretches only (invoked from `StretchViewModel.kt:494, 535`).
4. **UI rendering sites (verified via `grep_search` for `ArtworkMapper`, `painterResource`, `AsyncImage`, `customImagePath`, `Image(`):**
   - `app/src/main/java/com/example/ui/components/StretchItemCard.kt:83-86, 131-156`: calls `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)`; always renders a `50.dp` `Box` (`AsyncImage` if `customFile != null`, else `Image(painterResource(artworkRes))`) + `Spacer(10.dp)`.
   - `app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt:59-63, 77-233`: calls `ArtworkMapper.getDrawableForStretch(currentStretch.name, currentStretch.category)` with an independent fallback `else { R.drawable.img_stretch_standing }` (`line 62`). Always renders `Box(aspectRatio(16f / 10f))` with `AsyncImage`/`Image`, plus overlaid **top Step/Phase badges** (`lines 117-188`) and **bottom stretch name/notes** (`lines 191-231`) styled in `Color.White` over dark gradient scrims.
   - `app/src/main/java/com/example/ui/components/AddFromLibraryDialog.kt:358-361, 384-409`: calls `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)`; always renders a `48.dp` `Box` + `Spacer(10.dp)`.
   - `app/src/main/java/com/example/ui/components/StretchDetailDialog.kt:178-181, 189-216`: calls `ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)`; always renders `Box(fillMaxWidth().aspectRatio(16f / 10f))` + `Spacer(14.dp)`.
   - `app/src/main/java/com/example/ui/components/HeaderBar.kt:66-73`: renders `R.drawable.img_app_icon` (app logo only, not stretch artwork).
5. **Baseline build & tests:**
   - `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew assembleDebug testDebugUnitTest --no-daemon` exited `1`: `:app:assembleDebug` succeeded, and `:app:testDebugUnitTest` reported `11 tests completed, 1 failed, 1 skipped` (`FirestoreRoutineRepositoryRuleTest > authenticatedUser_canDeleteRoutine` failed with `FirebaseException` because local Firebase Auth/Firestore emulators on `127.0.0.1:9099` and `127.0.0.1:8085` per `firebase.json` and `FirestoreEmulatorTestBase.kt:81-83` are not running; `failFast = true` aborted the remaining emulator test).
   - Running the 4 non-emulator unit/Robolectric/Roborazzi test classes (`ExampleUnitTest`, `ExampleRobolectricTest`, `CardLayoutScreenshotTest`, `GreetingScreenshotTest`) passed `8/8` tests (`BUILD SUCCESSFUL`).
   - `ExampleUnitTest.kt:128-189` (`testArtworkMapper`) asserts 18 sample inputs against current drawables (including many of the 15 older drawables). `CardLayoutScreenshotTest.kt:34-67` captures a Roborazzi screenshot of `StretchItemCard` for `"Bhujangasana (Cobra Pose)"`.

---

## 2. Logic Chain

1. All stretch-to-drawable resolution in the app flows through a single function (`ArtworkMapper.getDrawableForStretch`), except `PlayerArtworkCard.kt:62` which also hardcodes `R.drawable.img_stretch_standing` when `currentStretch == null`. Therefore, removing generic fallbacks and the 15 older drawables requires changing `ArtworkMapper.getDrawableForStretch` to return `Int?` (`null` when unmapped) **and** removing the `else` branch in `PlayerArtworkCard.kt:62`.
2. Because all 4 UI rendering sites unconditionally allocate the image container (`Box` + `Spacer`) and pass `artworkRes` directly to `painterResource(id = artworkRes)`, changing `ArtworkMapper` to return `Int?` requires making all 4 UI sites conditional on `customFile != null || artworkRes != null` and omitting the `Box` and adjacent `Spacer` when neither image exists.
3. Because `PlayerArtworkCard` currently overlays white Step/Phase badges and active stretch title/notes on top of the `16:10` image `Box`, omitting the entire `PlayerArtworkCard` when `artworkRes == null` would hide essential player state (`STEP X OF Y`, `PREPARING`/`HOLDING`/`PAUSED`, stretch name), whereas keeping white text over a light surface without the image/scrim would break contrast. Thus `PlayerArtworkCard` needs a clean image-free compact header branch when `customFile == null && artworkRes == null`.
4. Because `FirestoreRoutineRepositoryRuleTest` requires external Firebase emulators and `app/build.gradle.kts:71` enables `failFast = true`, running `./gradlew testDebugUnitTest` without emulators running will fail on `FirestoreRoutineRepositoryRuleTest` even when all Robolectric/JVM/Roborazzi tests pass.

---

## 3. Caveats

- `androidTest/` (`ExampleInstrumentedTest.kt`) was inspected by reading only; no emulator/device instrumented tests were executed.
- Firebase emulators were not started during this read-only survey, so `FirestoreRoutineRepositoryRuleTest` could not pass in the unfiltered `testDebugUnitTest` invocation.
- Detailed per-stretch pose auditing of the 150 stretches against the 8 reference drawables is outside Explorer 1's codebase-survey scope (covered separately in the survey/audit workflow).

---

## 4. Conclusion

The codebase is cleanly structured around one centralized mapper (`ArtworkMapper.kt`) and four UI rendering composables (`StretchItemCard.kt`, `PlayerArtworkCard.kt`, `AddFromLibraryDialog.kt`, `StretchDetailDialog.kt`). To meet R1–R4:
1. Refactor `ArtworkMapper.getDrawableForStretch` to return `@DrawableRes Int?` (`null` for unverified/unmapped stretches), remove all category/catch-all fallbacks, and replace/remove all 15 older `1024x1024`/`1408x768` drawables.
2. Update all 4 UI composables to omit their image `Box` and `Spacer` when `customFile == null && artworkRes == null`, taking special care in `PlayerArtworkCard.kt` to remove `R.drawable.img_stretch_standing` (`line 62`) and preserve Step/Phase/Title metadata in a compact non-image header.
3. Update `ExampleUnitTest.testArtworkMapper()` (and verify `CardLayoutScreenshotTest` for `Bhujangasana`) to match the new nullable mapper and unified drawables.
4. Place the 150-item HTML visual gallery at repo root (e.g. `stretch_gallery.html`) using relative links to `app/src/main/res/drawable/<name>.jpg`, resolving Indian names from both `ExtraYogaAsanas` (`indianName`) and `StretchLibrary` (`SanskritPhonetics` / parenthetical Sanskrit).

---

## 5. Verification Method

1. Inspect `survey_codebase.md` for line-by-line file inventories and exact code references.
2. Confirm non-emulator baseline tests pass:
   ```bash
   JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew assembleDebug testDebugUnitTest --tests "com.example.ExampleUnitTest" --tests "com.example.ExampleRobolectricTest" --tests "com.example.CardLayoutScreenshotTest" --tests "com.example.GreetingScreenshotTest" --no-daemon
   ```
   (Expected: `BUILD SUCCESSFUL`, 8/8 tests passing.)
3. Note that full unfiltered `testDebugUnitTest` requires Firebase Local Emulator Suite running on `127.0.0.1:8085` (Firestore) and `127.0.0.1:9099` (Auth) due to `FirestoreRoutineRepositoryRuleTest` + `failFast = true`.
