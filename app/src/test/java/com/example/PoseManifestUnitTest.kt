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
    fun manifestLoadsCanonicalPosesAnd12Transitions() {
        PoseManifestRepository.ensureLoaded(context)
        assertEquals(112, PoseManifestRepository.totalLoadedPoses())
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

    @Test
    fun subahSubahRoutineHas52StepsAndAllResolveVerifiedVideos() {
        val subah = AppDatabase.createDefaultStretchesForRoutine(7L)
        assertEquals(52, subah.size)
        assertEquals("Ardha Pavanmuktasana (Left)", subah.first().name)
        assertEquals("Standing Side Lat Reach (Left)", subah.last().name)
        // All 5 Batch 5 poses + existing verified aliases now resolve cleanly
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("World's Greatest Stretch (Left)") != null)
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("Ardha Pavanmuktasana Diagonal (Left)") != null)
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("Parvatasana (Seated Mountain)") != null)
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("Spine Alignment") != null)
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("Scapula Stretch (Left)") != null)
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("Bhujangasana") != null)
        assertTrue(com.example.data.ArtworkMapper.getDrawableForStretch("Towel Hamstring Stretch (Left)") != null)
    }

    @Test
    fun overlayAnchorsAndAll12StudioOverlaysAreBundled() {
        PoseManifestRepository.ensureLoaded(context)
        val catCow = PoseManifestRepository.getPoseMetaByStem(context, "img_stretch_b1_cat_cow_flow")
        assertTrue(catCow != null)
        assertTrue(catCow!!.overlayAnchors.dog.visible)
        assertTrue(catCow.overlayAnchors.floorProp.visible)
        assertTrue(catCow.overlayAnchors.windChime.visible)

        val overlays = context.assets.list("stretch_overlays")?.toSet().orEmpty()
        val expected = listOf(
            // 12 Roaming Golden Retriever Puppy V2 (GREEN_KEY)
            "overlay_dog_walk_roam_chromakey.mp4",
            "overlay_dog_idle_sit_chromakey.mp4",
            "overlay_dog_play_bow_chromakey.mp4",
            "overlay_dog_belly_roll_chromakey.mp4",
            "overlay_dog_sniff_explore_chromakey.mp4",
            "overlay_dog_tail_chase_chromakey.mp4",
            "overlay_dog_ear_scratch_chromakey.mp4",
            "overlay_dog_head_tilt_chromakey.mp4",
            "overlay_dog_paw_shake_chromakey.mp4",
            "overlay_dog_yawn_stretch_chromakey.mp4",
            "overlay_dog_nap_curled_chromakey.mp4",
            "overlay_dog_celebrate_chromakey.mp4",
            // 12 Roaming Calico Cat V2 (GREEN_KEY)
            "overlay_cat_walk_roam_chromakey.mp4",
            "overlay_cat_idle_loaf_chromakey.mp4",
            "overlay_cat_paw_groom_chromakey.mp4",
            "overlay_cat_big_stretch_chromakey.mp4",
            "overlay_cat_curious_tilt_chromakey.mp4",
            "overlay_cat_playful_bat_chromakey.mp4",
            "overlay_cat_chase_butterfly_chromakey.mp4",
            "overlay_cat_knead_paws_chromakey.mp4",
            "overlay_cat_belly_flop_chromakey.mp4",
            "overlay_cat_yawn_sleepy_chromakey.mp4",
            "overlay_cat_nap_curled_chromakey.mp4",
            "overlay_cat_celebrate_chromakey.mp4",
            // 8 Original Cozy Mat Puppy (BLUE_KEY)
            "overlay_cozy_dog_idle_rest_chromakey.mp4",
            "overlay_cozy_dog_playful_stretch_chromakey.mp4",
            "overlay_cozy_dog_nap_curled_chromakey.mp4",
            "overlay_cozy_dog_celebrate_chromakey.mp4",
            "overlay_cozy_dog_head_tilt_curious_chromakey.mp4",
            "overlay_cozy_dog_yawn_sleepy_chromakey.mp4",
            "overlay_cozy_dog_belly_roll_chromakey.mp4",
            "overlay_cozy_dog_paw_wave_chromakey.mp4",
            // 4 Studio Ghibli Atmosphere Props & Wind Chime
            "overlay_ghibli_tea_set_chromakey.mp4",
            "overlay_ghibli_candle_warmer_chromakey.mp4",
            "overlay_ghibli_washi_lantern_chromakey.mp4",
            "overlay_ghibli_wind_chime_chromakey.mp4"
        )
        expected.forEach { file ->
            assertTrue("Missing bundled overlay asset: $file", overlays.contains(file))
        }
    }

    @Test
    fun suryaNamaskarFlowAndPigeonDualOrientationAnchorsAndPetRoamLanesWork() {
        PoseManifestRepository.ensureLoaded(context)
        assertEquals(125, PoseManifestRepository.totalLoadedRoamLanes())

        // Surya Namaskar flow routine & manifest metadata
        val suryaRoutine = AppDatabase.createDefaultStretchesForRoutine(8L)
        assertEquals(1, suryaRoutine.size)
        val suryaMeta = PoseManifestRepository.getPoseMetaByStem(context, "img_stretch_surya_namaskar_flow")
        assertTrue(suryaMeta != null)
        assertTrue(suryaMeta!!.isFlowMode)
        assertEquals(6, suryaMeta.defaultRounds)
        assertTrue(suryaMeta.anchorsFor(isPortrait = false).dog.visible)
        assertFalse(suryaMeta.anchorsFor(isPortrait = true).dog.visible)

        // Exact pixel-measured roam lanes for Tree Pose (landscape & portrait)
        val treeMeta = PoseManifestRepository.getPoseMetaByStem(context, "img_stretch_tree")
        assertTrue(treeMeta != null)
        val treeRoam = PoseManifestRepository.resolveRoamConfig(treeMeta)
        assertTrue(treeRoam.visibleLandscape)
        assertTrue(treeRoam.canRoamLandscape)
        assertTrue(treeRoam.landscapeLanes.first().spanPct >= 20f)

        // Empty Sanctuary uses exact pet_roam_lanes.json full-room corridor & clamps for floor prop
        val sanctuaryRoam = PoseManifestRepository.resolveRoamConfig(
            entry = null,
            isEmptySanctuary = true,
            hasActiveFloorProp = true,
            propRightPct = 2f,
            propWidthPct = 14f
        )
        assertTrue(sanctuaryRoam.canRoamLandscape)
        assertTrue(sanctuaryRoam.landscapeLanes.first().maxXPct <= 64f)
    }
}
