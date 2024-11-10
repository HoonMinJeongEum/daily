package com.example.diarytablet.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.model.ToolType
import com.example.diarytablet.ui.theme.DeepPastelBlue
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.PastelSkyBlue
import createPaintForTool
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WordTap(
    modifier: Modifier = Modifier,
    wordList: List<WordResponseDto>,
    onValidate: suspend (Context, WordResponseDto, Bitmap) -> Int,
    onFinish:suspend () -> Unit,
    learnedWordList: List<WordRequestDto>,
    navController: NavController

) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    var finishedIndex by remember { mutableIntStateOf(-1) }

    var canvasWidth by remember { mutableStateOf(780) }
    var canvasHeight by remember { mutableStateOf(510) }
    var showPopup by remember { mutableStateOf(false) }

    // canvasWidth와 canvasHeight를 활용해 Bitmap을 생성
    var writtenBitmap by remember {
        mutableStateOf(
            Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
        )
    }
    val context = LocalContext.current
    val isDrawingMode by remember { mutableStateOf(true) }
    var buttonText by remember { mutableStateOf("제출") }
    var buttonColor by remember { mutableStateOf(Color.White) }
    var initialized by remember { mutableStateOf(false) }

    fun clearCanvas() {
        writtenBitmap.eraseColor(android.graphics.Color.TRANSPARENT)

    }

    if (showPopup) {
        LaunchedEffect(showPopup) {
            delay(3000)
            showPopup = false
        }
    }

    Column(modifier = modifier.fillMaxSize()) {

        // 3초 동안 보여주는 팝업 메시지
        if (showPopup) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Next word loaded!",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

    LazyRow (
        state = listState,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(start = 210.dp, end = 210.dp),
        userScrollEnabled = false
    ){
        itemsIndexed(wordList) { index, word ->

            Box(
        modifier = modifier
            .width(860.dp)
            .height(510.dp)
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

            if (listState.firstVisibleItemIndex == index-1 && currentIndex != finishedIndex+1) {
                Image(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .padding(start = 50.dp)
                        .clickable{
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(word.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 40.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize() // 크기 조정
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .onSizeChanged { size ->
                                Log.d("wordtest","$${index} ${word}")
                                val characterCount = wordList[currentIndex].word.length
                                val targetWidth = characterCount * 200
                                val targetHeight = 200

                                if (!initialized && ( canvasWidth != targetWidth || canvasHeight != targetHeight)) {
                                    canvasWidth = targetWidth
                                    canvasHeight = targetHeight
                                    writtenBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888).apply {
                                        eraseColor(android.graphics.Color.TRANSPARENT)
                                    }
                                    Log.d("sizeTest", "Updated Bitmap size: $canvasWidth x $canvasHeight")
                                    initialized = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // 각 글자를 개별 박스로 나누어 배치
                        val characters = wordList[currentIndex].word.chunked(1)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .wrapContentSize()
                        ) {
                            characters.forEach { character ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .border(4.dp, Color.LightGray)
                                ) {
                                    Text(
                                        text = character,
                                        fontSize = 70.sp,
                                        style = MaterialTheme.typography.bodyLarge, color = Color.LightGray
                                    )
                                }
                            }
                        }
                        DrawCanvas(
                            modifier = Modifier
                                .matchParentSize()
                                .align(Alignment.Center),
                            currentBitmap = writtenBitmap,
                            isDrawingMode = isDrawingMode,
                        )
                    }



                    Spacer(
                        modifier = Modifier
                            .height(50.dp)
                    )
                    BasicButton(
                        onClick = {
                            if (currentIndex == finishedIndex + 1) {
                                Log.d("gon", "Button clicked")
                                coroutineScope.launch {
                                    val statusCode = onValidate(context, wordList[currentIndex], writtenBitmap!!)
                                    Log.d("wordTap","success ${statusCode}")

                                    when (statusCode) {
                                        200 -> {
                                            // Success, proceed to the next item
                                            finishedIndex = currentIndex
                                            buttonText = "제출"
                                            buttonColor = Color.White
                                            initialized = false
                                            if (finishedIndex == 4) {
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
                                            // Change button to "다시제출" with the specified color
                                            buttonText = "다시제출"
                                            buttonColor = Color(0xFFD27979) // Hex color D27979
                                        }
                                        else -> {
                                            // Handle other errors if needed
                                        }
                                    }
                                    clearCanvas()
                                }
                            }
                            Log.d("wordTap","f ${finishedIndex} , c ${currentIndex}")
                        },
                        modifier = Modifier.clickable(enabled = currentIndex == finishedIndex + 1) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(currentIndex)
                            }
                        },
                        text = if (currentIndex != finishedIndex + 1) "완료" else buttonText,
                        ButtonColor = if (currentIndex != finishedIndex + 1) Color.Gray else buttonColor,
                        imageResId = 11
                    )

                }
            }
            if (listState.firstVisibleItemIndex == index+1) {
                Image(
                    painter = painterResource(id = R.drawable.left_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .padding(end = 50.dp)
                        .clickable{
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
            finishedIndex = finishedIndex
        )
    }
}

@Composable
fun DrawCanvas(
    modifier: Modifier = Modifier,
    currentBitmap: Bitmap,
    isDrawingMode: Boolean
) {
    val path = remember { androidx.compose.ui.graphics.Path() }
    var lastX by remember { mutableStateOf(0f) }
    var lastY by remember { mutableStateOf(0f) }

    // Convert Bitmap to ImageBitmap to update UI
    val imageBitmap by rememberUpdatedState(currentBitmap.asImageBitmap())

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(isDrawingMode) {
                if (isDrawingMode) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            path.reset()
                            path.moveTo(offset.x, offset.y)
                            lastX = offset.x
                            lastY = offset.y
                        },
                        onDrag = { change, _ ->
                            path.lineTo(change.position.x, change.position.y)

                            // Draw directly on the bitmap
                            val canvas = android.graphics.Canvas(currentBitmap)
                            val paint = createPaintForTool(
                                ToolType.PENCIL,
                                Color.Black,
                                9f
                            )
                            canvas.drawPath(path.asAndroidPath(), paint)
                        },
                        onDragEnd = {
                            path.reset()
                        }
                    )
                }
            }
    ) {
        drawIntoCanvas { canvas ->
            // Draw ImageBitmap converted from the updated Bitmap
            canvas.drawImage(imageBitmap, Offset(0f, 0f), Paint())
        }
    }
}








@Composable
fun ProgressBar(finishedIndex: Int,currentIndex:Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until total) {
            val color = if (index == currentIndex) PastelNavy  else if (index <= finishedIndex) DeepPastelBlue else PastelSkyBlue
            val barColor = if (index <= finishedIndex) MaterialTheme.colorScheme.primary else Color.Gray
            if (index != 0) {
                Spacer(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(barColor)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(color, shape = CircleShape)
            ) {
                if (index <= finishedIndex) {
                    Image(
                        painter = painterResource(id = R.drawable.check_mark),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }
    }
}