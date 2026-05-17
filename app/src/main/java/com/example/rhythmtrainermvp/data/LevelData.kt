package com.example.rhythmtrainermvp.data

import androidx.annotation.Keep

@Keep
data class LevelData(
    val id: String,
    val name: String,
    val tempo: Int,
    val timeSignature: String,
    val totalBars: Int,
    val notes: List<NoteData>
)

@Keep
data class NoteData(
    val type: String // "quarter_note" or "quarter_rest"
)
