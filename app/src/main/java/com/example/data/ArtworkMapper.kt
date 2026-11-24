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
        "tadasana_mountain_alignment" to R.drawable.img_stretch_standing,
        // Subah Subah & Batch 5 verified aliases (all 122 Studio Ghibli videos)
        "ardha pavanmuktasana (left)" to R.drawable.img_stretch_b1_single_knee_to_chest,
        "ardha pavanmuktasana (right)" to R.drawable.img_stretch_b1_single_knee_to_chest,
        "ardha pavanmuktasan left" to R.drawable.img_stretch_b1_single_knee_to_chest,
        "ardha pavanmuktasan right" to R.drawable.img_stretch_b1_single_knee_to_chest,
        "ardha pavanmuktasana diagonal (left)" to R.drawable.img_stretch_b5_ardha_pavanmuktasana_diagonal,
        "ardha pavanmuktasana diagonal (right)" to R.drawable.img_stretch_b5_ardha_pavanmuktasana_diagonal,
        "ardha pavanmuktasan left diag" to R.drawable.img_stretch_b5_ardha_pavanmuktasana_diagonal,
        "ardha pavanmuktasan right diagonal" to R.drawable.img_stretch_b5_ardha_pavanmuktasana_diagonal,
        "pavanmuktasana" to R.drawable.img_stretch_b4_pavanamuktasana_wind_relieving,
        "pavanmuktasan" to R.drawable.img_stretch_b4_pavanamuktasana_wind_relieving,
        "towel hamstring stretch (left)" to R.drawable.img_stretch_b3_supta_padangusthasana,
        "towel hamstring stretch (right)" to R.drawable.img_stretch_b3_supta_padangusthasana,
        "nutraj lower back stretch (left)" to R.drawable.img_stretch_b1_reclining_spinal_twist,
        "nutraj lower back stretch (right)" to R.drawable.img_stretch_b1_reclining_spinal_twist,
        "hip flexor (left)" to R.drawable.img_stretch_b2_hip_flexor_lunge,
        "hip flexor (right)" to R.drawable.img_stretch_b2_hip_flexor_lunge,
        "hip flexor left" to R.drawable.img_stretch_b2_hip_flexor_lunge,
        "hip flexor right" to R.drawable.img_stretch_b2_hip_flexor_lunge,
        "world's greatest stretch" to R.drawable.img_stretch_b5_worlds_greatest_stretch,
        "world's greatest stretch (left)" to R.drawable.img_stretch_b5_worlds_greatest_stretch,
        "world's greatest stretch (right)" to R.drawable.img_stretch_b5_worlds_greatest_stretch,
        "world's greatest stretch left" to R.drawable.img_stretch_b5_worlds_greatest_stretch,
        "world's greatest stretch right" to R.drawable.img_stretch_b5_worlds_greatest_stretch,
        "bhujangasana" to R.drawable.img_stretch_b3_bhujangasana_cobra,
        "pancake stretch" to R.drawable.img_stretch_b4_upavistha_konasana_wide_angle,
        "spine alignment" to R.drawable.img_stretch_b5_spine_alignment,
        "parvatasana" to R.drawable.img_stretch_b5_parvatasana_seated_mountain,
        "parvatasana (seated mountain)" to R.drawable.img_stretch_b5_parvatasana_seated_mountain,
        "scapula stretch" to R.drawable.img_stretch_b5_scapula_stretch,
        "scapula stretch (left)" to R.drawable.img_stretch_b5_scapula_stretch,
        "scapula stretch (right)" to R.drawable.img_stretch_b5_scapula_stretch,
        "scapula stretch left" to R.drawable.img_stretch_b5_scapula_stretch,
        "scapula stretch right" to R.drawable.img_stretch_b5_scapula_stretch,
        "camel stretch" to R.drawable.img_stretch_b3_ustrasana_camel,
        "child's pose with lat reach (left)" to R.drawable.img_stretch_b1_child_pose_lat_reach,
        "child's pose with lat reach (right)" to R.drawable.img_stretch_b1_child_pose_lat_reach,
        "shoulder stretches" to R.drawable.img_stretch_b1_cross_body_shoulder,
        "overhead triceps & lat reach (left)" to R.drawable.img_stretch_b1_overhead_triceps_lat,
        "overhead triceps & lat reach (right)" to R.drawable.img_stretch_b1_overhead_triceps_lat,
        "neck 0 degree (left)" to R.drawable.img_stretch_b1_neck_side,
        "neck 0 degree (right)" to R.drawable.img_stretch_b1_neck_side,
        "neck 45 degree (left)" to R.drawable.img_stretch_b1_chin_tuck_levator,
        "neck 45 degree (right)" to R.drawable.img_stretch_b1_chin_tuck_levator,
        "neck 90 degree (left)" to R.drawable.img_stretch_b1_scm_neck_turn,
        "neck 90 degree (right)" to R.drawable.img_stretch_b1_scm_neck_turn,
        "standing side-bend arc (left)" to R.drawable.img_stretch_b1_standing_side_arc,
        "standing side-bend arc (right)" to R.drawable.img_stretch_b1_standing_side_arc,
        "standing side lat reach (left)" to R.drawable.img_stretch_b1_standing_side_reach_lat,
        "standing side lat reach (right)" to R.drawable.img_stretch_b1_standing_side_reach_lat
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
        val lower = name.lowercase()
        // Prefer dedicated directional video (e.g. img_stretch_b1_child_pose_lat_reach_left.mp4 / _right.mp4) when bundled
        if (lower.contains("left")) {
            val leftFile = "${stem}_left.mp4"
            if (leftFile in available) return "$VIDEO_ASSETS_DIR/$leftFile"
        } else if (lower.contains("right")) {
            val rightFile = "${stem}_right.mp4"
            if (rightFile in available) return "$VIDEO_ASSETS_DIR/$rightFile"
        }
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

