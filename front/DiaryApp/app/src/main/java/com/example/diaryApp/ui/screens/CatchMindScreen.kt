package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.components.quiz.Draw
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.components.quiz.Video
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.viewmodel.QuizViewModel

enum class QuizModalState {
    NONE,
    WORD_SELECTION,
    CORRECT_ANSWER,
    INCORRECT_ANSWER,
}

@Composable
fun CatchMindScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.ACTIVE,
    sessionId: String
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var quizModalState by remember { mutableStateOf(QuizModalState.NONE) }
    var currentRound by remember { mutableStateOf(1) }
    val isCorrectAnswer by viewModel.isCorrectAnswer.observeAsState()
    var selectedWord by remember { mutableStateOf<String?>(null) }
    var inputWord by remember { mutableStateOf("") } // 부모
    val isUserDisconnected = viewModel.userDisconnectedEvent.observeAsState(false).value ?: false
    var isQuizDisconnected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadQuiz(sessionId)
    }

    LaunchedEffect(isCorrectAnswer) {
        isCorrectAnswer?.let { correct ->
            quizModalState = if (correct) {
                QuizModalState.CORRECT_ANSWER
            } else {
                QuizModalState.INCORRECT_ANSWER
            }
        }
    }

    LaunchedEffect(isUserDisconnected) {
        if (isUserDisconnected) {
            isQuizDisconnected = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopLogoImg(
                logoImg = R.drawable.daily_logo,
                characterImg = R.drawable.daily_character
            )
        }
        Column (
          modifier = Modifier
              .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier
                .fillMaxSize()
                .weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(8f)
                    .background(color = Color.White)
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxSize(0.9f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(color = Color.Black.copy(alpha = 0.9f))
                    ){
                        Video(
                            modifier = Modifier
                                .fillMaxSize(),
                            viewModel = viewModel
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                    Box(
                        modifier = Modifier
                            .weight(4f)
                            .fillMaxSize(0.9f)
                            .border(2.dp, Color.Gray, RoundedCornerShape(32.dp))
                            .clip(RoundedCornerShape(32.dp))
                            .clipToBounds()

                    ) {
                        Draw(
                            modifier = Modifier,
                            viewModel = viewModel
                        )

                    }

                    Spacer(modifier = Modifier.weight(0.1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize(0.95f)
                            .weight(1f)

                    ) {
                        TextField(
                            value = inputWord,
                            onValueChange = { inputWord = it },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.sendCheckWordAction(inputWord.trim()) // 단어 확인 요청
                            inputWord = ""
                        }) {
                            Text("전송")
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                }

            }

        }
    }

    when (quizModalState) {
        QuizModalState.CORRECT_ANSWER -> {
            if (currentRound < 3) {
                QuizAlert(
                    title = "정답입니다! 다음 퀴즈로 넘어갑니다",
                    onDismiss = {
                        quizModalState = QuizModalState.WORD_SELECTION
                        viewModel.resetIsCorrectAnswer()
                        currentRound++
                        viewModel.resetPath()
                    }
                )
            } else {
                QuizAlert(
                    title = "정답입니다! 퀴즈가 끝났습니다",
                    onDismiss = {
                        quizModalState = QuizModalState.NONE
                        selectedWord = null
                        viewModel.resetIsCorrectAnswer()
                        viewModel.resetPath()
                        
                        // 완료 api호출
                    }
                )
            }
        }

        QuizModalState.INCORRECT_ANSWER -> {
            QuizAlert(
                title = "틀렸습니다. 다시 시도해보세요!",
                onDismiss = {
                    quizModalState = QuizModalState.NONE
                    viewModel.resetIsCorrectAnswer()
                }
            )
        }

        QuizModalState.NONE -> {

        }
        else -> {
        }

    }

    if(isQuizDisconnected) {
        QuizAlert(
            onDismiss = {
                navController.navigate("main") {
                    popUpTo("quiz") { inclusive = true }
                }
            },
            title = "다른 사용자가 방을 나갔습니다."
        )
    }

}
