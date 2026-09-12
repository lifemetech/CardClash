package com.royalcardclash.game.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.royalcardclash.game.model.PlayerStats

@Composable
fun HomeScreen(
    stats: PlayerStats,
    onPlayClick: () -> Unit,
    onStatsClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onGetCoinsClick: (Activity?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF1E293B), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = "🪙", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stats.totalCoins}",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = { onGetCoinsClick(activity) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700))
                ) {
                    Text("+ Free Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROYAL DECK",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "C L A S H",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 6.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuButton(
                    text = "PLAY WAR DUEL",
                    icon = Icons.Default.PlayArrow,
                    gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                    onClick = onPlayClick
                )

                MenuButton(
                    text = "STATISTICS",
                    icon = Icons.Default.EmojiEvents,
                    gradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                    onClick = onStatsClick
                )

                MenuButton(
                    text = "HOW TO PLAY",
                    icon = Icons.Default.Info,
                    gradient = listOf(Color(0xFF10B981), Color(0xFF047857)),
                    onClick = onHowToPlayClick
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Settings", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAboutClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("About", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Wins", value = "${stats.gamesWon}")
                StatItem(label = "Win Rate", value = "${stats.winRate.toInt()}%")
                StatItem(label = "Best Streak", value = "${stats.bestStreak}")
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Brush.horizontalGradient(gradient), RoundedCornerShape(27.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(27.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color(0xFFFFD700), fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
    }
}
