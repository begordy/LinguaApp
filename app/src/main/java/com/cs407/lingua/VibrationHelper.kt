package com.cs407.lingua

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModelProvider

class VibrationHelper(private val context: Context, private val settingsViewModel: SettingsViewModel) {


    fun vibrate(pattern: LongArray, repeat: Int = -1) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, repeat)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(pattern, repeat)
        }
    }

    fun vibrateCorrectAnswer() {
        if (settingsViewModel.vibAllowed.value == true) {
            val pattern =
                longArrayOf(0, 100, 50, 100, 200)  // Quick short vibration followed by a longer one
            vibrate(pattern)
        }
    }

    fun vibrateWrongAnswer() {
        if (settingsViewModel.vibAllowed.value == true) {
            val pattern =
                longArrayOf(0, 300, 100, 300, 100, 300)  // Long vibration with pauses in between
            vibrate(pattern)
        }
    }
}