package com.example.diaryApp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.NavMenu

@Composable
fun CatchMindScreen(
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.ACTIVE
) {
    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            TopLogoImg(
                logoImg = R.drawable.daily_logo,
                characterImg = R.drawable.daily_character
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // NavMenu를 화면 하단에 고정
        ) {
            NavMenu(navController)
        }
    }
}
