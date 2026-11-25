package com.example

import com.example.ai.HybridRoutineGenerator
import com.example.ai.InferenceTier
import com.example.ai.PosturePreference
import com.example.data.PostureHub
import com.example.data.StretchLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HybridRoutineGeneratorTest {

    private val allLibrary = StretchLibrary.ALL_100_STRETCHES

    @Test
    fun extractTargetMinutes_parsesNaturalLanguageAndClamps() {
        assertEquals(12, HybridRoutineGenerator.extractTargetMinutes("12 min tight hips", 10))
        assertEquals(15, HybridRoutineGenerator.extractTargetMinutes("Give me a 15 minutes morning flow", 10))
        assertEquals(8, HybridRoutineGenerator.extractTargetMinutes("Quick 8m desk break", 10))
        assertEquals(10, HybridRoutineGenerator.extractTargetMinutes("No time mentioned", 10))
        assertEquals(45, HybridRoutineGenerator.extractTargetMinutes("99 minutes marathon", 10))
    }

    @Test
    fun extractPosturePreference_detectsFloorSeatedAndStanding() {
        assertEquals(
            PosturePreference.FLOOR_ONLY,
            HybridRoutineGenerator.extractPosturePreference("bedtime floor only relax", PosturePreference.ANY)
        )
        assertEquals(
            PosturePreference.SEATED,
            HybridRoutineGenerator.extractPosturePreference("Quick desk break at my desk", PosturePreference.ANY)
        )
        assertEquals(
            PosturePreference.STANDING,
            HybridRoutineGenerator.extractPosturePreference("Standing only energy boost", PosturePreference.ANY)
        )
        // Explicit preference takes precedence
        assertEquals(
            PosturePreference.FLOOR_ONLY,
            HybridRoutineGenerator.extractPosturePreference("standing energy", PosturePreference.FLOOR_ONLY)
        )
    }

    @Test
    fun parseModelResponseToPlan_parsesFencedJsonAndAutoPairsLeftRight() {
        val raw = """
            Here is your routine:
            ```json
            {
              "name": "Desk Relief",
              "description": "Releases neck and shoulder tension",
              "rationale": "Targets upper trapezius and pectorals",
              "stretch_ids": ["neck_side_right", "nonexistent_pose_id", "doorway_chest_opener"]
            }
            ```
        """.trimIndent()

        val plan = HybridRoutineGenerator.parseModelResponseToPlan(
            rawText = raw,
            inferenceTier = InferenceTier.ON_DEVICE_NPU,
            targetMinutes = 6,
            allLibrary = allLibrary,
            context = null
        )

        assertNotNull(plan)
        plan!!
        assertEquals("Desk Relief", plan.name)
        assertEquals(InferenceTier.ON_DEVICE_NPU, plan.inferenceTier)
        val ids = plan.stretches.mapNotNull { it.libraryId }
        assertTrue("Should include requested right-side stretch", "neck_side_right" in ids)
        assertTrue("Should auto-pair left-side stretch", "neck_side_left" in ids)
        assertTrue("Should include doorway_chest_opener", "doorway_chest_opener" in ids)
        assertTrue("Should filter unknown IDs", "nonexistent_pose_id" !in ids)
    }

    @Test
    fun parseModelResponseToPlan_returnsNullOnMalformedOutput() {
        val plan = HybridRoutineGenerator.parseModelResponseToPlan(
            rawText = "Sorry, I cannot help with that.",
            inferenceTier = InferenceTier.CLOUD_GEMINI,
            targetMinutes = 10,
            allLibrary = allLibrary,
            context = null
        )
        assertNull(plan)
    }

    @Test
    fun localSmartFlowRoutine_buildsDeskNeckRoutineWithBilateralPairs() {
        val hubMap = HybridRoutineGenerator.buildHubMap(null, allLibrary)
        val plan = HybridRoutineGenerator.buildLocalSmartFlowRoutine(
            userPrompt = "Desk neck and shoulders tension relief",
            targetMinutes = 8,
            posturePreference = PosturePreference.ANY,
            allLibrary = allLibrary,
            hubById = hubMap,
            context = null
        )

        assertEquals(InferenceTier.LOCAL_SMART_FLOW, plan.inferenceTier)
        assertTrue("Should generate at least 4 stretches", plan.stretches.size >= 4)
        assertTrue(
            "Should prioritize neck/shoulder stretches",
            plan.stretches.any { it.category.contains("Neck", ignoreCase = true) }
        )

        // Verify bilateral symmetry: every _right has its _left partner
        val ids = plan.stretches.mapNotNull { it.libraryId }.toSet()
        ids.filter { it.endsWith("_right") }.forEach { rightId ->
            val leftId = rightId.removeSuffix("_right") + "_left"
            if (allLibrary.any { it.id == leftId }) {
                assertTrue("Expected $leftId partner for $rightId", leftId in ids)
            }
        }
    }

    @Test
    fun localSmartFlowRoutine_respectsFloorOnlyFilter() {
        val hubMap = HybridRoutineGenerator.buildHubMap(null, allLibrary)
        val plan = HybridRoutineGenerator.buildLocalSmartFlowRoutine(
            userPrompt = "Bedtime lower back and hip unwind",
            targetMinutes = 10,
            posturePreference = PosturePreference.FLOOR_ONLY,
            allLibrary = allLibrary,
            hubById = hubMap,
            context = null
        )

        assertTrue(plan.stretches.isNotEmpty())
        plan.stretches.forEach { entity ->
            val hub = hubMap[entity.libraryId] ?: PostureHub.STANDING
            assertTrue(
                "Expected floor-only posture for ${entity.name}, got $hub",
                hub != PostureHub.STANDING
            )
        }
    }
}
