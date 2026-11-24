package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreCommunityRepository(
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
            ?: throw IllegalStateException("User must be signed in with Google before performing this action.")
    }

    fun observePublicRoutines(): Flow<List<FirestorePublicRoutine>> = flow {
        if (auth.currentUser == null) {
            emit(emptyList())
            return@flow
        }
        val path = "public_routines"
        emitAll(
            firestore.collection(path)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FirestorePublicRoutine::class.java) }
                .catch { error ->
                    if (error is Exception) {
                        handleFirestoreError(error, OperationType.LIST, path)
                    }
                    emit(emptyList())
                }
        )
    }

    fun observeFollowedCreators(): Flow<Set<String>> = flow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            emit(emptySet())
            return@flow
        }
        val path = "users/$uid/following"
        emitAll(
            firestore.collection("users").document(uid).collection("following")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.map { it.id }.toSet()
                }
                .catch { error ->
                    if (error is Exception) {
                        handleFirestoreError(error, OperationType.LIST, path)
                    }
                    emit(emptySet())
                }
        )
    }

    suspend fun publishRoutine(
        routineName: String,
        description: String,
        category: String,
        stretches: List<FirestoreStretchItem>
    ): String = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        val authorName = auth.currentUser?.displayName?.ifBlank { "Stretch Coach User" }
            ?: auth.currentUser?.email?.substringBefore("@")
            ?: "Community Creator"

        val collection = firestore.collection("public_routines")
        val docRef = collection.document()

        val data = mapOf(
            "authorId" to uid,
            "authorName" to authorName,
            "name" to routineName.trim(),
            "description" to description.trim(),
            "category" to category.trim().ifBlank { "General" },
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
            "cloneCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(data).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun unpublishRoutine(routineId: String) = withContext(Dispatchers.IO) {
        requireUserId()
        require(routineId.isNotBlank())
        val docRef = firestore.collection("public_routines").document(routineId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun incrementCloneCount(routineId: String) = withContext(Dispatchers.IO) {
        require(routineId.isNotBlank())
        val docRef = firestore.collection("public_routines").document(routineId)
        try {
            docRef.update("cloneCount", FieldValue.increment(1)).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Non-fatal if count fails to update
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    suspend fun toggleFollowCreator(creatorId: String, creatorName: String, isFollowing: Boolean) = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        require(creatorId.isNotBlank())
        val docRef = firestore.collection("users").document(uid).collection("following").document(creatorId)
        try {
            if (isFollowing) {
                docRef.delete().await()
            } else {
                val data = mapOf(
                    "creatorId" to creatorId,
                    "creatorName" to creatorName,
                    "followedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (isFollowing) OperationType.DELETE else OperationType.CREATE, docRef.path)
            throw e
        }
    }
}
