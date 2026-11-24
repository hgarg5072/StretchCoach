# Handoff Report — Explorer 3 (`teamwork_explorer_i2_3`)

**Handoff Type**: Hard  
**Scope**: M5 Pose Prompt Specification (`ExtraYogaAsanas` `#101–#150`, excluding `#110` [M2] & `#131` [M1])  
**Primary Deliverable**: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_3/pose_prompts_m5.md`

---

## 1. Observation

1. **Source Catalog (`ExtraYogaAsanas.kt:8-560`)**:
   - `EXTRA_50_ASANAS` defines 50 `LibraryStretch` items (`#101–#150`), all with explicit Devanagari `indianName` and `category = StretchCategory.YOGA_ASANAS`.
   - `#110` (`pavanamuktasana_wind_relieving`) is assigned to **M2** (`PROJECT.md:169`), and `#131` (`shashankasana_hare`) is already mapped in **M1** (`ArtworkMapper.kt:24-25` → `R.drawable.img_stretch_child`).
   - Removing `#110` and `#131` leaves **48 stretches** in M5: **28 standalone** stretches and **10 symmetrical Left/Right pairs** (20 stretches) = **38 canonical poses**.
2. **Existing `img_stretch_lotus.jpg` Visual Defect**:
   - Direct `view_file` inspection of `app/src/main/res/drawable/img_stretch_lotus.jpg` confirms the right foot is tucked *underneath* the left calf/ankle on the mat rather than placed atop the left thigh, and the hands float slightly off the knees. This fails `#101 Padmasana (Lotus Pose)`'s `prepCue` (`"place each foot onto the opposite thigh with soles facing upward"`) and `holdCue` (`"Rest hands on knees in chin mudra"`), requiring regeneration as `img_stretch_b4_padmasana_lotus_full.jpg`.
3. **19 + 19 Canonical Split**:
   - `#101–#123` (excl. `#110`) contains **18** canonical poses (`#101–#109`, `#111–#115`, `#116/#117`, `#118/#119`, `#120/#121`, `#122/#123`).
   - `#124–#150` (excl. `#131`) contains **20** canonical poses.
   - Moving `#124/#125` (`natarajasana_dancer`) into Part A produces an exact **19 + 19** split:
     - **Part A (19 poses, 24 stretches)**: `#101–#125` excl. `#110` (`Pose A1`–`Pose A19`).
     - **Part B (19 poses, 24 stretches)**: `#126–#150` excl. `#131` (`Pose B1`–`Pose B19`).
4. **R4 Inspection of `#147 Surya Namaskar` (`ExtraYogaAsanas.kt:516-525`)**:
   - `prepCue`: `"Stand at top of mat in Prayer Pose and prepare to flow smoothly with each breath."`
   - `holdCue`: `"Move mindfully through reach, fold, lunge, plank, cobra, and downward dog."`
   - `PROJECT.md:218` states M5 covers `"38 canonical poses (covering 47 stretches)"` (`48 M5 stretches - 1 [#147] = 47`).
5. **Normalization Contract (`ArtworkMapper.kt:13-14`, `ExampleUnitTest.kt:214-216`)**:
   - `normalizeKey(raw)` lowercases, trims, and collapses whitespace to single spaces.
   - `ExampleUnitTest` enforces `getDrawableForStretch(stretch.name) == getDrawableForStretch(stretch.id)` for every stretch.

---

## 2. Logic Chain

1. **Exact 19 + 19 Split**: Moving `#124/#125` (`natarajasana_dancer`) into Part A balances worker batches cleanly at **19 canonical poses (24 stretches)** each without crossing any L/R pair boundary.
2. **Strict R4 Verdict on `#147`**: Because `#147`'s 60-second `holdCue` specifies a 6-pose dynamic vinyasa flow (`reach, fold, lunge, plank, cobra, and downward dog`), a single static 16:9 image cannot faithfully depict the pose without either multi-panel comic strips (violating single-scene studio style) or depicting only Prayer Pose (*Pranamasana* setup, which fails `holdCue` verification and risks confusion with `Tadasana`). Recommending `#147` remain **unmapped (`null`)** satisfies R3/R4 and reconciles `PROJECT.md:218`'s exact `47 stretches` count (`48 - 1 = 47`).
3. **Cue-Faithful Differentiation of `#148–#150` & Seated Meditative Poses (`#101–#105`)**:
   - `#101` (`padmasana_lotus_full`) explicitly enforces *both* bare feet atop opposite thighs with soles upward and hands resting *on* knees in Chin Mudra.
   - `#102` (`ardha_padmasana_half_lotus`) specifies one foot tucked under and one atop the opposite thigh with palms down on knees.
   - `#103` (`vajrasana_thunderbolt`) specifies kneeling side profile seated atop heels with big toes touching behind; `#126` (`virasana_hero`) distinguishes itself by seating *between* flared heels with knees together.
   - `#104` (`sukhasana_easy_pose`) keeps both feet tucked underneath mid-shins, soft forward gaze; `#105` (`siddhasana_accomplished`) aligns both heels along the midline in front of pelvis.
   - `#148` (`anulom_vilom_pranayama`) depicts right thumb & ring finger in Vishnu/Nasagra Mudra near nostrils; `#149` (`bhramari_pranayama`) depicts palms-down on knees, eyes closed, relaxed jaw/lips (avoiding un-cued Shanmukhi Mudra ear-plugging and avoiding wave/bee icons); `#150` (`ujjayi_pranayama`) uses a subtle 3/4 seated angle with Dhyana Mudra in lap, dropped shoulders, and lengthened throat.
4. **Reference Selection**: Each pose specifies 2 existing verified `app/src/main/res/drawable/img_stretch_*.jpg` references matching its posture plane (front seated, side standing/plank, supine/inversion, or standing lunge/balance) plus strict Unified Studio Art Style constraints (`16:9`, sage-olive top, charcoal leggings, low bun, barefoot, beige woven jute mat, honey-oak floor, cream plaster studio, zero text/watermarks/blobs).

---

## 3. Caveats

- **Prompt Generation Not Executed**: Per read-only scope, no `generate_image` calls were run. Complex inversions/arm-balances (`#106` Chakrasana, `#107` Sarvangasana, `#108` Sirsasana, `#112` Bakasana, `#113` Mayurasana, `#130` Kurmasana, `#137` Tolasana) and `#101` Full Lotus foot placement may require iterative visual inspection and regeneration during M5 execution to pass R4 anatomical verification.
- **Count Reconciliation**: `PROJECT.md:218` says `38 canonical poses (covering 47 stretches)`. If `#147` remains unmapped (`null`), M5 actually generates **37** image files covering **47** stretches (`#147` is the 38th canonical item audited and intentionally kept `null`). If an orchestrator overrides R4 and maps `#147` as Prayer Pose, that would be 38 images covering 48 stretches.

---

## 4. Conclusion

All **38** M5 canonical pose specifications (Part A: 19 poses `#101–#125` excl. `#110`; Part B: 19 poses `#126–#150` excl. `#131`) are fully specified in `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_3/pose_prompts_m5.md`, complete with `img_stretch_b4_<key>.jpg` filenames, exact normalized `name` + `id` keys for `ArtworkBatch4.kt`, 2 `ImagePaths` references per pose, cue-faithful prompts adhering to the Unified Studio Art Style, and a strict R4 recommendation to keep `#147` (`surya_namaskar_flow`) unmapped (`null`).

---

## 5. Verification Method

1. **Inspect Deliverable**: View `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_3/pose_prompts_m5.md` and verify:
   - Part A contains 19 poses (`A1`–`A19`) and Part B contains 19 poses (`B1`–`B19`), totaling 38 canonical items.
   - `#110` and `#131` are excluded from `ArtworkBatch4.kt` keys.
2. **Cross-Check Against Source**: Compare all normalized keys in `pose_prompts_m5.md` against `app/src/main/java/com/example/data/ExtraYogaAsanas.kt` (`#101–#150`) and `ArtworkMapper.normalizeKey()` (`app/src/main/java/com/example/data/ArtworkMapper.kt:13-14`).
3. **Invalidation Conditions**: Any key mismatch with `ExtraYogaAsanas.kt`, inclusion of `#110`/`#131` in `ArtworkBatch4.kt`, or invalid Android resource filenames (`[^a-z0-9_]` before `.jpg`) invalidates the mapping spec.
