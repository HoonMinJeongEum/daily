package com.example.diaryApp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.example.diaryApp.ui.components.TabletHeader
import com.example.diaryApp.ui.components.quiz.Alert
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.theme.MyTypography
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabletHeader(
                pageName = "main",
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            Spacer(modifier = Modifier.height(screenHeight*0.06f))

            ProfileList(
                profileList = profileList,
                modifier = Modifier
                    .fillMaxWidth(),
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

//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            ProfileList(
//                profileList = profileList,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 100.dp),
//                navController = navController,
//                profileViewModel = profileViewModel,
//                diaryViewModel = diaryViewModel,
//                wordViewModel = wordViewModel,
//                quizViewModel = quizViewModel,
//                onShowQuizAlert = { newSessionId ->
//                    if (newSessionId.isNotEmpty()) {
//                        sessionId = newSessionId
//                        showQuizConfirmDialog = true
//                    } else {
//                        showQuizAlert = true
//                    }
//                }
//            )
//        }


        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavMenu(navController, "main", "main")
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
}
