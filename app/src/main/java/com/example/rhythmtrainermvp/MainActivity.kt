package com.example.rhythmtrainermvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Phase 1: Initialize audio engine (no-op stub)
        // Phase 2: Wire actual sampleRate + framesPerBurst from AudioManager
        RhythmBridge.nativeInit(44100, 192)

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RhythmColors.AppBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Rhythm Trainer MVP",
                    color = Color.White
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RhythmBridge.nativeDestroy()
    }
}
