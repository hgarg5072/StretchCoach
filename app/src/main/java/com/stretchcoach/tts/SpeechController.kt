package com.stretchcoach.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

interface SpeechController {
    fun speak(text: String)
    fun stop()
    fun shutdown()
}

class AndroidSpeechController(context: Context) : SpeechController, TextToSpeech.OnInitListener {
    private var ready = false
    private var textToSpeech: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private val pending = ArrayDeque<String>()

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        textToSpeech?.language = Locale.getDefault()
        while (ready && pending.isNotEmpty()) {
            textToSpeech?.speak(pending.removeFirst(), TextToSpeech.QUEUE_ADD, null, "stretch-${System.nanoTime()}")
        }
    }

    override fun speak(text: String) {
        if (text.isBlank()) return
        if (!ready) {
            pending += text
            return
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "stretch-${System.nanoTime()}")
    }

    override fun stop() {
        pending.clear()
        textToSpeech?.stop()
    }

    override fun shutdown() {
        pending.clear()
        textToSpeech?.shutdown()
        textToSpeech = null
        ready = false
    }
}
