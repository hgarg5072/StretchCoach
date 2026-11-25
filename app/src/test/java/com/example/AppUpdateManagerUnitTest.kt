package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.update.AppUpdateManager
import com.example.update.UpdateInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppUpdateManagerUnitTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun versionComparisonHandlesSemverAndVPrefix() {
        assertTrue(AppUpdateManager.compareVersions("1.1.0", "1.0.0") > 0)
        assertTrue(AppUpdateManager.compareVersions("v1.0.1", "1.0.0") > 0)
        assertTrue(AppUpdateManager.compareVersions("2.0.0", "1.9.9") > 0)
        assertEquals(0, AppUpdateManager.compareVersions("v1.0.0", "1.0.0"))
        assertTrue(AppUpdateManager.compareVersions("1.0.0", "1.2.0") < 0)
    }

    @Test
    fun isNewerChecksVersionCodeAndSemver() {
        val same = UpdateInfo(1, "1.0.0", "https://example.com/app.apk", "abc", 100L, "")
        val higherCode = UpdateInfo(2, "1.0.0", "https://example.com/app.apk", "abc", 100L, "")
        val higherName = UpdateInfo(0, "1.0.1", "https://example.com/app.apk", "abc", 100L, "")

        assertFalse(AppUpdateManager.isNewer(same, 1, "1.0.0"))
        assertTrue(AppUpdateManager.isNewer(higherCode, 1, "1.0.0"))
        assertTrue(AppUpdateManager.isNewer(higherName, 1, "1.0.0"))
    }

    @Test
    fun parsesLatestJsonAccurately() {
        val manager = AppUpdateManager(context)
        val json = """
            {
              "versionCode": 2,
              "versionName": "1.1.0",
              "apkUrl": "https://github.com/hgarg5072/StretchCoach-Releases/releases/download/v1.1.0/StretchCoach.apk",
              "sha256": "ABCDEF0123456789abcdef0123456789abcdef0123456789abcdef0123456789",
              "sizeBytes": 49283072,
              "releaseNotes": "New Calico Cat companion & Surya Namaskar flow"
            }
        """.trimIndent()
        val parsed = manager.parseLatestJson(json)
        assertNotNull(parsed)
        assertEquals(2, parsed!!.versionCode)
        assertEquals("1.1.0", parsed.versionName)
        assertEquals(49283072L, parsed.sizeBytes)
        assertEquals("abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789", parsed.sha256)
    }
}
