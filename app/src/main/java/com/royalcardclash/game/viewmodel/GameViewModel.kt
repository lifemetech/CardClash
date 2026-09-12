package com.royalcardclash.game.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.royalcardclash.game.ads.AdManager
import com.royalcardclash.game.audio.SoundManager
import com.royalcardclash.game.data.PreferencesManager
import com.royalcardclash.game.engine.GameEngine
import com.royalcardclash.game.model.BattleResult
import com.royalcardclash.game.model.GamePhase
import com.royalcardclash.game.model.PlayerStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    val soundManager = SoundManager(application)
    val adManager = AdManager(application)
    val engine = GameEngine()

    val gameState = engine.gameState
    val playerStats: StateFlow<PlayerStats> = prefs.playerStats.stateIn(
        viewModelScope, SharingStarted.Lazily, PlayerStats()
    )

    val isSoundEnabled = prefs.isSoundEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val isVibrationEnabled = prefs.isVibrationEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)

    private var isBattleAnimating = false

    init {
        viewModelScope.launch {
            combine(isSoundEnabled, isVibrationEnabled) { sound, vibration ->
                Pair(sound, vibration)
            }.collect { (sound, vibration) ->
                soundManager.updateSettings(sound, vibration)
            }
        }
    }

    fun startNewGame() {
        soundManager.playClick()
        engine.startNewGame()
    }

    fun executeBattleRound() {
        if (isBattleAnimating || gameState.value.phase == GamePhase.GAME_OVER) return
        isBattleAnimating = true

        soundManager.playCardFlip()

        viewModelScope.launch {
            val (result, _) = engine.playBattleRound()

            when (result) {
                BattleResult.PLAYER_WIN -> soundManager.playWin()
                BattleResult.AI_WIN -> soundManager.playLose()
                BattleResult.WAR_TIE -> soundManager.playClick()
            }

            delay(600)
            isBattleAnimating = false

            if (engine.gameState.value.phase == GamePhase.GAME_OVER) {
                onGameOver()
            }
        }
    }

    fun setPauseState(isPaused: Boolean) {
        soundManager.playClick()
        engine.togglePause(isPaused)
    }

    private fun onGameOver() {
        val state = engine.gameState.value
        val isWin = state.playerScore > state.aiScore
        val rewardCoins = if (isWin) 50 else 10

        if (isWin) soundManager.playWin() else soundManager.playLose()

        viewModelScope.launch {
            prefs.recordMatchResult(isWin, state.playerScore, rewardCoins)
        }
    }

    fun claimRewardedAdCoins(activity: Activity?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (activity != null) {
            adManager.showRewardedAd(activity, onRewardEarned = {
                viewModelScope.launch {
                    prefs.addCoins(100)
                    soundManager.playCoinReward()
                    onSuccess()
                }
            }, onFailure = onFailure)
        } else {
            viewModelScope.launch {
                prefs.addCoins(100)
                soundManager.playCoinReward()
                onSuccess()
            }
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch { prefs.setSoundEnabled(enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch { prefs.setVibrationEnabled(enabled) }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
