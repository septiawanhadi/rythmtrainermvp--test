package com.example.rhythmtrainermvp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.rhythmtrainermvp.data.NoteEvent
import com.example.rhythmtrainermvp.data.NoteType
import com.example.rhythmtrainermvp.ui.theme.RhythmColors
import com.example.rhythmtrainermvp.ui.theme.RhythmDimensions

/**
 * Phase 3–5: Renders the full music score:
 *   1. 5 staff lines
 *   2. 16 NoteEvents (notes + rests)
 *   3. Moving playhead
 *
 * Current: Draws 5 static staff lines only. Notes and playhead added in Phase 4–5.
 */
@Composable
fun NotationCanvas(
    noteEvents: List<NoteEvent> = emptyList(),
    playheadFraction: Float = 0f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val staffSpacingPx = RhythmDimensions.StaffLineSpacingDp.toPx()
        val staffTopY = size.height * RhythmDimensions.StaffTopPaddingFraction

        // 1. Draw 5 staff lines (static, drawn every frame)
        for (i in 0 until RhythmDimensions.StaffLineCount) {
            val y = staffTopY + (i * staffSpacingPx)
            drawLine(
                color = RhythmColors.StaffLine,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // TODO Phase 4: Draw all 16 NoteEvents
        // TODO Phase 4: drawQuarterNote() + drawQuarterRest()
        // TODO Phase 5: drawPlayhead(playheadFraction) with glow
    }
}
