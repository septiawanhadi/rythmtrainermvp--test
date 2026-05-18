package com.example.rhythmtrainermvp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.rhythmtrainermvp.data.NoteEvent
import com.example.rhythmtrainermvp.data.NoteResult
import com.example.rhythmtrainermvp.data.NoteType
import com.example.rhythmtrainermvp.ui.theme.RhythmColors
import com.example.rhythmtrainermvp.ui.theme.RhythmDimensions

@Composable
fun NotationCanvas(
    noteEvents: List<NoteEvent>,
    playheadFraction: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val spacing = RhythmDimensions.StaffLineSpacingDp.toPx()
        val topPadding = size.height * RhythmDimensions.StaffTopPaddingFraction
        val noteY = topPadding + 2 * spacing // 3rd staff line
        
        drawStaffLines(topPadding, spacing)
        
        noteEvents.forEach { note ->
            val cx = size.width * (note.index + 0.5f) / 16f
            when (note.type) {
                NoteType.QUARTER_NOTE -> drawQuarterNote(note, cx, noteY)
                NoteType.QUARTER_REST -> drawQuarterRest(note, cx, noteY)
            }
        }
        
        drawPlayhead(playheadFraction)
    }
}

private fun DrawScope.drawStaffLines(topPadding: Float, spacing: Float) {
    for (i in 0 until RhythmDimensions.StaffLineCount) {
        val y = topPadding + i * spacing
        drawLine(
            color = RhythmColors.StaffLine,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawQuarterNote(note: NoteEvent, cx: Float, cy: Float) {
    val color = note.result.toColor()
    val rx = RhythmDimensions.NoteHeadRadiusXDp.toPx()
    val ry = RhythmDimensions.NoteHeadRadiusYDp.toPx()
    val stemHeight = RhythmDimensions.NoteStemHeightDp.toPx()
    val stemWidth = RhythmDimensions.NoteStemWidthDp.toPx()

    // Oval head
    drawOval(
        color = color,
        topLeft = Offset(cx - rx, cy - ry),
        size = Size(rx * 2, ry * 2)
    )
    // Stem (upward)
    drawLine(
        color = color,
        start = Offset(cx + rx, cy),
        end = Offset(cx + rx, cy - stemHeight),
        strokeWidth = stemWidth
    )
}

private fun DrawScope.drawQuarterRest(note: NoteEvent, cx: Float, cy: Float) {
    val color = RhythmColors.NoteDefault
    val size = 15.dp.toPx()
    // Simple zigzag for rest
    drawLine(color, Offset(cx - size/3, cy - size/2), Offset(cx + size/3, cy - size/4), strokeWidth = 2.dp.toPx())
    drawLine(color, Offset(cx + size/3, cy - size/4), Offset(cx - size/3, cy + size/4), strokeWidth = 2.dp.toPx())
    drawLine(color, Offset(cx - size/3, cy + size/4), Offset(cx + size/3, cy + size/2), strokeWidth = 2.dp.toPx())
}

private fun DrawScope.drawPlayhead(fraction: Float) {
    val x = size.width * fraction
    val glowWidth = 8.dp.toPx()
    val coreWidth = RhythmDimensions.PlayheadWidthDp.toPx()
    
    // Glow
    drawLine(
        color = RhythmColors.PlayheadGlow,
        start = Offset(x, 0f),
        end = Offset(x, size.height),
        strokeWidth = glowWidth
    )
    // Sharp line
    drawLine(
        color = RhythmColors.Playhead,
        start = Offset(x, 0f),
        end = Offset(x, size.height),
        strokeWidth = coreWidth
    )
}

private fun NoteResult.toColor(): Color = when (this) {
    NoteResult.PENDING    -> RhythmColors.NoteDefault
    NoteResult.PERFECT    -> RhythmColors.HitPerfect
    NoteResult.GOOD       -> RhythmColors.HitGood
    NoteResult.MISS       -> RhythmColors.HitMiss
    NoteResult.FALSE_TAP  -> RhythmColors.FalseTap
}
