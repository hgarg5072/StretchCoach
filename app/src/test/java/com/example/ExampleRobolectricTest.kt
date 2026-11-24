package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BackupRoutineItem
import com.example.data.BackupStretchItem
import com.example.data.RoutineBackupManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Stretch Coach", appName)
  }

  @Test
  fun testRoutineBackupManagerSerializationAndPersistence() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val testRoutines = listOf(
      BackupRoutineItem(
        name = "My Custom 10 Stretch Routine",
        description = "Custom stretches with adjusted times",
        stretches = listOf(
          BackupStretchItem(
            libraryId = "neck_side_right",
            name = "Neck Side Stretch",
            category = "Neck & Shoulders",
            preparationSeconds = 10,
            holdSeconds = 45,
            prepCue = "Get into neck stretch position",
            holdCue = "Tilt head gently to right",
            notes = "Keep shoulders relaxed",
            enabled = true,
            voiceCueEnabled = true,
            sortOrder = 0
          ),
          BackupStretchItem(
            libraryId = "cat_cow_spinal_flow",
            name = "Cat-Cow Flow",
            category = "Back & Spine",
            preparationSeconds = 5,
            holdSeconds = 60,
            prepCue = "Come onto hands and knees",
            holdCue = "Inhale arch, exhale round",
            notes = "Synchronize with breath",
            enabled = true,
            voiceCueEnabled = true,
            sortOrder = 1
          )
        )
      )
    )

    // 1. Verify JSON serialization
    val json = RoutineBackupManager.createBackupJson(testRoutines)
    assertTrue(json.contains("My Custom 10 Stretch Routine"))
    assertTrue(json.contains("Neck Side Stretch"))
    assertTrue(json.contains("45"))

    // 2. Verify JSON parsing
    val parsed = RoutineBackupManager.parseBackupJson(json)
    assertEquals(1, parsed.size)
    assertEquals("My Custom 10 Stretch Routine", parsed[0].name)
    assertEquals(2, parsed[0].stretches.size)
    assertEquals("Neck Side Stretch", parsed[0].stretches[0].name)
    assertEquals(45, parsed[0].stretches[0].holdSeconds)
    assertEquals(10, parsed[0].stretches[0].preparationSeconds)

    // 3. Verify dual-layer persistent saving and loading via Context
    RoutineBackupManager.saveAutoBackup(context, testRoutines)
    val loaded = RoutineBackupManager.loadAutoBackup(context)
    assertEquals(1, loaded.size)
    assertEquals("My Custom 10 Stretch Routine", loaded[0].name)
    assertEquals(2, loaded[0].stretches.size)

    // 4. Verify BackupStatus
    val status = RoutineBackupManager.getBackupStatus(context)
    assertEquals(1, status.totalRoutines)
    assertTrue(status.hasBackup)
    assertTrue(status.lastBackupTimestamp > 0)
  }
}
