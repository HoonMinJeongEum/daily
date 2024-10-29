import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.diarytablet.ui.screens.ToolType

data class DrawPath(val path: Path, val color: Color, val thickness: Float, val toolType: ToolType)

@Composable
fun DrawingScreen() {
    val paths = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentThickness by remember { mutableStateOf(6f) }
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }
    val backgroundColor = Color.LightGray

    // 크레용 질감 적용을 위한 ShaderBrush 설정
    val crayonShader = remember { createCrayonShader() }
    val crayonBrush = ShaderBrush(crayonShader)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        PaletteTool(
            selectedTool = selectedTool,
            onColorChange = { currentColor = it },
            onThicknessChange = { currentThickness = it },
            onToolSelect = { tool -> selectedTool = tool }
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, _ ->
                            // 실시간 업데이트
                            currentPath.lineTo(change.position.x, change.position.y)
                            val color = if (selectedTool == ToolType.HIGHLIGHTER) currentColor.copy(alpha = 0.3f) else currentColor
                            val pathCopy = Path().apply { addPath(currentPath) }
                            paths.removeLastOrNull()
                            paths.add(DrawPath(pathCopy, color, currentThickness, selectedTool))
                        },
                        onDragEnd = {
                            // 드래그가 끝나면 paths에 최종 경로 추가
                            val color = if (selectedTool == ToolType.HIGHLIGHTER) currentColor.copy(alpha = 0.3f) else currentColor
                            paths.add(DrawPath(currentPath, color, currentThickness, selectedTool))
                            currentPath = Path() // 새로운 경로로 초기화
                        }
                    )
                }
        ) {
            // 저장된 모든 경로 그리기
            paths.forEach { drawPath ->
                drawPathWithShader(
                    drawPath = drawPath,
                    crayonBrush = crayonBrush,
                    backgroundColor = backgroundColor
                )
            }
        }
    }
}

// 크레용 질감의 BitmapShader를 생성하는 함수
fun createCrayonShader(): Shader {
    val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val noisePaint = AndroidPaint().apply {
        color = android.graphics.Color.BLACK
        alpha = 40
    }

    // 랜덤 점을 찍어서 크레용 질감을 생성
    for (i in 0 until 500) {
        val x = (Math.random() * bitmap.width).toFloat()
        val y = (Math.random() * bitmap.height).toFloat()
        canvas.drawPoint(x, y, noisePaint)
    }

    return BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
}

// DrawPath를 그릴 때 도구별로 질감 적용을 처리
fun DrawScope.drawPathWithShader(drawPath: DrawPath, crayonBrush: ShaderBrush, backgroundColor: Color) {
    val color = if (drawPath.toolType == ToolType.HIGHLIGHTER) drawPath.color.copy(alpha = 0.3f) else drawPath.color
    val brush = when (drawPath.toolType) {
        ToolType.CRAYON -> crayonBrush
        ToolType.HIGHLIGHTER -> SolidColor(color)
        ToolType.ERASER -> SolidColor(backgroundColor)
        else -> SolidColor(drawPath.color)
    }
    drawPath(
        path = drawPath.path,
        brush = brush,
        style = Stroke(width = drawPath.thickness, cap = StrokeCap.Round)
    )
}

