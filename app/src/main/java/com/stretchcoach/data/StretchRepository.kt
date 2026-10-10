package com.stretchcoach.data

import kotlinx.coroutines.flow.Flow

class StretchRepository(private val dao: StretchDao) {
    val stretches: Flow<List<StretchEntity>> = dao.observeAll()
    val enabledStretches: Flow<List<StretchEntity>> = dao.observeEnabled()

    suspend fun seedDefaultsIfEmpty() {
        val existingNames = dao.names().map { it.lowercase() }.toSet()
        var nextOrder = dao.count()
        defaultStretches.forEach { stretch ->
            if (stretch.name.lowercase() !in existingNames) {
                dao.insert(stretch.copy(sortOrder = nextOrder))
                nextOrder += 1
            }
        }
    }

    suspend fun save(stretch: StretchEntity) {
        if (stretch.id == 0L) dao.insert(stretch) else dao.update(stretch)
    }

    suspend fun delete(stretch: StretchEntity) = dao.delete(stretch)

    suspend fun setEnabled(id: Long, enabled: Boolean) = dao.setEnabled(id, enabled)

    suspend fun reorder(ids: List<Long>) = dao.updateSortOrders(ids)

    private val defaultStretches = listOf(
        StretchEntity(
            name = "Neck side stretch",
            preparationSeconds = 8,
            holdSeconds = 25,
            notes = "Keep shoulders relaxed and breathe slowly.",
            enabled = true,
            sortOrder = 0
        ),
        StretchEntity(
            name = "Standing hamstring fold",
            preparationSeconds = 10,
            holdSeconds = 35,
            notes = "Soften the knees instead of locking them.",
            enabled = true,
            sortOrder = 1
        ),
        StretchEntity(
            name = "Hip flexor lunge",
            preparationSeconds = 12,
            holdSeconds = 40,
            notes = "Switch sides after the hold.",
            enabled = true,
            sortOrder = 2
        ),
        StretchEntity(
            name = "Child pose",
            preparationSeconds = 10,
            holdSeconds = 40,
            notes = "Sink hips back and reach through the fingertips.",
            enabled = true,
            sortOrder = 3
        ),
        StretchEntity(
            name = "Cat cow stretch",
            preparationSeconds = 8,
            holdSeconds = 45,
            notes = "Move slowly between arching and rounding the spine.",
            enabled = true,
            sortOrder = 4
        ),
        StretchEntity(
            name = "Seated spinal twist",
            preparationSeconds = 10,
            holdSeconds = 35,
            notes = "Lengthen tall before rotating through the ribs.",
            enabled = true,
            sortOrder = 5
        ),
        StretchEntity(
            name = "Standing quad stretch",
            preparationSeconds = 10,
            holdSeconds = 35,
            notes = "Keep knees close and stand tall.",
            enabled = true,
            sortOrder = 6
        ),
        StretchEntity(
            name = "Wall calf stretch",
            preparationSeconds = 8,
            holdSeconds = 35,
            notes = "Press the back heel down and lean gently forward.",
            enabled = true,
            sortOrder = 7
        ),
        StretchEntity(
            name = "Cross-body shoulder stretch",
            preparationSeconds = 8,
            holdSeconds = 30,
            notes = "Relax the neck and draw the arm across the chest.",
            enabled = true,
            sortOrder = 8
        ),
        StretchEntity(
            name = "Butterfly stretch",
            preparationSeconds = 10,
            holdSeconds = 40,
            notes = "Let the knees soften toward the floor.",
            enabled = true,
            sortOrder = 9
        )
    )
}
