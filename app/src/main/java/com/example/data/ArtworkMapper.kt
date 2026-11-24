package com.example.data

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.R
import com.example.data.artwork.ArtworkBatch1
import com.example.data.artwork.ArtworkBatch2
import com.example.data.artwork.ArtworkBatch3
import com.example.data.artwork.ArtworkBatch4

object ArtworkMapper {
    private val whitespaceRegex = Regex("\\s+")
    private const val VIDEO_ASSETS_DIR = "stretch_videos"

    @Volatile
    private var cachedVideoFiles: Set<String>? = null

    internal fun normalizeKey(raw: String): String =
        raw.trim().lowercase().replace(whitespaceRegex, " ")

    private val coreReferenceMap: Map<String, Int> = mapOf(
        // #52 Adho Mukha Svanasana (Downward-Facing Dog)
        "adho mukha svanasana (downward-facing dog)" to R.drawable.img_stretch_downward_dog,
        "adho_mukha_svanasana_downward_dog" to R.drawable.img_stretch_downward_dog,

        // #53 Balasana (Child's Pose) & #131 Shashankasana (Hare Pose)
        "balasana (child's pose)" to R.drawable.img_stretch_child,
        "balasana_childs_pose" to R.drawable.img_stretch_child,
        "shashankasana (hare pose)" to R.drawable.img_stretch_child,
        "shashankasana_hare" to R.drawable.img_stretch_child,

        // #57 / #58 Vrikshasana (Tree Pose - Right / Left)
        "vrikshasana (tree pose - right)" to R.drawable.img_stretch_tree,
        "vrikshasana_tree_right" to R.drawable.img_stretch_tree,
        "vrikshasana (tree pose - left)" to R.drawable.img_stretch_tree,
        "vrikshasana_tree_left" to R.drawable.img_stretch_tree,

        // #73 / #74 Virabhadrasana II (Warrior II - Right / Left)
        "virabhadrasana ii (warrior ii - right)" to R.drawable.img_stretch_warrior,
        "virabhadrasana_two_right" to R.drawable.img_stretch_warrior,
        "virabhadrasana ii (warrior ii - left)" to R.drawable.img_stretch_warrior,
        "virabhadrasana_two_left" to R.drawable.img_stretch_warrior,

        // #84 Tadasana (Mountain Pose Alignment)
        "tadasana (mountain pose alignment)" to R.drawable.img_stretch_standing,
        "tadasana_mountain_alignment" to R.drawable.img_stretch_standing
    )

    @DrawableRes
    fun getDrawableForStretch(name: String, @Suppress("UNUSED_PARAMETER") category: String = ""): Int? {
        val key = normalizeKey(name)
        if (key.isBlank()) return null

        return coreReferenceMap[key]
            ?: ArtworkBatch1.exactNameMap[key]
            ?: ArtworkBatch2.exactNameMap[key]
            ?: ArtworkBatch3.exactNameMap[key]
            ?: ArtworkBatch4.exactNameMap[key]
    }

    /**
     * Resolves the verified looping MP4 asset path (under assets/stretch_videos/)
     * matching the exact verified drawable stem, or null if no verified video exists.
     */
    fun getVideoAssetPathForStretch(context: Context, name: String, category: String = ""): String? {
        val drawableRes = getDrawableForStretch(name, category) ?: return null
        val stem = runCatching { context.resources.getResourceEntryName(drawableRes) }.getOrNull()
            ?: return null
        val available = availableVideoFiles(context)
        val fileName = "$stem.mp4"
        return if (fileName in available) "$VIDEO_ASSETS_DIR/$fileName" else null
    }

    private fun availableVideoFiles(context: Context): Set<String> {
        cachedVideoFiles?.let { return it }
        val files = runCatching {
            context.assets.list(VIDEO_ASSETS_DIR)?.toSet() ?: emptySet()
        }.getOrDefault(emptySet())
        cachedVideoFiles = files
        return files
    }
}

