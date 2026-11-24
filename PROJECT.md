# Project: Stretch Coach — Artwork Audit, Unified Illustration Generation & Visual Gallery

## Architecture
- **Android App (`app/src/main/java/com/example/`)**:
  - **Catalog Data**: `StretchLibrary.kt` (`#1–#100`) and `ExtraYogaAsanas.kt` (`#101–#150`) define the immutable 150-item stretch/asana catalog (`LibraryStretch`). `StretchRepository.kt` seeds Room (`LibraryStretchEntity`) and resolves `indianName` via `lib.indianName ?: SanskritPhonetics.buildSpokenPlan(lib.name).sanskritDevanagari`.
  - **Modular Artwork Mapping**:
    - `ArtworkMapper.kt`: Centralized lookup `@DrawableRes fun getDrawableForStretch(name: String, category: String = ""): Int?`. Returns `null` when no verified accurate pose image exists (NO category fallbacks, NO catch-all fallback).
    - Delegates exact normalized pose-key/name lookup across:
      1. Core verified reference poses (`ArtworkCoreRefs` / inline map for verified subset of the 8 existing `img_stretch_*.jpg` files)
      2. `com.example.data.artwork.ArtworkBatch1` (M2: Upper body, neck, chest, core, back, arms, wrists)
      3. `com.example.data.artwork.ArtworkBatch2` (M3: Hips, glutes, hamstrings, quads, calves, feet)
      4. `com.example.data.artwork.ArtworkBatch3` (M4: `StretchLibrary` Yoga Asanas `#55–#100`)
      5. `com.example.data.artwork.ArtworkBatch4` (M5: `ExtraYogaAsanas` `#102–#150`)
  - **UI Artwork Rendering (4 sites)**:
    - `StretchItemCard.kt`, `AddFromLibraryDialog.kt`, `StretchDetailDialog.kt`, `PlayerArtworkCard.kt`: Check `customFile` first, then `ArtworkMapper.getDrawableForStretch(...)`. When both are `null`, omit the image `Box` and adjacent `Spacer` completely. In `PlayerArtworkCard.kt`, remove the `else { R.drawable.img_stretch_standing }` fallback and render a clean compact non-image header preserving Step badge, Phase badge, stretch name, and notes with accessible contrast.
  - **Drawable Assets (`app/src/main/res/drawable/`)**:
    - Keep `img_app_icon.jpg`, `ic_launcher_background.xml`, `ic_launcher_foreground.xml`.
    - Keep verified subset of the 8 unified-style `1376x768` `.jpg` reference drawables (`img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_warrior.jpg`, `img_stretch_lotus.jpg`, `img_stretch_tree.jpg`, `img_stretch_bridge.jpg`, `img_stretch_child.jpg`).
    - Delete all 15 older mixed-style drawables (`butterfly`, `calf`, `cat_cow`, `chest`, `cobra`, `figure_four`, `floor`, `hamstring`, `lunge`, `pigeon`, `quad`, `side_bend`, `twist`, `upper`, `wrist`).
    - Add newly generated & visually verified `1376x768` unified-style `.jpg` drawables (`[a-z][a-z0-9_]*\.jpg`), sharing one image per symmetrical L/R pair and verified cross-catalog identical poses.
- **Local HTML Visual Gallery (`stretch_gallery.html`)**:
  - Root-level standalone HTML review sheet rendering all 150 stretches in exact catalog order (`#1–#150`), showing index, source, stretch name, Indian/Sanskrit name (explicit and runtime-resolved where defined), category, `prepCue`/`holdCue`, drawable filename, relative `<img src="app/src/main/res/drawable/...">` preview (or explicit `"No verified image assigned"` state when `null`).

## Code Layout
- `app/src/main/java/com/example/data/ArtworkMapper.kt` — **M1 owns** (M2–M5 MUST NOT edit)
- `app/src/main/java/com/example/data/artwork/ArtworkBatch1.kt` — **M2 owns** (stub created in M1)
- `app/src/main/java/com/example/data/artwork/ArtworkBatch2.kt` — **M3 owns** (stub created in M1)
- `app/src/main/java/com/example/data/artwork/ArtworkBatch3.kt` — **M4 owns** (stub created in M1)
- `app/src/main/java/com/example/data/artwork/ArtworkBatch4.kt` — **M5 owns** (stub created in M1)
- `app/src/main/java/com/example/ui/components/{StretchItemCard,PlayerArtworkCard,AddFromLibraryDialog,StretchDetailDialog}.kt` — **M1 owns**
- `app/src/test/java/com/example/ExampleUnitTest.kt`, `CardLayoutScreenshotTest.kt` — **M1 owns** (Final Milestone may add/run verification tests)
- `app/src/main/res/drawable/img_stretch_*.jpg` — **Disjoint file ownership per milestone**:
  - M1: Deletes 15 legacy drawables; owns the 8 existing unified reference drawables.
  - M2–M5: Each milestone owns only its uniquely named `img_stretch_b{1..4}_<pose>.jpg` (or canonical non-colliding `img_stretch_<pose>.jpg` assigned exclusively in its batch table below).
- `stretch_gallery.html` — **M6 owns**
- `tests/e2e/` (or project E2E verification scripts/tests) + `TEST_INFRA.md` + `TEST_READY.md` — **E2E Testing Track owns**

## Unified Studio Art Style Specification (Mandatory Reference for All Generated Artwork)
1. **Reference Images**: Always supply existing `app/src/main/res/drawable/img_stretch_*.jpg` reference files (e.g., `img_stretch_standing.jpg`, `img_stretch_child.jpg`, `img_stretch_lotus.jpg`) to `generate_image`.
2. **Medium & Linework**: Clean minimalist editorial ink-and-watercolor illustration. Fine dark-brown/charcoal ink outlines on foreground practitioner & mat, soft matte watercolor shading, softer lower-contrast watercolor washes in studio background.
3. **Practitioner**: Young woman with warm fair-to-light-olive skin, soft peach cheek blush, calm serene expression, dark brown hair in a neat **low bun** with center part and subtle wisps, **sage-olive** sleeveless sports bra/crop top, **charcoal-grey** high-waisted ankle-length yoga leggings, **always barefoot**.
4. **Setting & Framing**: Centered full-body on a **beige woven jute yoga mat** over **light honey-oak wood plank flooring** in a warm sunlit cream-plaster minimalist yoga studio with potted green plants (e.g. Monstera in terracotta pot) and natural window sunlight. Canvas **16:9** (`1376x768` JPEG), generous margins around entire body, **zero text, zero watermarks, zero mat logos, zero abstract pastel blobs**.

## Feature Inventory (All 150 Stretches + Architecture & Gallery)
Every stretch from `StretchLibrary.kt` (`#1–#100`) and `ExtraYogaAsanas.kt` (`#101–#150`) is assigned to an exact milestone and canonical pose key.

### System & UI Features
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| F-ARCH-1 | Nullable `ArtworkMapper` & Modular Registries | Return `@DrawableRes Int?` (`null` when unmapped), remove 15 legacy drawables, remove category/catch-all fallbacks, add `ArtworkBatch1..4` | M1 | R2, R3, Survey 1/3 |
| F-ARCH-2 | Clean No-Image UI (`4` Composables) | Omit image `Box` + `Spacer` when `customFile == null && artworkRes == null` in `StretchItemCard`, `AddFromLibraryDialog`, `StretchDetailDialog`, `PlayerArtworkCard` (compact header + remove line 62 fallback) | M1 | R3, Survey 1 |
| F-ARCH-3 | Audit 8 Existing Unified References | Verify exact anatomical cue alignment for `#1/#2`, `#50/#53/#131`, `#52`, `#54`, `#57/#58`, `#73/#74`, `#84`, `#101` | M1 | R2, R4, Survey 2/3 |
| F-ARCH-4 | Unit & Build Verification | Update `ExampleUnitTest.testArtworkMapper()`, verify `CardLayoutScreenshotTest`, verify `./gradlew assembleDebug testDebugUnitTest --no-daemon` | M1, Final | AC Build & Tests |
| F-GAL-1 | Local HTML Visual Gallery (`stretch_gallery.html`) | All 150 stretches in order (`#1–#150`) with name, Indian/Sanskrit name, cues, relative image preview or explicit no-image state | M6 | R1, AC Gallery |
| F-E2E-1 | Opaque-Box E2E Test Suite (Tiers 1–4) | Requirement-driven verification suite + `TEST_INFRA.md` + `TEST_READY.md` | E2E Track | Project Pattern |

### 150-Stretch Catalog Assignment (`#1–#150` -> 108 Canonical Poses)
| Cat # | Stretch ID | Name | Indian/Sanskrit (Explicit / Runtime) | Canonical Pose Key | Milestone |
|---:|---|---|---|---|---|
| 1 | `neck_side_right` | Neck Side Stretch (Right) | `—` | `neck_side_stretch` (regenerate hands-free per R4) | M2 |
| 2 | `neck_side_left` | Neck Side Stretch (Left) | `—` | `neck_side_stretch` (regenerate hands-free per R4) | M2 |
| 3 | `chin_tuck_levator` | Chin Tuck & Levator Stretch | `—` | `chin_tuck_levator` | M2 |
| 4 | `cross_body_shoulder_right` | Cross-Body Shoulder Stretch (Right) | `—` | `cross_body_shoulder` | M2 |
| 5 | `cross_body_shoulder_left` | Cross-Body Shoulder Stretch (Left) | `—` | `cross_body_shoulder` | M2 |
| 6 | `overhead_triceps_lat` | Overhead Triceps & Lat Reach | `—` | `overhead_triceps_lat` | M2 |
| 7 | `eagle_arms_stretch` | Eagle Arms Scapular Spread | `—` | `eagle_arms_stretch` | M2 |
| 8 | `scm_neck_turn` | Sternocleidomastoid (SCM) Neck Stretch | `—` | `scm_neck_turn` | M2 |
| 9 | `doorway_chest_opener` | Doorway / Wall Pectoral Opener | `—` | `doorway_chest_opener` | M2 |
| 10 | `interlaced_chest_expansion` | Interlaced Chest Expansion | `—` | `interlaced_chest_expansion` | M2 |
| 11 | `child_pose_lat_reach` | Child's Pose with Lat Reach | `—` | `child_pose_lat_reach` | M2 |
| 12 | `puppy_pose_melting_heart` | Puppy Pose (Melting Heart) | `—` | `puppy_pose_melting_heart` | M2 |
| 13 | `cat_cow_spinal_flow` | Cat-Cow Spine Mobilizer | `—` | `cat_cow_spinal_flow` | M2 |
| 14 | `thread_the_needle_right` | Thread the Needle (Right Side) | `—` | `thread_the_needle` | M2 |
| 15 | `thread_the_needle_left` | Thread the Needle (Left Side) | `—` | `thread_the_needle` | M2 |
| 16 | `standing_side_arc` | Standing Side-Bend Arc | `—` | `standing_side_arc` | M2 |
| 17 | `supine_knees_to_chest` | Supine Double Knee Hug | `—` | `supine_double_knee_hug` | M2 |
| 18 | `single_knee_to_chest` | Single Knee-to-Chest Decompression | `—` | `single_knee_to_chest` | M2 |
| 19 | `reclining_spinal_twist_right` | Reclining Spinal Twist (Right) | `—` | `reclining_spinal_twist` | M2 |
| 20 | `reclining_spinal_twist_left` | Reclining Spinal Twist (Left) | `—` | `reclining_spinal_twist` | M2 |
| 21 | `seated_spinal_twist` | Seated Torso Spinal Twist | `—` | `seated_spinal_twist` | M2 |
| 22 | `sphinx_pose_extension` | Sphinx Pose Lumbar Extension | `—` | `sphinx_pose_extension` | M2 |
| 23 | `cobra_stretch_gentle` | Gentle Cobra Pose | `—` | `cobra_pose_bhujangasana` | M2 |
| 24 | `hip_flexor_lunge_right` | Low Lunge Hip Flexor (Right Forward) | `—` | `low_lunge_hip_flexor` | M3 |
| 25 | `hip_flexor_lunge_left` | Low Lunge Hip Flexor (Left Forward) | `—` | `low_lunge_hip_flexor` | M3 |
| 26 | `figure_four_glute_right` | Reclining Figure-4 Glute (Right) | `—` | `reclining_figure_four_glute` | M3 |
| 27 | `figure_four_glute_left` | Reclining Figure-4 Glute (Left) | `—` | `reclining_figure_four_glute` | M3 |
| 28 | `seated_butterfly` | Seated Butterfly Adductor Stretch | `—` | `seated_butterfly_baddha_konasana` | M3 |
| 29 | `pigeon_pose_right` | Pigeon Pose (Right Shin Forward) | `—` | `pigeon_pose_prone` | M3 |
| 30 | `pigeon_pose_left` | Pigeon Pose (Left Shin Forward) | `—` | `pigeon_pose_prone` | M3 |
| 31 | `lizard_lunge_hip` | Lizard Lunge Inside Hip Opener | `—` | `lizard_lunge_hip` | M3 |
| 32 | `ninety_ninety_hip` | 90-90 Seated Hip Rotator Stretch | `—` | `ninety_ninety_hip` | M3 |
| 33 | `standing_hamstring_fold` | Standing Forward Hamstring Fold | `—` | `standing_forward_fold_uttanasana` | M3 |
| 34 | `single_leg_hamstring_heel` | Single-Leg Heel Hamstring Stretch | `—` | `single_leg_hamstring_heel` | M3 |
| 35 | `seated_single_leg_fold` | Seated Single-Leg Fold (Janu Sirsasana) | `जानु शीर्षासन` (runtime) | `head_to_knee_janu_sirsasana` | M3 |
| 36 | `standing_quad_right` | Standing Quad Stretch (Right Leg) | `—` | `standing_quad_stretch` | M3 |
| 37 | `standing_quad_left` | Standing Quad Stretch (Left Leg) | `—` | `standing_quad_stretch` | M3 |
| 38 | `side_lying_quad` | Side-Lying Quad & Hip Stretch | `—` | `side_lying_quad` | M3 |
| 39 | `wide_legged_straddle_fold` | Wide-Legged Straddle Forward Fold | `—` | `wide_legged_forward_fold_prasarita` | M3 |
| 40 | `pyramid_pose_stretch` | Pyramid Pose Alignment Stretch | `—` | `pyramid_pose_parsvottanasana` | M3 |
| 41 | `wall_calf_gastrocnemius_right` | Wall Calf Stretch (Right Leg Back) | `—` | `wall_calf_gastrocnemius` | M3 |
| 42 | `wall_calf_gastrocnemius_left` | Wall Calf Stretch (Left Leg Back) | `—` | `wall_calf_gastrocnemius` | M3 |
| 43 | `wall_soleus_bent_knee` | Soleus Bent-Knee Calf Stretch | `—` | `wall_soleus_bent_knee` | M3 |
| 44 | `downward_dog_pedal` | Downward Dog Calf Pedaling | `—` | `downward_dog_pedal` | M3 |
| 45 | `plantar_fascia_toe_tuck` | Kneeling Toe Tuck Foot Stretch | `—` | `plantar_fascia_toe_tuck` | M3 |
| 46 | `wrist_flexor_extension` | Wrist Flexor Stretch (Palm Up) | `—` | `wrist_flexor_extension` | M2 |
| 47 | `wrist_extensor_flexion` | Wrist Extensor Stretch (Palm Down) | `—` | `wrist_extensor_flexion` | M2 |
| 48 | `wrist_rotations_tendon` | Wrist Figure-Eight Fluid Mobilizer | `—` | `wrist_rotations_tendon` | M2 |
| 49 | `standing_side_reach_lat` | Standing Side Lat Reach | `—` | `standing_side_reach_lat` | M2 |
| 50 | `child_pose_full_body` | Restorative Child's Pose | `—` | `restorative_child_pose_wide_knees` (wide-knee cue per R4) | M2 |
| 51 | `bhujangasana_cobra_pose` | Bhujangasana (Cobra Pose) | `भुजंगासन` (runtime) | `cobra_pose_bhujangasana` | M2 |
| 52 | `adho_mukha_svanasana_downward_dog` | Adho Mukha Svanasana (Downward-Facing Dog) | `अधो मुख श्वानासन` (runtime) | `adho_mukha_svanasana` (existing `img_stretch_downward_dog`) | M1 |
| 53 | `balasana_childs_pose` | Balasana (Child's Pose) | `बालासन` (runtime) | `childs_pose_balasana` (existing `img_stretch_child`) | M1 |
| 54 | `setu_bandhasana_bridge_pose` | Setu Bandhasana (Bridge Pose) | `सेतु बंधासन` (runtime) | `setu_bandhasana_interlaced` (regenerate interlaced-fingers cue per R4) | M4 |
| 55 | `trikonasana_triangle_right` | Trikonasana (Triangle Pose - Right) | `त्रिकोणासन` (runtime) | `trikonasana_triangle` | M4 |
| 56 | `trikonasana_triangle_left` | Trikonasana (Triangle Pose - Left) | `त्रिकोणासन` (runtime) | `trikonasana_triangle` | M4 |
| 57 | `vrikshasana_tree_right` | Vrikshasana (Tree Pose - Right) | `वृक्षासन` (runtime) | `vrikshasana_tree` (existing `img_stretch_tree`) | M1 |
| 58 | `vrikshasana_tree_left` | Vrikshasana (Tree Pose - Left) | `वृक्षासन` (runtime) | `vrikshasana_tree` (existing `img_stretch_tree`) | M1 |
| 59 | `ustrasana_camel_pose` | Ustrasana (Camel Pose) | `उष्ट्रासन` (runtime) | `ustrasana_camel_pose` | M4 |
| 60 | `paschimottanasana_seated_forward_fold` | Paschimottanasana (Seated Forward Bend) | `पश्चिमोत्तानासन` (runtime) | `paschimottanasana_seated_forward_fold` | M4 |
| 61 | `janu_sirsasana_right` | Janu Sirsasana (Head-to-Knee - Right) | `जानु शीर्षासन` (runtime) | `head_to_knee_janu_sirsasana` | M3 |
| 62 | `janu_sirsasana_left` | Janu Sirsasana (Head-to-Knee - Left) | `जानु शीर्षासन` (runtime) | `head_to_knee_janu_sirsasana` | M3 |
| 63 | `baddha_konasana_butterfly` | Baddha Konasana (Bound Angle Butterfly) | `बद्ध कोणासन` (runtime) | `seated_butterfly_baddha_konasana` | M3 |
| 64 | `supta_baddha_konasana_reclining` | Supta Baddha Konasana (Reclining Butterfly) | `सुप्त बद्ध कोणासन` (runtime) | `supta_baddha_konasana_reclining` | M4 |
| 65 | `ardha_matsyendrasana_right` | Ardha Matsyendrasana (Half Lord Twist - Right) | `अर्ध मत्स्येन्द्रासन` (runtime) | `ardha_matsyendrasana` | M4 |
| 66 | `ardha_matsyendrasana_left` | Ardha Matsyendrasana (Half Lord Twist - Left) | `अर्ध मत्स्येन्द्रासन` (runtime) | `ardha_matsyendrasana` | M4 |
| 67 | `gomukhasana_arms_right` | Gomukhasana (Cow Face Arms - Right) | `गोमुखासन` (runtime) | `gomukhasana_arms` | M4 |
| 68 | `gomukhasana_arms_left` | Gomukhasana (Cow Face Arms - Left) | `गोमुखासन` (runtime) | `gomukhasana_arms` | M4 |
| 69 | `malasana_garland_yogi_squat` | Malasana (Yogi Squat) | `मालासन` (runtime) | `malasana_garland_yogi_squat` | M4 |
| 70 | `uttanasana_forward_fold` | Uttanasana (Standing Forward Fold) | `उत्तानासन` (runtime) | `standing_forward_fold_uttanasana` | M3 |
| 71 | `virabhadrasana_one_right` | Virabhadrasana I (Warrior I - Right) | `वीरभद्रासन एक` (runtime) | `virabhadrasana_1` | M4 |
| 72 | `virabhadrasana_one_left` | Virabhadrasana I (Warrior I - Left) | `वीरभद्रासन एक` (runtime) | `virabhadrasana_1` | M4 |
| 73 | `virabhadrasana_two_right` | Virabhadrasana II (Warrior II - Right) | `वीरभद्रासन दो` (runtime) | `virabhadrasana_2` (existing `img_stretch_warrior`) | M1 |
| 74 | `virabhadrasana_two_left` | Virabhadrasana II (Warrior II - Left) | `वीरभद्रासन दो` (runtime) | `virabhadrasana_2` (existing `img_stretch_warrior`) | M1 |
| 75 | `anjaneyasana_low_lunge_right` | Anjaneyasana (Crescent Low Lunge - Right) | `आञ्जनेयासन` (runtime) | `anjaneyasana_crescent_low_lunge` | M4 |
| 76 | `anjaneyasana_low_lunge_left` | Anjaneyasana (Crescent Low Lunge - Left) | `आञ्जनेयासन` (runtime) | `anjaneyasana_crescent_low_lunge` | M4 |
| 77 | `matsyasana_fish_pose` | Matsyasana (Fish Pose) | `मत्स्यासन` (runtime) | `matsyasana_fish_pose` | M4 |
| 78 | `halasana_plow_pose` | Halasana (Plow Pose) | `हलासन` (runtime) | `halasana_plow_pose` | M4 |
| 79 | `dhanurasana_bow_pose` | Dhanurasana (Bow Pose) | `धनुरासन` (runtime) | `dhanurasana_bow_pose` | M4 |
| 80 | `supta_padangusthasana_right` | Supta Padangusthasana (Reclining Leg - Right) | `सुप्त पादांगुष्ठासन` (runtime) | `supta_padangusthasana` | M4 |
| 81 | `supta_padangusthasana_left` | Supta Padangusthasana (Reclining Leg - Left) | `सुप्त पादांगुष्ठासन` (runtime) | `supta_padangusthasana` | M4 |
| 82 | `viparita_karani_legs_wall` | Viparita Karani (Legs-Up-The-Wall) | `विपरीत करणी` (runtime) | `viparita_karani_legs_wall` | M4 |
| 83 | `shavasana_corpse_pose` | Shavasana (Corpse Restorative Pose) | `शवासन` (runtime) | `shavasana_corpse_pose` | M4 |
| 84 | `tadasana_mountain_alignment` | Tadasana (Mountain Pose Alignment) | `ताड़ासन` (runtime) | `tadasana` (existing `img_stretch_standing`) | M1 |
| 85 | `prasarita_padottanasana_wide_fold` | Prasarita Padottanasana (Wide-Leg Fold) | `प्रसारित पादोत्तानासन` (runtime) | `wide_legged_forward_fold_prasarita` | M3 |
| 86 | `garudasana_eagle_pose_right` | Garudasana (Eagle Pose - Right) | `गरुड़ासन` (runtime) | `garudasana_eagle` | M4 |
| 87 | `garudasana_eagle_pose_left` | Garudasana (Eagle Pose - Left) | `गरुड़ासन` (runtime) | `garudasana_eagle` | M4 |
| 88 | `ananda_balasana_happy_baby` | Ananda Balasana (Happy Baby Pose) | `आनंद बालासन` (runtime) | `ananda_balasana_happy_baby` | M4 |
| 89 | `salabhasana_locust_pose` | Salabhasana (Locust Pose) | `शलभासन` (runtime) | `salabhasana_locust_pose` | M4 |
| 90 | `urdhva_mukha_svanasana_updog` | Urdhva Mukha Svanasana (Upward Dog) | `ऊर्ध्व मुख श्वानासन` (runtime) | `urdhva_mukha_svanasana_updog` | M4 |
| 91 | `parsvottanasana_pyramid_right` | Parsvottanasana (Pyramid Pose - Right) | `पार्श्वोत्तानासन` (runtime) | `pyramid_pose_parsvottanasana` | M3 |
| 92 | `parsvottanasana_pyramid_left` | Parsvottanasana (Pyramid Pose - Left) | `पार्श्वोत्तानासन` (runtime) | `pyramid_pose_parsvottanasana` | M3 |
| 93 | `utkatasana_chair_pose` | Utkatasana (Chair Pose Stretch) | `उत्कटासन` (runtime) | `utkatasana_chair_pose` | M4 |
| 94 | `eka_pada_rajakapotasana_pigeon_right` | Eka Pada Rajakapotasana (Pigeon - Right) | `एक पाद राजकपोतासन` (runtime) | `pigeon_pose_prone` | M3 |
| 95 | `eka_pada_rajakapotasana_pigeon_left` | Eka Pada Rajakapotasana (Pigeon - Left) | `एक पाद राजकपोतासन` (runtime) | `pigeon_pose_prone` | M3 |
| 96 | `supta_matsyendrasana_twist_right` | Supta Matsyendrasana (Supine Twist - Right) | `सुप्त मत्स्येन्द्रासन` (runtime) | `supta_matsyendrasana_supine_twist` | M4 |
| 97 | `supta_matsyendrasana_twist_left` | Supta Matsyendrasana (Supine Twist - Left) | `सुप्त मत्स्येन्द्रासन` (runtime) | `supta_matsyendrasana_supine_twist` | M4 |
| 98 | `goddess_pose_utkata_konasana` | Utkata Konasana (Goddess Pose) | `उत्कट कोणासन` (runtime) | `goddess_pose_utkata_konasana` | M4 |
| 99 | `marjaryasana_cat_pose` | Marjaryasana (Cat Pose Spinal Flexion) | `मार्जारी आसन` (runtime) | `marjaryasana_cat_pose` | M4 |
| 100 | `bitilasana_cow_pose` | Bitilasana (Cow Pose Spinal Extension) | `बिटिलासन` (runtime) | `bitilasana_cow_pose` | M4 |
| 101 | `padmasana_lotus` | Padmasana (Lotus Pose) | `पद्मासन` (explicit) | `padmasana_lotus_full` (regenerate full both-feet-on-thighs per R4) | M5 |
| 102 | `ardha_padmasana_half_lotus` | Ardha Padmasana (Half Lotus Pose) | `अर्ध पद्मासन` (explicit) | `ardha_padmasana_half_lotus` | M5 |
| 103 | `vajrasana_thunderbolt` | Vajrasana (Thunderbolt Pose) | `वज्रासन` (explicit) | `vajrasana_thunderbolt` | M5 |
| 104 | `sukhasana_easy_pose` | Sukhasana (Easy Seated Pose) | `सुखासन` (explicit) | `sukhasana_easy_pose` | M5 |
| 105 | `siddhasana_accomplished` | Siddhasana (Accomplished Pose) | `सिद्धासन` (explicit) | `siddhasana_accomplished` | M5 |
| 106 | `chakrasana_wheel` | Chakrasana (Wheel Pose) | `चक्रासन` (explicit) | `chakrasana_wheel` | M5 |
| 107 | `sarvangasana_shoulderstand` | Sarvangasana (Shoulder Stand) | `सर्वांगासन` (explicit) | `sarvangasana_shoulderstand` | M5 |
| 108 | `sirsasana_headstand` | Sirsasana (Supported Headstand) | `शीर्षासन` (explicit) | `sirsasana_headstand` | M5 |
| 109 | `naukasana_boat` | Naukasana (Boat Pose) | `नौकासन` (explicit) | `naukasana_boat` | M5 |
| 110 | `pavanamuktasana_wind_relieving` | Pavanamuktasana (Wind-Relieving Pose) | `पवनमुक्तासन` (explicit) | `supine_double_knee_hug` | M2 |
| 111 | `makarasana_crocodile` | Makarasana (Crocodile Relaxation Pose) | `मकरासन` (explicit) | `makarasana_crocodile` | M5 |
| 112 | `bakasana_crow` | Bakasana (Crow Pose) | `बकासन` (explicit) | `bakasana_crow` | M5 |
| 113 | `mayurasana_peacock` | Mayurasana (Peacock Pose) | `मयूरासन` (explicit) | `mayurasana_peacock` | M5 |
| 114 | `chaturanga_dandasana` | Chaturanga Dandasana (Four-Limbed Staff) | `चतुरंग दंडासन` (explicit) | `chaturanga_dandasana` | M5 |
| 115 | `dandasana_staff` | Dandasana (Staff Pose) | `दंडासन` (explicit) | `dandasana_staff` | M5 |
| 116 | `ardha_chandrasana_right` | Ardha Chandrasana (Half Moon - Right) | `अर्ध चन्द्रासन` (explicit) | `ardha_chandrasana_half_moon` | M5 |
| 117 | `ardha_chandrasana_left` | Ardha Chandrasana (Half Moon - Left) | `अर्ध चन्द्रासन` (explicit) | `ardha_chandrasana_half_moon` | M5 |
| 118 | `utthita_parsvakonasana_right` | Utthita Parsvakonasana (Extended Side Angle - Right) | `उत्थित पार्श्वकोणासन` (explicit) | `utthita_parsvakonasana_extended_side_angle` | M5 |
| 119 | `utthita_parsvakonasana_left` | Utthita Parsvakonasana (Extended Side Angle - Left) | `उत्थित पार्श्वकोणासन` (explicit) | `utthita_parsvakonasana_extended_side_angle` | M5 |
| 120 | `parivrtta_trikonasana_right` | Parivrtta Trikonasana (Revolved Triangle - Right) | `परिवृत्त त्रिकोणासन` (explicit) | `parivrtta_trikonasana_revolved_triangle` | M5 |
| 121 | `parivrtta_trikonasana_left` | Parivrtta Trikonasana (Revolved Triangle - Left) | `परिवृत्त त्रिकोणासन` (explicit) | `parivrtta_trikonasana_revolved_triangle` | M5 |
| 122 | `hanumanasana_right` | Hanumanasana (Monkey Split - Right) | `हनुमानासन` (explicit) | `hanumanasana_monkey_split` | M5 |
| 123 | `hanumanasana_left` | Hanumanasana (Monkey Split - Left) | `हनुमानासन` (explicit) | `hanumanasana_monkey_split` | M5 |
| 124 | `natarajasana_right` | Natarajasana (Dancer Pose - Right) | `नटराजासन` (explicit) | `natarajasana_dancer` | M5 |
| 125 | `natarajasana_left` | Natarajasana (Dancer Pose - Left) | `नटराजासन` (explicit) | `natarajasana_dancer` | M5 |
| 126 | `virasana_hero` | Virasana (Hero Pose) | `वीरासन` (explicit) | `virasana_hero` | M5 |
| 127 | `supta_virasana_reclining_hero` | Supta Virasana (Reclining Hero Pose) | `सुप्त वीरासन` (explicit) | `supta_virasana_reclining_hero` | M5 |
| 128 | `mandukasana_frog` | Mandukasana (Frog Pose) | `मंडूकासन` (explicit) | `mandukasana_frog` | M5 |
| 129 | `simhasana_lion` | Simhasana (Lion Pose) | `सिंहासन` (explicit) | `simhasana_lion` | M5 |
| 130 | `kurmasana_tortoise` | Kurmasana (Tortoise Pose) | `कूर्मासन` (explicit) | `kurmasana_tortoise` | M5 |
| 131 | `shashankasana_hare` | Shashankasana (Hare Pose) | `शशांकासन` (explicit) | `childs_pose_balasana` (existing `img_stretch_child`) | M1 |
| 132 | `padahastasana_hand_to_foot` | Padahastasana (Hand-to-Foot Forward Fold) | `पादहस्तासन` (explicit) | `padahastasana_hand_to_foot` | M5 |
| 133 | `vasisthasana_right` | Vasisthasana (Side Plank - Right) | `वसिष्ठासन` (explicit) | `vasisthasana_side_plank` | M5 |
| 134 | `vasisthasana_left` | Vasisthasana (Side Plank - Left) | `वसिष्ठासन` (explicit) | `vasisthasana_side_plank` | M5 |
| 135 | `skandasana_right` | Skandasana (Side Lunge - Right) | `स्कंदासन` (explicit) | `skandasana_side_lunge` | M5 |
| 136 | `skandasana_left` | Skandasana (Side Lunge - Left) | `स्कंदासन` (explicit) | `skandasana_side_lunge` | M5 |
| 137 | `tolasana_scale` | Tolasana (Scale Pose) | `तोलासन` (explicit) | `tolasana_scale` | M5 |
| 138 | `bharadvajasana_right` | Bharadvajasana (Seated Sage Twist - Right) | `भरद्वाजासन` (explicit) | `bharadvajasana_sage_twist` | M5 |
| 139 | `bharadvajasana_left` | Bharadvajasana (Seated Sage Twist - Left) | `भरद्वाजासन` (explicit) | `bharadvajasana_sage_twist` | M5 |
| 140 | `upavistha_konasana_wide_angle` | Upavistha Konasana (Seated Wide-Angle Fold) | `उपविष्ट कोणासन` (explicit) | `upavistha_konasana_wide_angle` | M5 |
| 141 | `purvottanasana_upward_plank` | Purvottanasana (Upward Plank Pose) | `पूर्वोत्तानासन` (explicit) | `purvottanasana_upward_plank` | M5 |
| 142 | `parighasana_gate_right` | Parighasana (Gate Pose - Right) | `परिघासन` (explicit) | `parighasana_gate` | M5 |
| 143 | `parighasana_gate_left` | Parighasana (Gate Pose - Left) | `परिघासन` (explicit) | `parighasana_gate` | M5 |
| 144 | `virabhadrasana_3_right` | Virabhadrasana III (Warrior III - Right) | `वीरभद्रासन तीन` (explicit) | `virabhadrasana_3` | M5 |
| 145 | `virabhadrasana_3_left` | Virabhadrasana III (Warrior III - Left) | `वीरभद्रासन तीन` (explicit) | `virabhadrasana_3` | M5 |
| 146 | `phalakasana_plank` | Phalakasana (Plank Pose) | `फलकासन` (explicit) | `phalakasana_plank` | M5 |
| 147 | `surya_namaskar_flow` | Surya Namaskar (Sun Salutation Flow) | `सूर्य नमस्कार` (explicit) | `surya_namaskar_flow` (evaluate static accuracy strictly per R4!) | M5 |
| 148 | `anulom_vilom_pranayama` | Anulom Vilom (Alternate Nostril Breathing) | `अनुलोम विलोम प्राणायाम` (explicit) | `anulom_vilom_pranayama` | M5 |
| 149 | `bhramari_pranayama` | Bhramari Pranayama (Humming Bee Breath) | `भ्रामरी प्राणायाम` (explicit) | `bhramari_pranayama` | M5 |
| 150 | `ujjayi_pranayama` | Ujjayi Pranayama (Ocean Breath) | `उज्जायी प्राणायाम` (explicit) | `ujjayi_pranayama` | M5 |

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|--------------|--------|
| M1 | Core Artwork Architecture, Null-Image UI & Legacy Drawable Cleanup | Refactor `ArtworkMapper` -> `Int?` + modular `ArtworkBatch1..4` stubs; delete 15 legacy drawables; keep 5 strictly verified existing unified drawables (`child` for `#53/#131`, `downward_dog` for `#52`, `tree` for `#57/#58`, `warrior` for `#73/#74`, `standing` for `#84`); update 4 UI composables & unit/emulator tests | none | DONE |
| M2 | Unified Pose Generation Batch 1 (Upper, Core, Back, Wrists + `#1/#2` & `#50`) | Prompt spec verified (`pose_prompts_m2_m3.md`); blocked by upstream `generate_image` 503 `MODEL_CAPACITY_EXHAUSTED`; `ArtworkBatch1.kt` clean `emptyMap()` per R3/R4 | M1 | BLOCKED_EXTERNAL (503) |
| M3 | Unified Pose Generation Batch 2 (Hips, Legs, Calves, Feet) | Prompt spec verified (`pose_prompts_m2_m3.md`); blocked by upstream `generate_image` 503 `MODEL_CAPACITY_EXHAUSTED`; `ArtworkBatch2.kt` clean `emptyMap()` per R3/R4 | M1 | BLOCKED_EXTERNAL (503) |
| M4 | Unified Pose Generation Batch 3 (`StretchLibrary` Yoga Asanas + `#54`) | Prompt spec verified (`pose_prompts_m4.md`); blocked by upstream `generate_image` 503 `MODEL_CAPACITY_EXHAUSTED`; `ArtworkBatch3.kt` clean `emptyMap()` per R3/R4 | M1 | BLOCKED_EXTERNAL (503) |
| M5 | Unified Pose Generation Batch 4 (`ExtraYogaAsanas` + `#101`) | Prompt spec verified (`pose_prompts_m5.md`); blocked by upstream `generate_image` 503 `MODEL_CAPACITY_EXHAUSTED`; `ArtworkBatch4.kt` clean `emptyMap()` per R3/R4 | M1 | BLOCKED_EXTERNAL (503) |
| M6 | Local HTML Visual Gallery & Programmatic Audit | Generate `stretch_gallery.html` covering all 150 stretches in order with names, Indian/Sanskrit names, cues, and working relative image previews / null states; run programmatic completeness check | M1, M2–M5 | DONE |
| E2E | Opaque-Box E2E Testing Track | Requirement-driven Tier 1–4 test suite (`ArtworkE2ETest.kt`, `UiNoImageE2ETest.kt`, `verify_stretch_coach.py`), `TEST_INFRA.md`, `TEST_READY.md` | M1 | DONE |
| Final | Final Milestone (E2E Pass + Tier 5 Adversarial Verification) | Phase 1: pass 100% of E2E tests + `./gradlew assembleDebug testDebugUnitTest --no-daemon`; Phase 2: Tier 5 Challenger-led white-box & visual/rubric verification across all 150 items | M6, E2E | DONE |

## Interface Contracts

### `ArtworkMapper` ↔ UI Composables (`StretchItemCard`, `PlayerArtworkCard`, `AddFromLibraryDialog`, `StretchDetailDialog`)
- Signature: `@DrawableRes fun getDrawableForStretch(name: String, category: String = ""): Int?`
- Semantics:
  - Performs exact normalized name/alias lookup (case-insensitive, trimmed, whitespace-normalized) against verified pose entries only.
  - Returns `@DrawableRes Int` ONLY when a verified accurate unified-style drawable exists for that specific stretch/pose.
  - Returns `null` for unknown stretches, custom stretches without verified matches, or any stretch whose illustration failed pose/style verification.
  - NEVER falls back to category defaults or generic standing/sitting/floor drawables.
- UI Rendering Contract:
  - Compute `customFile` (`customImagePath`) and `artworkRes = ArtworkMapper.getDrawableForStretch(name, category)`.
  - If `customFile != null`: render `AsyncImage`.
  - Else if `artworkRes != null`: render `Image(painterResource(id = artworkRes), ...)`.
  - Else (`customFile == null && artworkRes == null`): omit the image container (`Box`) and its spacing (`Spacer`) completely. In `PlayerArtworkCard`, render a clean surface-themed compact header displaying Step badge, Phase badge, stretch name, and notes with accessible contrast.

### `ArtworkMapper` ↔ Modular Batch Registries (`com.example.data.artwork.ArtworkBatch1..4`)
- Each `ArtworkBatchN` object exposes:
  - `val exactNameMap: Map<String, Int>` (keys = exact lowercase trimmed `LibraryStretch.name` and normalized aliases/IDs for its assigned stretches ONLY; values = `R.drawable.img_stretch_<pose>`).
- `ArtworkMapper.kt` merges `coreReferenceMap` + `ArtworkBatch1.exactNameMap` + `ArtworkBatch2.exactNameMap` + `ArtworkBatch3.exactNameMap` + `ArtworkBatch4.exactNameMap` (or checks them in order) with zero substring fallback.
- **Disjoint Ownership Rule**: Each batch ONLY registers the exact stretches assigned to its milestone in `## Feature Inventory`, and ONLY creates drawables named `img_stretch_<canonical_pose_key>.jpg` corresponding to its assigned canonical poses.
