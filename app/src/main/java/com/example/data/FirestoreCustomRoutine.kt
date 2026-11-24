package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreStretchItem(
    val libraryId: String? = null,
    val name: String = "",
    val category: String = "General",
    val preparationSeconds: Int = 8,
    val holdSeconds: Int = 30,
    val prepCue: String? = null,
    val holdCue: String? = null,
    val notes: String? = null,
    val enabled: Boolean = true,
    val voiceCueEnabled: Boolean = true,
    val sortOrder: Int = 0
)

@IgnoreExtraProperties
data class FirestoreCustomRoutine(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val isCustom: Boolean = true,
    val stretches: List<FirestoreStretchItem> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
