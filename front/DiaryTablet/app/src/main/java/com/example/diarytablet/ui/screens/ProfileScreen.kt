package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.ui.components.ProfileList
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)
    val profileList by viewModel.profileList

    fun chooseProfile(profile: Profile) {
        val selectProfileRequestDto = SelectProfileRequestDto(memberId = profile.id)
        viewModel.selectProfile(selectProfileRequestDto) { isSuccess ->
            if (isSuccess) {
                navController.navigate("main") {
                    popUpTo("profileList") { inclusive = true }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // 로고 이미지 위치와 크기 조정
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(screenWidth * 0.26f)
                .aspectRatio(1.67f)
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * 0.08f)
        )

        // ProfileList 위치 조정
        ProfileList(
            profileList = profileList,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = screenHeight * 0.2f),
            onChooseProfile = { profile -> chooseProfile(profile) }
        )
    }
}
