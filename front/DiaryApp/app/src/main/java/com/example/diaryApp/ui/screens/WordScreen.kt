package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DailyRegisterButton
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TopBackImage

@Composable
fun WordScreen(
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.ACTIVE
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var selectedTab by remember { mutableStateOf("가나다순") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            TopBackImage(
                logoText = "의 단어장!",
                BackImage = R.drawable.navigate_back,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp)
                    )
                    .fillMaxWidth()
                    .height(780.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                    ) {
                        DailyRegisterButton(
                            text = "가나다순",
                            fontSize = 26,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            backgroundColor = Color.Transparent,
                            width = 140,
                            height = 60,
                            isSelected = selectedTab == "가나다순",
                            onClick = { selectedTab = "가나다순" },
                        )
                        DailyRegisterButton(
                            text = "날짜순",
                            fontSize = 26,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            backgroundColor = Color.Transparent,
                            width = 140,
                            height = 60,
                            isSelected = selectedTab == "날짜순",
                            onClick = { selectedTab = "날짜순" },
                        )
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                NavMenu(navController, "main", "word")
            }
        }
    }
}
