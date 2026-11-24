package com.example.ui.components

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Hardware-accelerated, muted, seamless looping MP4 player backed by TextureView + MediaPlayer.
 *
 * - Plays directly from APK assets (assets/stretch_videos/<stem>.mp4) with zero extra dependencies.
 * - Never requests audio focus and stays muted so Google TTS and background studio audio are unaffected.
 * - Uses center-crop matrix scaling so 16:9 videos fill tall or wide rounded containers cleanly.
 * - Stays transparent (alpha = 0f) until the first decoded video frame renders, eliminating black flashes
 *   over the underlying static JPEG poster.
 */
@Composable
fun LoopingStretchVideo(
    assetPath: String,
    isPlaying: Boolean,
    isLooping: Boolean = true,
    mirrorHorizontal: Boolean = false,
    onCompleted: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var hasRenderedFirstFrame by remember(assetPath) { mutableStateOf(false) }
    val currentIsPlaying by rememberUpdatedState(isPlaying)
    val currentIsLooping by rememberUpdatedState(isLooping)
    val currentMirror by rememberUpdatedState(mirrorHorizontal)
    val currentOnCompleted by rememberUpdatedState(onCompleted)
    val playerHolder = remember { VideoPlayerHolder() }

    // Sync play / pause and mirror state without recreating MediaPlayer
    DisposableEffect(isPlaying, mirrorHorizontal, assetPath) {
        playerHolder.setMirror(mirrorHorizontal)
        playerHolder.setPlaying(isPlaying)
        onDispose {}
    }

    DisposableEffect(Unit) {
        onDispose {
            playerHolder.release()
        }
    }

    AndroidView(
        factory = { context ->
            TextureView(context).apply {
                isOpaque = false
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                        playerHolder.attachSurface(this@apply, st, width, height)
                        playerHolder.setMirror(currentMirror)
                        playerHolder.loadAsset(
                            context = context,
                            assetPath = assetPath,
                            shouldPlay = currentIsPlaying,
                            looping = currentIsLooping,
                            onFirstFrame = { hasRenderedFirstFrame = true },
                            onCompleted = { currentOnCompleted?.invoke() }
                        )
                    }

                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {
                        playerHolder.updateViewSize(this@apply, width, height)
                    }

                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                        playerHolder.release()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) = Unit
                }
            }
        },
        update = { textureView ->
            playerHolder.setMirror(mirrorHorizontal)
            if (playerHolder.loadedAssetPath != assetPath) {
                hasRenderedFirstFrame = false
                if (textureView.isAvailable && textureView.surfaceTexture != null) {
                    playerHolder.attachSurface(
                        textureView,
                        textureView.surfaceTexture!!,
                        textureView.width,
                        textureView.height
                    )
                    playerHolder.loadAsset(
                        context = textureView.context,
                        assetPath = assetPath,
                        shouldPlay = currentIsPlaying,
                        looping = isLooping,
                        onFirstFrame = { hasRenderedFirstFrame = true },
                        onCompleted = { currentOnCompleted?.invoke() }
                    )
                }
            } else {
                playerHolder.setPlaying(isPlaying)
            }
        },
        modifier = modifier.alpha(if (hasRenderedFirstFrame) 1f else 0f)
    )
}

private class VideoPlayerHolder {
    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null
    private var textureView: TextureView? = null
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var videoWidth: Int = 0
    private var videoHeight: Int = 0
    private var isPrepared: Boolean = false
    private var desiredPlaying: Boolean = true
    private var mirrorHorizontal: Boolean = false
    var loadedAssetPath: String? = null
        private set

    fun setMirror(mirror: Boolean) {
        if (mirrorHorizontal != mirror) {
            mirrorHorizontal = mirror
            applyCenterCropTransform()
        }
    }

    fun attachSurface(tv: TextureView, st: SurfaceTexture, width: Int, height: Int) {
        textureView = tv
        viewWidth = width
        viewHeight = height
        surface?.release()
        surface = Surface(st)
    }

    fun updateViewSize(tv: TextureView, width: Int, height: Int) {
        textureView = tv
        viewWidth = width
        viewHeight = height
        applyCenterCropTransform()
    }

    fun loadAsset(
        context: android.content.Context,
        assetPath: String,
        shouldPlay: Boolean,
        looping: Boolean = true,
        onFirstFrame: () -> Unit,
        onCompleted: () -> Unit = {}
    ) {
        desiredPlaying = shouldPlay
        loadedAssetPath = assetPath
        releasePlayerOnly()

        val activeSurface = surface ?: return
        runCatching {
            val afd = context.assets.openFd(assetPath)
            val mp = MediaPlayer()
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            mp.setSurface(activeSurface)
            mp.isLooping = looping
            mp.setVolume(0f, 0f)

            mp.setOnCompletionListener {
                if (!looping) {
                    onCompleted()
                }
            }

            mp.setOnVideoSizeChangedListener { _, w, h ->
                videoWidth = w
                videoHeight = h
                applyCenterCropTransform()
            }
            mp.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    onFirstFrame()
                    true
                } else {
                    false
                }
            }
            mp.setOnPreparedListener { prepared ->
                isPrepared = true
                videoWidth = prepared.videoWidth
                videoHeight = prepared.videoHeight
                applyCenterCropTransform()
                prepared.setVolume(0f, 0f)
                if (desiredPlaying) {
                    prepared.start()
                } else {
                    // Render first frame while paused
                    prepared.seekTo(1)
                }
            }
            mp.setOnErrorListener { _, what, extra ->
                Log.w(TAG, "MediaPlayer error ($what, $extra) for $assetPath")
                true
            }
            mediaPlayer = mp
            mp.prepareAsync()
        }.onFailure { e ->
            Log.w(TAG, "Could not load video asset '$assetPath': ${e.message}")
            releasePlayerOnly()
        }
    }

    fun setPlaying(playing: Boolean) {
        desiredPlaying = playing
        val mp = mediaPlayer ?: return
        if (!isPrepared) return
        runCatching {
            if (playing && !mp.isPlaying) {
                mp.start()
            } else if (!playing && mp.isPlaying) {
                mp.pause()
            }
        }
    }

    private fun applyCenterCropTransform() {
        val tv = textureView ?: return
        if (viewWidth <= 0 || viewHeight <= 0 || videoWidth <= 0 || videoHeight <= 0) return

        val viewRatio = viewWidth.toFloat() / viewHeight.toFloat()
        val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()

        val scaleX: Float
        val scaleY: Float
        if (videoRatio > viewRatio) {
            scaleX = videoRatio / viewRatio
            scaleY = 1f
        } else {
            scaleX = 1f
            scaleY = viewRatio / videoRatio
        }

        val matrix = Matrix()
        val signedScaleX = if (mirrorHorizontal) -scaleX else scaleX
        matrix.setScale(signedScaleX, scaleY, viewWidth / 2f, viewHeight / 2f)
        tv.post {
            tv.setTransform(matrix)
        }
    }

    private fun releasePlayerOnly() {
        isPrepared = false
        runCatching {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.release()
            }
        }
        mediaPlayer = null
    }

    fun release() {
        releasePlayerOnly()
        runCatching { surface?.release() }
        surface = null
        textureView = null
        loadedAssetPath = null
    }

    companion object {
        private const val TAG = "LoopingStretchVideo"
    }
}
