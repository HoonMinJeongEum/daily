package com.example.diaryApp.ui.components.alarm

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import com.example.diaryApp.ui.theme.DarkGray
import com.example.diaryApp.ui.theme.DeepPastelBlue
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.GrayDetail
import com.example.diaryApp.ui.theme.LightSkyBlue
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.myFontFamily
import com.example.diaryApp.viewmodel.QuizViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@Composable
fun AlarmItem(
    alarm: AlarmResponseDto,
    navController: NavController,
    quizViewModel: QuizViewModel,
    onShowQuizAlert: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val displayDate = alarm.createdAt.format(dateTimeFormatter)

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .height(100.dp),
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
                    .padding(start = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = alarm.name,
                        color = PastelNavy,
                        style = MyTypography.bodySmall
                    )
                    Text(text = " 님의 ", fontSize = 18.sp, color = DarkGray)
                    Text(text = alarm.title, fontSize = 22.sp, color = DeepPastelBlue)
                    if (alarm.title == "그림 퀴즈") {
                        Text(text = " 요청", fontSize = 18.sp, color = DarkGray)
                    } else {
                        Text(text = " 업로드", fontSize = 18.sp, color = DarkGray)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = displayDate, fontSize = 16.sp, color = GrayDetail)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(end = 20.dp)
            ) {
                Button(
                    modifier = Modifier
                        .weight(0.2f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepPastelBlue, // 배경색
                        contentColor = Color.White // 텍스트 색상 (필요에 따라 변경 가능)
                    ),
                    onClick = {
                        if (alarm.title == "그림 퀴즈") {
                            coroutineScope.launch {
                                quizViewModel.checkSession(alarm.name, onShowQuizAlert = {
                                    onShowQuizAlert()
                                }, onNavigateToSession = { sessionId ->
                                    navController.navigate("catchMind/$sessionId")
                                })
                            }
                        } else {
                            navController.navigate("diary")
                        }

                    },
                ) {
                    if (alarm.confirmedAt != null) {
                        Text(text = "완료", fontSize = 18.sp, fontFamily = myFontFamily)
                    } else {
                        if (alarm.title == "그림 퀴즈") {
                            Text(text = "수락", fontSize = 18.sp, fontFamily = myFontFamily)
                        } else {
                            Text(text = "입장", fontSize = 18.sp, fontFamily = myFontFamily)
                        }
                    }
                }
            }
        }
    }
}