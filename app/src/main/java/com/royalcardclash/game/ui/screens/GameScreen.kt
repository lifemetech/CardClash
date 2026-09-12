package com.royalcardclash.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.royalcardclash.game.model.GamePhase
import com.royalcardclash.game.model.PlayingCardModel
import com.royalcardclash.game.model.Rank
import com.royalcardclash.game.model.Suit
import com.royalcardclash.game.ui.components.PlayingCardView
import com.royalcardclash.game.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToHome: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF064E3B), Color(0xFF022C22))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF065F46), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreText(label = "AI Cards", score = gameState.aiScore, color = Color(0xFFFCA5A5))
                Text(
                    text = "Round ${gameState.roundsPlayed}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                ScoreText(label = "Your Cards", score = gameState.playerScore, color = Color(0xFF6EE7B7))
                IconButton(onClick = { viewModel.setPauseState(true) }) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.White)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AI DECK (${gameState.aiDeck.size})", color = Color.LightGray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                PlayingCardView(
                    card = PlayingCardModel(suit = Suit.SPADES, rank = Rank.ACE, isFaceUp = false),
                    width = 60.dp,
                    height = 86.dp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFF047857).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = gameState.statusMessage,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        gameState.currentBattlePlayerCard?.let { card ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("YOU", color = Color.LightGray, fontSize = 10.sp)
                                PlayingCardView(card = card, width = 68.dp, height = 98.dp)
                            }
                        }

                        if (gameState.currentBattlePlayerCard != null && gameState.currentBattleAiCard != null) {
                            Text(
                                text = if (gameState.isWarActive) "⚔️ WAR!" else "VS",
                                color = Color(0xFFFFD700),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        gameState.currentBattleAiCard?.let { card ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("AI", color = Color.LightGray, fontSize = 10.sp)
                                PlayingCardView(card = card, width = 68.dp, height = 98.dp)
                            }
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { viewModel.executeBattleRound() },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gameState.isWarActive) Color(0xFFDC2626) else Color(0xFFF59E0B)
                    ),
                    shape = RoundedCornerShape(25.dp),
                    enabled = gameState.phase != GamePhase.GAME_OVER
                ) {
                    Text(
                        text = if (gameState.isWarActive) "⚔️ FIGHT WAR!" else "DRAW BATTLE CARD",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("YOUR DECK (${gameState.playerScore} total)", color = Color.LightGray, fontSize = 11.sp)
            }
        }

        AnimatedVisibility(
            visible = gameState.isPaused,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("GAME PAUSED", color = Color(0xFFFFD700), fontSize = 22.sp, fontWeight = FontWeight.Bold)

                        Button(
                            onClick = { viewModel.setPauseState(false) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("RESUME", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.setPauseState(false)
                                onBackToHome()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151))
                        ) {
                            Text("QUIT TO MAIN MENU", color = Color.White)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = gameState.phase == GamePhase.GAME_OVER,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isWin = gameState.playerScore > gameState.aiScore
                        Text(
                            text = if (isWin) "VICTORY!" else "DEFEAT",
                            color = if (isWin) Color(0xFFFFD700) else Color(0xFFEF4444),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Cards Captured: ${gameState.playerScore} vs ${gameState.aiScore}",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            text = if (isWin) "Reward: +50 Coins 🪙" else "Reward: +10 Coins 🪙",
                            color = Color(0xFFFFD700),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.startNewGame() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("PLAY AGAIN", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onBackToHome,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151))
                        ) {
                            Text("MAIN MENU", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreText(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.LightGray, fontSize = 11.sp)
        Text(text = "$score", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
