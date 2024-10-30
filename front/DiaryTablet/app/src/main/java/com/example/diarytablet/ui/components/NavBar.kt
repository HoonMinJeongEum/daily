package com.example.diarytablet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.Profile
import com.example.diarytablet.ui.components.AlarmButton

@Composable
fun Navbar(
    point: String,
    isBasicButtonOutlined: Boolean,
    isAlarmOn: Boolean,
    onBasicButtonClick: () -> Unit,
    onAlarmButtonClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .padding(20.dp), // 여백 조정
        horizontalArrangement = Arrangement.spacedBy(16.dp), // 버튼 간 간격 조정
        verticalAlignment = Alignment.CenterVertically // 수직 정렬
    ) {
        BasicButton(
            onClick = {},
            text = point,
            isOutlined = false
        )
        AlarmButton(
            isAlarmOn = isAlarmOn,
            onClick = {}
        )
        Profile(
            onProfileClick = {}
        )
    }
}

@Preview
@Composable
fun previewNav() {
    Navbar(
        point= "240",
        isBasicButtonOutlined= false,
        isAlarmOn= true,
        onBasicButtonClick= {},
        onAlarmButtonClick= {},
        onProfileClick= {}
    )
}

