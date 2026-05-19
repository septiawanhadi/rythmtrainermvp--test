package com.example.rhythmtrainermvp.data

enum class NoteType { QUARTER_NOTE, QUARTER_REST }

enum class NoteResult { PENDING, PERFECT, GOOD, MISS, FALSE_TAP }

data class NoteEvent(
    val index: Int,           // 0–15 (beat position in the 16-beat sequence)
    val type: NoteType,
    val timestampMs: Long,    // absolute engine time (ms) when this beat fires
    var result: NoteResult = NoteResult.PENDING
)
