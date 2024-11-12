package com.example.diarytablet.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.quiz.Alert
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.quiz.Draw
import com.example.diarytablet.ui.components.quiz.DrawingColorPalette
import com.example.diarytablet.ui.components.quiz.DrawingRedoButton
import com.example.diarytablet.ui.components.quiz.DrawingThicknessSelector
import com.example.diarytablet.ui.components.quiz.DrawingUndoButton
import com.example.diarytablet.ui.components.quiz.QuizAlert
import com.example.diarytablet.ui.components.quiz.RecommendWordModal
import com.example.diarytablet.ui.components.quiz.Video
import com.example.diarytablet.ui.theme.MyTypography

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
    val pathStyle by viewModel.pathStyle.observeAsState()
    var isQuizAlertVisible by remember { mutableStateOf(false) }

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
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.diary_box),
                            contentDescription = "배경 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                        Draw(
                            modifier = Modifier
                                .fillMaxSize(0.9f)
                                .clipToBounds()
                                .align(Alignment.Center),
                            viewModel = viewModel,
                        )
                        selectedWord?.let { word ->
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f)
                                    .align(Alignment.Center),
                            ) {
                                val textSize = with(LocalDensity.current) { (maxHeight * 0.1f).toSp() }
                                Column(
                                    modifier = Modifier.align(Alignment.TopCenter),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier.width(IntrinsicSize.Max)
                                    ) {
                                        Text(
                                            text = word,
                                            fontSize = textSize,
                                            color = Color(0xFF5A72A0),
                                            textAlign = TextAlign.Center,
                                            style = MyTypography.bodyLarge,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        HorizontalDivider(
                                            color = Color(0xFF5A72A0),
                                            thickness = 3.dp,
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(top = 4.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }

                            }
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
                                    .weight(2.5f)
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
                                    .weight(4.7f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color = Color.White.copy(alpha = 0.8f))
                            ){
                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ){
                                    DrawingColorPalette(
                                        modifier = Modifier
                                            .weight(2f),
                                        onColorChanged = { viewModel.updateColor(it) }
                                    )
                                    DrawingThicknessSelector(
                                        modifier = Modifier
                                            .weight(1f),
                                        onSizeChanged = { viewModel.updateWidth(it) },
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.7f),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        var selectedImage by remember { mutableStateOf("pen") }
                                        val penScale by animateFloatAsState(targetValue = if (selectedImage == "pen") 1.2f else 1f)
                                        val eraserScale by animateFloatAsState(targetValue = if (selectedImage == "eraser") 1.2f else 1f)
                                        val penColor = remember { mutableStateOf(Color.Black) }

                                        Image(
                                            painter = painterResource(id = R.drawable.palette_pen),
                                            contentDescription = "연필",
                                            modifier = Modifier
                                                .fillMaxHeight(0.8f)
                                                .graphicsLayer(scaleX = penScale, scaleY = penScale)
                                                .clickable(
                                                    onClick = {
                                                        if (selectedImage == "eraser") {
                                                            viewModel.updateColor(penColor.value)
                                                            selectedImage = "pen"
                                                        }
                                                    },
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() }
                                                )
                                        )
                                        Image(
                                            painter = painterResource(id = R.drawable.palette_eraser),
                                            contentDescription = "지우개",
                                            modifier = Modifier
                                                .fillMaxHeight(0.8f)
                                                .graphicsLayer(scaleX = eraserScale, scaleY = eraserScale)
                                                .clickable(
                                                    onClick = {
                                                        if (selectedImage == "pen") {
                                                            penColor.value = pathStyle?.color ?: Color.Black
                                                            viewModel.updateColor(Color.White)
                                                            selectedImage = "eraser"
                                                        }

                                                    },
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() }
                                                )

                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(0.3f)
                                            .weight(0.5f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        DrawingUndoButton(
                                            modifier = Modifier
                                                .weight(1f),
                                            onClick = { viewModel.undoPath() }
                                        )
                                        Spacer(modifier = Modifier.weight(0.1f))
                                        DrawingRedoButton(
                                            modifier = Modifier
                                                .weight(1f),
                                            onClick = { viewModel.redoPath() }
                                        )
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.weight(0.3f))
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
                                ButtonColor = when {
                                    isQuizStarted -> Color(0xFFD27979) // 종료일 때 빨간색
                                    else -> Color(0xFF5A72A0) // 기본 색상
                                }
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
                        viewModel.sendQuizStart()
                    },
                    title = "그림퀴즈를 시작할까요?",
                    confirmText = "퀴즈시작"
                )
            }

            QuizModalState.CORRECT_ANSWER -> {
                if (currentRound < 3) {
                    QuizAlert(
                        title = "정답이에요!\n" +
                                "다음 퀴즈로 넘어갑니다.",
                        onDismiss = {
                            quizModalState = QuizModalState.WORD_SELECTION
                            viewModel.resetIsCorrectAnswer()
                            currentRound++
                            viewModel.resetPath()
                        }
                    )
                } else {
                    QuizAlert(
                        title = "정답입니다!\n" +
                                "퀴즈가 끝났습니다.",
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
                    title = "틀렸습니다.\n" +
                            "다시 시도해보세요!",
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
                    viewModel.leaveSession()
                    navController.navigate("main") {
                        popUpTo("quiz") { inclusive = true }
                    }
                },
                title = "부모님이 방을 나갔어요."
            )
        }
        if(isQuizStartEnabled && !isQuizAlertVisible) {
            QuizAlert(
                title = "이제 그림 퀴즈를\n" +
                        "시작할 수 있어요!",
                onDismiss = {
                    isQuizAlertVisible = true
                }
            )
        }
    }
}

