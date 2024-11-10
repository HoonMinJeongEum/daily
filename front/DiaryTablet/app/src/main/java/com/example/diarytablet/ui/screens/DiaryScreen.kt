import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.model.ToolType
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diarytablet.viewmodel.DiaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.graphics.Matrix
import androidx.compose.foundation.clickable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil3.compose.rememberAsyncImagePainter
import java.net.URL
import android.graphics.Path as AndroidPath


@Composable
fun DiaryScreen(
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.DEFAULT,
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    BackgroundPlacement(backgroundType = backgroundType)
    val userStickers by diaryViewModel.userStickers.observeAsState(emptyList())
    // 스티커 관련 상태
    var selectedStickerImage by remember { mutableStateOf<Bitmap?>(null) }
    var stickerPosition by remember { mutableStateOf(Offset.Zero) }


    LaunchedEffect(Unit) {
        diaryViewModel.fetchUserStickers()
    }


    val context = LocalContext.current
    var isDrawingMode by remember { mutableStateOf(true) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(5f) }
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }
    var isPreviewDialogVisible by remember { mutableStateOf(false) } // 모달 표시 여부

    // 화면 크기 및 레이아웃 설정
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val padding = 16.dp
    val contentWidth = screenWidthDp - (padding * 2)
    val contentHeight = screenHeightDp - (padding * 2)
    val leftBoxWidth = contentWidth * 0.75f
    val boxHeight = contentHeight * 0.88f

    // Dp 단위를 픽셀로 변환
    val density = LocalDensity.current
    val bitmapWidthPx = with(density) { leftBoxWidth.roundToPx() }
    val bitmapHeightPx = with(density) { boxHeight.roundToPx() }

    val bitmapsList = remember {
        mutableStateListOf(
            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888),
            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val firstPageDrawingSteps =
        remember { mutableStateListOf<DrawingStep>() } // 첫 번째 페이지의 DrawingSteps 저장

    // 이미지 저장 및 업로드 트리거 함수
    suspend fun saveAndUploadImages() {
        // leftBoxWidth와 boxHeight 값을 전달
        val imageFiles = savePageImagesWithTemplate(
            bitmapsList,
            context,
            leftBoxWidth = leftBoxWidth,
            boxHeight = boxHeight
        )
        if (imageFiles.size >= 2) {
            val drawUri = Uri.fromFile(imageFiles[0])
            val writeUri = Uri.fromFile(imageFiles[1])
            diaryViewModel.uploadDiary(context, drawUri, writeUri)
        } else {
            Log.e("DiaryScreen", "Failed to save images for upload.")
        }
    }

    // 스티커 중앙 위치 계산
    val centerPosition = Offset(
        x = with(density) { leftBoxWidth.toPx() / 2 },
        y = with(density) { boxHeight.toPx() / 2 }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // 상단에 제목과 뒤로 가기 버튼 배치
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.cute_back),
                contentDescription = "뒤로 가기 버튼",
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        navController.navigate("main") {
                            popUpTo("diary") { inclusive = true }
                        }
                    }
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "다이어리",
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
        }

        // 메인 콘텐츠 영역
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp),
            horizontalArrangement = Arrangement.spacedBy(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 박스 (그림판)
            Box(
                modifier = Modifier
                    .width(leftBoxWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            // 선택한 스티커 드래그 이동
                            stickerPosition += dragAmount
                            change.consume()
                        }
                    }
            ) {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) { page ->
                    val currentBitmap = bitmapsList[page]
                    val path = remember { mutableStateOf(Path()) }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clipToBounds()
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(leftBoxWidth, boxHeight)
                                .clipToBounds()
                                .pointerInput(isDrawingMode) {
                                    if (isDrawingMode) {
                                        detectDragGestures(
                                            onDragStart = { offset ->
                                                path.value =
                                                    Path().apply { moveTo(offset.x, offset.y) }
                                            },
                                            onDrag = { change, _ ->
                                                path.value.lineTo(
                                                    change.position.x,
                                                    change.position.y
                                                )

                                                // 비트맵에 즉시 그리기 반영
                                                val canvas = AndroidCanvas(currentBitmap)
                                                val paint = createPaintForTool(
                                                    selectedTool,
                                                    selectedColor,
                                                    brushSize
                                                )
                                                canvas.drawPath(path.value.asAndroidPath(), paint)

                                                // 첫 번째 페이지의 그리기 과정을 저장
                                                if (page == 0) {
                                                    firstPageDrawingSteps.add(
                                                        DrawingStep(
                                                            path = Path().apply { addPath(path.value) },
                                                            color = selectedColor,
                                                            thickness = brushSize
                                                        )
                                                    )
                                                }
                                            },
                                            onDragEnd = {
                                                path.value = Path() // 초기화
                                            }
                                        )
                                    }
                                }
                        ) {
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawBitmap(currentBitmap, 0f, 0f, null)
                            }

                            // 현재 경로를 Canvas에 실시간 반영
                            drawPath(
                                path = path.value,
                                color = selectedColor,
                                style = Stroke(width = brushSize, cap = StrokeCap.Round)
                            )
                            // 선택한 스티커 이미지를 Canvas에 그리기
                            selectedStickerImage?.let { stickerBitmap ->
                                drawIntoCanvas { canvas ->
                                    canvas.nativeCanvas.drawBitmap(
                                        stickerBitmap,
                                        stickerPosition.x,
                                        stickerPosition.y,
                                        null
                                    )
                                }
                            }
                        }

                        Image(
                            painter = painterResource(if (page == 0) R.drawable.draw_template else R.drawable.write_template),
                            contentDescription = null,
                            modifier = Modifier.size(leftBoxWidth, boxHeight)
                        )
                    }
                }
            }

            // 오른쪽: 팔레트 및 업로드 버튼
            Column(
                modifier = Modifier
                    .width(contentWidth * 0.25f)
                    .fillMaxHeight()
                    .background(Color.LightGray),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PaletteTool(
                    selectedTool = selectedTool,
                    selectedColor = selectedColor,
                    onColorChange = { selectedColor = it },
                    onThicknessChange = { brushSize = it },
                    onToolSelect = { selectedTool = it },
                    stickerList = userStickers,
                    onStickerSelect = { sticker ->
                        CoroutineScope(Dispatchers.IO).launch {
                            selectedStickerImage = loadBitmapFromUrl(sticker.img)
                            stickerPosition = centerPosition // 그림판 중앙에 스티커 배치
                        }
                    }
                )

                Button(onClick = { isDrawingMode = !isDrawingMode }) {
                    Text(if (isDrawingMode) "스크롤 모드로 전환" else "그리기 모드로 전환")
                }
                Button(onClick = { isPreviewDialogVisible = true }) {
                    Text("그리기 과정 미리보기")
                }
            }
        }
    }

    // 모달 대신 전체 화면 Box로 미리보기 보여주기
    if (isPreviewDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center // 화면의 중앙에 박스를 위치
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(0.9f) // 화면의 90% 크기로 설정
                    .background(Color.White, shape = RoundedCornerShape(16.dp)) // 흰색 배경 및 둥근 모서리 설정
                    .padding(16.dp), // 내부 여백 설정
                horizontalArrangement = Arrangement.SpaceBetween, // 좌우 여백 분리
                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
            ) {
                // 템플릿과 경로 표시하는 부분
                Box(
                    modifier = Modifier
                        .weight(0.8f) // 왼쪽 공간을 크게 설정
                        .fillMaxHeight()
                        .padding(end = 8.dp), // 오른쪽에 여백 추가
                    contentAlignment = Alignment.Center
                ) {
                    DrawingPlaybackView(
                        drawingSteps = firstPageDrawingSteps,
                        context = context,
                        templateWidth = with(LocalDensity.current) { (leftBoxWidth - padding * 2).toPx().toInt() },
                        templateHeight = with(LocalDensity.current) { (boxHeight - padding * 2).toPx().toInt() }
                    )
                }

                // 오른쪽에 취소 및 보내기 버튼
                Column(
                    modifier = Modifier
                        .weight(0.2f) // 오른쪽 공간을 작게 설정
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly // 세로로 균등 배치
                ) {
                    Button(onClick = { isPreviewDialogVisible = false }) {
                        Text("취소")
                    }
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                saveAndUploadImages() // API 호출
                            }
                            isPreviewDialogVisible = false
                        }
                    ) {
                        Text("보내기")
                    }
                }
            }
        }
    }
}

// URL에서 Bitmap 로드하는 함수
suspend fun loadBitmapFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        BitmapFactory.decodeStream(URL(url).openStream())
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Composable
fun DrawingPlaybackView(
    drawingSteps: List<DrawingStep>,
    context: Context,
    templateWidth: Int,
    templateHeight: Int
) {
    val currentPath = remember { Path() }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    // 애니메이션 효과로 경로를 재생
    LaunchedEffect(drawingSteps) {
        currentStepIndex = 0
        while (currentStepIndex < drawingSteps.size) {
            delay(10)
            currentPath.addPath(drawingSteps[currentStepIndex].path)
            currentStepIndex++
        }
    }

    Box(
        modifier = Modifier
            .width(templateWidth.dp)
            .height(templateHeight.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.TopStart // 좌상단을 기준으로 경로 시작점 설정
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 템플릿 이미지를 좌상단에 위치시켜 그리기
            drawIntoCanvas { canvas ->
                val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
                val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, templateWidth, templateHeight, true)
                canvas.nativeCanvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)
            }

            // 경로를 템플릿 좌상단에 맞게 그리기
            drawingSteps.take(currentStepIndex).forEach { step ->
                drawPath(
                    path = step.path,
                    color = step.color,
                    style = Stroke(width = step.thickness, cap = StrokeCap.Round)
                )
            }
        }
    }
}
data class DrawingStep(val path: Path, val color: Color, val thickness: Float)

suspend fun savePageImagesWithTemplate(
    bitmapsList: List<Bitmap>,
    context: Context,
    leftBoxWidth: Dp,
    boxHeight: Dp,
    padding: Int = 16 // 바깥 박스를 위한 추가 패딩
): List<File> {
    // Dp를 픽셀로 변환
    val density = context.resources.displayMetrics.density
    val outerBoxWidthPx = (leftBoxWidth.toPx(density) + padding * 2).toInt()
    val outerBoxHeightPx = (boxHeight.toPx(density) + padding * 2).toInt()

    return withContext(Dispatchers.IO) {
        bitmapsList.mapIndexed { index, drawingBitmap ->
            val targetWidth = outerBoxWidthPx - padding * 2
            val targetHeight = outerBoxHeightPx - padding * 2

            // 바깥 박스 배경용 흰색 Bitmap 생성
            val outerBoxBackground = Bitmap.createBitmap(outerBoxWidthPx, outerBoxHeightPx, Bitmap.Config.ARGB_8888)
            val outerCanvas = AndroidCanvas(outerBoxBackground)
            val paint = android.graphics.Paint().apply { color = android.graphics.Color.WHITE }
            outerCanvas.drawRect(0f, 0f, outerBoxWidthPx.toFloat(), outerBoxHeightPx.toFloat(), paint) // 흰색으로 칠하기

            // 템플릿 이미지 불러와서 크기 조정
            val templateBitmap = if (index == 0) {
                BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.write_template)
            }
            val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, targetWidth - padding * 4, targetHeight - padding * 4, true)

            // 그림판 이미지 크기 조정
            val resizedDrawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, targetWidth, targetHeight, true)

            // 중앙 위치 계산
            val centerX = (outerBoxWidthPx - targetWidth + padding*4) / 2f
            val centerY = (outerBoxHeightPx - targetHeight + padding*4) / 2f

            // 바깥 박스의 중앙에 템플릿 및 비트맵을 그리기
            outerCanvas.drawBitmap(resizedTemplateBitmap, centerX, centerY, null)
            outerCanvas.drawBitmap(resizedDrawingBitmap, centerX, centerY, null)

            // 이미지 크기 줄이기
            val finalBitmap = resizeBitmap(outerBoxBackground, 1450, 1000) // 저장 크기 설정 (필요시 조정 가능)

            // 압축하여 파일로 저장
            val file = File(context.filesDir, "drawing_combined_$index.jpg")
            compressBitmap(finalBitmap, file, quality = 50)

            if (file.exists()) {
                Log.d("DiaryScreen", "File created successfully: ${file.absolutePath}")
            } else {
                Log.e("DiaryScreen", "File creation failed: ${file.absolutePath}")
            }

            file // 파일 반환
        }
    }
}

// Dp를 픽셀로 변환하는 확장 함수
fun Dp.toPx(density: Float): Float = this.value * density

// 해상도를 조절하는 함수
fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

// 품질을 낮춰서 압축하는 함수
fun compressBitmap(bitmap: Bitmap, outputFile: File, quality: Int = 30) {
    FileOutputStream(outputFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    }
}