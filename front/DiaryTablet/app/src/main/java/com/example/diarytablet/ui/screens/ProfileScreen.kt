package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.ui.components.ProfileList
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen (
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    backgroundType: BackgroundType= BackgroundType.DEFAULT) {

    BackgroundPlacement(backgroundType = backgroundType)
    val profileList by viewModel.profileList

    fun chooseProfile(profile: Profile) {
        val selectProfileRequestDto = SelectProfileRequestDto(memberId = profile.id)
        viewModel.selectProfile(selectProfileRequestDto) { isSuccess ->  // 콜백 추가
            if (isSuccess) {
                navController.navigate("main") {
                    popUpTo("profileList") { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .aspectRatio(1.67f)
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        )
        ProfileList(
            profileList = profileList,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 70.dp),
            onChooseProfile = {profile -> chooseProfile(profile)},

        )
    }
}
