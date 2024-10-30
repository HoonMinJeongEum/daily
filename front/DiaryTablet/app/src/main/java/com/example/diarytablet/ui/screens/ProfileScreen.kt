package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(254.dp, 153.dp)
                .offset(x = 511.dp, y = 79.dp)
        )
        ProfileList(
            profileList = viewModel.profileList.value
        )
    }
}
