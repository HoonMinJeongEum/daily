package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R

@Composable
fun AlarmButton(
    isAlarmOn: Boolean, // 알람 상태에 따른 이미지 전환
    modifier: Modifier = Modifier,
    onClick: () -> Unit, // 모달 생성시 onClick에 모달 오픈 함수 작성 후 매개변수에서 삭제.
) {
    val alarmIcon = if (isAlarmOn) R.drawable.alarm_on else R.drawable.alarm_off

    Image(
        painter = painterResource(id = alarmIcon),
        contentDescription = "Alarm Button",
        modifier = modifier
            .size(40.dp) // 아이콘 크기
            .clickable(onClick = onClick) // 클릭 이벤트
    )
}

@Preview(showBackground = true)
@Composable
fun previewAlarmButton() {
    AlarmButton(
        isAlarmOn = false,
        onClick = {}
    )
}
