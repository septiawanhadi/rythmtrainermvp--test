package com.example.rhythmtrainermvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rhythmtrainermvp.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RhythmUiState(
    val isPlaying: Boolean = false,
    val score: Int = 0,
    val highestScore: Int = 0,
    val lastFeedback: String = "" // "Perfect", "Good", "Miss"
)

class RhythmViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RhythmUiState())
    val uiState: StateFlow<RhythmUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.highestScoreFlow.collect { highestScore ->
                _uiState.value = _uiState.value.copy(highestScore = highestScore)
            }
        }
    }

    fun togglePlay() {
        _uiState.value = _uiState.value.copy(isPlaying = !_uiState.value.isPlaying)
        if (_uiState.value.isPlaying) {
            _uiState.value = _uiState.value.copy(score = 0, lastFeedback = "")
        }
    }

    fun onUserTap(timestampNs: Long) {
        if (!_uiState.value.isPlaying) return

        // TODO: MOCK LOGIC for expected timestamp.
        // Di aplikasi nyata, kita akan menerima waktu ketukan dari Oboe C++.
        // Untuk MVP ini, kita mensimulasikan selisih waktu.
        val expectedTimestampNs = System.nanoTime() 
        val deltaMs = Math.abs(timestampNs - expectedTimestampNs) / 1_000_000

        val (feedback, points) = when {
            deltaMs <= 40 -> "Perfect" to 10
            deltaMs <= 80 -> "Good" to 5
            else -> "Miss" to 0
        }

        val newScore = _uiState.value.score + points
        _uiState.value = _uiState.value.copy(
            score = newScore,
            lastFeedback = feedback
        )

        if (newScore > _uiState.value.highestScore) {
            viewModelScope.launch {
                preferencesManager.saveHighestScore(newScore)
            }
        }
    }
}

class RhythmViewModelFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RhythmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RhythmViewModel(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
