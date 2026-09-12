package com.royalcardclash.game.engine

import com.royalcardclash.game.model.BattleResult
import com.royalcardclash.game.model.GamePhase
import com.royalcardclash.game.model.PlayingCardModel
import com.royalcardclash.game.model.Rank
import com.royalcardclash.game.model.Suit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GameState(
    val playerDeck: List<PlayingCardModel> = emptyList(),
    val aiDeck: List<PlayingCardModel> = emptyList(),
    val playerWonCards: List<PlayingCardModel> = emptyList(),
    val aiWonCards: List<PlayingCardModel> = emptyList(),
    val currentBattlePlayerCard: PlayingCardModel? = null,
    val currentBattleAiCard: PlayingCardModel? = null,
    val warPile: List<PlayingCardModel> = emptyList(),
    val playerScore: Int = 26,
    val aiScore: Int = 26,
    val roundsPlayed: Int = 0,
    val phase: GamePhase = GamePhase.IDLE,
    val lastBattleResult: BattleResult? = null,
    val statusMessage: String = "Tap 'BATTLE' to start the duel!",
    val isWarActive: Boolean = false,
    val isPaused: Boolean = false
)

class GameEngine {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun startNewGame() {
        val fullDeck = createFairShuffledDeck()
        val pDeck = fullDeck.take(26)
        val aDeck = fullDeck.drop(26)

        _gameState.update {
            GameState(
                playerDeck = pDeck,
                aiDeck = aDeck,
                playerWonCards = emptyList(),
                aiWonCards = emptyList(),
                currentBattlePlayerCard = null,
                currentBattleAiCard = null,
                warPile = emptyList(),
                playerScore = 26,
                aiScore = 26,
                roundsPlayed = 0,
                phase = GamePhase.READY_TO_BATTLE,
                lastBattleResult = null,
                statusMessage = "Match started! Tap 'BATTLE' to draw cards.",
                isWarActive = false,
                isPaused = false
            )
        }
    }

    private fun createFairShuffledDeck(): List<PlayingCardModel> {
        val cards = mutableListOf<PlayingCardModel>()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(PlayingCardModel(suit = suit, rank = rank))
            }
        }
        return cards.shuffled()
    }

    fun playBattleRound(): Pair<BattleResult, Int> {
        val state = _gameState.value
        if (state.phase == GamePhase.BATTLE_ANIMATING || state.phase == GamePhase.GAME_OVER) {
            return Pair(BattleResult.WAR_TIE, 0)
        }

        // Check card replenishment from won piles if active draw pile runs low
        var pDeck = state.playerDeck.toMutableList()
        var aDeck = state.aiDeck.toMutableList()
        var pWon = state.playerWonCards.toMutableList()
        var aWon = state.aiWonCards.toMutableList()

        if (pDeck.isEmpty() && pWon.isNotEmpty()) {
            pDeck.addAll(pWon.shuffled())
            pWon.clear()
        }
        if (aDeck.isEmpty() && aWon.isNotEmpty()) {
            aDeck.addAll(aWon.shuffled())
            aWon.clear()
        }

        // Win/loss check before drawing
        if (pDeck.isEmpty()) {
            triggerGameOver(playerWon = false, message = "AI captured all cards!")
            return Pair(BattleResult.AI_WIN, 0)
        }
        if (aDeck.isEmpty()) {
            triggerGameOver(playerWon = true, message = "You captured all cards!")
            return Pair(BattleResult.PLAYER_WIN, 0)
        }

        val playerCard = pDeck.removeAt(0).copy(isFaceUp = true)
        val aiCard = aDeck.removeAt(0).copy(isFaceUp = true)

        val currentWarPile = state.warPile.toMutableList()
        currentWarPile.add(playerCard)
        currentWarPile.add(aiCard)

        val result: BattleResult
        val cardsWonCount = currentWarPile.size

        if (playerCard.rank.value > aiCard.rank.value) {
            result = BattleResult.PLAYER_WIN
            pWon.addAll(currentWarPile)
            currentWarPile.clear()
        } else if (aiCard.rank.value > playerCard.rank.value) {
            result = BattleResult.AI_WIN
            aWon.addAll(currentWarPile)
            currentWarPile.clear()
        } else {
            // WAR TIE!
            result = BattleResult.WAR_TIE
            // Draw up to 3 cards face down into War pile
            val playerWarDraw = minOf(3, pDeck.size)
            val aiWarDraw = minOf(3, aDeck.size)

            for (i in 0 until playerWarDraw) {
                currentWarPile.add(pDeck.removeAt(0).copy(isFaceUp = false))
            }
            for (i in 0 until aiWarDraw) {
                currentWarPile.add(aDeck.removeAt(0).copy(isFaceUp = false))
            }
        }

        val totalPlayerCards = pDeck.size + pWon.size
        val totalAiCards = aDeck.size + aWon.size

        val isGameOver = totalPlayerCards == 0 || totalAiCards == 0
        val nextPhase = when {
            isGameOver -> GamePhase.GAME_OVER
            result == BattleResult.WAR_TIE -> GamePhase.WAR_DECLARED
            else -> GamePhase.READY_TO_BATTLE
        }

        val msg = when (result) {
            BattleResult.PLAYER_WIN -> if (state.isWarActive) "YOU WON THE WAR! (+$cardsWonCount cards)" else "Round Won! (+2 cards)"
            BattleResult.AI_WIN -> if (state.isWarActive) "AI WON THE WAR! (+$cardsWonCount cards)" else "AI Won Round! (+2 cards)"
            BattleResult.WAR_TIE -> "⚔️ WAR DECLARED! Equal Ranks! Draw 3 cards face down!"
        }

        _gameState.update {
            it.copy(
                playerDeck = pDeck,
                aiDeck = aDeck,
                playerWonCards = pWon,
                aiWonCards = aWon,
                currentBattlePlayerCard = playerCard,
                currentBattleAiCard = aiCard,
                warPile = currentWarPile,
                playerScore = totalPlayerCards,
                aiScore = totalAiCards,
                roundsPlayed = state.roundsPlayed + 1,
                phase = nextPhase,
                lastBattleResult = result,
                statusMessage = msg,
                isWarActive = (result == BattleResult.WAR_TIE || (state.isWarActive && currentWarPile.isNotEmpty()))
            )
        }

        if (isGameOver) {
            triggerGameOver(playerWon = totalPlayerCards > 0, message = if (totalPlayerCards > 0) "VICTORY! You captured all 52 cards!" else "DEFEAT! AI captured all 52 cards!")
        }

        return Pair(result, cardsWonCount)
    }

    fun togglePause(isPaused: Boolean) {
        _gameState.update { it.copy(isPaused = isPaused) }
    }

    private fun triggerGameOver(playerWon: Boolean, message: String) {
        _gameState.update {
            it.copy(
                phase = GamePhase.GAME_OVER,
                statusMessage = message
            )
        }
    }
}
