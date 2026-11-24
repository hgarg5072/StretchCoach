package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreRoutineRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserRoutines(): Flow<List<FirestoreCustomRoutine>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/custom_routines"
        emitAll(
            firestore.collection("users").document(uid).collection("custom_routines")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FirestoreCustomRoutine::class.java) }
                .catch { error ->
                    if (error is Exception) {
                        handleFirestoreError(error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun fetchUserRoutines(): List<FirestoreCustomRoutine> = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        val path = "users/$uid/custom_routines"
        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("custom_routines")
                .get()
                .await()
            snapshot.toObjects(FirestoreCustomRoutine::class.java)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            throw e
        }
    }

    suspend fun saveRoutine(
        routineId: String,
        name: String,
        description: String,
        stretches: List<FirestoreStretchItem>,
        isCustom: Boolean = true,
        existingCreatedAt: Timestamp? = null
    ): String = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        val collection = firestore.collection("users").document(uid).collection("custom_routines")
        val docRef = if (routineId.isNotBlank()) collection.document(routineId) else collection.document()

        val data = mutableMapOf<String, Any>(
            "userId" to uid,
            "name" to name.trim(),
            "description" to description.trim(),
            "isCustom" to isCustom,
            "stretches" to stretches.map { s ->
                val stretchMap = mutableMapOf<String, Any>(
                    "name" to s.name,
                    "category" to s.category,
                    "preparationSeconds" to s.preparationSeconds,
                    "holdSeconds" to s.holdSeconds,
                    "enabled" to s.enabled,
                    "voiceCueEnabled" to s.voiceCueEnabled,
                    "sortOrder" to s.sortOrder
                )
                if (s.libraryId != null) stretchMap["libraryId"] = s.libraryId
                if (s.prepCue != null) stretchMap["prepCue"] = s.prepCue
                if (s.holdCue != null) stretchMap["holdCue"] = s.holdCue
                if (s.notes != null) stretchMap["notes"] = s.notes
                stretchMap
            },
            "updatedAt" to FieldValue.serverTimestamp()
        )

        if (existingCreatedAt != null) {
            data["createdAt"] = existingCreatedAt
        } else {
            data["createdAt"] = FieldValue.serverTimestamp()
        }

        try {
            docRef.set(data).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (existingCreatedAt != null) OperationType.UPDATE else OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteRoutine(routineId: String) = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        require(routineId.isNotBlank()) { "Routine ID must not be blank" }
        val docRef = firestore.collection("users").document(uid).collection("custom_routines").document(routineId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
