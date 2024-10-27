package com.example.diarytablet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R


// 미션 항목 데이터 클래스
data class MissionItem(
    val text: String,
    val isSuccess: Boolean
)

@Composable
fun MissionBar(
    modifier: Modifier = Modifier,
    missions: List<MissionItem> // 미션 리스트
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        for (mission in missions) {
            MissionRow(mission)
        }
    }
}

@Composable
fun MissionRow(mission: MissionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, Color.Gray)) // 테두리 색상
            .padding(16.dp), // 패딩
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 체크 표시 아이콘
        Image(
            painter = painterResource(id = if (mission.isSuccess) R.drawable.check_icon else R.drawable.uncheck_icon), // 성공/실패에 따라 아이콘 변경
            contentDescription = null,
            modifier = Modifier.size(24.dp) // 아이콘 크기
        )
        Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 공간
        Text(
            text = mission.text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface // 텍스트 색상
        )
    }
}

@Preview(showBackground = true)
@Composable
fun previewMissionBar() {
    MissionBar(
        missions = listOf( // 예시 미션 데이터 추가
            MissionItem("미션 1", isSuccess = true),
            MissionItem("미션 2", isSuccess = false),
            MissionItem("미션 3", isSuccess = true)
        )
    )
}

