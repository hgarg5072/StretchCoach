package com.example

import com.example.audio.SanskritPhonetics
import com.example.data.ArtworkMapper
import com.example.data.StretchCategory
import com.example.data.StretchLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStretchLibrarySizeAndCategories() {
        val stretches = StretchLibrary.ALL_100_STRETCHES
        assertEquals("Stretch library should have 135 unique stretches & asanas", 135, stretches.size)

        // Verify each category is represented
        StretchCategory.values().forEach { category ->
            val count = stretches.count { it.category == category }
            assertTrue("Category ${category.title} should have stretches", count > 0)
        }

        // Verify all IDs are unique
        val ids = stretches.map { it.id }.toSet()
        assertEquals("All 135 stretch IDs should be unique", 135, ids.size)

        // Verify all stretches have valid cues
        stretches.forEach { stretch ->
            assertTrue("Prep cue should not be empty for ${stretch.name}", stretch.prepCue.isNotBlank())
            assertTrue("Hold cue should not be empty for ${stretch.name}", stretch.holdCue.isNotBlank())
            assertTrue("Benefits should not be empty for ${stretch.name}", stretch.benefits.isNotBlank())
            assertTrue("Prep time > 0", stretch.defaultPrepSeconds > 0)
            assertTrue("Hold time > 0", stretch.defaultHoldSeconds > 0)
        }
    }

    @Test
    fun testSanskritPhonetics() {
        // Without Hindi/Devanagari voice installed on phone (English-TTS-safe phonetic bilingual)
        val defaultSpoken = SanskritPhonetics.formatForSpeech("Bhujangasana (Cobra Pose)", isIndianVoiceInstalled = false)
        assertTrue("Should contain English-TTS phonetic Bhoo-jung-ah-suh-nuh, was: $defaultSpoken", defaultSpoken.contains("Bhoo-jung-ah-suh-nuh"))
        assertTrue("Should contain Cobra Pose", defaultSpoken.contains("Cobra Pose"))

        // With Hindi/Devanagari voice installed on phone (authentic Devanagari Sanskrit)
        val devanagariSpoken = SanskritPhonetics.formatForSpeech("Bhujangasana (Cobra Pose)", isIndianVoiceInstalled = true)
        assertTrue("Should contain Devanagari भुजंगासन, was: $devanagariSpoken", devanagariSpoken.contains("भुजंगासन"))
        assertTrue("Should contain Cobra Pose", devanagariSpoken.contains("Cobra Pose"))

        // Test structured SpokenAsanaPlan for dual-voice routing
        val plan = SanskritPhonetics.buildSpokenPlan("Trikonasana (Triangle Pose - Right)")
        assertEquals("त्रिकोणासन", plan.sanskritDevanagari)
        assertEquals("Tree-koh-nah-suh-nuh", plan.sanskritPhonetic)
        assertEquals("Triangle Pose", plan.englishName)
        assertEquals("right side", plan.sideSuffix)

        // Test English Only style
        val englishOnly = SanskritPhonetics.formatForSpeech(
            "Bhujangasana (Cobra Pose)",
            com.example.data.AsanaPronunciationStyle.ENGLISH_ONLY
        )
        assertEquals("Cobra Pose", englishOnly)

        // Test Phonetic Sanskrit style
        val phonetic = SanskritPhonetics.formatForSpeech(
            "Bhujangasana (Cobra Pose)",
            com.example.data.AsanaPronunciationStyle.PHONETIC_SANSKRIT
        )
        assertTrue(phonetic.contains("Bhoo-jung-ah-suh-nuh"))

        // Manual custom Sanskrit entries (both curated and unknown rule-based fallback)
        val manualBhujangasana = SanskritPhonetics.formatForSpeech(
            "bhujangasana",
            com.example.data.AsanaPronunciationStyle.SANSKRIT_ONLY,
            isIndianVoiceInstalled = true
        )
        assertEquals("भुजंगासन", manualBhujangasana)

        val customAsanaDevanagari = SanskritPhonetics.formatForSpeech(
            "tolasana",
            com.example.data.AsanaPronunciationStyle.SANSKRIT_ONLY,
            isIndianVoiceInstalled = true
        )
        assertEquals("तोलासन", customAsanaDevanagari)

        val customAsanaPhonetic = SanskritPhonetics.formatForSpeech(
            "tolasana",
            com.example.data.AsanaPronunciationStyle.PHONETIC_SANSKRIT,
            isIndianVoiceInstalled = false
        )
        assertEquals("tol-ah-suh-nuh", customAsanaPhonetic)

        // When Sanskrit is inside parentheses: e.g. "Seated Single-Leg Fold (Janu Sirsasana)"
        val insideBracketEnglishOnly = SanskritPhonetics.formatForSpeech(
            "Seated Single-Leg Fold (Janu Sirsasana)",
            com.example.data.AsanaPronunciationStyle.ENGLISH_ONLY
        )
        assertEquals("Seated Single-Leg Fold", insideBracketEnglishOnly)

        val insideBracketPhonetic = SanskritPhonetics.formatForSpeech(
            "Seated Single-Leg Fold (Janu Sirsasana)",
            com.example.data.AsanaPronunciationStyle.PHONETIC_SANSKRIT
        )
        assertTrue(insideBracketPhonetic.contains("Jah-noo Sheer-shah-suh-nuh"))

        val balasanSpoken = SanskritPhonetics.formatForSpeech("Balasana (Child's Pose)", isIndianVoiceInstalled = true)
        assertTrue(balasanSpoken.contains("बालासन"))
        assertTrue(balasanSpoken.contains("Child's Pose"))

        val sideSpoken = SanskritPhonetics.formatForSpeech("Reclining Pigeon (Right)")
        assertTrue(sideSpoken.contains("right side"))
    }

    @Test
    fun testStretchSearchAndFilter() {
        val neckStretches = StretchLibrary.search("", StretchCategory.NECK_SHOULDERS)
        assertTrue(neckStretches.isNotEmpty())
        neckStretches.forEach {
            assertEquals(StretchCategory.NECK_SHOULDERS, it.category)
        }

        val searchResult = StretchLibrary.search("hamstring")
        assertTrue(searchResult.isNotEmpty())
    }

    @Test
    fun testArtworkMapper() {
        // 1. Verified M1 exact matches (5 strictly R4-verified drawables covering 8 catalog stretches)
        assertEquals(
            R.drawable.img_stretch_downward_dog,
            ArtworkMapper.getDrawableForStretch("Adho Mukha Svanasana (Downward-Facing Dog)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_downward_dog,
            ArtworkMapper.getDrawableForStretch("adho_mukha_svanasana_downward_dog")
        )
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("Balasana (Child's Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("balasana_childs_pose")
        )
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("Shashankasana (Hare Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("shashankasana_hare")
        )
        assertEquals(
            R.drawable.img_stretch_tree,
            ArtworkMapper.getDrawableForStretch("Vrikshasana (Tree Pose - Right)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_tree,
            ArtworkMapper.getDrawableForStretch("Vrikshasana (Tree Pose - Left)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_warrior,
            ArtworkMapper.getDrawableForStretch("Virabhadrasana II (Warrior II - Right)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_warrior,
            ArtworkMapper.getDrawableForStretch("Virabhadrasana II (Warrior II - Left)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_standing,
            ArtworkMapper.getDrawableForStretch("Tadasana (Mountain Pose Alignment)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_standing,
            ArtworkMapper.getDrawableForStretch("tadasana_mountain_alignment")
        )

        // 2. Whitespace & case normalization
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("  balasana (child's pose)  ", "")
        )
        assertEquals(
            R.drawable.img_stretch_downward_dog,
            ArtworkMapper.getDrawableForStretch("ADHO MUKHA   SVANASANA (DOWNWARD-FACING DOG)", "")
        )

        // 3. No-fallback / null behavior for custom, unknown, empty, or M1-deferred stretches
        assertNull(ArtworkMapper.getDrawableForStretch("My Custom Neck Mobility", "Neck & Shoulders"))
        assertNull(ArtworkMapper.getDrawableForStretch("Unknown Yoga Flow", "Yoga Asanas"))
        assertNull(ArtworkMapper.getDrawableForStretch("Custom Floor Stretch", "Lower Back & Core"))
        assertNull(ArtworkMapper.getDrawableForStretch("", ""))
        assertNull(ArtworkMapper.getDrawableForStretch("   ", "Yoga Asanas"))

        // Newly visually-verified Batch 1..4 exact matches (deduplicated to canonical Balasana asset)
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("Restorative Child's Pose", "Back & Spine")
        )
        assertEquals(
            R.drawable.img_stretch_child,
            ArtworkMapper.getDrawableForStretch("child_pose_full_body", "Back & Spine")
        )
        assertEquals(
            R.drawable.img_stretch_b1_neck_side,
            ArtworkMapper.getDrawableForStretch("Neck Side Stretch (Right)", "Neck & Shoulders")
        )
        assertEquals(
            R.drawable.img_stretch_b1_neck_side,
            ArtworkMapper.getDrawableForStretch("Neck Side Stretch (Left)", "Neck & Shoulders")
        )
        assertEquals(
            R.drawable.img_stretch_b3_bhujangasana_cobra,
            ArtworkMapper.getDrawableForStretch("Bhujangasana (Cobra Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b3_setu_bandhasana_bridge,
            ArtworkMapper.getDrawableForStretch("Setu Bandhasana (Bridge Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b3_goddess_pose,
            ArtworkMapper.getDrawableForStretch("Utkata Konasana (Goddess Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_padmasana_lotus,
            ArtworkMapper.getDrawableForStretch("Padmasana (Lotus Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_chakrasana_wheel,
            ArtworkMapper.getDrawableForStretch("Chakrasana (Wheel Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_anulom_vilom_pranayama,
            ArtworkMapper.getDrawableForStretch("Anulom Vilom (Alternate Nostril Breathing)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_tolasana_scale,
            ArtworkMapper.getDrawableForStretch("Tolasana (Scale Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_kurmasana_tortoise,
            ArtworkMapper.getDrawableForStretch("Kurmasana (Tortoise Pose)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_hanumanasana_split,
            ArtworkMapper.getDrawableForStretch("Hanumanasana (Monkey Split - Right)", "Yoga Asanas")
        )
        assertEquals(
            R.drawable.img_stretch_b4_bharadvajasana_twist,
            ArtworkMapper.getDrawableForStretch("Bharadvajasana (Seated Sage Twist - Left)", "Yoga Asanas")
        )

        // Surya Namaskar continuous flow is now bundled and mapped
        assertEquals(
            R.drawable.img_stretch_surya_namaskar_flow,
            ArtworkMapper.getDrawableForStretch("Surya Namaskar (Sun Salutation Flow)", "Yoga Asanas")
        )

        // 4. Full 135-item deduplicated catalog safety & name/id agreement check
        val mappedStretches = StretchLibrary.ALL_100_STRETCHES.filter { stretch ->
            val byName = ArtworkMapper.getDrawableForStretch(stretch.name, stretch.category.title)
            val byId = ArtworkMapper.getDrawableForStretch(stretch.id, stretch.category.title)
            assertEquals("Name and ID mappings must agree for ${stretch.id}", byName, byId)
            byName != null
        }
        assertEquals("Catalog must map all 135 visually-verified unique stretches", 135, mappedStretches.size)
    }
}
