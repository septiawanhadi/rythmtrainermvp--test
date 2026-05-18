package com.example.rhythmtrainermvp

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rhythmtrainermvp.ui.RhythmScreen
import com.example.rhythmtrainermvp.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inisialisasi awal NDK Audio Engine
        initAudioEngine()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen(onLoadComplete = { showSplash = false })
                    } else {
                        // RhythmScreen di sini bertindak sebagai GameScreen utama
                        RhythmScreen()
                    }
                }
            }
        }
    }

    private fun initAudioEngine() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toInt() ?: 48000
        val framesPerBurst = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toInt() ?: 128
        
        // Inisialisasi engine via JNI Bridge
        RhythmBridge.nativeInit(sampleRate, framesPerBurst)
    }

    override fun onDestroy() {
        super.onDestroy()
        RhythmBridge.nativeDestroy()
    }
}
