import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var isDrawingMode by remember { mutableStateOf(true) }
    val bitmapWidth = 2000 // 원하는 비트맵 너비
    val bitmapHeight = 1500 // 원하는 비트맵 높이

    // 각 페이지마다 개별적인 비트맵 리스트 생성
    val bitmapsList = remember {
        mutableStateListOf(
            Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888),
            Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        )
    }

    // 전체 화면 구성 (좌우 레이아웃)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // 왼쪽: 풀페이지 스크롤 가능한 그림판 공간
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
        val pathsList = remember { mutableStateListOf<MutableList<Path>>() }
        if (pathsList.size < 2) {
            pathsList.add(mutableListOf())
            pathsList.add(mutableListOf())
        }

        Box(
            modifier = Modifier
                .fillMaxHeight(0.88f)
                .aspectRatio(16f / 11f)
                .align(Alignment.Bottom)
        ) {
            // 배경 이미지 설정
            Image(
                painter = painterResource(id = R.drawable.diary_box),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(29f / 20f)
            )

            // 페이지 컨텐츠 추가 (그림 그리기 또는 스크롤 모드)
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .align(Alignment.Center)
            ) { page ->
                var path by remember { mutableStateOf(Path()) }
                val paths = pathsList[page]
                val currentBitmap = bitmapsList[page] // 각 페이지마다 다른 비트맵 사용

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                        .pointerInput(isDrawingMode) {
                            if (isDrawingMode) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        path = Path().apply { moveTo(offset.x, offset.y) }
                                    },
                                    onDrag = { change, _ ->
                                        path.lineTo(change.position.x, change.position.y)
                                        paths.add(Path().apply { addPath(path) })

                                        // 각 페이지 비트맵에 그림 추가
                                        val canvas = AndroidCanvas(currentBitmap)
                                        canvas.drawPath(
                                            path.asAndroidPath(),
                                            android.graphics.Paint().apply {
                                                color = android.graphics.Color.BLACK
                                                strokeWidth = 5f
                                                style = android.graphics.Paint.Style.STROKE
                                                strokeCap = android.graphics.Paint.Cap.ROUND
                                            }
                                        )
                                    },
                                    onDragEnd = {
                                        paths.add(path)
                                        path = Path()
                                    }
                                )
                            }
                        }
                ) {
                    // 배경 이미지
                    Image(
                        painter = painterResource(id = if (page == 0) R.drawable.draw_template else R.drawable.write_template),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 그림 그리기 캔버스
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawBitmap(currentBitmap, 0f, 0f, null)
                        }
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 5f, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }

        // 오른쪽: 고정된 도구 공간 및 모드 전환 버튼
        Column(
            modifier = Modifier
                .fillMaxHeight(0.88f)
                .fillMaxWidth()
                .align(Alignment.Bottom)
                .padding(start = 16.dp)
                .background(Color(0xFFFAF8EF)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 모드 전환 버튼
            Button(onClick = { isDrawingMode = !isDrawingMode }) {
                Text(if (isDrawingMode) "스크롤 모드로 전환" else "그리기 모드로 전환")
            }

            // 완료 버튼
            Button(onClick = {
                saveBitmapToFile(bitmapsList[pagerState.currentPage], File("/path/to/save/drawing_${pagerState.currentPage}.png"))
            }) {
                Text("완료 및 저장")
            }
        }
    }
}

// 비트맵 저장 함수
fun saveBitmapToFile(bitmap: Bitmap, file: File) {
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
}
