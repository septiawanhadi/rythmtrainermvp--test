package com.example.rhythmtrainermvp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhythmtrainermvp.RhythmBridge
import com.example.rhythmtrainermvp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class GameViewModel : ViewModel(), RhythmBridge.EngineListener {

    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _playheadFraction = MutableStateFlow(0f)
    val playheadFraction: StateFlow<Float> = _playheadFraction.asStateFlow()

    private val _noteEvents = MutableStateFlow<List<NoteEvent>>(emptyList())
    val noteEvents: StateFlow<List<NoteEvent>> = _noteEvents.asStateFlow()

    private val _sessionScore = MutableStateFlow(SessionScore())
    val sessionScore: StateFlow<SessionScore> = _sessionScore.asStateFlow()

    private val _countInBeat = MutableStateFlow<Int?>(null)
    val countInBeat: StateFlow<Int?> = _countInBeat.asStateFlow()

    private val _lastFeedback = MutableStateFlow<Pair<NoteResult, Long>?>(null)
    val lastFeedback: StateFlow<Pair<NoteResult, Long>?> = _lastFeedback.asStateFlow()

    init {
        RhythmBridge.setListener(this)
        generateNewPattern()
    }

    private fun generateNewPattern() {
        _noteEvents.value = PatternGenerator.generate()
        _sessionScore.value = SessionScore(totalNotes = _noteEvents.value.count { it.type == NoteType.QUARTER_NOTE })
    }

    fun onStartRequested() {
        if (_gameState.value == GameState.IDLE) {
            _gameState.value = GameState.COUNT_IN
            RhythmBridge.nativeStartCountIn()
        }
    }

    fun onUserTap() {
        if (_gameState.value != GameState.PLAYING) {
            if (_gameState.value == GameState.IDLE) onStartRequested()
            return
        }

        val tapTimeMs = RhythmBridge.nativeGetCurrentTimestampMs()
        val events = _noteEvents.value

        // Find nearest PENDING note
        val candidate = events
            .filter { it.type == NoteType.QUARTER_NOTE && it.result == NoteResult.PENDING }
            .minByOrNull { abs(it.timestampMs - tapTimeMs) }

        if (candidate == null) {
            recordResult(NoteResult.FALSE_TAP)
            return
        }

        val delta = abs(candidate.timestampMs - tapTimeMs)
        val result = when {
            delta <= TimingConstants.WINDOW_PERFECT_MS -> NoteResult.PERFECT
            delta <= TimingConstants.WINDOW_GOOD_MS -> NoteResult.GOOD
            else -> {
                recordResult(NoteResult.FALSE_TAP)
                return
            }
        }

        // Update note result
        _noteEvents.value = events.map {
            if (it.index == candidate.index) it.copy(result = result) else it
        }
        recordResult(result)
    }

    private fun recordResult(result: NoteResult) {
        val current = _sessionScore.value
        _sessionScore.value = when (result) {
            NoteResult.PERFECT -> current.copy(perfect = current.perfect + 1)
            NoteResult.GOOD -> current.copy(good = current.good + 1)
            NoteResult.MISS -> current.copy(miss = current.miss + 1)
            NoteResult.FALSE_TAP -> current.copy(falseTaps = current.falseTaps + 1)
            else -> current
        }
        _lastFeedback.value = result to System.currentTimeMillis()
    }

    fun onRetry() {
        _gameState.value = GameState.IDLE
        _playheadFraction.value = 0f
        _countInBeat.value = null
        _lastFeedback.value = null
        generateNewPattern()
    }

    // --- RhythmBridge.EngineListener Callbacks (Called from Audio Thread) ---

    override fun onBeatTick(beatIndex: Int, timestampMs: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            if (_gameState.value == GameState.COUNT_IN) {
                if (beatIndex >= TimingConstants.COUNT_IN_BEATS - 1) {
                    _gameState.value = GameState.PLAYING
                    _countInBeat.value = null
                    RhythmBridge.nativeStartPlayback()
                } else {
                    _countInBeat.value = beatIndex + 2 // Display 1, 2, 3, 4
                }
            }

            if (_gameState.value == GameState.PLAYING) {
                _playheadFraction.value = (beatIndex.toFloat() / TimingConstants.TOTAL_BEATS).coerceIn(0f, 1f)
                
                // Auto-miss detection for previous notes
                _noteEvents.value = _noteEvents.value.map { note ->
                    if (note.type == NoteType.QUARTER_NOTE && 
                        note.result == NoteResult.PENDING && 
                        timestampMs - note.timestampMs > TimingConstants.MISS_DETECTION_GRACE_MS) {
                        recordResult(NoteResult.MISS)
                        note.copy(result = NoteResult.MISS)
                    } else {
                        note
                    }
                }
            }
        }
    }

    override fun onPlaybackComplete() {
        viewModelScope.launch(Dispatchers.Main) {
            _gameState.value = GameState.SUMMARY
        }
    }

    override fun onCleared() {
        super.onCleared()
        RhythmBridge.nativeStop()
    }
}
