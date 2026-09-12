package com.royalcardclash.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.royalcardclash.game.model.PlayerStats

@Composable
fun StatsScreen(
    stats: PlayerStats,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Player Statistics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(title = "Games Played", value = "${stats.gamesPlayed}")
                StatCard(title = "Games Won", value = "${stats.gamesWon}")
                StatCard(title = "Games Lost", value = "${stats.gamesLost}")
                StatCard(title = "Win Rate", value = "${stats.winRate.toInt()}%")
                StatCard(title = "Best Cards Captured", value = "${stats.bestScore}")
                StatCard(title = "Current Win Streak", value = "${stats.currentStreak}")
                StatCard(title = "Best Win Streak", value = "${stats.bestStreak}")
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = Color.LightGray, fontSize = 15.sp)
            Text(text = value, color = Color(0xFFFFD700), fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}
