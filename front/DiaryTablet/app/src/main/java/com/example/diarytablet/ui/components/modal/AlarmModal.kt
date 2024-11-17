package com.example.diarytablet.ui.components.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.theme.DeepPastelBlue
import com.example.diarytablet.ui.theme.PastelNavy
import java.time.format.DateTimeFormatter

@Composable
fun AlarmModal(
    navController: NavController,
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    alarmItems: List<AlarmResponseDto>,
    onConfirmClick: (Long) -> Unit,
    screenWidth: Dp,
    screenHeight: Dp
) {
    if (isModalVisible) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(-350,200),
            onDismissRequest = { onDismiss() },
            properties = PopupProperties(focusable = true, dismissOnClickOutside = true)
        ) {
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.4f)
                    .heightIn(max = screenHeight * 0.6f)
                    .background(Color.White, shape = RoundedCornerShape(screenWidth * 0.02f))
                    .padding(vertical = screenHeight * 0.05f, horizontal = screenWidth * 0.02f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
                ) {
                    if (alarmItems.isEmpty()) {
                        // 알림이 없는 경우 표시
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.3f), // 높이를 적절히 설정
                            contentAlignment = Alignment.Center // 중앙 정렬 설정
                        ) {
                            Text(
                                text = "알림이 없습니다",
                                fontSize = (screenWidth.value * 0.025f).sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(alarmItems.size) { index ->
                                AlarmItem(
                                    navController = navController,
                                    alarmItem = alarmItems[index],
                                    onConfirmClick = { alarmId ->
                                        onConfirmClick(alarmId)
                                    },
                                    screenWidth = screenWidth,
                                    screenHeight = screenHeight
                                )
                                if (index < alarmItems.size - 1) {
                                    Spacer(modifier = Modifier.height(screenHeight * 0.02f))
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
}

@Composable
fun AlarmItem(
    navController: NavController,
    alarmItem: AlarmResponseDto,
    onConfirmClick: (Long) -> Unit,
    screenWidth: Dp,
    screenHeight: Dp
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val displayDate = alarmItem.createdAt.format(dateTimeFormatter)

    Row(
        modifier = Modifier.fillMaxWidth(),
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
                fontSize = (screenWidth.value * 0.018f).sp,
                color = PastelNavy
            )
            Spacer(modifier = Modifier.height(screenHeight * 0.02f))
            Text(
                text = displayDate,
                fontSize = (screenWidth.value * 0.012f).sp,
                color = Color.Gray
            )
        }
        BasicButton(
            onClick = {
                onConfirmClick(alarmItem.id)
                navController.navigate("record/${alarmItem.titleId}") {
                    popUpTo("main") { inclusive = false }
                }
            },
            imageResId = 11,
            ButtonColor = if (alarmItem.confirmedAt != null) Color.Gray else DeepPastelBlue,
            enabled = alarmItem.confirmedAt == null,
            text = if (alarmItem.confirmedAt != null) "확인 완료" else "보러 가기",
        )
    }
}
