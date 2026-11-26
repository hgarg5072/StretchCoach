# Stretch Coach

An offline-first Android yoga & mobility coaching app built with **Kotlin**, **Jetpack Compose**, **Room**, **DataStore**, **Google TTS**, and **OpenGL ES 2.0**, featuring a hand-painted Studio Ghibli-inspired sunlit yoga sanctuary.

- **Official Download Page (GitHub Pages + 1-Tap In-App Updates):** [https://hgarg5072.github.io/StretchCoach-Releases/](https://hgarg5072.github.io/StretchCoach-Releases/)
- **Direct Verified APK (`v1.0.0`, `47 MB`):** [GitHub Releases](https://github.com/hgarg5072/StretchCoach-Releases/releases/latest)
- **Shared Studio Assets & Reference Folder:** [Google Drive Folder](https://drive.google.com/drive/folders/1K8UG0yUtaxtDtEzZj4VGnCrVHo4nXhe-)

---

## Key Features

- **135+ Unique Bilingual Stretches & 7 Built-In Routines:** Deduplicated English + Devanagari (`हिन्दी / संस्कृत`) catalog with prep/hold cues and 7 preloaded routines — including **Nisha's Custom Stretch** (52-step morning mobility routine) and the timer-free **Surya Namaskar (सूर्य नमस्कार)** continuous flow routine with adjustable Rounds (`1–24`) and playback speed (`0.5x`–`1.25x`).
- **112 Canonical Looping Pose Videos & 12 Hub Transitions:** Smooth posture-hub transitions (`STANDING ↔ KNEELING ↔ SEATED ↔ PRONE ↔ SUPINE`), automatic left-side mirroring (and dedicated `_left.mp4` / `_right.mp4` clips where needed), and Smart Flow sorting.
- **Living Studio Atmosphere (OpenGL ES 2.0):**
  - Localized Monstera plant breeze shader (`GlBreezeVideoRenderer`) running directly on the pose video texture.
  - Real-time chroma-key studio overlays (`ChromaKeyVideoOverlay`): **3-way Studio Companion** (`Off` / **8-action Golden Puppy** / **9-action Person-Safe Roaming Calico Cat** with layout-aware floor corridors), **Auto Ghibli** lighting props (`Tea Set` in Day, `Candle Warmer` in Sunset, `Washi Lantern` with warm floor glow in Night), and **Glass Furin Wind Chime** (with automatic low-RAM device guard).
  - Event-driven `SurfaceTexture.OnFrameAvailableListener` rendering that automatically pauses decoders when the timer is paused/idle.
- **Living Empty Sanctuary for Custom Stretches:** Custom user-created stretches without a matched pose video render the full-resolution sunlit empty yoga studio backdrop (`img_stretch_empty_studio_sanctuary.webp`) with a 4s/4s meditative breathing ring and live atmosphere overlays.
- **Voice Coaching & Bundled Meditation Music:** Offline Google TTS prep/hold cues with a 5-second spoken countdown, 1:1 tick-locked progress animation, and bundled 9-minute ambient meditation music (`default_meditation_music.m4a`).
- **Full-Screen Zen Mode & Landscape Support:** Clean top-center stretch name pill (`Name • देवनागरी`), unclipped 16:9 stage for wide supine/prone poses, and 1-tap orientation toggle.

---

## Asset & APK Size Optimization (`130 MB → 45.0 MB`)

All optimized assets are tracked directly in Git under `app/src/main/`:

| Asset / Layer | Specs | Size |
| :--- | :--- | :---: |
| `app/src/main/res/drawable/` | 112 pose thumbnails (`480×270` WebP, `-q 75`) + full-res `1376×768` Empty Sanctuary (`-q 82`) | **2.2 MB** |
| `app/src/main/assets/stretch_videos/` | 111 canonical poses + L/R variants (`960×540`, 16 fps, H.264 `main`, CRF 27, `+faststart`, silent) | **27 MB** |
| `app/src/main/assets/stretch_transitions/` | 12 posture-hub transitions (`960×540`, 16 fps, H.264 `main`, CRF 27) | **2.4 MB** |
| `app/src/main/assets/stretch_overlays/` | 12 chroma-key overlays (8 puppy actions + 3 Ghibli lighting props + wind chime) | **3.3 MB** |
| `app/src/main/assets/ambient_audio/` | Bundled 9-minute meditation track (96 kbps stereo AAC `.m4a`) | **6.3 MB** |
| **Release APK (`app-release.apk`)** | R8 minification (`isMinifyEnabled = true`), resource shrinking (`isShrinkResources = true`), `arm64-v8a` & `armeabi-v7a` ABI filter | **45.0 MB** |

---

## Project Structure

```text
app/src/main/
├── assets/
│   ├── ambient_audio/          # Default bundled meditation music (.m4a)
│   ├── pose_manifest.json      # 111 canonical poses, posture hubs, overlay anchor coordinates, 12 transitions
│   ├── stretch_overlays/       # 12 chroma-key MP4 overlays (puppy, tea set, candle warmer, lantern, wind chime)
│   ├── stretch_transitions/    # 12 posture-hub transition MP4s
│   └── stretch_videos/         # Looping Studio Ghibli pose MP4s (540p, 16fps H.264)
├── res/drawable/               # WebP pose thumbnails & full-res Empty Sanctuary backdrop
└── java/com/example/
    ├── audio/                  # AmbientAudioController & SpeechController (TTS)
    ├── data/                   # Room DB, StretchLibrary, ExtraYogaAsanas, ArtworkMapper, PoseManifestRepository
    ├── ui/components/          # PlayerArtworkCard, LoopingStretchVideo, ChromaKeyVideoOverlay, StudioAtmosphereOverlay, SettingsDialog
    └── ui/screens/             # RoutineScreen, PlayerScreen, MainScreen
```

---

## Build & Test Locally

**Prerequisites:** JDK 21 and Android SDK (`compileSdk 36`, `minSdk 26`).

```bash
# Run all unit tests (ArtworkE2ETest, PoseManifestUnitTest, AmbientAudioUnitTest, ExampleUnitTest)
./gradlew testDebugUnitTest --no-daemon

# Build R8-minified Release APK (45 MB, falls back to debug keystore locally if no upload keystore is configured)
./gradlew assembleRelease --no-daemon

# Install Release APK onto a connected Android device
adb install -r app/build/outputs/apk/release/app-release.apk

# Publish a new SHA-256 verified release to GitHub Releases & GitHub Pages (1-tap in-app update for all users)
./scripts/publish_release.sh 1.1.0 2 "Release notes here"
```

Optional: copy `.env.example` to `.env` if configuring Firebase/Google Sign-In cloud backup locally.

---

## Reference Videos & Documentation

- **Recorded Custom Stretches Reference (`Spine Alignment`, `Scapula Stretch`, `Shoulder Stretches`):** https://drive.google.com/file/d/1irYd7QxWxeF4v57BCdu8PrWBySD2bwQ1/view
- **52-Step Routine Walkthrough Recording:** https://drive.google.com/file/d/1Csj_XEKOpQIu9Vn3yLiD1N_xXKCJnNpC/view
- **Batch 5 Image & Video Generation Spec:** [`docs/new_poses_generation_prompts.md`](docs/new_poses_generation_prompts.md)
- **52-Step Routine Analysis:** [`docs/whatsapp_routine_analysis.md`](docs/whatsapp_routine_analysis.md)
