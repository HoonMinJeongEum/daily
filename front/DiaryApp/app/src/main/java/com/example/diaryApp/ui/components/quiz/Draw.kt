package com.example.diaryApp.ui.components.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.diaryApp.viewmodel.PathStyle
import com.example.diaryApp.viewmodel.QuizViewModel

@Composable
fun Draw(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel
) {
    val path by viewModel.path
    val paths by viewModel.paths.observeAsState()
    val pathStyle by viewModel.pathStyle.observeAsState()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                viewModel.setCanvasSize(coordinates.size.width, coordinates.size.height) // 뷰모델에 캔버스 크기 전달
            }
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