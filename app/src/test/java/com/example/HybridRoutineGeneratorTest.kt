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
    fun parseModelResponseToPlan_parsesThinkingStepsAndDeduplicatesSamePose() {
        val raw = """
            {
              "name": "Spine Reset",
              "description": "Calming spinal mobility",
              "rationale": "Combines flexion and extension",
              "thinking": [
                "Identified lower back tension goal",
                "Selected cat-cow and child pose decompression",
                "Balanced sequence for floor flow"
              ],
              "stretch_ids": ["cat_cow_spinal_flow", "balasana_childs_pose", "child_pose_full_body", "bhujangasana_cobra_pose"]
            }
        """.trimIndent()

        val plan = HybridRoutineGenerator.parseModelResponseToPlan(
            rawText = raw,
            inferenceTier = InferenceTier.ON_DEVICE_NPU,
            targetMinutes = 8,
            allLibrary = allLibrary,
            context = null,
            hardwareAccelerator = "Google Tensor G4 TPU",
            modelName = "Gemini Nano",
            latencyMs = 1420L,
            candidateCount = 34
        )

        assertNotNull(plan)
        plan!!
        assertEquals("Google Tensor G4 TPU", plan.hardwareAccelerator)
        assertEquals(1420L, plan.latencyMs)
        assertTrue("Should capture model thinking steps", plan.thinkingSteps.any { "Identified lower back tension" in it })
        // balasana_childs_pose and legacy child_pose_full_body must deduplicate to 1 child's pose
        val childCount = plan.stretches.count { it.libraryId == "balasana_childs_pose" }
        assertEquals("Duplicate child's pose should be merged to 1", 1, childCount)
    }

    @Test
    fun buildCompactPrompt_includesStretchBenefitsAndThinkingInstruction() {
        val hubMap = HybridRoutineGenerator.buildHubMap(null, allLibrary)
        val candidates = allLibrary.take(5)
        val prompt = HybridRoutineGenerator.buildCompactPrompt(
            userGoal = "Desk neck relief",
            targetMinutes = 8,
            posturePreference = PosturePreference.ANY,
            candidates = candidates,
            hubById = hubMap
        )
        assertTrue("Prompt should include benefit column", "benefit" in prompt)
        assertTrue("Prompt should request thinking steps", "thinking" in prompt)
        assertTrue("Prompt should include actual stretch benefit text", candidates.first().benefits.take(20) in prompt)
    }
}
