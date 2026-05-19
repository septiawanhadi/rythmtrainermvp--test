package com.example.rhythmtrainermvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.data.NoteResult
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

/**
 * Phase 6: Shows "Perfect!", "Good!", "Miss!", or "False!" centered at top of screen.
 * Appears instantly on tap result, visible 300ms, fades out over 400ms.
 */
@Composable
fun FeedbackText(
    lastResult: NoteResult? = null,
    modifier: Modifier = Modifier
) {
    if (lastResult == null) return

    // TODO Phase 6: Animated fade in/out (0ms in, 300ms visible, 400ms out)
    val (text, color) = when (lastResult) {
        NoteResult.PERFECT    -> "Perfect!" to RhythmColors.HitPerfect
        NoteResult.GOOD       -> "Good!" to RhythmColors.HitGood
        NoteResult.MISS       -> "Miss!" to RhythmColors.HitMiss
        NoteResult.FALSE_TAP  -> "False!" to RhythmColors.FalseTap
        else                  -> return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xB3121212), // semi-transparent pill bg
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = text,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
