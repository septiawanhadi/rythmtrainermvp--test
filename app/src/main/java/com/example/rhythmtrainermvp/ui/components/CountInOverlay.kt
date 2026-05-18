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

@Composable
fun CountInOverlay(
    count: Int?,
    modifier: Modifier = Modifier
) {
    if (count == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RhythmColors.SummaryOverlay),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = RhythmTypography.displayMedium
        )
    }
}
