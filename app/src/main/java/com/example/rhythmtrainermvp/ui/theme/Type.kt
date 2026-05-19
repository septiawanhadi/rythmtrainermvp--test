package com.example.rhythmtrainermvp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Font: Inter (Google Fonts — downloadable font in res/font/)
 * Fallback: sans-serif system until Inter font files are added.
 */
private val InterFamily = FontFamily.SansSerif // TODO: Replace after adding Inter .ttf to res/font/

val RhythmTypography = Typography(
    // "Tap Anywhere to Start" instruction
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.5.sp,
        color = RhythmColors.TextInstruction
    ),
    // Count-In digits (1, 2, 3, 4)
    displayMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 72.sp,
        color = RhythmColors.TextCountIn
    ),
    // Scoring feedback popup text (Perfect / Good / Miss)
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    // Summary modal labels
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = RhythmColors.TextPrimary
    ),
    // Retry button
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )
)
