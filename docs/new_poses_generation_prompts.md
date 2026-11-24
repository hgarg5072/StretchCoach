# New StretchCoach Poses — Image & Video Generation Spec (Batch 5)

Use this file to generate the **5 missing poses** from your 52-step routine in the **exact same Studio Ghibli visual style** as the existing 117 poses.

- **Reference Recording on Drive:** `PXL_20261124_141104321.mp4` (`https://drive.google.com/file/d/1irYd7QxWxeF4v57BCdu8PrWBySD2bwQ1/view`)

---

## Global Style & Technical Anchor (Apply to All Prompts)

- **Image Specs:** `1376x768` (`16:9` widescreen), JPEG, no text, no labels, no watermarks.
- **Video Specs:** `1280x720`, `20 fps`, H.264 (`main`, `yuv420p`, `+faststart`, silent seamless loop, ~4–5s).
- **Consistent Character & Room:**
  > *Studio Ghibli-inspired hand-painted watercolor and fine ink illustration of a calm woman with dark hair tied in a neat low bun, wearing an olive-green sports bra and dark charcoal-grey leggings, barefoot, practicing on a woven beige yoga mat. Warm sunlit Japanese-style yoga studio with soft cream plaster walls, a wooden-framed window on the left with morning sunbeams, a potted terracotta Monstera plant on the left, a small wooden shelf on the back wall, and warm oak plank flooring. Clean full-body head-to-toe framing with generous studio margin on all sides so overlays fit cleanly in bottom-left and bottom-right corners.*

---

## 5 New Poses to Generate (Covers All Remaining Missing Steps)

### 1. World's Greatest Stretch (Right Side — auto-mirrors for Left)
- **Routine Steps:** 16 (Left), 17 (Right)
- **Image Filename:** `img_stretch_b5_worlds_greatest_stretch.jpg`
- **Video Filename:** `img_stretch_b5_worlds_greatest_stretch.mp4`
- **Posture Hub:** `KNEELING` | **Unilateral (`is_unilateral`):** `true` *(render Right leg forward; app mirrors `scaleX = -1` for Left)*
- **Image Prompt:**
  > *[Global Style Anchor]* She is in the **World's Greatest Stretch** facing right on the yoga mat: right foot planted firmly forward with right knee bent at 90 degrees directly over the ankle, left leg extended straight back with toes tucked and back knee lifted off the mat, left palm planted flat on the mat inside her right foot, torso rotated open toward the viewer with her right arm extended straight up toward the ceiling, fingers relaxed, gazing softly upward toward her raised right hand.
- **Video Motion Prompt:**
  > *Slow, peaceful 4-second looping motion: subtle chest-opening breath as her raised right arm lengthens gently toward the ceiling, hips sink slightly deeper into the lunge on the exhale, back heel pulses softly, steady grounded left hand. Camera completely static, no cuts, seamless loop.*

---

### 2. Ardha Pavanmuktasana Diagonal (Right Knee Across — auto-mirrors for Left)
- **Routine Steps:** 2 (Left), 4 (Right)
- **Image Filename:** `img_stretch_b5_ardha_pavanmuktasana_diagonal.jpg`
- **Video Filename:** `img_stretch_b5_ardha_pavanmuktasana_diagonal.mp4`
- **Posture Hub:** `SUPINE` | **Unilateral (`is_unilateral`):** `true` *(render Right knee pulled toward Left shoulder; app mirrors for Left)*
- **Image Prompt:**
  > *[Global Style Anchor]* She is lying supine (flat on her back) on the yoga mat with her head on the right side and feet toward the left: left leg extended straight and relaxed flat along the mat, right knee bent and hugged **diagonally across the midline of her chest toward her left shoulder** using both hands clasped gently around the right shin/knee, both shoulder blades and the back of her head resting peacefully on the mat, eyes softly closed.
- **Video Motion Prompt:**
  > *Slow, calm 4-second looping motion: gentle diaphragmatic breathing as she draws the right knee a fraction closer diagonally toward the opposite shoulder on each exhale and softens slightly on the inhale. Left leg and shoulders stay grounded. Camera completely static, seamless loop.*

---

### 3. Parvatasana — Seated Mountain Pose
- **Routine Step:** 23
- **Image Filename:** `img_stretch_b5_parvatasana_seated_mountain.jpg`
- **Video Filename:** `img_stretch_b5_parvatasana_seated_mountain.mp4`
- **Posture Hub:** `SEATED` | **Unilateral (`is_unilateral`):** `false`
- **Image Prompt:**
  > *[Global Style Anchor]* She is seated cross-legged in Easy Pose (*Sukhasana*) facing forward in the center of the yoga mat, spine tall and upright, both arms extended straight overhead alongside her ears with palms gently pressed together in Namaste (*Anjali Mudra*) above her head, shoulders relaxed away from her ears, calm serene expression with eyes softly closed.
- **Video Motion Prompt:**
  > *Slow, meditative 4-second looping motion: subtle vertical spinal lengthening and gentle ribcage expansion on the inhale as her overhead arms reach slightly taller, softening peacefully on the exhale. Camera completely static, seamless loop.*

---

### 4. Spine Alignment — Seated Flowing Arm-Wave Mobility
- **Routine Step:** 22
- **Reference Video Timestamp:** `00:00 – 00:15` in `PXL_20261124_141104321.mp4`
- **Image Filename:** `img_stretch_b5_spine_alignment.jpg`
- **Video Filename:** `img_stretch_b5_spine_alignment.mp4`
- **Posture Hub:** `SEATED` | **Unilateral (`is_unilateral`):** `false`
- **Exact Movement Observed in Recording:**
  - Seated cross-legged (*Sukhasana*) facing forward with tall, upright spine.
  - Both arms sweep gently in a smooth, flowing, slightly staggered alternating wave: rising from the knees/lap → forward & upward past the shoulders → high overhead with open relaxed palms → opening wide out to the sides → floating softly back down to the knees/lap, mobilizing the thoracic spine, shoulders, and ribcage.
- **Image Prompt:**
  > *[Global Style Anchor]* She is seated cross-legged (*Sukhasana*) facing forward in the center of the yoga mat with a tall, aligned spine, captured mid-flow with both arms raised gracefully upward and slightly open in a wide V above her shoulders (one arm slightly higher than the other in a gentle flowing wave), fingers soft and open, chest lifted, calm relaxed gaze forward.
- **Video Motion Prompt:**
  > *Smooth, continuous 5-second seamless loop: seated cross-legged with tall spine, her arms glide gracefully in a gentle alternating wave—sweeping up from her knees in front of her body to high overhead with open soft palms, then opening wide out to the sides and floating gently back down to her lap while her thoracic spine lengthens smoothly. Camera completely static, serene Ghibli pacing, seamless loop.*

---

### 5. Scapula Stretch — Seated Cross-Arm Diagonal Forward Fold (Right Side — auto-mirrors for Left)
- **Routine Steps:** 26 (Left), 27 (Right)
- **Reference Video Timestamp:** `00:16 – 00:30` in `PXL_20261124_141104321.mp4`
- **Image Filename:** `img_stretch_b5_scapula_stretch.jpg`
- **Video Filename:** `img_stretch_b5_scapula_stretch.mp4`
- **Posture Hub:** `SEATED` | **Unilateral (`is_unilateral`):** `true` *(render Right arm pulled across to Left side; app mirrors `scaleX = -1` for Left)*
- **Exact Movement Observed in Recording:**
  - Seated cross-legged (*Sukhasana*) facing forward.
  - Extends one arm straight forward, grasps that wrist/forearm with the opposite hand, pulls the straight arm diagonally across the body toward the opposite hip/thigh, and **folds the torso and head deeply forward and down toward the mat** while keeping the arm stretched diagonally across/beside the opposite leg—creating a deep scapular protraction, posterior deltoid, and rhomboid stretch.
- **Image Prompt:**
  > *[Global Style Anchor]* She is seated cross-legged (*Sukhasana*) on the yoga mat in a **deep diagonal cross-arm forward fold for scapular release**: her right arm is extended straight and pulled diagonally across her body toward the left side of the mat, with her left hand firmly holding her right wrist/forearm near her left hip/knee, while her upper torso and head bow deeply forward and downward over her crossed legs so her upper back rounds gently and her right shoulder blade opens wide.
- **Video Motion Prompt:**
  > *Slow, deeply relaxing 5-second seamless loop: from the seated cross-arm grip, she gently exhales to bow her torso and head slightly deeper forward toward the mat while softly lengthening the diagonal pull on her right arm to open the right shoulder blade, then eases upward slightly on the inhale. Camera completely static, smooth Ghibli motion, seamless loop.*

---

### 6. Single-Arm Wall Pectoral Opener (Right Arm on Wall — auto-mirrors for Left)
- **Routine Step:** 50 (`Doorway / Wall Pectoral Opener`)
- **Reference Style:** Standing side-on beside a wall with one forearm/arm pressed back against the wall at ~90° and turning the chest gently away from the wall to deeply open the pectoral muscle and anterior shoulder.
- **Image Filename:** `img_stretch_b5_wall_pectoral_opener.jpg`
- **Video Filename:** `img_stretch_b5_wall_pectoral_opener.mp4`
- **Posture Hub:** `STANDING` | **Unilateral (`is_unilateral`):** `true`
- **Image Prompt:**
  > *[Global Style Anchor]* She is standing sideways next to the right studio wall on the wooden floor: her right forearm and palm are pressed flat vertically against the wall with elbow bent at 90 degrees at shoulder height, while she gently steps her feet slightly forward and rotates her chest and torso open away from the wall toward the center of the studio, left arm relaxed by her side, calm expression.
- **Video Motion Prompt:**
  > *Slow, steady 4-second seamless loop: subtle deep chest-opening breath as her torso gently rotates a fraction further away from the wall on the exhale to lengthen the right pectoral and front shoulder, softening slightly on the inhale. Camera completely static, seamless loop.*

---

### 7. Empty Yoga Studio Background (Fallback Stage for Custom / Upcoming Poses)
- **Usage:** Rendered as the serene room backdrop behind the companion dog, candle warmer/lantern, corner leaves, and dust motes whenever a stretch doesn't have a dedicated pose video yet.
- **Image Filename:** `img_studio_empty_bg.jpg` *(image only, no video needed)*
- **Image Prompt:**
  > *Studio Ghibli-inspired hand-painted watercolor and fine ink illustration of an **empty** warm sunlit Japanese-style yoga studio (NO person, NO dog, NO text). Soft cream plaster walls, wooden-framed window on the left with gentle morning sunbeams, small potted plant on the windowsill, potted terracotta Monstera plant on the left wooden floor, small wooden shelf with ceramic vessels on the back wall, wooden door frame on the far right, warm oak plank flooring, and an empty flat woven beige yoga mat centered on the floor. Exact 16:9 widescreen match to the existing studio background.*

---

## Confirmation: Shoulder Stretches (`00:31 – 00:47` in Recording)

I reviewed all 4 shoulder stretches you demonstrated at `00:31–00:47` against your existing 117 verified poses:

| Timestamp | Movement Demonstrated | Status in App | Existing Asset |
| :---: | :--- | :--- | :--- |
| `00:32 – 00:36` | **Cross-Body Shoulder Stretch** (arm pulled horizontally across chest, Left & Right) | **Already Present** | `img_stretch_b1_cross_body_shoulder` (unilateral, auto-mirrors) |
| `00:42 – 00:46` | **Interlaced Hands Behind Back Chest Expansion** (hands clasped behind back, lifting chest) | **Already Present** | `img_stretch_b1_interlaced_chest_expansion` *(standing version; same movement)* |
| `00:39 – 00:41` | **Interlaced Fingers Overhead Reach (Palms Up)** | **Covered** | Covered by `img_stretch_b5_parvatasana_seated_mountain` (or `img_stretch_b1_standing_side_reach_lat`) |
| `00:37 – 00:38` | **Interlaced Fingers Forward Reach (Palms Out)** | Optional / Not separate | If you want this exact seated palms-out forward stretch as its own clip later, we can add `img_stretch_b5_seated_interlaced_reach`—otherwise `img_stretch_b1_cross_body_shoulder` + `img_stretch_b1_interlaced_chest_expansion` already cover Step 32! |

---

## Ready-to-Append `pose_manifest.json` Entries (All 5 New Poses)

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
  },
  {
    "id": "img_stretch_b5_spine_alignment",
    "display_name": "Spine Alignment (Flowing Arm Wave)",
    "video_file": "img_stretch_b5_spine_alignment.mp4",
    "image_file": "img_stretch_b5_spine_alignment.jpg",
    "posture_hub": "SEATED",
    "category": "mobility",
    "is_unilateral": false,
    "recommended_dog_state": "overlay_dog_idle_rest",
    "room_layout": "standard_center"
  },
  {
    "id": "img_stretch_b5_scapula_stretch",
    "display_name": "Scapula Diagonal Forward-Fold Stretch",
    "video_file": "img_stretch_b5_scapula_stretch.mp4",
    "image_file": "img_stretch_b5_scapula_stretch.jpg",
    "posture_hub": "SEATED",
    "category": "mobility",
    "is_unilateral": true,
    "recommended_dog_state": "overlay_dog_idle_rest",
    "room_layout": "standard_center"
  }
]
```
