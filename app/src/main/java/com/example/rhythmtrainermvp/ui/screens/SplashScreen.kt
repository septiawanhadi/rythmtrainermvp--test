package com.example.rhythmtrainermvp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

/**
 * Phase 3: Splash screen with co-brand layout.
 * Purpose: Load the NDK library, display co-branding, then navigate to GameScreen.
 * Current: Minimal placeholder — shows app name.
 */
@Composable
fun SplashScreen(
    onNavigateToGame: () -> Unit = {}
) {
    // TODO Phase 1/3: Wire LaunchedEffect for RhythmBridge.nativeInit() + min display time
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RhythmColors.AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Placeholder for co-brand logos
            Text(
                text = "Rhythm Trainer",
                color = Color.White,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tap to Start",
                color = RhythmColors.TextInstruction,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
