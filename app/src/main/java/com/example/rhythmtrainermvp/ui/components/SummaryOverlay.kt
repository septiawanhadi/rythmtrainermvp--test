package com.example.rhythmtrainermvp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rhythmtrainermvp.data.SessionScore
import com.example.rhythmtrainermvp.ui.theme.RhythmColors
import com.example.rhythmtrainermvp.ui.theme.RhythmTypography

@Composable
fun SummaryOverlay(
    show: Boolean,
    score: SessionScore,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(RhythmColors.SummaryOverlay),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RhythmColors.SummaryCard)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Results",
                        style = RhythmTypography.headlineLarge,
                        color = RhythmColors.TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    ResultRow("Perfect", score.perfect.toString(), RhythmColors.HitPerfect)
                    ResultRow("Good", score.good.toString(), RhythmColors.HitGood)
                    ResultRow("Miss", score.miss.toString(), RhythmColors.HitMiss)
                    ResultRow("False Taps", score.falseTaps.toString(), RhythmColors.HitMiss)
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = RhythmColors.StaffLine)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Accuracy", style = RhythmTypography.bodyLarge)
                        Text(
                            text = "${"%.1f".format(score.accuracyPercent)}%",
                            style = RhythmTypography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RhythmColors.HitPerfect),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "RETRY", 
                            style = RhythmTypography.labelLarge, 
                            color = RhythmColors.AppBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = RhythmTypography.bodyLarge)
        Text(value, style = RhythmTypography.bodyLarge, color = color, fontWeight = FontWeight.Bold)
    }
}
