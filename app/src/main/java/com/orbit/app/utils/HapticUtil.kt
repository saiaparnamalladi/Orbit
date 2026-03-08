package com.orbit.app.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticUtil {

    // Gentle double-pulse for heart button
    fun heartPulse(context: Context) {
        vibrate(context, longArrayOf(0, 80, 60, 120), intArrayOf(0, 200, 0, 255))
    }

    // Single short tap for message send
    fun messageSent(context: Context) {
        vibrate(context, longArrayOf(0, 40), intArrayOf(0, 150))
    }

    // Received partner heart — longer, slower throb
    fun receivedHeart(context: Context) {
        vibrate(context, longArrayOf(0, 150, 100, 150, 100, 200), intArrayOf(0, 255, 0, 200, 0, 255))
    }

    private fun vibrate(context: Context, timings: LongArray, amplitudes: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                vibrator.vibrate(timings, -1)
            }
        }
    }
}
