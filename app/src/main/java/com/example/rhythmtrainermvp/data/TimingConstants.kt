package com.example.rhythmtrainermvp.data

// /data/TimingConstants.kt
object TimingConstants {
    const val BPM                  = 80
    const val MS_PER_BEAT          = 60_000L / BPM   // 750ms
    const val TOTAL_BEATS          = 16
    const val COUNT_IN_BEATS       = 4
    const val TOTAL_GAME_DURATION_MS = TOTAL_BEATS * MS_PER_BEAT  // 12_000ms

    // Scoring windows (±ms from beat timestamp)
    const val WINDOW_PERFECT_MS    = 40L    // ±40ms = Perfect
    const val WINDOW_GOOD_MS       = 80L    // ±41ms to ±80ms = Good
    // > ±80ms = Miss

    // Miss detection: if a QUARTER_NOTE's timestamp passes with no tap
    // within WINDOW_GOOD_MS + one processing frame, it's auto-scored MISS.
    const val MISS_DETECTION_GRACE_MS = WINDOW_GOOD_MS + 16L  // ~96ms
}
