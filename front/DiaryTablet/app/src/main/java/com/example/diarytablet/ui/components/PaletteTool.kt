import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.model.ToolType

@Composable
fun PaletteTool(
    selectedTool: ToolType,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    onThicknessChange: (Float) -> Unit,
    onToolSelect: (ToolType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ToolButton(
                icon = painterResource(R.drawable.palette_pen),
                selected = selectedTool == ToolType.PENCIL,
                onClick = { onToolSelect(ToolType.PENCIL) }
            )
            ToolButton(
                icon = painterResource(R.drawable.palette_eraser),
                selected = selectedTool == ToolType.ERASER,
                onClick = { onToolSelect(ToolType.ERASER) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        ColorPalette(selectedColor = selectedColor, onColorChange = onColorChange)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "두께")
            Spacer(modifier = Modifier.width(8.dp))

            var thickness by remember { mutableStateOf(5f) }

            Slider(
                value = thickness,
                onValueChange = {
                    thickness = it
                    onThicknessChange(it)
                },
                valueRange = 1f..10f
            )
        }
    }
}

@Composable
fun ToolButton(icon: Painter, selected: Boolean, onClick: () -> Unit) {
    val iconSize by animateDpAsState(targetValue = if (selected) 50.dp else 40.dp, label = "")
    val borderSize by animateDpAsState(targetValue = if (selected) 4.dp else 0.dp, label = "")

    Box(
        modifier = Modifier
            .size(iconSize)
            .background(if (selected) Color.Blue.copy(alpha = 0.2f) else Color.Transparent, CircleShape)
            .padding(borderSize)
            .clickable(onClick = onClick), // 클릭 이벤트
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = if (selected) Color.Blue else Color.Gray,
            modifier = Modifier.size(iconSize - borderSize * 2)
        )
    }
}

@Composable
fun ColorPalette(selectedColor: Color, onColorChange: (Color) -> Unit) {
    val colors = listOf(
        Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Cyan, Color.Black
    )

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
                    .background(color, CircleShape)
                    .clickable { onColorChange(color) }
                    .border(
                        width = if (color == selectedColor) 4.dp else 0.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}

// Paint 객체 생성 함수
fun createPaintForTool(toolType: ToolType, color: Color, strokeWidth: Float): Paint {
    return when (toolType) {
        ToolType.CRAYON -> createCrayonPaint(color, strokeWidth)
        ToolType.ERASER -> createEraserPaint(strokeWidth)
        ToolType.BRUSH -> createBrushPaint(color, strokeWidth)
        else -> createPencilPaint(color, strokeWidth)
    }
}

// PENCIL 도구용 Paint 생성 함수
fun createPencilPaint(color: Color, strokeWidth: Float): Paint {
    return Paint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
    }
}

// CRAYON 도구용 Paint 생성 함수 (일정한 투명도 유지)
fun createCrayonPaint(color: Color, strokeWidth: Float): Paint {
    return Paint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
        alpha = 100 // 일정한 투명도 유지

        // 크레파스 질감 생성
        val crayonBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(crayonBitmap)
        val paint = Paint().apply {
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
fun createBrushPaint(color: Color, strokeWidth: Float): Paint {
    return Paint().apply {
        this.color = color.toArgb()
        this.strokeWidth = strokeWidth
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
    }
}

// 지우개 도구용 Paint 생성 함수 (투명하게 처리)
fun createEraserPaint(strokeWidth: Float): Paint {
    return Paint().apply {
        color = Color.Transparent.toArgb()
        this.strokeWidth = strokeWidth
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // CLEAR 모드로 설정
    }
}