package com.example.diarytablet.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.ui.theme.DeepPastelBlue
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.PastelSkyBlue
import kotlinx.coroutines.launch
import kotlin.io.path.Path
import kotlin.io.path.moveTo

@Composable
fun WordTap(
    modifier: Modifier = Modifier,
    wordList: List<WordResponseDto>,
    onValidate: (Context, Bitmap, Bitmap) -> Unit,
    onFinish: (List<WordRequestDto>) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    var finishedIndex by remember { mutableIntStateOf(-1) }

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var writtenBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var textSize by remember { mutableStateOf(IntSize.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = modifier.fillMaxSize()) {



    LazyRow (
        state = listState,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(start = 250.dp, end = 250.dp)

    ){
        itemsIndexed(wordList) { index, word ->
    Box(
        modifier = modifier
            .width(780.dp)
            .height(510.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.word_container),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(word.imageUrl)
//                    .build(),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth(0.5f)
//                    .aspectRatio(1f)
//                contentScale = ContentScale.Crop
//            )
            if (listState.firstVisibleItemIndex == index-1) {
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
                Image(
                    painter = painterResource(id = R.drawable.big_jogae),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.6f) // 크기 조정
                        .aspectRatio(1f)
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
                            .padding(30.dp)
                            .border(2.dp, color = Color.Gray)
                            .wrapContentSize()
//                            .captureToBitmap { bitmap -> originalBitmap = bitmap }
                    ) {
                        Text(
                            text = word.word,
                        )
                    }
                    DrawCanvas(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(100.dp)
//                            .captureToBitmap { bitmap -> writtenBitmap = bitmap }
                    )

                    Spacer(
                        modifier = Modifier
                            .height(30.dp)
                    )
                    BasicButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index + 1)
                            }
//                            originalBitmap?.let { orig ->
//                                writtenBitmap?.let { written ->
//                                    onValidate(context, orig, written)
//                                }
//                            }
                        },

                        text = "제출",
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
fun DrawCanvas(modifier: Modifier = Modifier) {
    val path = remember { androidx.compose.ui.graphics.Path() }
    var lastX by remember { mutableStateOf(0f) }
    var lastY by remember { mutableStateOf(0f) }

    var isDrawing by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(100.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // 드래그 시작 시 초기 위치 설정
                        path.moveTo(offset.x, offset.y)
                        lastX = offset.x
                        lastY = offset.y
                        isDrawing = !isDrawing // 트리거를 반전시켜 다시 그리기
                    },
                    onDrag = { change, _ ->
                        val (x, y) = change.position
                        // 경계 내에서만 경로를 업데이트
                        if (x in 0f..size.width.toFloat() && y in 0f..size.height.toFloat()) {
                            path.lineTo(x, y)
                            lastX = x
                            lastY = y
                            isDrawing = !isDrawing // 경로가 변경되었을 때마다 다시 그리기 트리거 업데이트
                            change.consume() // 이벤트 소모
                        }
                    }
                )
            }
            .border(1.dp, shape = RectangleShape, color = Color.Gray)  // 경계선
    ) {
        // 그리는 과정을 실시간으로 보여주기 위해 Canvas에 경로 그리기
        drawPath(
            path = path,
            color = Color.Black,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
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