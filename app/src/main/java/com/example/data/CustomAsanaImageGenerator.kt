package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Custom asana image generation is intentionally disabled so the app only uses
 * free/offline resources and never invokes paid Firebase AI / Blaze endpoints.
 */
object CustomAsanaImageGenerator {

    private val _generatingKeys = MutableStateFlow<Set<String>>(emptySet())
    val generatingKeys: StateFlow<Set<String>> = _generatingKeys.asStateFlow()

    @Suppress("UNUSED_PARAMETER")
    suspend fun generateAndSave(
        context: Context,
        uniqueKey: String,
        asanaName: String,
        indianName: String? = null,
        notes: String? = null
    ): String? = null
}
