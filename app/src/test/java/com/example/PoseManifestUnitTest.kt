package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.PoseManifestRepository
import com.example.data.PostureHub
import com.example.data.StretchEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PoseManifestUnitTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun manifestLoads117PosesAnd12Transitions() {
        PoseManifestRepository.ensureLoaded(context)
        assertEquals(117, PoseManifestRepository.totalLoadedPoses())
        assertEquals(12, PoseManifestRepository.totalLoadedTransitions())
    }

    @Test
    fun hubTransitionsResolveDirectAndTwoHopRoutes() {
        PoseManifestRepository.ensureLoaded(context)

        // Same hub -> no transition
        assertTrue(PoseManifestRepository.resolveTransitionRoute(PostureHub.STANDING, PostureHub.STANDING).isEmpty())

        // Direct 1-hop
        val standToKneel = PoseManifestRepository.resolveTransitionRoute(PostureHub.STANDING, PostureHub.KNEELING)
        assertEquals(1, standToKneel.size)
        assertEquals("stretch_transitions/transition_standing_to_kneeling.mp4", standToKneel[0].assetPath)

        // 2-hop: Standing Hamstring -> Cobra (STANDING -> PRONE via KNEELING)
        val standToProne = PoseManifestRepository.resolveTransitionRoute(PostureHub.STANDING, PostureHub.PRONE)
        assertEquals(2, standToProne.size)
        assertEquals(PostureHub.KNEELING, standToProne[0].toHub)
        assertEquals(PostureHub.PRONE, standToProne[1].toHub)
    }

    @Test
    fun unilateralLeftSideMirroringWorksAccurately() {
        PoseManifestRepository.ensureLoaded(context)
        assertTrue(PoseManifestRepository.shouldMirrorForLeftSide(context, "Virabhadrasana II (Warrior II - Left)", "Yoga Asanas"))
        assertFalse(PoseManifestRepository.shouldMirrorForLeftSide(context, "Virabhadrasana II (Warrior II - Right)", "Yoga Asanas"))
        assertFalse(PoseManifestRepository.shouldMirrorForLeftSide(context, "Tadasana (Mountain Pose Alignment)", "Yoga Asanas"))
    }

    @Test
    fun smartFlowSortsByPostureHubOrder() {
        PoseManifestRepository.ensureLoaded(context)
        val unsorted = listOf(
            StretchEntity(id = 1, name = "Shavasana (Corpse Restorative Pose)", category = "Yoga Asanas"), // SUPINE
            StretchEntity(id = 2, name = "Tadasana (Mountain Pose Alignment)", category = "Yoga Asanas"),  // STANDING
            StretchEntity(id = 3, name = "Bhujangasana (Cobra Pose)", category = "Yoga Asanas"),           // PRONE
            StretchEntity(id = 4, name = "Cat-Cow Spine Mobilizer", category = "Chest & Upper Back")       // KNEELING
        )
        val sorted = PoseManifestRepository.sortStretchesBySmartFlow(context, unsorted)
        assertEquals("Tadasana (Mountain Pose Alignment)", sorted[0].name)
        assertEquals("Cat-Cow Spine Mobilizer", sorted[1].name)
        assertEquals("Bhujangasana (Cobra Pose)", sorted[2].name)
        assertEquals("Shavasana (Corpse Restorative Pose)", sorted[3].name)
    }

    @Test
    fun quickVerifyRoutineHas30ShortStretches() {
        val quick = AppDatabase.createDefaultStretchesForRoutine(6L)
        assertEquals(30, quick.size)
        assertTrue(quick.all { it.preparationSeconds == 5 && it.holdSeconds == 6 })
    }
}
