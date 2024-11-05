import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import com.example.diarytablet.R
import com.example.diarytablet.model.ToolType
import com.example.diarytablet.ui.PaletteTool
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class DrawingStep(val path: Path, val color: Color, val thickness: Float)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var isDrawingMode by remember { mutableStateOf(true) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableStateOf(5f) }
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }

    val bitmapWidth = 2000
    val bitmapHeight = 1500

    // 각 페이지마다 개별적인 비트맵 리스트 생성
    val bitmapsList = remember {
        mutableStateListOf(
            Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888),
            Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val context = LocalContext.current
    val drawingSteps = remember { mutableStateListOf<DrawingStep>() }
    val isPlaying = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // 왼쪽: 그림판 공간
        Box(
            modifier = Modifier
                .fillMaxHeight(0.88f)
                .aspectRatio(16f / 11f)
                .align(Alignment.Bottom)
        ) {
            // 가장 아래 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.diary_box),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(29f / 20f)
            )

            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .align(Alignment.Center)
            ) { page ->
                val currentBitmap = bitmapsList[page]
                var path by remember { mutableStateOf(Path()) }

                // 중간 레이어: 그림을 그리는 캔버스
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Canvas(
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
                                            drawingSteps.add(
                                                DrawingStep(
                                                    path = Path().apply { addPath(path) },
                                                    color = selectedColor,
                                                    thickness = brushSize
                                                )
                                            )
                                            val canvas = AndroidCanvas(currentBitmap)
                                            val paint = createPaintForTool(selectedTool, selectedColor, brushSize)
                                            canvas.drawPath(path.asAndroidPath(), paint)
                                        },
                                        onDragEnd = {
                                            path = Path()
                                        }
                                    )
                                }
                            }
                    ) {
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawBitmap(currentBitmap, 0f, 0f, null)
                        }
                        drawPath(
                            path = path,
                            color = selectedColor,
                            style = Stroke(width = brushSize, cap = StrokeCap.Round)
                        )
                    }
                    Image(
                        painter = painterResource(id = if (page == 0) R.drawable.draw_template else R.drawable.write_template),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
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
            PaletteTool(
                selectedTool = selectedTool,
                onColorChange = { color -> selectedColor = color },
                onThicknessChange = { thickness -> brushSize = thickness },
                onToolSelect = { tool -> selectedTool = tool }
            )
            Button(onClick = { isDrawingMode = !isDrawingMode }) {
                Text(if (isDrawingMode) "스크롤 모드로 전환" else "그리기 모드로 전환")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    savePageImagesWithTemplate(bitmapsList, pagerState.currentPage, context)
                }
                isPlaying.value = true
            }) {
                Text("완료 및 저장")
            }
            // 완료 후 재생되는 작은 미니 플레이어
            Box(
                modifier = Modifier
                    .size(150.dp) // 미니 플레이어 크기 설정
                    .background(Color.Gray)
                    .padding(8.dp)
            ) {
                if (isPlaying.value) {
                    DrawingPlaybackView(drawingSteps = drawingSteps)
                } else {
                    Text("완료 버튼을 눌러 재생 확인", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

// Paint 객체 생성 함수
fun createPaintForTool(toolType: ToolType, color: Color, strokeWidth: Float): AndroidPaint {
    return when (toolType) {
        ToolType.CRAYON -> createCrayonPaint(color, strokeWidth)
        ToolType.ERASER -> createEraserPaint(strokeWidth)
        ToolType.BRUSH -> createBrushPaint(color, strokeWidth)
        else -> createPencilPaint(color, strokeWidth)
    }
}

// PENCIL 도구용 Paint 생성 함수
fun createPencilPaint(color: Color, strokeWidth: Float): AndroidPaint {
    return AndroidPaint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = AndroidPaint.Style.STROKE
        this.strokeCap = AndroidPaint.Cap.ROUND
    }
}

// CRAYON 도구용 Paint 생성 함수 (일정한 투명도 유지)
fun createCrayonPaint(color: Color, strokeWidth: Float): AndroidPaint {
    return AndroidPaint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = AndroidPaint.Style.STROKE
        this.strokeCap = AndroidPaint.Cap.ROUND
        alpha = 100 // 일정한 투명도 유지

        // 크레파스 질감 생성
        val crayonBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(crayonBitmap)
        val paint = AndroidPaint().apply {
            this.color = color.toArgb()
            alpha = 50
        }

        for (i in 0 until 500) {
            val x = (Math.random() * crayonBitmap.width).toFloat()
            val y = (Math.random() * crayonBitmap.height).toFloat()
            canvas.drawPoint(x, y, paint)
        }

        shader = BitmapShader(crayonBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }
}

// 붓 도구용 Paint 생성 함수
fun createBrushPaint(color: Color, strokeWidth: Float): AndroidPaint {
    return AndroidPaint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = AndroidPaint.Style.STROKE
        this.strokeCap = AndroidPaint.Cap.ROUND
    }
}

// 지우개 도구용 Paint 생성 함수 (투명하게 처리)
fun createEraserPaint(strokeWidth: Float): AndroidPaint {
    return AndroidPaint().apply {
        color = Color.Transparent.toArgb() // 투명으로 설정하여 지우기 효과
        this.strokeWidth = strokeWidth
        this.style = AndroidPaint.Style.STROKE
        this.strokeCap = AndroidPaint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // CLEAR 모드로 지우기 효과
    }
}

@Composable
fun DrawingPlaybackView(drawingSteps: List<DrawingStep>) {
    val currentStepIndex = remember { mutableStateOf(0) }
    val currentPath = remember { Path() }

    LaunchedEffect(Unit) {
        while (currentStepIndex.value < drawingSteps.size) {
            delay(100) // 각 단계가 그려지는 간격 (100ms)
            currentPath.addPath(drawingSteps[currentStepIndex.value].path)
            currentStepIndex.value++
        }
    }

    Canvas(modifier = Modifier.aspectRatio(16f/11f).size(150.dp)) {
        if (currentStepIndex.value < drawingSteps.size) {
            val step = drawingSteps[currentStepIndex.value.coerceAtMost(drawingSteps.size - 1)]
            drawPath(
                path = currentPath,
                color = step.color,
                style = Stroke(
                    width = step.thickness,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

// 템플릿과 그림을 결합하여 저장하는 함수
suspend fun savePageImagesWithTemplate(bitmapsList: List<Bitmap>, pageIndex: Int, context: Context) {
    withContext(Dispatchers.IO) {
        bitmapsList.forEachIndexed { index, drawingBitmap ->
            val templateBitmap = if (index == 0) {
                BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.write_template)
            }

            val combinedBitmap = Bitmap.createBitmap(drawingBitmap.width, drawingBitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = AndroidCanvas(combinedBitmap)

            canvas.drawBitmap(templateBitmap, 0f, 0f, null)
            canvas.drawBitmap(drawingBitmap, 0f, 0f, null)

            val file = File(context.filesDir, "drawing_combined_$index.png")
            FileOutputStream(file).use { out ->
                combinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
    }
}

