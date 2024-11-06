package com.example.diarytablet.ui.components

import ProfileModal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.Profile
import com.example.diarytablet.ui.components.AlarmButton
import com.example.diarytablet.viewmodel.NavBarViewModel

@Composable
fun Navbar(
    viewModel: NavBarViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val shellCount by viewModel.shellCount
    val profileImageUrl by viewModel.profileImageUrl
    val isAlarmOn by viewModel.isAlarmOn
    val userName by viewModel.userName
    var isModalVisible by remember { mutableStateOf(false) }


    Row(
        modifier = Modifier
            .padding(20.dp), // 여백 조정
        horizontalArrangement = Arrangement.spacedBy(16.dp), // 버튼 간 간격 조정
        verticalAlignment = Alignment.CenterVertically // 수직 정렬
    ) {
        BasicButton(
            onClick = {},
            text = shellCount.toString(),
            isOutlined = false
        )
        AlarmButton(
            isAlarmOn = isAlarmOn,
            onClick = {}
        )
        Profile(
            onProfileClick = {isModalVisible = true},
            imageUrl = profileImageUrl
        )
    }

    if (isModalVisible) {
        ProfileModal(
            isModalVisible = isModalVisible,
            onDismiss = { isModalVisible = false },
            profileImageUrl = profileImageUrl,
            userName = userName,
            onEditNameClick = { newName -> viewModel.updateUserName(newName) }
        )
    }
}



