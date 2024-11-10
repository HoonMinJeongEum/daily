package com.example.diaryApp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.quiz.Draw
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.components.quiz.Video
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.viewmodel.QuizViewModel
import androidx.compose.ui.text.TextStyle
import com.example.diaryApp.ui.components.quiz.Alert

enum class QuizModalState {
    NONE,
    NOT_QUIZ_START,
    CORRECT_ANSWER,
    INCORRECT_ANSWER,
}

@Composable
fun CatchMindScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.FUN,
    sessionId: String
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var quizModalState by remember { mutableStateOf(QuizModalState.NONE) }
    var currentRound by remember { mutableIntStateOf(1) }
    var selectedWord by remember { mutableStateOf<String?>(null) }
    var inputWord by remember { mutableStateOf("") }
    val isUserDisconnected = viewModel.userDisconnectedEvent.observeAsState(false).value ?: false
    var isQuizDisconnected by remember { mutableStateOf(false) }
    var showQuizEnd by remember { mutableStateOf(false) }

    // 단어 선택 확인 변수
    val isWordSelected by viewModel.isWordSelected.observeAsState(false)
    var isQuizNotStartedAlert by remember { mutableStateOf(false) }

    BackHandler {
        showQuizEnd = true
    }

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
        Column (
          modifier = Modifier
              .fillMaxSize()
        ) {

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val boxHeight = with(LocalDensity.current) { maxHeight.toPx() }
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.navigate_back),
                        contentDescription = "퀴즈 뒤로가기 이미지",
                        modifier = Modifier
                            .weight(0.25f)
                            .fillMaxHeight(0.25f)
                            .clickable {
                                showQuizEnd = true
                            },
                        contentScale = ContentScale.FillBounds
                    )
                    Spacer(
                        modifier = Modifier.weight(0.7f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(3f)
                    ) {
                        Text(
                            text = "자녀1",
                            fontSize = (boxHeight * 0.18f).sp,
                            style = MyTypography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(2f)
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "와 그림 퀴즈",
                            fontSize = (boxHeight * 0.13f).sp,
                            style = MyTypography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(6f)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(8f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(color = Color.White)
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(0.3f))
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(color = Color.Black.copy(alpha = 0.9f))
                    ){
                        Video(
                            modifier = Modifier
                                .fillMaxSize(),
                            viewModel = viewModel
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.3f))
                    Box(
                        modifier = Modifier
                            .weight(4f)
                            .fillMaxWidth(0.9f)
                            .border(1.dp, Color.Black, RoundedCornerShape(32.dp))
                            .clip(RoundedCornerShape(32.dp))
                            .clipToBounds()

                    ) {
                        Draw(
                            modifier = Modifier,
                            viewModel = viewModel
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.3f))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .weight(0.01f),
                        color = Color(0xFFB9B9B9)
                    )
                    Row(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.quiz_chat),
                            contentDescription = "퀴즈 챗 이미지",
                            modifier = Modifier
                                .fillMaxHeight(0.9f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BoxWithConstraints {
                            val rowHeight = with(LocalDensity.current) { maxHeight.toPx() }
                            val textSize = rowHeight * 0.4f

                            Text(
                                text = "정답",
                                fontSize = with(LocalDensity.current) { textSize.toSp() }
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .weight(0.5f)
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .weight(1.6f)
                                .background(Color.White, shape = RoundedCornerShape(7.dp))
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(7.dp))
                                .fillMaxHeight()
                        ) {
                            val fieldHeight = with(LocalDensity.current) { maxHeight.toPx() }
                            val fontSize = (fieldHeight * 0.25f).sp
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(0.04f))
                                BasicTextField(
                                    value = inputWord,
                                    onValueChange = { inputWord = it },
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .fillMaxHeight(0.6f),
                                    textStyle = TextStyle(color = Color.Black, fontSize = fontSize)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                        Button(
                            modifier = Modifier
                                .weight(0.4f)
                                .align(Alignment.CenterVertically)
                                .fillMaxHeight(),
                            onClick = {
                                if (isWordSelected) {
                                    viewModel.sendCheckWordAction(inputWord.trim())
                                    inputWord = ""
                                } else {
                                    isQuizNotStartedAlert = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5A72A0),
                            ),
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ){
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center

                            ) {
                                val buttonHeight = with(LocalDensity.current) { maxHeight.toPx() }
                                Text(
                                    text = "전송",
                                    fontSize = (buttonHeight * 0.3f).sp,
                                    textAlign = TextAlign.Center,
                                    style = MyTypography.bodyLarge,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.3f))
                }

            }

        }
    }
    if (showQuizEnd){
        Alert(
            isVisible = true,
            onDismiss = {
                showQuizEnd = false
            },
            onConfirm = {
                viewModel.leaveSession()
                showQuizEnd = false
                navController.navigate("main") {
                    popUpTo("catchMind") { inclusive = true }
                }
            },
            title = "그림 퀴즈를 종료할까요?",
        )
    }
    when (quizModalState) {
        QuizModalState.CORRECT_ANSWER -> {
            if (currentRound < 3) {
                QuizAlert(
                    title = "정답입니다!\n다음 퀴즈로 넘어갑니다.",
                    onDismiss = {
                        quizModalState = QuizModalState.NONE
                        viewModel.resetIsCorrectAnswer()
                        currentRound++
                        viewModel.resetPath()
                    }
                )
            } else {
                QuizAlert(
                    title = "정답입니다!\n퀴즈가 끝났습니다.",
                    onDismiss = {
                        quizModalState = QuizModalState.NONE
                        selectedWord = null
                        viewModel.resetIsCorrectAnswer()
                        viewModel.resetPath()
                    }
                )
            }
        }

        QuizModalState.INCORRECT_ANSWER -> {
            QuizAlert(
                title = "틀렸습니다.\n다시 시도해보세요!",
                onDismiss = {
                    quizModalState = QuizModalState.NONE
                    viewModel.resetIsCorrectAnswer()
                }
            )
        }
        QuizModalState.NOT_QUIZ_START -> {
            QuizAlert(
                title = "아직 퀴즈를 시작하지 않았어요!",
                onDismiss = {
                    quizModalState = QuizModalState.NONE
                }
            )
        }
        QuizModalState.NONE -> {

        }
    }

    if (isQuizNotStartedAlert) {
        QuizAlert(
            title = "퀴즈가 아직 시작되지 않았어요.",
            onDismiss = { isQuizNotStartedAlert = false }
        )
    }

    if(isQuizDisconnected) {
        QuizAlert(
            onDismiss = {
                viewModel.leaveSession()
                navController.navigate("main") {
                    popUpTo("quiz") { inclusive = true }
                }
            },
            title = "아이가 방을 나갔어요."
        )
    }

}
