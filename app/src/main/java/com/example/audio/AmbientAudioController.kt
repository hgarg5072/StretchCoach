package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Log
import com.example.data.AmbientSoundscape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Offline, zero-asset procedural background audio engine for Stretch Coach.
 *
 * Synthesizes gentle, non-repeating yoga studio soundscapes (Tanpura drone, soft rain,
 * morning breeze, Tibetan singing bowls) and transition chimes via Android AudioTrack,
 * with smooth gain ramping and automatic ducking whenever TTS voice cues speak.
 */
class AmbientAudioController(private val appContext: Context? = null) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var currentSoundscape: AmbientSoundscape = AmbientSoundscape.OFF

    @Volatile
    private var customTrackPath: String = ""

    @Volatile
    private var userVolume: Float = 0.35f

    @Volatile
    private var isDucked: Boolean = false

    @Volatile
    private var isPlayingLoop: Boolean = false

    private var loopJob: Job? = null
    private var previewJob: Job? = null

    private val mediaPlayerLock = Any()
    private var mediaPlayer: MediaPlayer? = null
    private var activeMediaPath: String = ""

    fun start(soundscape: AmbientSoundscape, volume: Float, customFilePath: String = "") {
        previewJob?.cancel()
        currentSoundscape = soundscape
        customTrackPath = customFilePath
        userVolume = volume.coerceIn(0f, 1f)
        if (soundscape == AmbientSoundscape.OFF || userVolume <= 0.001f) {
            stop()
            return
        }
        if (soundscape == AmbientSoundscape.MEDITATION_MUSIC) {
            stopSynthLoop()
            startAssetMediaPlayer(DEFAULT_MEDITATION_ASSET)
            return
        }
        if (soundscape == AmbientSoundscape.CUSTOM_TRACK) {
            stopSynthLoop()
            startMediaPlayer(customFilePath)
            return
        }
        stopMediaPlayer()
        if (isPlayingLoop && loopJob?.isActive == true) {
            return
        }
        launchLoop()
    }

    fun updateSettings(
        soundscape: AmbientSoundscape,
        volume: Float,
        isSessionActive: Boolean,
        customFilePath: String = ""
    ) {
        currentSoundscape = soundscape
        customTrackPath = customFilePath
        userVolume = volume.coerceIn(0f, 1f)
        applyMediaPlayerVolume()

        if (soundscape == AmbientSoundscape.OFF || userVolume <= 0.001f) {
            stop()
        } else if (isSessionActive) {
            if (soundscape == AmbientSoundscape.MEDITATION_MUSIC) {
                stopSynthLoop()
                if (activeMediaPath != "asset://$DEFAULT_MEDITATION_ASSET" || mediaPlayer == null) {
                    startAssetMediaPlayer(DEFAULT_MEDITATION_ASSET)
                }
            } else if (soundscape == AmbientSoundscape.CUSTOM_TRACK) {
                stopSynthLoop()
                if (activeMediaPath != customFilePath || mediaPlayer == null) {
                    startMediaPlayer(customFilePath)
                }
            } else {
                stopMediaPlayer()
                if (!isPlayingLoop || loopJob?.isActive != true) {
                    launchLoop()
                }
            }
        }
    }

    /**
     * Plays a brief preview of a soundscape or custom track when changed in Settings while idle.
     */
    fun preview(
        soundscape: AmbientSoundscape,
        volume: Float,
        customFilePath: String = "",
        durationMs: Long = 3500L
    ) {
        previewJob?.cancel()
        if (soundscape == AmbientSoundscape.OFF || volume <= 0.001f) {
            stop()
            return
        }
        start(soundscape, volume, customFilePath)
        previewJob = scope.launch {
            delay(durationMs)
            stop()
        }
    }

    fun setDucked(ducked: Boolean) {
        isDucked = ducked
        applyMediaPlayerVolume()
    }

    fun pause() {
        previewJob?.cancel()
        stop()
    }

    fun stop() {
        stopSynthLoop()
        stopMediaPlayer()
    }

    private fun stopSynthLoop() {
        isPlayingLoop = false
        loopJob?.cancel()
        loopJob = null
    }

    private fun startAssetMediaPlayer(assetPath: String) {
        val ctx = appContext ?: return
        val key = "asset://$assetPath"
        synchronized(mediaPlayerLock) {
            if (mediaPlayer != null && activeMediaPath == key) {
                runCatching {
                    applyMediaPlayerVolumeLocked()
                    if (mediaPlayer?.isPlaying != true) {
                        mediaPlayer?.start()
                    }
                }
                return
            }
            stopMediaPlayerLocked()
            runCatching {
                val afd = ctx.assets.openFd(assetPath)
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    isLooping = true
                    prepare()
                    val v = effectiveMediaVolume()
                    setVolume(v, v)
                    start()
                }
                mediaPlayer = player
                activeMediaPath = key
            }.onFailure { e ->
                Log.w(TAG, "Built-in meditation audio playback failed for '$assetPath': ${e.message}")
                stopMediaPlayerLocked()
            }
        }
    }

    private fun startMediaPlayer(filePath: String) {
        if (filePath.isBlank() || !File(filePath).isFile) {
            stopMediaPlayer()
            return
        }
        synchronized(mediaPlayerLock) {
            if (mediaPlayer != null && activeMediaPath == filePath) {
                runCatching {
                    applyMediaPlayerVolumeLocked()
                    if (mediaPlayer?.isPlaying != true) {
                        mediaPlayer?.start()
                    }
                }
                return
            }
            stopMediaPlayerLocked()
            runCatching {
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(filePath)
                    isLooping = true
                    prepare()
                    val v = effectiveMediaVolume()
                    setVolume(v, v)
                    start()
                }
                mediaPlayer = player
                activeMediaPath = filePath
            }.onFailure { e ->
                Log.w(TAG, "Custom audio playback failed for '$filePath': ${e.message}")
                stopMediaPlayerLocked()
            }
        }
    }

    private fun effectiveMediaVolume(): Float =
        (userVolume * (if (isDucked) DUCK_MULTIPLIER else 1.0f)).coerceIn(0f, 1f)

    private fun applyMediaPlayerVolume() {
        synchronized(mediaPlayerLock) {
            applyMediaPlayerVolumeLocked()
        }
    }

    private fun applyMediaPlayerVolumeLocked() {
        runCatching {
            val v = effectiveMediaVolume()
            mediaPlayer?.setVolume(v, v)
        }
    }

    private fun stopMediaPlayer() {
        synchronized(mediaPlayerLock) {
            stopMediaPlayerLocked()
        }
    }

    private fun stopMediaPlayerLocked() {
        runCatching {
            mediaPlayer?.let { p ->
                if (p.isPlaying) p.stop()
                p.release()
            }
        }
        mediaPlayer = null
        activeMediaPath = ""
    }

    /**
     * Plays a soft, warm Tibetan singing bowl chime for phase transitions or routine completion.
     * Works even when background soundscape is OFF.
     */
    fun playTransitionChime(isCompletion: Boolean = false, volume: Float = 0.45f) {
        val clampedVol = volume.coerceIn(0.08f, 1f)
        scope.launch {
            runCatching {
                val durationSec = if (isCompletion) 2.6 else 1.5
                val numFrames = (SAMPLE_RATE * durationSec).toInt()
                val pcm = ShortArray(numFrames * 2)
                val twoPi = 2.0 * PI

                // Tibetan singing bowl partials (harmonic + warm metallic overtone)
                val freqs = if (isCompletion) {
                    doubleArrayOf(264.0, 396.0, 528.0, 792.0, 1056.0)
                } else {
                    doubleArrayOf(432.0, 864.0, 1296.0)
                }
                val amps = if (isCompletion) {
                    doubleArrayOf(0.42, 0.28, 0.30, 0.14, 0.07)
                } else {
                    doubleArrayOf(0.55, 0.25, 0.10)
                }
                val decays = if (isCompletion) {
                    doubleArrayOf(1.1, 1.35, 1.6, 2.2, 2.8)
                } else {
                    doubleArrayOf(1.9, 2.7, 3.6)
                }

                for (i in 0 until numFrames) {
                    val t = i.toDouble() / SAMPLE_RATE
                    // Soft 18ms attack to avoid any percussive click
                    val attack = (t / 0.018).coerceIn(0.0, 1.0)
                    var left = 0.0
                    var right = 0.0
                    for (k in freqs.indices) {
                        val env = attack * exp(-t * decays[k])
                        val shimmer = 1.0 + 0.06 * sin(twoPi * (1.8 + k * 0.4) * t)
                        val phaseL = twoPi * freqs[k] * t
                        val phaseR = twoPi * (freqs[k] + 0.6) * t
                        left += amps[k] * env * shimmer * sin(phaseL)
                        right += amps[k] * env * shimmer * sin(phaseR)
                    }
                    val scale = clampedVol * 0.65 * Short.MAX_VALUE
                    pcm[i * 2] = (left * scale).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    pcm[i * 2 + 1] = (right * scale).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(pcm.size * 2)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()
                delay((durationSec * 1000).toLong() + 120L)
                runCatching {
                    track.stop()
                    track.release()
                }
            }.onFailure { e ->
                Log.w(TAG, "Transition chime skipped: ${e.message}")
            }
        }
    }

    private fun launchLoop() {
        loopJob?.cancel()
        isPlayingLoop = true
        loopJob = scope.launch {
            var track: AudioTrack? = null
            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                if (minBuf <= 0) return@launch

                val chunkFrames = 2048
                val bufferBytes = maxOf(minBuf * 2, chunkFrames * 4 * 2)

                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(bufferBytes)
                    .build()

                track.play()

                val pcm = ShortArray(chunkFrames * 2)
                val synth = SoundscapeSynthesizer(SAMPLE_RATE)
                var currentGain = 0f

                while (isActive && isPlayingLoop) {
                    val mode = currentSoundscape
                    if (mode == AmbientSoundscape.OFF) {
                        break
                    }
                    val targetGain = userVolume * (if (isDucked) DUCK_MULTIPLIER else 1.0f)

                    for (i in 0 until chunkFrames) {
                        // Smooth gain ramping eliminates clicks on start, volume drag, and TTS ducking
                        val coeff = if (targetGain < currentGain) 0.0022f else 0.0007f
                        currentGain += (targetGain - currentGain) * coeff

                        val (rawL, rawR) = synth.nextFrame(mode)
                        val scaledL = (rawL * currentGain * MASTER_HEADROOM * Short.MAX_VALUE)
                            .toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        val scaledR = (rawR * currentGain * MASTER_HEADROOM * Short.MAX_VALUE)
                            .toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        pcm[i * 2] = scaledL
                        pcm[i * 2 + 1] = scaledR
                    }

                    val written = track.write(pcm, 0, pcm.size)
                    if (written < 0) break
                }
            } catch (e: Exception) {
                Log.w(TAG, "Ambient audio stream unavailable: ${e.message}")
            } finally {
                runCatching {
                    track?.pause()
                    track?.flush()
                    track?.stop()
                    track?.release()
                }
            }
        }
    }

    fun shutdown() {
        stop()
        previewJob?.cancel()
        scope.cancel()
    }

    /**
     * Stateful per-sample synthesizer producing smooth, non-looping stereo yoga soundscapes.
     */
    internal class SoundscapeSynthesizer(private val sampleRate: Int) {
        private var sampleIndex: Long = 0L
        private val rng = Random(42)

        // Filtered noise states for rain & breeze
        private var lpL1 = 0.0
        private var lpL2 = 0.0
        private var lpR1 = 0.0
        private var lpR2 = 0.0
        private var dropEnv = 0.0
        private var dropFreq = 1200.0
        private var dropPhase = 0.0
        private var dropPan = 0.5

        // Gentle breeze chime state
        private var chimeEnv = 0.0
        private var chimeFreq = 528.0
        private var chimePhase = 0.0
        private var chimePan = 0.5
        private val pentatonic = doubleArrayOf(392.0, 440.0, 523.25, 587.33, 659.25, 783.99)

        private val twoPi = 2.0 * PI

        fun nextFrame(mode: AmbientSoundscape): Pair<Double, Double> {
            val t = sampleIndex.toDouble() / sampleRate
            sampleIndex++

            return when (mode) {
                AmbientSoundscape.OFF, AmbientSoundscape.CUSTOM_TRACK, AmbientSoundscape.MEDITATION_MUSIC -> 0.0 to 0.0
                AmbientSoundscape.TANPURA_DRONE -> synthTanpura(t)
                AmbientSoundscape.SOFT_RAIN -> synthRain(t)
                AmbientSoundscape.MORNING_BREEZE -> synthBreeze(t)
                AmbientSoundscape.SINGING_BOWLS -> synthSingingBowls(t)
            }
        }

        private fun synthTanpura(t: Double): Pair<Double, Double> {
            // Classic 4-string Tanpura cycle (Pa - Sa - Sa' - Low Sa) over 7.2 seconds
            val cycle = 7.2
            val freqs = doubleArrayOf(98.0, 130.81, 131.08, 65.41)
            val offsets = doubleArrayOf(0.0, 1.8, 3.6, 5.4)
            var left = 0.0
            var right = 0.0

            for (s in 0 until 4) {
                val localT = ((t + cycle - offsets[s]) % cycle)
                val attack = (localT / 0.14).coerceIn(0.0, 1.0)
                val decay = exp(-localT * 0.36)
                val env = attack * decay

                val f = freqs[s]
                // Warm rich jawari-like harmonic spectrum (softened upper harmonics)
                val sig = 0.52 * sin(twoPi * f * t) +
                    0.26 * sin(twoPi * f * 2.0 * t + 0.3) +
                    0.13 * sin(twoPi * f * 3.0 * t + 0.7) +
                    0.06 * sin(twoPi * f * 4.0 * t) +
                    0.03 * sin(twoPi * f * 5.0 * t)

                val panL = if (s % 2 == 0) 0.62 else 0.45
                val panR = if (s % 2 == 0) 0.45 else 0.62
                left += sig * env * panL
                right += sig * env * panR
            }

            // Soft low-pass warmth
            lpL1 += 0.25 * (left - lpL1)
            lpR1 += 0.25 * (right - lpR1)
            return (lpL1 * 0.48) to (lpR1 * 0.48)
        }

        private fun synthRain(t: Double): Pair<Double, Double> {
            // Dual-stage low-pass filtered pink/brown noise for warm, non-harsh rainfall
            val wL = rng.nextDouble(-1.0, 1.0)
            val wR = rng.nextDouble(-1.0, 1.0)
            lpL1 += 0.18 * (wL - lpL1)
            lpL2 += 0.07 * (lpL1 - lpL2)
            lpR1 += 0.18 * (wR - lpR1)
            lpR2 += 0.07 * (lpR1 - lpR2)

            val swell = 0.82 + 0.18 * sin(twoPi * 0.08 * t)
            var left = (lpL1 * 0.35 + lpL2 * 0.65) * swell
            var right = (lpR1 * 0.35 + lpR2 * 0.65) * swell

            // Occasional gentle studio window droplets
            if (dropEnv < 0.002 && rng.nextDouble() < 0.00018) {
                dropEnv = rng.nextDouble(0.08, 0.18)
                dropFreq = rng.nextDouble(900.0, 1800.0)
                dropPan = rng.nextDouble(0.2, 0.8)
            }
            if (dropEnv > 0.001) {
                dropPhase += twoPi * dropFreq / sampleRate
                val drop = sin(dropPhase) * dropEnv
                left += drop * (1.0 - dropPan)
                right += drop * dropPan
                dropEnv *= 0.9965
            }

            return (left * 0.72) to (right * 0.72)
        }

        private fun synthBreeze(t: Double): Pair<Double, Double> {
            // Very soft slow-breathing wind bed
            val wL = rng.nextDouble(-1.0, 1.0)
            val wR = rng.nextDouble(-1.0, 1.0)
            lpL1 += 0.045 * (wL - lpL1)
            lpL2 += 0.025 * (lpL1 - lpL2)
            lpR1 += 0.045 * (wR - lpR1)
            lpR2 += 0.025 * (lpR1 - lpR2)

            val windSwellL = 0.55 + 0.45 * sin(twoPi * 0.09 * t)
            val windSwellR = 0.55 + 0.45 * cos(twoPi * 0.085 * t)
            var left = lpL2 * windSwellL * 1.15
            var right = lpR2 * windSwellR * 1.15

            // Occasional distant pentatonic wind chime
            if (chimeEnv < 0.002 && rng.nextDouble() < 0.000045) {
                chimeEnv = rng.nextDouble(0.14, 0.24)
                chimeFreq = pentatonic[rng.nextInt(pentatonic.size)]
                chimePan = rng.nextDouble(0.2, 0.8)
            }
            if (chimeEnv > 0.001) {
                chimePhase += twoPi * chimeFreq / sampleRate
                val chime = (sin(chimePhase) * 0.8 + sin(chimePhase * 2.0) * 0.2) * chimeEnv
                left += chime * (1.0 - chimePan)
                right += chime * chimePan
                chimeEnv *= 0.99985
            }

            return (left * 0.65) to (right * 0.65)
        }

        private fun synthSingingBowls(t: Double): Pair<Double, Double> {
            // Deep Om resonant Tibetan bowl (136.1 Hz fundamental + octave + fifth + gentle 2Hz shimmer)
            val strikePeriod = 9.5
            val localT = t % strikePeriod
            val attack = (localT / 0.25).coerceIn(0.0, 1.0)
            val env = attack * (0.38 + 0.62 * exp(-localT * 0.22))
            val breathSwell = 0.85 + 0.15 * sin(twoPi * 0.11 * t)

            val f1 = 136.1
            val f2 = 272.2
            val f3 = 408.3
            val left = (0.50 * sin(twoPi * f1 * t) +
                0.28 * sin(twoPi * f2 * t) +
                0.12 * sin(twoPi * f3 * t)) * env * breathSwell

            // Subtle stereo beating (+0.8 Hz) for warm spatial immersion
            val right = (0.50 * sin(twoPi * (f1 + 0.8) * t) +
                0.28 * sin(twoPi * (f2 + 1.2) * t) +
                0.12 * sin(twoPi * (f3 + 0.6) * t)) * env * breathSwell

            return (left * 0.42) to (right * 0.42)
        }
    }

    companion object {
        private const val TAG = "AmbientAudioController"
        const val DEFAULT_MEDITATION_ASSET = "ambient_audio/default_meditation_music.m4a"
        private const val SAMPLE_RATE = 22050
        private const val DUCK_MULTIPLIER = 0.22f
        private const val MASTER_HEADROOM = 0.55f
    }
}
