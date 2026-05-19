package com.example.rhythmtrainermvp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

/**
 * Phase 3–7: Main game screen.
 * Layout: 40/60 split — top Display Zone (NotationCanvas), bottom Tap Zone.
 * Current: Placeholder showing zone labels.
 */
@Composable
fun GameScreen(
    onTap: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // TOP 40% — Display Zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(RhythmDimensions.DisplayZoneHeightFraction)
                .background(RhythmColors.AppBackground),
            contentAlignment = Alignment.Center
        ) {
            // TODO Phase 3: NotationCanvas(...)
            // TODO Phase 3: FeedbackText(...)
            // TODO Phase 3: CountInOverlay(...)
            Text("Display Zone\n(NotationCanvas)", color = RhythmColors.StaffLine, fontSize = 14.sp)
        }

        // BOTTOM 60% — Tap Zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(RhythmColors.TapZoneBg),
            contentAlignment = Alignment.Center
        ) {
            // TODO Phase 3: TapZone(onTap)
            Text("Tap Zone", color = RhythmColors.StaffLine, fontSize = 14.sp)
        }
    }

    // TODO Phase 7: InstructionOverlay when IDLE
    // TODO Phase 7: SummaryOverlay when SUMMARY
}

private val RhythmDimensions = com.example.rhythmtrainermvp.ui.theme.RhythmDimensions
