package com.example.rhythmtrainermvp.data

import kotlin.random.Random

/**
 * /data/PatternGenerator.kt
 * Generates a reproducible list of 16 NoteEvents for a 4-bar, 4/4, 80 BPM pattern.
 * ⛔ RULE: Must always produce exactly 16 NoteEvents with indices 0–15.
 * ⛔ RULE: Must always have at least 4 QUARTER_NOTE and at least 2 QUARTER_REST
 *          per generation to guarantee meaningful gameplay and rest handling.
 */
object PatternGenerator {
    private const val MS_PER_BEAT = 750L   // 80 BPM -> 60000 / 80 = 750ms
    private const val TOTAL_BEATS = 16

    fun generate(seed: Long = System.currentTimeMillis()): List<NoteEvent> {
        val random = Random(seed)
        var pattern: List<NoteEvent>
        
        // Ensure constraints: at least 4 notes and 2 rests
        do {
            pattern = (0 until TOTAL_BEATS).map { i ->
                NoteEvent(
                    index = i,
                    type = if (random.nextFloat() < 0.75f) NoteType.QUARTER_NOTE
                           else NoteType.QUARTER_REST,
                    timestampMs = i * MS_PER_BEAT
                )
            }
            val noteCount = pattern.count { it.type == NoteType.QUARTER_NOTE }
            val restCount = TOTAL_BEATS - noteCount
        } while (noteCount < 4 || restCount < 2)
        
        return pattern
    }
}
