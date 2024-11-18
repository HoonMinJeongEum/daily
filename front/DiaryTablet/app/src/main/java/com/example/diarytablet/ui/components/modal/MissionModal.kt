package com.example.diarytablet.ui.components.modal

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.MissionItem
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.MainViewModel


@Composable
fun MissionModal(
    lastMissions : List<MissionItem>,
    screenWidth : Dp,
    screenHeight : Dp,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit,
    missionItems: List<MissionItem>
) {
    val isOnlyOneFalse = lastMissions.count { !it.isSuccess } == 1
    val specialMission = if (isOnlyOneFalse) {
        lastMissions.find { !it.isSuccess && missionItems.any { mission -> mission.text == it.text && mission.isSuccess } }
    } else {
        null
    }

    val context = LocalContext.current

    LaunchedEffect(isDialogVisible) {
        if (isDialogVisible) {
            playButtonSound(context,R.raw.main_clear )

        }
    }

    if (isDialogVisible) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Box(
                modifier = Modifier
                    .width(screenHeight * 0.7f)
                    .background(Color(0xFFDEE5D4), shape = RoundedCornerShape(10)) // 모달 배경색 및 모서리
                    .padding(horizontal = screenHeight * 0.03f)
                    .padding(top =  screenHeight * 0.03f , bottom = screenHeight * 0.1f)
            ) {
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
                    )

                    if (specialMission != null) {

                        Box(
                            modifier = Modifier.fillMaxWidth()
                            ,
                            contentAlignment = Alignment.BottomStart // 캐릭터를 하단에 배치
                        ) {
                            // 말풍선
                            Box(
                                modifier = Modifier
                                    .width(screenHeight * 0.35f)
                                    .background(Color.Transparent)
                                    .align(Alignment.TopEnd)
                                    .offset(x = -screenHeight * 0.05f)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.text_balloon),
                                    contentDescription = "Text Balloon",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(screenHeight * 0.02f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f) // 텍스트와 조개 간격
                                ) {
                                    // "모든 미션 완료!" 텍스트
                                    Text(
                                        text = "모든 미션 완료!",
                                        color = Color(0xFF49566F),
                                        fontSize = (screenHeight * 0.035f).value.sp,
                                        lineHeight = (screenHeight * 0.05f).value.sp
                                    )
                                    // 조개 아이콘과 +25 텍스트
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .background(Color.White, shape = RoundedCornerShape(8.dp)) // 흰색 배경 추가
                                            .padding(horizontal = screenHeight * 0.02f, vertical = screenHeight * 0.01f)
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.mission_clear_modal),
                                            contentDescription = "Shell Icon",
                                            modifier = Modifier.size(screenHeight * 0.05f)
                                        )
                                        Spacer(modifier = Modifier.width(screenHeight * 0.02f))
                                        Text(
                                            text = "+25",
                                            color = Color(0xFF339900),
                                            fontSize = (screenHeight * 0.03f).value.sp
                                        )
                                    }
                                }
                            }

                            // 캐릭터 이미지
                            Image(
                                painter = painterResource(R.drawable.main_char),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(screenHeight * 0.25f)
                                    .align(Alignment.BottomStart)
                                    .offset(x = screenHeight * 0.03f,  y = screenHeight * 0.1f)
                            )
                        }



                    } else {
                        // 각 미션 항목을 반복하여 표시
                        missionItems.forEach { missionItem ->
                            MissionItemRow(screenWidth, screenHeight, missionItem)
                        }
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

