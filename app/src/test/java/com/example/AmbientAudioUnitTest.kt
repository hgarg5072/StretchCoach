package com.example

import com.example.audio.AmbientAudioController
import com.example.data.AmbientSoundscape
import com.example.data.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmbientAudioUnitTest {

    @Test
    fun synthesizerProducesValidNonClippingStereoFramesForAllSoundscapes() {
        val sampleRate = 22050
        val activeModes = listOf(
            AmbientSoundscape.TANPURA_DRONE,
            AmbientSoundscape.SOFT_RAIN,
            AmbientSoundscape.MORNING_BREEZE,
            AmbientSoundscape.SINGING_BOWLS
        )

        activeModes.forEach { mode ->
            val synth = AmbientAudioController.SoundscapeSynthesizer(sampleRate)
            var peakL = 0.0
            var peakR = 0.0
            repeat(sampleRate / 2) { // 0.5 second sample
                val (l, r) = synth.nextFrame(mode)
                assertTrue("Left sample must be finite for $mode", l.isFinite())
                assertTrue("Right sample must be finite for $mode", r.isFinite())
                assertTrue("Left sample must not clip for $mode: $l", abs(l) <= 1.2)
                assertTrue("Right sample must not clip for $mode: $r", abs(r) <= 1.2)
                if (abs(l) > peakL) peakL = abs(l)
                if (abs(r) > peakR) peakR = abs(r)
            }
            assertTrue("Left channel must produce audible signal for $mode", peakL > 0.01)
            assertTrue("Right channel must produce audible signal for $mode", peakR > 0.01)
        }
    }

    @Test
    fun offSoundscapeProducesExactSilence() {
        val synth = AmbientAudioController.SoundscapeSynthesizer(22050)
        repeat(256) {
            val (l, r) = synth.nextFrame(AmbientSoundscape.OFF)
            assertEquals(0.0, l, 0.0)
            assertEquals(0.0, r, 0.0)
        }
    }

    @Test
    fun defaultUserSettingsEnableAmbientAndChimes() {
        val defaults = UserSettings()
        assertEquals(AmbientSoundscape.MEDITATION_MUSIC, defaults.ambientSoundscape)
        assertTrue(defaults.ambientVolume in 0.1f..0.8f)
        assertTrue(defaults.transitionChimesEnabled)
    }

    @Test
    fun onlineAudioLibraryParsesInternetArchiveSearchAndMetadata() {
        val searchJson = """
            {
              "response": {
                "docs": [
                  {
                    "identifier": "bansuri_raga_yaman",
                    "title": "Peaceful Bansuri Flute",
                    "creator": "Studio Artist",
                    "description": "<p>Calming yoga flute</p>"
                  }
                ]
              }
            }
        """.trimIndent()

        val parsed = com.example.audio.OnlineAudioLibrary.parseArchiveSearchJson(searchJson)
        assertEquals(1, parsed.size)
        assertEquals("bansuri_raga_yaman", parsed[0].identifier)
        assertEquals("Peaceful Bansuri Flute", parsed[0].title)
        assertEquals("Calming yoga flute", parsed[0].description)

        val metaJson = """
            {
              "files": [
                {"name": "cover.jpg", "size": "50000"},
                {"name": "raw_lossless.flac", "size": "45000000"},
                {"name": "Bansuri Morning.mp3", "size": "4200000"}
              ]
            }
        """.trimIndent()

        val picked = com.example.audio.OnlineAudioLibrary.pickBestAudioFileFromMetadata(metaJson, "bansuri_raga_yaman")
        assertTrue("Should pick MP3 audio URL", picked != null)
        assertEquals(
            "https://archive.org/download/bansuri_raga_yaman/Bansuri%20Morning.mp3",
            picked!!.first
        )
        assertEquals("mp3", picked.second)
    }
}
