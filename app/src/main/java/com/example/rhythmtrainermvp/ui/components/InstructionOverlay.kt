package com.example.rhythmtrainermvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rhythmtrainermvp.ui.theme.RhythmColors
import com.example.rhythmtrainermvp.ui.theme.RhythmTypography

/**
 * /ui/components/InstructionOverlay.kt
 * Menampilkan instruksi utama saat game dalam keadaan IDLE.
 */
@Composable
fun InstructionOverlay(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RhythmColors.SummaryOverlay),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = RhythmTypography.displayLarge,
            color = RhythmColors.TextInstruction
        )
    }
}
