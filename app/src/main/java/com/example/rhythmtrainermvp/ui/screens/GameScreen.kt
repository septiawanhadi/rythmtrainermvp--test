package com.example.rhythmtrainermvp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rhythmtrainermvp.data.GameState
import com.example.rhythmtrainermvp.ui.components.*
import com.example.rhythmtrainermvp.ui.theme.*
import com.example.rhythmtrainermvp.viewmodel.GameViewModel

/**
 * /ui/screens/GameScreen.kt
 * Implementasi PRD Section 4.2
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val playheadFraction by viewModel.playheadFraction.collectAsState()
    val noteEvents by viewModel.noteEvents.collectAsState()
    val sessionScore by viewModel.sessionScore.collectAsState()
    val countInBeat by viewModel.countInBeat.collectAsState()
    val lastFeedback by viewModel.lastFeedback.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RhythmColors.AppBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // TOP 40% — Display Zone (PRD 4.2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(RhythmDimensions.DisplayZoneHeightFraction)
            ) {
                // Layer 0: Notation Canvas
                NotationCanvas(
                    noteEvents = noteEvents,
                    playheadFraction = playheadFraction,
                    modifier = Modifier.fillMaxSize()
                )

                // Layer 1: Feedback Text
                lastFeedback?.let { (result, timestamp) ->
                    FeedbackText(
                        result = result,
                        trigger = timestamp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = RhythmDimensions.FeedbackTextTopOffsetDp)
                    )
                }

                // Layer 2: Count-In Overlay
                CountInOverlay(
                    count = countInBeat,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // BOTTOM 60% — Tap Zone (PRD 4.4)
            TapZone(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onTap = { viewModel.onUserTap() }
            )
        }

        // Instruction Overlay
        if (gameState == GameState.IDLE) {
            InstructionOverlay(text = "Tap Anywhere to Start")
        }

        // SUMMARY Overlay (PRD 4.7)
        SummaryOverlay(
            show = gameState == GameState.SUMMARY,
            score = sessionScore,
            onRetry = { viewModel.onRetry() }
        )

        // Settings Icon
        IconButton(
            onClick = { /* Navigate to Credits */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Credits",
                tint = RhythmColors.TextPrimary
            )
        }
    }
}
