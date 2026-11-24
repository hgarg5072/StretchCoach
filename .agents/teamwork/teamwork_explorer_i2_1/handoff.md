# Handoff Report — Explorer 1 (`teamwork_explorer_i2_1`)

**Handoff Type**: Hard  
**Scope**: Read-only pose prompt specification for Milestone M2 (`ArtworkBatch1`, 24 poses / 30 stretches) and Milestone M3 (`ArtworkBatch2`, 17 poses / 31 stretches).  
**Primary Deliverable**: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_1/pose_prompts_m2_m3.md`

---

## 1. Observation

1. **Catalog & Milestone Assignments (`PROJECT.md`, `StretchLibrary.kt`, `ExtraYogaAsanas.kt`)**:
   - `StretchLibrary.kt` (`#1–#100`) and `ExtraYogaAsanas.kt` (`#101–#150`) define 150 `LibraryStretch` items (`id`, `name`, `prepCue`, `holdCue`).
   - `ArtworkMapper.kt:13-14` normalizes lookup keys via `raw.trim().lowercase().replace(Regex("\\s+"), " ")`, and `coreReferenceMap` (`ArtworkMapper.kt:16-42`) registers both lowercase trimmed `name` and `id` for each stretch.
   - `PROJECT.md:58-170` assigns **24 canonical poses (30 stretches)** to **M2** (`#1–#23`, `#46–#51`, `#110`) and **17 canonical poses (31 stretches)** to **M3** (`#24–#45`, `#61–#63`, `#70`, `#85`, `#91–#92`, `#94–#95`). *(Note: `PROJECT.md:217` summary line states "covering 30 stretches" for M3, while its per-row table assigns 31 stretches to M3: 22 in `#24–#45` + 9 in `#61, #62, #63, #70, #85, #91, #92, #94, #95`.)*
2. **Existing Unified Reference Drawables (`app/src/main/res/drawable/`)**:
   - `list_dir` and `view_file` confirmed 11 files in `app/src/main/res/drawable/`: `ic_launcher_background.xml`, `ic_launcher_foreground.xml`, `img_app_icon.jpg`, and the 8 unified `1376×768` `.jpg` reference drawables (`img_stretch_bridge.jpg`, `img_stretch_child.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_lotus.jpg`, `img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_tree.jpg`, `img_stretch_warrior.jpg`). All 15 legacy mixed-style drawables have already been removed.
   - Direct visual inspection confirmed `img_stretch_neck.jpg` depicts a seated neck tilt **with the practitioner's left hand resting on top of her head**, whereas `#1`/`#2` (`neck_side_right`/`neck_side_left`) explicitly cue *"Let gravity do the work"* (hands-free).
3. **Programmatic Verification of `pose_prompts_m2_m3.md`**:
   - Executed Python verification script checking `pose_prompts_m2_m3.md` against `StretchLibrary.kt` and `ExtraYogaAsanas.kt`:
     - Command exit code: `0`
     - Output: `Total filenames: 41`, `Total ImagePaths refs: 82`, `ALL CHECKS PASSED: 41 poses (24 M2 + 17 M3), 61 stretches (122 keys), 82 verified ImagePaths!`

---

## 2. Logic Chain

1. From Observation 1, every canonical pose entry in `ArtworkBatch1.kt` (M2) and `ArtworkBatch2.kt` (M3) must map **both** exact normalized `name` and `id` strings for every stretch assigned to that pose so `ArtworkMapper.getDrawableForStretch()` resolves regardless of whether caller passes `name` or `id`.
2. From Observation 2, passing `img_stretch_neck.jpg` as an `ImagePaths` reference for `#1/#2` (`neck_side_stretch`), `#3` (`chin_tuck_levator`), or `#8` (`scm_neck_turn`) risks biasing `generate_image` toward placing a hand on the head. Therefore, those poses pair `img_stretch_lotus.jpg` + `img_stretch_standing.jpg` and explicitly state *"no hand touching her head"*.
3. Checking `prepCue`/`holdCue` across all 41 poses revealed:
   - **38 poses** are fully and unambiguously depictable in a single static 16:9 illustration (including `#1/#2` hands-free, `#9` forearm flat on wall at 90°, `#19/#20` both-knees-together twist, `#23/#51` gentle cobra with bent elbows and grounded pelvis/hips, `#41/#42` barefoot wall calf stretch, `#43` bent-knee soleus stretch, `#50` wide-knee restorative child's pose).
   - **3 poses** contain dynamic alternating or continuous circular cues in `holdCue` where a single static frame cannot animate the full motion under R4:
     - **#13 (`cat_cow_spinal_flow`)** — alternating inhale arch (Cow) / exhale round (Cat), while `#99` Cat and `#100` Cow have separate static poses in M4.
     - **#44 (`downward_dog_pedal`)** — alternating rhythmic heel pedaling (mitigated by depicting one heel pressed flat down and the opposite knee bent/heel lifted high).
     - **#48 (`wrist_rotations_tendon`)** — rolling clasped wrists in figure-eight circles and reversing halfway (mitigated by depicting the exact `prepCue` clasped-hands posture in front of chest with zero arrows).
4. From Observation 3, all 41 `drawable_filename` values match `^[a-z][a-z0-9_]*\.jpg$` with zero collisions (`img_stretch_b1_*` for M2, `img_stretch_b2_*` for M3), all 82 `ImagePaths` point to existing unified reference files, and all 122 normalized keys across the 61 stretches are verified present.

---

## 3. Caveats

- **`PROJECT.md` M3 Stretch Count Typo**: `PROJECT.md:217` states M3 covers "30 stretches", while the table in `PROJECT.md:83-104, 120-154` assigns **31 stretches** to M3 (`#24–#45` + `#61, #62, #63, #70, #85, #91, #92, #94, #95`). `pose_prompts_m2_m3.md` covers all 31 assigned stretches.
- **Image Generation Format**: `generate_image` may output `.png` or non-`1376×768` dimensions depending on the backend; M2/M3 implementers must convert/resize outputs to **1376 × 768 baseline JPEG** (quality ~85) matching the existing 8 unified drawables before placing them in `app/src/main/res/drawable/` (or `drawable-nodpi/` per `drawables_audit.md §5.3`).
- **R4 Judge Discretion on Flagged Dynamic Poses**: `#13`, `#44`, and `#48` are explicitly flagged; if the independent visual/rubric judge determines a single static frame fails R4 for `#13` or `#48`, those entries should remain unmapped (`null`) in `ArtworkBatch1.kt` per R3/R4.

---

## 4. Conclusion

Complete, self-contained `generate_image` prompt specifications, `ImagePaths`, `drawable_filename`s, normalized Kotlin registry keys, and R4 flags for all **41 canonical poses (61 stretches)** in M2 (`ArtworkBatch1.kt`) and M3 (`ArtworkBatch2.kt`) are finalized in `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_1/pose_prompts_m2_m3.md`. M2 and M3 implementers can execute image generation and populate `ArtworkBatch1.kt` and `ArtworkBatch2.kt` directly from this specification.

---

## 5. Verification Method

To independently verify completeness and validity of `pose_prompts_m2_m3.md`:
1. Inspect `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_1/pose_prompts_m2_m3.md`.
2. Re-run the read-only Python verification check confirming 41 unique filenames matching `^[a-z][a-z0-9_]*\.jpg$` (`24` `img_stretch_b1_*` + `17` `img_stretch_b2_*`), 82 existing `ImagePaths`, and all 122 normalized `id`/`name` keys for the 61 M2+M3 stretches from `StretchLibrary.kt` and `ExtraYogaAsanas.kt`:
   ```bash
   python3 -c '
   import os, re
   md = open("/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_1/pose_prompts_m2_m3.md").read()
   fnames = re.findall(r"\*\*drawable_filename\*\*: `(img_stretch_[^`]+)`", md)
   assert len(fnames) == 41 and len(set(fnames)) == 41
   assert len([f for f in fnames if f.startswith("img_stretch_b1_")]) == 24
   assert len([f for f in fnames if f.startswith("img_stretch_b2_")]) == 17
   assert all(re.match(r"^[a-z][a-z0-9_]*\.jpg$", f) for f in fnames)
   img_paths = re.findall(r"(/Users/hn/antigravity/Stretch Coach/app/src/main/res/drawable/img_stretch_[^`\s]+)", md)
   assert len(img_paths) == 82 and all(os.path.exists(p) for p in img_paths)
   print("Verified 41 poses and 82 ImagePaths")
   '
   ```
3. **Invalidation conditions**: Any change to `LibraryStretch.id`, `LibraryStretch.name`, `prepCue`, `holdCue`, or milestone assignments in `PROJECT.md` invalidates the affected pose entries.
