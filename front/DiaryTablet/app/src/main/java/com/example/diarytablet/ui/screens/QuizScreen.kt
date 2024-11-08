package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.QuizViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.quiz.Alert
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.quiz.Draw
import com.example.diarytablet.ui.components.quiz.QuizAlert
import com.example.diarytablet.ui.components.quiz.RecommendWordModal
import com.example.diarytablet.ui.components.quiz.Video

enum class QuizModalState {
    NONE,
    START_CONFIRM,
    WORD_SELECTION,
    CORRECT_ANSWER,
    INCORRECT_ANSWER,
}

@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var quizModalState by remember { mutableStateOf(QuizModalState.NONE) }
    var isQuizStarted by remember { mutableStateOf(false) }
    var isQuizEnded by remember { mutableStateOf(false) }
    var currentRound by remember { mutableStateOf(1) }
    val recommendWords by remember { viewModel.recommendWords }
    val isCorrectAnswer by viewModel.isCorrectAnswer.observeAsState()
    var selectedWord by remember { mutableStateOf<String?>(null) }
    val isUserDisconnected = viewModel.userDisconnectedEvent.observeAsState(false).value ?: false
    var isQuizDisconnected by remember { mutableStateOf(false) }

    val isParentJoined by viewModel.parentJoinedEvent.observeAsState(false)
    var isQuizStartEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(isParentJoined) {
        if (isParentJoined) {
            isQuizStartEnabled = true
        }
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

    val roundWords = when (currentRound) {
        1 -> recommendWords.take(3)
        2 -> recommendWords.drop(3).take(3)
        3 -> recommendWords.drop(6).take(3)
        else -> emptyList()
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(35.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(innerPadding)
            ) {
                Row (
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.back_button),
                        contentDescription = "뒤로가기 버튼",
                        modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                isQuizEnded = true
                            }
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    Text(
                        text = "그림 퀴즈",
                        fontSize = 40.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(7f)
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(4f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .clipToBounds()
                    ) {
                        Draw(
                            modifier = Modifier,
                            viewModel = viewModel
                        )
                        selectedWord?.let { word ->
                            Text(
                                text = word,
                                fontSize = 36.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                            )
                        }
                        if (quizModalState == QuizModalState.WORD_SELECTION) {
                            RecommendWordModal(
                                roundWords = roundWords,
                                onWordSelected = { word ->
                                    selectedWord = word
                                    viewModel.sendSetWordAction(word)
                                    quizModalState = QuizModalState.NONE
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.2f))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column (
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                        ){

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(3f)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(color = Color.White.copy(alpha = 0.8f))
                            ){
                                Video(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    viewModel = viewModel
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.5f))
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(4f)
                            ){
                                Image(
                                    painter = painterResource(id = R.drawable.drawing),
                                    contentDescription = "팔레트",
                                    modifier = Modifier
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.5f))
                            BasicButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    if (!isQuizStarted) {
                                        quizModalState = QuizModalState.START_CONFIRM
                                    }
                                    else {
                                        isQuizEnded = true
                                    }
                                },
                                text = if (isQuizStarted) "종료" else "퀴즈 시작",
                                imageResId = 11,
                                enabled = isQuizStartEnabled,
                                ButtonColor = if (isQuizStartEnabled) Color(0xFF5A72A0) else Color.Gray
                            )
                        }
                    }
                }
            }
        }


        when (quizModalState) {
            QuizModalState.START_CONFIRM -> {
                Alert(
                    isVisible = true,
                    onDismiss = { quizModalState = QuizModalState.NONE },
                    onConfirm = {
                        quizModalState = QuizModalState.WORD_SELECTION
                        isQuizStarted = true
                        viewModel.resetPath()
                    },
                    title = "그림퀴즈를 시작할까요?",
                    confirmText = "퀴즈시작"
                )
            }

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
//                            viewModel.updateQuest()
                            viewModel.resetIsCorrectAnswer()
                            viewModel.resetPath()
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
        Alert(
            isVisible = isQuizEnded,
            onDismiss = {
                isQuizEnded = false
            },
            onConfirm = {
                viewModel.leaveSession()
                if (currentRound >= 3) {
                    navController.navigate("main?origin=quiz&isFinished=true") {
                        popUpTo("quiz") { inclusive = true }
                }

                } else {
                    navController.navigate("main") {
                        popUpTo("quiz") { inclusive = true }
                    }
                }

            },
            title = "퀴즈를 종료할까요?",
            confirmText = "종료"
        )
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
}

