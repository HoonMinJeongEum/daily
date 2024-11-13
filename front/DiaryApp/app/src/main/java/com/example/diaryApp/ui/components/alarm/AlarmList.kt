package com.example.diaryApp.ui.components.alarm

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import com.example.diaryApp.ui.components.DeleteProfileItem
import com.example.diaryApp.ui.components.quiz.Alert
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.theme.DarkGray
import com.example.diaryApp.ui.theme.GrayDetail
import com.example.diaryApp.ui.theme.GrayText
import com.example.diaryApp.viewmodel.AlarmViewModel
import com.example.diaryApp.viewmodel.QuizViewModel

@Composable
fun AlarmList(
    screenHeight : Dp,
    screenWidth : Dp,
    modifier: Modifier = Modifier,
    alarmList: List<AlarmResponseDto>,
    quizViewModel: QuizViewModel,
    alarmViewModel: AlarmViewModel,
    navController: NavController
){
    var showQuizAlert by remember { mutableStateOf(false) }
    Log.d("AlarmList", "${alarmList}")
    var showQuizConfirmDialog by remember { mutableStateOf(false) }
    var sessionId by remember { mutableStateOf<String?>(null) }
    var childName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        alarmViewModel.getAlarms()
    }

    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = screenWidth * 0.04f, vertical =  screenHeight * 0.05f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(alarmList) { index, alarm ->
            AlarmItem(
                screenHeight = screenHeight,
                screenWidth = screenWidth,
                alarm,
                navController,
                quizViewModel,
                onShowQuizAlert = { newSessionId, newChildName ->
                    if (newSessionId.isNotEmpty()) {
                        sessionId = newSessionId
                        childName = newChildName
                        showQuizConfirmDialog = true
                    } else {
                        showQuizAlert = true
                    }})
            if (index < alarmList.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = screenWidth * 0.06f),
                    thickness = 1.dp,
                    color = GrayDetail
                )
            }
        }
    }
    if (showQuizConfirmDialog && sessionId != null) {
        Alert(
            isVisible = true,
            onDismiss = {
                showQuizConfirmDialog = false
            },
            onConfirm = {
                showQuizConfirmDialog = false
                navController.navigate("catchMind/$sessionId/$childName")
            },
            title = "그림 퀴즈에 입장할까요?",
        )
    }

    if (showQuizAlert) {
        QuizAlert(
            title = "아직 퀴즈가 준비되지 않았어요.",
            onDismiss = { showQuizAlert = false }
        )
    }
}