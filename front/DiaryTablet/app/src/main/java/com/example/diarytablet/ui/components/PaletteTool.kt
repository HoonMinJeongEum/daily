import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Shader
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.R
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.model.ToolType
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableFloatStateOf

@Composable
fun PaletteTool(
    selectedTool: ToolType,
    selectedColor: Color,
    onScrollModeChange: (Boolean) -> Unit,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onToolSelect: (ToolType) -> Unit,
    stickerList: List<StickerStock>,
    onStickerSelect: (StickerStock) -> Unit,
) {
    var isStickerModalVisible by remember { mutableStateOf(false) }

    fun handleToolSelect(tool: ToolType) {
        onToolSelect(tool)
        onScrollModeChange(tool == ToolType.FINGER) // FINGER가 선택되면 스크롤 모드 활성화
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(color = Color.White.copy(alpha = 0.8f))
            .clip(RoundedCornerShape(16.dp))
            .width(250.dp),  // 팔레트 툴 박스의 고정 너비
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorPalette(selectedColor = selectedColor, onColorChange = onColorChange)

        Spacer(modifier = Modifier.height(30.dp))

        ThicknessSelector(onThicknessChange = onThicknessChange)

        Spacer(modifier = Modifier.height(30.dp))

        ToolSelectionRow(
            selectedTool = selectedTool,
            onToolSelect = { handleToolSelect(it) },
            onStickerIconClick = { isStickerModalVisible = true } // 스티커 모달창 열기
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    // 스티커 모달창
    if (isStickerModalVisible) {
        StickerModal(
            stickerList = stickerList,
            onStickerSelect = { sticker ->
                onStickerSelect(sticker)
                isStickerModalVisible = false // 스티커 선택 후 모달창 닫기
            },
            onDismiss = { isStickerModalVisible = false } // 모달창 닫기
        )
    }
}


@Composable
fun StickerModal(
    stickerList: List<StickerStock>,
    onStickerSelect: (StickerStock) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("스티커 선택", color = Color.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                // 스티커 목록을 가로로 스크롤 가능하게 표시
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(stickerList) { sticker ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = sticker.img,
                                placeholder = painterResource(R.drawable.loading),
                                error = painterResource(R.drawable.loading)
                            ),
                            contentDescription = "스티커 이미지",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onStickerSelect(sticker) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPalette(selectedColor: Color, onColorChange: (Color) -> Unit) {
    val colors = listOf(
        Color.Red, Color(0xFFFFA500), Color.Yellow, Color(0xFFADFF2F),
        Color.Green, Color(0xFF87CEEB), Color.Blue, Color(0xFF000080),
        Color(0xFFFFC0CB), Color(0xFF800080), Color.Gray, Color.Black
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in colors.indices step 4) {
            Row(modifier = Modifier.fillMaxWidth()) {
                colors.slice(i until i + 4).forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(color)
                            .clickable { onColorChange(color) }
//                            .border(
//                                width = if (color == selectedColor) 2.dp else 0.dp,
//                                color = Color.White
//                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ThicknessSelector(onThicknessChange: (Float) -> Unit) {
    val thicknessOptions = listOf(10f, 20f, 35f, 50f)
    var selectedThickness by remember { mutableFloatStateOf(10f) } // 선택된 굵기 상태 저장

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        thicknessOptions.forEach { thickness ->
            // 선택된 굵기일 경우 색상을 진하게 변경
            val color by animateColorAsState(
                targetValue = if (thickness == selectedThickness) Color.DarkGray else Color.Gray
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(thickness.dp)
                    .background(color, CircleShape)
                    .clickable {
                        selectedThickness = thickness // 선택된 굵기 상태 업데이트
                        onThicknessChange(thickness) // 굵기 변경 함수 호출
                    }
            )
        }
    }
}

@Composable
fun ToolSelectionRow(
    selectedTool: ToolType,
    onToolSelect: (ToolType) -> Unit,
    onStickerIconClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center, // 중앙 정렬
        modifier = Modifier.fillMaxWidth()
    ) {
        ToolImage(
            imageRes = R.drawable.palette_pen,
            selected = selectedTool == ToolType.PENCIL,
            onClick = { onToolSelect(ToolType.PENCIL) }
        )

        Spacer(modifier = Modifier.width(8.dp)) // 아이콘 간격

        ToolImage(
            imageRes = R.drawable.palette_eraser,
            selected = selectedTool == ToolType.ERASER,
            onClick = { onToolSelect(ToolType.ERASER) }
        )

        Spacer(modifier = Modifier.width(8.dp)) // 아이콘 간격

        ToolImage(
            imageRes = R.drawable.palette_sticker,
            selected = false,
            onClick = { onStickerIconClick() }
        )

        Spacer(modifier = Modifier.width(8.dp)) // 아이콘 간격

        ToolImage(
            imageRes = R.drawable.palette_finger,
            selected = selectedTool == ToolType.FINGER,
            onClick = { onToolSelect(ToolType.FINGER) }
        )
    }
}


@Composable
fun ToolImage(imageRes: Int, selected: Boolean, onClick: () -> Unit) {
    val size by animateDpAsState(targetValue = if (selected) 40.dp else 30.dp)

    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .background(
                    color = if (selected) Color.LightGray else Color.Transparent,
                    shape = CircleShape
                ) // 선택 시 배경 색을 추가해 강조
                .padding(4.dp)
        )
    }
}

fun createPaintForTool(toolType: ToolType, color: androidx.compose.ui.graphics.Color, thickness: Float): android.graphics.Paint {
    return when (toolType) {
        ToolType.ERASER -> createEraserPaint(thickness) // 지우개 설정
        ToolType.PENCIL -> createPencilPaint(color, thickness)
        ToolType.CRAYON -> createCrayonPaint(color, thickness)
        else -> throw IllegalArgumentException("Unsupported ToolType: $toolType")
    }
}

fun createPencilPaint(color: androidx.compose.ui.graphics.Color, strokeWidth: Float): android.graphics.Paint {
    return android.graphics.Paint().apply {
        this.color = color.toArgb() // Color를 Int로 변환
        this.strokeWidth = strokeWidth
        this.style = android.graphics.Paint.Style.STROKE
        this.strokeCap = android.graphics.Paint.Cap.ROUND
    }
}

fun createCrayonPaint(color: androidx.compose.ui.graphics.Color, strokeWidth: Float): android.graphics.Paint {
    return android.graphics.Paint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = android.graphics.Paint.Style.STROKE
        this.strokeCap = android.graphics.Paint.Cap.ROUND
        alpha = 200

        // 크레파스 질감 비트맵 생성
        val crayonBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(crayonBitmap)
        val noisePaint = android.graphics.Paint().apply {
            alpha = 200
        }

        // 랜덤 점 그리기: 한번만 랜덤 점을 그리도록 설정
        // 여러 번 그리지 않도록 수정
        for (i in 0 until 100) {  // 점을 한 번만 그리도록 제한
            val x = (Math.random() * crayonBitmap.width).toFloat()
            val y = (Math.random() * crayonBitmap.height).toFloat()
            canvas.drawPoint(x, y, noisePaint)
        }

        // BitmapShader를 사용하여 크레파스 질감 텍스처 적용
        shader = BitmapShader(crayonBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }
}

fun createEraserPaint(strokeWidth: Float): android.graphics.Paint {
    return android.graphics.Paint().apply {
        this.color = android.graphics.Color.TRANSPARENT
        this.strokeWidth = strokeWidth
        this.style = android.graphics.Paint.Style.STROKE
        this.strokeCap = android.graphics.Paint.Cap.ROUND
        xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
    }
}
