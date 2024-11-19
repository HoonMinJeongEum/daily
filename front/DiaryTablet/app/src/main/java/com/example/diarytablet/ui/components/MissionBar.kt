package com.example.diarytablet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.MyTypography
data class MissionItem(
    val text: String,
    val isSuccess: Boolean
)

@Composable
fun MissionBar(
    modifier: Modifier = Modifier,
    screenWidth: Dp,
    screenHeight: Dp,
    missions: List<MissionItem> // 미션 리스트
) {
    Box(
        modifier = modifier.wrapContentSize()
    ) {
        val barWidth = screenWidth * 0.6f
        val barHeight = screenHeight / 6

        Surface(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.missioncontainer),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = barWidth * 0.06f), // 화면 크기에 비례한 여백
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "오늘의 미션",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (barWidth.value * 0.034f).sp
                        ),
                    )
                    missions.forEach { mission ->
                        MissionRow(mission, barHeight)
                    }
                }
            }
        }
    }
}

@Composable
fun MissionRow(mission: MissionItem, containerHeight: Dp) {
    val iconSize = containerHeight * 0.30f // 화면 크기에 비례한 아이콘 크기 설정

    Row(
        modifier = Modifier
            .height(containerHeight * 0.6f)
            .wrapContentWidth()
            .background(Color.Transparent)
            .padding(horizontal = containerHeight * 0.1f), // 가로 여백 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = if (mission.isSuccess) R.drawable.check_icon else R.drawable.uncheck_icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = mission.text,
            style = MyTypography.bodyLarge.copy(
                fontSize = (containerHeight.value * 0.2f).sp,
                color = DeepPastelNavy,
            )
        )
    }
}

