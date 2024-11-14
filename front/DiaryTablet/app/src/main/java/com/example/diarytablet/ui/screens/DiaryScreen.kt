//@file:Suppress("DEPRECATION")
//
//import android.graphics.Bitmap
//import android.net.Uri
//import android.util.Log
//import android.graphics.Canvas as AndroidCanvas
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.pager.VerticalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.asAndroidPath
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
//import androidx.compose.ui.graphics.nativeCanvas
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.diarytablet.R
//import com.example.diarytablet.model.ToolType
//import com.example.diarytablet.ui.theme.BackgroundPlacement
//import com.example.diarytablet.ui.theme.BackgroundType
//import kotlinx.coroutines.Dispatchers
//import java.io.File
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.diarytablet.viewmodel.DiaryViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import android.os.Environment
//import androidx.compose.foundation.clickable
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.text.style.TextAlign
//import com.example.diarytablet.utils.DrawingPlaybackView
//import com.example.diarytablet.utils.DrawingStep
//import com.example.diarytablet.utils.loadBitmapFromUrl
//import com.example.diarytablet.utils.savePageImagesWithTemplate
//
//data class StickerItem(
//    val bitmap: Bitmap,
//    var position: MutableState<Offset> // MutableState로 변경
//)
//
//@Composable
//fun DiaryScreen(
//    navController: NavController,
//    backgroundType: BackgroundType = BackgroundType.DEFAULT,
//    diaryViewModel: DiaryViewModel = hiltViewModel()
//) {
//    BackgroundPlacement(backgroundType = backgroundType)
//    val userStickers by diaryViewModel.userStickers.observeAsState(emptyList())
//    val firstPageStickers = remember { mutableStateListOf<StickerItem>() }
//    var selectedStickerIndex by remember { mutableStateOf<Int?>(null) }
//
//    LaunchedEffect(Unit) {
//        diaryViewModel.fetchUserStickers()
//    }
//
//
//    val context = LocalContext.current
//    var isDrawingMode by remember { mutableStateOf(true) }
//    var selectedColor by remember { mutableStateOf(Color.Black) }
//    var brushSize by remember { mutableFloatStateOf(5f) }
//    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }
//    var isPreviewDialogVisible by remember { mutableStateOf(false) } // 모달 표시 여부
//
//    val configuration = LocalConfiguration.current
//    val screenWidthDp = configuration.screenWidthDp.dp
//    val screenHeightDp = configuration.screenHeightDp.dp
//    val padding = 16.dp
//    val contentWidth = screenWidthDp - (padding * 2)
//    val contentHeight = screenHeightDp - (padding * 2)
//    val leftBoxWidth = contentWidth * 0.75f
//    val boxHeight = contentHeight * 0.88f
//
//    val density = LocalDensity.current
//    val bitmapWidthPx = with(density) { leftBoxWidth.roundToPx() }
//    val bitmapHeightPx = with(density) { boxHeight.roundToPx() }
//
//    val bitmapsList = remember {
//        mutableStateListOf(
//            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888),
//            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888)
//        )
//    }
//
//    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
//    val firstPageDrawingSteps =
//        remember { mutableStateListOf<DrawingStep>() } // 첫 번째 페이지의 DrawingSteps 저장
//
//    val undoStack = remember { mutableStateListOf<DrawingStep>() }
//    val redoStack = remember { mutableStateListOf<DrawingStep>() }
//    val redrawTrigger = remember { mutableStateOf(0) }
//
//    // undo와 redo 기능 수정
//    fun undo() {
//        if (firstPageDrawingSteps.isNotEmpty()) {
//            val lastStroke = firstPageDrawingSteps.removeLast()
//            redoStack.add(lastStroke) // redoStack에 저장
//            redrawTrigger.value++ // Trigger recomposition
//        } else {
//            Log.d("DiaryScreen", "Undo: No steps to undo.")
//        }
//    }
//
//    fun redo() {
//        if (redoStack.isNotEmpty()) {
//            val lastUndoneStroke = redoStack.removeLast() // redoStack에서 가져오기
//            firstPageDrawingSteps.add(lastUndoneStroke) // 다시 firstPageDrawingSteps에 추가
//            redrawTrigger.value++
//        } else {
//            Log.d("DiaryScreen", "Redo: No steps to redo.")
//        }
//    }
//
//    // 이미지 저장 및 업로드 트리거 함수
//    suspend fun saveAndUploadImages() {
//        // leftBoxWidth와 boxHeight 값을 전달
//        val imageFiles = savePageImagesWithTemplate(
//            bitmapsList,
//            context,
//            leftBoxWidth = leftBoxWidth,
//            boxHeight = boxHeight,
//            firstPageStickers = firstPageStickers
//        )
//        // 비디오 파일 저장 위치 설정 및 파일 생성
//        val videoFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "drawing_playback.mp4")
//        val videoUri = Uri.fromFile(videoFile) // 비디오 파일 URI 생성
//
//        if (imageFiles.size >= 2) {
//            val drawUri = Uri.fromFile(imageFiles[0])
//            val writeUri = Uri.fromFile(imageFiles[1])
//            diaryViewModel.uploadDiary(context, drawUri, writeUri, videoUri)
//        } else {
//            Log.e("DiaryScreen", "Failed to save images for upload.")
//        }
//    }
//
//
//
//    val centerPosition = Offset(
//        x = with(density) { leftBoxWidth.toPx() / 2 },
//        y = with(density) { boxHeight.toPx() / 2 }
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(padding)
//    ) {
//        // 상단에 제목과 뒤로 가기 버튼 배치
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 16.dp, top = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.cute_back),
//                contentDescription = "뒤로 가기 버튼",
//                modifier = Modifier
//                    .size(48.dp)
//                    .clickable {
//                        navController.navigate("main") {
//                            popUpTo("diary") { inclusive = true }
//                        }
//                    }
//            )
//            Spacer(modifier = Modifier.width(30.dp))
//            Text(
//                text = "그림일기",
//                fontSize = 32.sp,
//                color = Color.White,
//                textAlign = TextAlign.Start
//            )
//        }
//
//        // 메인 콘텐츠 영역
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 72.dp),
//            horizontalArrangement = Arrangement.spacedBy(padding),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box(
//                modifier = Modifier
//                    .width(leftBoxWidth)
//                    .fillMaxHeight()
//                    .clip(RoundedCornerShape(50.dp))
//                    .background(Color.White)
//                    .pointerInput(Unit) {
//                        detectDragGestures(
//                            onDragStart = {
//                            },
//                            onDrag = { change, dragAmount ->
//                                if (pagerState.currentPage == 0) {
//                                    firstPageStickers
//                                        .lastOrNull()
//                                        ?.let { lastSticker ->
//                                            lastSticker.position.value += dragAmount
//                                            change.consume()
//                                        }
//                                }
//                            }
//                        )
//                    }
//            ) {
//                VerticalPager(
//                    state = pagerState,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding)
//                ) { page ->
//                    val currentBitmap = bitmapsList[page]
//                    val path = remember { mutableStateOf(Path()) }
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clipToBounds()
//                    ) {
//                        Canvas(
//                            modifier = Modifier
//                                .size(leftBoxWidth, boxHeight)
//                                .clipToBounds()
//                                .pointerInput(isDrawingMode) {
//                                    if (isDrawingMode) {
//                                        detectDragGestures(
//                                            onDragStart = { offset ->
//                                                path.value =
//                                                    Path().apply { moveTo(offset.x, offset.y) }
//                                            },
//                                            onDrag = { change, _ ->
//                                                path.value.lineTo(
//                                                    change.position.x,
//                                                    change.position.y
//                                                )
//
//                                                // 비트맵에 즉시 그리기 반영
//                                                val canvas = AndroidCanvas(currentBitmap)
//                                                val paint = createPaintForTool(
//                                                    selectedTool,
//                                                    selectedColor,
//                                                    brushSize
//                                                )
//                                                canvas.drawPath(path.value.asAndroidPath(), paint)
//
//                                                // 첫 번째 페이지의 그리기 과정을 저장
//                                                if (page == 0) {
//                                                    firstPageDrawingSteps.add(
//                                                        DrawingStep(
//                                                            path = Path().apply { addPath(path.value) },
//                                                            color = selectedColor,
//                                                            thickness = brushSize,
//                                                            toolType = selectedTool
//                                                        )
//                                                    )
//                                                }
//                                            },
//                                            onDragEnd = {
//                                                path.value = Path() // 초기화
//                                            }
//                                        )
//                                    }
//                                }
//                        ) {
//                            drawIntoCanvas { canvas ->
//                                canvas.nativeCanvas.drawBitmap(currentBitmap, 0f, 0f, null)
//                            }
//
//                            // 현재 경로를 Canvas에 실시간 반영
//                            drawPath(
//                                path = path.value,
//                                color = selectedColor,
//                                style = Stroke(width = brushSize, cap = StrokeCap.Round)
//                            )
//                            if (pagerState.currentPage == 0) {
//                                firstPageStickers.forEach { stickerItem ->
//                                    drawIntoCanvas { canvas ->
//                                        canvas.nativeCanvas.drawBitmap(
//                                            stickerItem.bitmap,
//                                            stickerItem.position.value.x,
//                                            stickerItem.position.value.y,
//                                            null
//                                        )
//                                    }
//                                }
//                            }
//                        }
//
//
//
//                        Image(
//                            painter = painterResource(if (page == 0) R.drawable.draw_template else R.drawable.write_template),
//                            contentDescription = null,
//                            modifier = Modifier.size(leftBoxWidth, boxHeight)
//                        )
//                    }
//                }
//            }
//
//            // 오른쪽: 팔레트 및 업로드 버튼
//            Column(
//                modifier = Modifier
//                    .width(contentWidth * 0.25f)
//                    .fillMaxHeight()
//                    .background(Color.Transparent),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                PaletteTool(
//                    selectedTool = selectedTool,
//                    selectedColor = selectedColor,
//                    onColorChange = { selectedColor = it },
//                    onThicknessChange = { brushSize = it },
//                    onToolSelect = { selectedTool = it },
//                    stickerList = userStickers,
//                    onStickerSelect = { sticker ->
//                        CoroutineScope(Dispatchers.IO).launch {
//                            val bitmap = loadBitmapFromUrl(sticker.img)
//                            if (bitmap != null) {
//                                firstPageStickers.add(StickerItem(bitmap, mutableStateOf(centerPosition)))
//                            }
//                        }
//                    },
//                    onUndo = { undo() },
//                    onRedo = { redo() }
//                )
//
//                Button(onClick = { isDrawingMode = !isDrawingMode }) {
//                    Text(if (isDrawingMode) "스크롤 모드로 전환" else "그리기 모드로 전환")
//                }
//                Button(onClick = { isPreviewDialogVisible = true }) {
//                    Text("그리기 과정 미리보기")
//                }
//            }
//        }
//    }
//
//    // 모달 대신 전체 화면 Box로 미리보기 보여주기
//    if (isPreviewDialogVisible) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Black.copy(alpha = 0.7f)),
//            contentAlignment = Alignment.Center // 화면의 중앙에 박스를 위치
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxSize(0.9f) // 화면의 90% 크기로 설정
//                    .background(Color.White, shape = RoundedCornerShape(16.dp)) // 흰색 배경 및 둥근 모서리 설정
//                    .padding(16.dp), // 내부 여백 설정
//                horizontalArrangement = Arrangement.SpaceBetween, // 좌우 여백 분리
//                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
//            ) {
//                // 템플릿과 경로 표시하는 부분
//                Box(
//                    modifier = Modifier
//                        .weight(0.8f) // 왼쪽 공간을 크게 설정
//                        .fillMaxHeight()
//                        .padding(end = 8.dp), // 오른쪽에 여백 추가
//                    contentAlignment = Alignment.Center
//                ) {
//                    DrawingPlaybackView(
//                        drawingSteps = firstPageDrawingSteps,
//                        firstPageStickers = firstPageStickers,
//                        context = context,
//                        templateWidth = with(LocalDensity.current) { (leftBoxWidth - padding * 2).toPx().toInt() },
//                        templateHeight = with(LocalDensity.current) { (boxHeight - padding * 2).toPx().toInt() }
//                    )
//                }
//
//                // 오른쪽에 취소 및 보내기 버튼
//                Column(
//                    modifier = Modifier
//                        .weight(0.2f) // 오른쪽 공간을 작게 설정
//                        .fillMaxHeight(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.SpaceEvenly // 세로로 균등 배치
//                ) {
//                    Button(onClick = { isPreviewDialogVisible = false }) {
//                        Text("취소")
//                    }
//                    Button(
//                        onClick = {
//                            CoroutineScope(Dispatchers.IO).launch {
//                                saveAndUploadImages() // API 호출
//                            }
//                            isPreviewDialogVisible = false
//                        }
//                    ) {
//                        Text("보내기")
//                    }
//                }
//            }
//        }
//    }
//}

@file:Suppress("DEPRECATION")

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import java.io.File
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diarytablet.viewmodel.DiaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import com.example.diarytablet.utils.DrawingPlaybackView
import com.example.diarytablet.utils.DrawingStep
import com.example.diarytablet.utils.loadBitmapFromUrl
import com.example.diarytablet.utils.savePageImagesWithTemplate

data class StickerItem(
    val bitmap: Bitmap,
    var position: MutableState<Offset>
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
    var selectedStickerIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        diaryViewModel.fetchUserStickers()
    }

    val isLoading by diaryViewModel.isLoading.observeAsState(false)
    val responseMessage by diaryViewModel.responseMessage.observeAsState()

    val context = LocalContext.current
    var isDrawingMode by remember { mutableStateOf(true) }
    var isPreviewDialogVisible by remember { mutableStateOf(false) }
    var isWarningDialogVisible by remember { mutableStateOf(false) }
    var isVideoReady by remember { mutableStateOf(false)}

    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(5f) }
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }

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
    val firstPageDrawingSteps = remember { mutableStateListOf<DrawingStep>() }

    val undoStack = remember { mutableStateListOf<DrawingStep>() }
    val redoStack = remember { mutableStateListOf<DrawingStep>() }
    val redrawTrigger = remember { mutableStateOf(0) }

    fun undo() {
        if (firstPageDrawingSteps.isNotEmpty()) {
            val lastStroke = firstPageDrawingSteps.removeLast()
            redoStack.add(lastStroke)
            redrawTrigger.value++
        } else {
            Log.d("DiaryScreen", "Undo: No steps to undo.")
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val lastUndoneStroke = redoStack.removeLast()
            firstPageDrawingSteps.add(lastUndoneStroke)
            redrawTrigger.value++
        } else {
            Log.d("DiaryScreen", "Redo: No steps to redo.")
        }
    }

    suspend fun saveAndUploadImages() {
        val imageFiles = savePageImagesWithTemplate(
            bitmapsList,
            context,
            leftBoxWidth = leftBoxWidth,
            boxHeight = boxHeight,
            firstPageStickers = firstPageStickers
        )
        val videoFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "drawing_playback.mp4")
        val videoUri = Uri.fromFile(videoFile)

        if (imageFiles.size >= 2) {
            val drawUri = Uri.fromFile(imageFiles[0])
            val writeUri = Uri.fromFile(imageFiles[1])
            diaryViewModel.uploadDiary(context, drawUri, writeUri, videoUri)
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
                text = "그림일기",
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
        }

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
                        detectTapGestures(
                            onTap = { tapOffset ->
                                selectedStickerIndex = firstPageStickers.indexOfFirst { sticker ->
                                    val stickerPosition = sticker.position.value
                                    tapOffset.x in (stickerPosition.x..stickerPosition.x + sticker.bitmap.width) &&
                                            tapOffset.y in (stickerPosition.y..stickerPosition.y + sticker.bitmap.height)
                                }.takeIf { it >= 0 }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                selectedStickerIndex?.let { index ->
                                    firstPageStickers[index].position.value += dragAmount
                                    change.consume()
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
                            .pointerInput(isDrawingMode) {
                                if (isDrawingMode) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            path.value = Path().apply { moveTo(offset.x, offset.y) }
                                        },
                                        onDrag = { change, _ ->
                                            path.value.lineTo(
                                                change.position.x,
                                                change.position.y
                                            )

                                            val canvas = AndroidCanvas(currentBitmap)
                                            val paint = createPaintForTool(
                                                selectedTool,
                                                selectedColor,
                                                brushSize
                                            )
                                            canvas.drawPath(path.value.asAndroidPath(), paint)

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
                                            path.value = Path()
                                        }
                                    )
                                }
                            }
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(leftBoxWidth, boxHeight)
                                .clipToBounds()
                        ) {
                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawBitmap(currentBitmap, 0f, 0f, null)
                            }

                            drawPath(
                                path = path.value,
                                color = selectedColor,
                                style = Stroke(width = brushSize, cap = StrokeCap.Round)
                            )
                            if (pagerState.currentPage == 0) {
                                firstPageStickers.forEachIndexed { index, stickerItem ->
                                    drawIntoCanvas { canvas ->
                                        canvas.nativeCanvas.drawBitmap(
                                            stickerItem.bitmap,
                                            stickerItem.position.value.x,
                                            stickerItem.position.value.y,
                                            null
                                        )
                                    }

                                    if (selectedStickerIndex == index) {
                                        // 선택된 스티커에 테두리 표시
                                        val stickerPosition = stickerItem.position.value
                                        drawRect(
                                            color = Color.Red,
                                            topLeft = stickerPosition,
                                            size = androidx.compose.ui.geometry.Size(
                                                stickerItem.bitmap.width.toFloat(),
                                                stickerItem.bitmap.height.toFloat()
                                            ),
                                            style = Stroke(width = 2f)
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

            Column(
                modifier = Modifier
                    .width(contentWidth * 0.25f)
                    .fillMaxHeight()
                    .background(Color.Transparent),
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
                    },
                    onUndo = { undo() },
                    onRedo = { redo() }
                )

                Button(onClick = { isDrawingMode = !isDrawingMode }) {
                    Text(if (isDrawingMode) "스크롤 모드로 전환" else "그리기 모드로 전환")
                }
                Button(onClick = {
                    isWarningDialogVisible = true
                }) {
                    Text("그림 일기 작성 완료")
                }
            }
        }
    }

    // Canvas 바깥에 'X' 버튼을 위한 Box를 배치
    selectedStickerIndex?.let { index ->
        val stickerPosition = firstPageStickers[index].position.value
        Box(
            modifier = Modifier
                .offset(
                    x = with(LocalDensity.current) { stickerPosition.x.toDp() + 8.dp },
                    y = with(LocalDensity.current) { stickerPosition.y.toDp() + 8.dp }
                )
                .size(24.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .clickable {
                    firstPageStickers.removeAt(index)
                    selectedStickerIndex = null
                },
            contentAlignment = Alignment.Center
        ) {
            Text("X", color = Color.Red, fontSize = 16.sp)
        }
    }
    if (isWarningDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("그림 일기를 다시 작성 할 수 없어요!", fontSize = 20.sp, color = Color.Black)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { isWarningDialogVisible = false }) {
                        Text("취소")
                    }
                    Button(onClick = {
                        isWarningDialogVisible = false
                        isPreviewDialogVisible = true // 확인 버튼을 눌렀을 때 프리뷰 다이얼로그 표시
                        isVideoReady = false // 동영상 준비 상태 초기화
                    }) {
                        Text("확인")
                    }
                }
            }
        }
    }

    if (isPreviewDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DrawingPlaybackView(
                        drawingSteps = firstPageDrawingSteps,
                        firstPageStickers = firstPageStickers,
                        context = context,
                        templateWidth = with(LocalDensity.current) { (leftBoxWidth - padding * 2).toPx().toInt() },
                        templateHeight = with(LocalDensity.current) { (boxHeight - padding * 2).toPx().toInt() },
                        onVideoReady = { isVideoReady = true }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { isPreviewDialogVisible = false }) {
                        Text("취소")
                    }
                    Button(
                        onClick = {
                            if (isVideoReady) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    diaryViewModel.clearResponseMessage() // 이전 응답 메시지 초기화
                                    saveAndUploadImages() // 이미지 저장 및 업로드 함수 호출
                                }
                            }
                        },
                        enabled = isVideoReady && !isLoading // 로딩 중이 아니고, 비디오 준비 완료 시 활성화
                    ) {
                        Text("일기 저장")
                    }
                }
            }
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text("일기가 생성중입니다. 잠시만 기다려주세요.", color = Color.White, fontSize = 18.sp)
        }
    }

    responseMessage?.let { message ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(message, color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        diaryViewModel.clearResponseMessage()
                        isPreviewDialogVisible = false // 다이얼로그 닫기
                        if (responseMessage == "그림일기 작성 완료!") {
                            navController.navigate("main?origin=diary&isFinished=true"){
                            popUpTo("diary") {inclusive = true}}
                        }
                    }
                ) {
                    Text("확인")
                }
            }
        }
    }
}

