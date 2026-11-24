## 2026-11-23T14:11:14Z
[Message] timestamp=2026-11-23T14:11:14Z sender=11858d5d-e7ac-4d45-9f42-d5f95d0ae4b0 priority=MESSAGE_PRIORITY_HIGH content=You are Explorer 1 (`teamwork_explorer_m1_1`) for Milestone M1 (Core Artwork Architecture, Legacy Cleanup & Test Setup) of Stretch Coach.
Your working directory: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_m1_1`
Project root: `/Users/hn/antigravity/Stretch Coach`

MANDATORY FIRST STEP: Read `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/ORIGINAL_REQUEST.md` and `/Users/hn/antigravity/Stretch Coach/PROJECT.md` completely.

Your Investigation Scope (Read-Only — do NOT modify source files):
1. Inspect `app/src/main/java/com/example/data/ArtworkMapper.kt`, `StretchLibrary.kt`, `ExtraYogaAsanas.kt`, `ExampleUnitTest.kt`, `CardLayoutScreenshotTest.kt`, `app/src/test/screenshots/`, `app/build.gradle.kts`, `FirestoreRoutineRepositoryRuleTest.kt`, and `FirestoreEmulatorTestBase.kt`.
2. Check how `./gradlew assembleDebug testDebugUnitTest --no-daemon` handles `FirestoreRoutineRepositoryRuleTest`:
   - Is `firebase` CLI installed (`which firebase`)? What happens if emulators are not running? Look at `FirestoreEmulatorTestBase.kt` and `app/build.gradle.kts` (`failFast = true`) to explain why it failed in Survey 1 and what clean, non-cheating options exist (do NOT suggest deleting `FirestoreRoutineRepositoryRuleTest`; check whether `Assume` / emulator availability check or running `firebase emulators:exec` works).
3. Check `CardLayoutScreenshotTest.kt`: does it verify against a checked-in golden (`stretch_card_pixel8.png`) or only capture? If `Bhujangasana (Cobra Pose)` changes image or becomes temporarily `null` in M1 until M2 generates Cobra, how does `CardLayoutScreenshotTest` behave?
4. Design the exact specification for:
   - Deleting the 15 older mixed-style drawables.
   - Refactoring `ArtworkMapper.kt` (`@DrawableRes fun getDrawableForStretch(name: String, category: String = ""): Int?`) using exact normalized name/alias lookup (zero substring fallbacks, zero category fallbacks, zero catch-all `else`), delegating to `coreReferenceMap` + `ArtworkBatch1..4` in `com.example.data.artwork`.
   - Exact audit of the 8 existing unified drawables (`img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_warrior.jpg`, `img_stretch_lotus.jpg`, `img_stretch_tree.jpg`, `img_stretch_bridge.jpg`, `img_stretch_child.jpg`) vs their assigned stretches in `PROJECT.md`. Check the exact cue text of `#1/#2` and `#54` (`setu_bandhasana`) to advise whether `img_stretch_neck` and `img_stretch_bridge` pass strict R4 cue verification or should be regenerated in M2/M4.
   - Updating `ExampleUnitTest.testArtworkMapper()`.

Output Requirements:
- Maintain `progress.md` (`Last visited: <UTC>`).
- Write `m1_backend_plan.md` and `handoff.md` in `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_m1_1/`.
- Send concise summary + paths to parent (`11858d5d-e7ac-4d45-9f42-d5f95d0ae4b0`) via `send_message`.
