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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.diarytablet.ui.components.DailyButton
import com.example.diarytablet.ui.components.modal.CommonModal
import com.example.diarytablet.ui.components.modal.CommonPopup
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.utils.DrawingPlaybackView
import com.example.diarytablet.utils.DrawingStep
import com.example.diarytablet.utils.loadBitmapFromUrl
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.utils.savePageImagesWithTemplate
import com.example.diarytablet.viewmodel.SpenEventViewModel
import com.samsung.android.sdk.penremote.ButtonEvent
import kotlinx.coroutines.delay

data class StickerItem(
    val bitmap: Bitmap,
    var position: MutableState<Offset>
)

@Composable
fun DiaryScreen(
    navController: NavController,
    backgroundType: BackgroundType = BackgroundType.DRAWING_DIARY,
    diaryViewModel: DiaryViewModel = hiltViewModel(),
    spenEventViewModel: SpenEventViewModel
) {
    BackgroundPlacement(backgroundType = backgroundType)
    val userStickers by diaryViewModel.userStickers.observeAsState(emptyList())
    val firstPageStickers = remember { mutableStateListOf<StickerItem>() }
    var selectedStickerIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        diaryViewModel.fetchUserStickers()
    }
    var isError by remember { mutableStateOf(false) }
    val isLoading by diaryViewModel.isLoading.observeAsState(false)
    val responseMessage by diaryViewModel.responseMessage.observeAsState()

    val context = LocalContext.current
    var isDrawingMode by remember { mutableStateOf(true) }
    var isPreviewDialogVisible by remember { mutableStateOf(false) }
    var isWarningDialogVisible by remember { mutableStateOf(false) }
    var isVideoReady by remember { mutableStateOf(false)}
    var isModalOpen by remember { mutableStateOf(false) }

    var selectedColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(10f) }
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
    val bitmapWidthPx = with(density) { (leftBoxWidth-padding*2).roundToPx() }
    val bitmapHeightPx = with(density) { (boxHeight-padding*2).roundToPx() }

    val bitmapsList = remember {
        mutableStateListOf(
            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888),
            Bitmap.createBitmap(bitmapWidthPx, bitmapHeightPx, Bitmap.Config.ARGB_8888)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val interactionSource = remember { pagerState.interactionSource }
    val firstPageDrawingSteps = remember { mutableStateListOf<DrawingStep>() }
    LaunchedEffect(Unit) {
        spenEventViewModel.spenEventFlow.collect { event ->
            val buttonEvent = ButtonEvent(event)

            when (buttonEvent.action) {
                ButtonEvent.ACTION_DOWN -> {
                    selectedTool = when (selectedTool) {
                        ToolType.PENCIL -> {
                            selectedStickerIndex = null
                            isDrawingMode = true
                            ToolType.ERASER
                        }
                        ToolType.ERASER -> {
                            isDrawingMode = false
                            ToolType.FINGER
                        }
                        ToolType.FINGER -> {
                            selectedStickerIndex = null
                            isDrawingMode = true
                            selectedColor = Color.Black
                            ToolType.PENCIL
                        }
                        ToolType.CRAYON -> {
                            isDrawingMode = true
                            ToolType.PENCIL
                        } // 예시로 CRAYON에서 PENCIL로 변경
                    }
                    Log.d("ToolSwitcher", "Tool switched to: $selectedTool")
                }
                ButtonEvent.ACTION_UP -> {
                    Log.d("ToolSwitcher", "Button Up Detected")
                }
            }
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
                contentDescription = "뒤로가기 버튼",
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        playButtonSound(context, R.raw.all_button)
                        isModalOpen = true
                    }
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "그림 일기",
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
                                // 클릭한 좌표가 스티커 내부인지 확인
                                selectedStickerIndex = firstPageStickers.indexOfFirst { sticker ->
                                    val stickerPosition = sticker.position.value
                                    tapOffset.x in (stickerPosition.x..stickerPosition.x + sticker.bitmap.width) &&
                                            tapOffset.y in (stickerPosition.y..stickerPosition.y + sticker.bitmap.height)
                                }.takeIf { it >= 0 }

                                if (selectedStickerIndex != null) {
                                    // 스티커를 클릭한 경우
                                    isDrawingMode = false // 스크롤 모드로 전환
                                    selectedTool = ToolType.FINGER // 손가락 도구 선택 상태
                                } else {
                                    // 스티커 외부를 클릭한 경우
                                    if (selectedTool != ToolType.ERASER ){
                                        selectedStickerIndex = null // 선택 해제
                                        isDrawingMode = true // 드로잉 모드로 전환
                                        selectedTool = ToolType.PENCIL // 기본 도구 선택
                                        selectedColor = Color.Black
                                    }
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                selectedStickerIndex?.let { index ->
                                    val sticker = firstPageStickers[index]
                                    val newPosition = sticker.position.value + dragAmount

                                    // 비트맵의 경계 내로 제한
                                    val clampedX = newPosition.x.coerceIn(0f, bitmapWidthPx - sticker.bitmap.width.toFloat())
                                    val clampedY = newPosition.y.coerceIn(0f, bitmapHeightPx - sticker.bitmap.height.toFloat())

                                    // 스티커 위치 업데이트
                                    sticker.position.value = Offset(clampedX, clampedY)
                                    change.consume()
                                }
                            }
                        )
                    }

            ) {
                // 페이지 전환 시 스티커 선택 해제
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Press || interaction is DragInteraction.Start) {
                            // 페이지 이동 시작 시 스티커 선택 해제
                            if (selectedStickerIndex != null) {
                                selectedStickerIndex = null
                                isDrawingMode = true // 드로잉 모드로 전환
                                selectedTool = ToolType.PENCIL // 기본 도구 설정
                            }
                        }
                    }
                }
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
                                detectTapGestures(
                                    onTap = { offset ->
                                        val clickedStickerIndex = firstPageStickers.indexOfFirst { sticker ->
                                            val stickerPosition = sticker.position.value
                                            offset.x in stickerPosition.x..(stickerPosition.x + sticker.bitmap.width) &&
                                                    offset.y in stickerPosition.y..(stickerPosition.y + sticker.bitmap.height)
                                        }

                                        if (clickedStickerIndex >= 0) {
                                            // 스티커를 클릭한 경우
                                            selectedStickerIndex = clickedStickerIndex
                                            isDrawingMode = false // 스크롤 모드로 전환
                                            selectedTool = ToolType.FINGER
                                        } else {
                                            // 스티커 외부를 클릭한 경우
                                            if (selectedTool != ToolType.ERASER ){
                                                selectedStickerIndex = null // 선택 해제
                                                isDrawingMode = true // 드로잉 모드로 전환
                                                selectedTool = ToolType.PENCIL // 기본 도구 선택
                                                selectedColor = Color.Black
                                            }

                                            // 드로잉 모드일 경우 점 찍기
//                                            if (isDrawingMode) {
//                                                val canvas = AndroidCanvas(currentBitmap)
//                                                val paint = createPaintForTool(
//                                                    toolType = selectedTool,
//                                                    color = selectedColor,
//                                                    thickness = brushSize
//                                                )
//
//                                                // 점을 그림
//                                                canvas.drawCircle(offset.x, offset.y, brushSize / 2, paint)
//
//                                                // 드로잉 스텝 저장
//                                                if (pagerState.currentPage == 0) {
//                                                    firstPageDrawingSteps.add(
//                                                        DrawingStep(
//                                                            path = Path().apply {
//                                                                addOval(
//                                                                    Rect(
//                                                                        offset.x - brushSize / 2,
//                                                                        offset.y - brushSize / 2,
//                                                                        offset.x + brushSize / 2,
//                                                                        offset.y + brushSize / 2
//                                                                    )
//                                                                )
//                                                            },
//                                                            color = selectedColor,
//                                                            thickness = brushSize,
//                                                            toolType = selectedTool
//                                                        )
//                                                    )
//                                                }
//                                            }
                                        }
                                    }
                                )
                            }

                            .pointerInput(isDrawingMode) {
                                if (isDrawingMode) {
//                                    detectDragGestures(
//                                        onDragStart = { offset ->
//                                            path.value = Path().apply { moveTo(offset.x, offset.y) }
//
////                                            if (selectedTool == ToolType.ERASER) {
////                                                val canvas = AndroidCanvas(currentBitmap)
////                                                val eraserPaint = createPaintForTool(
////                                                    toolType = selectedTool,
////                                                    color = Color.Transparent,
////                                                    thickness = brushSize
////                                                )
//////                                                canvas.drawCircle(offset.x, offset.y, brushSize / 2, eraserPaint)
////                                            }
//                                        },
//                                        onDrag = { change, _ ->
//                                            path.value.lineTo(change.position.x, change.position.y)
//
//                                            val canvas = AndroidCanvas(currentBitmap)
//                                            val paint = createPaintForTool(
//                                                toolType = selectedTool,
//                                                color = selectedColor,
//                                                thickness = brushSize
//                                            )
//
//                                            if (selectedTool == ToolType.ERASER) {
//                                                selectedColor = Color.Transparent
//                                                paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
//                                            }
//
//                                            canvas.drawPath(path.value.asAndroidPath(), paint)
//
//                                            if (pagerState.currentPage == 0) {
//                                                firstPageDrawingSteps.add(
//                                                    DrawingStep(
//                                                        path = Path().apply { addPath(path.value) },
//                                                        color = if (selectedTool == ToolType.ERASER) Color.Transparent else selectedColor,
//                                                        thickness = brushSize,
//                                                        toolType = selectedTool
//                                                    )
//                                                )
//                                            }
//                                            change.consume()
//                                        },
//                                        onDragEnd = {
//                                            path.value = Path()
//                                        }
//                                    )
                                    awaitPointerEventScope {
                                        var lastPosition: Offset? = null // 마지막 터치 위치를 저장

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val position = event.changes.first().position

                                            // 터치 이벤트 처리
                                            event.changes.first().apply {
                                                if (pressed) {
                                                    // 드래그 시작
                                                    if (lastPosition == null) {
                                                        path.value = Path().apply { moveTo(position.x, position.y) }
                                                    } else {
                                                        // 드래그 중
                                                        path.value.lineTo(position.x, position.y)
                                                        val canvas = AndroidCanvas(currentBitmap)
                                                        val paint = createPaintForTool(
                                                            toolType = selectedTool,
                                                            color = selectedColor,
                                                            thickness = brushSize
                                                        )

                                                        if (selectedTool == ToolType.ERASER) {
                                                            paint.xfermode =
                                                                android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
                                                        }

                                                        canvas.drawPath(path.value.asAndroidPath(), paint)

                                                        if (pagerState.currentPage == 0) {
                                                            firstPageDrawingSteps.add(
                                                                DrawingStep(
                                                                    path = Path().apply { addPath(path.value) },
                                                                    color = if (selectedTool == ToolType.ERASER) Color.Transparent else selectedColor,
                                                                    thickness = brushSize,
                                                                    toolType = selectedTool
                                                                )
                                                            )
                                                        }
                                                    }
                                                    lastPosition = position // 마지막 위치 업데이트
                                                } else if (!pressed && lastPosition != null) {
                                                    // 터치가 끝난 경우 초기화
                                                    path.value = Path()
                                                    lastPosition = null
                                                }

                                                // 이벤트 소비
                                                consume()
                                            }
                                        }
                                    }
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
                                        // 선택된 스티커에 테두리와 'X' 버튼 추가
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
                            contentScale = ContentScale.FillBounds,
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // 위쪽 여백

                PaletteTool(
                    selectedTool = selectedTool,
                    selectedColor = selectedColor,
                    onScrollModeChange = { isScrollMode ->
                        isDrawingMode = !isScrollMode
                        if (isScrollMode) selectedTool = ToolType.FINGER
                    },
                    onColorChange = { selectedColor = it },
                    onThicknessChange = { brushSize = it },
                    onToolSelect = { tool ->
                        selectedTool = tool
                        if (tool == ToolType.PENCIL || tool == ToolType.ERASER) {
                            selectedStickerIndex = null
                            isDrawingMode = true
                            selectedColor = Color.Black
                            if (tool == ToolType.ERASER) {
                                selectedStickerIndex = null
                                isDrawingMode = true
//                                selectedColor = Color.Transparent
                            }
                        }
                    },
                    stickerList = userStickers,
                    onStickerSelect = { sticker ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val bitmap = loadBitmapFromUrl(sticker.img)
                            if (bitmap != null) {
                                firstPageStickers.add(
                                    StickerItem(
                                        bitmap,
                                        mutableStateOf(centerPosition)
                                    )
                                )
                                selectedStickerIndex = firstPageStickers.size - 1
                                isDrawingMode = false
                                selectedTool = ToolType.FINGER

                                CoroutineScope(Dispatchers.Main).launch {
                                    pagerState.scrollToPage(0)
                                }
                            }
                        }
                    }
                )

                DailyButton(
                    text = "그림일기 만들러 가기",
                    fontSize = 20.sp,
                    onClick = { isWarningDialogVisible = true },
                    cornerRadius = 50,
                    width = 240.dp,
                    height = 60.dp
                )

                Spacer(modifier = Modifier.height(8.dp)) // 아래쪽 여백
            }
        }

        // 선택된 스티커의 'X' 버튼 추가
        selectedStickerIndex?.let { index ->
            val sticker = firstPageStickers[index]
            val stickerPosition = sticker.position.value

            StickerWithDeleteButton(stickerSize = 150, position = stickerPosition) {
                firstPageStickers.removeAt(index)
                selectedStickerIndex = null
            }
        }
    }

    if (isModalOpen) {
        CommonModal(
            onDismissRequest = { isModalOpen = false },
            titleText = "그림 일기를 그만 그릴까요?",
            confirmText= "종료",
            onConfirm = {
                isModalOpen = false
                navController.navigate("main") {
                    popUpTo("diary") { inclusive = true }
                }
            }
        )
    }

    if (isWarningDialogVisible) {
        CommonModal(
            onDismissRequest = { isWarningDialogVisible = false },
            titleText = "그림 일기를 다시 작성할 수 없어요!",
            cancelText = "이어 쓰기",
            confirmText = "일기 완성",
            confirmButtonColor = PastelNavy,
            onConfirm = {
                isWarningDialogVisible = false
                isPreviewDialogVisible = true
                isVideoReady = false
            }
        )
    }

    if (isPreviewDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .pointerInput(Unit) {
                    detectTapGestures { /* 터치 이벤트를 소비하여 아무 동작도 하지 않음 */ }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isError) {
                // 에러 발생 시 모달 표시
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "비디오 생성 중 오류가 발생했습니다.\n메인 화면으로 이동합니다.",
                        color = Color.Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DailyButton(
                        text = "확인",
                        fontSize = 20.sp,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                diaryViewModel.clearResponseMessage()
                                saveAndUploadImages()
                            }
                        },
                        cornerRadius = 50,
                        width = 200.dp,
                        height = 60.dp
                    )
                }
            } else {
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
                            .fillMaxHeight()
                            .padding(start = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        if (isVideoReady && !isLoading) {
                            DailyButton(
                                text = "일기 저장",
                                fontSize = 20.sp,
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        diaryViewModel.clearResponseMessage()
                                        saveAndUploadImages()
                                    }
                                },
                                cornerRadius = 50,
                                width = 200.dp,
                                height = 60.dp
                            )
                        } else {
                            val rotation by rememberInfiniteTransition().animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 1000, easing = LinearEasing)
                                ), label = ""
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "화면 녹화중이에요.\n잠시만 기다려 주세요!",
                                    color = Color.Gray,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.loading),
                                    contentDescription = "로딩 중 아이콘",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .graphicsLayer { rotationZ = rotation }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(35.dp)) // 하단 여백 추가
                    }
                }
            }
        }
    }
    data class LoadingItem(
        val message: String,
        val imageResId: Int
    )
    if (isLoading) {
        // Animatable을 사용하여 흔들림 애니메이션 설정
        val rotation = remember { Animatable(0f) }
        LaunchedEffect(isLoading) {
            rotation.animateTo(
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }

        // 순차적으로 메시지와 이미지를 보여주기 위한 상태
        var currentIndex by remember { mutableStateOf(0) }
        var displayedText by remember { mutableStateOf("") } // 현재 타이핑된 텍스트
        val loadingItems = listOf(
            LoadingItem("기록 버튼을 누르면 저장된 일기를 볼 수 있어요 !", R.drawable.main_char),
            LoadingItem("전 해달이라 해꽁이입니다 !", R.drawable.main_char2),
            LoadingItem("오늘 단어학습 해꽁 ??", R.drawable.main_char3),
            LoadingItem("저는 물에 누워서 조개를 먹기도 하고, 잠을 자기도 해요 ~", R.drawable.main_char4),
            LoadingItem("오늘 부모님께 사랑한다고 해봐요 !", R.drawable.main_char5),
        )

        // 타이핑 효과와 순환
        LaunchedEffect(currentIndex) {
            val currentItem = loadingItems[currentIndex]
            displayedText = "" // 텍스트 초기화
            currentItem.message.forEach { char ->
                displayedText += char // 한 글자씩 추가
                delay(100L) // 타이핑 속도
            }
            delay(2000L) // 메시지 타이핑이 끝난 후 1초 대기
            currentIndex = (currentIndex + 1) % loadingItems.size // 다음 메시지로 이동
        }

        // 화면 전체를 차지하는 Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)), // 반투명 배경
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 타이핑 효과가 적용된 텍스트
                Text(
                    text = displayedText,
                    color = Color.White,
                    fontSize = 50.sp // 텍스트 크기
                )

                // 흔들림 애니메이션이 적용된 이미지
                Image(
                    painter = painterResource(id = loadingItems[currentIndex].imageResId), // 현재 이미지
                    contentDescription = "로딩 중 이미지",
                    modifier = Modifier
                        .size(400.dp) // 이미지 크기
                        .graphicsLayer { rotationZ = rotation.value } // 흔들리는 애니메이션 적용
                )
            }
        }
    }

    responseMessage?.let { message ->
        CommonPopup(
            onDismissRequest = {
                diaryViewModel.clearResponseMessage()

                // 응답 메시지에 따라 네비게이션 처리
                if (message == "그림 일기 작성 완료!") {
                    navController.navigate("main?origin=diary&isFinished=true") {
                        popUpTo("diary") { inclusive = true }
                    }
                } else {
                    navController.navigate("main") {
                        popUpTo("diary") { inclusive = true }
                    }
                }
            },
            titleText = message
        )
    }
}

@Composable
fun StickerWithDeleteButton(stickerSize: Int, position: Offset, onDelete: () -> Unit) {
    val density = LocalDensity.current
    val xDp = with(density) { (position.x).toDp() }
    val yDp = with(density) { (position.y + 120).toDp() }

    Box(
        modifier = Modifier
            .offset(x = xDp, y = yDp)
            .size(24.dp) // X 버튼 크기
            .background(Color.White, shape = CircleShape)
            .clickable { onDelete() },
        contentAlignment = Alignment.Center
    ) {
        Text("X", color = Color.Red, fontSize = 10.sp)
    }
}
