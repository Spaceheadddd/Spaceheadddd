package com.spacehead.handwarmer

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLExt
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * TURBO heat: renders a heavy fragment shader into an offscreen buffer, duty-cycled
 * the same way as the CPU workers. Needs no window, so it keeps going with the screen off.
 */
class GpuBurner(private val duty: () -> Float) {

    @Volatile private var running = false
    /** GPU throughput in megapixels shaded per second, for the session log; null until measured. */
    @Volatile var megapixelsPerSecond: Int? = null
        private set
    private var thread: Thread? = null

    fun start() {
        running = true
        thread = Thread({ run() }, "heater-gpu").apply { isDaemon = true; start() }
    }

    fun stop() {
        running = false
        thread?.interrupt()
        thread = null
    }

    private fun run() {
        val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        if (!EGL14.eglInitialize(display, version, 0, version, 1)) return

        val configs = arrayOfNulls<EGLConfig>(1)
        val count = IntArray(1)
        val attribs = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGLExt.EGL_OPENGL_ES3_BIT_KHR,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_NONE
        )
        if (!EGL14.eglChooseConfig(display, attribs, 0, configs, 0, 1, count, 0) || count[0] == 0) return
        val config = configs[0]

        // Ask for a low-priority GPU context so the display compositor and app UI
        // always preempt the heat load (no stutter or glitches on screen).
        var lowPriority = EGL14.eglQueryString(display, EGL14.EGL_EXTENSIONS)
            ?.contains("EGL_IMG_context_priority") == true
        var context = if (lowPriority) {
            EGL14.eglCreateContext(
                display, config, EGL14.EGL_NO_CONTEXT,
                intArrayOf(
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                    EGL_CONTEXT_PRIORITY_LEVEL_IMG, EGL_CONTEXT_PRIORITY_LOW_IMG,
                    EGL14.EGL_NONE
                ), 0
            )
        } else EGL14.EGL_NO_CONTEXT
        if (context == EGL14.EGL_NO_CONTEXT) {
            // Driver refused the priority hint: plain context, and the loop leaves gaps instead.
            lowPriority = false
            context = EGL14.eglCreateContext(
                display, config, EGL14.EGL_NO_CONTEXT,
                intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE), 0
            )
        }
        val surface = EGL14.eglCreatePbufferSurface(
            display, config, intArrayOf(EGL14.EGL_WIDTH, SIZE, EGL14.EGL_HEIGHT, SIZE, EGL14.EGL_NONE), 0
        )
        if (context == EGL14.EGL_NO_CONTEXT || surface == EGL14.EGL_NO_SURFACE ||
            !EGL14.eglMakeCurrent(display, surface, surface, context)
        ) {
            if (surface != EGL14.EGL_NO_SURFACE) EGL14.eglDestroySurface(display, surface)
            if (context != EGL14.EGL_NO_CONTEXT) EGL14.eglDestroyContext(display, context)
            return
        }

        try {
            val program = buildProgram() ?: return
            GLES30.glUseProgram(program)
            val tLoc = GLES30.glGetUniformLocation(program, "t")
            val tri = ByteBuffer.allocateDirect(6 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(floatArrayOf(-1f, -1f, 3f, -1f, -1f, 3f)).also { it.position(0) }
            GLES30.glEnableVertexAttribArray(0)
            GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, tri)
            val windowNs = 100_000_000L
            var frame = 0
            var countStart = System.nanoTime()
            var pixelsInSecond = 0L
            // Side of the square drawn per job; adapts so each GPU job stays within
            // JOB_BUDGET_NS, short enough that screen frames are never held up.
            var side = 512
            while (running) {
                val start = System.nanoTime()
                val busyNs = (windowNs * duty()).toLong()
                while (running && System.nanoTime() - start < busyNs) {
                    val jobStart = System.nanoTime()
                    GLES30.glViewport(0, 0, side, side)
                    GLES30.glUniform1f(tLoc, (frame++ % 10_000) * 0.001f)
                    GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
                    GLES30.glFinish()
                    val jobNs = System.nanoTime() - jobStart
                    pixelsInSecond += side.toLong() * side
                    side = when {
                        jobNs > JOB_BUDGET_NS -> (side * 0.85f).toInt().coerceAtLeast(MIN_SIDE)
                        jobNs < JOB_BUDGET_NS / 2 -> (side * 1.1f).toInt().coerceAtMost(SIZE)
                        else -> side
                    }
                    // Without context priority, leave the GPU a short gap for the UI.
                    if (!lowPriority) {
                        try {
                            Thread.sleep(1)
                        } catch (_: InterruptedException) {
                            running = false
                        }
                    }
                }
                val now = System.nanoTime()
                if (now - countStart >= 1_000_000_000L) {
                    megapixelsPerSecond = (pixelsInSecond * 1_000L / (now - countStart)).toInt()
                    pixelsInSecond = 0
                    countStart = now
                }
                val restMs = (windowNs - (System.nanoTime() - start)) / 1_000_000L
                if (restMs > 0) {
                    try {
                        Thread.sleep(restMs)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }
            GLES30.glDeleteProgram(program)
        } finally {
            // No eglTerminate: the display is shared with the app's UI renderer.
            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglReleaseThread()
        }
    }

    private fun buildProgram(): Int? {
        val vs = compile(GLES30.GL_VERTEX_SHADER, VERTEX) ?: return null
        val fs = compile(GLES30.GL_FRAGMENT_SHADER, FRAGMENT) ?: return null
        val p = GLES30.glCreateProgram()
        GLES30.glAttachShader(p, vs)
        GLES30.glAttachShader(p, fs)
        GLES30.glBindAttribLocation(p, 0, "p")
        GLES30.glLinkProgram(p)
        GLES30.glDeleteShader(vs)
        GLES30.glDeleteShader(fs)
        val ok = IntArray(1)
        GLES30.glGetProgramiv(p, GLES30.GL_LINK_STATUS, ok, 0)
        if (ok[0] == 0) {
            GLES30.glDeleteProgram(p)
            return null
        }
        return p
    }

    private fun compile(type: Int, src: String): Int? {
        val s = GLES30.glCreateShader(type)
        GLES30.glShaderSource(s, src)
        GLES30.glCompileShader(s)
        val ok = IntArray(1)
        GLES30.glGetShaderiv(s, GLES30.GL_COMPILE_STATUS, ok, 0)
        if (ok[0] == 0) {
            GLES30.glDeleteShader(s)
            return null
        }
        return s
    }

    private companion object {
        /** Largest square per job (and the pbuffer size). */
        const val SIZE = 1536
        const val MIN_SIDE = 128
        /** Max GPU time per job: well under one 120 Hz frame (8.3 ms). */
        const val JOB_BUDGET_NS = 4_000_000L

        // EGL_IMG_context_priority (not in EGL14 constants).
        const val EGL_CONTEXT_PRIORITY_LEVEL_IMG = 0x3100
        const val EGL_CONTEXT_PRIORITY_LOW_IMG = 0x3103

        const val VERTEX = """#version 300 es
in vec2 p;
void main() { gl_Position = vec4(p, 0.0, 1.0); }
"""

        // Per-pixel iterated trig; depends on position and time so it can't be folded away.
        const val FRAGMENT = """#version 300 es
precision highp float;
uniform float t;
out vec4 o;
void main() {
    vec2 uv = gl_FragCoord.xy * 0.001 + t;
    float a = uv.x;
    float b = uv.y;
    for (int i = 0; i < 192; i++) {
        a = sin(a * 1.31 + b) + cos(b * 0.73 - a);
        b = fract(a * b + 0.123) * 2.0 - 1.0;
    }
    o = vec4(a, b, a * b, 1.0);
}
"""
    }
}
