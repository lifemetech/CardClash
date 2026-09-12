package com.royalcardclash.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var isSoundEnabled = true
    private var isVibrationEnabled = true

    init {
        try {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attrs)
                .build()
        } catch (_: Exception) {
            soundPool = null
        }
    }

    fun updateSettings(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        this.isSoundEnabled = soundEnabled
        this.isVibrationEnabled = vibrationEnabled
    }

    fun playClick() {
        if (!isSoundEnabled) return
        vibrate(20)
    }

    fun playCardFlip() {
        if (!isSoundEnabled) return
        vibrate(35)
    }

    fun playWin() {
        if (!isSoundEnabled) return
        vibrate(100)
    }

    fun playLose() {
        if (!isSoundEnabled) return
        vibrate(150)
    }

    fun playCoinReward() {
        if (!isSoundEnabled) return
        vibrate(50)
    }

    private fun vibrate(durationMs: Long) {
        if (!isVibrationEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) { }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
