package com.example.diarytablet.ui.components


import ProfileModal
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.Profile
import com.example.diarytablet.ui.components.AlarmButton
import com.example.diarytablet.viewmodel.NavBarViewModel

@Composable
fun Navbar(
    viewModel: NavBarViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavController,
    screenWidth: Dp,
    screenHeight: Dp
) {
    val shellCount by viewModel.shellCount
    val profileImageUrl by viewModel.profileImageUrl
    val isAlarmOn by viewModel.isAlarmOn
    val userName by viewModel.userName
    val alarms by viewModel.alarms
    var isProfileMenuVisible by remember { mutableStateOf(false) }
    var isProfileModalVisible by remember { mutableStateOf(false) }
    var isAlarmModalVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(screenWidth * 0.02f)
            .padding(start = screenWidth * 0.03f)
        , // 여백 조정을 화면 비율에 맞춤
        horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.015f), // 버튼 간 간격 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicButton(
            onClick = {},
            text = shellCount.toString(),
            isOutlined = false
        )
        AlarmButton(
            isAlarmOn = isAlarmOn,
            onClick = {
                viewModel.getAlarms {
                    isAlarmModalVisible = true
                }
                viewModel.setAlarmState(false)
            }
        )
        Profile(
            onProfileClick = { isProfileMenuVisible = true },
            imageUrl = profileImageUrl
        )
    }

    ProfileModal(
        isModalVisible = isProfileModalVisible,
        onDismiss = { isProfileModalVisible = false },
        profileImageUrl = profileImageUrl,
        userName = userName,
        onEditNameClick = { newName -> viewModel.updateUserName(newName) }
    )

    AlarmModal(
        isModalVisible = isAlarmModalVisible,
        onDismiss = { isAlarmModalVisible = false },
        alarmItems = alarms,
        onConfirmClick = { alarmId -> viewModel.checkAlarm(alarmId) },
        navController = navController
    )

    if (isProfileMenuVisible) {
        Dialog(
            onDismissRequest = { isProfileMenuVisible = false },
            properties = DialogProperties(
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = screenWidth * 0.04f, vertical = screenHeight * 0.1f) // 다이얼로그 크기를 화면 비율에 맞춤
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
                ) {
                    Text(
                        text = "내 정보 수정",
                        modifier = Modifier
                            .clickable {
                                isProfileModalVisible = true
                                isProfileMenuVisible = false
                            }
                            .padding(vertical = screenHeight * 0.01f)
                    )
                    Text(
                        text = "프로필 전환",
                        modifier = Modifier
                            .clickable {
                                navController.navigate("profileList") {
                                    popUpTo("main") { inclusive = true }
                                }
                                isProfileMenuVisible = false
                            }
                            .padding(vertical = screenHeight * 0.01f)
                    )
                    Text(
                        text = "로그아웃",
                        modifier = Modifier
                            .clickable {
                                RetrofitClient.logout()
                                isProfileMenuVisible = false
                                navController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                            .padding(vertical = screenHeight * 0.01f)
                    )
                }
            }
        }
    }
}


