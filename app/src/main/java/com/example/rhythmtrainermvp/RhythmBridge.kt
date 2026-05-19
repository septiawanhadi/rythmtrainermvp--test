package com.example.rhythmtrainermvp

/**
 * JNI bridge to the native rhythm engine.
 * ⛔ RULE: ALL JNI calls go through this object — never call System.loadLibrary elsewhere.
 */
@Suppress("unused")
object RhythmBridge {
    init {
        System.loadLibrary("rhythmengine")
    }

    // Called from Kotlin → C++
    external fun nativeInit(sampleRate: Int, framesPerBurst: Int): Boolean
    external fun nativeStartCountIn()
    external fun nativeStartPlayback()
    external fun nativeStop()
    external fun nativeDestroy()
    external fun nativeGetCurrentTimestampMs(): Long
}
