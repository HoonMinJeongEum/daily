package com.example.diarytablet.ui.components.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.MissionItem


@Composable
fun MissionModal(
    screenWidth : Dp,
    screenHeight : Dp,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit,
    missionItems: List<MissionItem>
) {
    if (isDialogVisible) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Box(
                modifier = Modifier
                    .width(screenHeight * 0.7f)
                    .background(Color(0xFFDEE5D4), shape = RoundedCornerShape(10)) // 모달 배경색 및 모서리
                    .padding(screenHeight * 0.03f)
            ) {
                // 닫기 버튼
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .clickable { onDismiss() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mission_close),
                        contentDescription = null,
                        modifier = Modifier.size(screenHeight * 0.06f)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(vertical = screenHeight * 0.06f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.05f)
                ) {
                    // "오늘의 미션" 제목
                    Text(
                        text = "오늘의 미션",
                        color = Color(0xFF49566F),
                        fontSize = (screenHeight * 0.045f).value.sp,
                        modifier = Modifier.padding(bottom = screenHeight * 0.015f)
                    )

                    // 각 미션 항목을 반복하여 표시
                    missionItems.forEach { missionItem ->
                        MissionItemRow(screenWidth, screenHeight, missionItem)
                    }
                }
            }
        }
    }
}


@Composable
fun MissionItemRow(screenWidth: Dp, screenHeight: Dp, missionItem: MissionItem) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(12))
            .padding(horizontal = screenHeight * 0.03f, vertical = screenHeight * 0.015f)
            .fillMaxWidth(0.8f),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 미션 이름
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = missionItem.text,
                fontSize = (screenHeight * 0.04f).value.sp,
                color = Color(0xFF49566F)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,

            ) {


            val imageRes =
                if (missionItem.isSuccess) R.drawable.mission_clear_modal else R.drawable.mission_noclear_modal
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(screenHeight * 0.06f)
            )

            if (missionItem.isSuccess) {
                Text(
                    text = "+10",
                    color = Color(0xFF339900),
                    fontSize = (screenHeight * 0.04f).value.sp
                )
            } else {
                Text(
                    text = "+10",
                    color = Color.White,
                    fontSize = (screenHeight * 0.04f).value.sp
                )
            }
        }
        }
    }

