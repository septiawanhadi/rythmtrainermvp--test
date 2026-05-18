package com.example.rhythmtrainermvp

import android.util.Log

/**
 * RhythmBridge.kt
 * ⛔ RULE: ALL JNI calls go through this object — never call System.loadLibrary elsewhere.
 */
object RhythmBridge {
    
    interface EngineListener {
        fun onBeatTick(beatIndex: Int, timestampMs: Long)
        fun onPlaybackComplete()
    }

    private var listener: EngineListener? = null

    init {
        try {
            System.loadLibrary("rhythmengine")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("RhythmBridge", "Native library 'rhythmengine' not found.")
        }
    }

    fun setListener(listener: EngineListener) {
        this.listener = listener
    }

    // Called from C++ via JNI
    @JvmStatic
    fun onBeatTick(beatIndex: Int, timestampMs: Long) {
        listener?.onBeatTick(beatIndex, timestampMs)
    }

    @JvmStatic
    fun onPlaybackComplete() {
        listener?.onPlaybackComplete()
    }

    // Called from Kotlin → C++
    external fun nativeInit(sampleRate: Int, framesPerBurst: Int): Boolean
    external fun nativeStartCountIn()
    external fun nativeStartPlayback()
    external fun nativeStop()
    external fun nativeDestroy()
    external fun nativeGetCurrentTimestampMs(): Long
}
