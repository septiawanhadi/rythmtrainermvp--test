package com.example.rhythmtrainermvp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.data.SessionScore
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

/**
 * Phase 7: Full-screen summary overlay.
 * Slides up from bottom, shows score breakdown + Retry button.
 */
@Composable
fun SummaryOverlay(
    score: SessionScore,
    onRetry: () -> Unit = {}
) {
    // TODO Phase 7: Animated slide-up entrance (300ms FastOutSlowIn)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RhythmColors.SummaryOverlay),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RhythmColors.SummaryCard)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Results",
                    color = RhythmColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))

                ScoreRow("Perfect", score.perfect, RhythmColors.HitPerfect)
                ScoreRow("Good", score.good, RhythmColors.HitGood)
                ScoreRow("Miss", score.miss, RhythmColors.HitMiss)
                ScoreRow("False Taps", score.falseTaps, RhythmColors.HitMiss)

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Accuracy: ${"%.1f".format(score.accuracyPercent)}%",
                    color = RhythmColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RhythmColors.HitPerfect,
                        contentColor = RhythmColors.AppBackground
                    )
                ) {
                    Text("RETRY", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ScoreRow(label: String, value: Int, valueColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = RhythmColors.TextInstruction, fontSize = 16.sp)
        Text(
            text = value.toString(),
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
