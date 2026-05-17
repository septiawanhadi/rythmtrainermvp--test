package com.example.rhythmtrainermvp.audio

class AudioController {

    private var engineHandle: Long = 0

    companion object {
        init {
            System.loadLibrary("rhythmtrainermvp")
        }
    }

    fun startEngine() {
        if (engineHandle == 0L) {
            engineHandle = nativeStartEngine()
        }
    }

    fun stopEngine() {
        if (engineHandle != 0L) {
            nativeStopEngine(engineHandle)
            engineHandle = 0L
        }
    }

    private external fun nativeStartEngine(): Long
    private external fun nativeStopEngine(engineHandle: Long)
}
