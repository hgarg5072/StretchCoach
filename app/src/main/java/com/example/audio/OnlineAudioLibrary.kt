package com.example.audio

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class OnlineAudioTrack(
    val identifier: String,
    val title: String,
    val creator: String = "",
    val description: String = ""
)

data class DownloadedAudioTrack(
    val filePath: String,
    val title: String,
    val sizeBytes: Long
) {
    val formattedSize: String
        get() {
            val mb = sizeBytes / (1024.0 * 1024.0)
            return if (mb >= 1.0) String.format("%.1f MB", mb) else "${(sizeBytes / 1024).coerceAtLeast(1)} KB"
        }
}

/**
 * Free, zero-API-key audio library manager backed by:
 * 1) Internet Archive (archive.org) open JSON search & metadata APIs
 * 2) Local audio import (e.g., MP3/M4A/OGG downloaded from StockTune or Pixabay)
 *
 * All downloaded/imported files are stored offline inside context.filesDir/ambient_tracks/.
 */
object OnlineAudioLibrary {

    private const val TAG = "OnlineAudioLibrary"
    private const val DIR_NAME = "ambient_tracks"
    private const val MAX_AUDIO_BYTES = 35L * 1024L * 1024L // 35 MB safety cap

    val STOCKTUNE_URL = "https://stocktune.com/free-music/indian-flute"
    val PIXABAY_URL = "https://pixabay.com/music/search/indian%20meditation/"

    val PRESET_SEARCHES = listOf(
        "Bansuri Flute" to "bansuri flute meditation",
        "Sitar" to "sitar relaxation meditation",
        "Morning Raga" to "indian classical raga flute",
        "Tibetan Bowls" to "tibetan singing bowl meditation",
        "Om Chant" to "om meditation chant"
    )

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    private fun tracksDir(context: Context): File =
        File(context.filesDir, DIR_NAME).apply { mkdirs() }

    /**
     * Searches Internet Archive for free audio recordings matching [query].
     * No API key or sign-up required.
     */
    suspend fun searchInternetArchive(query: String): Result<List<OnlineAudioTrack>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val trimmed = query.trim().ifBlank { "bansuri flute meditation" }
                val archiveQuery = "($trimmed) AND mediatype:(audio)"
                val encodedQ = URLEncoder.encode(archiveQuery, "UTF-8")
                val url = "https://archive.org/advancedsearch.php" +
                    "?q=$encodedQ" +
                    "&fl[]=identifier&fl[]=title&fl[]=creator&fl[]=description" +
                    "&sort[]=downloads+desc" +
                    "&rows=16&page=1&output=json"

                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "StretchCoachAndroid/1.0")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Internet Archive returned HTTP ${response.code}")
                    }
                    val body = response.body?.string().orEmpty()
                    parseArchiveSearchJson(body)
                }
            }
        }

    internal fun parseArchiveSearchJson(jsonText: String): List<OnlineAudioTrack> {
        val root = JSONObject(jsonText)
        val docs = root.optJSONObject("response")?.optJSONArray("docs") ?: return emptyList()
        val results = mutableListOf<OnlineAudioTrack>()
        for (i in 0 until docs.length()) {
            val doc = docs.optJSONObject(i) ?: continue
            val id = doc.optString("identifier").trim()
            val title = doc.optString("title").trim()
            if (id.isBlank() || title.isBlank()) continue
            val creator = when (val c = doc.opt("creator")) {
                is String -> c.trim()
                is org.json.JSONArray -> c.optString(0).trim()
                else -> ""
            }
            val desc = when (val d = doc.opt("description")) {
                is String -> d.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim().take(120)
                is org.json.JSONArray -> d.optString(0).replace(Regex("<[^>]+>"), " ").trim().take(120)
                else -> ""
            }
            results.add(OnlineAudioTrack(identifier = id, title = title, creator = creator, description = desc))
        }
        return results
    }

    /**
     * Resolves the best playable MP3/OGG/M4A file URL from an Internet Archive item's metadata.
     */
    internal fun pickBestAudioFileFromMetadata(metadataJson: String, identifier: String): Pair<String, String>? {
        val root = JSONObject(metadataJson)
        val files = root.optJSONArray("files") ?: return null
        var bestName: String? = null
        var bestScore = Int.MIN_VALUE

        for (i in 0 until files.length()) {
            val f = files.optJSONObject(i) ?: continue
            val name = f.optString("name").trim()
            if (name.isBlank()) continue
            val lower = name.lowercase()
            val ext = when {
                lower.endsWith(".mp3") -> "mp3"
                lower.endsWith(".m4a") -> "m4a"
                lower.endsWith(".ogg") -> "ogg"
                else -> continue
            }
            val size = f.optString("size").toLongOrNull() ?: 0L
            if (size > MAX_AUDIO_BYTES) continue
            // Skip tiny clips (< 80 KB) if larger ones exist
            var score = when (ext) {
                "mp3" -> 30
                "m4a" -> 25
                "ogg" -> 20
                else -> 0
            }
            if (size in 200_000L..12_000_000L) score += 20
            else if (size in 80_000L..25_000_000L) score += 10
            if (lower.contains("sample") || lower.contains("preview")) score -= 15

            if (score > bestScore) {
                bestScore = score
                bestName = name
            }
        }

        val chosen = bestName ?: return null
        val encodedPath = chosen.split("/").joinToString("/") { segment ->
            URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
        }
        val url = "https://archive.org/download/$identifier/$encodedPath"
        val ext = chosen.substringAfterLast('.', "mp3").lowercase()
        return url to ext
    }

    /**
     * Downloads an Internet Archive track into offline app storage.
     */
    suspend fun downloadInternetArchiveTrack(
        context: Context,
        track: OnlineAudioTrack
    ): Result<DownloadedAudioTrack> = withContext(Dispatchers.IO) {
        runCatching {
            val metaReq = Request.Builder()
                .url("https://archive.org/metadata/${track.identifier}")
                .header("User-Agent", "StretchCoachAndroid/1.0")
                .build()

            val (downloadUrl, ext) = client.newCall(metaReq).execute().use { resp ->
                if (!resp.isSuccessful) {
                    throw IllegalStateException("Metadata lookup failed (HTTP ${resp.code})")
                }
                val json = resp.body?.string().orEmpty()
                pickBestAudioFileFromMetadata(json, track.identifier)
                    ?: throw IllegalStateException("No playable MP3/M4A/OGG audio found in this item")
            }

            val safeTitle = sanitizeFilename(track.title).ifBlank { track.identifier }
            val outFile = File(tracksDir(context), "${safeTitle}.$ext")

            val dlReq = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "StretchCoachAndroid/1.0")
                .build()

            client.newCall(dlReq).execute().use { resp ->
                if (!resp.isSuccessful) {
                    throw IllegalStateException("Download failed (HTTP ${resp.code})")
                }
                val body = resp.body ?: throw IllegalStateException("Empty audio response")
                body.byteStream().use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buf = ByteArray(16 * 1024)
                        var total = 0L
                        while (true) {
                            val read = input.read(buf)
                            if (read <= 0) break
                            total += read
                            if (total > MAX_AUDIO_BYTES) {
                                throw IllegalStateException("Audio file exceeds 35 MB limit")
                            }
                            output.write(buf, 0, read)
                        }
                    }
                }
            }

            if (!outFile.isFile || outFile.length() < 4096L) {
                outFile.delete()
                throw IllegalStateException("Downloaded audio file was empty or invalid")
            }

            Log.i(TAG, "Downloaded '${track.title}' to ${outFile.absolutePath} (${outFile.length()} bytes)")
            DownloadedAudioTrack(
                filePath = outFile.absolutePath,
                title = track.title.trim().take(60),
                sizeBytes = outFile.length()
            )
        }
    }

    /**
     * Copies a user-selected audio file (e.g., downloaded from StockTune or Pixabay)
     * into Stretch Coach's private offline folder so it stays playable forever.
     */
    suspend fun importLocalAudioUri(
        context: Context,
        uri: Uri
    ): Result<DownloadedAudioTrack> = withContext(Dispatchers.IO) {
        runCatching {
            val displayName = resolveDisplayName(context, uri) ?: "Imported_Track.mp3"
            val ext = displayName.substringAfterLast('.', "mp3")
                .lowercase()
                .takeIf { it in setOf("mp3", "m4a", "ogg", "wav", "aac", "flac") } ?: "mp3"
            val cleanTitle = displayName.substringBeforeLast('.').replace('_', ' ').trim().ifBlank { "Imported Audio" }
            val safeFile = sanitizeFilename(cleanTitle).ifBlank { "imported_${System.currentTimeMillis()}" }
            val outFile = File(tracksDir(context), "$safeFile.$ext")

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Could not open selected audio file")

            inputStream.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output, bufferSize = 16 * 1024)
                }
            }

            if (!outFile.isFile || outFile.length() < 1024L) {
                outFile.delete()
                throw IllegalStateException("Selected audio file appears empty")
            }

            DownloadedAudioTrack(
                filePath = outFile.absolutePath,
                title = cleanTitle.take(60),
                sizeBytes = outFile.length()
            )
        }
    }

    fun listDownloadedTracks(context: Context): List<DownloadedAudioTrack> {
        val dir = tracksDir(context)
        return dir.listFiles()
            ?.filter { it.isFile && it.length() > 1024L }
            ?.sortedByDescending { it.lastModified() }
            ?.map { file ->
                DownloadedAudioTrack(
                    filePath = file.absolutePath,
                    title = file.nameWithoutExtension.replace('_', ' ').trim(),
                    sizeBytes = file.length()
                )
            }
            ?: emptyList()
    }

    fun deleteDownloadedTrack(filePath: String): Boolean {
        return runCatching {
            val f = File(filePath)
            if (f.exists()) f.delete() else false
        }.getOrDefault(false)
    }

    private fun resolveDisplayName(context: Context, uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else null
            }
        }.getOrNull() ?: uri.lastPathSegment?.substringAfterLast('/')
    }

    private fun sanitizeFilename(raw: String): String {
        return raw.trim()
            .replace(Regex("[^a-zA-Z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "_")
            .take(48)
    }
}
