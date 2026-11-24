package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.StretchEntity
import com.example.ui.components.StretchItemCard
import com.example.ui.theme.StretchCoachTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class CardLayoutScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verify_stretch_card_layout_proportions() {
        val testStretch = StretchEntity(
            id = 1L,
            routineId = 1L,
            name = "Bhujangasana (Cobra Pose)",
            category = "Back & Spine",
            holdSeconds = 25,
            preparationSeconds = 5,
            sortOrder = 0,
            prepCue = "Lie prone and place palms under shoulders.",
            holdCue = "Press chest gently upward while keeping elbows soft."
        )

        composeTestRule.setContent {
            StretchCoachTheme {
                Surface(modifier = Modifier.width(380.dp).padding(16.dp)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        StretchItemCard(
                            stretch = testStretch,
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
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/stretch_card_pixel8.png")
    }
}
