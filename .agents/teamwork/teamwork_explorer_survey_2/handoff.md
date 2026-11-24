# Handoff Report — Explorer 2 (`teamwork_explorer_survey_2`)

**Type**: Hard  
**Deliverable**: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_survey_2/catalog_150.md`

---

## 1. Observation

1. **Catalog Sources & Counts**
   - `app/src/main/java/com/example/data/StretchLibrary.kt:27-1043` defines `StretchLibrary.ALL_100_STRETCHES` containing **100** `LibraryStretch` items (`#1` `neck_side_right` through `#100` `bitilasana_cow_pose`), concatenated with `+ ExtraYogaAsanas.EXTRA_50_ASANAS` at line `1043`.
   - `app/src/main/java/com/example/data/ExtraYogaAsanas.kt:8-559` defines `ExtraYogaAsanas.EXTRA_50_ASANAS` containing **50** `LibraryStretch` items (`#101` `padmasana_lotus` through `#150` `ujjayi_pranayama`).
   - Total catalog size = **100 + 50 = 150** stretches.

2. **Data Models & `indianName` Field Behavior**
   - `LibraryStretch` (`StretchLibrary.kt:3-13`) has `id`, `name`, `category`, `defaultPrepSeconds`, `defaultHoldSeconds`, `prepCue`, `holdCue`, `benefits`, and `indianName: String? = null`.
   - `LibraryStretchEntity` (`app/src/main/java/com/example/data/LibraryStretchEntity.kt:8-42`) stores `indianName: String? = null` and `customImagePath: String? = null`.
   - `StretchEntity` (`app/src/main/java/com/example/data/StretchEntity.kt:24-51`) stores routine-level `libraryId`, `name`, `indianName`, `customImagePath`, `category`, `prepCue`, `holdCue`, `notes`.
   - In `StretchLibrary.kt` (`#1–100`), **0 of 100** items explicitly set `indianName` (all default to `null`), though `#35` (`Seated Single-Leg Fold (Janu Sirsasana)`) and `#51–100` embed Sanskrit names in `name`.
   - In `ExtraYogaAsanas.kt` (`#101–150`), **50 of 50** items explicitly set `indianName` in Devanagari script (e.g., `"पद्मासन"`).
   - At runtime, `StretchRepository.checkAndSeedDefaults()` (`app/src/main/java/com/example/data/StretchRepository.kt:39-40`) populates `LibraryStretchEntity.indianName` via:
     `val resolvedIndianName = lib.indianName ?: com.example.audio.SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari`
     which resolves Devanagari for `#35` and `#51–100` (51 items in `StretchLibrary`), leaving `#1–34` and `#36–50` as `null` (49 items).

3. **Current Artwork Mapping (`ArtworkMapper.kt`)**
   - `ArtworkMapper.getDrawableForStretch(name, category)` (`app/src/main/java/com/example/data/ArtworkMapper.kt:8-135`) returns `@DrawableRes Int` across 23 drawables (never `null`) and ends with a generic category fallback (`c.contains("yoga") -> img_stretch_standing`, etc.) plus `else -> R.drawable.img_stretch_floor` (`ArtworkMapper.kt:124-133`).
   - Across the 150 stretches, `ArtworkMapper` currently maps:
     - **61 stretches** to the 8 unified-style reference drawables (`img_stretch_standing`: 17, `img_stretch_lotus`: 12, `img_stretch_warrior`: 10, `img_stretch_child`: 5, `img_stretch_neck`: 4, `img_stretch_tree`: 4, `img_stretch_bridge`: 3, `img_stretch_downward_dog`: 1) — with massive over-broad false positives (e.g., Headstand `#108`, Crow `#112`, Peacock `#113`, Splits `#122–123`, Side Plank `#133–134`, Plank `#146` all fall back to `img_stretch_standing` via `c.contains("yoga")`; `Chaturanga Dandasana` `#114` matches `"dandasana"` -> `img_stretch_lotus`; `Natarajasana` `#124–125` matches `img_stretch_tree`; `Chakrasana` `#106` & `Purvottanasana` `#141` match `img_stretch_bridge`; `Puppy Pose` `#12` & `Child's Pose with Lat Reach` `#11` match `img_stretch_child`).
     - **89 stretches** to the 15 legacy mixed-style drawables (`img_stretch_hamstring`: 15, `img_stretch_twist`: 11, `img_stretch_floor`: 9, `img_stretch_upper`: 9, `img_stretch_lunge`: 8, `img_stretch_butterfly`: 7, `img_stretch_side_bend`: 6, `img_stretch_calf`: 5, `img_stretch_chest`: 5, `img_stretch_cobra`: 4, `img_stretch_pigeon`: 4, `img_stretch_cat_cow`: 3, `img_stretch_figure_four`: 3, `img_stretch_quad`: 3, `img_stretch_wrist`: 2).
     - Notable keyword collision bugs observed:
       - `#48` `Wrist Figure-Eight Fluid Mobilizer` matches `"figure"` -> `img_stretch_figure_four` (rule 3 before rule 13).
       - `#135–136` `Skandasana (Side Lunge - Right/Left)` matches `"lunge"` -> `img_stretch_lunge` (rule 9) before `"skandasana"` -> `img_stretch_warrior` (rule 16).
       - `#44` `Downward Dog Calf Pedaling` matches `"pedal"` -> `img_stretch_calf` (rule 7) before `"downward"` (rule 16).

4. **Direct Visual Inspection of the 8 Unified Reference Drawables (`app/src/main/res/drawable/`)**
   - `img_stretch_neck.jpg`: Seated cross-legged, head tilted to left shoulder with left hand on crown/head, right fingertips on mat.
   - `img_stretch_standing.jpg`: Standing upright facing front, feet hip-width apart, arms relaxed by sides (Tadasana).
   - `img_stretch_downward_dog.jpg`: Inverted-V Downward-Facing Dog, straight arms/legs, both heels reaching toward mat.
   - `img_stretch_warrior.jpg`: Warrior II — wide stance, front knee bent 90°, back leg straight, arms parallel to floor, gaze over front hand.
   - `img_stretch_lotus.jpg`: Full Padmasana (both feet on opposite thighs soles up), hands on knees in Chin Mudra, eyes closed.
   - `img_stretch_tree.jpg`: Standing on one leg, opposite foot pressed to inner thigh above knee, palms in prayer at heart center.
   - `img_stretch_bridge.jpg`: Supine, knees bent, feet flat hip-width, hips lifted, arms flat palms-down on mat beside torso.
   - `img_stretch_child.jpg`: Kneeling hips on heels, torso folded forward, forehead on mat, arms extended straight forward.

---

## 2. Logic Chain

1. **Enumerating & Verifying 150 Items**: Parsing `StretchLibrary.kt` and `ExtraYogaAsanas.kt` yields 100 + 50 = 150 unique `id` entries in exact catalog order (`#1–150`).
2. **Symmetrical Left/Right Pairing (R2)**:
   - Scanning all 150 IDs, names, and cues identifies **32 explicit Left/Right pairs** (**64 stretches**: 22 pairs in `StretchLibrary`, 10 pairs in `ExtraYogaAsanas`) and **86 standalone stretches** (56 in `StretchLibrary`, 30 in `ExtraYogaAsanas`).
   - Merging each symmetrical L/R pair into 1 canonical pose produces **118 canonical poses** (32 + 86 = 118).
3. **Cross-Catalog Identical Pose Deduplication**:
   - Comparing `prepCue`/`holdCue` across `StretchLibrary` (`#1–50` general stretches vs `#51–100` Yoga Asanas) and `ExtraYogaAsanas` (`#101–150`) reveals **9 groups** where multiple catalog entries describe the same anatomical pose:
     1. Cobra (`#23`, `#51`)
     2. Child's Pose (`#50`, `#53`, `#131` Shashankasana)
     3. Seated Butterfly (`#28`, `#63`)
     4. Pigeon Pose (`#29/#30`, `#94/#95`)
     5. Standing Forward Fold (`#33`, `#70`)
     6. Janu Sirsasana (`#35`, `#61/#62`)
     7. Wide-Legged Straddle Fold (`#39`, `#85`)
     8. Pyramid Pose (`#40`, `#91/#92`)
     9. Supine Double Knee Hug (`#17`, `#110` Pavanamuktasana)
   - If these 9 groups also share illustrations across catalog sections, unique poses drop from **118** to **108** (-10).
   - Note: `#24/#25` Low Lunge vs `#75/#76` Crescent Low Lunge (arms overhead), `#19/#20` two-knee twist vs `#96/#97` single-knee twist, `#21` vs `#65/#66` vs `#138/#139` seated twists, `#16` vs `#49` side bends, and `#13` vs `#99` vs `#100` Cat/Cow have distinct limb/phase cues and are kept separate.
4. **Candidate Matches to the 8 Unified-Style Reference Drawables** (for cross-check with Explorer 3):
   - `img_stretch_neck.jpg` -> **#1/#2** `neck_side_right/left` *(caveat: image shows seated hand-assisted tilt; cues don't specify seated or hand-assist)*.
   - `img_stretch_standing.jpg` -> **#84** `tadasana_mountain_alignment`.
   - `img_stretch_downward_dog.jpg` -> **#52** `adho_mukha_svanasana_downward_dog` *(plus candidate **#44** `downward_dog_pedal`, caveat: static image vs alternating heel-pedal cue)*.
   - `img_stretch_warrior.jpg` -> **#73/#74** `virabhadrasana_two_right/left`.
   - `img_stretch_lotus.jpg` -> **#101** `padmasana_lotus` *(plus debatable candidates **#149** `bhramari_pranayama` and **#150** `ujjayi_pranayama` whose cues say sit tall/upright with eyes closed rather than full Lotus)*.
   - `img_stretch_tree.jpg` -> **#57/#58** `vrikshasana_tree_right/left`.
   - `img_stretch_bridge.jpg` -> **#54** `setu_bandhasana_bridge_pose` *(minor caveat: #54 holdCue says interlace fingers under back, while image shows arms flat beside body)*.
   - `img_stretch_child.jpg` -> **#50** `child_pose_full_body`, **#53** `balasana_childs_pose`, **#131** `shashankasana_hare`.
   - Across the 8 reference drawables, **9 to 13 stretches** (covering **8 to 11 canonical poses**) are plausible matches, leaving **107–110 canonical poses** (or **97–100** if cross-catalog duplicates share images) needing new unified-style illustrations or `null` unmapped states.

---

## 3. Caveats

- **Static Code Parsing vs Runtime DB**: `catalog_150.md` reflects the static definitions in `StretchLibrary.kt` and `ExtraYogaAsanas.kt` plus a faithful Python simulation of `ArtworkMapper.kt` and `SanskritPhonetics.kt`; Gradle unit tests were not executed in this read-only survey step.
- **Unilateral Single-Entry Stretches**: Several stretches in `StretchLibrary` (`#3`, `#11`, `#16`, `#18`, `#21`, `#34`, `#35`, `#43`, `#46`, `#47`, `#49`, `#102`) instruct one side (e.g., "right armpit", "one leg") without a separate Left/Right partner in the catalog; they are classified as `Standalone` (1 image each).
- **Pose Subtlety / Judge Decisions Needed**:
  - Whether `#1/#2` (`neck_side_right/left`), `#44` (`downward_dog_pedal`), `#54` (`setu_bandhasana_bridge_pose`), and `#149/#150` (`bhramari`/`ujjayi`) pass strict visual-to-cue verification against the existing 8 reference drawables (to be cross-checked with Explorer 3).
  - Whether the 9 cross-catalog identical pose groups (e.g. `#23` Gentle Cobra vs `#51` Bhujangasana; `#50`/`#53`/`#131` Child's/Hare) should share 1 illustration (**108** total) or remain 1-per-canonical-L/R-group (**118** total).
  - For R1 ("each entry must include the stretch name, its Indian/Sanskrit name (when present)"): note that `#35` and `#51–100` have `indianName = null` in `StretchLibrary.kt` source literals, yet resolve Devanagari at runtime via `SanskritPhonetics` in `StretchRepository.kt:39-40`. Both explicit and runtime-resolved names are provided in `catalog_150.md`.

---

## 4. Conclusion

The Stretch Coach library contains **150** stretches (`100` in `StretchLibrary.kt` + `50` in `ExtraYogaAsanas.kt`), collapsing to **118 canonical poses** when merging the **32 symmetrical Left/Right pairs** (64 stretches) with the **86 standalone stretches** (or **108 distinct anatomical poses** if also deduplicating 9 cross-catalog identical pose groups). Currently, `ArtworkMapper.kt` assigns legacy mixed-style drawables to **89** stretches and over-broadly maps **61** stretches onto the **8** unified reference drawables; at most **9–13 stretches** (representing **8–11 canonical poses**) accurately match the 8 existing unified reference drawables.

Complete stretch-by-stretch inventory, cues, `indianName` values, current mappings, L/R pairings, and canonical groupings are saved at:
`/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_survey_2/catalog_150.md`

---

## 5. Verification Method

1. Inspect `catalog_150.md` directly (1,561 lines, sequential sections `### 1.` through `### 150.`).
2. Cross-check counts and cue text against:
   - `app/src/main/java/com/example/data/StretchLibrary.kt` (lines 27–1043)
   - `app/src/main/java/com/example/data/ExtraYogaAsanas.kt` (lines 8–559)
   - `app/src/main/java/com/example/data/ArtworkMapper.kt` (lines 8–135)
   - `app/src/main/java/com/example/data/StretchRepository.kt` (lines 35–55)
   - `app/src/main/java/com/example/audio/SanskritPhonetics.kt` (lines 140–214, 374–451)
3. Re-run the header verification check or Gradle unit tests (`./gradlew testDebugUnitTest --no-daemon`) in later phases.
4. **Invalidation conditions**: Any edit to `StretchLibrary.kt`, `ExtraYogaAsanas.kt`, `ArtworkMapper.kt`, or `SanskritPhonetics.kt` invalidates the current mappings / counts recorded in `catalog_150.md`.
