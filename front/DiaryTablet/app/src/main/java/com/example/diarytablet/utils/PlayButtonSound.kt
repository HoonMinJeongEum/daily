package com.example.diarytablet.utils

import android.content.Context
import android.media.MediaPlayer

fun playButtonSound(context: Context, soundResId: Int) {
    try {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
