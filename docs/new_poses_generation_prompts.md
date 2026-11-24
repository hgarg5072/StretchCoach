# New StretchCoach Poses — Image & Video Generation Spec (Batch 5)

Use this file to generate the missing poses from your 52-step routine in the **exact same Studio Ghibli visual style** as the existing 117 poses.

---

## Global Style & Technical Anchor (Apply to All Prompts)

- **Image Specs:** `1376x768` (`16:9` widescreen), JPEG, no text, no labels, no watermarks.
- **Video Specs:** `1280x720`, `20 fps`, H.264 (`main`, `yuv420p`, `+faststart`, silent seamless loop, ~4–5s).
- **Consistent Character & Room:**
  > *Studio Ghibli-inspired hand-painted watercolor and fine ink illustration of a calm woman with dark hair tied in a neat low bun, wearing an olive-green sports bra and dark charcoal-grey leggings, barefoot, practicing on a woven beige yoga mat. Warm sunlit Japanese-style yoga studio with soft cream plaster walls, a wooden-framed window on the left with morning sunbeams, a potted terracotta Monstera plant on the left, a small wooden shelf on the back wall, and warm oak plank flooring. Clean full-body head-to-toe framing with generous studio margin on all sides so overlays fit cleanly in bottom-left and bottom-right corners.*

---

## Confirmed New Poses to Generate (3 Clips — Covers 5 Steps via Mirroring)

### 1. World's Greatest Stretch (Right Side — auto-mirrors for Left)
- **Routine Steps:** 16 (Left), 17 (Right)
- **Image Filename:** `img_stretch_b5_worlds_greatest_stretch.jpg`
- **Video Filename:** `img_stretch_b5_worlds_greatest_stretch.mp4`
- **Posture Hub:** `KNEELING`
- **Unilateral (`is_unilateral`):** `true` *(render Right leg forward; app mirrors `scaleX = -1` for Left)*
- **Image Prompt:**
  > *[Global Style Anchor]* She is in the **World's Greatest Stretch** facing right on the yoga mat: right foot planted firmly forward with right knee bent at 90 degrees directly over the ankle, left leg extended straight back with toes tucked and back knee lifted off the mat, left palm planted flat on the mat inside her right foot, torso rotated open toward the viewer with her right arm extended straight up toward the ceiling, fingers relaxed, gazing softly upward toward her raised right hand.
- **Video Motion Prompt:**
  > *Slow, peaceful 4-second looping motion: subtle chest-opening breath as her raised right arm lengthens gently toward the ceiling, hips sink slightly deeper into the lunge on the exhale, back heel pulses softly, steady grounded left hand. Camera completely static, no cuts, seamless loop.*

---

### 2. Ardha Pavanmuktasana Diagonal (Right Knee Across — auto-mirrors for Left)
- **Routine Steps:** 2 (Left), 4 (Right)
- **Image Filename:** `img_stretch_b5_ardha_pavanmuktasana_diagonal.jpg`
- **Video Filename:** `img_stretch_b5_ardha_pavanmuktasana_diagonal.mp4`
- **Posture Hub:** `SUPINE`
- **Unilateral (`is_unilateral`):** `true` *(render Right knee pulled toward Left shoulder; app mirrors for Left)*
- **Image Prompt:**
  > *[Global Style Anchor]* She is lying supine (flat on her back) on the yoga mat with her head on the right side and feet toward the left: left leg extended straight and relaxed flat along the mat, right knee bent and hugged **diagonally across the midline of her chest toward her left shoulder** using both hands clasped gently around the right shin/knee, both shoulder blades and the back of her head resting peacefully on the mat, eyes softly closed.
- **Video Motion Prompt:**
  > *Slow, calm 4-second looping motion: gentle diaphragmatic breathing as she draws the right knee a fraction closer diagonally toward the opposite shoulder on each exhale and softens slightly on the inhale. Left leg and shoulders stay grounded. Camera completely static, seamless loop.*

---

### 3. Parvatasana — Seated Mountain Pose
- **Routine Step:** 23
- **Image Filename:** `img_stretch_b5_parvatasana_seated_mountain.jpg`
- **Video Filename:** `img_stretch_b5_parvatasana_seated_mountain.mp4`
- **Posture Hub:** `SEATED`
- **Unilateral (`is_unilateral`):** `false`
- **Image Prompt:**
  > *[Global Style Anchor]* She is seated cross-legged in Easy Pose (*Sukhasana*) facing forward in the center of the yoga mat, spine tall and upright, both arms extended straight overhead alongside her ears with palms gently pressed together in Namaste (*Anjali Mudra*) above her head, shoulders relaxed away from her ears, calm serene expression with eyes softly closed.
- **Video Motion Prompt:**
  > *Slow, meditative 4-second looping motion: subtle vertical spinal lengthening and gentle ribcage expansion on the inhale as her overhead arms reach slightly taller, softening peacefully on the exhale. Camera completely static, seamless loop.*

---

## Pending Custom Poses (Fill In Once Confirmed)

### 4. Spine Alignment (Step 22)
- **Image Filename:** `img_stretch_b5_spine_alignment.jpg`
- **Video Filename:** `img_stretch_b5_spine_alignment.mp4`
- **Pose Description:** *(TBD — specify exact seated/supine/standing form)*

### 5. Scapula Stretch (Steps 26–27)
- **Image Filename:** `img_stretch_b5_scapula_stretch.jpg`
- **Video Filename:** `img_stretch_b5_scapula_stretch.mp4`
- **Unilateral (`is_unilateral`):** `true`
- **Pose Description:** *(TBD — specify exact arm/hand position or attach reference photo)*

### 6. Shoulder Stretches (Step 32)
- **Image Filename:** `img_stretch_b5_shoulder_stretches.jpg` *(or reuse `img_stretch_b1_interlaced_chest_expansion` / `img_stretch_b1_cross_body_shoulder`)*
- **Pose Description:** *(TBD)*

---

## Ready-to-Append `pose_manifest.json` Entries

```json
[
  {
    "id": "img_stretch_b5_worlds_greatest_stretch",
    "display_name": "World's Greatest Stretch",
    "video_file": "img_stretch_b5_worlds_greatest_stretch.mp4",
    "image_file": "img_stretch_b5_worlds_greatest_stretch.jpg",
    "posture_hub": "KNEELING",
    "category": "mobility",
    "is_unilateral": true,
    "recommended_dog_state": "overlay_dog_idle_rest",
    "room_layout": "standard_center"
  },
  {
    "id": "img_stretch_b5_ardha_pavanmuktasana_diagonal",
    "display_name": "Ardha Pavanmuktasana Diagonal",
    "video_file": "img_stretch_b5_ardha_pavanmuktasana_diagonal.mp4",
    "image_file": "img_stretch_b5_ardha_pavanmuktasana_diagonal.jpg",
    "posture_hub": "SUPINE",
    "category": "restorative",
    "is_unilateral": true,
    "recommended_dog_state": "overlay_dog_nap_curled",
    "room_layout": "standard_center"
  },
  {
    "id": "img_stretch_b5_parvatasana_seated_mountain",
    "display_name": "Parvatasana (Seated Mountain)",
    "video_file": "img_stretch_b5_parvatasana_seated_mountain.mp4",
    "image_file": "img_stretch_b5_parvatasana_seated_mountain.jpg",
    "posture_hub": "SEATED",
    "category": "mobility",
    "is_unilateral": false,
    "recommended_dog_state": "overlay_dog_idle_rest",
    "room_layout": "standard_center"
  }
]
```
