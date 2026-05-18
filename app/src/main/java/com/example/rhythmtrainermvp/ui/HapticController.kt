package com.example.rhythmtrainermvp.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * /ui/HapticController.kt
 * Implementasi PRD Section 5.5.
 */
enum class HapticType { PERFECT, GOOD, MISS }

class HapticController(context: Context) {
    private val vibrator = if (Build.VERSION.SDK_INT >= 31) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate(type: HapticType) {
        val effect = when (type) {
            HapticType.PERFECT -> VibrationEffect.createOneShot(30L, 150)   // short, medium-strong
            HapticType.GOOD -> VibrationEffect.createOneShot(30L, 80)      // short, soft
            HapticType.MISS -> VibrationEffect.createOneShot(80L, 255)     // longer, maximum strength
        }
        vibrator.vibrate(effect)
    }
}
