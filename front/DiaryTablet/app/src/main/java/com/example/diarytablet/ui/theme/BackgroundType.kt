// theme/BackgroundType.kt
package com.example.diarytablet.ui.theme

import com.example.diarytablet.R

enum class BackgroundType {
    DEFAULT, DRAWING_QUIZ, DRAWING_DIARY;

    fun getBackgroundResource(): Int {
        return when (this) {
            DEFAULT -> R.drawable.normal_background
            DRAWING_QUIZ -> R.drawable.drawingquiz_background
            DRAWING_DIARY -> R.drawable.drawingdiary_background
        }
    }
}
