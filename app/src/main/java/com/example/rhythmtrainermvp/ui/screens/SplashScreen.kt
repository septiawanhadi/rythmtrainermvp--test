package com.example.rhythmtrainermvp.ui.screens

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.RhythmBridge
import com.example.rhythmtrainermvp.ui.theme.RhythmAnimations
import com.example.rhythmtrainermvp.ui.theme.RhythmColors
import com.example.rhythmtrainermvp.ui.theme.RhythmDimensions
import kotlinx.coroutines.delay

/**
 * SplashScreen.kt
 * PRD Section 4.1: Load NDK library, display co-branding.
 */
@Composable
fun SplashScreen(onLoadComplete: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Init Engine
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toInt() ?: 48000
        val framesPerBurst = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toInt() ?: 128
        
        RhythmBridge.nativeInit(sampleRate, framesPerBurst)
        
        // Min display time
        delay(RhythmAnimations.SplashMinMs)
        onLoadComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RhythmColors.AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Placeholder for logo_developer
                Box(modifier = Modifier.size(RhythmDimensions.SplashLogoHeightDp).background(Color.Gray))
                
                // Separator
                Box(modifier = Modifier.width(1.dp).height(RhythmDimensions.SplashLogoHeightDp).background(Color(0x55FFFFFF)))
                
                // Placeholder for logo_client
                Box(modifier = Modifier.size(RhythmDimensions.SplashLogoHeightDp).background(Color.LightGray))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Developed in partnership with",
                color = RhythmColors.TextInstruction,
                fontSize = 12.sp
            )
        }
    }
}
