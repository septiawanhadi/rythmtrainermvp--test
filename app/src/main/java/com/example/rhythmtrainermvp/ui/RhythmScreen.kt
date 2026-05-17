package com.example.rhythmtrainermvp.ui

import android.annotation.SuppressLint
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.rhythmtrainermvp.viewmodel.RhythmUiState
import com.example.rhythmtrainermvp.viewmodel.RhythmViewModel

@SuppressLint("NewApi", "MissingPermission")
@Composable
fun RhythmScreen(viewModel: RhythmViewModel, onTogglePlay: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current // Check if we are in Preview mode

    RhythmScreenContent(
        uiState = uiState,
        onTogglePlay = onTogglePlay,
        onUserTap = { tapTimeNs ->
            // Haptic Feedback - Only execute if NOT in preview mode
            if (!isPreview) {
                val vibrator = context.getSystemService(Vibrator::class.java)
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                }
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
        
        // TOP 40%: Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(Color(0xFFE0E0E0))
        ) {
            // Z-Index for Metadata (Score/Feedback)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                Text(
                    text = "Score: ${uiState.score}", 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Black
                )
                Text(
                    text = "Highest Score: ${uiState.highestScore}", 
                    fontSize = 16.sp, 
                    color = Color.DarkGray
                )
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
                val staffY = size.height / 2
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, staffY),
                    end = Offset(size.width, staffY),
                    strokeWidth = 4f
                )
            }
        }

        // BOTTOM 60%: Tap Zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onUserTap(System.nanoTime())
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TAP ZONE", 
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
