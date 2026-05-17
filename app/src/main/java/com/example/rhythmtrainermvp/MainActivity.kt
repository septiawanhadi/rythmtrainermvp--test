package com.example.rhythmtrainermvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.rhythmtrainermvp.audio.AudioController
import com.example.rhythmtrainermvp.data.PreferencesManager
import com.example.rhythmtrainermvp.ui.RhythmScreen
import com.example.rhythmtrainermvp.viewmodel.RhythmViewModel
import com.example.rhythmtrainermvp.viewmodel.RhythmViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var audioController: AudioController
    private lateinit var preferencesManager: PreferencesManager

    private val viewModel: RhythmViewModel by viewModels {
        RhythmViewModelFactory(preferencesManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        audioController = AudioController()
        preferencesManager = PreferencesManager(applicationContext)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RhythmScreen(
                        viewModel = viewModel,
                        onTogglePlay = {
                            viewModel.togglePlay()
                            if (viewModel.uiState.value.isPlaying) {
                                audioController.startEngine()
                            } else {
                                audioController.stopEngine()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.uiState.value.isPlaying) {
            audioController.stopEngine()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.isPlaying) {
            audioController.startEngine()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        audioController.stopEngine()
    }
}