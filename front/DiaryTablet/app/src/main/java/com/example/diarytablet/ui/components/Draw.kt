package com.example.diarytablet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import com.example.diarytablet.viewmodel.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Draw(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel
) {
    val path by viewModel.path
    var currentPath by remember { mutableStateOf(Path()) }
    var isDrawing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDrawing = true
                        currentPath = Path().apply {
                            addPath(currentPath)
                            moveTo(offset.x, offset.y)
                        }
                        viewModel.sendDrawAction("DOWN", offset.x, offset.y) // 드로잉 시작 메시지 전송
                    },
                    onDrag = { change, _ ->
                        if (isDrawing) {
                            currentPath = Path().apply {
                                addPath(currentPath)
                                lineTo(change.position.x, change.position.y)
                            }
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.sendDrawAction("MOVE", change.position.x, change.position.y) // 드로잉 진행 메시지 전송
                            }
                        }
                    },
                    onDragEnd = {
                        isDrawing = false
                        coroutineScope.launch(Dispatchers.IO) {
                            viewModel.sendDrawAction("UP", 0f, 0f) // 드로잉 종료 메시지 전송
                        }
                    }
                )
            }
    ) {
        drawPath(
            path = Path().apply {
                addPath(path) // WebSocket 수신 경로
                addPath(currentPath) // 현재 로컬 드로잉 경로
            },
            color = Color.Black,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
    }
}
