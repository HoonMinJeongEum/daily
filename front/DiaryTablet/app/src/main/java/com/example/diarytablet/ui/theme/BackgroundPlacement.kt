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
        "coral" to Point(650, 632),
        "big-sora" to Point(1083, 450),
        "big-jogae" to Point(293, 510),
        "jogae" to Point(64, 360),
        "duck" to Point(1023, 150),
        "tube" to Point(306, 100)
    )

    Image(
        painter = painterResource(id = backgroundRes),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

    placements.forEach { (name, point) ->
        val drawableId = when (name) {
            "coral" -> R.drawable.coral
            "big-sora" -> R.drawable.big_sora
            "big-jogae" -> R.drawable.big_jogae
            "jogae" -> R.drawable.jogae
            "duck" -> R.drawable.duck
            "tube" -> R.drawable.tube
            else -> null
        }

        drawableId?.let {
            val size = when (name) {
                "coral" -> 80.dp
                "big-sora" -> 80.dp
                "big-jogae" -> 70.dp
                "jogae" -> 60.dp
                "duck" -> 70.dp
                "tube" -> 100.dp
                else -> 50.dp
            }

            val rotationState = remember { Animatable(0f) }
            // 요소별 애니메이션 시간과 각도 설정
            val duration = when (name) {
                "coral" -> 2000
                "big-sora" -> 2000
                "big-jogae" -> 1900
                "jogae" -> 2000
                "duck" -> 1900
                "tube" -> 2000
                else -> 2000
            }

            val maxRotation = when (name) {
                "coral" -> 3f
                "big-sora" -> 5f
                "big-jogae" -> 4f
                "jogae" -> 5f
                "duck" -> 5f
                "tube" -> 4f
                else -> 3f
            }
            LaunchedEffect(name) {
                rotationState.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = duration // 전체 애니메이션 기간
                            0f at 0 with LinearEasing // 시작 각도
//                            5f at 800 with LinearEasing // 오른쪽으로 회전
                            maxRotation at duration / 2 with LinearEasing
                            0f at duration // 다시 원래 위치로
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
                        rotationZ = rotationState.value * maxRotation  // 회전 각도 조정 (조정 가능)
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
