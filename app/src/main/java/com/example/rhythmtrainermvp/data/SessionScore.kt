package com.example.rhythmtrainermvp.data

data class SessionScore(
    val perfect: Int,
    val good: Int,
    val miss: Int,
    val falseTaps: Int,
    val totalNotes: Int        // count of QUARTER_NOTE events only (not rests)
) {
    val accuracyPercent: Float
        get() = if (totalNotes == 0) 0f
                else (perfect + good).toFloat() / totalNotes.toFloat() * 100f
}
