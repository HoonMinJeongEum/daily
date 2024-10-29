// BackgroundPlacement.kt
package com.example.diarytablet.ui.theme

import android.graphics.Point
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R

@Composable
fun BackgroundPlacement(backgroundType: BackgroundType) {
    val backgroundRes = backgroundType.getBackgroundResource()

    // 이미지 배치 좌표
    val placements = mapOf(
        "sora" to Point(517, 632),
        "big-sora" to Point(1083, 481),
        "big-jogae" to Point(293, 546),
        "jogae" to Point(64, 482),
        "duck" to Point(1023, 150),
        "tube" to Point(306, 69)
    )

    Image(
        painter = painterResource(id = backgroundRes),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

    placements.forEach { (name, point) ->
        val drawableId = when (name) {
            "sora" -> R.drawable.sora
            "big-sora" -> R.drawable.big_sora
            "big-jogae" -> R.drawable.big_jogae
            "jogae" -> R.drawable.jogae
            "duck" -> R.drawable.duck
            "tube" -> R.drawable.tube
            else -> null
        }

        drawableId?.let {
            val size = when (name) {
                "sora" -> 82.dp
                "big-sora" -> 75.dp
                "big-jogae" -> 83.dp
                "jogae" -> 75.dp
                "duck" -> 55.dp
                "tube" -> 100.dp
                else -> 50.dp
            }

            val rotationState = remember { Animatable(0f) }
//요소 별로 에니메이션 효과 따로 적용 예정
            LaunchedEffect(name) {
                rotationState.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 2000 // 전체 애니메이션 기간
                            0f at 0 with LinearEasing // 시작 각도
                            5f at 800 with LinearEasing // 오른쪽으로 회전
                            0f at 1600 // 다시 원래 위치로
                        },
                        repeatMode = RepeatMode.Reverse // 애니메이션이 끝나면 반전
                    )
                )
            }

            // 애니메이션 효과를 적용
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier
                    .size(size) // 크기 설정
                    .offset(x = point.x.dp, y = point.y.dp) // 각 캐릭터의 위치에 따라 배치
                    .graphicsLayer(
                        rotationZ = rotationState.value * 5f // 회전 각도 조정 (조정 가능)
                    )
            )
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
fun previewBackground() {
    BackgroundPlacement(backgroundType = BackgroundType.DEFAULT)
}
