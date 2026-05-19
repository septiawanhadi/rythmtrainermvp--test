package com.example.rhythmtrainermvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

/**
 * Phase 5: Displays count-in digits 1-2-3-4.
 * Each digit visible for exactly 750ms (one beat at 80 BPM).
 */
@Composable
fun CountInOverlay(
    digit: Int? = null,
    modifier: Modifier = Modifier
) {
    if (digit == null) return

    // TODO Phase 5: Animated entrance/exit (scale + fade)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x60121212)), // semi-transparent scrim
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.toString(),
            color = RhythmColors.TextCountIn,
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
