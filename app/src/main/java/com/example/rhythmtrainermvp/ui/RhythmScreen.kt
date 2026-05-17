package com.example.rhythmtrainermvp.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.rhythmtrainermvp.viewmodel.RhythmUiState
import com.example.rhythmtrainermvp.viewmodel.RhythmViewModel

@Composable
fun RhythmScreen(viewModel: RhythmViewModel, onTogglePlay: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    RhythmScreenContent(
        uiState = uiState,
        onTogglePlay = onTogglePlay,
        onUserTap = { tapTimeNs ->
            // Haptic Feedback
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            viewModel.onUserTap(tapTimeNs)
        }
    )
}

@Composable
fun RhythmScreenContent(
    uiState: RhythmUiState,
    onTogglePlay: () -> Unit,
    onUserTap: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        
        // TOP 40%: Canvas Rendering Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(Color(0xFFE0E0E0))
        ) {
            // Z-Index for Text (Metadata) MUST be highest
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f) // Ensure it is on top
            ) {
                Text("Score: ${uiState.score}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Highest Score: ${uiState.highestScore}", fontSize = 16.sp, color = Color.DarkGray)
                if (uiState.lastFeedback.isNotEmpty()) {
                    Text(
                        text = uiState.lastFeedback, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold,
                        color = when(uiState.lastFeedback) {
                            "Perfect" -> Color(0xFF4CAF50)
                            "Good" -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                }
            }

            // Canvas for musical notation
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw simple staff lines (placeholder for MVP visual)
                val staffY = canvasHeight / 2
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, staffY),
                    end = Offset(canvasWidth, staffY),
                    strokeWidth = 4f
                )
            }
        }

        // BOTTOM 60%: Distraction-free "Tap Zone"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            val tapTimeNs = System.nanoTime()
                            onUserTap(tapTimeNs)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "TAP ZONE", 
                    fontSize = 24.sp, 
                    color = Color.LightGray, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onTogglePlay) {
                    Text(if (uiState.isPlaying) "Stop" else "Start 80 BPM")
                }
            }
        }
    }
}

// ==========================================
// PREVIEW UNTUK ANDROID STUDIO
// ==========================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RhythmScreenPreview() {
    MaterialTheme {
        RhythmScreenContent(
            uiState = RhythmUiState(
                isPlaying = false,
                score = 15,
                highestScore = 120,
                lastFeedback = "Perfect"
            ),
            onTogglePlay = {},
            onUserTap = {}
        )
    }
}
