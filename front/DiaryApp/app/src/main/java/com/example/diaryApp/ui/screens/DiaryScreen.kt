package com.example.diaryApp.ui.screens

import DailyCalendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TopBackImage
import com.example.diaryApp.viewmodel.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryScreen(
    navController: NavController,
    diaryViewModel: DiaryViewModel,
    backgroundType: BackgroundType = BackgroundType.ACTIVE,
) {
    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            TopBackImage(
                logoText = "${diaryViewModel.memberName.value}의 그림 일기",
                BackImage = R.drawable.navigate_back,
                onBackClick = {
                    navController.popBackStack()
                    diaryViewModel.memberName.value = ""
                    diaryViewModel.memberId.intValue = 0
                }
            )
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxWidth()
                    .height(780.dp)
            ) {
                DailyCalendar(viewModel = diaryViewModel, navController = navController)
            }
        }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                NavMenu(navController, "main", "diary")
            }
        }
    }

