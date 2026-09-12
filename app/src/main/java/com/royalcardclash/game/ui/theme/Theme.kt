package com.royalcardclash.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkGold = Color(0xFFFFD700)
val BrightGold = Color(0xFFFFE57F)
val DarkBackground = Color(0xFF0F172A)
val CardBackground = Color(0xFF1E293B)
val CrimsonRed = Color(0xFFDC2626)
val EmeraldGreen = Color(0xFF059669)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGold,
    secondary = BrightGold,
    tertiary = EmeraldGreen,
    background = DarkBackground,
    surface = CardBackground,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun RoyalCardClashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
