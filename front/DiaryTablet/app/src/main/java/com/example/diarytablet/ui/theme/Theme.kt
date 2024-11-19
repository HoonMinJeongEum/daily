package com.example.diarytablet.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext


// Dark theme color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PastelNavy,
    secondary = PastelSkyBlue,
    background = PastelSkyBlue,
    surface = DeepPastelBlue,
    error = PastelSkyBlue,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    onError = White
)

// Light theme color scheme
private val LightColorScheme = lightColorScheme(
    primary = PastelNavy,
    secondary = PastelSkyBlue,
    background = PastelYellow,
    surface = White,
    error = PastelSkyBlue,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,
    onError = Black
)

//색상은 import 따로 안하고 MaterialTheme.colorScheme.primary 이런식으로 사용
@Composable
fun DiaryTabletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyTypography, // 정의한 Typography 사용
        content = content
    )
}
