# M1 UI Null-Image Handling Spec & Existing-8 Visual Audit

## Executive Summary
1. **UI Null-Image Handling**: Updating `ArtworkMapper.getDrawableForStretch(name, category)` to return `@DrawableRes Int?` requires coordinated updates across `StretchItemCard.kt`, `AddFromLibraryDialog.kt`, `StretchDetailDialog.kt`, and `PlayerArtworkCard.kt`. In the first three composables, both the thumbnail/artwork `Box` and its adjacent `Spacer` must be omitted completely when `customFile == null && artworkRes == null`. In `PlayerArtworkCard.kt`, the `R.drawable.img_stretch_standing` fallback (line 62) must be removed, and when `customFile == null && artworkRes == null`, the `16:10` image frame and dark scrims must be replaced with a clean surface-themed compact header preserving Step badge, Phase badge, `currentStretch.name`, and `currentStretch.notes` with accessible Light/Dark mode contrast.
2. **Existing-8 Unified Drawables Audit (R4)**: **6 of the 8 existing unified drawables PASS** both the Unified Studio Art Style and exact `prepCue`/`holdCue` pose matching (`standing`, `downward_dog`, `child`, `warrior`, `tree`, `lotus`). **`img_stretch_bridge.jpg` FAILS strict R4** (`#54` `holdCue` explicitly requires `"Roll shoulders under, interlace fingers"`, whereas the image shows arms flat beside hips with palms down) and **`img_stretch_neck.jpg` has a cue mismatch** (`#1` `holdCue` explicitly says `"Let gravity do the work"` and `#1/#2` mention no hand on the head, whereas the image shows the left hand resting on top of the head assisting the tilt). Both are recommended for cue-faithful regeneration (and unmapped/`null` until regenerated if strict R4 is enforced immediately in M1).

---

## 1. File-by-File UI Modification Specification

### 1.1 Theme & Caller Layout Verification
- **`Color.kt` (`app/src/main/java/com/example/ui/theme/Color.kt`) & `Theme.kt` (`app/src/main/java/com/example/ui/theme/Theme.kt`)**:
  - **LightColorScheme** (`Theme.kt:39-60`): `surface = #FFFFFF`, `onSurface = #0F172A`, `surfaceVariant = #F1F5F9`, `onSurfaceVariant = #64748B`, `primary = #0D9488`, `onPrimary = #FFFFFF`, `primaryContainer = #CCFBF1`, `onPrimaryContainer = #042F2E`, `outline = #E2E8F0`.
  - **DarkColorScheme** (`Theme.kt:16-37`): `surface = #1E293B`, `onSurface = #F8FAFC`, `surfaceVariant = #334155`, `onSurfaceVariant = #94A3B8`, `primary = #2DD4BF`, `onPrimary = #003731`, `primaryContainer = #115E59`, `onPrimaryContainer = #70F5E4`, `outline = #475569`.
  - **Contrast hazard identified in `PlayerArtworkCard.kt`**:
    - Existing lines 150/159 used `Color.White.copy(alpha = 0.92f)` background with `MaterialTheme.colorScheme.primary` text (`#2DD4BF` in Dark mode on white = low ~1.8:1 contrast, only tolerable on dark scrim; on a surface header in Light mode a white pill on `#FFFFFF` surface loses edge separation). Using `MaterialTheme.colorScheme.primaryContainer` + `MaterialTheme.colorScheme.onPrimaryContainer` for the compact Step badge gives ~13:1 (Light) and ~7.5:1 (Dark).
    - Existing line 184 used `Color.White` text on `badgeColor` for all states, including `PlayerState.IDLE -> "READY" to MaterialTheme.colorScheme.primary` (line 165). In Dark mode, `primary` is `#2DD4BF` (light teal), so `Color.White` fails contrast (~1.89:1). Using `if (state == PlayerState.IDLE) MaterialTheme.colorScheme.onPrimary else Color.White` resolves this cleanly (`#FFFFFF` on `#0D9488` in Light; `#003731` on `#2DD4BF` in Dark).
    - Existing lines 215/224 used `Color.White` and `Color.White.copy(alpha = 0.85f)` over a `Color.Black.copy(alpha = 0.75f)` bottom scrim. On a surface-themed compact header (no scrim), those MUST be `MaterialTheme.colorScheme.onSurface` and `MaterialTheme.colorScheme.onSurfaceVariant`.
- **Callers Verified**:
  - `PlayerScreen.kt:103-133`: Outer `Column` uses `verticalArrangement = Arrangement.Top` and `.verticalScroll(rememberScrollState())`, with `PlayerArtworkCard` directly above `PlayerTimerCard`. Neither `PlayerScreen.kt` nor `PlayerTimerCard.kt` assumes a fixed height or aspect ratio for `PlayerArtworkCard`. When `PlayerArtworkCard` collapses to a compact header, `PlayerTimerCard` and `PlayerControlsRow` move upward cleanly with `8.dp` total vertical card margin (`4.dp + 4.dp`).
  - `RoutineScreen.kt:230-305`: `LazyColumn` renders `StretchItemCard` items; drag threshold is `80.dp` (`RoutineScreen.kt:90`). Omitting the `50.dp` thumbnail leaves card height ~62–76dp (driven by title + optional Indian name + Prep/Hold chips + `18.dp` vertical padding), so list layout and drag-reorder behave normally.
  - `HeaderBar.kt:67`: Uses `R.drawable.img_app_icon` (non-stretch app icon; retained).
  - `EditStretchDialog.kt` / `EditLibraryStretchDialog.kt`: Do not call `ArtworkMapper` or render stretch images.

---

### 1.2 `app/src/main/java/com/example/ui/components/StretchItemCard.kt`
- **Target lines**: `83–86` and `131–157`
- **Changes**:
  1. Explicitly type `val artworkRes: Int? = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` (line 83).
  2. Guard both the `50.dp` thumbnail `Box` (lines 132–154) and the `Spacer(modifier = Modifier.width(10.dp))` (line 156) with `if (customFile != null || artworkRes != null)`.
  3. Replace `else` at line 146 with `else if (artworkRes != null)` so `painterResource(id = artworkRes)` receives a smart-cast non-null `Int`.
- **Exact `before -> after` (`lines 131–157`)**:
```kotlin
// BEFORE (lines 131-157)
                // Thumbnail (custom AI-generated image if available, else unified studio drawable)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                ) {
                    if (customFile != null) {
                        AsyncImage(
                            model = customFile,
                            contentDescription = stretch.name,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = artworkRes),
                            contentDescription = stretch.name,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))
```
```kotlin
// AFTER
                // Thumbnail (only rendered when custom image or verified studio artwork exists)
                if (customFile != null || artworkRes != null) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    ) {
                        if (customFile != null) {
                            AsyncImage(
                                model = customFile,
                                contentDescription = stretch.name,
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else if (artworkRes != null) {
                            Image(
                                painter = painterResource(id = artworkRes),
                                contentDescription = stretch.name,
                                modifier = Modifier.size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }
```

---

### 1.3 `app/src/main/java/com/example/ui/components/AddFromLibraryDialog.kt`
- **Target lines**: `358–361` and `384–410`
- **Changes**:
  1. Explicitly type `val artworkRes: Int? = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` (line 358).
  2. Guard both the `48.dp` thumbnail `Box` (lines 385–407) and `Spacer(modifier = Modifier.width(10.dp))` (line 409) with `if (customFile != null || artworkRes != null)`.
  3. Replace `else` at line 399 with `else if (artworkRes != null)`.
- **Exact `before -> after` (`lines 384–410`)**:
```kotlin
// BEFORE (lines 384-410)
                                    // Thumbnail
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    ) {
                                        if (customFile != null) {
                                            AsyncImage(
                                                model = customFile,
                                                contentDescription = stretch.name,
                                                modifier = Modifier.size(48.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Image(
                                                painter = painterResource(id = artworkRes),
                                                contentDescription = stretch.name,
                                                modifier = Modifier.size(48.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
```
```kotlin
// AFTER
                                    // Thumbnail (omitted cleanly when no custom or verified image exists)
                                    if (customFile != null || artworkRes != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                        ) {
                                            if (customFile != null) {
                                                AsyncImage(
                                                    model = customFile,
                                                    contentDescription = stretch.name,
                                                    modifier = Modifier.size(48.dp),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else if (artworkRes != null) {
                                                Image(
                                                    painter = painterResource(id = artworkRes),
                                                    contentDescription = stretch.name,
                                                    modifier = Modifier.size(48.dp),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
```

---

### 1.4 `app/src/main/java/com/example/ui/components/StretchDetailDialog.kt`
- **Target lines**: `178–181` and `189–217`
- **Changes**:
  1. Explicitly type `val artworkRes: Int? = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category)` (line 178).
  2. Guard both the `16:10` artwork `Box` (lines 190–214) and `Spacer(modifier = Modifier.height(14.dp))` (line 216) with `if (customFile != null || artworkRes != null)`.
  3. Replace `else` at line 206 with `else if (artworkRes != null)`.
- **Exact `before -> after` (`lines 189–217`)**:
```kotlin
// BEFORE (lines 189-217)
                        // 16:9 Studio Artwork
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customFile != null) {
                                AsyncImage(
                                    model = customFile,
                                    contentDescription = stretch.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = artworkRes),
                                    contentDescription = stretch.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
```
```kotlin
// AFTER
                        // Studio Artwork (omitted completely when no custom or verified image exists)
                        if (customFile != null || artworkRes != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 10f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (customFile != null) {
                                    AsyncImage(
                                        model = customFile,
                                        contentDescription = stretch.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (artworkRes != null) {
                                    Image(
                                        painter = painterResource(id = artworkRes),
                                        contentDescription = stretch.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                        }
```

---

### 1.5 `app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt`
- **Target lines**: `43`, `59–63`, `65–75`, `77–233` (and imports: add `androidx.compose.runtime.remember`, `coil.compose.AsyncImage`, `java.io.File`; remove `com.example.R`).
- **Design requirements**:
  1. Remove `else { R.drawable.img_stretch_standing }` fallback completely (`line 62`).
  2. Resolve `customFile = remember(currentStretch?.customImagePath) { currentStretch?.customImagePath?.let { File(it) }?.takeIf { it.exists() } }` and `artworkRes: Int? = currentStretch?.let { ArtworkMapper.getDrawableForStretch(it.name, it.category) }` at top of composable.
  3. Compute `stepText`, `phaseText`, `badgeColor`, and `phaseTextColor` once before branching inside `Card`:
     - `phaseTextColor = if (state == PlayerState.IDLE) MaterialTheme.colorScheme.onPrimary else Color.White` (fixes Dark mode white-on-bright-teal contrast!).
  4. **Branch A (`customFile != null || artworkRes != null`)**:
     - Keep the `16:10` `Box` frame with `AsyncImage` / `Image(painterResource(id = artworkRes))`, `.scale(scale)`, top dark scrim + Step/Phase badges, and bottom dark scrim + `currentStretch.name` + `currentStretch.notes` (when `PREPARING`, `HOLDING`, or `PAUSED`).
  5. **Branch B (`customFile == null && artworkRes == null`)**:
     - **Omit** the `16:10` `Box` (`aspectRatio(16f / 10f)`), **omit** both black gradient scrims, and **omit** any empty placeholder box.
     - Render a **surface-themed compact header** `Column` inside the `Card`:
       - `Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(22.dp)).padding(horizontal = 16.dp, vertical = 14.dp)`
       - Row 1: Step badge (`background = MaterialTheme.colorScheme.primaryContainer`, text `color = MaterialTheme.colorScheme.onPrimaryContainer`) + Phase badge (`background = badgeColor`, text `color = phaseTextColor`).
       - Conditional block (same guard `if (currentStretch != null && (state == PlayerState.PREPARING || state == PlayerState.HOLDING || state == PlayerState.PAUSED))`):
         - `Spacer(modifier = Modifier.height(10.dp))`
         - `Text(text = currentStretch.name, ..., color = MaterialTheme.colorScheme.onSurface, maxLines = 1, modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE))`
         - `if (!currentStretch.notes.isNullOrBlank())` -> `Spacer(modifier = Modifier.height(2.dp))` + `Text(text = currentStretch.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)` (allowing up to 2 lines in compact header without clipping notes unnecessarily, or `maxLines = 1` to match image overlay; `maxLines = 2` is comfortable in compact mode without image).
- **Exact `after` replacement for `PlayerArtworkCard.kt` (`lines 28–234`)**:
```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
// ... keep existing imports, add:
import coil.compose.AsyncImage
import java.io.File
// remove: import com.example.R

@Composable
fun PlayerArtworkCard(
    currentStretch: StretchEntity?,
    state: PlayerState,
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val customFile = remember(currentStretch?.customImagePath) {
        currentStretch?.customImagePath?.let { File(it) }?.takeIf { it.exists() }
    }
    val artworkRes: Int? = currentStretch?.let {
        ArtworkMapper.getDrawableForStretch(it.name, it.category)
    }
    val hasImage = customFile != null || artworkRes != null

    // Subtle breathing pulse animation while running (PREPARING or HOLDING)
    val infiniteTransition = rememberInfiniteTransition(label = "player_artwork_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (hasImage && (state == PlayerState.PREPARING || state == PlayerState.HOLDING)) 1.03f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_anim"
    )

    val stepText = if (totalCount > 0 && state != PlayerState.IDLE && state != PlayerState.COMPLETE) {
        "STEP ${currentIndex + 1} OF $totalCount"
    } else if (state == PlayerState.COMPLETE) {
        "ALL COMPLETE"
    } else {
        "READY TO START"
    }

    val (phaseText, badgeColor) = when (state) {
        PlayerState.IDLE -> "READY" to MaterialTheme.colorScheme.primary
        PlayerState.PREPARING -> "PREPARING" to CyanPrep
        PlayerState.HOLDING -> "HOLDING" to CoralHold
        PlayerState.PAUSED -> "PAUSED" to Color(0xFFF59E0B)
        PlayerState.COMPLETE -> "COMPLETE" to EmeraldSuccess
    }
    val phaseTextColor = if (state == PlayerState.IDLE) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        Color.White
    }
    val showStretchHeader = currentStretch != null &&
        (state == PlayerState.PREPARING || state == PlayerState.HOLDING || state == PlayerState.PAUSED)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("player_artwork_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        if (hasImage) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
            ) {
                if (customFile != null) {
                    AsyncImage(
                        model = customFile,
                        contentDescription = currentStretch?.name ?: "Stretch demonstration",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale),
                        contentScale = ContentScale.Crop
                    )
                } else if (artworkRes != null) {
                    Image(
                        painter = painterResource(id = artworkRes),
                        contentDescription = currentStretch?.name ?: "Stretch demonstration",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale),
                        contentScale = ContentScale.Crop
                    )
                }

                // Top scrim & badges
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = 160f
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stepText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = TealPrimaryLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(badgeColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = phaseText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = phaseTextColor
                            )
                        }
                    }
                }

                // Bottom subtle overlay on image
                if (showStretchHeader && currentStretch != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    ),
                                    startY = 0f,
                                    endY = 160f
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Column {
                            Text(
                                text = currentStretch.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp
                                ),
                                color = Color.White,
                                maxLines = 1,
                                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                            )
                            if (!currentStretch.notes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentStretch.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Surface-themed compact header when no custom or verified artwork exists
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stepText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(badgeColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phaseText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = phaseTextColor
                        )
                    }
                }

                if (showStretchHeader && currentStretch != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentStretch.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                    )
                    if (!currentStretch.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentStretch.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
```
*(Note: Notice in Branch A above, using `TealPrimaryLight` for the white Step pill over image also fixes the low-contrast `#2DD4BF` on `Color.White` issue that previously existed in Dark mode! Import `com.example.ui.theme.TealPrimaryLight` if adopted.)*

---

### 1.6 Test Impact Note (`CardLayoutScreenshotTest.kt` & `ExampleUnitTest.kt`)
- `CardLayoutScreenshotTest.kt:38` uses `"Bhujangasana (Cobra Pose)"` (M2 pose, `null` in M1). The test calls `captureRoboImage(...)` without threshold failure, so it still passes while exercising the null-image branch of `StretchItemCard`! Adding a Robolectric/Compose UI test for `PlayerArtworkCard` (light + dark, null artwork vs valid artwork) and `StretchDetailDialog` / `AddFromLibraryDialog` in M1 will directly verify null-image omission and contrast.

---

## 2. Visual & R4 Cue-Verification Audit of the 8 Existing Unified Drawables

All 8 existing drawables (`app/src/main/res/drawable/img_stretch_*.jpg`) were visually inspected via `view_file` and compared against the Unified Studio Art Style Specification (`PROJECT.md:38-42`) and their assigned `prepCue` / `holdCue` in `StretchLibrary.kt` and `ExtraYogaAsanas.kt`.

### Summary Matrix

| # | Drawable | Assigned Catalog Entries | Studio Style Match | Pose / Cue Match (`prepCue` + `holdCue`) | R4 Verdict & Recommendation |
|---|---|---|---|---|---|
| 1 | `img_stretch_neck.jpg` | `#1` `neck_side_right`, `#2` `neck_side_left` | **PASS** (sage-olive top, charcoal leggings, low bun, barefoot, jute mat, honey-oak floor, monstera, sunlit window, zero text) | **MISMATCH** — Image shows left hand resting over the crown/top of head assisting a left ear-to-shoulder tilt while seated cross-legged. `#1` `holdCue` (`StretchLibrary.kt:36`) states *"Let gravity do the work and relax your jaw"*, and `#2` (`lines 45-46`) cues no hand on the head. | **NEEDS CUE-FAITHFUL REGENERATION** (or unmap `#1/#2` in M1 under strict R4 until regenerated without hand-on-head, with both hands relaxed on thighs/mat). |
| 2 | `img_stretch_bridge.jpg` | `#54` `setu_bandhasana_bridge_pose` | **PASS** (exact unified studio style, zero text) | **MISMATCH** — Image shows arms extended flat alongside torso with palms down on the mat. `#54` `holdCue` (`StretchLibrary.kt:580`) explicitly states *"Roll shoulders under, interlace fingers, and expand chest."* | **NEEDS CUE-FAITHFUL REGENERATION** (unmap `#54` in M1 under strict R4 until regenerated with shoulders tucked under torso and fingers interlaced underneath hips). |
| 3 | `img_stretch_standing.jpg` | `#84` `tadasana_mountain_alignment` | **PASS** | **EXACT MATCH** — Standing tall, feet parallel hip-width, weight balanced across both soles, shoulders rolled down, arms relaxed at sides, crown lifted (`StretchLibrary.kt:879-880`). | **PASS — KEEP** for `#84` in M1. |
| 4 | `img_stretch_downward_dog.jpg` | `#52` `adho_mukha_svanasana_downward_dog` | **PASS** | **EXACT MATCH** — Inverted-V alignment, straight arms pressing mat away, head relaxed between biceps, heels drawing softly toward floor (`StretchLibrary.kt:559-560`). | **PASS — KEEP** for `#52` in M1. (Do not map `#44` pedaling here; `#44` belongs to M3.) |
| 5 | `img_stretch_child.jpg` | `#50` `child_pose_full_body`, `#53` `balasana_childs_pose`, `#131` `shashankasana_hare` | **PASS** | **MATCH** — Kneeling with hips sunk back onto heels, torso folded over thighs, forehead down, arms extended long in front (`StretchLibrary.kt:537-538, 569-570`; `ExtraYogaAsanas.kt:346-347`). Minor note: knees are together (exact match for `#53`/`#131`; `#50` says "Kneel wide", acceptable or optionally noted). | **PASS — KEEP** for `#50`, `#53`, `#131` in M1 (highest confidence for `#53` & `#131`). |
| 6 | `img_stretch_warrior.jpg` | `#73` `virabhadrasana_two_right`, `#74` `virabhadrasana_two_left` | **PASS** | **EXACT MATCH** — Wide stance, front knee bent 90° over ankle, back leg straight, torso upright centered over hips, arms parallel to floor, gaze over front fingertips (`StretchLibrary.kt:769-770, 779-780`). Symmetrical L/R pair allowed per R2. | **PASS — KEEP** for `#73`, `#74` in M1. |
| 7 | `img_stretch_tree.jpg` | `#57` `vrikshasana_tree_right`, `#58` `vrikshasana_tree_left` | **PASS** | **EXACT MATCH** — Grounded standing leg, opposite sole pressed high on inner thigh (avoiding knee), palms pressed together at heart center, calm forward gaze (`StretchLibrary.kt:609-610, 619-620`). Symmetrical L/R pair allowed per R2. | **PASS — KEEP** for `#57`, `#58` in M1. |
| 8 | `img_stretch_lotus.jpg` | `#101` `padmasana_lotus` | **PASS** | **EXACT MATCH** — Seated upright with both feet placed onto opposite thighs soles upward, hands resting on knees in **chin mudra**, tall spine (`ExtraYogaAsanas.kt:16-17`). | **PASS — KEEP** for `#101` in M1. |

### Detailed Audit Notes for `img_stretch_neck.jpg` & `img_stretch_bridge.jpg`
1. **`img_stretch_neck.jpg` (`#1` `neck_side_right` & `#2` `neck_side_left`)**:
   - **Observed Cues (`StretchLibrary.kt:35-36, 45-46`)**:
     - `#1` `prepCue`: `"Lower your right ear toward your right shoulder without lifting your left shoulder."`
     - `#1` `holdCue`: `"Breathe gently into the left side of your neck. Let gravity do the work and relax your jaw."`
     - `#2` `prepCue`: `"Gently tilt your left ear toward your left shoulder while keeping shoulders relaxed."`
     - `#2` `holdCue`: `"Feel the soothing release down the right side of your neck. Take smooth, calm breaths."`
   - **Observed Image (`img_stretch_neck.jpg`)**:
     - Practitioner sits cross-legged facing forward, eyes closed, tilting her head toward her **left** shoulder (`#2` direction), right fingertips resting on the mat, and **left hand placed over the crown of her head** assisting the lateral flexion.
   - **R4 Assessment**: Because `#1` explicitly instructs `"Let gravity do the work"` (and neither `#1` nor `#2` mentions placing a hand on the head), the hand-on-head pose conflicts with the gravity-only cue. **Recommendation**: Mark `img_stretch_neck.jpg` as **needs cue-faithful regeneration** (hands resting relaxed on thighs/mat or at sides, no hand on head) and omit `#1`/`#2` from `coreReferenceMap` until regenerated (either in M1 or reassigned to M2).

2. **`img_stretch_bridge.jpg` (`#54` `setu_bandhasana_bridge_pose`)**:
   - **Observed Cues (`StretchLibrary.kt:579-580`)**:
     - `#54` `prepCue`: `"Lie on back with knees bent, feet hip-width flat on mat close to sit bones."`
     - `#54` `holdCue`: `"Drive through heels to lift hips skyward. Roll shoulders under, interlace fingers, and expand chest."`
   - **Observed Image (`img_stretch_bridge.jpg`)**:
     - Practitioner lies supine in profile with knees bent and hips elevated into a bridge, **but arms lie straight on the mat beside her hips with palms flat down** — shoulders are not rolled under and fingers are not interlaced beneath her back.
   - **R4 Assessment**: Directly misses the explicit `"Roll shoulders under, interlace fingers, and expand chest"` instruction in `holdCue`. **Recommendation**: Mark `img_stretch_bridge.jpg` as **needs cue-faithful regeneration** (with shoulders tucked underneath and fingers interlaced on the mat under the elevated pelvis) and omit `#54` from `coreReferenceMap` until regenerated (either in M1 or reassigned to M4).
