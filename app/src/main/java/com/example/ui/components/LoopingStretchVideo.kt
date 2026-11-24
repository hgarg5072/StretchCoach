package com.example.ui.components

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Hardware-accelerated, muted, seamless looping MP4 player backed by OpenGL ES 2.0 + MediaPlayer.
 *
 * - Plays directly from APK assets (assets/stretch_videos/<stem>.mp4 or assets/stretch_transitions/)
 * - Never requests audio focus and stays muted so Google TTS and background studio audio are unaffected
 * - Performs aspect-preserving center-crop & optional Left-side horizontal mirroring (scaleX = -1) in GLSL
 * - Applies a 0 MB localized Ghibli breeze shader that gently sways ONLY the background Monstera and
 *   windowsill plant leaves (double-gated by spatial plant mask + green foliage color mask), leaving
 *   the yogi, walls, pots, and floor 100% crisp and stationary.
 */
@Composable
fun LoopingStretchVideo(
    assetPath: String,
    isPlaying: Boolean,
    isLooping: Boolean = true,
    mirrorHorizontal: Boolean = false,
    breezeEnabled: Boolean = true,
    playbackSpeed: Float = 1f,
    onCompleted: (() -> Unit)? = null,
    onLoopCompleted: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var hasRenderedFirstFrame by remember(assetPath) { mutableStateOf(false) }
    val currentIsPlaying by rememberUpdatedState(isPlaying)
    val currentIsLooping by rememberUpdatedState(isLooping)
    val currentMirror by rememberUpdatedState(mirrorHorizontal)
    val currentBreeze by rememberUpdatedState(breezeEnabled)
    val currentSpeed by rememberUpdatedState(playbackSpeed)
    val currentOnCompleted by rememberUpdatedState(onCompleted)
    val currentOnLoopCompleted by rememberUpdatedState(onLoopCompleted)
    val renderer = remember { GlBreezeVideoRenderer() }

    DisposableEffect(isPlaying, mirrorHorizontal, breezeEnabled, playbackSpeed, assetPath) {
        renderer.updateParams(
            playing = isPlaying,
            mirror = mirrorHorizontal,
            breezeStrength = if (breezeEnabled) 1f else 0f,
            speed = playbackSpeed
        )
        onDispose {}
    }

    DisposableEffect(Unit) {
        onDispose {
            renderer.release()
        }
    }

    AndroidView(
        factory = { context ->
            TextureView(context).apply {
                isOpaque = false
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                        renderer.start(
                            context = context.applicationContext,
                            outputSurface = st,
                            width = width,
                            height = height,
                            assetPath = assetPath,
                            playing = currentIsPlaying,
                            looping = currentIsLooping,
                            mirror = currentMirror,
                            breezeStrength = if (currentBreeze) 1f else 0f,
                            speed = currentSpeed,
                            onFirstFrame = { hasRenderedFirstFrame = true },
                            onCompleted = { currentOnCompleted?.invoke() },
                            onLoopCompleted = if (currentOnLoopCompleted != null) {
                                { currentOnLoopCompleted?.invoke() }
                            } else null
                        )
                    }

                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {
                        renderer.resize(width, height)
                    }

                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                        renderer.release()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) = Unit
                }
            }
        },
        update = { tv ->
            renderer.updateParams(
                playing = isPlaying,
                mirror = mirrorHorizontal,
                breezeStrength = if (breezeEnabled) 1f else 0f,
                speed = playbackSpeed
            )
            if (renderer.loadedAssetPath != assetPath && tv.isAvailable) {
                hasRenderedFirstFrame = false
                renderer.switchAsset(
                    context = tv.context.applicationContext,
                    assetPath = assetPath,
                    playing = currentIsPlaying,
                    looping = isLooping,
                    onFirstFrame = { hasRenderedFirstFrame = true },
                    onCompleted = { currentOnCompleted?.invoke() },
                    onLoopCompleted = if (currentOnLoopCompleted != null) {
                        { currentOnLoopCompleted?.invoke() }
                    } else null
                )
            }
        },
        modifier = modifier.alpha(if (hasRenderedFirstFrame) 1f else 0f)
    )
}

private class GlBreezeVideoRenderer {
    private var glThread: HandlerThread? = null
    private var glHandler: Handler? = null

    private var eglDisplay: EGLDisplay? = null
    private var eglContext: EGLContext? = null
    private var eglSurface: EGLSurface? = null

    private var program = 0
    private var oesTextureId = 0
    private var videoTexture: SurfaceTexture? = null
    private var videoSurface: Surface? = null
    private var mediaPlayer: MediaPlayer? = null

    private val stMatrix = FloatArray(16)
    private var viewportW = 1
    private var viewportH = 1
    private var videoW = 1280
    private var videoH = 720
    private var startTimeMs = SystemClock.uptimeMillis()

    @Volatile
    private var desiredPlaying = true

    @Volatile
    private var mirrorHorizontal = false

    @Volatile
    private var breezeStrength = 1f

    @Volatile
    private var playbackSpeed = 1f

    @Volatile
    private var isPrepared = false

    @Volatile
    private var firstFrameNotified = false

    @Volatile
    var loadedAssetPath: String? = null
        private set

    private var onFirstFrameCb: (() -> Unit)? = null
    private var onCompletedCb: (() -> Unit)? = null
    private var onLoopCompletedCb: (() -> Unit)? = null

    // Full-screen quad: (posX, posY, uvX, uvY) where uvY=0 is bottom, uvY=1 is top
    private val vertexCoords = floatArrayOf(
        -1f, -1f, 0f, 0f,
         1f, -1f, 1f, 0f,
        -1f,  1f, 0f, 1f,
         1f,  1f, 1f, 1f
    )
    private val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertexCoords.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply { put(vertexCoords); position(0) }

    fun start(
        context: Context,
        outputSurface: SurfaceTexture,
        width: Int,
        height: Int,
        assetPath: String,
        playing: Boolean,
        looping: Boolean,
        mirror: Boolean,
        breezeStrength: Float,
        speed: Float = 1f,
        onFirstFrame: () -> Unit,
        onCompleted: () -> Unit,
        onLoopCompleted: (() -> Unit)? = null
    ) {
        release()
        this.viewportW = width.coerceAtLeast(1)
        this.viewportH = height.coerceAtLeast(1)
        this.desiredPlaying = playing
        this.mirrorHorizontal = mirror
        this.breezeStrength = breezeStrength
        this.playbackSpeed = speed.coerceIn(0.5f, 1.5f)
        this.onFirstFrameCb = onFirstFrame
        this.onCompletedCb = onCompleted
        this.onLoopCompletedCb = onLoopCompleted
        this.startTimeMs = SystemClock.uptimeMillis()

        val thread = HandlerThread("GlBreezeVideo_${System.nanoTime()}").apply { start() }
        val handler = Handler(thread.looper)
        glThread = thread
        glHandler = handler

        handler.post {
            runCatching {
                initEgl(outputSurface)
                initGl()
                loadAssetOnGlThread(context, assetPath, looping)
            }.onFailure { e ->
                Log.w(TAG, "Breeze GL init failed for $assetPath: ${e.message}")
            }
        }
    }

    fun resize(width: Int, height: Int) {
        viewportW = width.coerceAtLeast(1)
        viewportH = height.coerceAtLeast(1)
    }

    fun updateParams(playing: Boolean, mirror: Boolean, breezeStrength: Float, speed: Float = 1f) {
        this.desiredPlaying = playing
        this.mirrorHorizontal = mirror
        this.breezeStrength = breezeStrength
        val clampedSpeed = speed.coerceIn(0.5f, 1.5f)
        val speedChanged = kotlin.math.abs(this.playbackSpeed - clampedSpeed) > 0.01f
        this.playbackSpeed = clampedSpeed
        glHandler?.post {
            val mp = mediaPlayer ?: return@post
            if (!isPrepared) return@post
            runCatching {
                if (speedChanged && playing) {
                    mp.playbackParams = mp.playbackParams.setSpeed(clampedSpeed)
                }
                if (playing && !mp.isPlaying) {
                    mp.playbackParams = mp.playbackParams.setSpeed(clampedSpeed)
                    if (!mp.isPlaying) mp.start()
                } else if (!playing && mp.isPlaying) {
                    mp.pause()
                }
            }
        }
    }

    fun switchAsset(
        context: Context,
        assetPath: String,
        playing: Boolean,
        looping: Boolean,
        onFirstFrame: () -> Unit,
        onCompleted: () -> Unit,
        onLoopCompleted: (() -> Unit)? = null
    ) {
        this.desiredPlaying = playing
        this.onFirstFrameCb = onFirstFrame
        this.onCompletedCb = onCompleted
        this.onLoopCompletedCb = onLoopCompleted
        glHandler?.post {
            loadAssetOnGlThread(context, assetPath, looping)
        }
    }

    private fun loadAssetOnGlThread(context: Context, assetPath: String, looping: Boolean) {
        loadedAssetPath = assetPath
        isPrepared = false
        firstFrameNotified = false
        runCatching {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.release()
            }
        }
        mediaPlayer = null

        val surf = videoSurface ?: return
        runCatching {
            val afd = context.assets.openFd(assetPath)
            val mp = MediaPlayer()
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.setSurface(surf)
            // When counting loops (Flow Mode), use OnCompletionListener to increment rounds & restart
            val trackLoops = looping && onLoopCompletedCb != null
            mp.isLooping = looping && !trackLoops
            mp.setVolume(0f, 0f)
            mp.setOnVideoSizeChangedListener { _, w, h ->
                if (w > 0 && h > 0) {
                    videoW = w
                    videoH = h
                }
            }
            mp.setOnPreparedListener { prepared ->
                isPrepared = true
                if (prepared.videoWidth > 0 && prepared.videoHeight > 0) {
                    videoW = prepared.videoWidth
                    videoH = prepared.videoHeight
                }
                prepared.setVolume(0f, 0f)
                if (desiredPlaying) {
                    runCatching { prepared.playbackParams = prepared.playbackParams.setSpeed(playbackSpeed) }
                    if (!prepared.isPlaying) prepared.start()
                } else {
                    prepared.seekTo(1)
                }
            }
            mp.setOnCompletionListener { completedPlayer ->
                if (trackLoops) {
                    onLoopCompletedCb?.invoke()
                    if (desiredPlaying) {
                        runCatching {
                            completedPlayer.seekTo(0)
                            completedPlayer.playbackParams = completedPlayer.playbackParams.setSpeed(playbackSpeed)
                            if (!completedPlayer.isPlaying) completedPlayer.start()
                        }
                    }
                } else if (!looping) {
                    onCompletedCb?.invoke()
                }
            }
            mp.setOnErrorListener { _, _, _ -> true }
            mediaPlayer = mp
            mp.prepareAsync()
        }.onFailure { e ->
            Log.w(TAG, "Could not load video asset '$assetPath': ${e.message}")
        }
    }

    private fun initEgl(outputSurface: SurfaceTexture) {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        EGL14.eglInitialize(eglDisplay, version, 0, version, 1)

        val attribs = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        EGL14.eglChooseConfig(eglDisplay, attribs, 0, configs, 0, 1, numConfigs, 0)
        val config = configs[0] ?: error("No RGBA8888 EGLConfig")

        val ctxAttribs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
        eglContext = EGL14.eglCreateContext(eglDisplay, config, EGL14.EGL_NO_CONTEXT, ctxAttribs, 0)
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, config, outputSurface, intArrayOf(EGL14.EGL_NONE), 0)
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
    }

    private fun initGl() {
        val vShader = compileShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fShader = compileShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        program = GLES20.glCreateProgram().also { p ->
            GLES20.glAttachShader(p, vShader)
            GLES20.glAttachShader(p, fShader)
            GLES20.glLinkProgram(p)
        }

        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        oesTextureId = texIds[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        val st = SurfaceTexture(oesTextureId)
        st.setOnFrameAvailableListener({
            glHandler?.post { drawFrame() }
        }, glHandler)
        videoTexture = st
        videoSurface = Surface(st)
    }

    private fun drawFrame() {
        val st = videoTexture ?: return
        val dpy = eglDisplay ?: return
        val surf = eglSurface ?: return
        if (dpy == EGL14.EGL_NO_DISPLAY || surf == EGL14.EGL_NO_SURFACE) return
        runCatching {
            st.updateTexImage()
            st.getTransformMatrix(stMatrix)

            GLES20.glViewport(0, 0, viewportW, viewportH)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            // Aspect-preserving center-crop UV scaling
            val viewRatio = viewportW.toFloat() / viewportH.toFloat()
            val videoRatio = videoW.toFloat() / videoH.toFloat()
            val cropScaleX: Float
            val cropScaleY: Float
            if (videoRatio > viewRatio) {
                cropScaleX = viewRatio / videoRatio
                cropScaleY = 1f
            } else {
                cropScaleX = 1f
                cropScaleY = videoRatio / viewRatio
            }

            GLES20.glUseProgram(program)
            val aPos = GLES20.glGetAttribLocation(program, "aPosition")
            val aTex = GLES20.glGetAttribLocation(program, "aTexCoord")
            val uMat = GLES20.glGetUniformLocation(program, "uSTMatrix")
            val uCropScale = GLES20.glGetUniformLocation(program, "uCropScale")
            val uMirror = GLES20.glGetUniformLocation(program, "uMirror")
            val uTime = GLES20.glGetUniformLocation(program, "uTime")
            val uBreeze = GLES20.glGetUniformLocation(program, "uBreezeStrength")

            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(aPos)
            GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

            vertexBuffer.position(2)
            GLES20.glEnableVertexAttribArray(aTex)
            GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

            GLES20.glUniformMatrix4fv(uMat, 1, false, stMatrix, 0)
            GLES20.glUniform2f(uCropScale, cropScaleX, cropScaleY)
            GLES20.glUniform1f(uMirror, if (mirrorHorizontal) 1f else 0f)
            val elapsedSec = (SystemClock.uptimeMillis() - startTimeMs) / 1000f
            GLES20.glUniform1f(uTime, elapsedSec)
            GLES20.glUniform1f(uBreeze, if (desiredPlaying) breezeStrength else 0f)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            EGL14.eglSwapBuffers(dpy, surf)

            if (!firstFrameNotified) {
                firstFrameNotified = true
                onFirstFrameCb?.invoke()
            }
        }
    }

    private fun compileShader(type: Int, src: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, src)
        GLES20.glCompileShader(shader)
        return shader
    }

    fun release() {
        val handler = glHandler
        val thread = glThread
        glHandler = null
        glThread = null
        loadedAssetPath = null
        isPrepared = false
        firstFrameNotified = false

        val cleanup = {
            runCatching {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) mp.stop()
                    mp.release()
                }
            }
            mediaPlayer = null
            runCatching { videoSurface?.release() }
            videoSurface = null
            runCatching { videoTexture?.release() }
            videoTexture = null
            val dpy = eglDisplay
            if (dpy != null && dpy != EGL14.EGL_NO_DISPLAY) {
                runCatching {
                    EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                    eglSurface?.takeIf { it != EGL14.EGL_NO_SURFACE }?.let { EGL14.eglDestroySurface(dpy, it) }
                    eglContext?.takeIf { it != EGL14.EGL_NO_CONTEXT }?.let { EGL14.eglDestroyContext(dpy, it) }
                    EGL14.eglTerminate(dpy)
                }
            }
            eglSurface = null
            eglContext = null
            eglDisplay = null
            thread?.quitSafely()
            Unit
        }

        if (handler != null) {
            handler.post(cleanup)
        } else {
            cleanup()
        }
    }

    companion object {
        private const val TAG = "LoopingStretchVideo"

        private const val VERTEX_SHADER = """
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            uniform mat4 uSTMatrix;
            uniform vec2 uCropScale;
            uniform float uMirror;
            varying vec2 vTexCoord;
            varying vec2 vVideoUV;

            void main() {
                gl_Position = aPosition;
                // Center-crop UV inside [0, 1]
                vec2 uv = (aTexCoord - 0.5) * uCropScale + 0.5;
                if (uMirror > 0.5) {
                    uv.x = 1.0 - uv.x;
                }
                // vVideoUV is unmirrored video-space UV with y=0 at top, y=1 at bottom
                vVideoUV = vec2(uv.x, 1.0 - uv.y);
                vTexCoord = (uSTMatrix * vec4(uv, 0.0, 1.0)).xy;
            }
        """

        private const val FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES sTexture;
            uniform float uTime;
            uniform float uBreezeStrength;
            varying vec2 vTexCoord;
            varying vec2 vVideoUV;

            float ellipseMask(vec2 p, vec2 center, vec2 radius) {
                vec2 d = (p - center) / radius;
                float dist2 = dot(d, d);
                return 1.0 - smoothstep(0.55, 1.0, dist2);
            }

            void main() {
                vec4 base = texture2D(sTexture, vTexCoord);
                if (uBreezeStrength <= 0.01) {
                    gl_FragColor = base;
                    return;
                }

                // Spatial masks for background studio plants (left Monstera, windowsill plant, right ficus)
                vec2 p = vVideoUV;
                float monsteraMask = ellipseMask(p, vec2(0.24, 0.40), vec2(0.14, 0.21));
                float windowPlantMask = ellipseMask(p, vec2(0.14, 0.36), vec2(0.08, 0.12));
                float rightPlantMask = ellipseMask(p, vec2(0.66, 0.31), vec2(0.10, 0.20));
                float zoneMask = max(monsteraMask, max(windowPlantMask, rightPlantMask));

                if (zoneMask <= 0.005) {
                    gl_FragColor = base;
                    return;
                }

                // Green foliage color gate so walls, terracotta pots, and yogi stay 100% still
                float greenExcess = base.g - max(base.r * 0.94, base.b * 1.04);
                float leafMask = smoothstep(0.012, 0.068, greenExcess);
                float weight = zoneMask * leafMask * uBreezeStrength;

                if (weight <= 0.005) {
                    gl_FragColor = base;
                    return;
                }

                // Gentle organic 2-frequency Ghibli breeze gust
                float gust = 0.65 + 0.35 * sin(uTime * 0.60);
                float waveX = (sin(uTime * 1.85 + p.y * 15.0 + p.x * 6.0) * 0.65 +
                               sin(uTime * 3.10 + p.y * 24.0) * 0.35) * 0.0052 * gust;
                float waveY = cos(uTime * 1.40 + p.x * 16.0) * 0.0026 * gust;

                vec2 warpedUV = clamp(vTexCoord + vec2(waveX, waveY) * weight, 0.002, 0.998);
                gl_FragColor = texture2D(sTexture, warpedUV);
            }
        """
    }
}
