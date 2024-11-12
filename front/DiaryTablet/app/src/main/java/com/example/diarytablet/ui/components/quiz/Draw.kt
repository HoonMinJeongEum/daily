package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
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
}
@Composable
fun DrawingThicknessSelector(
    modifier: Modifier,
    onSizeChanged: (Float) -> Unit
) {

    val thicknessOptions = listOf(10f, 15f, 20f, 25f)
    var selectedSize by remember { mutableStateOf(thicknessOptions[0]) }
    Row(
        modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        thicknessOptions.forEach { thickness ->
            Box(
                modifier = Modifier
                    .size(thickness.dp)
                    .clip(CircleShape)
                    .background(if (selectedSize == thickness) Color.Black else Color.Gray)
                    .clickable {
                        selectedSize = thickness
                        onSizeChanged(thickness)
                    }
            )
        }
    }
}
@Composable
fun DrawingUndoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.quiz_undo),
        contentDescription = "되돌리기",
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun DrawingRedoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.quiz_redo),
        contentDescription = "다시하기",
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun DrawingColorPalette(
    modifier: Modifier,
    onColorChanged: (Color) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val colors = listOf(
        Color.Red, Color(0xFFFFA500), Color.Yellow, Color(0xFFADFF2F), // 첫 줄: 빨, 주, 노, 연두
        Color.Green, Color.Cyan, Color.Blue, Color(0xFF00008B),         // 둘째 줄: 초, 하늘, 파, 남
        Color.Magenta, Color(0xFF8A2BE2), Color.Gray, Color.Black       // 셋째 줄: 핑, 보, 회, 검
    )
    Column(
        modifier.fillMaxSize()
    ) {
        colors.chunked(4).forEachIndexed { rowIndex, rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
            ) {
                rowColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedIndex = rowIndex * 4 + index
                                onColorChanged(color)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                        )

                        if (selectedIndex == rowIndex * 4 + index) {
                            // 선택된 색상에 테두리 표시
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(2.dp, Color.Black)
                            )
                        }
                    }
                }
            }
        }
    }
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