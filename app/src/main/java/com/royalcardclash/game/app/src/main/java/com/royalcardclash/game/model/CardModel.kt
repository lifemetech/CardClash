package com.royalcardclash.game.model

import androidx.compose.ui.graphics.Color

enum class Suit(val symbol: String, val color: Color, val priority: Int) {
    SPADES("♠", Color(0xFF1E293B), 4),
    HEARTS("♥", Color(0xFFDC2626), 3),
    DIAMONDS("♦", Color(0xFFDC2626), 2),
    CLUBS("♣", Color(0xFF1E293B), 1)
}

enum class Rank(val value: Int, val display: String) {
    TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5, "5"),
    SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"), NINE(9, "9"),
    TEN(10, "10"), JACK(11, "J"), QUEEN(12, "Q"), KING(13, "K"), ACE(14, "A")
}

data class PlayingCardModel(
    val id: String = java.util.UUID.randomUUID().toString(),
    val suit: Suit,
    val rank: Rank,
    val isFaceUp: Boolean = false,
    val isSelected: Boolean = false
) {
    val title: String get() = "${rank.display}${suit.symbol}"
}

enum class GamePhase {
    IDLE,
    DEALING,
    READY_TO_BATTLE,
    BATTLE_ANIMATING,
    WAR_DECLARED,
    GAME_OVER
}

enum class BattleResult {
    PLAYER_WIN,
    AI_WIN,
    WAR_TIE
}

data class PlayerStats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val gamesLost: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val bestScore: Int = 0,
    val totalCoins: Int = 250
) {
    val winRate: Float
        get() = if (gamesPlayed > 0) (gamesWon.toFloat() / gamesPlayed.toFloat()) * 100f else 0f
}
