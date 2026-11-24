# Handoff Report — Explorer 2 (`teamwork_explorer_m1_2`) — Milestone M1

**Handoff Type**: Hard
**Detailed UI Spec & Audit**: `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_m1_2/m1_ui_plan.md`

---

## 1. Observation
- **All UI artwork call sites (`grep_search` for `ArtworkMapper`, `customImagePath`, `R.drawable` across `app/src`)**:
  - `StretchItemCard.kt:83-86, 132-156`: Resolves `artworkRes = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` and `customFile`; unconditionally renders a `50.dp` `Box` (`background(MaterialTheme.colorScheme.surfaceVariant)`) and `Spacer(width(10.dp))` before the text `Column(Modifier.weight(1f))`.
  - `AddFromLibraryDialog.kt:358-361, 385-409`: Resolves `artworkRes` and `customFile` inside `LazyColumn` `itemsIndexed`; unconditionally renders a `48.dp` `Box` (`background(MaterialTheme.colorScheme.surface)`) and `Spacer(width(10.dp))` before the text `Column(Modifier.weight(1f))`.
  - `StretchDetailDialog.kt:178-181, 190-216`: Resolves `artworkRes` and `customFile` inside `HorizontalPager`; unconditionally renders a `fillMaxWidth().aspectRatio(16f / 10f)` `Box` and `Spacer(height(14.dp))` above the stretch title `Text`.
  - `PlayerArtworkCard.kt:59-63, 88-232`:
    - Line 62 falls back to `R.drawable.img_stretch_standing` when `currentStretch == null`.
    - Lines 88-232 unconditionally render an `aspectRatio(16f / 10f)` `Box` with `AsyncImage`/`Image`, top black gradient scrim (`Color.Black.copy(alpha = 0.6f)`), white Step pill (`Color.White.copy(alpha = 0.92f)` + `MaterialTheme.colorScheme.primary`), Phase badge (`Color.White` text on `badgeColor`, where `IDLE` uses `MaterialTheme.colorScheme.primary`), and bottom black gradient scrim with `Color.White` `currentStretch.name` and `Color.White.copy(alpha = 0.85f)` `currentStretch.notes` (lines 191-230).
  - `HeaderBar.kt:67` uses `R.drawable.img_app_icon` (non-stretch launcher/brand icon). No other UI file calls `ArtworkMapper` or renders `customImagePath`.
- **Theme & Callers**:
  - `Theme.kt:16-60` & `Color.kt:6-48`: Light (`surface = #FFFFFF`, `onSurface = #0F172A`, `onSurfaceVariant = #64748B`, `primary = #0D9488`, `onPrimary = #FFFFFF`, `primaryContainer = #CCFBF1`, `onPrimaryContainer = #042F2E`) and Dark (`surface = #1E293B`, `onSurface = #F8FAFC`, `onSurfaceVariant = #94A3B8`, `primary = #2DD4BF`, `onPrimary = #003731`, `primaryContainer = #115E59`, `onPrimaryContainer = #70F5E4`).
  - `PlayerScreen.kt:103-133`: `Column(verticalArrangement = Arrangement.Top)` with `.verticalScroll(rememberScrollState())`; `PlayerArtworkCard` sits directly above `PlayerTimerCard` with no fixed height or weight constraints.
  - `RoutineScreen.kt:90, 230-305`: `LazyColumn` of `StretchItemCard` items; drag threshold is `80.dp`.
  - `CardLayoutScreenshotTest.kt:38, 66`: Tests `StretchItemCard` using `"Bhujangasana (Cobra Pose)"` (M2 pose -> `null` in M1) and calls `captureRoboImage(...)`.
- **Visual Inspection of All 8 Existing Unified Drawables (`app/src/main/res/drawable/img_stretch_*.jpg`) vs Catalog Cues**:
  - `img_stretch_neck.jpg` vs `#1`/`#2` (`StretchLibrary.kt:35-36, 45-46`): Practitioner seated cross-legged tilting head left with **left hand on top of head** assisting tilt; `#1` `holdCue` says *"Let gravity do the work and relax your jaw"*, and neither `#1` nor `#2` cues a hand on the head.
  - `img_stretch_bridge.jpg` vs `#54` (`StretchLibrary.kt:579-580`): Practitioner in supine bridge with **arms lying flat on the mat beside torso, palms down**; `#54` `holdCue` explicitly instructs *"Roll shoulders under, interlace fingers, and expand chest."*
  - `img_stretch_standing.jpg` (`#84`, `StretchLibrary.kt:879-880`), `img_stretch_downward_dog.jpg` (`#52`, `StretchLibrary.kt:559-560`), `img_stretch_child.jpg` (`#50` `537-538`, `#53` `569-570`, `#131` `ExtraYogaAsanas.kt:346-347`), `img_stretch_warrior.jpg` (`#73`/`#74`, `StretchLibrary.kt:769-780`), `img_stretch_tree.jpg` (`#57`/`#58`, `StretchLibrary.kt:609-620`), `img_stretch_lotus.jpg` (`#101`, `ExtraYogaAsanas.kt:16-17`): All match unified studio style and anatomical pose cues.

---

## 2. Logic Chain
1. Changing `ArtworkMapper.getDrawableForStretch` to return `@DrawableRes Int?` makes `painterResource(id = artworkRes)` fail compilation unless guarded, and leaving an unguarded `Box` + `Spacer` would show empty placeholder containers violating R3.
2. Wrapping both `Box` and `Spacer` inside `if (customFile != null || artworkRes != null)` in `StretchItemCard.kt`, `AddFromLibraryDialog.kt`, and `StretchDetailDialog.kt` eliminates both the compile error and any empty space/placeholder boxes. Because `RoutineScreen.kt` and `StretchDetailDialog.kt` use flexible `Row(Modifier.weight(1f))` and scrollable `Column` layouts, omitting the image collapses cleanly.
3. In `PlayerArtworkCard.kt`, removing `else { R.drawable.img_stretch_standing }` (line 62) and branching on `hasImage = customFile != null || artworkRes != null` preserves the `16:10` overlaid artwork when an image exists, while replacing it with a compact surface-themed `Column` when `hasImage == false`. Replacing `Color.White` text/pills with `MaterialTheme.colorScheme` tokens (`primaryContainer`/`onPrimaryContainer` for Step badge, `onPrimary` for `IDLE` Phase badge, `onSurface` for `currentStretch.name`, `onSurfaceVariant` for `currentStretch.notes`) guarantees accessible contrast in both Light and Dark themes. Because `PlayerScreen.kt` uses `Arrangement.Top` + `verticalScroll`, `PlayerTimerCard` moves up cleanly.
4. Under R4 (`ORIGINAL_REQUEST.md:30, 40`), mapped images must match `prepCue`/`holdCue` with no contradiction. `img_stretch_bridge.jpg` omits the explicit `"Roll shoulders under, interlace fingers"` cue (`#54`), and `img_stretch_neck.jpg` uses hand-over-head assistance contradicting `"Let gravity do the work"` (`#1`). Therefore, 6 of the 8 existing drawables pass R4 as-is, while `img_stretch_neck.jpg` and `img_stretch_bridge.jpg` require cue-faithful regeneration (and `#1/#2` and `#54` should remain unmapped/`null` in M1 until regenerated).

---

## 3. Caveats
- No source files were modified (read-only exploration). Contrast ratios were derived analytically from `Color.kt`/`Theme.kt` hex values rather than measured via an automated runtime accessibility scanner.
- In `img_stretch_child.jpg`, practitioner knees are together (exact match for `#53` *Balasana* and `#131` *Shashankasana*; `#50` *Restorative Child's Pose* `prepCue` mentions *"Kneel wide"* as a minor variation). If ultra-strict interpretation is applied to `#50`, `#50` could get a wide-knee variant in M2, though knees-together Child's Pose is anatomically standard and accurate.

---

## 4. Conclusion
1. **UI Implementer** should apply the exact edits specified in `m1_ui_plan.md` across `StretchItemCard.kt`, `AddFromLibraryDialog.kt`, `StretchDetailDialog.kt`, and `PlayerArtworkCard.kt` (removing `R.drawable.img_stretch_standing` fallback and adding the surface-themed compact header with Light/Dark safe `colorScheme` tokens).
2. **M1 Artwork Mapping (`ArtworkMapper.kt`)** should keep **6 of the 8** existing unified drawables (`img_stretch_standing.jpg`, `img_stretch_downward_dog.jpg`, `img_stretch_child.jpg`, `img_stretch_warrior.jpg`, `img_stretch_tree.jpg`, `img_stretch_lotus.jpg`) and **flag `img_stretch_neck.jpg` (`#1/#2`) and `img_stretch_bridge.jpg` (`#54`) for cue-faithful regeneration** (leaving them unmapped/`null` in M1 under strict R4 until regenerated).

---

## 5. Verification Method
- Inspect `/Users/hn/antigravity/Stretch Coach/.agents/teamwork/teamwork_explorer_m1_2/m1_ui_plan.md` for line-accurate `before -> after` snippets and the 8-drawable matrix.
- After M1 implementation, run:
  ```bash
  JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home" \
  ANDROID_HOME="$HOME/Library/Android/sdk" \
  ./gradlew assembleDebug testDebugUnitTest --no-daemon
  ```
- Verify via Roborazzi / Compose UI tests (in both `AppTheme.LIGHT` and `AppTheme.DARK`) that:
  1. Unmapped stretches omit the thumbnail/artwork `Box` and `Spacer` in `StretchItemCard`, `AddFromLibraryDialog`, and `StretchDetailDialog`.
  2. `PlayerArtworkCard` with `currentStretch == null` or `artworkRes == null` renders the compact surface header (Step badge, Phase badge, name, notes when active) with zero `16:10` image frame and legible Light/Dark contrast.
- **Invalidation conditions**: If `PlayerScreen.kt` or `PlayerArtworkCard` design changes to show `currentStretch.name` during `PlayerState.IDLE`, update `showStretchHeader` in `m1_ui_plan.md` §1.5 accordingly.
