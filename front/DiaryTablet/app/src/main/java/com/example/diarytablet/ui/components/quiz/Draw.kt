package com.example.diarytablet.ui.components.quiz

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.checkStylusConnection
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
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown() // 드래그 시작 시 즉시 감지
                        point = down.position
                        points.add(point)
                        path = Path().apply { moveTo(point.x, point.y) }

                        viewModel.sendDrawAction("DOWN", point.x, point.y)

                        drag(down.id) { change ->
                            change.consume() // 이벤트 소비
                            point = change.position
                            points.add(point)

                            path = Path().apply {
                                points.forEachIndexed { index, point ->
                                    if (index == 0) moveTo(point.x, point.y)
                                    else lineTo(point.x, point.y)
                                }
                            }

                            viewModel.sendDrawAction("MOVE", point.x, point.y)
                        }

                        // 드래그가 끝난 후
                        viewModel.addPath(Pair(path, pathStyle!!.copy()))
                        points.clear()
                        path = Path()
                    }
                }
            },
    ) {
        val pathsCopy = paths?.toList()

        pathsCopy?.forEach { pair ->
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

    val thicknessOptions = listOf(10f, 20f, 35f, 50f)
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
    onClick: () -> Unit,
    viewModel: QuizViewModel
) {
    val isUndoAvailable by viewModel.isUndoAvailable.observeAsState(false)
    val undoImage = if (isUndoAvailable) R.drawable.quiz_undo else R.drawable.quiz_undo_gray

    Image(
        painter = painterResource(id = undoImage),
        contentDescription = "되돌리기",
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    )
}

@Composable
fun DrawingRedoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    viewModel: QuizViewModel
) {
    val isRedoAvailable by viewModel.isRedoAvailable.observeAsState(false)
    val redoImage = if (isRedoAvailable) R.drawable.quiz_redo else R.drawable.quiz_redo_gray

    Image(
        painter = painterResource(id = redoImage),
        contentDescription = "다시하기",
        modifier = Modifier.clickable (
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ){ onClick() }
    )
}

@Composable
fun DrawingColorPalette(
    modifier: Modifier,
    onColorChanged: (Color) -> Unit,
    isPenSelected: Boolean
) {
    var selectedIndex by remember { mutableStateOf(11) }
    val colors = listOf(
        Color.Red, Color(0xFFFFA500), Color.Yellow, Color(0xFFADFF2F),
        Color.Green, Color(0xFF87CEEB), Color.Blue, Color(0xFF000080),
        Color(0xFFFFC0CB), Color(0xFF800080), Color.Gray, Color.Black     // 셋째 줄: 핑, 보, 회, 검
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
                                if (isPenSelected) {
                                    selectedIndex = rowIndex * 4 + index
                                    onColorChanged(color)
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                        )

                        if (selectedIndex == rowIndex * 4 + index) {
                            if (rowIndex == 0) {
                                // 첫 번째 줄에서 선택된 색상에 테두리 표시
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            1.5.dp,
                                            Color.Black,
                                            when (index) {
                                                0 -> RoundedCornerShape(topStart = 16.dp)  // 첫 번째 칸은 왼쪽 둥글게
                                                3 -> RoundedCornerShape(topEnd = 16.dp)    // 네 번째 칸은 오른쪽 꼭지점 둥글게
                                                else -> RoundedCornerShape(0.dp)  // 나머지는 각진 모서리
                                            }
                                        )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(1.5.dp, Color.Black)
                                )
                            }
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