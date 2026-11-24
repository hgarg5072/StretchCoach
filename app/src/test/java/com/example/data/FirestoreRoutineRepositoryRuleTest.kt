package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreRoutineRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun unauthenticatedUser_cannotAccessRoutines() = runBlocking {
        auth.signOut()
        val repository = FirestoreRoutineRepository(firestore, auth)
        try {
            repository.fetchUserRoutines()
            fail("Expected IllegalStateException for unauthenticated user")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }

    @Test
    fun authenticatedUser_canSaveAndFetchRoutines() = runBlocking {
        val uniqueSuffix = System.currentTimeMillis()
        val aliceUid = signInTestUser("alice_${uniqueSuffix}@test.com")
        val repository = FirestoreRoutineRepository(firestore, auth)

        val stretch = FirestoreStretchItem(
            libraryId = "neck_roll",
            name = "Neck Gentle Roll",
            category = "Neck",
            preparationSeconds = 5,
            holdSeconds = 25,
            prepCue = "Relax shoulders",
            holdCue = "Breathe easy"
        )

        val docId = "routine_${uniqueSuffix}"
        val routineId = repository.saveRoutine(
            routineId = docId,
            name = "Desk Break Stretch",
            description = "Relieves upper body fatigue",
            stretches = listOf(stretch),
            isCustom = true
        )

        assertEquals(docId, routineId)

        val routines = repository.fetchUserRoutines()
        assertEquals(1, routines.size)
        val saved = routines[0]
        assertEquals(docId, saved.id)
        assertEquals("Desk Break Stretch", saved.name)
        assertEquals(aliceUid, saved.userId)
        assertEquals(1, saved.stretches.size)
        assertEquals("Neck Gentle Roll", saved.stretches[0].name)
        assertNotNull(saved.createdAt)
    }

    @Test
    fun authenticatedUser_canDeleteRoutine() = runBlocking {
        val uniqueSuffix = System.currentTimeMillis()
        signInTestUser("alice_del_${uniqueSuffix}@test.com")
        val repository = FirestoreRoutineRepository(firestore, auth)

        val docId = "to_delete_${uniqueSuffix}"
        repository.saveRoutine(
            routineId = docId,
            name = "Temporary Routine",
            description = "Will be deleted",
            stretches = emptyList()
        )

        var list = repository.fetchUserRoutines()
        assertEquals(1, list.size)

        repository.deleteRoutine(docId)
        list = repository.fetchUserRoutines()
        assertEquals(0, list.size)
    }
}
