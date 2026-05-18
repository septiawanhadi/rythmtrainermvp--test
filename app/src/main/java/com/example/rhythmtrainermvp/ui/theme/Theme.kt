package com.example.rhythmtrainermvp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Rhythm Trainer MVP Level 1 Theme
 * Strictly following PRD Section 1.
 */
private val DarkColorScheme = darkColorScheme(
    primary = RhythmColors.Playhead,
    secondary = RhythmColors.HitPerfect,
    tertiary = RhythmColors.HitGood,
    background = RhythmColors.AppBackground,
    surface = RhythmColors.SummaryCard,
    onPrimary = RhythmColors.AppBackground,
    onSecondary = RhythmColors.AppBackground,
    onTertiary = RhythmColors.AppBackground,
    onBackground = RhythmColors.TextPrimary,
    onSurface = RhythmColors.TextPrimary
)

@Composable
fun RhythmTrainerMVPTheme(
    content: @Composable () -> Unit
) {
    // PRD Level 1 is strictly Dark Mode (Section 1.1)
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = RhythmTypography,
        content = content
    )
}
