package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.data.AppTheme
import com.example.data.LibraryStretchEntity
import com.example.data.StretchEntity
import com.example.ui.PlayerState
import com.example.ui.components.AddFromLibraryDialog
import com.example.ui.components.PlayerArtworkCard
import com.example.ui.components.StretchDetailDialog
import com.example.ui.components.StretchItemCard
import com.example.ui.theme.StretchCoachTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import android.graphics.Bitmap
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class UiNoImageE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTempJpegFile(): File {
        val file = File.createTempFile("custom_stretch_e2e_", ".jpg")
        val bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        bitmap.recycle()
        file.deleteOnExit()
        return file
    }

    @Test
    fun stretchItemCard_nullArtwork_omitsImageAndShowsDetails() {
        val unmapped = StretchEntity(
            id = 101L,
            name = "Custom Unmapped Mobility",
            indianName = "योग आसन",
            category = "Yoga Asanas",
            preparationSeconds = 8,
            holdSeconds = 30,
            customImagePath = null
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                Surface {
                    StretchItemCard(
                        stretch = unmapped,
                        index = 0,
                        totalCount = 1,
                        onToggleEnabled = {},
                        onToggleVoiceCue = {},
                        onEdit = {},
                        onDelete = {},
                        onPlaySingle = {}
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onNodeWithTag("stretch_card_101").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Unmapped Mobility").assertIsDisplayed()
        composeTestRule.onNodeWithText("योग आसन").assertIsDisplayed()
        composeTestRule.onNodeWithText("Prep\u00A08s").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Custom Unmapped Mobility").assertDoesNotExist()
    }

    @Test
    fun stretchItemCard_mappedOrCustomArtwork_rendersImage() {
        val mapped = StretchEntity(
            id = 102L,
            name = "Tadasana (Mountain Pose Alignment)",
            category = "Yoga Asanas",
            preparationSeconds = 6,
            holdSeconds = 30
        )
        val customTempFile = createTempJpegFile()
        val custom = StretchEntity(
            id = 103L,
            name = "Custom Uploaded Pose XYZ",
            category = "Custom",
            preparationSeconds = 8,
            holdSeconds = 25,
            customImagePath = customTempFile.absolutePath
        )

        try {
            composeTestRule.mainClock.autoAdvance = false
            composeTestRule.setContent {
                StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                    Surface {
                        Column {
                            StretchItemCard(
                                stretch = mapped,
                                index = 0,
                                totalCount = 2,
                                onToggleEnabled = {},
                                onToggleVoiceCue = {},
                                onEdit = {},
                                onDelete = {},
                                onPlaySingle = {}
                            )
                            StretchItemCard(
                                stretch = custom,
                                index = 1,
                                totalCount = 2,
                                onToggleEnabled = {},
                                onToggleVoiceCue = {},
                                onEdit = {},
                                onDelete = {},
                                onPlaySingle = {}
                            )
                        }
                    }
                }
            }
            composeTestRule.mainClock.advanceTimeBy(100)

            composeTestRule.onNodeWithContentDescription("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Custom Uploaded Pose XYZ").assertExists()
        } finally {
            customTempFile.delete()
        }
    }

    @Test
    fun playerArtworkCard_nullArtwork_omitsImageAndPreservesBadgesAndText_lightAndDark() {
        val unmapped = StretchEntity(
            id = 201L,
            name = "Custom Breathing Flow",
            category = "Custom",
            notes = "Breathe slowly"
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            Column {
                StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                    Surface {
                        Column {
                            PlayerArtworkCard(
                                currentStretch = null,
                                state = PlayerState.IDLE,
                                currentIndex = 0,
                                totalCount = 0
                            )
                            PlayerArtworkCard(
                                currentStretch = unmapped,
                                state = PlayerState.PREPARING,
                                currentIndex = 1,
                                totalCount = 5
                            )
                        }
                    }
                }
                StretchCoachTheme(appTheme = AppTheme.DARK) {
                    Surface {
                        PlayerArtworkCard(
                            currentStretch = unmapped,
                            state = PlayerState.HOLDING,
                            currentIndex = 1,
                            totalCount = 5
                        )
                    }
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(100)

        // IDLE state (no hardcoded img_stretch_standing fallback)
        composeTestRule.onNodeWithText("READY TO START").assertIsDisplayed()
        composeTestRule.onNodeWithText("READY").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stretch demonstration").assertDoesNotExist()

        // PREPARING (Light) & HOLDING (Dark)
        composeTestRule.onNodeWithText("PREPARING").assertIsDisplayed()
        composeTestRule.onNodeWithText("HOLDING").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Custom Breathing Flow").assertDoesNotExist()
    }

    @Test
    fun playerArtworkCard_nullArtwork_singleCardHoldingAndPreparingStates() {
        val unmapped = StretchEntity(
            id = 202L,
            name = "Custom Neck Release",
            category = "Neck & Shoulders",
            notes = "Relax shoulders downward"
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.DARK) {
                Surface {
                    PlayerArtworkCard(
                        currentStretch = unmapped,
                        state = PlayerState.PREPARING,
                        currentIndex = 1,
                        totalCount = 5
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onNodeWithTag("player_artwork_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("STEP 2 OF 5").assertIsDisplayed()
        composeTestRule.onNodeWithText("PREPARING").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Neck Release").assertIsDisplayed()
        composeTestRule.onNodeWithText("Relax shoulders downward").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Custom Neck Release").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Stretch demonstration").assertDoesNotExist()
    }

    @Test
    fun playerArtworkCard_mappedAndCustomArtwork_rendersImageAndBadges() {
        val mapped = StretchEntity(
            id = 203L,
            name = "Adho Mukha Svanasana (Downward-Facing Dog)",
            category = "Yoga Asanas",
            notes = "Lengthen spine evenly."
        )
        val customTempFile = createTempJpegFile()
        val custom = StretchEntity(
            id = 204L,
            name = "Custom Player Pose",
            category = "Custom",
            notes = "Stay balanced.",
            customImagePath = customTempFile.absolutePath
        )

        try {
            composeTestRule.mainClock.autoAdvance = false
            composeTestRule.setContent {
                StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                    Surface {
                        Column {
                            PlayerArtworkCard(
                                currentStretch = mapped,
                                state = PlayerState.PAUSED,
                                currentIndex = 0,
                                totalCount = 3
                            )
                            PlayerArtworkCard(
                                currentStretch = custom,
                                state = PlayerState.HOLDING,
                                currentIndex = 2,
                                totalCount = 3
                            )
                        }
                    }
                }
            }
            composeTestRule.mainClock.advanceTimeBy(100)

            composeTestRule.onNodeWithText("STEP 1 OF 3").assertIsDisplayed()
            composeTestRule.onNodeWithText("PAUSED").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Adho Mukha Svanasana (Downward-Facing Dog)").assertIsDisplayed()

            composeTestRule.onNodeWithText("STEP 3 OF 3").assertIsDisplayed()
            composeTestRule.onNodeWithText("HOLDING").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Custom Player Pose").assertExists()
        } finally {
            customTempFile.delete()
        }
    }

    @Test
    fun addFromLibraryDialog_mixedMappedAndNullItems_rendersCleanlyWithoutCrash() {
        val mappedLib = LibraryStretchEntity(
            id = "tadasana_mountain_alignment",
            name = "Tadasana (Mountain Pose Alignment)",
            indianName = "ताड़ासन",
            category = "Yoga Asanas",
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 30,
            prepCue = "Stand tall with feet parallel.",
            holdCue = "Engage thighs and lengthen crown.",
            benefits = "Improves posture and balance."
        )
        val unmappedLib = LibraryStretchEntity(
            id = "custom_unmapped_1",
            name = "Custom Unmapped Pose",
            indianName = "कस्टम आसन",
            category = "Yoga Asanas",
            defaultPrepSeconds = 8,
            defaultHoldSeconds = 30,
            prepCue = "Prepare calmly.",
            holdCue = "Hold steadily.",
            benefits = "Custom mobility."
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                Surface {
                    AddFromLibraryDialog(
                        libraryStretches = listOf(mappedLib, unmappedLib),
                        onDismiss = {},
                        onAddStretch = {},
                        onUpdateLibraryStretch = {},
                        onAddNewLibraryStretch = {}
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onNodeWithTag("add_from_library_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("library_item_custom_unmapped_1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Unmapped Pose").assertIsDisplayed()
        composeTestRule.onNodeWithText("कस्टम आसन").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Custom Unmapped Pose").assertDoesNotExist()

        composeTestRule.onNodeWithText("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()
    }

    @Test
    fun stretchDetailDialog_nullArtwork_omitsImageAndShowsCues() {
        val unmappedLib = LibraryStretchEntity(
            id = "custom_detail_1",
            name = "Custom Unmapped Detail",
            indianName = "विस्तार आसन",
            category = "Yoga Asanas",
            prepCue = "Sit upright cleanly.",
            holdCue = "Hold steady breath.",
            benefits = "Improves focus."
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                Surface {
                    StretchDetailDialog(
                        stretches = listOf(unmappedLib),
                        initialIndex = 0,
                        addedStretchIds = emptySet(),
                        onDismiss = {},
                        onAdd = {},
                        onEdit = {}
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onNodeWithTag("stretch_detail_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("stretch_detail_pager").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Unmapped Detail").assertIsDisplayed()
        composeTestRule.onNodeWithText("विस्तार आसन").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sit upright cleanly.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hold steady breath.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Improves focus.").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Custom Unmapped Detail").assertDoesNotExist()
    }

    @Test
    fun stretchDetailDialog_mappedArtwork_rendersImage() {
        val mappedLib = LibraryStretchEntity(
            id = "balasana_childs_pose",
            name = "Balasana (Child's Pose)",
            indianName = "बालासन",
            category = "Yoga Asanas",
            prepCue = "Kneel with big toes touching.",
            holdCue = "Rest forehead on floor.",
            benefits = "Calms nervous system."
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.DARK) {
                Surface {
                    StretchDetailDialog(
                        stretches = listOf(mappedLib),
                        initialIndex = 0,
                        addedStretchIds = emptySet(),
                        onDismiss = {},
                        onAdd = {},
                        onEdit = {}
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onNodeWithTag("stretch_detail_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balasana (Child's Pose)").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Balasana (Child's Pose)").assertIsDisplayed()
    }

    @Test
    fun stretchDetailDialog_customArtwork_rendersImage() {
        val customTempFile = createTempJpegFile()
        val customLib = LibraryStretchEntity(
            id = "custom_detail_img",
            name = "Custom Uploaded Detail Pose",
            category = "Custom",
            customImagePath = customTempFile.absolutePath,
            prepCue = "Setup custom pose.",
            holdCue = "Hold custom pose.",
            benefits = "Custom benefit."
        )

        try {
            composeTestRule.mainClock.autoAdvance = false
            composeTestRule.setContent {
                StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                    Surface {
                        StretchDetailDialog(
                            stretches = listOf(customLib),
                            initialIndex = 0,
                            addedStretchIds = emptySet(),
                            onDismiss = {},
                            onAdd = {},
                            onEdit = {}
                        )
                    }
                }
            }
            composeTestRule.mainClock.advanceTimeBy(100)

            composeTestRule.onNodeWithTag("stretch_detail_dialog").assertIsDisplayed()
            composeTestRule.onNodeWithText("Custom Uploaded Detail Pose").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Custom Uploaded Detail Pose").assertExists()
        } finally {
            customTempFile.delete()
        }
    }
}
