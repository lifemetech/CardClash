package com.royalcardclash.game.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.royalcardclash.game.model.PlayerStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "royal_card_clash_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_GAMES_PLAYED = intPreferencesKey("games_played")
        private val KEY_GAMES_WON = intPreferencesKey("games_won")
        private val KEY_GAMES_LOST = intPreferencesKey("games_lost")
        private val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
        private val KEY_BEST_STREAK = intPreferencesKey("best_streak")
        private val KEY_BEST_SCORE = intPreferencesKey("best_score")
        private val KEY_TOTAL_COINS = intPreferencesKey("total_coins")

        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val playerStats: Flow<PlayerStats> = context.dataStore.data.map { prefs ->
        PlayerStats(
            gamesPlayed = prefs[KEY_GAMES_PLAYED] ?: 0,
            gamesWon = prefs[KEY_GAMES_WON] ?: 0,
            gamesLost = prefs[KEY_GAMES_LOST] ?: 0,
            currentStreak = prefs[KEY_CURRENT_STREAK] ?: 0,
            bestStreak = prefs[KEY_BEST_STREAK] ?: 0,
            bestScore = prefs[KEY_BEST_SCORE] ?: 0,
            totalCoins = prefs[KEY_TOTAL_COINS] ?: 250
        )
    }

    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND_ENABLED] ?: true }
    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_VIBRATION_ENABLED] ?: true }

    suspend fun recordMatchResult(isWin: Boolean, score: Int, rewardCoins: Int) {
        context.dataStore.edit { prefs ->
            val played = (prefs[KEY_GAMES_PLAYED] ?: 0) + 1
            prefs[KEY_GAMES_PLAYED] = played

            val currentStreak = if (isWin) (prefs[KEY_CURRENT_STREAK] ?: 0) + 1 else 0
            prefs[KEY_CURRENT_STREAK] = currentStreak

            val bestStreak = prefs[KEY_BEST_STREAK] ?: 0
            if (currentStreak > bestStreak) {
                prefs[KEY_BEST_STREAK] = currentStreak
            }

            if (isWin) {
                prefs[KEY_GAMES_WON] = (prefs[KEY_GAMES_WON] ?: 0) + 1
            } else {
                prefs[KEY_GAMES_LOST] = (prefs[KEY_GAMES_LOST] ?: 0) + 1
            }

            val bestScore = prefs[KEY_BEST_SCORE] ?: 0
            if (score > bestScore) {
                prefs[KEY_BEST_SCORE] = score
            }

            val currentCoins = prefs[KEY_TOTAL_COINS] ?: 250
            prefs[KEY_TOTAL_COINS] = currentCoins + rewardCoins
        }
    }

    suspend fun addCoins(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_TOTAL_COINS] ?: 250
            prefs[KEY_TOTAL_COINS] = current + amount
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION_ENABLED] = enabled }
    }
}
