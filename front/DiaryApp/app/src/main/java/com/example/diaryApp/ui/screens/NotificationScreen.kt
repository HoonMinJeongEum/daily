package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.example.diaryApp.ui.components.BasicModal
import com.example.diaryApp.ui.components.DeleteProfileList
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TabletHeader
import com.example.diaryApp.ui.components.alarm.AlarmItem
import com.example.diaryApp.ui.components.alarm.AlarmList
import com.example.diaryApp.ui.components.quiz.Alert
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

    var showQuizAlert by remember { mutableStateOf(false) }
    var showQuizConfirmDialog by remember { mutableStateOf(false) }
    var sessionId by remember { mutableStateOf<String?>(null) }
    var childName by remember { mutableStateOf<String?>(null) }
    val alarms by viewModel.alarms

    BackgroundPlacement(backgroundType = backgroundType)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val footerHeight = screenWidth / 4.5f
        val textFieldHeight = screenWidth / 5f
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabletHeader(
                    pageName = "알림",
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxSize()
                    .padding(bottom = footerHeight) // 필요한 높이 지정
            ) {
                AlarmList(
                    screenHeight = screenHeight,
                    screenWidth = screenWidth,
                    alarmList = alarms,
                    quizViewModel = quizViewModel,
                    alarmViewModel = viewModel,
                    navController = navController,
                    onShowQuizAlert = { newSessionId, newChildName ->
                        if (newSessionId.isNotEmpty()) {
                            sessionId = newSessionId
                            childName = newChildName
                            showQuizConfirmDialog = true
                        } else {
                            showQuizAlert = true
                        }
                    }
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
            BasicModal(
                screenWidth = screenWidth, // 스크린 크기를 전달
                isDialogVisible = (showQuizConfirmDialog && sessionId != null),
                onDismiss = {
                    showQuizConfirmDialog = false
                },
                onSuccessClick = {
                    showQuizConfirmDialog = false
                    navController.navigate("catchMind/$sessionId/$childName")
                },
                mainText = "그림 퀴즈에 입장할까요?",
                buttonText = "네",
                successButtonColor = Color(0xFF5A72A0) // "네" 버튼의 색상
            )

    }




    if (showQuizAlert) {
        QuizAlert(
            title = "아직 퀴즈가 준비되지 않았어요.",
            onDismiss = { showQuizAlert = false }
        )
    }

}


