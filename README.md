<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/e03c86f2-0daa-415a-81b3-6bcb91bb144d

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.

## Reference Videos & Asset Specs

- **Recorded Custom Stretches Reference (`Spine Alignment`, `Scapula Stretch`, `Shoulder Stretches`):** https://drive.google.com/file/d/1irYd7QxWxeF4v57BCdu8PrWBySD2bwQ1/view
- **52-Step Routine Walkthrough Recording:** https://drive.google.com/file/d/1Csj_XEKOpQIu9Vn3yLiD1N_xXKCJnNpC/view
- **Shared Studio Assets & APK Folder:** https://drive.google.com/drive/folders/1K8UG0yUtaxtDtEzZj4VGnCrVHo4nXhe-
- **Batch 5 Image & Video Generation Spec:** [`docs/new_poses_generation_prompts.md`](docs/new_poses_generation_prompts.md)
- **52-Step Routine Analysis:** [`docs/whatsapp_routine_analysis.md`](docs/whatsapp_routine_analysis.md)
