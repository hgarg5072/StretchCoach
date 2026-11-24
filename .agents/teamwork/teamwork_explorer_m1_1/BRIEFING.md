# BRIEFING — 2026-11-23T14:15:00Z

## Mission
Investigate M1 backend architecture, legacy drawable cleanup, 8 existing unified drawable cue/visual verification, `FirestoreRoutineRepositoryRuleTest` Gradle behavior, and `CardLayoutScreenshotTest` behavior, and produce `m1_backend_plan.md` + `handoff.md`.

## 🔒 My Identity
- Archetype: Explorer (`teamwork_explorer_m1_1`)
- Roles: Read-only Investigator, Synthesizer
- Working directory: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_m1_1`
- Original parent: `11858d5d-e7ac-4d45-9f42-d5f95d0ae4b0`
- Milestone: M1 (Core Artwork Architecture, Legacy Cleanup & Test Setup)

## 🔒 Key Constraints
- Read-only investigation — do NOT modify source files
- Only write metadata/reports to `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_m1_1/`
- Check `ORIGINAL_REQUEST.md` and `PROJECT.md` requirements (R1-R4, code layout, disjoint ownership)
- Do NOT suggest deleting `FirestoreRoutineRepositoryRuleTest`

## Current Parent
- Conversation ID: `11858d5d-e7ac-4d45-9f42-d5f95d0ae4b0`
- Updated: 2026-11-23T14:15:00Z

## Investigation State
- **Explored paths**: `ORIGINAL_REQUEST.md`, `PROJECT.md`, `ArtworkMapper.kt`, `StretchLibrary.kt`, `ExtraYogaAsanas.kt`, `AppDatabase.kt`, `StretchRepository.kt`, `ExampleUnitTest.kt`, `CardLayoutScreenshotTest.kt`, `GreetingScreenshotTest.kt`, `ExampleRobolectricTest.kt`, `FirestoreRoutineRepositoryRuleTest.kt`, `FirestoreEmulatorTestBase.kt`, `FirestoreRoutineRepository.kt`, `app/build.gradle.kts`, `firebase.json`, `app/src/test/screenshots/`, all 8 unified `.jpg` drawables, sample legacy `.jpg` drawables, `StretchItemCard.kt`, `PlayerArtworkCard.kt`, `AddFromLibraryDialog.kt`, `StretchDetailDialog.kt`.
- **Key findings**:
  1. `firebase` CLI and `node`/`npx` are not installed; ports 8085/9099 are closed. `FirestoreRoutineRepositoryRuleTest` fails with `Connection refused` on `signInTestUser` (`FirestoreEmulatorTestBase.kt:72`), and `failFast = true` (`app/build.gradle.kts:71`) aborts the suite. Clean non-cheating fix: JUnit `Assume.assumeTrue` TCP reachability check on `127.0.0.1:8085` & `9099` in `FirestoreEmulatorTestBase.signInTestUser()` (with optional `-DrequireFirebaseEmulator=true` override).
  2. `app/src/test/screenshots/` only contains `greeting.png` (`stretch_card_pixel8.png` is NOT checked in). `CardLayoutScreenshotTest` runs `captureRoboImage` without golden verification in `testDebugUnitTest` and passes cleanly; once `StretchItemCard` handles `artworkRes == null`, Cobra being temporarily `null` in M1 will not break `CardLayoutScreenshotTest`.
  3. 15 legacy drawables verified (14 are `1024x1024`, `img_stretch_cobra.jpg` is `1408x768`, only referenced in `ArtworkMapper.kt` and `ExampleUnitTest.kt`).
  4. Visual + R4 cue audit of 8 unified drawables (`1376x768`): 5 pass strictly (`standing`, `downward_dog`, `warrior`, `tree`, `child`); `img_stretch_bridge.jpg` FAILS `#54` (`holdCue` requires rolled-under shoulders + interlaced fingers, image has flat palms); `img_stretch_neck.jpg` FAILS strict `#1/#2` (`holdCue` says `"Let gravity do the work"`, image has hand on head); `img_stretch_lotus.jpg` is borderline (`#101` says *each* foot on opposite thigh, image shows only one foot atop thigh with other tucked under).
- **Unexplored areas**: None within M1 backend scope.

## Key Decisions Made
- Recommend mapping only the 5 strictly verified unified drawables in M1 `coreReferenceMap` (`img_stretch_standing`, `img_stretch_downward_dog`, `img_stretch_warrior`, `img_stretch_tree`, `img_stretch_child`), leaving `#1/#2` for M2 regeneration, `#54` for M4 regeneration, and `#101` for M5 regeneration (while keeping all 8 unified `.jpg` files on disk as style references).
- Recommend JUnit `Assume` emulator-availability guard in `FirestoreEmulatorTestBase` instead of `firebase emulators:exec` (unavailable without `firebase`/`node`) or deleting tests.

## Artifact Index
- `DISPATCH.md` — Logged incoming task dispatch
- `BRIEFING.md` — Persistent working memory
- `progress.md` — Heartbeat log
- `m1_backend_plan.md` — Detailed M1 backend & test implementation specification (next)
- `handoff.md` — 5-component handoff report (next)
