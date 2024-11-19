package com.example.diarytablet.ui.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.modal.CommonModal
import com.example.diarytablet.ui.components.modal.CommonPopup
import com.example.diarytablet.ui.components.quiz.CorrectQuizPopup
import com.example.diarytablet.ui.components.quiz.Draw
import com.example.diarytablet.ui.components.quiz.DrawingColorPalette
import com.example.diarytablet.ui.components.quiz.DrawingRedoButton
import com.example.diarytablet.ui.components.quiz.DrawingThicknessSelector
import com.example.diarytablet.ui.components.quiz.DrawingUndoButton
import com.example.diarytablet.ui.components.quiz.RecommendWordModal
import com.example.diarytablet.ui.components.quiz.ToggleAudioButton
import com.example.diarytablet.ui.components.quiz.ToggleMicButton
import com.example.diarytablet.ui.components.quiz.Video
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.SpenEventViewModel
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEvent

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
    backgroundType: BackgroundType = BackgroundType.DRAWING_QUIZ,
    spenEventViewModel: SpenEventViewModel
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val isLoading by viewModel.isLoading
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
    var selectedImage by remember { mutableStateOf("pen") }
    val parentWord by viewModel.parentWord.observeAsState()
    var isExitInProgress by remember { mutableStateOf(false) }

    val penScale by animateFloatAsState(targetValue = if (selectedImage == "pen") 1.2f else 1f)
    val eraserScale by animateFloatAsState(targetValue = if (selectedImage == "eraser") 1.2f else 1f)
    val penColor = remember { mutableStateOf(Color.Black) }
    val context = LocalContext.current
    LaunchedEffect(isParentJoined) {
        if (isParentJoined) {
            isQuizStartEnabled = true
        }
    }
    LaunchedEffect(Unit) {
        spenEventViewModel.spenEventFlow.collect { event ->
            val buttonEvent = ButtonEvent(event)

            when (buttonEvent.action) {
                ButtonEvent.ACTION_DOWN -> {
                    if (selectedImage == "pen") {
                        selectedImage = "eraser"
                        penColor.value =
                            pathStyle?.color ?: Color.Black
                        viewModel.updateColor(Color.White)
                        selectedImage = "eraser"
                    }
                    else {
                        selectedImage = "pen"
                        viewModel.updateColor(penColor.value)
                    }
                }
                ButtonEvent.ACTION_UP -> {
                    Log.d("QuizScreen", "Button Up Detected")
                }
            }
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
    if(!isLoading) {
        Box{
            Column(
                modifier = Modifier
                    .padding(35.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
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
                                    playButtonSound(context, R.raw.all_button)
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
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .weight(4f)
                                .fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.diary_box),
                                contentDescription = "배경 이미지",
                                modifier = Modifier.fillMaxSize().align(Alignment.Center),
                                contentScale = ContentScale.FillBounds
                            )
                            var drawWidth by remember { mutableStateOf(0) }
                            var drawHeight by remember { mutableStateOf(0) }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.9f)
                                    .border(1.dp, Color.Black, RoundedCornerShape(32.dp))
                                    .clip(RoundedCornerShape(32.dp))
                                    .clipToBounds()
                                    .align(Alignment.Center)
                                    .onSizeChanged { size ->
                                        drawWidth = size.width
                                        drawHeight = size.height
                                        val aspectRatio = drawWidth.toFloat() / drawHeight // 비율 계산
                                        viewModel.sendAspectRatio(aspectRatio) // 서버에 비율 전송
                                    },
                            ) {
                                Draw(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    viewModel = viewModel
                                )
                            }

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
                                        Spacer(modifier = Modifier.height(7.dp))
                                        Box(
                                            modifier = Modifier.width(IntrinsicSize.Max)
                                        ) {
                                            val (text, textColor) = when {
                                                !isQuizStarted -> "퀴즈를 시작해 보세요!" to Color.LightGray // 연한 회색
                                                currentRound > 3 -> "퀴즈 종료" to Color.LightGray // 연한 회색
                                                else -> selectedWord to Color(0xFF5A72A0) // 진행 중일 때의 기본 색상
                                            }

                                            text?.let {
                                                Text(
                                                    text = it,
                                                    fontSize = textSize,
                                                    color = textColor,
                                                    textAlign = TextAlign.Center,
                                                    style = MyTypography.bodyLarge,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                                HorizontalDivider(
                                                    color = textColor,
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

                        Spacer(modifier = Modifier.weight(0.1f))
                        Box(
                            modifier = Modifier
                                .weight(1.1f)
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
                                            onColorChanged = { viewModel.updateColor(it) },
                                            isPenSelected = (selectedImage == "pen")
                                        )
                                        DrawingThicknessSelector(
                                            modifier = Modifier
                                                .weight(1f),
                                            onSizeChanged = { viewModel.updateWidth(it) },
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1.2f),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ){

                                            Spacer(modifier = Modifier.weight(0.2f))
                                            BoxWithConstraints(
                                                modifier = Modifier
                                                    .fillMaxHeight(0.5f)
                                                    .weight(0.9f)
                                            ) {
                                                val buttonSize = minOf(maxWidth, maxHeight) // 가로와 세로 중 작은 값을 기준으로 크기 설정

                                                DrawingUndoButton(
                                                    modifier = Modifier.size(buttonSize), // 버튼을 1:1 비율로 설정
                                                    onClick = { viewModel.undoPath() },
                                                    viewModel = viewModel
                                                )
                                            }

                                            BoxWithConstraints(
                                                modifier = Modifier
                                                    .fillMaxHeight(0.5f)
                                                    .weight(0.9f)
                                            ) {
                                                val buttonSize = minOf(maxWidth, maxHeight)

                                                DrawingRedoButton(
                                                    modifier = Modifier.size(buttonSize),
                                                    onClick = { viewModel.redoPath() },
                                                    viewModel = viewModel
                                                )
                                            }
                                            Image(
                                                painter = painterResource(id = R.drawable.palette_pen),
                                                contentDescription = "연필",
                                                modifier = Modifier
                                                    .fillMaxHeight(0.6f)
                                                    .aspectRatio(1f)
                                                    .graphicsLayer(scaleX = penScale, scaleY = penScale)
                                                    .background(
                                                        color = if (selectedImage == "pen") Color.LightGray else Color.Transparent,
                                                        shape = CircleShape
                                                    ) // 선택 시 배경 색 강조
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
                                                    .fillMaxHeight(0.6f)
                                                    .aspectRatio(1f)
                                                    .graphicsLayer(
                                                        scaleX = eraserScale,
                                                        scaleY = eraserScale
                                                    )
                                                    .background(
                                                        color = if (selectedImage == "eraser") Color.LightGray else Color.Transparent,
                                                        shape = CircleShape
                                                    ) // 선택 시 배경 색 강조
                                                    .clickable(
                                                        onClick = {
                                                            if (selectedImage == "pen") {
                                                                penColor.value =
                                                                    pathStyle?.color ?: Color.Black
                                                                viewModel.updateColor(Color.White)
                                                                selectedImage = "eraser"
                                                            }

                                                        },
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() }
                                                    )

                                            )
                                            Spacer(modifier = Modifier.weight(0.2f))
                                        }
                                    }

                                }
                                Spacer(modifier = Modifier.weight(0.3f))
                                val rowWidth = remember { mutableStateOf(0) }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .onSizeChanged { size ->
                                            rowWidth.value = size.width
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    ToggleMicButton(
                                        modifier = Modifier
                                            .weight(2f),
                                        viewModel = viewModel,
                                        enable = isQuizStartEnabled
                                    )
                                    ToggleAudioButton(
                                        modifier = Modifier
                                            .weight(2f),
                                        viewModel = viewModel,
                                        enable = isQuizStartEnabled
                                    )
                                    val fontSize = (rowWidth.value * 0.028f)
                                    BasicButton(
                                        modifier = Modifier
                                            .weight(6f),
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
                                        },
                                        fontSize = fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            }

            when (quizModalState) {
                QuizModalState.START_CONFIRM -> {
                    CommonModal(
                        onDismissRequest = {quizModalState = QuizModalState.NONE},
                        titleText= "그림퀴즈를 시작할까요?",
                        confirmText= "퀴즈 시작",
                        onConfirm= {
                            quizModalState = QuizModalState.WORD_SELECTION
                            isQuizStarted = true
                            viewModel.resetPath()
                            viewModel.sendQuizStart()
                            playButtonSound(context, R.raw.quiz_start)

                        },
                    )

                }

                QuizModalState.CORRECT_ANSWER -> {
                    if (currentRound < 3) {
                        CommonPopup(
                            titleText = "정답이에요!\n다음 퀴즈로 넘어갑니다.",
                            onDismissRequest = {
                                quizModalState = QuizModalState.WORD_SELECTION
                                viewModel.resetIsCorrectAnswer()
                                currentRound++
                                viewModel.resetPath()
                            }
                        )
                    } else {
                        CommonPopup(
                            titleText = "정답이에요!\n퀴즈가 끝났습니다.",
                            onDismissRequest = {
                                quizModalState = QuizModalState.NONE
                                selectedWord = null
                                currentRound++
                                viewModel.resetIsCorrectAnswer()
                                viewModel.resetPath()
                            }
                        )
                    }
                    playButtonSound(context, R.raw.quiz_pass)

                }

                QuizModalState.INCORRECT_ANSWER -> {
                    parentWord?.let {
                        CorrectQuizPopup(
                            answer = it,
                            titleText = "틀렸습니다.\n다시 시도해보세요!",
                            onDismissRequest = {
                                quizModalState = QuizModalState.NONE
                                viewModel.resetIsCorrectAnswer()
                            }
                        )
                        playButtonSound(context, R.raw.quiz_fail)

                    }
                }

                QuizModalState.NONE -> {

                }
                else -> {
                }

            }
            if (isQuizEnded){
                CommonModal(
                    onDismissRequest = {isQuizEnded = false},
                    titleText= "퀴즈를 종료할까요?",
                    confirmText= "종료",
                    onConfirm= {
                        if (!isExitInProgress) {
                            isExitInProgress = true
                            viewModel.leaveSession()
                            if (currentRound > 3) {
                                navController.navigate("main?origin=quiz&isFinished=true") {
                                    popUpTo("quiz") { inclusive = true }
                                }

                            } else {
                                navController.navigate("main") {
                                    popUpTo("quiz") { inclusive = true }
                                }
                            }
                        }
                    },
                )
            }

            if(isQuizDisconnected) {
                CommonPopup(
                    titleText = "부모님이 방을 나갔어요.",
                    onDismissRequest = {
                        viewModel.leaveSession()
                        if (currentRound > 3) {
                            navController.navigate("main?origin=quiz&isFinished=true") {
                                popUpTo("quiz") { inclusive = true }
                            }

                        } else {
                            navController.navigate("main") {
                                popUpTo("quiz") { inclusive = true }
                            }
                        }
                    }
                )
            }
            if(isQuizStartEnabled && !isQuizAlertVisible) {
                playButtonSound(context, R.raw.quiz_start)
                CommonPopup(
                    titleText = "이제 그림 퀴즈를\n시작할 수 있어요!",
                    onDismissRequest = {
                        isQuizAlertVisible = true
                    }
                )
            }
        }
    }
    else {
        Log.d("QuizViewModel", "loading ")
    }

}

