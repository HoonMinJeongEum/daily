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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography
data class MissionItem(
    val text: String,
    val isSuccess: Boolean
)

@Composable
fun MissionBar(
    modifier: Modifier = Modifier,
    missions: List<MissionItem> // 미션 리스트
) {
    Surface(
        modifier = modifier
            .padding(16.dp)
            .height(116.dp) // 원하는 높이 설정
            .wrapContentWidth(),
        shape = RoundedCornerShape(16.dp), // 모서리 둥글게 설정
        color = Color.Transparent // 투명한 배경색 설정
    ) {
        // missionContainer 배경 이미지
        Box( // Box로 감싸서 Surface와 Image의 크기를 같게 설정
            modifier = Modifier
                .wrapContentWidth()
                .height(116.dp), // Surface와 동일한 높이 설정
            contentAlignment = Alignment.CenterStart
        ) {
        Image(
            painter = painterResource(id = R.drawable.missioncontainer),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight(),
        )

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp), // 여백 설정
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 각 MissionRow 간 간격
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "오늘의 미션",
                        style = TextStyle(
                        fontSize = 25.sp,
                fontFamily = MyTypography.bodyLarge.fontFamily,
                fontWeight = MyTypography.bodyLarge.fontWeight
            )
            )
            for (mission in missions) {
                MissionRow(mission)
            }
            }
        }
    }
}

@Composable
fun MissionRow(mission: MissionItem) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .wrapContentWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp), // 가로 여백 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 체크 표시 아이콘
        Image(
            painter = painterResource(id = if (mission.isSuccess) R.drawable.check_icon else R.drawable.uncheck_icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = mission.text,
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = MyTypography.bodyLarge.fontFamily,
                fontWeight = MyTypography.bodyLarge.fontWeight
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun previewMissionBar() {
    MissionBar(
        missions = listOf(
            MissionItem("미션 1", isSuccess = true),
            MissionItem("미션 2", isSuccess = false),
            MissionItem("미션 3", isSuccess = true)
        )
    )
}
