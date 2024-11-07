package com.example.diaryApp.ui.components.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.diaryApp.viewmodel.QuizViewModel

@Composable
fun Draw(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel
) {
    val path by viewModel.path

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                viewModel.setCanvasSize(coordinates.size.width, coordinates.size.height) // 뷰모델에 캔버스 크기 전달
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
