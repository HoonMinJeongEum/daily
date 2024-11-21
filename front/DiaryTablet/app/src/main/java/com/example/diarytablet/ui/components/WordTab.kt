package com.example.diarytablet.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.model.ToolType
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.ui.theme.DeepPastelBlue
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.PastelSkyBlue
import com.example.diarytablet.utils.playButtonSound
import createPaintForTool
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WordTap(
    modifier: Modifier = Modifier,
    wordList: List<WordResponseDto>,
    onValidate: suspend (Context, WordResponseDto, Bitmap) -> Int,
    onFinish: suspend () -> Unit,
    learnedWordList: List<WordRequestDto>,
    navController: NavController,
    username: String
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        var currentIndex by remember { mutableIntStateOf(0) }
        var finishedIndex by remember { mutableIntStateOf(-1) }
        var canvasWidth by remember { mutableStateOf(780) }
        var canvasHeight by remember { mutableStateOf(510) }
        var showPopup by remember { mutableStateOf(false) }
        var isFalsePopup by remember { mutableStateOf(false) }
        // 단어마다 독립적인 Bitmap 생성
        val writtenBitmaps = remember(wordList) {
            wordList.map { word ->
                val characterCount = word.word.length
                val targetWidth = characterCount * 220 // 글자 수에 따라 너비 조정
                val targetHeight = 210 // 고정 높이 또는 필요에 따라 조정

                Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888).also {
                    Log.d("BitmapCreation", "Created Bitmap for word '${word.word}' with width: $targetWidth, height: $targetHeight")
                }
            }
        }.toMutableList()

        val context = LocalContext.current
        val isDrawingMode by remember { mutableStateOf(true) }
        var buttonText by remember { mutableStateOf("제출") }
        var buttonColor by remember { mutableStateOf(Color.White) }
        var initialized by remember { mutableStateOf(false) }
        var popupAlpha by remember { mutableStateOf(1f) }

        fun clearCanvas() {
            writtenBitmaps[currentIndex].eraseColor(android.graphics.Color.TRANSPARENT)
        }

        val popupMessages = listOf(
            "{}~\n잘하고 있어!",
            "{}~\n조금만 더 힘내!",
            "{}~\n정말 잘했어!",
            "{}~\n아주 훌륭해!",
            "{}~\n넌 단어 마스터!",
            "{}~\n집중력 짱!",
            "{}~\n끝까지 해보자!",
            "{}~\n최고야 ~",
            "{}~\n노력하는 모습이 멋져!",
            "{}~\n글씨를 참 잘 적구나",
            "{}~\n계속 이렇게 해보자!"
        )

        var popupMessage by remember { mutableStateOf("") }
        var isButtonEnabled by remember { mutableStateOf(true) } // 버튼 활성화 상태 변수 추가
        // 버튼 클릭 이벤트 처리
        fun onButtonClick() {
            if (isButtonEnabled && currentIndex == finishedIndex + 1 && !isCanvasEmpty(writtenBitmaps[currentIndex])) {
                isButtonEnabled = false // 버튼 비활성화로 설정
                buttonText = "보내는 중..."

                coroutineScope.launch {
                    val statusCode = onValidate(context, wordList[currentIndex], writtenBitmaps[currentIndex])
                    Log.d("wordTap", "success $statusCode")

                    when (statusCode) {
                        200 -> {
                            playButtonSound(context,R.raw.word_pass )
                            finishedIndex = currentIndex
                            buttonText = "제출"
                            buttonColor = Color.White
                            initialized = false
                            if (finishedIndex == 2) {
                                onFinish()
                                navController.navigate("main?origin=wordLearning&isFinished=true") {
                                    popUpTo("wordLearning") { inclusive = true }
                                }
                            } else {
                                listState.animateScrollToItem(++currentIndex)
                                showPopup = true // Show popup on transition
                            }
                        }
                        400, 422 -> {
                            playButtonSound(context,R.raw.word_fail )
                            buttonText = "다시제출"
                            buttonColor = Color(0xFFD27979) // Hex color D27979
                            popupMessage = "틀렸어요! 다시 시도해 보세요."
                            isFalsePopup = true
                            showPopup = true
                        }
                        else -> {
                            playButtonSound(context,R.raw.word_fail )
                            buttonText = "다시제출"
                            buttonColor = Color(0xFFD27979)
                            popupMessage = "틀렸어요! 다시 시도해 보세요."
                            isFalsePopup = true
                            showPopup = true
                        }
                    }
                    clearCanvas()
                    isButtonEnabled = true
                }
            }
        }

        Column(modifier = modifier.fillMaxSize()) {

            if (showPopup) {
                // alpha 애니메이션 추가
                val alpha by animateFloatAsState(
                    targetValue = popupAlpha,
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                )

                Popup(
                    alignment = Alignment.BottomStart,
                    onDismissRequest = {},
                    properties = PopupProperties(focusable = false, dismissOnBackPress = false)
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(x = -screenWidth * 0.05f)
                            .padding(bottom = screenHeight * 0.05f)
                            .graphicsLayer(alpha = alpha)
                    ) {
                        Image(
                            painter = if (!isFalsePopup) painterResource(id = R.drawable.main_char) else painterResource(id = R.drawable.main_char3),
                            contentDescription = "Character",
                            modifier = Modifier.width(screenWidth * 0.3f).aspectRatio(1.67f)
                        )

                        Box(
                            modifier = Modifier.width(screenWidth * 0.3f).aspectRatio(2.5f)
                                .offset(x = -screenWidth * 0.08f)
                                .padding(bottom = screenHeight * 0.05f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.popup_balloon),
                                contentDescription = "Text Balloon",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = screenWidth * 0.05f, vertical = screenHeight * 0.02f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = popupMessage,
                                    color = DarkGray,
                                    fontSize = 24.sp,
                                    lineHeight = 30.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                LaunchedEffect(showPopup) {
                    if (!isFalsePopup) {
                        popupMessage = popupMessages.random().replace("{}", username)
                    }
                    delay(2000)
                    popupAlpha = 0f  // 투명도 점진적 감소 시작
                    delay(1000)  // 페이드 아웃 애니메이션 지속 시간
                    showPopup = false  // 완료 후 팝업 종료
                    isFalsePopup = false
                    popupAlpha = 1f
                }
            }

            LazyRow(
                state = listState,
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.09f),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(start = screenWidth * 0.18f, end = screenWidth * 0.18f),
                userScrollEnabled = false
            ) {
                itemsIndexed(wordList) { index, word ->
                    Box(
                        modifier = modifier.width(screenWidth * 0.64f).height(screenHeight * 0.8f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.word_container),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 오른쪽 화살표
                            if (listState.firstVisibleItemIndex == index - 1 && currentIndex != finishedIndex + 1) {
                                Image(
                                    painter = painterResource(id = R.drawable.right_arrow),
                                    contentDescription = null,
                                    modifier = Modifier.size(screenHeight * 0.13f)
                                        .padding(start = screenWidth * 0.03f)
                                        .clickable {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(++currentIndex)
                                                if (currentIndex > finishedIndex) {
                                                    finishedIndex = currentIndex - 1
                                                }
                                            }
                                        }
                                )
                            }

                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(word.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds
                                )
                            }

                            Box(
                                modifier = Modifier.weight(1f).padding(end = screenWidth * 0.05f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(
                                        modifier = Modifier.wrapContentSize()
                                            .onSizeChanged { size ->
                                                val characterCount = wordList[currentIndex].word.length
                                                val targetWidth = characterCount * 220
                                                val targetHeight = 210

                                                if (canvasWidth != targetWidth || canvasHeight != targetHeight) {
                                                    canvasWidth = targetWidth
                                                    canvasHeight = targetHeight

                                                    // 현재 인덱스의 비트맵을 새 크기로 재생성
                                                    writtenBitmaps[currentIndex] = Bitmap.createBitmap(
                                                        targetWidth,
                                                        targetHeight,
                                                        Bitmap.Config.ARGB_8888
                                                    ).also {
                                                        Log.d("BitmapUpdate", "Updated Bitmap for index $currentIndex with width: $targetWidth, height: $targetHeight")
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    )
                                    {
                                        val characters = wordList[currentIndex].word.chunked(1)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.wrapContentSize()
                                        ) {
                                            characters.forEach { character ->
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.size(screenHeight * 0.18f)
                                                        .border(screenHeight * 0.01f, Color.LightGray)
                                                ) {
                                                    Text(
                                                        text = character,
                                                        fontSize = (screenHeight.value * 0.13f).sp,
                                                        style = MyTypography.bodyLarge, color = Color.LightGray
                                                    )
                                                }
                                            }
                                        }
                                        DrawCanvas(
                                            modifier = Modifier.matchParentSize().align(Alignment.Center),
                                            currentBitmap = writtenBitmaps[currentIndex],
                                            isDrawingMode = isDrawingMode,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(screenHeight * 0.1f))
                                    BasicButton(
                                        onClick = { onButtonClick() }, // onClick에 위에서 정의한 함수 연결
                                        modifier = Modifier.clickable(enabled = currentIndex == finishedIndex + 1) {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(currentIndex)
                                            }
                                        },
                                        text = if (currentIndex != finishedIndex + 1) "완료" else buttonText,
                                        ButtonColor = if (currentIndex == finishedIndex + 1 && isButtonEnabled) buttonColor else Color.Gray, // 비활성화 시 색상 변경
                                        imageResId = 11
                                    )
                                }
                            }
                            // 왼쪽 화살표
                            if (listState.firstVisibleItemIndex == index + 1) {
                                Image(
                                    painter = painterResource(id = R.drawable.left_arrow),
                                    contentDescription = null,
                                    modifier = Modifier.size(screenHeight * 0.13f)
                                        .padding(end = screenWidth * 0.03f)
                                        .clickable {
                                            coroutineScope.launch {
                                                listState.animateScrollToItem(--currentIndex)
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }

            ProgressBar(
                currentIndex = currentIndex,
                total = wordList.size,
                finishedIndex = finishedIndex,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }
    }
}


@Composable
fun DrawCanvas(
    modifier: Modifier = Modifier,
    currentBitmap: Bitmap,
    isDrawingMode: Boolean
) {
    val context = LocalContext.current
    val path = remember { androidx.compose.ui.graphics.Path() }
    val imageBitmap by rememberUpdatedState(currentBitmap.asImageBitmap())

    // 상태 관리: 펜 연결 여부
    val isStylusConnected = remember { mutableStateOf(checkStylusConnection(context)) }

    LaunchedEffect(Unit) {
        // 지속적으로 펜 연결 상태를 확인
        while (true) {
            isStylusConnected.value = checkStylusConnection(context)
            kotlinx.coroutines.delay(2000) // 1초마다 확인
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(isDrawingMode && (!isStylusConnected.value || isStylusConnected.value)) {
                if (isDrawingMode) {
                    awaitPointerEventScope {
                        while (true) {
                            val downEvent = awaitPointerEvent()
                            val position = downEvent.changes.first().position
                            val inputType = downEvent.changes.first().type

                            if (isStylusConnected.value && inputType != PointerType.Stylus) {
                                // 펜이 연결된 경우, 터치 입력 무시
                                continue
                            }

                            path.moveTo(position.x, position.y)

                            val canvas = android.graphics.Canvas(currentBitmap)
                            val paint = createPaintForTool(
                                ToolType.PENCIL,
                                Color.Black,
                                9f
                            )

                            var currentPosition = position
                            while (true) {
                                val dragEvent = awaitPointerEvent()
                                val dragChange = dragEvent.changes.first()

                                if (dragChange.pressed) {
                                    currentPosition = dragChange.position
                                    path.lineTo(currentPosition.x, currentPosition.y)
                                    canvas.drawPath(path.asAndroidPath(), paint)
                                } else {
                                    break
                                }
                            }

                            path.reset()
                        }
                    }
                }
            }
    ) {
        drawIntoCanvas { canvas ->
            canvas.drawImage(imageBitmap, Offset(0f, 0f), Paint())
        }
    }
}


fun checkStylusConnection(context: Context): Boolean {
    val inputManager = context.getSystemService(Context.INPUT_SERVICE) as android.hardware.input.InputManager
    val deviceIds = inputManager.inputDeviceIds

    if (isRunningOnEmulator()) {
        return false
    }

    for (deviceId in deviceIds) {
        val inputDevice = inputManager.getInputDevice(deviceId)
        if (inputDevice != null && inputDevice.sources and android.view.InputDevice.SOURCE_STYLUS == android.view.InputDevice.SOURCE_STYLUS) {
            return true // 스타일러스 입력 장치가 연결됨
        }
    }
    return false // 스타일러스 입력 장치가 없음
}


fun isRunningOnEmulator(): Boolean {
    val fingerprint = android.os.Build.FINGERPRINT
    val model = android.os.Build.MODEL
    val product = android.os.Build.PRODUCT
    val manufacturer = android.os.Build.MANUFACTURER

    return (fingerprint.contains("generic") || fingerprint.contains("emulator") || fingerprint.contains("sdk_gphone")) ||
            (model.contains("Emulator") || model.contains("Android SDK built for x86")) ||
            (product.contains("sdk_gphone") || product.contains("sdk") || product.contains("google_sdk")) ||
            manufacturer.contains("Genymotion")
}




fun isCanvasEmpty(bitmap: Bitmap): Boolean {
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    return pixels.all { it == android.graphics.Color.TRANSPARENT }
}


@Composable
fun ProgressBar(finishedIndex: Int, currentIndex:Int, total: Int, screenWidth : Dp, screenHeight : Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = screenHeight * 0.01f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until total) {
            val color = if (index == currentIndex) PastelNavy  else if (index <= finishedIndex) DeepPastelBlue else PastelSkyBlue
            val barColor = if (index <= finishedIndex) MaterialTheme.colorScheme.primary else Color.Gray
            if (index != 0) {
                Spacer(
                    modifier = Modifier
                        .width(screenWidth * 0.07f)
                        .height(screenHeight * 0.01f)
                        .background(barColor)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(screenHeight * 0.08f)
                    .background(color, shape = CircleShape)
            ) {
                if (index <= finishedIndex) {
                    Image(
                        painter = painterResource(id = R.drawable.check_mark),
                        contentDescription = null,
                        modifier = Modifier
                            .size(screenHeight * 0.06f)
                    )
                }
            }
        }
    }
}