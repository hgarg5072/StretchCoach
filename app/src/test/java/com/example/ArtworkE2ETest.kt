package com.example

import android.content.Context
import android.graphics.BitmapFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.ArtworkMapper
import com.example.data.ExtraYogaAsanas
import com.example.data.StretchLibrary
import com.example.data.StretchRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArtworkE2ETest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val legacy15DrawableNames = setOf(
        "img_stretch_butterfly",
        "img_stretch_calf",
        "img_stretch_cat_cow",
        "img_stretch_chest",
        "img_stretch_cobra",
        "img_stretch_figure_four",
        "img_stretch_floor",
        "img_stretch_hamstring",
        "img_stretch_lunge",
        "img_stretch_pigeon",
        "img_stretch_quad",
        "img_stretch_side_bend",
        "img_stretch_twist",
        "img_stretch_upper",
        "img_stretch_wrist"
    )

    private val devanagariRegex = Regex("[\u0900-\u097F]")
    private val validDrawableNameRegex = Regex("^img_stretch_[a-z0-9_]+$")

    // =========================================================================
    // Tier 1: Catalog Completeness & Drawable Hygiene
    // =========================================================================

    @Test
    fun tier1_catalogCompletenessAndIntegrity() {
        val allStretches = StretchLibrary.ALL_100_STRETCHES
        val extra50 = ExtraYogaAsanas.EXTRA_50_ASANAS

        assertEquals("Combined catalog must contain exactly 150 stretches", 150, allStretches.size)
        assertEquals("ExtraYogaAsanas must contain exactly 50 asanas", 50, extra50.size)

        val first100 = allStretches.take(100)
        val first100Ids = first100.map { it.id }.toSet()
        val extra50Ids = extra50.map { it.id }.toSet()
        assertEquals("First 100 IDs must be unique", 100, first100Ids.size)
        assertEquals("Extra 50 IDs must be unique", 50, extra50Ids.size)
        assertTrue(
            "First 100 IDs and Extra 50 IDs must be completely disjoint",
            first100Ids.intersect(extra50Ids).isEmpty()
        )

        val allIds = allStretches.map { it.id }.toSet()
        assertEquals("All 150 stretch IDs must be unique", 150, allIds.size)

        val allTrimmedNames = allStretches.map { it.name.trim() }.toSet()
        assertEquals("All 150 trimmed stretch names must be unique", 150, allTrimmedNames.size)

        allStretches.forEach { stretch ->
            assertTrue("id must not be blank", stretch.id.isNotBlank())
            assertTrue("name must not be blank for ${stretch.id}", stretch.name.isNotBlank())
            assertTrue("prepCue must not be blank for ${stretch.id}", stretch.prepCue.isNotBlank())
            assertTrue("holdCue must not be blank for ${stretch.id}", stretch.holdCue.isNotBlank())
            assertTrue("benefits must not be blank for ${stretch.id}", stretch.benefits.isNotBlank())
            assertTrue("defaultPrepSeconds must be > 0 for ${stretch.id}", stretch.defaultPrepSeconds > 0)
            assertTrue("defaultHoldSeconds must be > 0 for ${stretch.id}", stretch.defaultHoldSeconds > 0)
        }

        extra50.forEach { asana ->
            val indian = asana.indianName
            assertFalse("ExtraYogaAsanas ${asana.id} must have non-blank indianName", indian.isNullOrBlank())
            assertTrue(
                "ExtraYogaAsanas ${asana.id} indianName must contain Devanagari characters, was: $indian",
                devanagariRegex.containsMatchIn(indian!!)
            )
        }
    }

    @Test
    fun tier1_legacy15DrawablesEliminatedFromResourcesAndMapper() {
        val res = context.resources
        val pkg = context.packageName

        legacy15DrawableNames.forEach { legacyName ->
            val id = res.getIdentifier(legacyName, "drawable", pkg)
            assertEquals("Legacy drawable $legacyName must not exist in app resources", 0, id)
        }

        StretchLibrary.ALL_100_STRETCHES.forEach { stretch ->
            val byName = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category.title)
            if (byName != null) {
                val entryName = res.getResourceEntryName(byName)
                assertFalse(
                    "Stretch '${stretch.name}' mapped to legacy drawable '$entryName'",
                    entryName in legacy15DrawableNames
                )
            }
            val byId = ArtworkMapper.getDrawableForStretch(stretch.id, stretch.category.title)
            if (byId != null) {
                val entryName = res.getResourceEntryName(byId)
                assertFalse(
                    "Stretch ID '${stretch.id}' mapped to legacy drawable '$entryName'",
                    entryName in legacy15DrawableNames
                )
            }
        }
    }

    @Test
    fun tier1_allMappedDrawablesDecodeAsValidUnifiedDimensions() {
        val res = context.resources

        StretchLibrary.ALL_100_STRETCHES.forEach { stretch ->
            val resId = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category.title)
            if (resId != null) {
                assertNotEquals("Mapped resId for ${stretch.id} must be non-zero", 0, resId)
                assertEquals(
                    "Mapped resource for ${stretch.id} must be a drawable",
                    "drawable",
                    res.getResourceTypeName(resId)
                )
                val entryName = res.getResourceEntryName(resId)
                assertTrue(
                    "Mapped drawable '$entryName' for ${stretch.id} must match ^img_stretch_[a-z0-9_]+$",
                    validDrawableNameRegex.matches(entryName)
                )

                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeResource(res, resId, opts)
                assertEquals(
                    "Drawable '$entryName' width must be 1376 (not legacy 1024/1408)",
                    1376,
                    opts.outWidth
                )
                assertEquals(
                    "Drawable '$entryName' height must be 768",
                    768,
                    opts.outHeight
                )
                val aspect = opts.outWidth.toFloat() / opts.outHeight.toFloat()
                assertTrue(
                    "Drawable '$entryName' must be 16:9 aspect ratio, was $aspect",
                    abs(aspect - (16f / 9f)) < 0.05f
                )
            }
        }
    }

    // =========================================================================
    // Tier 2: Mapping Safety, No-Fallback & Anti-Collision Traps
    // =========================================================================

    @Test
    fun tier2_unknownCustomAndCategoryFallbacksReturnNull() {
        val fallbackTrapInputs = listOf(
            "" to "",
            "   " to "Yoga Asanas",
            "Unknown Custom Stretch XYZ" to "Yoga Asanas",
            "Morning Desk Flow" to "Neck & Shoulders",
            "My Custom Hamstring Flow" to "Hamstrings & Quads",
            "Hip Routine" to "Hips & Glutes",
            "Back Relief" to "Lower Back & Core",
            "Calf Mobility" to "Calves & Feet",
            "Chest Opening" to "Chest & Upper Back",
            "Arm Stretch" to "Arms & Wrists",
            "Standing Stretch" to "General",
            "Floor Stretch" to "Lower Back & Core"
        )

        fallbackTrapInputs.forEach { (name, category) ->
            assertNull(
                "Expected null (no category/generic fallback) for name='$name', category='$category'",
                ArtworkMapper.getDrawableForStretch(name, category)
            )
        }
    }

    @Test
    fun tier2_substringCollisionTrapsNeverMapToWrongDrawable() {
        val res = context.resources

        val collisionTraps = listOf(
            "Chaturanga Dandasana (Four-Limbed Staff)" to setOf("img_stretch_lotus"),
            "Dandasana (Staff Pose)" to setOf("img_stretch_lotus"),
            "Vajrasana (Thunderbolt Pose)" to setOf("img_stretch_lotus"),
            "Tolasana (Scale Pose)" to setOf("img_stretch_lotus"),
            "Simhasana (Lion Pose)" to setOf("img_stretch_lotus"),
            "Anulom Vilom (Alternate Nostril Breathing)" to setOf("img_stretch_lotus"),
            "Wrist Figure-Eight Fluid Mobilizer" to setOf("img_stretch_figure_four"),
            "Skandasana (Side Lunge - Right)" to setOf("img_stretch_lunge", "img_stretch_warrior"),
            "Skandasana (Side Lunge - Left)" to setOf("img_stretch_lunge", "img_stretch_warrior"),
            "Downward Dog Calf Pedaling" to setOf("img_stretch_calf"),
            "Puppy Pose (Melting Heart)" to setOf("img_stretch_child"),
            "Child's Pose with Lat Reach" to setOf("img_stretch_child"),
            "Ustrasana (Camel Pose)" to setOf("img_stretch_chest"),
            "Matsyasana (Fish Pose)" to setOf("img_stretch_chest"),
            "Dhanurasana (Bow Pose)" to setOf("img_stretch_chest"),
            "Natarajasana (Dancer Pose - Right)" to setOf("img_stretch_tree"),
            "Natarajasana (Dancer Pose - Left)" to setOf("img_stretch_tree"),
            "Chakrasana (Wheel Pose)" to setOf("img_stretch_bridge"),
            "Purvottanasana (Upward Plank Pose)" to setOf("img_stretch_bridge"),
            "Virabhadrasana I (Warrior I - Right)" to setOf("img_stretch_warrior"),
            "Virabhadrasana I (Warrior I - Left)" to setOf("img_stretch_warrior"),
            "Virabhadrasana III (Warrior III - Right)" to setOf("img_stretch_warrior"),
            "Virabhadrasana III (Warrior III - Left)" to setOf("img_stretch_warrior"),
            "Ardha Chandrasana (Half Moon - Right)" to setOf("img_stretch_warrior"),
            "Ardha Chandrasana (Half Moon - Left)" to setOf("img_stretch_warrior"),
            "Utthita Parsvakonasana (Extended Side Angle - Right)" to setOf("img_stretch_warrior"),
            "Utthita Parsvakonasana (Extended Side Angle - Left)" to setOf("img_stretch_warrior"),
            "Sirsasana (Supported Headstand)" to setOf("img_stretch_standing"),
            "Bakasana (Crow Pose)" to setOf("img_stretch_standing"),
            "Phalakasana (Plank Pose)" to setOf("img_stretch_standing"),
            "Utkatasana (Chair Pose Stretch)" to setOf("img_stretch_standing"),
            "Surya Namaskar (Sun Salutation Flow)" to setOf("img_stretch_standing"),
            "Chin Tuck & Levator Stretch" to setOf("img_stretch_neck"),
            "Sternocleidomastoid (SCM) Neck Stretch" to setOf("img_stretch_neck")
        )

        collisionTraps.forEach { (stretchName, forbiddenDrawables) ->
            val mapped = ArtworkMapper.getDrawableForStretch(stretchName, "Yoga Asanas")
            if (mapped != null) {
                val entryName = res.getResourceEntryName(mapped)
                assertFalse(
                    "Substring collision trap triggered: '$stretchName' mapped to forbidden '$entryName'",
                    entryName in forbiddenDrawables
                )
            }
        }
    }

    @Test
    fun tier2_caseAndWhitespaceNormalizationAndCategoryInvariance() {
        val mappedStretches = StretchLibrary.ALL_100_STRETCHES.filter {
            ArtworkMapper.getDrawableForStretch(it.name, it.category.title) != null
        }
        assertTrue("At least one stretch must be mapped to verify normalization", mappedStretches.isNotEmpty())

        mappedStretches.forEach { stretch ->
            val expected = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category.title)
            assertNotNull(expected)

            assertEquals(
                "Lowercase padded lookup failed for '${stretch.name}'",
                expected,
                ArtworkMapper.getDrawableForStretch("  ${stretch.name.lowercase()}  ", "")
            )
            assertEquals(
                "Uppercase lookup with different category failed for '${stretch.name}'",
                expected,
                ArtworkMapper.getDrawableForStretch(stretch.name.uppercase(), "Completely Different Category")
            )
            assertEquals(
                "Multi-space normalized lookup failed for '${stretch.name}'",
                expected,
                ArtworkMapper.getDrawableForStretch(stretch.name.replace(" ", "   "), stretch.category.title)
            )
        }
    }

    // =========================================================================
    // Tier 3: Symmetrical Left/Right Pair Consistency (All 32 Pairs)
    // =========================================================================

    @Test
    fun tier3_symmetricalLeftRightPairsMapConsistently() {
        val pairs = listOf(
            // StretchLibrary (22 pairs)
            "neck_side_right" to "neck_side_left",
            "cross_body_shoulder_right" to "cross_body_shoulder_left",
            "thread_the_needle_right" to "thread_the_needle_left",
            "reclining_spinal_twist_right" to "reclining_spinal_twist_left",
            "hip_flexor_lunge_right" to "hip_flexor_lunge_left",
            "figure_four_glute_right" to "figure_four_glute_left",
            "pigeon_pose_right" to "pigeon_pose_left",
            "standing_quad_right" to "standing_quad_left",
            "wall_calf_gastrocnemius_right" to "wall_calf_gastrocnemius_left",
            "trikonasana_triangle_right" to "trikonasana_triangle_left",
            "vrikshasana_tree_right" to "vrikshasana_tree_left",
            "janu_sirsasana_right" to "janu_sirsasana_left",
            "ardha_matsyendrasana_right" to "ardha_matsyendrasana_left",
            "gomukhasana_arms_right" to "gomukhasana_arms_left",
            "virabhadrasana_one_right" to "virabhadrasana_one_left",
            "virabhadrasana_two_right" to "virabhadrasana_two_left",
            "anjaneyasana_low_lunge_right" to "anjaneyasana_low_lunge_left",
            "supta_padangusthasana_right" to "supta_padangusthasana_left",
            "garudasana_eagle_pose_right" to "garudasana_eagle_pose_left",
            "parsvottanasana_pyramid_right" to "parsvottanasana_pyramid_left",
            "eka_pada_rajakapotasana_pigeon_right" to "eka_pada_rajakapotasana_pigeon_left",
            "supta_matsyendrasana_twist_right" to "supta_matsyendrasana_twist_left",
            // ExtraYogaAsanas (10 pairs)
            "ardha_chandrasana_right" to "ardha_chandrasana_left",
            "utthita_parsvakonasana_right" to "utthita_parsvakonasana_left",
            "parivrtta_trikonasana_right" to "parivrtta_trikonasana_left",
            "hanumanasana_right" to "hanumanasana_left",
            "natarajasana_right" to "natarajasana_left",
            "vasisthasana_right" to "vasisthasana_left",
            "skandasana_right" to "skandasana_left",
            "bharadvajasana_right" to "bharadvajasana_left",
            "parighasana_gate_right" to "parighasana_gate_left",
            "virabhadrasana_3_right" to "virabhadrasana_3_left"
        )
        assertEquals("Must test all 32 symmetrical L/R pairs", 32, pairs.size)

        pairs.forEach { (rightId, leftId) ->
            val right = StretchLibrary.getById(rightId)
            val left = StretchLibrary.getById(leftId)
            assertNotNull("Right stretch '$rightId' must exist in catalog", right)
            assertNotNull("Left stretch '$leftId' must exist in catalog", left)

            val rightDrawable = ArtworkMapper.getDrawableForStretch(right!!.name, right.category.title)
            val leftDrawable = ArtworkMapper.getDrawableForStretch(left!!.name, left.category.title)
            assertEquals(
                "Symmetrical pair ($rightId, $leftId) must map to the same drawable (or both null)",
                rightDrawable,
                leftDrawable
            )

            val rightById = ArtworkMapper.getDrawableForStretch(right.id, right.category.title)
            val leftById = ArtworkMapper.getDrawableForStretch(left.id, left.category.title)
            assertEquals(
                "Symmetrical pair IDs ($rightId, $leftId) must agree with name mapping",
                rightDrawable,
                rightById
            )
            assertEquals(
                "Symmetrical pair IDs ($rightId, $leftId) must agree with name mapping",
                leftDrawable,
                leftById
            )
        }
    }

    // =========================================================================
    // Tier 4: Room Repository Seeding & Devanagari Resolution
    // =========================================================================

    @Test
    fun tier4_repositorySeedingResolves150StretchesAnd101DevanagariNames() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = db.stretchDao()
            val repo = StretchRepository(dao, context)
            repo.checkAndSeedDefaults()

            val seededLibrary = dao.getAllLibraryStretchesSync()
            assertEquals("Room must seed all 150 LibraryStretchEntity rows", 150, seededLibrary.size)

            val seededById = seededLibrary.associateBy { it.id }
            assertEquals("Seeded library IDs must be unique", 150, seededById.size)

            // All 50 ExtraYogaAsanas must match their explicit Devanagari indianName
            ExtraYogaAsanas.EXTRA_50_ASANAS.forEach { expected ->
                val row = seededById[expected.id]
                assertNotNull("Missing seeded ExtraYogaAsana row '${expected.id}'", row)
                assertEquals(
                    "Explicit indianName mismatch for '${expected.id}'",
                    expected.indianName,
                    row!!.indianName
                )
            }

            // #35 (seated_single_leg_fold) + #51-#100 (51 items total) resolve runtime Devanagari
            val expectedRuntimeIds = (listOf(StretchLibrary.ALL_100_STRETCHES[34]) +
                StretchLibrary.ALL_100_STRETCHES.subList(50, 100)).map { it.id }.toSet()
            assertEquals("Expected 51 runtime-resolved Devanagari stretches", 51, expectedRuntimeIds.size)

            expectedRuntimeIds.forEach { id ->
                val row = seededById[id]
                assertNotNull("Missing seeded StretchLibrary row '$id'", row)
                assertFalse("Expected non-blank Devanagari indianName for '$id'", row!!.indianName.isNullOrBlank())
                assertTrue(
                    "Expected Devanagari script for '$id', was '${row.indianName}'",
                    devanagariRegex.containsMatchIn(row.indianName!!)
                )
            }

            // Remaining 49 items (#1-#34 and #36-#50) must have null indianName
            val nonNullCount = seededLibrary.count { !it.indianName.isNullOrBlank() }
            val nullCount = seededLibrary.count { it.indianName == null }
            assertEquals("Exactly 101 stretches must have Devanagari indianName", 101, nonNullCount)
            assertEquals("Exactly 49 stretches must have null indianName", 49, nullCount)

            // Verify all 7 default routines seed cleanly and ArtworkMapper never throws or returns legacy drawables
            val routines = dao.getAllRoutinesSync()
            assertEquals("Must seed all 7 default routines", 7, routines.size)
            routines.forEach { routine ->
                val stretches = dao.getStretchesForRoutineSync(routine.id)
                assertTrue("Default routine '${routine.name}' must contain stretches", stretches.isNotEmpty())
                stretches.forEach { entity ->
                    val mapped = ArtworkMapper.getDrawableForStretch(entity.name, entity.category)
                    if (mapped != null) {
                        val entryName = context.resources.getResourceEntryName(mapped)
                        assertFalse(
                            "Routine stretch '${entity.name}' mapped to legacy '$entryName'",
                            entryName in legacy15DrawableNames
                        )
                    }
                }
            }
        } finally {
            db.close()
        }
    }
}
