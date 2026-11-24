package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestorePublicRoutine(
    @DocumentId
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "General",
    val stretches: List<FirestoreStretchItem> = emptyList(),
    val cloneCount: Long = 0L,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    val totalSeconds: Int
        get() = stretches.filter { it.enabled }.sumOf { it.preparationSeconds + it.holdSeconds }

    val formattedDuration: String
        get() {
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
        }
}

@IgnoreExtraProperties
data class FirestoreFollowedCreator(
    @DocumentId
    val creatorId: String = "",
    val creatorName: String = "",
    @ServerTimestamp
    val followedAt: Timestamp? = null
)
