# Challenger 1 Handoff Report — Iteration 1 (M1 + E2E Test Suite)

**Verdict**: `REQUEST_CHANGES`
**Overall Risk Assessment**: MEDIUM (M1 ArtworkMapper, drawables, and E2E/unit suites are solid; 1 empirically confirmed test-infrastructure bug in Gradle `-DrequireFirebaseEmulator=true` propagation).

---

## 1. Observation

### 1.1 `ArtworkMapper.kt`, `app/src/main/res/drawable/`, and `verify_stretch_coach.py --phase m1`
- Inspected `app/src/main/java/com/example/data/ArtworkMapper.kt` (lines 1–57) and `app/src/main/java/com/example/data/artwork/ArtworkBatch1..4.kt`:
  - `getDrawableForStretch(name: String, category: String = ""): Int?` normalizes via `raw.trim().lowercase().replace(Regex("\\s+"), " ")`, returns `null` when blank, and checks `coreReferenceMap[key]` followed by `ArtworkBatch1..4.exactNameMap[key]`. Zero category or catch-all fallbacks exist.
  - `coreReferenceMap` contains 16 exact keys (8 stretch names + 8 stretch IDs) mapping to 5 drawables (`img_stretch_downward_dog`, `img_stretch_child`, `img_stretch_tree`, `img_stretch_warrior`, `img_stretch_standing`), covering 8 catalog stretches (`#52`, `#53`, `#57`, `#58`, `#73`, `#74`, `#84`, `#131`).
- Listed `app/src/main/res/drawable/`: contains exactly 11 files (`ic_launcher_background.xml`, `ic_launcher_foreground.xml`, `img_app_icon.jpg`, and 8 `img_stretch_*.jpg` files). All 15 legacy mixed-style drawables are absent, and grep across `app/src/main` confirmed zero references to the 15 legacy names and zero `R.drawable.img_stretch_standing` fallback in `PlayerArtworkCard.kt`.
- Ran `python3 tests/e2e/verify_stretch_coach.py --phase m1`:
  - Exit code `0`:
  - `[PASS] verify_stretch_coach.py --phase m1: 150 catalog items (101 Devanagari), 8 unified 1376x768 JPEGs, 0 legacy drawables, 32 L/R pairs consistent, app mappings verified (8 mapped, 142 unmapped; gallery skipped in m1).`
- Ran `sips -g pixelWidth -g pixelHeight -g format app/src/main/res/drawable/*.jpg`:
  - All 8 `img_stretch_*.jpg` files (`bridge`, `child`, `downward_dog`, `lotus`, `neck`, `standing`, `tree`, `warrior`) reported `pixelWidth: 1376`, `pixelHeight: 768`, `format: jpeg`. (`img_app_icon.jpg` is `1024x1024` jpeg.)
- Visually inspected all 8 `img_stretch_*.jpg` files against `StretchLibrary.kt` / `ExtraYogaAsanas.kt` cues and Unified Studio Art Style:
  - The 5 mapped drawables (`downward_dog`, `child`, `tree`, `warrior`, `standing`) accurately depict `#52`, `#53`/`#131`, `#57`/`#58`, `#73`/`#74`, and `#84` in the unified studio style.
  - The 3 excluded drawables (`neck` hand-on-head vs hands-free cue, `bridge` flat palms vs interlaced fingers cue, `lotus` cross-legged vs full lotus feet-on-thighs cue) are properly excluded from M1 mapping (`null`).
- Executed an inline Python adversarial stress script against all 150 catalog entries and 23 edge-case inputs:
  - Confirmed 8 mapped / 142 `null` across all 150 items with 100% name-vs-ID agreement.
  - Confirmed whitespace/tab/newline/case variations (`"  \t\n  Balasana   (Child's   Pose) \n\t "`, `"ADHO MUKHA   SVANASANA (DOWNWARD-FACING DOG)"`) resolve accurately, while partial/near-miss/category-trap/curly-apostrophe inputs (`"Balasana"`, `"Child's Pose"`, `"Balasana (Child’s Pose)"`, `"Adho Mukha Svanasana (Downward Facing Dog)"`, `"Neck Side Stretch (Right)"`, `"Yoga Asanas"`) return `null`.

### 1.2 Unit & E2E Gradle Test Runs
- Ran:
  ```bash
  JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" ANDROID_HOME="$HOME/Library/Android/sdk" ./gradlew :app:testDebugUnitTest --rerun --tests "com.example.ArtworkE2ETest" --tests "com.example.UiNoImageE2ETest" --tests "com.example.ExampleUnitTest" --no-daemon
  ```
- Result: Exit code `0`, `BUILD SUCCESSFUL in 9s`.
- Inspected fresh XML reports in `app/build/test-results/testDebugUnitTest/`:
  - `TEST-com.example.ArtworkE2ETest.xml`: `tests="8" skipped="0" failures="0" errors="0"`
  - `TEST-com.example.UiNoImageE2ETest.xml`: `tests="9" skipped="0" failures="0" errors="0"`
  - `TEST-com.example.ExampleUnitTest.xml`: `tests="4" skipped="0" failures="0" errors="0"`

### 1.3 Adversarial Challenge of `FirestoreEmulatorTestBase.kt` `Assume.assumeTrue` Guard
- Verified `lsof -i :9099 -i :8085`: `Ports 9099/8085 closed`.
- Inspected `app/src/test/java/com/example/base/FirestoreEmulatorTestBase.kt` (lines 72–102) and `app/src/test/java/com/example/data/FirestoreRoutineRepositoryRuleTest.kt` (lines 13–83):
  - `unauthenticatedUser_cannotAccessRoutines` does NOT call `signInTestUser()`, so it bypasses `Assume.assumeTrue`.
  - `signInTestUser()` checks `java.lang.Boolean.getBoolean("requireFirebaseEmulator")` (line 83); if `false`, calls `Assume.assumeTrue(..., emulatorsAvailable)` (lines 88–91).
- Inspected `TEST_INFRA.md` (lines 36 & 69): explicitly documents that passing `-DrequireFirebaseEmulator=true` to `./gradlew` makes `FirestoreRoutineRepositoryRuleTest` require the emulators instead of skipping.
- Inspected `app/build.gradle.kts` (lines 70–72):
  ```kotlin
  tasks.withType<Test> {
    failFast = true
  }
  ```
  It does **not** forward `-DrequireFirebaseEmulator` to forked `Test` worker JVMs.
- **Empirical Reproduction**:
  1. Ran offline (default flags):
     `./gradlew :app:testDebugUnitTest --rerun --tests "com.example.data.FirestoreRoutineRepositoryRuleTest" --no-daemon`
     - Exit code `0`. XML `TEST-com.example.data.FirestoreRoutineRepositoryRuleTest.xml`: `tests="3" skipped="2" failures="0" errors="0"`.
     - `unauthenticatedUser_cannotAccessRoutines` executed and passed (`time="0.018"`, no `<skipped>` element).
     - `authenticatedUser_canDeleteRoutine` and `authenticatedUser_canSaveAndFetchRoutines` skipped via `AssumptionViolatedException` at `FirestoreEmulatorTestBase.kt:88`.
  2. Ran offline **with `-DrequireFirebaseEmulator=true`** (as documented in `TEST_INFRA.md`):
     `./gradlew :app:testDebugUnitTest --rerun --tests "com.example.data.FirestoreRoutineRepositoryRuleTest" -DrequireFirebaseEmulator=true --no-daemon`
     - **Exit code `0` (`BUILD SUCCESSFUL in 5s`)!**
     - Fresh XML (`timestamp="2026-11-23T14:36:52.646Z"`): STILL `tests="3" skipped="2" failures="0" errors="0"` — both authenticated emulator tests **silently skipped** via `Assume.assumeTrue` at `FirestoreEmulatorTestBase.kt:88` despite `-DrequireFirebaseEmulator=true`!
  3. Ran offline with `JAVA_TOOL_OPTIONS="-DrequireFirebaseEmulator=true"` to confirm root cause:
     `JAVA_TOOL_OPTIONS="-DrequireFirebaseEmulator=true" ./gradlew :app:testDebugUnitTest --rerun --tests "com.example.data.FirestoreRoutineRepositoryRuleTest" --no-daemon`
     - **Exit code `1`**: `FirestoreRoutineRepositoryRuleTest > authenticatedUser_canDeleteRoutine FAILED: java.lang.IllegalStateException at FirestoreEmulatorTestBase.kt:84`.
  4. Re-ran `./gradlew :app:testDebugUnitTest --rerun --tests "com.example.data.FirestoreRoutineRepositoryRuleTest" --no-daemon` afterward to leave `app/build/test-results/` in a clean passing state.

---

## 2. Logic Chain

1. Observation 1.1 and 1.2 prove M1's core artwork refactor (`ArtworkMapper.kt`, `ArtworkBatch1..4.kt`, 4 null-safe UI composables, legacy-15 removal, 5 verified unified drawables) and the Python/Robolectric E2E suites (`verify_stretch_coach.py --phase m1`, `ArtworkE2ETest`, `UiNoImageE2ETest`, `ExampleUnitTest`) are sound and pass all empirical checks.
2. Observation 1.3 proves `unauthenticatedUser_cannotAccessRoutines` executes and passes cleanly offline without being skipped.
3. However, Observation 1.3 step (2) & (3) prove a **silent fail-open bug** in the `Assume.assumeTrue` opt-out mechanism:
   - Gradle executes `testDebugUnitTest` inside a forked `Gradle Test Executor` JVM (`GradleWorkerMain`).
   - `app/build.gradle.kts` (`tasks.withType<Test>`) does not propagate `System.getProperty("requireFirebaseEmulator")` to the test worker JVM via `systemProperty(...)`.
   - Therefore, `./gradlew testDebugUnitTest -DrequireFirebaseEmulator=true` (the exact flag documented in `TEST_INFRA.md:36,69` and cited in the verification scope) silently leaves `java.lang.Boolean.getBoolean("requireFirebaseEmulator") == false` in the test worker, causing `Assume.assumeTrue` to skip emulator tests and report `BUILD SUCCESSFUL` even when the caller explicitly required emulators.

---

## 3. Caveats

- Firebase Auth (`9099`) and Firestore (`8085`) emulators were offline during this verification run, so positive execution of `authenticatedUser_canSaveAndFetchRoutines` and `authenticatedUser_canDeleteRoutine` against live local emulators was not tested.
- Note that during our initial Gradle invocation (`task-49`), a concurrent `./gradlew` run by another workspace agent collided on `app/build/test-results/testDebugUnitTest/binary/`; all subsequent runs documented above were executed sequentially with zero concurrent Gradle processes (`ps aux` verified) and `--rerun`.

---

## 4. Conclusion

**Verdict: `REQUEST_CHANGES`**

### What Passes
- `ArtworkMapper.kt`, `ArtworkBatch1..4.kt`, `app/src/main/res/drawable/`, `verify_stretch_coach.py --phase m1`, `ArtworkE2ETest` (8/8), `UiNoImageE2ETest` (9/9), and `ExampleUnitTest` (4/4) all pass empirical verification.
- `FirestoreRoutineRepositoryRuleTest.unauthenticatedUser_cannotAccessRoutines` executes and passes offline (not skipped).

### Required Fix Before Approval
- **[Medium/High] Forward `requireFirebaseEmulator` system property to forked test JVMs in `app/build.gradle.kts`**:
  In `app/build.gradle.kts` (`tasks.withType<Test>`, lines 70–72), add:
  ```kotlin
  tasks.withType<Test> {
    failFast = true
    System.getProperty("requireFirebaseEmulator")?.let {
      systemProperty("requireFirebaseEmulator", it)
    }
  }
  ```
  (Alternatively, also check `project.findProperty("requireFirebaseEmulator")` if `-P` support is desired.) This ensures `./gradlew testDebugUnitTest -DrequireFirebaseEmulator=true` reliably disables the `Assume.assumeTrue` skip guard and fails loudly when ports `9099`/`8085` are unreachable.

---

## 5. Verification Method

After applying the `app/build.gradle.kts` fix, independently verify with ports `9099`/`8085` closed:
1. Confirm `-DrequireFirebaseEmulator=true` now **fails** (does NOT silently skip):
   ```bash
   JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
   ANDROID_HOME="$HOME/Library/Android/sdk" \
   ./gradlew :app:testDebugUnitTest --rerun --tests "com.example.data.FirestoreRoutineRepositoryRuleTest" -DrequireFirebaseEmulator=true --no-daemon
   ```
   **Invalidation condition**: Must exit non-zero with `IllegalStateException: Firebase Auth/Firestore emulators are required on 127.0.0.1:9099/8085`. If it exits 0 or skips, the fix failed.
2. Confirm offline default (without `-DrequireFirebaseEmulator=true`) skips only the 2 authenticated tests and runs+passes `unauthenticatedUser_cannotAccessRoutines`:
   ```bash
   JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
   ANDROID_HOME="$HOME/Library/Android/sdk" \
   ./gradlew :app:testDebugUnitTest --rerun --tests "com.example.data.FirestoreRoutineRepositoryRuleTest" --no-daemon
   ```
   Inspect `app/build/test-results/testDebugUnitTest/TEST-com.example.data.FirestoreRoutineRepositoryRuleTest.xml`: `tests="3" skipped="2" failures="0" errors="0"` and `<testcase name="unauthenticatedUser_cannotAccessRoutines" .../>` has no `<skipped>` child.
3. Re-run Python & Gradle suites:
   ```bash
   python3 tests/e2e/verify_stretch_coach.py --phase m1
   JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
   ANDROID_HOME="$HOME/Library/Android/sdk" \
   ./gradlew assembleDebug testDebugUnitTest --no-daemon
   ```
