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
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.StudioLightingMode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

enum class OverlayKeyMode(val shaderId: Int) {
    GREEN_KEY(0),
    BLUE_KEY(1),
    SCREEN_BLACK(2)
}

/**
 * Hardware-accelerated OpenGL ES 2.0 transparent video overlay for Android.
 *
 * Plays compact H.264 MP4s from APK assets (assets/stretch_overlays/) and keys out
 * green (#00FF00), blue (#0000FF), or black (#000000 screen-blend) in real time at 60fps
 * with spill suppression and synchronized Day/Sunset/Night color grading.
 */
@Composable
fun ChromaKeyVideoOverlay(
    assetPath: String,
    keyMode: OverlayKeyMode,
    lightingMode: StudioLightingMode = StudioLightingMode.DAY,
    opacity: Float = 1f,
    isPlaying: Boolean = true,
    modifier: Modifier = Modifier
) {
    val currentPlaying by rememberUpdatedState(isPlaying)
    val currentOpacity by rememberUpdatedState(opacity)
    val currentLighting by rememberUpdatedState(lightingMode)
    val renderer = remember { GlChromaKeyRenderer() }

    DisposableEffect(isPlaying, opacity, lightingMode, keyMode) {
        renderer.updateParams(
            keyMode = keyMode,
            lightingMode = lightingMode,
            opacity = opacity,
            playing = isPlaying
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
                            keyMode = keyMode,
                            lightingMode = currentLighting,
                            opacity = currentOpacity,
                            playing = currentPlaying
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
            tv.alpha = opacity.coerceIn(0f, 1f)
            renderer.updateParams(
                keyMode = keyMode,
                lightingMode = lightingMode,
                opacity = opacity,
                playing = isPlaying
            )
            if (renderer.currentAssetPath != assetPath && tv.isAvailable) {
                renderer.switchAsset(tv.context.applicationContext, assetPath, isPlaying)
            }
        },
        modifier = modifier
    )
}

private class GlChromaKeyRenderer {
    private var glThread: HandlerThread? = null
    private var glHandler: Handler? = null

    private var eglDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var eglContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var eglSurface: EGLSurface = EGL14.EGL_NO_SURFACE

    private var program = 0
    private var oesTextureId = 0
    private var videoTexture: SurfaceTexture? = null
    private var videoSurface: Surface? = null
    private var mediaPlayer: MediaPlayer? = null

    private val stMatrix = FloatArray(16)
    private var viewportW = 1
    private var viewportH = 1

    @Volatile
    private var keyMode: OverlayKeyMode = OverlayKeyMode.GREEN_KEY

    @Volatile
    private var lightingMode: StudioLightingMode = StudioLightingMode.DAY

    @Volatile
    private var opacity: Float = 1f

    @Volatile
    private var desiredPlaying: Boolean = true

    @Volatile
    private var isPrepared: Boolean = false

    @Volatile
    var currentAssetPath: String? = null
        private set

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
        keyMode: OverlayKeyMode,
        lightingMode: StudioLightingMode,
        opacity: Float,
        playing: Boolean
    ) {
        release()
        this.keyMode = keyMode
        this.lightingMode = lightingMode
        this.opacity = opacity
        this.desiredPlaying = playing
        this.viewportW = width.coerceAtLeast(1)
        this.viewportH = height.coerceAtLeast(1)

        val thread = HandlerThread("GlOverlay_${System.nanoTime()}").apply { start() }
        val handler = Handler(thread.looper)
        glThread = thread
        glHandler = handler

        handler.post {
            runCatching {
                initEgl(outputSurface)
                initGl()
                loadAssetOnGlThread(context, assetPath)
            }.onFailure { e ->
                Log.w(TAG, "Overlay GL init failed for $assetPath: ${e.message}")
            }
        }
    }

    fun resize(width: Int, height: Int) {
        viewportW = width.coerceAtLeast(1)
        viewportH = height.coerceAtLeast(1)
    }

    fun updateParams(
        keyMode: OverlayKeyMode,
        lightingMode: StudioLightingMode,
        opacity: Float,
        playing: Boolean
    ) {
        this.keyMode = keyMode
        this.lightingMode = lightingMode
        this.opacity = opacity.coerceIn(0f, 1f)
        this.desiredPlaying = playing
        glHandler?.post {
            val mp = mediaPlayer ?: return@post
            if (!isPrepared) return@post
            runCatching {
                if (playing && !mp.isPlaying) mp.start()
                else if (!playing && mp.isPlaying) mp.pause()
            }
        }
    }

    fun switchAsset(context: Context, newAssetPath: String, playing: Boolean) {
        desiredPlaying = playing
        glHandler?.post {
            loadAssetOnGlThread(context, newAssetPath)
        }
    }

    private fun loadAssetOnGlThread(context: Context, assetPath: String) {
        currentAssetPath = assetPath
        isPrepared = false
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
            mp.isLooping = true
            mp.setVolume(0f, 0f)
            mp.setOnPreparedListener { prepared ->
                isPrepared = true
                prepared.setVolume(0f, 0f)
                if (desiredPlaying) prepared.start() else prepared.seekTo(1)
            }
            mp.setOnErrorListener { _, _, _ -> true }
            mediaPlayer = mp
            mp.prepareAsync()
        }.onFailure { e ->
            Log.w(TAG, "Failed to load overlay asset $assetPath: ${e.message}")
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
        if (eglDisplay == EGL14.EGL_NO_DISPLAY || eglSurface == EGL14.EGL_NO_SURFACE) return
        runCatching {
            st.updateTexImage()
            st.getTransformMatrix(stMatrix)

            GLES20.glViewport(0, 0, viewportW, viewportH)
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            GLES20.glUseProgram(program)
            val aPos = GLES20.glGetAttribLocation(program, "aPosition")
            val aTex = GLES20.glGetAttribLocation(program, "aTexCoord")
            val uMat = GLES20.glGetUniformLocation(program, "uSTMatrix")
            val uKey = GLES20.glGetUniformLocation(program, "uKeyMode")
            val uLight = GLES20.glGetUniformLocation(program, "uLightMode")
            val uOpacity = GLES20.glGetUniformLocation(program, "uOpacity")

            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(aPos)
            GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

            vertexBuffer.position(2)
            GLES20.glEnableVertexAttribArray(aTex)
            GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

            GLES20.glUniformMatrix4fv(uMat, 1, false, stMatrix, 0)
            GLES20.glUniform1i(uKey, keyMode.shaderId)
            val lightId = when (lightingMode) {
                StudioLightingMode.SUNSET -> 1
                StudioLightingMode.NIGHT -> 2
                else -> 0
            }
            GLES20.glUniform1i(uLight, lightId)
            GLES20.glUniform1f(uOpacity, opacity)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

            EGL14.eglSwapBuffers(eglDisplay, eglSurface)
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
        currentAssetPath = null

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
            if (program != 0) {
                runCatching { GLES20.glDeleteProgram(program) }
                program = 0
            }
            if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
                runCatching {
                    EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
                    if (eglSurface != EGL14.EGL_NO_SURFACE) EGL14.eglDestroySurface(eglDisplay, eglSurface)
                    if (eglContext != EGL14.EGL_NO_CONTEXT) EGL14.eglDestroyContext(eglDisplay, eglContext)
                    EGL14.eglTerminate(eglDisplay)
                }
            }
            eglSurface = EGL14.EGL_NO_SURFACE
            eglContext = EGL14.EGL_NO_CONTEXT
            eglDisplay = EGL14.EGL_NO_DISPLAY
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
        private const val TAG = "ChromaKeyVideoOverlay"

        private const val VERTEX_SHADER = """
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            uniform mat4 uSTMatrix;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = aPosition;
                vTexCoord = (uSTMatrix * vec4(aTexCoord, 0.0, 1.0)).xy;
            }
        """

        private const val FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES sTexture;
            uniform int uKeyMode;
            uniform int uLightMode;
            uniform float uOpacity;
            varying vec2 vTexCoord;

            void main() {
                vec4 c = texture2D(sTexture, vTexCoord);
                vec3 rgb = c.rgb;
                float alpha = 1.0;

                if (uKeyMode == 0) {
                    // Pure green screen (#00FF00) for diffuser & incense overlays
                    float maxRB = max(rgb.r, rgb.b);
                    float greenExcess = rgb.g - maxRB;
                    alpha = 1.0 - smoothstep(0.08, 0.24, greenExcess);
                    rgb.g = min(rgb.g, maxRB * 1.04 + 0.03);
                } else if (uKeyMode == 1) {
                    // Pure blue screen (#0000FF / #0088FF) for dog & corner leaves overlays
                    float maxRG = max(rgb.r, rgb.g);
                    float blueExcess = rgb.b - maxRG;
                    alpha = 1.0 - smoothstep(0.06, 0.20, blueExcess);
                    // Suppress blue fringe/halo along fur and leaf edges
                    rgb.b = min(rgb.b, maxRG * 1.02 + 0.02);
                } else {
                    // Screen/additive blend on black (#000000) for sunbeams & dust motes
                    float lum = max(rgb.r, max(rgb.g, rgb.b));
                    alpha = smoothstep(0.04, 0.72, lum);
                }

                // Match Day / Sunset / Night studio lighting on L1 props
                if (uKeyMode != 2) {
                    if (uLightMode == 1) {
                        // Sunset warm amber
                        rgb = clamp(rgb * vec3(1.10, 0.97, 0.78) + vec3(0.05, 0.015, -0.03), 0.0, 1.0);
                    } else if (uLightMode == 2) {
                        // Night twilight indigo
                        rgb = clamp(rgb * vec3(0.62, 0.68, 0.92) - vec3(0.08), 0.0, 1.0);
                    }
                }

                alpha *= uOpacity;
                gl_FragColor = vec4(rgb * alpha, alpha);
            }
        """
    }
}
