package com.example.diarytablet.ui.components

import MissionItemRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto

@Composable
fun AlarmModal(
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    alarmItems: List<AlarmResponseDto>
) {
    if (isModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .width(500.dp)
                    .padding(40.dp)
//                    .background(Color(0xFFDEE5D4), shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)) // 모달 배경색 및 모서리
                    .padding(24.dp)
                    .align(Alignment.Center)
            ) {
//                Box(
//                    modifier = Modifier
//                        .align(alignment = Alignment.TopEnd)
//                        .clickable { onDismiss() }
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.mission_close),
//                        contentDescription = null,
//                        modifier = Modifier.size(40.dp)
//
//                    )
//                }
                Column(
                    modifier = Modifier.padding(vertical = 40.dp)
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {

//                    AlarmItem()
                }
            }
        }
    }
}

@Composable
fun AlarmItem(
    alarmItem: AlarmResponseDto
) {

}