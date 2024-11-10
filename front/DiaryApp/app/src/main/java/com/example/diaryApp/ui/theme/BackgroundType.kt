package com.example.diaryApp.ui.theme

import com.example.diaryApp.R

enum class BackgroundType {
    DEFAULT, ACTIVE, NORMAL;

    fun getBackgroundResource(): Int {
        return when (this) {
            DEFAULT -> R.drawable.daily_parent
            ACTIVE -> R.drawable.daily_active
            NORMAL -> R.drawable.daily_normal
        }
    }
}