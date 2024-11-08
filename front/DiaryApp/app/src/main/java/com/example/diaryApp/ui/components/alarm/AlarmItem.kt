package com.example.diaryApp.ui.components.alarm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import com.example.diaryApp.viewmodel.QuizViewModel
import kotlinx.coroutines.launch


@Composable
fun AlarmItem(
    alarm: AlarmResponseDto,
    navController: NavController,
    quizViewModel: QuizViewModel,
    onShowQuizAlert: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row (
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .weight(8f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = alarm.name, fontSize = 20.sp, color = Color.Blue, fontWeight = FontWeight.Bold)
                Text(text = "님의", fontSize = 15.sp)
                Text(text = alarm.title, fontSize = 15.sp, color = Color.Blue)
                if(alarm.title == "그림 퀴즈") {
                    Text(text = "요청", fontSize = 15.sp)
                }
                else {
                    Text(text = "업로드", fontSize = 15.sp)
                }
            }
            
            Text(text = "받은 시간: ${alarm.createdAt}", fontSize = 10.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.weight(0.1f))
    
        Button(
            modifier = Modifier
                .weight(3f),
            onClick = {
                if(alarm.title == "그림 퀴즈") {
                    coroutineScope.launch {
                        quizViewModel.checkSession(alarm.name, onShowQuizAlert = {
                            onShowQuizAlert()
                        }, onNavigateToSession = { sessionId ->
                            navController.navigate("catchMind/$sessionId")
                        })
                    }
                }
                else {
                    navController.navigate("diary")
                }

            },
        ) {
            if (alarm.confirmedAt != null){
                Text(text = "완료", fontSize = 15.sp)
            }
            else {
                if(alarm.title == "그림 퀴즈") {
                    Text(text = "수락", fontSize = 15.sp)
                }

                else {
                    Text(text = "입장", fontSize = 15.sp)
                }
            }

        }

    }

}