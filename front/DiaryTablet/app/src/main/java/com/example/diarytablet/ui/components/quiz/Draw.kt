package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.diarytablet.viewmodel.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Draw(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel
) {
    val path by viewModel.path
    val coroutineScope = rememberCoroutineScope()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                viewModel.setCanvasSize(coordinates.size.width, coordinates.size.height) // 뷰모델에 캔버스 크기 전달
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->

                        viewModel.sendDrawAction("DOWN", offset.x, offset.y)
                    },
                    onDrag = { change, _ ->
                        coroutineScope.launch(Dispatchers.IO) {
                            viewModel.sendDrawAction("MOVE", change.position.x, change.position.y)
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch(Dispatchers.IO) {
                            viewModel.sendDrawAction("UP", 0f, 0f)
                        }
                    }
                )
            }
    ) {
        drawPath(
            path = Path().apply {
                addPath(path) // WebSocket 수신 경로
            },
            color = Color.Black,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
    }
}
