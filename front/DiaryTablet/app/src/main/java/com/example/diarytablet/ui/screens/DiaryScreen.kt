@file:Suppress("DEPRECATION")

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
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.scanFile
import android.os.Environment
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.clickable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.rememberAsyncImagePainter
import com.arthenica.ffmpegkit.FFmpegKit
import java.io.IOException
import java.net.URL
import android.graphics.Path as AndroidPath

data class StickerItem(
    val bitmap: Bitmap,
    var position: MutableState<Offset> // MutableState로 변경
)

@Composable
fun DiaryScreen(
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.DEFAULT,
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    BackgroundPlacement(backgroundType = backgroundType)
    val userStickers by diaryViewModel.userStickers.observeAsState(emptyList())
    val firstPageStickers = remember { mutableStateListOf<StickerItem>() }

    LaunchedEffect(Unit) {
        diaryViewModel.fetchUserStickers()
    }


    val context = LocalContext.current
    var isDrawingMode by remember { mutableStateOf(true) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(5f) }
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }
    var isPreviewDialogVisible by remember { mutableStateOf(false) } // 모달 표시 여부

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val padding = 16.dp
    val contentWidth = screenWidthDp - (padding * 2)
    val contentHeight = screenHeightDp - (padding * 2)
    val leftBoxWidth = contentWidth * 0.75f
    val boxHeight = contentHeight * 0.88f

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
            boxHeight = boxHeight,
            firstPageStickers = firstPageStickers
        )
        if (imageFiles.size >= 2) {
            val drawUri = Uri.fromFile(imageFiles[0])
            val writeUri = Uri.fromFile(imageFiles[1])
            diaryViewModel.uploadDiary(context, drawUri, writeUri)
        } else {
            Log.e("DiaryScreen", "Failed to save images for upload.")
        }
    }

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
            Box(
                modifier = Modifier
                    .width(leftBoxWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                            },
                            onDrag = { change, dragAmount ->
                                if (pagerState.currentPage == 0) {
                                    firstPageStickers.lastOrNull()?.let { lastSticker ->
                                        lastSticker.position.value += dragAmount
                                        change.consume()
                                    }
                                }
                            }
                        )
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
                                                            thickness = brushSize,
                                                            toolType = selectedTool
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
                            if (pagerState.currentPage == 0) {
                                firstPageStickers.forEach { stickerItem ->
                                    drawIntoCanvas { canvas ->
                                        canvas.nativeCanvas.drawBitmap(
                                            stickerItem.bitmap,
                                            stickerItem.position.value.x,
                                            stickerItem.position.value.y,
                                            null
                                        )
                                    }
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
                            val bitmap = loadBitmapFromUrl(sticker.img)
                            if (bitmap != null) {
                                firstPageStickers.add(StickerItem(bitmap, mutableStateOf(centerPosition)))
                            }
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
                        firstPageStickers = firstPageStickers,
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
    firstPageStickers: List<StickerItem>,
    context: Context,
    templateWidth: Int,
    templateHeight: Int
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }

    // 투명한 레이어 비트맵 생성
    val overlayBitmap = remember {
        Bitmap.createBitmap(templateWidth, templateHeight, Bitmap.Config.ARGB_8888)
    }
    val overlayCanvas = remember { AndroidCanvas(overlayBitmap) }


    LaunchedEffect(drawingSteps) {
        currentStepIndex = 0
        val totalSteps = drawingSteps.size
        while (currentStepIndex < totalSteps) {
            delay(2L)
            currentStepIndex += 5
        }
    }

    Box(
        modifier = Modifier
            .width(templateWidth.dp)
            .height(templateHeight.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.TopStart
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawIntoCanvas { canvas ->
                // 배경 템플릿 그리기
                val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
                val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, templateWidth, templateHeight, true)
                canvas.nativeCanvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)
            }

            // Path를 Overlay Canvas에 그림
            drawingSteps.take(currentStepIndex).forEach { step ->
                val paint = createPaintForTool(step.toolType, step.color, step.thickness)

                // 지우개일 경우 투명한 효과 적용
                if (step.toolType == ToolType.ERASER) {
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }

                // overlayCanvas에 Path 그리기
                overlayCanvas.drawPath(step.path.asAndroidPath(), paint)
            }

            // 최종적으로 Overlay Bitmap을 그리기
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(overlayBitmap, 0f, 0f, null)
            }
        }
    }
}




fun drawToBitmap(
    canvas: AndroidCanvas,
    path: Path,
    width: Int,
    height: Int,
    context: Context,
    firstPageStickers: List<StickerItem>,
    showStickers: Boolean
) {
    val backgroundPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

    val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
    val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, width, height, true)
    canvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)

    val paint = android.graphics.Paint().apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 5f
        color = android.graphics.Color.BLACK
    }
    canvas.drawPath(path.asAndroidPath(), paint)

    // 마지막 프레임에만 스티커 표시
    if (showStickers) {
        firstPageStickers.forEach { sticker ->
            canvas.drawBitmap(sticker.bitmap, sticker.position.value.x, sticker.position.value.y, null)
        }
    }
}

// Bitmap을 파일로 저장하는 함수
fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
    return try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        if (file.exists()) {
            Log.d("DiaryScreen", "File created successfully: ${file.absolutePath}")
            true
        } else {
            Log.e("DiaryScreen", "File creation failed: ${file.absolutePath}")
            false
        }
    } catch (e: Exception) {
        Log.e("DiaryScreen", "Error saving bitmap to file: ${e.message}")
        false
    }
}


fun createVideoFromFrames(context: Context, framesDir: File, outputFile: File) {
    // 파일 목록을 가져와 누락된 파일이 있는지 확인
    val frameFiles = framesDir.listFiles()?.filter { it.extension == "png" }
    if (frameFiles.isNullOrEmpty()) {
        Log.e("DrawingPlaybackView", "No frame files found, video creation aborted.")
        return
    }

    // 프레임 순서대로 파일이 있는지 확인
    val totalFrames = frameFiles.size
    for (i in 0 until totalFrames) {
        val frameFile = File(framesDir, "frame_$i.png")
        if (!frameFile.exists()) {
            Log.e("DrawingPlaybackView", "Missing frame file: ${frameFile.absolutePath}")
            return // 누락된 파일이 있으면 중단
        }
    }

    // 모든 프레임이 존재하면 FFmpeg 실행
    // framerate를 조절해보세요
    val command = "-y -framerate 30 -i ${framesDir.absolutePath}/frame_%d.png -c:v mpeg4 -qscale:v 2 -pix_fmt yuv420p ${outputFile.absolutePath}"
    FFmpegKit.executeAsync(command) { session ->
        if (session.returnCode.isValueSuccess) {
            Log.d("DrawingPlaybackView", "Video created successfully at: ${outputFile.absolutePath}")
            scanFile(context, outputFile)
        } else {
            Log.e("DrawingPlaybackView", "Failed to create video: ${session.output}")
        }
    }
}


// 미디어 스캔을 수행하여 갤러리에 파일 추가
fun scanFile(context: Context, videoFile: File) {
    MediaScannerConnection.scanFile(context, arrayOf(videoFile.absolutePath), null) { path, uri ->
        Log.d("DrawingPlaybackView", "Scanned $path:")
        Log.d("DrawingPlaybackView", "-> uri=$uri")
    }
}


data class DrawingStep(
    val path: Path,
    val color: Color,
    val thickness: Float,
    val toolType: ToolType // toolType 추가
)

suspend fun savePageImagesWithTemplate(
    bitmapsList: List<Bitmap>,
    context: Context,
    leftBoxWidth: Dp,
    boxHeight: Dp,
    firstPageStickers: List<StickerItem>,
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

            // 바깥 박스 배경용 흰색 Bitma
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
            val resizedDrawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, targetWidth, targetHeight, true)
            // 중앙 위치 계산
            val centerX = (outerBoxWidthPx - targetWidth + padding*4) / 2f
            val centerY = (outerBoxHeightPx - targetHeight + padding*4) / 2f
            // 바깥 박스의 중앙에 템플릿 및 비트맵을 그리기
            outerCanvas.drawBitmap(resizedTemplateBitmap, centerX, centerY, null)
            outerCanvas.drawBitmap(resizedDrawingBitmap, centerX, centerY, null)
            // 스티커 추가 (첫 번째 페이지에만 스티커 표시)
            if (index == 0) {
                firstPageStickers.forEach { sticker ->
                    val stickerX = centerX + sticker.position.value.x
                    val stickerY = centerY + sticker.position.value.y
                    outerCanvas.drawBitmap(sticker.bitmap, stickerX, stickerY, null)
                }
            }
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