package com.example.rhythmtrainermvp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rhythmtrainermvp.data.NoteResult
import com.example.rhythmtrainermvp.ui.theme.RhythmAnimations
import com.example.rhythmtrainermvp.ui.theme.RhythmColors
import com.example.rhythmtrainermvp.ui.theme.RhythmTypography
import kotlinx.coroutines.delay

@Composable
fun FeedbackText(
    result: NoteResult,
    trigger: Long, // Use timestamp to trigger animation on same result
    modifier: Modifier = Modifier
) {
    if (result == NoteResult.PENDING) return

    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        alpha.snapTo(1f)
        delay(RhythmAnimations.FeedbackVisibleMs)
        alpha.animateTo(0f, animationSpec = tween(RhythmAnimations.FeedbackFadeOutMs.toInt()))
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val (text, color) = when (result) {
            NoteResult.PERFECT -> "PERFECT!" to RhythmColors.HitPerfect
            NoteResult.GOOD -> "GOOD" to RhythmColors.HitGood
            NoteResult.MISS -> "MISS" to RhythmColors.HitMiss
            NoteResult.FALSE_TAP -> "FALSE!" to RhythmColors.FalseTap
            else -> "" to Color.Transparent
        }

        Text(
            text = text,
            style = RhythmTypography.headlineLarge,
            color = color
        )
    }
}
