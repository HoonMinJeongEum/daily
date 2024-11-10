package com.example.diaryApp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.ProfileList
import com.example.diaryApp.ui.components.quiz.Alert
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.viewmodel.ProfileViewModel
import com.example.diaryApp.viewmodel.QuizViewModel
import com.example.diaryApp.viewmodel.WordViewModel

@Composable
fun MainScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    diaryViewModel: DiaryViewModel,
    wordViewModel: WordViewModel,
    quizViewModel: QuizViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.ACTIVE
) {
    BackgroundPlacement(backgroundType = backgroundType)
    val profileList by profileViewModel.profileList
    var showQuizAlert by remember { mutableStateOf(false) }
    var showQuizConfirmDialog by remember { mutableStateOf(false) }
    var sessionId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            TopLogoImg(
                logoImg = R.drawable.daily_logo,
                characterImg = R.drawable.daily_character
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileList(
                profileList = profileList,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                navController = navController,
                profileViewModel = profileViewModel,
                diaryViewModel = diaryViewModel,
                wordViewModel = wordViewModel,
                quizViewModel = quizViewModel,
                onShowQuizAlert = { newSessionId ->
                    if (newSessionId.isNotEmpty()) {
                        sessionId = newSessionId
                        showQuizConfirmDialog = true
                    } else {
                        showQuizAlert = true
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavMenu(navController, "main", "main")
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
                navController.navigate("catchMind/$sessionId")
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
