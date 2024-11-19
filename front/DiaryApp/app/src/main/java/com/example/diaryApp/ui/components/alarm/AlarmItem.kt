package com.example.diaryApp.ui.components.alarm

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.theme.DarkGray
import com.example.diaryApp.ui.theme.DeepPastelBlue
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.GrayDetail
import com.example.diaryApp.ui.theme.LightSkyBlue
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.myFontFamily
import com.example.diaryApp.viewmodel.AlarmViewModel
import com.example.diaryApp.viewmodel.QuizViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@Composable
fun AlarmItem(
    screenHeight : Dp,
    screenWidth : Dp,
    alarm: AlarmResponseDto,
    navController: NavController,
    quizViewModel: QuizViewModel,
    alarmViewModel: AlarmViewModel = hiltViewModel(),
    diaryViewModel: DiaryViewModel = hiltViewModel(),
    onShowQuizAlert: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val displayDate = alarm.createdAt.format(dateTimeFormatter)

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = screenHeight * 0.01f, vertical = screenWidth * 0.01f)
        .height(screenHeight * 0.1f),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(0.7f)
                    .padding(start = screenWidth * 0.04f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = alarm.name,
                        color = PastelNavy,
                        style = MyTypography.bodySmall.copy(
                            fontSize = (screenWidth.value * 0.05f).sp
                        )
                    )
                    Text(text = " 님의 ", fontSize = (screenWidth.value * 0.04f).sp, color = DarkGray)
                    Text(text = alarm.title, fontSize = (screenWidth.value * 0.045f).sp, color = DeepPastelBlue)
                    if (alarm.title == "그림 퀴즈") {
                        Text(text = " 요청", fontSize = (screenWidth.value * 0.04f).sp, color = DarkGray)
                    } else if(alarm.title == "그림 일기"){
                        Text(text = " 업로드", fontSize = (screenWidth.value * 0.04f).sp, color = DarkGray)
                    }
                    else {
                        Text(text = " 구매", fontSize = (screenWidth.value * 0.04f).sp, color = DarkGray)
                    }
                }
                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                Text(text = displayDate, fontSize = (screenWidth.value * 0.03f).sp, color = GrayDetail)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(end = screenWidth * 0.04f)
            ) {
                
                Button(
                    modifier = Modifier
                        .weight(0.2f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepPastelBlue, // 배경색
                        contentColor = Color.White, // 텍스트 색상 (필요에 따라 변경 가능)
                        disabledContainerColor = Color.LightGray, // 비활성화 상태 배경색
                        disabledContentColor = Color.White
                    ),
                    onClick = {
                        if (alarm.title == "그림 퀴즈") {
                            coroutineScope.launch {
                                quizViewModel.checkSession(
                                    alarm.name,
                                    onShowQuizAlert = { newSessionId ->
                                        onShowQuizAlert(newSessionId, alarm.name)
                                    })
                            }
                        }
                        else if(alarm.title == "쿠폰") {
                            // 상점 탭 전환
                            alarmViewModel.checkAlarm(alarm.id)
                            navController.navigate("shop")
                        }
                        else {
                            diaryViewModel.memberName.value = alarm.name
                            alarmViewModel.checkAlarm(alarm.id)
                            navController.navigate("diary/${alarm.titleId}/${alarm.name}")
                        }

                    },
                    enabled = alarm.confirmedAt == null
                ) {
                    if (alarm.confirmedAt != null) {
                        Text(text = "완료", fontSize = (screenWidth.value * 0.04f).sp, fontFamily = myFontFamily)
                    } else {
                        if (alarm.title == "그림 퀴즈") {
                            Text(text = "수락", fontSize = (screenWidth.value * 0.04f).sp, fontFamily = myFontFamily)
                        }
                        else if (alarm.title == "쿠폰"){
                            Text(text = "확인", fontSize = (screenWidth.value * 0.04f).sp, fontFamily = myFontFamily)
                        }
                        else {
                            Text(text = "입장", fontSize = (screenWidth.value * 0.04f).sp, fontFamily = myFontFamily)
                        }
                    }
                }

            }
        }
    }
}