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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.diarytablet.ui.screens.ToolType

data class DrawPath(val path: Path, val brush: Brush, val thickness: Float, val toolType: ToolType)

@Composable
fun DrawingScreen() {
    val paths = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var maxThickness by remember { mutableStateOf(6f) } // 슬라이더로 설정한 최대 두께
    var selectedTool by remember { mutableStateOf(ToolType.PENCIL) }
    val backgroundColor = Color.LightGray
    val initialThickness = 2f // 붓의 초기 얇은 두께
    val thicknessIncreaseRate = 0.5f // 두께 증가 속도
    var drawingThickness by remember { mutableStateOf(initialThickness) } // 실제 드로잉에 사용되는 두께

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        PaletteTool(
            selectedTool = selectedTool,
            onColorChange = { currentColor = it },
            onThicknessChange = { maxThickness = it }, // 슬라이더로 최대 두께 설정
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
                            if (selectedTool == ToolType.BRUSH) {
                                drawingThickness = initialThickness // 드래그 시작 시 초기 두께
                            }
                        },
                        onDrag = { change, _ ->
                            currentPath.lineTo(change.position.x, change.position.y)

                            // 브러시 설정
                            val brush = when (selectedTool) {
                                ToolType.CRAYON -> ShaderBrush(createCrayonShader(currentColor))
                                ToolType.HIGHLIGHTER -> SolidColor(currentColor.copy(alpha = 0.3f))
                                ToolType.ERASER -> SolidColor(backgroundColor)
                                ToolType.BRUSH -> SolidColor(currentColor)
                                else -> SolidColor(currentColor)
                            }

                            // 붓 도구일 경우 두께가 점진적으로 증가하고 최대값에 도달하면 유지
                            val thickness = if (selectedTool == ToolType.BRUSH) {
                                (drawingThickness + thicknessIncreaseRate).coerceAtMost(maxThickness)
                            } else {
                                maxThickness
                            }

                            // 하이라이터일 경우 드래그 중에도 paths에 추가
                            if (selectedTool == ToolType.HIGHLIGHTER) {
                                paths.add(
                                    DrawPath(
                                        Path().apply {
                                            moveTo(change.previousPosition.x, change.previousPosition.y)
                                            lineTo(change.position.x, change.position.y)
                                        },
                                        brush,
                                        thickness,
                                        ToolType.HIGHLIGHTER
                                    )
                                )
                            } else {
                                // 다른 도구는 드래그 종료 후에만 paths에 추가
                                val pathSegment = Path().apply {
                                    moveTo(change.previousPosition.x, change.previousPosition.y)
                                    lineTo(change.position.x, change.position.y)
                                }
                                paths.add(DrawPath(pathSegment, brush, thickness, selectedTool))
                            }

                            drawingThickness = thickness // 현재 두께 업데이트
                        },
                        onDragEnd = {
                            currentPath = Path() // 새로운 경로로 초기화
                        }
                    )
                }
        ) {
            // 저장된 모든 경로를 그리기
            paths.forEach { drawPath ->
                drawPath(
                    path = drawPath.path,
                    brush = drawPath.brush,
                    style = Stroke(width = drawPath.thickness, cap = StrokeCap.Round)
                )
            }
        }
    }
}

// 색상에 따라 크레용 질감의 BitmapShader 생성 함수
fun createCrayonShader(color: Color): Shader {
    val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val noisePaint = AndroidPaint().apply {
        this.color = color.toArgb()
        alpha = 100
    }

    for (i in 150 until 500) {
        val x = (Math.random() * bitmap.width).toFloat()
        val y = (Math.random() * bitmap.height).toFloat()
        canvas.drawPoint(x, y, noisePaint)
    }

    return BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
}
