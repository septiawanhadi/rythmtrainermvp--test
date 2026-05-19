package com.example.rhythmtrainermvp.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class HapticType { PERFECT, MISS }

class HapticController(context: Context) {
    private val vibrator = if (Build.VERSION.SDK_INT >= 31) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate(type: HapticType) {
        val effect = when (type) {
            HapticType.PERFECT -> VibrationEffect.createOneShot(30L, 80)
            HapticType.MISS    -> VibrationEffect.createOneShot(80L, 255)
        }
        vibrator.vibrate(effect)
    }
}
