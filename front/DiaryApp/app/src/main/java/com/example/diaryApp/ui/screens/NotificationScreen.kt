package com.example.diaryApp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.alarm.AlarmItem
import com.example.diaryApp.viewmodel.AlarmViewModel

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: AlarmViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.ACTIVE
) {

    LaunchedEffect(Unit) {
        viewModel.getAlarms()
    }

    val alarms by viewModel.alarms.observeAsState(emptyList())

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
                characterImg = R.drawable.daily_character
            )
            Text(
                text = "알림",
                fontSize = 30.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                modifier = Modifier
                    .offset(x = 183.dp, y = 30.dp)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp), // 상단 간격 조절
                verticalArrangement = Arrangement.Top
            ) {
                // 알림 목록을 리스트로 표시
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(alarms) { alarm ->
                        AlarmItem(alarm, navController)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                    }
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
    }

