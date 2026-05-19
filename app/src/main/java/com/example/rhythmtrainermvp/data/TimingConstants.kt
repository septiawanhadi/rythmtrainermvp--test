package com.example.rhythmtrainermvp.data

object TimingConstants {
    const val BPM = 80
    const val MS_PER_BEAT: Long = 60_000L / BPM   // 750ms
    const val TOTAL_BEATS = 16
    const val COUNT_IN_BEATS = 4
    const val TOTAL_GAME_DURATION_MS: Long = TOTAL_BEATS * MS_PER_BEAT  // 12_000ms

    // Scoring windows (±ms from beat timestamp)
    const val WINDOW_PERFECT_MS: Long = 40L    // ±40ms = Perfect
    const val WINDOW_GOOD_MS: Long = 80L       // ±41ms to ±80ms = Good

    // Miss detection grace period
    const val MISS_DETECTION_GRACE_MS: Long = WINDOW_GOOD_MS + 16L  // ~96ms
}
