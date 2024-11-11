package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diarytablet.viewmodel.PathStyle
import com.example.diarytablet.viewmodel.QuizViewModel

@Composable
fun Draw(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel
) {
    var point by remember { mutableStateOf(Offset.Zero) } // point 위치 추적을 위한 State
    val points = remember { mutableListOf<Offset>() } // 새로 그려지는 path 표시하기 위한 points State

    var path by remember { mutableStateOf(Path()) } // 새로 그려지고 있는 중인 획 State

    val paths by viewModel.paths.observeAsState()
    val pathStyle by viewModel.pathStyle.observeAsState()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                viewModel.setCanvasSize(coordinates.size.width, coordinates.size.height)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        point = offset
                        points.add(point)
                    },
                    onDrag = { _, dragAmount ->
                        point += dragAmount
                        points.add(point)
                        path = Path()
                        points.forEachIndexed { index, point ->
                            if (index == 0) {
                                viewModel.sendDrawAction("DOWN", point.x, point.y)
                                path.moveTo(point.x, point.y)
                            } else {
                                viewModel.sendDrawAction("MOVE", point.x, point.y)
                                path.lineTo(point.x, point.y)
                            }
                        }
                    },
                    onDragEnd = {
                        viewModel.addPath(Pair(path, pathStyle!!.copy()))
                        points.clear()

                        path = Path()
                    }
                )
            },
    ) {
        paths?.forEach { pair ->
            drawPath(
                path = pair.first,
                style = pair.second
            )
        }

        drawPath(
            path = path,
            style = pathStyle!!
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    // Undo, Redo 버튼
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        DrawingUndoButton {
            viewModel.undoPath()
        }

        Spacer(modifier = Modifier.width(24.dp))

        DrawingRedoButton {
            viewModel.redoPath()
        }
    }
    // 획 스타일 조절하는 영역
    DrawingStyleArea(
        onSizeChanged = { viewModel.updateWidth(it) },
        onColorChanged = { viewModel.updateColor(it) },
        onAlphaChanged = { viewModel.updateAlpha(it) }
    )
}
@Composable
fun DrawingStyleArea(
    onSizeChanged: (Float) -> Unit,
    onColorChanged: (Color) -> Unit,
    onAlphaChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .width(72.dp)
                    .padding(horizontal = 8.dp),
                text = "두께",
                textAlign = TextAlign.Center
            )

            var size by remember { mutableStateOf(10.0f) }

            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = size,
                valueRange = 1.0f..30.0f,
                onValueChange = {
                    size = it
                    onSizeChanged(it)
                }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .width(72.dp)
                    .padding(horizontal = 8.dp),
                text = "투명도",
                textAlign = TextAlign.Center
            )

            var alpha by remember { mutableStateOf(1.0f) }

            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                value = alpha,
                valueRange = 0.0f..1.0f,
                onValueChange = {
                    alpha = it
                    onAlphaChanged(it)
                }
            )
        }

        DrawingColorPalette(
            onColorChanged = onColorChanged
        )
    }
}
@Composable
fun DrawingUndoButton(
    onClick: () -> Unit
) {
    Button(onClick = { onClick() }) {
        Text(text = "Undo")
    }
}

@Composable
fun DrawingRedoButton(
    onClick: () -> Unit
) {
    Button(onClick = { onClick() }) {
        Text(text = "Redo")
    }
}

//@Composable
//fun DrawingColorPalette(
//    onColorChanged: (Color) -> Unit
//) {
//    var selectedIndex by remember { mutableStateOf(0) }
//    val colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Yellow, Color.White)
//    Row(
//        modifier = Modifier.fillMaxWidth()
//            .padding(horizontal = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        colors.forEachIndexed { index, color ->
//            Box(
//                modifier = Modifier.size(36.dp)
//            ) {
//                Image(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(CircleShape)
//                        .clickable {
//                            selectedIndex = index
//                            onColorChanged(color)
//                        },
//                    painter = ColorPainter(color),
//                    contentDescription = "색상 선택"
//                )
//
//                if (selectedIndex == index) {
//
//                }
//            }
//        }
//    }
//}
@Composable
fun DrawingColorPalette(
    onColorChanged: (Color) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Yellow, Color.White)
    val radius = 80f // 원형 배치의 반지름
    val angleStep = 360f / colors.size // 각 색상의 각도 간격

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(12.dp)
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val center = Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
                    val angle = calculateAngle(center, tapOffset)
                    val selected = (angle / angleStep).toInt()
                    if (selected in colors.indices) {
                        selectedIndex = selected
                        onColorChanged(colors[selectedIndex])
                    }
                }
            }
    ) {
        colors.forEachIndexed { index, color ->
            val angle = Math.toRadians((index * angleStep - 90).toDouble())
            val x = center.x + radius * Math.cos(angle).toFloat()
            val y = center.y + radius * Math.sin(angle).toFloat()

            drawCircle(
                color = color,
                radius = 20f,
                center = Offset(x, y),
            )

            // 선택된 색상에 테두리를 그려서 강조
            if (selectedIndex == index) {
                drawCircle(
                    color = Color.White,
                    radius = 24f,
                    center = Offset(x, y),
                    style = Stroke(width = 3f)
                )
            }
        }
    }
}

// 각도를 계산하는 함수
fun calculateAngle(center: Offset, point: Offset): Float {
    val dx = point.x - center.x
    val dy = point.y - center.y
    val angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
    return if (angle < 0) angle + 360 else angle
}


internal fun DrawScope.drawPath(
    path: Path,
    style: PathStyle
) {
    drawPath(
        path = path,
        color = style.color,
        alpha = style.alpha,
        style = Stroke(width = style.width)
    )
}