package com.example.rhythmtrainermvp.ui.theme

import androidx.compose.ui.unit.dp

object RhythmDimensions {
    // Screen split
    val DisplayZoneHeightFraction = 0.40f   // top 40% = NotationCanvas
    val TapZoneHeightFraction = 0.60f        // bottom 60% = Tap Zone

    // Canvas internals
    val StaffLineCount = 5
    val StaffLineSpacingDp = 10.dp
    val StaffTopPaddingFraction = 0.15f
    val NoteHeadRadiusXDp = 7.dp
    val NoteHeadRadiusYDp = 5.dp
    val NoteStemHeightDp = 35.dp
    val NoteStemWidthDp = 2.dp
    val PlayheadWidthDp = 3.dp
    val PlayheadFixedXFraction = 0.20f

    // Scoring feedback text
    val FeedbackTextTopOffsetDp = 24.dp

    // Summary modal
    val SummaryCardCornerDp = 16.dp
    val SummaryCardPaddingDp = 24.dp

    // Splash logo height
    val SplashLogoHeightDp = 80.dp
}
