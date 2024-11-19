package com.example.diaryApp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.BackgroundType
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(
    startDestination: String = "login",
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    LaunchedEffect(Unit) {
        delay(1000) // 3초 동안 대기
        navController.navigate(startDestination) {
            popUpTo("landing") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.daily_splash),
            contentDescription = "Daily Splash Screen",
            modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // 이미지 비율에 맞춰 화면에 꽉 채우기
        )
    }
}