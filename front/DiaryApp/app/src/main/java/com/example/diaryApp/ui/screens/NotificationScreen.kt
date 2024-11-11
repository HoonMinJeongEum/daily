package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DeleteProfileList
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.alarm.AlarmItem
import com.example.diaryApp.ui.components.alarm.AlarmList
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.viewmodel.AlarmViewModel
import com.example.diaryApp.viewmodel.QuizViewModel

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: AlarmViewModel = hiltViewModel(),
    quizViewModel: QuizViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.NORMAL
) {


    val alarms by viewModel.alarms

    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopLogoImg(
                    characterImg = R.drawable.daily_character,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "알림",
                    color = Color.White,
                    style = MyTypography.bodyMedium,
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 40.dp)
                )
            }
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxWidth()
                    .height(780.dp) // 필요한 높이 지정
            ) {
                AlarmList(
                    alarmList = alarms,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 80.dp), // 원하는 만큼 상단 간격 설정
                    quizViewModel = quizViewModel,
                    alarmViewModel = viewModel,
                    navController = navController
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // NavMenu를 화면 하단에 고정
        ) {
            NavMenu(navController, "notification", "notification")
        }
    }

}


