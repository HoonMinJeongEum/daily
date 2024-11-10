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
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Dp
import android.graphics.Path as AndroidPath


@Composable
fun DiaryScreen(
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.DEFAULT,
    diaryViewModel: DiaryViewModel = hiltViewModel()
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val context = LocalContext.current
    var isDrawingMode by remember { mutableStateOf(true) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(5f) }
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }
    var isPreviewDialogVisible by remember { mutableStateOf(false) } // 모달 표시 여부

    // 현재 기기의 화면 크기 가져오기
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

    // 비트맵 생성 (Canvas와 동일한 크기)
    val bitmapsList = remember {
        mutableStateListOf(
            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888),
            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val firstPageDrawingSteps = remember { mutableStateListOf<DrawingStep>() }

    // 이미지 저장 및 업로드 트리거 함수
    suspend fun saveAndUploadImages() {
        val imageFiles = savePageImagesWithTemplate(bitmapsList, context)
        if (imageFiles.size >= 2) {
            val drawUri = Uri.fromFile(imageFiles[0])
            val writeUri = Uri.fromFile(imageFiles[1])
            diaryViewModel.uploadDiary(context, drawUri, writeUri)
        } else {
            Log.e("DiaryScreen", "Failed to save images for upload.")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .width(contentWidth)
                .height(boxHeight),
            horizontalArrangement = Arrangement.spacedBy(padding)
        ) {
            // 좌측 박스 (그림판)
            Box(
                modifier = Modifier
                    .width(leftBoxWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
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
                    onToolSelect = { tool -> selectedTool = tool }
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




// Path의 bounds를 가져와 문자열로 변환하여 로그로 출력
//fun pathBoundsToString(path: AndroidPath): String {
//    val bounds = android.graphics.RectF()
//    path.computeBounds(bounds, true)
//    return "Left: ${bounds.left}, Top: ${bounds.top}, Right: ${bounds.right}, Bottom: ${bounds.bottom}"
//}

































        data class DrawingStep(val path: Path, val color: Color, val thickness: Float)



suspend fun savePageImagesWithTemplate(bitmapsList: List<Bitmap>, context: Context): List<File> {
    return withContext(Dispatchers.IO) {
        bitmapsList.mapIndexed { index, drawingBitmap ->
            // 박스 배경, 템플릿, 그림판의 크기를 동일하게 설정
            val targetWidth = 2000
            val targetHeight = 1500

            // 박스 배경 이미지 불러와서 크기 조정
            val boxBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.diary_box)
            val resizedBoxBitmap = Bitmap.createScaledBitmap(boxBitmap, targetWidth, targetHeight, true)

            // 템플릿 이미지 불러와서 크기 조정
            val templateBitmap = if (index == 0) {
                BitmapFactory.decodeResource(context.resources, R.drawable.draw_template)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.write_template)
            }
            val resizedTemplateBitmap = Bitmap.createScaledBitmap(templateBitmap, targetWidth, targetHeight, true)

            // 그림판 이미지 크기 조정
            val resizedDrawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, targetWidth, targetHeight, true)

            // 같은 크기의 새로운 비트맵 생성
            val combinedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = AndroidCanvas(combinedBitmap)

            // 박스 이미지 그리기
            canvas.drawBitmap(resizedBoxBitmap, 0f, 0f, null)

            // 그림판 그리기
            canvas.drawBitmap(resizedDrawingBitmap, 0f, 0f, null)

            // 템플릿 그리기
            canvas.drawBitmap(resizedTemplateBitmap, 0f, 0f, null)

            // 이미지 크기 줄이기
            val finalBitmap = resizeBitmap(combinedBitmap, 1000, 750)

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

