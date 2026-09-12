package com.royalcardclash.game

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.royalcardclash.game.ui.screens.AboutScreen
import com.royalcardclash.game.ui.screens.GameScreen
import com.royalcardclash.game.ui.screens.HomeScreen
import com.royalcardclash.game.ui.screens.HowToPlayScreen
import com.royalcardclash.game.ui.screens.SettingsScreen
import com.royalcardclash.game.ui.screens.StatsScreen
import com.royalcardclash.game.ui.theme.RoyalCardClashTheme
import com.royalcardclash.game.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoyalCardClashTheme {
                val navController = rememberNavController()
                val viewModel: GameViewModel = viewModel()

                val playerStats by viewModel.playerStats.collectAsState()
                val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
                val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            stats = playerStats,
                            onPlayClick = {
                                viewModel.startNewGame()
                                navController.navigate("game")
                            },
                            onStatsClick = { navController.navigate("stats") },
                            onHowToPlayClick = { navController.navigate("how_to_play") },
                            onSettingsClick = { navController.navigate("settings") },
                            onAboutClick = { navController.navigate("about") },
                            onGetCoinsClick = { activity ->
                                viewModel.claimRewardedAdCoins(
                                    activity = activity,
                                    onSuccess = {
                                        Toast.makeText(this@MainActivity, "Earned 100 Gold Coins!", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(this@MainActivity, "Ad loading... try again shortly", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    }

                    composable("game") {
                        GameScreen(
                            viewModel = viewModel,
                            onBackToHome = { navController.popBackStack("home", false) }
                        )
                    }

                    composable("stats") {
                        StatsScreen(
                            stats = playerStats,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("how_to_play") {
                        HowToPlayScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("about") {
                        AboutScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            isSoundEnabled = isSoundEnabled,
                            isVibrationEnabled = isVibrationEnabled,
                            onToggleSound = { viewModel.toggleSound(it) },
                            onToggleVibration = { viewModel.toggleVibration(it) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
