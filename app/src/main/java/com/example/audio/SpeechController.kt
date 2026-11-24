package com.example.audio

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.AsanaPronunciationStyle
import com.example.data.StretchEntity
import com.example.data.UserSettings
import com.example.data.VoiceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class VoiceEngineInfo(
    val voiceName: String = "Detecting...",
    val localeTag: String = "en-IN",
    val isIndianVoice: Boolean = false,
    val hasSanskritDevanagariVoice: Boolean = false,
    val sanskritVoiceName: String = "",
    val isReady: Boolean = false,
    val enginePackage: String = ""
)

class SpeechController(private val context: Context) {

    private var englishTts: TextToSpeech? = null
    private var sanskritTts: TextToSpeech? = null

    @Volatile
    private var isEnglishReady = false
    @Volatile
    private var isSanskritReady = false
    @Volatile
    private var hasDevanagariVoice = false

    private val pendingSpeechQueue = mutableListOf<Pair<String, Int>>()

    private val _voiceEngineInfo = MutableStateFlow(VoiceEngineInfo())
    val voiceEngineInfo: StateFlow<VoiceEngineInfo> = _voiceEngineInfo.asStateFlow()

    @Volatile
    private var onPrepSpeechDoneListener: (() -> Unit)? = null
    @Volatile
    private var pendingEnglishFollowUp: Pair<String, String>? = null
    @Volatile
    var onSpeakingStateChanged: ((Boolean) -> Unit)? = null

    private val englishLocaleIn = Locale.Builder().setLanguage("en").setRegion("IN").build()
    private val hindiLocaleIn = Locale.Builder().setLanguage("hi").setRegion("IN").build()

    init {
        initEngines()
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun initEngines() {
        val preferredEngine = if (isPackageInstalled(GOOGLE_TTS_PACKAGE)) {
            GOOGLE_TTS_PACKAGE
        } else {
            null
        }

        try {
            englishTts = if (preferredEngine != null) {
                TextToSpeech(context.applicationContext, { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        onEnglishInit(status)
                    } else {
                        // Fallback to system default TTS engine if Google TTS fails
                        englishTts = TextToSpeech(context.applicationContext) { fallbackStatus ->
                            onEnglishInit(fallbackStatus)
                        }
                    }
                }, preferredEngine)
            } else {
                TextToSpeech(context.applicationContext) { status ->
                    onEnglishInit(status)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize English TextToSpeech", e)
        }

        try {
            sanskritTts = if (preferredEngine != null) {
                TextToSpeech(context.applicationContext, { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        onSanskritInit(status)
                    } else {
                        sanskritTts = TextToSpeech(context.applicationContext) { fallbackStatus ->
                            onSanskritInit(fallbackStatus)
                        }
                    }
                }, preferredEngine)
            } else {
                TextToSpeech(context.applicationContext) { status ->
                    onSanskritInit(status)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Sanskrit/Hindi TextToSpeech", e)
        }
    }

    /**
     * Re-scans installed TTS voices (e.g. after returning from Android TTS Settings).
     */
    fun refreshVoices() {
        if (isEnglishReady) {
            englishTts?.let { configureEnglishEngine(it) }
        }
        if (isSanskritReady) {
            sanskritTts?.let { configureSanskritEngine(it) }
        }
    }

    private fun isVoiceInstalled(voice: Voice): Boolean {
        val features = voice.features ?: emptySet()
        return !features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
    }

    private fun selectBestVoice(
        voices: List<Voice>,
        matcher: (Voice) -> Boolean
    ): Voice? {
        return voices
            .filter { matcher(it) && isVoiceInstalled(it) }
            .sortedWith(
                compareBy<Voice> { it.isNetworkConnectionRequired } // Prefer offline/local voices
                    .thenByDescending { it.quality }                // Prefer higher quality
            )
            .firstOrNull()
    }

    private fun onEnglishInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "English TTS init failed with status $status")
            return
        }
        val engine = englishTts ?: return
        configureEnglishEngine(engine)

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onSpeakingStateChanged?.invoke(true)
            }
            override fun onDone(utteranceId: String?) {
                onSpeakingStateChanged?.invoke(false)
                if (utteranceId?.startsWith("intro_prep") == true) {
                    val listener = onPrepSpeechDoneListener
                    onPrepSpeechDoneListener = null
                    listener?.invoke()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onSpeakingStateChanged?.invoke(false)
                if (utteranceId?.startsWith("intro_prep") == true) {
                    val listener = onPrepSpeechDoneListener
                    onPrepSpeechDoneListener = null
                    listener?.invoke()
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                onSpeakingStateChanged?.invoke(false)
                if (utteranceId?.startsWith("intro_prep") == true) {
                    val listener = onPrepSpeechDoneListener
                    onPrepSpeechDoneListener = null
                    listener?.invoke()
                }
            }
        })

        isEnglishReady = true

        synchronized(pendingSpeechQueue) {
            pendingSpeechQueue.forEach { (text, queueMode) ->
                engine.speak(text, queueMode, null, "utterance_${System.currentTimeMillis()}")
            }
            pendingSpeechQueue.clear()
        }
    }

    private fun configureEnglishEngine(engine: TextToSpeech) {
        var selectedVoiceName = "System Default"
        var activeLocaleTag = "en-US"
        var isIndianEnglish = false

        try {
            val availableVoices = engine.voices?.toList() ?: emptyList()
            val enInVoice = selectBestVoice(availableVoices) { voice ->
                (voice.locale.language.equals("en", ignoreCase = true) &&
                    voice.locale.country.equals("IN", ignoreCase = true)) ||
                    voice.name.contains("en-in", ignoreCase = true) ||
                    voice.name.contains("en_in", ignoreCase = true)
            }

            if (enInVoice != null) {
                engine.setLanguage(enInVoice.locale)
                engine.voice = enInVoice
                selectedVoiceName = enInVoice.name
                activeLocaleTag = enInVoice.locale.toLanguageTag()
                isIndianEnglish = true
            } else {
                val enInResult = engine.isLanguageAvailable(englishLocaleIn)
                if (enInResult >= TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    engine.setLanguage(englishLocaleIn)
                    selectedVoiceName = engine.voice?.name ?: "Indian English (en-IN)"
                    activeLocaleTag = "en-IN"
                    isIndianEnglish = true
                } else {
                    val fallbackVoice = selectBestVoice(availableVoices) { voice ->
                        voice.locale.language.equals("en", ignoreCase = true)
                    }
                    if (fallbackVoice != null) {
                        engine.setLanguage(fallbackVoice.locale)
                        engine.voice = fallbackVoice
                        selectedVoiceName = fallbackVoice.name
                        activeLocaleTag = fallbackVoice.locale.toLanguageTag()
                    } else {
                        val ukResult = engine.setLanguage(Locale.UK)
                        if (ukResult == TextToSpeech.LANG_MISSING_DATA || ukResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            engine.setLanguage(Locale.getDefault())
                        }
                        selectedVoiceName = engine.voice?.name ?: "Standard English"
                        activeLocaleTag = engine.voice?.locale?.toLanguageTag() ?: Locale.getDefault().toLanguageTag()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring English TTS voice: ${e.message}", e)
        }

        engine.setPitch(1.0f)
        engine.setSpeechRate(0.92f)

        val current = _voiceEngineInfo.value
        _voiceEngineInfo.value = current.copy(
            voiceName = selectedVoiceName,
            localeTag = activeLocaleTag,
            isIndianVoice = isIndianEnglish || hasDevanagariVoice,
            isReady = true,
            enginePackage = engine.defaultEngine ?: current.enginePackage
        )
    }

    private fun onSanskritInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Log.w(TAG, "Sanskrit/Hindi TTS init failed with status $status; using phonetic English fallback")
            return
        }
        val engine = sanskritTts ?: return
        configureSanskritEngine(engine)

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onSpeakingStateChanged?.invoke(true)
            }
            override fun onDone(utteranceId: String?) {
                if (utteranceId?.startsWith("sanskrit_prep") == true) {
                    handleSanskritUtteranceFinished()
                } else {
                    onSpeakingStateChanged?.invoke(false)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (utteranceId?.startsWith("sanskrit_prep") == true) {
                    handleSanskritUtteranceFinished()
                } else {
                    onSpeakingStateChanged?.invoke(false)
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                if (utteranceId?.startsWith("sanskrit_prep") == true) {
                    handleSanskritUtteranceFinished()
                } else {
                    onSpeakingStateChanged?.invoke(false)
                }
            }
        })

        isSanskritReady = true
    }

    private fun handleSanskritUtteranceFinished() {
        val followUp = pendingEnglishFollowUp
        pendingEnglishFollowUp = null
        if (followUp != null) {
            val (englishText, utteranceId) = followUp
            speak(englishText, TextToSpeech.QUEUE_FLUSH, utteranceId)
        } else {
            onSpeakingStateChanged?.invoke(false)
            val listener = onPrepSpeechDoneListener
            onPrepSpeechDoneListener = null
            listener?.invoke()
        }
    }

    private fun configureSanskritEngine(engine: TextToSpeech) {
        var foundHindi = false
        var hindiVoiceName = ""

        try {
            val availableVoices = engine.voices?.toList() ?: emptyList()
            val hiVoice = selectBestVoice(availableVoices) { voice ->
                voice.locale.language.equals("hi", ignoreCase = true) ||
                    voice.name.contains("hi-in", ignoreCase = true) ||
                    voice.name.contains("hi_in", ignoreCase = true)
            }

            if (hiVoice != null) {
                engine.setLanguage(hiVoice.locale)
                engine.voice = hiVoice
                foundHindi = true
                hindiVoiceName = hiVoice.name
                Log.d(TAG, "Assigned Devanagari/Hindi voice for Sanskrit: ${hiVoice.name}")
            } else {
                val hiResult = engine.isLanguageAvailable(hindiLocaleIn)
                if (hiResult >= TextToSpeech.LANG_AVAILABLE) {
                    engine.setLanguage(hindiLocaleIn)
                    foundHindi = true
                    hindiVoiceName = engine.voice?.name ?: "Hindi (hi-IN)"
                    Log.d(TAG, "Assigned hi-IN locale for Sanskrit Devanagari")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring Hindi/Sanskrit TTS voice: ${e.message}", e)
        }

        hasDevanagariVoice = foundHindi
        if (foundHindi) {
            // Slightly slower, crisp cadence for clear Sanskrit asana enunciation
            engine.setPitch(1.0f)
            engine.setSpeechRate(0.86f)
        }

        val current = _voiceEngineInfo.value
        _voiceEngineInfo.value = current.copy(
            hasSanskritDevanagariVoice = foundHindi,
            sanskritVoiceName = hindiVoiceName,
            isIndianVoice = current.isIndianVoice || foundHindi
        )
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH, utteranceId: String? = null) {
        if (text.isBlank()) return
        val id = utteranceId ?: "utterance_${System.currentTimeMillis()}_${(1..1000).random()}"
        if (isEnglishReady && englishTts != null) {
            englishTts?.speak(text, queueMode, null, id)
        } else {
            synchronized(pendingSpeechQueue) {
                if (queueMode == TextToSpeech.QUEUE_FLUSH) {
                    pendingSpeechQueue.clear()
                }
                pendingSpeechQueue.add(text to queueMode)
            }
        }
    }

    /**
     * Computes an adaptive speech rate for stretch titles so very long custom names
     * don't drag on, while short/normal names remain calm and unhurried.
     */
    private fun adaptiveTitleSpeechRate(textLength: Int, baseRate: Float): Float {
        return when {
            textLength > 75 -> (baseRate + 0.18f).coerceAtMost(1.15f)
            textLength > 40 -> (baseRate + 0.08f).coerceAtMost(1.05f)
            else -> baseRate
        }
    }

    /**
     * Truncates overly long custom stretch titles cleanly at a word boundary
     * so TTS announcements stay concise (max ~120 chars).
     */
    private fun capSpokenTitle(text: String, maxChars: Int = MAX_SPOKEN_TITLE_CHARS): String {
        val trimmed = text.trim()
        if (trimmed.length <= maxChars) return trimmed
        val cut = trimmed.substring(0, maxChars)
        val lastSpace = cut.lastIndexOf(' ')
        return if (lastSpace > maxChars / 2) cut.substring(0, lastSpace).trimEnd(',', '-', ' ') else cut
    }

    /**
     * Estimates a safe watchdog timeout (ms) for a stretch title announcement so
     * long names are never cut off mid-sentence before countdown begins.
     */
    fun estimateAnnouncementTimeoutMs(rawName: String, style: AsanaPronunciationStyle): Long {
        val preview = capSpokenTitle(
            SanskritPhonetics.formatForSpeech(rawName, style, hasDevanagariVoice)
        )
        // ~95 ms per character at calm cadence + 3.5s buffer, bounded to [5s, 15s]
        return ((preview.length * 95L) + 3500L).coerceIn(5000L, 15000L)
    }

    /**
     * Speaks an asana or stretch name using authentic Devanagari (hi-IN) for the Sanskrit portion
     * when available, seamlessly followed by the English voice for translations and side cues.
     */
    private fun speakAsanaTitle(
        rawName: String,
        style: AsanaPronunciationStyle,
        onComplete: (() -> Unit)? = null,
        englishPrefix: String = "",
        englishSuffix: String = ""
    ) {
        val plan = SanskritPhonetics.buildSpokenPlan(rawName)
        val useDevanagari = hasDevanagariVoice && isSanskritReady && sanskritTts != null &&
            plan.sanskritDevanagari != null &&
            (style == AsanaPronunciationStyle.BILINGUAL || style == AsanaPronunciationStyle.SANSKRIT_ONLY)

        val prepUtteranceId = "intro_prep_${System.currentTimeMillis()}"

        if (useDevanagari) {
            val cappedSanskrit = capSpokenTitle(plan.sanskritDevanagari!!)
            val baseFollowUp = capSpokenTitle(
                when (style) {
                    AsanaPronunciationStyle.BILINGUAL -> {
                        listOfNotNull(
                            plan.englishName?.takeIf { it.isNotBlank() },
                            plan.sideSuffix.takeIf { it.isNotBlank() }
                        ).joinToString(", ")
                    }
                    AsanaPronunciationStyle.SANSKRIT_ONLY -> plan.sideSuffix
                    else -> ""
                }
            )
            val englishFollowUpText = listOf(baseFollowUp, englishSuffix)
                .filter { it.isNotBlank() }
                .joinToString(", ")

            val totalChars = cappedSanskrit.length + englishFollowUpText.length
            sanskritTts?.setSpeechRate(adaptiveTitleSpeechRate(totalChars, DEFAULT_SANSKRIT_RATE))
            englishTts?.setSpeechRate(adaptiveTitleSpeechRate(totalChars, DEFAULT_ENGLISH_RATE))

            onPrepSpeechDoneListener = onComplete
            pendingEnglishFollowUp = if (englishFollowUpText.isNotBlank()) {
                englishFollowUpText to prepUtteranceId
            } else {
                null
            }

            val sanskritId = "sanskrit_prep_${System.currentTimeMillis()}"
            sanskritTts?.speak(cappedSanskrit, TextToSpeech.QUEUE_FLUSH, null, sanskritId)
        } else {
            // Use improved English-TTS phonetic respelling or English-only name
            val titleText = capSpokenTitle(
                SanskritPhonetics.formatForSpeech(
                    rawName = rawName,
                    style = style,
                    isIndianVoiceInstalled = false
                )
            )
            val fullSpoken = buildString {
                if (englishPrefix.isNotBlank()) {
                    append(englishPrefix.trim())
                    append(" ")
                }
                append(titleText)
                if (englishSuffix.isNotBlank()) {
                    append(", ")
                    append(englishSuffix.trim())
                }
            }
            englishTts?.setSpeechRate(adaptiveTitleSpeechRate(fullSpoken.length, DEFAULT_ENGLISH_RATE))
            onPrepSpeechDoneListener = onComplete
            pendingEnglishFollowUp = null
            speak(fullSpoken, TextToSpeech.QUEUE_FLUSH, prepUtteranceId)
        }
    }

    fun testSamplePronunciation(
        poseName: String = "Bhujangasana (Cobra Pose)",
        style: AsanaPronunciationStyle = AsanaPronunciationStyle.BILINGUAL
    ) {
        stop()
        speakAsanaTitle(poseName, style, onComplete = null)
    }

    fun announcePreparation(
        stretch: StretchEntity,
        settings: UserSettings,
        onComplete: () -> Unit = {}
    ) {
        if (!settings.spokenCuesEnabled) {
            onComplete()
            return
        }
        stop()
        val combinedName = if (!stretch.indianName.isNullOrBlank() &&
            !stretch.name.contains(stretch.indianName, ignoreCase = true)
        ) {
            "${stretch.name} (${stretch.indianName})"
        } else {
            stretch.name
        }
        val prepDurationSuffix = if (stretch.preparationSeconds > 0) {
            "in ${stretch.preparationSeconds} seconds"
        } else {
            ""
        }
        speakAsanaTitle(
            rawName = combinedName,
            style = settings.asanaPronunciationStyle,
            onComplete = onComplete,
            englishPrefix = "Prepare for",
            englishSuffix = prepDurationSuffix
        )
    }

    fun announceHold(stretch: StretchEntity, settings: UserSettings) {
        if (!settings.spokenCuesEnabled) return
        stop()
        englishTts?.setSpeechRate(DEFAULT_ENGLISH_RATE)

        val holdIntro = if (stretch.holdSeconds > 0) {
            "Hold for ${stretch.holdSeconds} seconds"
        } else {
            "Hold"
        }
        speak(holdIntro, TextToSpeech.QUEUE_FLUSH)

        if (stretch.voiceCueEnabled && settings.coachingCuesEnabled && settings.voiceMode != VoiceMode.QUIET) {
            val holdInstruction = stretch.effectiveHoldCue
            if (holdInstruction.isNotBlank()) {
                speak(holdInstruction, TextToSpeech.QUEUE_ADD)
            }
        }
    }

    fun announceCountdown(secondsRemaining: Int, @Suppress("UNUSED_PARAMETER") totalSeconds: Int, settings: UserSettings, @Suppress("UNUSED_PARAMETER") isHoldPhase: Boolean) {
        if (!settings.spokenCuesEnabled || !settings.countdownAnnouncements) return
        if (secondsRemaining !in 1..5) return
        englishTts?.setSpeechRate(DEFAULT_ENGLISH_RATE)
        speak("$secondsRemaining", TextToSpeech.QUEUE_FLUSH)
    }

    fun announceHalfway(settings: UserSettings) {
        if (!settings.spokenCuesEnabled || !settings.halfwayCue) return
        speak("Halfway", TextToSpeech.QUEUE_ADD)
    }

    fun announceNextStretch(nextStretchName: String, settings: UserSettings) {
        // Next stretch name announcement removed as per user request
    }

    fun announceComplete(settings: UserSettings) {
        if (!settings.spokenCuesEnabled) return
        stop()
        speak("Routine complete! Great job.", TextToSpeech.QUEUE_FLUSH)
    }

    fun stop() {
        try {
            onSpeakingStateChanged?.invoke(false)
            onPrepSpeechDoneListener = null
            pendingEnglishFollowUp = null
            synchronized(pendingSpeechQueue) {
                pendingSpeechQueue.clear()
            }
            englishTts?.stop()
            sanskritTts?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            stop()
            englishTts?.shutdown()
            sanskritTts?.shutdown()
            englishTts = null
            sanskritTts = null
            isEnglishReady = false
            isSanskritReady = false
            hasDevanagariVoice = false
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
    }

    companion object {
        private const val TAG = "SpeechController"
        private const val GOOGLE_TTS_PACKAGE = "com.google.android.tts"
        private const val DEFAULT_ENGLISH_RATE = 0.92f
        private const val DEFAULT_SANSKRIT_RATE = 0.86f
        private const val MAX_SPOKEN_TITLE_CHARS = 120

        fun launchInstallTtsData(context: Context) {
            try {
                val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                launchTtsSettings(context)
            }
        }

        fun launchTtsSettings(context: Context) {
            try {
                val intent = Intent("com.android.settings.TTS_SETTINGS").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }

        fun launchPlayStoreGoogleTts(context: Context) {
            val pkg = "com.google.android.tts"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkg")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }
    }
}
