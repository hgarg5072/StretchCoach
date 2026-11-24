# Handoff Report — Iteration 2 M4 Pose Prompt Specification (`teamwork_explorer_i2_2`)

**Handoff Type**: Hard  
**Deliverable**: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_2/pose_prompts_m4.md`

---

## 1. Observation

1. **M4 Scope in `PROJECT.md` & `StretchLibrary.kt`**:
   - `PROJECT.md:113–159` assigns **25 canonical poses** (17 standalone + 8 L/R pairs = **33 stretches**) to Milestone `M4`:
     `#54`, `#55/#56`, `#59`, `#60`, `#64`, `#65/#66`, `#67/#68`, `#69`, `#71/#72`, `#75/#76`, `#77`, `#78`, `#79`, `#80/#81`, `#82`, `#83`, `#86/#87`, `#88`, `#89`, `#90`, `#93`, `#96/#97`, `#98`, `#99`, `#100`.
   - Across `PROJECT.md:60–209`, milestone stretch counts sum to `M1(8) + M2(30) + M3(31) + M4(33) + M5(48) = 150`. (`PROJECT.md:217` prose says `(covering 35 stretches)`, which is a prose count mismatch against the 33 `M4` rows in the authoritative catalog table.)
   - `StretchLibrary.kt:573–1042` provides the verbatim `id`, `name`, `prepCue`, and `holdCue` for all 33 stretches.
2. **Lookup & Test Contract (`ArtworkMapper.kt` & `ExampleUnitTest.kt`)**:
   - `ArtworkMapper.kt:13–14` normalizes keys via `raw.trim().lowercase().replace(Regex("\\s+"), " ")` and checks `ArtworkBatch3.exactNameMap[key]` (`ArtworkMapper.kt:52`) with no substring fallback.
   - `ExampleUnitTest.kt:214–216` enforces `ArtworkMapper.getDrawableForStretch(stretch.name, ...) == ArtworkMapper.getDrawableForStretch(stretch.id, ...)`, requiring both normalized `name` and `id` keys (66 keys total for 33 stretches) in `ArtworkBatch3.kt`.
3. **Reference Drawables (`app/src/main/res/drawable/img_stretch_*.jpg`)**:
   - Direct `list_dir` and `view_file` inspection confirmed 8 unified-style `1376x768` JPEGs in two consistent studio variants (`drawables_audit.md` §2.1):
     - **Variant A (window left, floating shelf, fringed jute mat)**: `img_stretch_standing.jpg`, `img_stretch_warrior.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_neck.jpg`.
     - **Variant B (window right, clean back wall, unfringed jute mat)**: `img_stretch_bridge.jpg`, `img_stretch_child.jpg`, `img_stretch_lotus.jpg`, `img_stretch_tree.jpg`.
4. **Special Cue & R4 Requirements Observed**:
   - `#54` (`StretchLibrary.kt:579–580`): `"Drive through heels to lift hips skyward. Roll shoulders under, interlace fingers, and expand chest."` — `img_stretch_bridge.jpg` shows arms flat separately beside the torso, so M4-01 (`img_stretch_b3_setu_bandhasana_interlaced.jpg`) explicitly requires rolled-under shoulders and interlaced fingers on the mat beneath elevated hips.
   - `#67/#68` (`StretchLibrary.kt:709–720`): `Gomukhasana (Cow Face Arms)` requires clasping fingertips behind the spine between shoulder blades — a rear 3/4 view is required so the clasped hands are visibly verifiable.
   - `#82` (`StretchLibrary.kt:859–860`): `Viparita Karani` requires legs extended vertically straight up against a wall, arms at sides with palms upward — prompt places the mat perpendicular to the left cream-plaster studio wall with hips at the baseboard.
   - `#99` (`StretchLibrary.kt:1029–1030`) vs `#100` (`StretchLibrary.kt:1039–1040`): Cat Pose (`marjaryasana_cat_pose`) requires a single-scene convex rounded spine and dropped head, whereas Cow Pose (`bitilasana_cow_pose`) requires a single-scene concave dropped belly, lifted chest/sit bones, and forward gaze.
5. **Programmatic Verification of `pose_prompts_m4.md`**:
   - Verified via Python script: `25` canonical `drawable_filename` entries matching `^img_stretch_b3_[a-z0-9_]+\.jpg$` and `^[a-z][a-z0-9_]*\.jpg$`; `25` matching `ImageName` values; `50` `ImagePaths` (2 per pose) all pointing to existing files in `app/src/main/res/drawable/`; `25` prompts all containing mandatory Unified Studio Art Style tokens (`low bun`, `sage-olive`, `charcoal-grey`, `barefoot`, `jute yoga mat`, `honey-oak`, `cream-plaster`, `16:9`, `zero text`, `zero split-panels`); and `66` unique normalized keys matching `StretchLibrary.kt` `name` and `id` for all 33 M4 stretches.

---

## 2. Logic Chain

1. From Observation 1 & 2, M4 must cover all 25 canonical poses (33 stretches) in `PROJECT.md:113–159`, registering both `normalizeKey(name)` and `id` (66 keys) in `ArtworkBatch3.exactNameMap` so `ExampleUnitTest` name/id parity holds.
2. From Observation 3, pairing 2 `ImagePaths` from the **same** studio variant (Variant A or Variant B) prevents conflicting window/shelf layouts during `generate_image` synthesis while matching the pose plane (e.g., `bridge` + `child` for supine/prone/kneeling horizontal poses; `warrior` + `standing` for wide-stance/standing Variant A poses; `tree` + `lotus` for front-facing balance).
3. From Observation 4, camera angles and explicit anatomical overrides were tailored to each pose's `prepCue`/`holdCue` so an independent R4 visual judge can verify every cue (interlaced fingers under hips for `#54`, rear 3/4 view for `#67/#68`, cream wall behind legs for `#82`, single-pose flexion vs extension for `#99` vs `#100`, 3/4 supine views for `#64`/`#83`/`#88`/`#96`).
4. Observation 5 confirms 100% syntactic, filename, reference-file, and `StretchLibrary.kt` key completeness in `pose_prompts_m4.md`.

---

## 3. Caveats

- **`PROJECT.md:217` Stretch Count Discrepancy**: `PROJECT.md:217` and the dispatch prompt state `"25 canonical poses (covering 35 stretches)"`, whereas the authoritative 150-stretch table in `PROJECT.md:60–209` assigns **33 stretches** (`25` canonical poses) to M4 (`8+30+31+33+48 = 150`). `pose_prompts_m4.md` covers all 25 canonical poses and all 33 M4 stretches (66 keys) from the catalog table.
- **No Images Generated (Read-Only Scope)**: Prompts are designed to maximize first-pass anatomical fidelity, but generated outputs from `generate_image` must still undergo visual inspection against the per-pose verification checklist in `pose_prompts_m4.md` (and post-processed to baseline `1376x768` `.jpg` in `app/src/main/res/drawable/`).
- **L/R Symmetry Convention**: The 8 paired M4 poses (`#55/#56`, `#65/#66`, `#67/#68`, `#71/#72`, `#75/#76`, `#80/#81`, `#86/#87`, `#96/#97`) share one canonical pose image per R2 (`ORIGINAL_REQUEST.md:24`) and depict the Right-side setup cleanly while mapping both Right and Left keys.

---

## 4. Conclusion

All 25 canonical pose specifications (covering 33 stretches / 66 normalized keys) for Milestone M4 (`ArtworkBatch3.kt`, `img_stretch_b3_<key>.jpg`) are complete, cue-faithful, style-locked to the 8 unified studio references, and ready for the M4 generation worker in:
- `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_2/pose_prompts_m4.md`

---

## 5. Verification Method

1. Inspect `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_2/pose_prompts_m4.md` directly.
2. Re-run the read-only Python check verifying 25 drawables, 50 existing `ImagePaths`, 25 prompts, and 66 normalized keys against `app/src/main/java/com/example/data/StretchLibrary.kt` and `PROJECT.md`:
   - Invalidation condition: any missing M4 ID from `#54–#100`, any duplicate key, any non-existent `ImagePaths` file, or any `drawable_filename` failing `^img_stretch_b3_[a-z0-9_]+\.jpg$`.
3. After M4 image generation and `ArtworkBatch3.kt` implementation, run `./gradlew assembleDebug testDebugUnitTest --no-daemon` (with `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"` and `ANDROID_HOME="$HOME/Library/Android/sdk"`).
