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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.StrokeCap
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var isDrawingMode by remember { mutableStateOf(true) }

    // 전체 화면 구성 (좌우 레이아웃)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // 왼쪽: 풀페이지 스크롤 가능한 그림판 공간
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
        Box(
            modifier = Modifier
                .fillMaxHeight(0.88f)
                .aspectRatio(16f / 11f)
                .align(Alignment.Bottom) // 아래쪽 정렬 유지
        ) {
            // 배경 이미지 설정
            Image(
                painter = painterResource(id = R.drawable.diary_box),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(29f/20f)
            )

            // 페이지 컨텐츠 추가 (그림 그리기 or 스크롤 모드)
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .align(Alignment.Center)
            ) { page ->
                // 각 페이지에 배경 이미지 설정
                val backgroundImage = if (page == 0) {
                    R.drawable.draw_template
                } else {
                    R.drawable.write_template
                }

                // 그림 그리기 기능 추가
                var path by remember { mutableStateOf(Path()) }
                val paths = remember { mutableStateListOf<Path>() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds() // 이미지 부분에만 그리기 제한
                        .padding(
                            top = if (page == 0) 16.dp else 0.dp,
                            bottom = if (page == 0) 0.dp else 16.dp
                        )
                        .pointerInput(isDrawingMode) {
                            if (isDrawingMode) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        if (offset.x in 0f..size.width.toFloat() && offset.y in 0f..size.height.toFloat()) {
                                            path = Path().apply { moveTo(offset.x, offset.y) }
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        if (change.position.x in 0f..size.width.toFloat() && change.position.y in 0f..size.height.toFloat()) {
                                            path.lineTo(change.position.x, change.position.y)
                                        }
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
                        painter = painterResource(id = backgroundImage),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 그림 그리기 캔버스 (이미지와 동일한 크기로 제한)
                    Canvas(modifier = Modifier
                        .fillMaxSize()
                    ) {
                        // 저장된 모든 경로 그리기
                        paths.forEach { drawPath ->
                            drawPath(
                                path = drawPath,
                                color = Color.Black,
                                style = Stroke(width = 5f, cap = StrokeCap.Round)
                            )
                        }

                        // 현재 그리고 있는 경로 그리기
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
                .align(Alignment.Bottom) // 아래쪽 정렬 유지
                .padding(start = 16.dp)
                .background(Color(0xFFFAF8EF)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 모드 전환 버튼
            Button(onClick = { isDrawingMode = !isDrawingMode }) {
                Text(if (isDrawingMode) "스크롤 모드로 전환" else "그리기 모드로 전환")
            }

            // 도구들 추가 예정 (팔레트 등)
            Text("팔레트 도구 공간", color = Color.Gray)
            Button(onClick = { /* 완료 버튼 클릭 처리 */ }) {
                Text("완료")
            }
        }
    }
}
