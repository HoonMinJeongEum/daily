import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Shader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.R
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.model.ToolType
import com.example.diarytablet.model.ToolType.*

@Composable
fun PaletteTool(
    selectedTool: ToolType,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onToolSelect: (ToolType) -> Unit,
    stickerList: List<StickerStock>,
    onStickerSelect: (StickerStock) -> Unit,
    onUndo: () -> Unit, // 되돌리기 콜백
    onRedo: () -> Unit  // 다시하기 콜백 추가
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 색상 팔레트
        ColorPalette(selectedColor = selectedColor, onColorChange = onColorChange)

        Spacer(modifier = Modifier.height(20.dp))

        // 두께 선택 버튼
        ThicknessSelector(onThicknessChange = onThicknessChange)

        Spacer(modifier = Modifier.height(20.dp))

        // 도구 선택 섹션 (이미지 사용)
        ToolSelectionRow(selectedTool = selectedTool, onToolSelect = onToolSelect)

        Spacer(modifier = Modifier.height(20.dp))

        // 되돌리기 및 다시하기 버튼 행
        UndoRedoButtons(onUndo = onUndo, onRedo = onRedo)
    }
    // 스티커 목록
    StickerRow(stickerList = stickerList, onStickerSelect = onStickerSelect)
}

@Composable
fun UndoRedoButtons(onUndo: () -> Unit, onRedo: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_undo), // 되돌리기 아이콘 리소스 사용
            contentDescription = "Undo",
            modifier = Modifier
                .size(40.dp)
                .clickable { onUndo() }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_redo), // 다시하기 아이콘 리소스 사용
            contentDescription = "Redo",
            modifier = Modifier
                .size(40.dp)
                .clickable { onRedo() }
        )
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
                            .border(
                                width = if (color == selectedColor) 2.dp else 0.dp,
                                color = Color.White
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ThicknessSelector(onThicknessChange: (Float) -> Unit) {
    val thicknessOptions = listOf(4f, 8f, 16f, 20f)
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        thicknessOptions.forEach { thickness ->
            Box(
                modifier = Modifier
                    .size(thickness.dp)
                    .background(Color.Gray, CircleShape)
                    .clickable { onThicknessChange(thickness) }
            )
        }
    }
}

@Composable
fun ToolSelectionRow(
    selectedTool: ToolType,
    onToolSelect: (ToolType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ToolImage(
            imageRes = R.drawable.palette_pen,
            selected = selectedTool == ToolType.PENCIL,
            onClick = { onToolSelect(ToolType.PENCIL) }
        )
        ToolImage(
            imageRes = R.drawable.palette_crayon,
            selected = selectedTool == ToolType.CRAYON,
            onClick = { onToolSelect(ToolType.CRAYON) }
        )
        ToolImage(
            imageRes = R.drawable.palette_eraser,
            selected = selectedTool == ToolType.ERASER,
            onClick = { onToolSelect(ToolType.ERASER) }
        )
    }
}

@Composable
fun ToolImage(imageRes: Int, selected: Boolean, onClick: () -> Unit) {
    val borderSize = if (selected) 4.dp else 0.dp

    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(borderSize)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun StickerRow(
    stickerList: List<StickerStock>,
    onStickerSelect: (StickerStock) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        stickerList.forEach { sticker ->
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


