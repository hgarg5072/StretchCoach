# Dispatch Log

## 2026-11-23T14:01:56Z

Sender: 5ae8a7bd-13f4-42e5-a611-0425ca372b45 (Sentinel)
Priority: MESSAGE_PRIORITY_HIGH

Update the Stretch Coach Android app so every stretch in the 150-item library (`StretchLibrary` and `ExtraYogaAsanas`) is audited and paired with an accurate, unified-style illustration (or shows no image at all if unverified, instead of a wrong or generic fallback), and deliver a thorough local HTML visual gallery review sheet listing every stretch's name, Indian/Sanskrit name, and image link/preview. Animations are deferred to a future next step and are out of scope for this task.

Working directory: /Users/hn/antigravity/Stretch Coach
Integrity mode: demo
The team must NOT: Copy or reuse artwork from existing open-source projects, stock image packs, or external illustration sources (external images may be used as pose reference only).

## Verification Resources
- Existing unified studio art-style reference drawables in `app/src/main/res/drawable/`: `img_stretch_neck.jpg`, `img_stretch_standing.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_warrior.jpg`, `img_stretch_lotus.jpg`, `img_stretch_tree.jpg`, `img_stretch_bridge.jpg`, `img_stretch_child.jpg`.
- Stretch pose definitions, cues (`prepCue`, `holdCue`), and Indian names in `app/src/main/java/com/example/data/StretchLibrary.kt` and `app/src/main/java/com/example/data/ExtraYogaAsanas.kt`.

## Requirements

### R1. Local HTML Visual Gallery Review Sheet
Create and deliver a local HTML visual gallery file in the project covering all 150 stretches. Each entry must include the stretch name, its Indian/Sanskrit name (when present), and its linked/embedded image preview (or clearly indicate when no verified image is assigned).

### R2. Accurate Unified-Style Image Audit & Generation
Audit all 150 stretches and target generating accurate unified-style illustrations across the full library:
- Keep any of the 8 existing unified-style drawables only for the specific pose(s) they accurately depict.
- Replace all 15 older mixed-style drawables and any mismatched, wrong, or overly broad shared mappings with newly generated illustrations matching the unified studio style (using the 8 existing unified drawables as the style reference).
- Symmetrical Left/Right stretch pairs may share one accurate unified-style pose image.

### R3. No Wrong or Generic Fallback Images in the App
Update the app's stretch-to-artwork mapping and UI so that if a stretch does not have a verified accurate image, the app displays no image at all (omitting the image area rather than showing a wrong pose or generic standing/sitting fallback).

### R4. Pose & Style Verification
Verify every mapped and generated image against both the stretch's pose cues and the unified studio style reference before mapping it in the app. Any stretch whose image cannot be verified as accurate must remain unmapped (no image) rather than using an inaccurate or off-style image.

## Acceptance Criteria

### Visual Gallery & Catalog Completeness
- [ ] A local HTML visual gallery file exists in the repository and renders all 150 stretches in order, displaying each stretch's name, Indian/Sanskrit name (where defined), and a working relative image link/preview (or explicit "no image" state if unmapped).
- [ ] A programmatic check confirms every entry in `StretchLibrary` (100 items) and `ExtraYogaAsanas` (50 items) appears in the HTML gallery and every linked image path resolves to an existing image file.

### Image Accuracy, Unified Style & App Mapping
- [ ] None of the 15 older mixed-style drawables remain mapped to any stretch in the app, and no generic catch-all standing/sitting fallback is returned for unmapped stretches.
- [ ] Every stretch mapped to an image in the app is verified by an independent visual/rubric check (agent-as-judge) to match both (a) the anatomical pose described by that stretch's `prepCue`/`holdCue` and (b) the unified studio art style of the 8 reference drawables, with no text overlays or visual artifacts.
- [ ] For any stretch without a verified accurate image, the artwork lookup returns no image (`null`) and the app UI omits the image area cleanly without crashing or showing a generic pose.

### Build & Tests
- [ ] `./gradlew assembleDebug testDebugUnitTest --no-daemon` (with `JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"` and `ANDROID_HOME="$HOME/Library/Android/sdk"`) succeeds with all unit tests passing, including updated artwork-mapping tests covering valid mappings and the no-image behavior.
