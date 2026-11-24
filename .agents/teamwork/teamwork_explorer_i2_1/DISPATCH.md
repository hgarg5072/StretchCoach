## 2026-11-23T14:46:58Z
From: parent (11858d5d-e7ac-4d45-9f42-d5f95d0ae4b0)
You are Explorer 1 (`teamwork_explorer_i2_1`) for Iteration 2 (Pose Prompt Specification for M2 & M3) of Stretch Coach.
Your working directory: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_1`
Project root: `/Users/hn/antigravity/Stretch Coach`

MANDATORY FIRST STEP: Read `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/ORIGINAL_REQUEST.md` and `/Users/hn/antigravity/Stretch Coach/PROJECT.md` completely, plus:
- `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_survey_2/catalog_150.md`
- `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_survey_3/drawables_audit.md` (§2 Unified Studio Style Spec)

Scope (Read-Only — do NOT generate images or modify source code):
1. Design exact, cue-faithful `generate_image` prompt specifications for all **41** canonical poses assigned to **M2 (24 poses, 30 stretches)** and **M3 (17 poses, 30 stretches)** in `PROJECT.md`:
   - **M2 (`img_stretch_b1_<key>.jpg`, `ArtworkBatch1.kt`)**: `#1/#2` (hands-free gravity-only neck side tilt, NO hand on head!), `#3`, `#4/#5`, `#6`, `#7`, `#8`, `#9` (forearm flat against studio wall at 90°), `#10`, `#11`, `#12`, `#13` (note `#13` is Cat-Cow flow — clearly show classic arched/transition or neutral-to-cat-cow mindful tabletop pose matching cues without split frames/text!), `#14/#15`, `#16`, `#17` (+ `#110` Pavanamuktasana), `#18`, `#19/#20`, `#21`, `#22`, `#23` (+ `#51` Bhujangasana gentle cobra with slight elbow bend and hips grounded), `#46`, `#47`, `#48`, `#49`, `#50` (Restorative Child's Pose with KNEES WIDE!).
   - **M3 (`img_stretch_b2_<key>.jpg`, `ArtworkBatch2.kt`)**: `#24/#25`, `#26/#27`, `#28` (+ `#63`), `#29/#30` (+ `#94/#95`), `#31`, `#32`, `#33` (+ `#70`), `#34`, `#35` (+ `#61/#62`), `#36/#37`, `#38`, `#39` (+ `#85`), `#40` (+ `#91/#92`), `#41/#42`, `#43`, `#44` (Downward Dog Calf Pedaling — clearly show one heel pressed down and the opposite knee bent/heel lifted in alternating pedal!), `#45`.
2. For EVERY canonical pose, specify:
   - Exact `drawable_filename` (`^[a-z][a-z0-9_]*\.jpg$`, strictly `img_stretch_b1_*` for M2 and `img_stretch_b2_*` for M3)
   - Exact normalized keys (both lowercase trimmed `name` and `id` for all stretches in that canonical pose)
   - Exact `ImagePaths` (pick 2 matching reference drawables from `app/src/main/res/drawable/img_stretch_*.jpg`)
   - Exact `generate_image` prompt enforcing the Unified Studio Art Style (young woman, espresso brown hair in neat low bun with center part, warm fair-to-light-olive skin, sage-olive sleeveless sports bra/crop top, charcoal high-waisted ankle-length leggings, barefoot, beige woven jute yoga mat on honey-oak wood planks, sunlit cream-plaster studio with terracotta potted plants, fine dark ink outlines + soft matte watercolor shading, centered full-body 16:9 composition with generous margins, ZERO text/watermarks/split-panels) + precise anatomical details from `prepCue`/`holdCue`.
   - Flag any stretch where a single static illustration cannot accurately depict the cue under R4.

Output Requirements:
- Maintain `progress.md` (`Last visited: <UTC>`).
- Write `pose_prompts_m2_m3.md` and `handoff.md` in `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_i2_1/`.
- Send concise summary + paths to parent (`11858d5d-e7ac-4d45-9f42-d5f95d0ae4b0`) via `send_message`.
