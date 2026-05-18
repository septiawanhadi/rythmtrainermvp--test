package com.example.rhythmtrainermvp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

/**
 * /ui/components/TapZone.kt
 * A full-surface invisible tap catcher as per PRD Section 4.4.
 * ⛔ RULE: No visible UI elements inside TapZone.
 */
@Composable
fun TapZone(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(RhythmColors.TapZoneBg)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() }
                )
            }
    )
}
