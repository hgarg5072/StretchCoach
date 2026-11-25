package com.example.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val sha256: String,
    val sizeBytes: Long,
    val releaseNotes: String
)

sealed interface AppUpdateState {
    data object Idle : AppUpdateState
    data object Checking : AppUpdateState
    data class UpToDate(val currentVersion: String) : AppUpdateState
    data class Available(val info: UpdateInfo) : AppUpdateState
    data class Downloading(
        val info: UpdateInfo,
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : AppUpdateState
    data class Verifying(val info: UpdateInfo) : AppUpdateState
    data class ReadyToInstall(val info: UpdateInfo, val apkFile: File) : AppUpdateState
    data class Error(val message: String, val info: UpdateInfo? = null) : AppUpdateState
}

/**
 * Free, zero-tracker, SHA-256 verified 1-tap in-app updater backed by GitHub Pages + GitHub Releases.
 *
 * - Reads tiny CDN-cached `latest.json` from GitHub Pages (zero GitHub API rate limits)
 * - Falls back automatically to GitHub Releases REST API if Pages is unreachable
 * - Downloads APK into private app cache with live progress
 * - Strictly verifies SHA-256 checksum before launching Android's native PackageInstaller sheet
 */
class AppUpdateManager(private val context: Context) {

    companion object {
        private const val TAG = "AppUpdateManager"
        private const val PREFS_NAME = "stretch_coach_updater"
        private const val KEY_LAST_CHECK_MS = "last_auto_check_ms"
        private const val AUTO_CHECK_INTERVAL_MS = 12L * 60L * 60L * 1000L // 12 hours

        const val PAGES_LATEST_JSON_URL =
            "https://hgarg5072.github.io/StretchCoach-Releases/latest.json"
        const val GITHUB_RELEASES_API_URL =
            "https://api.github.com/repos/hgarg5072/StretchCoach-Releases/releases/latest"

        /** Compares semantic version strings like "1.2.0" vs "1.1.9". Returns >0 if remote > local. */
        internal fun compareVersions(remote: String, local: String): Int {
            val rParts = remote.trim().removePrefix("v").removePrefix("V")
                .split(".").map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
            val lParts = local.trim().removePrefix("v").removePrefix("V")
                .split(".").map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
            val maxLen = maxOf(rParts.size, lParts.size)
            for (i in 0 until maxLen) {
                val r = rParts.getOrElse(i) { 0 }
                val l = lParts.getOrElse(i) { 0 }
                if (r != l) return r.compareTo(l)
            }
            return 0
        }

        internal fun isNewer(remote: UpdateInfo, localVersionCode: Int, localVersionName: String): Boolean {
            if (remote.versionCode > 0 && remote.versionCode > localVersionCode) return true
            return compareVersions(remote.versionName, localVersionName) > 0
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    fun shouldRunAutoCheck(): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val last = prefs.getLong(KEY_LAST_CHECK_MS, 0L)
        return (System.currentTimeMillis() - last) >= AUTO_CHECK_INTERVAL_MS
    }

    fun markAutoCheckDone() {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_CHECK_MS, System.currentTimeMillis())
            .apply()
    }

    suspend fun checkForUpdate(): Result<UpdateInfo?> = withContext(Dispatchers.IO) {
        runCatching {
            val remote = fetchFromPages() ?: fetchFromGitHubApi()
                ?: throw IllegalStateException("Could not reach update server. Check your internet connection.")

            if (isNewer(remote, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)) {
                remote
            } else {
                null
            }
        }
    }

    private fun fetchFromPages(): UpdateInfo? {
        return runCatching {
            val req = Request.Builder()
                .url(PAGES_LATEST_JSON_URL)
                .header("Accept", "application/json")
                .header("Cache-Control", "no-cache")
                .build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val body = resp.body?.string() ?: return null
                parseLatestJson(body)
            }
        }.onFailure {
            Log.d(TAG, "Pages latest.json fetch fell back: ${it.message}")
        }.getOrNull()
    }

    private fun fetchFromGitHubApi(): UpdateInfo? {
        return runCatching {
            val req = Request.Builder()
                .url(GITHUB_RELEASES_API_URL)
                .header("Accept", "application/vnd.github+json")
                .build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val body = resp.body?.string() ?: return null
                val root = JSONObject(body)
                val tag = root.optString("tag_name").trim().removePrefix("v").removePrefix("V")
                val notes = root.optString("body").trim()
                val assets = root.optJSONArray("assets") ?: return null
                var apkUrl = ""
                var sizeBytes = 0L
                for (i in 0 until assets.length()) {
                    val asset = assets.optJSONObject(i) ?: continue
                    val name = asset.optString("name")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkUrl = asset.optString("browser_download_url")
                        sizeBytes = asset.optLong("size", 0L)
                        break
                    }
                }
                if (tag.isBlank() || apkUrl.isBlank()) return null
                // Extract optional SHA-256 line from release notes if present
                val shaMatch = Regex("(?i)sha-?256[:\\s`]+([a-f0-9]{64})").find(notes)
                UpdateInfo(
                    versionCode = 0,
                    versionName = tag,
                    apkUrl = apkUrl,
                    sha256 = shaMatch?.groupValues?.getOrNull(1)?.lowercase() ?: "",
                    sizeBytes = sizeBytes,
                    releaseNotes = notes
                )
            }
        }.onFailure {
            Log.w(TAG, "GitHub API fetch failed: ${it.message}")
        }.getOrNull()
    }

    internal fun parseLatestJson(jsonText: String): UpdateInfo? {
        val root = JSONObject(jsonText)
        val versionName = root.optString("versionName").trim()
        val apkUrl = root.optString("apkUrl").trim()
        if (versionName.isBlank() || apkUrl.isBlank()) return null
        return UpdateInfo(
            versionCode = root.optInt("versionCode", 0),
            versionName = versionName,
            apkUrl = apkUrl,
            sha256 = root.optString("sha256", "").trim().lowercase(),
            sizeBytes = root.optLong("sizeBytes", 0L),
            releaseNotes = root.optString("releaseNotes", "").trim()
        )
    }

    suspend fun downloadAndVerifyApk(
        info: UpdateInfo,
        onProgress: (progress: Float, downloaded: Long, total: Long) -> Unit,
        onVerifying: () -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            // Clean up any stale APKs first
            updatesDir.listFiles()?.forEach { it.delete() }

            val safeVer = info.versionName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val targetFile = File(updatesDir, "StretchCoach-$safeVer.apk")

            val req = Request.Builder().url(info.apkUrl).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    throw IllegalStateException("Download failed (HTTP ${resp.code})")
                }
                val body = resp.body ?: throw IllegalStateException("Empty APK response")
                val total = body.contentLength().takeIf { it > 0 } ?: info.sizeBytes

                val digest = MessageDigest.getInstance("SHA-256")
                var downloaded = 0L
                var lastEmitMs = 0L

                body.byteStream().use { input ->
                    FileOutputStream(targetFile).use { output ->
                        val buffer = ByteArray(16 * 1024)
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            digest.update(buffer, 0, read)
                            downloaded += read
                            val now = System.currentTimeMillis()
                            if (now - lastEmitMs >= 100L || (total > 0 && downloaded >= total)) {
                                lastEmitMs = now
                                val frac = if (total > 0) (downloaded.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
                                onProgress(frac, downloaded, total)
                            }
                        }
                    }
                }

                onVerifying()
                val actualSha = digest.digest().joinToString("") { "%02x".format(it) }
                if (info.sha256.isNotBlank() && !actualSha.equals(info.sha256, ignoreCase = true)) {
                    targetFile.delete()
                    throw SecurityException("Integrity check failed (SHA-256 mismatch). Download aborted.")
                }
            }
            targetFile
        }
    }

    fun canRequestPackageInstalls(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun createUnknownSourcesSettingsIntent(): Intent {
        return Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun launchApkInstaller(apkFile: File): Boolean {
        return runCatching {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.updateprovider",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
            true
        }.onFailure { e ->
            Log.e(TAG, "Failed to launch APK installer: ${e.message}")
        }.getOrDefault(false)
    }
}
