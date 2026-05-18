package com.example.rhythmtrainermvp.ui.theme

// /ui/theme/Animations.kt
object RhythmAnimations {
    // Note scroll: notes move LEFT at constant px/ms rate derived from BPM
    // Formula: pixelsPerMs = canvasWidth * (1 - PlayheadXFraction) / ((16 beats) * msPerBeat)
    // at 80 BPM: msPerBeat = 750ms, total duration = 16 * 750 = 12000ms

    // Feedback text: fade in immediately on tap, fade out after 400ms
    val FeedbackFadeInMs   = 0L
    val FeedbackVisibleMs  = 300L
    val FeedbackFadeOutMs  = 400L

    // Count-in digit: each digit shows for 750ms
    val CountInDigitMs     = 750L   // matches one beat at 80 BPM

    // Summary overlay: slide up from bottom over 300ms
    val SummarySlideMs     = 300L

    // Splash screen: minimum display 1500ms (while Oboe library loads)
    val SplashMinMs        = 1500L
}
