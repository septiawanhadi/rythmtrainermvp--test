package com.example.rhythmtrainermvp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rhythmtrainermvp.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GameState {
    IDLE, COUNT_IN, PLAYING, SUMMARY
}

class GameViewModel : ViewModel() {

    // === StateFlows (PRD §2.2) ===

    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _playheadFraction = MutableStateFlow(0f)
    val playheadFraction: StateFlow<Float> = _playheadFraction.asStateFlow()

    private val _currentBeat = MutableStateFlow(-1)
    val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    private val _noteEvents = MutableStateFlow(emptyList<NoteEvent>())
    val noteEvents: StateFlow<List<NoteEvent>> = _noteEvents.asStateFlow()

    private val _countInBeat = MutableStateFlow<Int?>(null)
    val countInBeat: StateFlow<Int?> = _countInBeat.asStateFlow()

    private val _lastFeedback = MutableStateFlow<Pair<NoteResult, Long>?>(null)
    val lastFeedback: StateFlow<Pair<NoteResult, Long>?> = _lastFeedback.asStateFlow()

    private val _showSummary = MutableStateFlow(false)
    val showSummary: StateFlow<Boolean> = _showSummary.asStateFlow()

    // === Internal state ===
    private var falseTaps = 0

    // === Public actions ===

    fun onStartFromIdle() {
        if (_gameState.value != GameState.IDLE) return
        // TODO Phase 5: Move to COUNT_IN, start Oboe count-in
        _gameState.value = GameState.PLAYING // stub: skip count-in for now
        _noteEvents.value = PatternGenerator.generate()
    }

    fun onUserTap() {
        if (_gameState.value != GameState.PLAYING) return

        // TODO Phase 6: Implement full scoring algorithm (PRD §5.2)
        // - Get tap time from RhythmBridge.nativeGetCurrentTimestampMs()
        // - Find nearest PENDING QUARTER_NOTE within scoring windows
        // - Score as PERFECT/GOOD/MISS/FALSE_TAP
        // - Update note result, emit feedback, trigger haptic
    }

    fun onBeatTick(beatIndex: Int, timestampMs: Long) {
        // TODO Phase 5: Called from JNI callback (posted to Main thread)
        // - Update playheadFraction
        // - Auto-score missed notes (PRD §5.2 Miss Auto-Detection)
        // - Check game completion
        _currentBeat.value = beatIndex
        updatePlayheadFraction(timestampMs)
    }

    fun onGameComplete() {
        _gameState.value = GameState.SUMMARY
        _showSummary.value = true
    }

    fun onRetry() {
        // Reset all state, generate new pattern
        falseTaps = 0
        _noteEvents.value = PatternGenerator.generate()
        _playheadFraction.value = 0f
        _currentBeat.value = -1
        _showSummary.value = false
        _lastFeedback.value = null
        _gameState.value = GameState.IDLE
    }

    // === Helpers ===

    private fun updatePlayheadFraction(currentTimeMs: Long) {
        val fraction = currentTimeMs.toFloat() / TimingConstants.TOTAL_GAME_DURATION_MS.toFloat()
        _playheadFraction.value = fraction.coerceIn(0f, 1f)
    }

    fun getSessionScore(): SessionScore {
        val notes = _noteEvents.value
        val noteEvents = notes.filter { it.type == NoteType.QUARTER_NOTE }
        return SessionScore(
            perfect = noteEvents.count { it.result == NoteResult.PERFECT },
            good = noteEvents.count { it.result == NoteResult.GOOD },
            miss = noteEvents.count { it.result == NoteResult.MISS },
            falseTaps = falseTaps,
            totalNotes = noteEvents.size,
        )
    }
}
