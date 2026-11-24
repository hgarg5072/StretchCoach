# Test Suite Ready — Stretch Coach E2E (Tiers 1–4)

## Status
**READY** — Opaque-box E2E test suites across Tiers 1–4 are implemented, verified, and passing cleanly on Milestone M1. All suites are milestone-agnostic and ready for incremental batch milestones (M2–M5), HTML gallery generation (M6), and Final verification.

---

## Test Artifacts Delivered
1. `TEST_INFRA.md` — Test infrastructure, commands, prerequisites, and milestone-agnosticism contract.
2. `app/src/test/java/com/example/ArtworkE2ETest.kt` — Robolectric/JUnit E2E suite (8 tests covering Tiers 1–4).
3. `app/src/test/java/com/example/UiNoImageE2ETest.kt` — Compose UI Robolectric E2E suite (9 tests covering Tier 3 across all 4 UI artwork sites).
4. `tests/e2e/verify_stretch_coach.py` — Pure Python 3.9+ stdlib E2E verification script (supports `--phase m1` and `--phase final`).

---

## Exact Test Runner Commands

### 1. Android Build + E2E + Unit Test Suite
```bash
JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
ANDROID_HOME="$HOME/Library/Android/sdk" \
./gradlew assembleDebug testDebugUnitTest --no-daemon
```

### 2. Python Static / Asset / Gallery E2E Suite
- **During Milestones M1–M5** (before `stretch_gallery.html` exists):
  ```bash
  python3 tests/e2e/verify_stretch_coach.py --phase m1
  ```
- **After Milestone M6 (Final Verification of all 150 items + `stretch_gallery.html`)**:
  ```bash
  python3 tests/e2e/verify_stretch_coach.py --phase final
  ```

---

## Coverage Summary Across Tiers 1–4

| Tier | Scope | Android / Robolectric (`ArtworkE2ETest` & `UiNoImageE2ETest`) | Python CLI (`verify_stretch_coach.py`) |
|---|---|---|---|
| **Tier 1** | Catalog completeness & drawable hygiene | `tier1_catalogCompletenessAndIntegrity`, `tier1_legacy15DrawablesEliminatedFromResourcesAndMapper`, `tier1_allMappedDrawablesDecodeAsValidUnifiedDimensions` (`1376x768`, 16:9, zero legacy-15) | Parses `StretchLibrary.kt` (100) + `ExtraYogaAsanas.kt` (50) = 150 unique IDs/names; verifies `101` Devanagari names; checks zero legacy-15 or PNG/WEBP files in `res/`; binary JPEG `SOF0` `1376x768` validation |
| **Tier 2** | Mapping safety, no-fallback & anti-collision | `tier2_unknownCustomAndCategoryFallbacksReturnNull`, `tier2_substringCollisionTrapsNeverMapToWrongDrawable`, `tier2_caseAndWhitespaceNormalizationAndCategoryInvariance` | Verifies `Int?` return signature, zero category/catch-all fallbacks in `ArtworkMapper.kt` / `ArtworkBatch1..4.kt`, zero `PlayerArtworkCard` standing fallback, normalized key uniqueness & determinism |
| **Tier 3** | L/R pose symmetry, UI no-image rendering & HTML gallery | `tier3_symmetricalLeftRightPairsMapConsistently` (all 32 L/R pairs); `UiNoImageE2ETest` (9 tests covering `StretchItemCard`, `PlayerArtworkCard` Light/Dark `IDLE`/`PREPARING`/`HOLDING`/`PAUSED`, `AddFromLibraryDialog`, `StretchDetailDialog` for `null`, mapped, and custom `customImagePath` files) | Verifies all 32 L/R pairs match deterministically; in `--phase final`, parses `stretch_gallery.html` for 150 cards in exact `#1–#150` order, 101 Devanagari names, valid relative `<img>` paths (`1376x768`), and explicit `"No verified image assigned"` state on unmapped cards |
| **Tier 4** | Room DB seeding & end-to-end data resolution | `tier4_repositorySeedingResolves150StretchesAnd101DevanagariNames` (in-memory Room `StretchRepository.checkAndSeedDefaults()` verifies 150 `LibraryStretchEntity` rows, 101 Devanagari + 49 `null`, and 5 default routines) | Cross-checks `SanskritPhonetics.SANSKRIT_TO_DEVANAGARI_MAP` resolution across all 150 items (`101` Devanagari, `49` `null`) |

---

## Latest Verified Run Results
- `python3 tests/e2e/verify_stretch_coach.py --phase m1` → **Exit 0**:
  `[PASS] verify_stretch_coach.py --phase m1: 150 catalog items (101 Devanagari), 8 unified 1376x768 JPEGs, 0 legacy drawables, 32 L/R pairs consistent, app mappings verified (8 mapped, 142 unmapped; gallery skipped in m1).`
- `./gradlew assembleDebug testDebugUnitTest --no-daemon` → **Exit 0 (`BUILD SUCCESSFUL in 16s`)**:
  - `ArtworkE2ETest`: 8 passed, 0 failed, 0 skipped
  - `UiNoImageE2ETest`: 9 passed, 0 failed, 0 skipped
  - `ArtworkUiNullHandlingTest`: 6 passed, 0 failed, 0 skipped
  - `ExampleUnitTest`: 4 passed, 0 failed, 0 skipped
  - `ExampleRobolectricTest`: 2 passed, 0 failed, 0 skipped
  - `CardLayoutScreenshotTest`: 1 passed, 0 failed, 0 skipped
  - `GreetingScreenshotTest`: 1 passed, 0 failed, 0 skipped
  - `FirestoreRoutineRepositoryRuleTest`: 1 passed, 2 skipped (`Assume.assumeTrue` when local Firebase emulators are offline), 0 failed
  - **Total**: 34 tests run (32 passed, 2 skipped, 0 failed, 0 errors).
