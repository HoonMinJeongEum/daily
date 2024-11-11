package com.example.diaryApp.ui.components.alarm

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import com.example.diaryApp.ui.components.DeleteProfileItem
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.theme.DarkGray
import com.example.diaryApp.ui.theme.GrayDetail
import com.example.diaryApp.ui.theme.GrayText
import com.example.diaryApp.viewmodel.AlarmViewModel
import com.example.diaryApp.viewmodel.QuizViewModel

@Composable
fun AlarmList(
    modifier: Modifier = Modifier,
    alarmList: List<AlarmResponseDto>,
    quizViewModel: QuizViewModel,
    alarmViewModel: AlarmViewModel,
    navController: NavController
){
    var showQuizAlert by remember { mutableStateOf(false) }
    Log.d("AlarmList", "${alarmList}")

    LaunchedEffect(Unit) {
        alarmViewModel.getAlarms()
    }

    LazyColumn (
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(alarmList) { index, alarm ->
            AlarmItem(alarm,
                navController,
                quizViewModel,
                onShowQuizAlert = { showQuizAlert = true })
            if (index < alarmList.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    thickness = 1.dp,
                    color = GrayDetail
                )
            }
        }
    }
    if (showQuizAlert) {
        QuizAlert(
            title = "방이 생성되지 않았습니다.",
            onDismiss = { showQuizAlert = false }
        )
    }
}