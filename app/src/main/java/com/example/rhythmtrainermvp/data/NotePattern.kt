package com.example.rhythmtrainermvp.data

// /data/NotePattern.kt

enum class NoteType { QUARTER_NOTE, QUARTER_REST }

enum class NoteResult { PENDING, PERFECT, GOOD, MISS, FALSE_TAP }

enum class GameState {
    IDLE,        // app just opened, "Tap Anywhere to Start" visible
    COUNT_IN,    // 4-beat metronome count-in running, playhead not yet moving
    PLAYING,     // 16-beat gameplay, playhead moving
    SUMMARY      // game over, SummaryOverlay shown
}

data class NoteEvent(
    val index: Int,          // 0–15 (beat position in the 16-beat sequence)
    val type: NoteType,
    val timestampMs: Long,   // absolute engine time (ms) when this beat fires
    var result: NoteResult = NoteResult.PENDING
)

data class SessionScore(
    val perfect: Int = 0,
    val good: Int = 0,
    val miss: Int = 0,
    val falseTaps: Int = 0,
    val totalNotes: Int = 0        // count of QUARTER_NOTE events only (not rests)
) {
    val accuracyPercent: Float
        get() = if (totalNotes == 0) 0f
                else (perfect + good).toFloat() / totalNotes.toFloat() * 100f
}
