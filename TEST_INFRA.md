# Test Infrastructure — Stretch Coach

## 1. Test Suites & Locations

### Android / Robolectric E2E & Unit Suites (`app/src/test/java/com/example/`)
- **`ArtworkE2ETest.kt`** (Tiers 1–4):
  - **Tier 1 (Catalog & Drawable Hygiene)**: Verifies the 150-item combined catalog (`100` in `StretchLibrary` + `50` in `ExtraYogaAsanas`), unique IDs/names, valid cues/timings, explicit Devanagari on all 50 `ExtraYogaAsanas`, elimination of all 15 legacy mixed-style drawables from Android resources and `ArtworkMapper`, and unified `1376x768` (16:9) JPEG decoding for every mapped drawable.
  - **Tier 2 (Mapping Safety & Normalization)**: Verifies `ArtworkMapper.getDrawableForStretch(...)` returns `null` for empty/blank/unknown/custom names and never falls back to category defaults; verifies zero substring collision traps across 30+ known collision-prone pose titles; verifies case/whitespace normalization and category invariance.
  - **Tier 3 (L/R Pose Symmetry)**: Verifies all 32 symmetrical Left/Right pairs (22 in `StretchLibrary` + 10 in `ExtraYogaAsanas`) resolve consistently (same drawable or both `null`).
  - **Tier 4 (Room Repository Seeding)**: Uses an in-memory Room `AppDatabase` and `StretchRepository.checkAndSeedDefaults()` to verify 150 `LibraryStretchEntity` rows, 101 non-null Devanagari `indianName` values (`51` runtime-resolved + `50` explicit) and 49 `null` entries, plus clean seeding of the 5 default routines.
- **`UiNoImageE2ETest.kt`** (Tier 3 — Compose UI No-Image & Artwork Rendering):
  - Exercises all 4 UI artwork rendering sites (`StretchItemCard`, `PlayerArtworkCard` in Light/Dark across `IDLE`, `PREPARING`, `HOLDING`, `PAUSED`, `AddFromLibraryDialog`, and `StretchDetailDialog`).
  - Verifies clean omission of the image node (`onNodeWithContentDescription(...).assertDoesNotExist()`) when both `customImagePath` and `ArtworkMapper` return `null`, with zero crashes and preserved badges/text/cues.
  - Verifies image rendering for both verified studio drawables and custom JPEG files (`customImagePath`).
- **Existing Unit, Robolectric & Screenshot Suites**:
  - `ExampleUnitTest.kt`, `ExampleRobolectricTest.kt`, `ArtworkUiNullHandlingTest.kt`, `CardLayoutScreenshotTest.kt`, `GreetingScreenshotTest.kt`, `data/FirestoreRoutineRepositoryRuleTest.kt`.

### Python Static, Asset & Gallery E2E Suite (`tests/e2e/`)
- **`tests/e2e/verify_stretch_coach.py`** (Pure Python 3.9+ stdlib, Tiers 1–3/4):
  - Supports `--phase m1` (before `stretch_gallery.html` exists) and `--phase final` (default; full gallery + app verification).
  - Pure-Python JPEG `SOF0` (`1376x768`) binary validator, Kotlin catalog parser (`100 + 50 = 150`), `SanskritPhonetics` Devanagari resolver (`101` expected non-null Devanagari strings), `ArtworkMapper` + `ArtworkBatch1..4` guardrail & key-consistency parser, 32-pair L/R symmetry check, and `stretch_gallery.html` static DOM + image resolution + `"No verified image assigned"` verification.

---

## 2. Exact Execution Commands

### Android Build + E2E + Unit Tests
Run from the repository root (`/Users/hn/antigravity/Stretch Coach`):

```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew assembleDebug testDebugUnitTest --no-daemon
```

> Note: `FirestoreRoutineRepositoryRuleTest` automatically skips via `Assume.assumeTrue` when local Firebase Auth/Firestore emulators (`127.0.0.1:9099`, `127.0.0.1:8085`) are offline (unless `-DrequireFirebaseEmulator=true` is passed). To run only the non-emulator unit + E2E suites explicitly:
> ```bash
> JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
> ANDROID_HOME="$HOME/Library/Android/sdk" \
> ./gradlew assembleDebug testDebugUnitTest \
>   --tests "com.example.ExampleUnitTest" \
>   --tests "com.example.ExampleRobolectricTest" \
>   --tests "com.example.ArtworkUiNullHandlingTest" \
>   --tests "com.example.CardLayoutScreenshotTest" \
>   --tests "com.example.GreetingScreenshotTest" \
>   --tests "com.example.ArtworkE2ETest" \
>   --tests "com.example.UiNoImageE2ETest" \
>   --no-daemon
> ```

### Python E2E Verification Script
- **During Milestone M1–M5** (before `stretch_gallery.html` is generated in M6):
  ```bash
  python3 tests/e2e/verify_stretch_coach.py --phase m1
  ```
- **After Milestone M6 (Final Verification)**:
  ```bash
  python3 tests/e2e/verify_stretch_coach.py --phase final
  ```

---

## 3. Environment Prerequisites & Constraints
- **JDK**: OpenJDK 21 at `/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home`.
- **Android SDK**: `$HOME/Library/Android/sdk` (`compileSdk = 36`, Robolectric target `sdk = 34`).
- **Gradle Test Options**: `isIncludeAndroidResources = true` and `failFast = true` configured in `app/build.gradle.kts`.
- **Python**: Python 3.9+ standard library only (`re`, `pathlib`, `struct`, `argparse`, `html.parser`, `sys`); no `Pillow` or `pytest` required.
- **Compose Animation Idleness**: `PlayerArtworkCard` runs `rememberInfiniteTransition` in `PREPARING`/`HOLDING` and `StretchItemCard`/`PlayerArtworkCard` apply `basicMarquee(iterations = Int.MAX_VALUE)`; `UiNoImageE2ETest` disables `mainClock.autoAdvance` and advances the clock explicitly before asserting.
- **Firebase Emulators**: Optional for offline runs; required on `127.0.0.1:8085` (Firestore) and `127.0.0.1:9099` (Auth) only when running with `-DrequireFirebaseEmulator=true`.

---

## 4. Milestone Agnosticism Rule
- E2E tests MUST NOT fail simply because an unverified or deferred stretch returns `null` from `ArtworkMapper.getDrawableForStretch(...)` (per Requirements R3 and R4) or because M2–M5 illustration batches have not yet been populated.
- Positive mapping checks verify **structural and safety invariants**: non-legacy drawable ID, valid `1376x768` unified JPEG asset, case/whitespace normalization, category invariance, L/R pair symmetry, and absence of substring collision traps.
