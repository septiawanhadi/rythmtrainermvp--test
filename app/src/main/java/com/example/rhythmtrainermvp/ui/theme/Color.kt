package com.example.rhythmtrainermvp.ui.theme

import androidx.compose.ui.graphics.Color

// /ui/theme/Color.kt — SINGLE SOURCE OF TRUTH for all colors
// ⛔ RULE: Never hardcode hex values outside this file.

object RhythmColors {
    // Base
    val AppBackground   = Color(0xFF121212)   // Material Dark charcoal
    val TapZoneBg       = Color(0xFF121212)   // same as app bg, no visual border

    // Staff & Notation
    val StaffLine       = Color(0x4DFFFFFF)   // white at 30% opacity
    val NoteDefault     = Color(0xFFE0E0E0)   // off-white, upcoming note
    val NoteStem        = Color(0xFFE0E0E0)   // same as head

    // Playhead
    val Playhead        = Color(0xFF00E676)   // Neon Green — must glow
    val PlayheadGlow    = Color(0x4000E676)   // Neon Green at 25% for glow halo

    // Scoring states (applied to note head fill)
    val HitPerfect      = Color(0xFF00E676)   // Neon Green
    val HitGood         = Color(0xFFFFD600)   // Amber/Yellow
    val HitMiss         = Color(0xFFFF1744)   // Vibrant Red
    val FalseTap        = Color(0xFFFF1744)   // same as Miss

    // Text
    val TextPrimary     = Color(0xFFFFFFFF)
    val TextInstruction = Color(0xFFE0E0E0)
    val TextCountIn     = Color(0xFF00E676)   // count-in digits rendered in accent

    // Overlay
    val SummaryOverlay  = Color(0xCC121212)   // 80% opacity black for summary modal
    val SummaryCard     = Color(0xFF1E1E1E)
}
