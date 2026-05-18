package com.example.rhythmtrainermvp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// /ui/theme/Type.kt
// Menggunakan SansSerif sebagai fallback sistem sebelum file font Inter ditambahkan ke res/font/

val RhythmTypography = Typography(
    // "Tap Anywhere to Start" instruction
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.5.sp,
        color = RhythmColors.TextInstruction
    ),
    // Count-In digits (1, 2, 3, 4)
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 72.sp,
        color = RhythmColors.TextCountIn
    ),
    // Scoring feedback popup text (Perfect / Good / Miss)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    // Summary modal labels
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = RhythmColors.TextPrimary
    ),
    // Retry button
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )
)
