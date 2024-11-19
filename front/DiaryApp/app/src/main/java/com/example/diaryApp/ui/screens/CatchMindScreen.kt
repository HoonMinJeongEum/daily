package com.example.diaryApp.ui.screens

import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import com.example.diaryApp.ui.components.quiz.Alert
import com.example.diaryApp.ui.components.quiz.ToggleAudioButton
import com.example.diaryApp.ui.components.quiz.ToggleMicButton
import com.example.diaryApp.ui.theme.DeepPastelBlue
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.PastelRed

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
    backgroundType: BackgroundType = BackgroundType.NORMAL,
    sessionId: String,
    childName: String
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var quizModalState by remember { mutableStateOf(QuizModalState.NONE) }
    var currentRound by remember { mutableIntStateOf(1) }
    var selectedWord by remember { mutableStateOf<String?>(null) }
    var inputWord by remember { mutableStateOf("") }
    val isUserDisconnected = viewModel.userDisconnectedEvent.observeAsState(false).value ?: false
    var isQuizDisconnected by remember { mutableStateOf(false) }
    var showQuizEnd by remember { mutableStateOf(false) }
    val isCorrectAnswer by viewModel.isCorrectAnswer.observeAsState()
    val aspectRatio by viewModel.aspectRatio.observeAsState(1f)

    // 단어 선택 확인 변수
    val isWordSelected by viewModel.isWordSelected.observeAsState(false)
    var isQuizStartedAlert by remember { mutableStateOf(false) }
    val parentWord by viewModel.parentWord.observeAsState()
    var errorMessage by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler {
        showQuizEnd = true
    }

    LaunchedEffect(isWordSelected) {
        if (isWordSelected) {
            isQuizStartedAlert = true
        }
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
                Box (
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.85f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.navigate_back),
                        contentDescription = "퀴즈 뒤로가기 이미지",
                        modifier = Modifier
                            .fillMaxHeight(0.25f)
                            .aspectRatio(1f)
                            .clickable {
                                showQuizEnd = true
                            }
                            .align(Alignment.CenterStart),
                        contentScale = ContentScale.FillBounds
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = (boxHeight * 0.13f).sp)) {
                                append(childName)
                            }
                            withStyle(style = SpanStyle(fontSize = (boxHeight * 0.1f).sp)) {
                                append(" 와 그림 퀴즈")
                            }
                        },
                        style = MyTypography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
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
                            .fillMaxWidth(0.9f)
                            .aspectRatio(aspectRatio)
                            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clipToBounds()

                    ) {
                        Draw(
                            modifier = Modifier,
                            viewModel = viewModel
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(0.7f)
                            .fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.quiz_chat),
                            contentDescription = "퀴즈 챗 이미지",
                            modifier = Modifier
                                .fillMaxHeight(0.3f).aspectRatio(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            val rowHeight = with(LocalDensity.current) { maxHeight.toPx() }
                            val textSize = rowHeight * 0.3f

                            Text(
                                text = "정답",
                                fontSize = with(LocalDensity.current) { textSize.toSp() },
                                style = MyTypography.bodyLarge,
                                color = PastelNavy,
                                modifier = Modifier.align(alignment = Alignment.Center)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)  // 버튼들이 오른쪽으로 밀리게 하기 위해 여백을 추가
                                .fillMaxHeight(),  // 버튼이 Row 높이를 채우도록 설정
                            horizontalArrangement = Arrangement.End,  // 버튼들을 오른쪽 끝으로 정렬
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            ToggleMicButton(
                                modifier = Modifier,
                                viewModel = viewModel
                            )
                            ToggleAudioButton(
                                modifier = Modifier,
                                viewModel = viewModel
                            )
                        }

                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .weight(0.01f),
                        color = Color(0xFFB9B9B9)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .weight(0.2f),
                        contentAlignment = Alignment.CenterStart
                    ){
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            val errorFontSize = with(LocalDensity.current) { (maxHeight * 0.5f).toSp() } // 부모 높이의 10% 크기로 설정

                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = PastelRed,
                                    fontSize = errorFontSize,
                                    modifier = Modifier.align(alignment = Alignment.Center)
                                )

                            }
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
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            val fieldHeight = with(LocalDensity.current) { maxHeight.toPx() }
                            val fontSize = (fieldHeight * 0.2f).sp

                            BasicTextField(
                                value = inputWord,
                                onValueChange = {
                                    if (it.length <= 10) {
                                        inputWord = it
                                        errorMessage = "" // 에러 메시지 초기화
                                    } else {
                                        inputWord = it.take(10)
                                        errorMessage = "10글자까지만 입력 가능합니다."
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxHeight(0.7f)
                                    .fillMaxWidth(0.9f),
                                textStyle = TextStyle(color = Color.Black, fontSize = fontSize),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Send
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (isWordSelected && inputWord.isNotBlank()) {
                                            viewModel.sendCheckWordAction(inputWord.trim())
                                            inputWord = ""
                                            keyboardController?.hide()
                                        }
                                    }
                                ),
                                singleLine = true,
                                )

                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                        Button(
                            modifier = Modifier
                                .weight(0.4f)
                                .align(Alignment.CenterVertically)
                                .fillMaxHeight(),
                            onClick = {
                                keyboardController?.hide()
                                viewModel.sendCheckWordAction(inputWord.trim())
                                inputWord = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelNavy,
                                contentColor = Color.White,
                                disabledContainerColor = Color.LightGray, // 비활성화 상태 배경색
                                disabledContentColor = Color.White
                            ),
                            enabled = isWordSelected && inputWord.isNotBlank(),
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
                                    fontSize = (buttonHeight * 0.25f).sp,
                                    textAlign = TextAlign.Center,
                                    style = MyTypography.bodyLarge,
                                    color = Color.White,
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
                    title = "정답이에요!\n다음 퀴즈로 넘어갑니다.",
                    onDismiss = {
                        quizModalState = QuizModalState.NONE
                        viewModel.resetIsCorrectAnswer()
                        currentRound++
                        viewModel.resetPath()
                    }
                )
            } else {
                QuizAlert(
                    title = "정답이에요!\n퀴즈가 끝났습니다.",
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

    if (isQuizStartedAlert) {
        QuizAlert(
            title = "퀴즈가 시작되었어요!",
            onDismiss = { isQuizStartedAlert = false }
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
