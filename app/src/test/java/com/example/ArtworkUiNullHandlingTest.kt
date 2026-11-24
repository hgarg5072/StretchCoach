package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArtworkUiNullHandlingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stretchItemCard_omitsThumbnailWhenUnmapped_andRendersWhenVerified() {
        val unmapped = StretchEntity(
            id = 1L,
            name = "Custom Unmapped Stretch",
            category = "Back & Spine",
            preparationSeconds = 5,
            holdSeconds = 20
        )
        val verified = StretchEntity(
            id = 2L,
            name = "Balasana (Child's Pose)",
            category = "Yoga Asanas",
            preparationSeconds = 6,
            holdSeconds = 45
        )

        composeTestRule.setContent {
            StretchCoachTheme {
                androidx.compose.foundation.layout.Column {
                    StretchItemCard(
                        stretch = unmapped,
                        index = 0,
                        totalCount = 2,
                        onToggleEnabled = {},
                        onToggleVoiceCue = {},
                        onEdit = {},
                        onDelete = {},
                        onPlaySingle = {}
                    )
                    StretchItemCard(
                        stretch = verified,
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

        composeTestRule.onNodeWithText("Custom Unmapped Stretch").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Custom Unmapped Stretch").assertCountEquals(0)

        composeTestRule.onNodeWithText("Balasana (Child's Pose)").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Balasana (Child's Pose)").assertIsDisplayed()
    }

    @Test
    fun playerArtworkCard_rendersCompactHeaderWithoutFallbackWhenNull_lightAndDark() {
        val unmapped = StretchEntity(
            id = 10L,
            name = "Custom Mobility Twist",
            category = "Back & Spine",
            notes = "Keep shoulders soft and breathe evenly."
        )

        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.DARK) {
                androidx.compose.foundation.layout.Column {
                    PlayerArtworkCard(
                        currentStretch = null,
                        state = PlayerState.IDLE,
                        currentIndex = 0,
                        totalCount = 3
                    )
                    PlayerArtworkCard(
                        currentStretch = unmapped,
                        state = PlayerState.HOLDING,
                        currentIndex = 0,
                        totalCount = 3
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("READY TO START").assertIsDisplayed()
        composeTestRule.onNodeWithText("READY").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Stretch demonstration").assertCountEquals(0)

        composeTestRule.onNodeWithText("STEP 1 OF 3").assertIsDisplayed()
        composeTestRule.onNodeWithText("HOLDING").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Mobility Twist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Keep shoulders soft and breathe evenly.").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Custom Mobility Twist").assertCountEquals(0)
    }

    @Test
    fun playerArtworkCard_rendersImageWhenVerified() {
        val verified = StretchEntity(
            id = 11L,
            name = "Tadasana (Mountain Pose Alignment)",
            category = "Yoga Asanas",
            notes = "Root evenly through both soles."
        )

        composeTestRule.setContent {
            StretchCoachTheme(appTheme = AppTheme.LIGHT) {
                PlayerArtworkCard(
                    currentStretch = verified,
                    state = PlayerState.PREPARING,
                    currentIndex = 1,
                    totalCount = 4
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()
        composeTestRule.onNodeWithText("STEP 2 OF 4").assertIsDisplayed()
        composeTestRule.onNodeWithText("PREPARING").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tadasana (Mountain Pose Alignment)").assertIsDisplayed()
    }

    @Test
    fun libraryAndDetailDialogs_omitArtworkWhenUnmapped_andRenderWhenVerified() {
        val unmappedLib = LibraryStretchEntity(
            id = "custom_unmapped",
            name = "Custom Unmapped Stretch",
            category = "Back & Spine",
            defaultPrepSeconds = 5,
            defaultHoldSeconds = 20,
            prepCue = "Sit upright comfortably.",
            holdCue = "Hold steady posture.",
            benefits = "Improves posture."
        )
        val verifiedLib = LibraryStretchEntity(
            id = "balasana_childs_pose",
            name = "Balasana (Child's Pose)",
            category = "Yoga Asanas",
            defaultPrepSeconds = 6,
            defaultHoldSeconds = 45,
            prepCue = "Kneel with big toes touching.",
            holdCue = "Rest forehead on floor.",
            benefits = "Calms nervous system."
        )

        composeTestRule.setContent {
            StretchCoachTheme {
                AddFromLibraryDialog(
                    libraryStretches = listOf(unmappedLib, verifiedLib),
                    onDismiss = {},
                    onAddStretch = {},
                    onUpdateLibraryStretch = {},
                    onAddNewLibraryStretch = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Custom Unmapped Stretch").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Custom Unmapped Stretch").assertCountEquals(0)
        composeTestRule.onNodeWithText("Balasana (Child's Pose)").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Balasana (Child's Pose)").assertIsDisplayed()
    }

    @Test
    fun stretchDetailDialog_omitsArtworkWhenUnmapped() {
        val unmappedLib = LibraryStretchEntity(
            id = "custom_unmapped",
            name = "Custom Unmapped Stretch",
            category = "Back & Spine",
            prepCue = "Sit upright comfortably.",
            holdCue = "Hold steady posture.",
            benefits = "Improves posture."
        )

        composeTestRule.setContent {
            StretchCoachTheme {
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

        composeTestRule.onNodeWithText("Custom Unmapped Stretch").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sit upright comfortably.").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Custom Unmapped Stretch").assertCountEquals(0)
    }

    @Test
    fun stretchDetailDialog_rendersArtworkWhenVerified() {
        val verifiedLib = LibraryStretchEntity(
            id = "adho_mukha_svanasana_downward_dog",
            name = "Adho Mukha Svanasana (Downward-Facing Dog)",
            category = "Yoga Asanas",
            prepCue = "From hands and knees, lift hips.",
            holdCue = "Lengthen spine.",
            benefits = "Elongates hamstrings and calves."
        )

        composeTestRule.setContent {
            StretchCoachTheme {
                StretchDetailDialog(
                    stretches = listOf(verifiedLib),
                    initialIndex = 0,
                    addedStretchIds = emptySet(),
                    onDismiss = {},
                    onAdd = {},
                    onEdit = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Adho Mukha Svanasana (Downward-Facing Dog)").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Adho Mukha Svanasana (Downward-Facing Dog)").assertIsDisplayed()
    }
}
