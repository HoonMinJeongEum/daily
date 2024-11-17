package com.example.diarytablet.ui.components


import android.util.Log
import com.example.diarytablet.ui.components.modal.ProfileModal
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.ui.components.modal.AlarmModal
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.MainViewModel
import com.example.diarytablet.viewmodel.NavBarViewModel

@Composable
fun Navbar(
    viewModel: NavBarViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
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
    val context = LocalContext.current

    LaunchedEffect(isAlarmOn) {
        if (isAlarmOn) {
            playButtonSound(context, R.raw.alarm )

        }
    }
    LaunchedEffect(Unit) {
        viewModel.initializeData()
    }

    LaunchedEffect(isProfileModalVisible) {
        if (!isProfileModalVisible) {
            viewModel.loadStatus()
            mainViewModel.loadStatus()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f), // 여백 조정을 화면 비율에 맞춤
        horizontalArrangement = Arrangement.SpaceBetween, // 버튼 간 간격 조정
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicButton(
            onClick = {},
            text = shellCount.toString(),
            isOutlined = false,
            enabled = false,
            useDisabledColor = true
        )
        AlarmButton(
            modifier = Modifier
                .weight(1f),
            isAlarmOn = isAlarmOn,
            onClick = {
                viewModel.getAlarms {
                    isAlarmModalVisible = true
                }
                viewModel.setAlarmState(false)
            }
        )
        Profile(
            modifier = Modifier
                .weight(1f),
            onProfileClick = { isProfileMenuVisible = true },
            imageUrl = profileImageUrl
        )
    }

    ProfileModal(
        isModalVisible = isProfileModalVisible,
        onDismiss = {
            isProfileModalVisible = false
        },
        profileImageUrl = profileImageUrl,
        userName = userName,
        onEditProfileClick = { file -> viewModel.updateProfileImage(file) },
        onEditNameClick = { newName -> viewModel.updateUserName(newName) },
        screenWidth = screenWidth,
        screenHeight = screenHeight
    )

    AlarmModal(
        isModalVisible = isAlarmModalVisible,
        onDismiss = { isAlarmModalVisible = false },
        alarmItems = alarms,
        onConfirmClick = { alarmId -> viewModel.checkAlarm(alarmId) },
        navController = navController,
        screenWidth = screenWidth,
        screenHeight = screenHeight
    )

    if (isProfileMenuVisible) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(-100,200),
            properties = PopupProperties(focusable = false , dismissOnClickOutside = true),
            onDismissRequest = { isProfileMenuVisible = false }

        ) {
             Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = screenWidth * 0.03f, vertical = screenHeight * 0.05f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
                ) {
                    Text(
                        text = "내 정보 수정",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.025f).sp,
                            color = DeepPastelNavy
                        ),
                        modifier = Modifier
                            .clickable {
                                playButtonSound(context, R.raw.all_button )
                                isProfileModalVisible = true
                                isProfileMenuVisible = false
                            }
                            .padding(vertical = screenHeight * 0.01f)
                    )
                    Text(
                        text = "프로필 전환",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.025f).sp,
                            color = DeepPastelNavy
                        ),
                        modifier = Modifier
                            .clickable {
                                playButtonSound(context, R.raw.all_button )
                                navController.navigate("profileList") {
                                    popUpTo("main") { inclusive = true }
                                }
                                isProfileMenuVisible = false
                            }
                            .padding(vertical = screenHeight * 0.01f)
                    )
                    Text(
                        text = "로그아웃",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.025f).sp,
                            color = DeepPastelNavy
                        ),
                        modifier = Modifier
                            .clickable {
                                playButtonSound(context, R.raw.all_button )
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


