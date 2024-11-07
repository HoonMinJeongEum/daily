package com.example.diarytablet.ui.components

import MissionItemRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.example.diarytablet.ui.theme.DeepPastelBlue
import java.time.LocalDateTime

@Composable
fun AlarmModal(
    navController: NavController,
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    alarmItems: List<AlarmResponseDto>,
    onConfirmClick: (Long) -> Unit
) {
    if (isModalVisible) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .width(600.dp)
                    .background(Color.White, shape = RoundedCornerShape(30.dp)) // 둥근 흰색 박스
                    .padding(vertical = 44.dp, horizontal = 32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

//                    Image(
//                        painter = painterResource(id = R.drawable.mission_close),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(24.dp)
//                            .clickable { onDismiss() }
//                    )
//

//                    Spacer(modifier = Modifier.height(10.dp))

                    // 알림 리스트
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(alarmItems.size) { index ->
                            AlarmItem(
                                navController = navController,
                                alarmItem = alarmItems[index],
                                onConfirmClick = {alarmId ->
                                    onConfirmClick(alarmId)}

                                )
                            if (index < alarmItems.size - 1) { // 마지막 아이템이 아니면 구분선 추가
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color.LightGray)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmItem(
    navController: NavController,
    alarmItem: AlarmResponseDto,
    onConfirmClick: (Long) -> Unit

) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = alarmItem.body,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = alarmItem.createdAt.toString(),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        BasicButton(
            onClick = {
                onConfirmClick(alarmItem.id)
                navController.navigate("") {
                    popUpTo("main") { inclusive = true }
                }
                      },
            imageResId = 11,
            ButtonColor = if (alarmItem.confirmedAt != null) Color.Gray else DeepPastelBlue,
            enabled = if (alarmItem.confirmedAt != null) false else true,
            text = if (alarmItem.confirmedAt != null) "확인 완료" else "보러 가기",
        )
    }
}
//
//@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
//@Composable
//fun PreviewAlarmDialog() {
//    AlarmModal(
//        isModalVisible = true,
//        onDismiss = {},
//        alarmItems = listOf(
//            AlarmResponseDto(1, "Title1", "Name1", "알림 제목 1", "알림 내용 1", LocalDateTime.now(), null),
//            AlarmResponseDto(2, "Title2", "Name2", "알림 제목 2", "알림 내용 2", LocalDateTime.now(), LocalDateTime.now())
//        ),
//        onConfirmClick = {},
//    )
//}