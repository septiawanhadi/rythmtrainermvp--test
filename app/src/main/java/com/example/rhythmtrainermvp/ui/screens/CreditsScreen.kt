package com.example.rhythmtrainermvp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhythmtrainermvp.ui.theme.RhythmColors

data class CreditEntry(
    val name: String,
    val role: String,
    val portfolioUrl: String
)

private val credits = listOf(
    CreditEntry("Name TBD", "Audio Engine / NDK", "https://example.com"),   // ⏳ PENDING
    CreditEntry("Name TBD", "UI/UX Designer", "https://example.com")        // ⏳ PENDING
)

@Composable
fun CreditsScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RhythmColors.AppBackground)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Credits",
            color = RhythmColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        credits.forEach { credit ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RhythmColors.SummaryCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = credit.name, color = RhythmColors.TextPrimary, fontSize = 16.sp)
                    Text(
                        text = credit.role,
                        color = RhythmColors.TextInstruction,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = credit.portfolioUrl,
                        color = RhythmColors.HitPerfect,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(credit.portfolioUrl)))
                        }
                    )
                }
            }
        }
    }
}
