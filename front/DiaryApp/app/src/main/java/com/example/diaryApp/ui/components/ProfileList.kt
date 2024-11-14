package com.example.diaryApp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.domain.dto.response.profile.Profile
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.viewmodel.ProfileViewModel
import com.example.diaryApp.viewmodel.QuizViewModel
import com.example.diaryApp.viewmodel.WordViewModel

@Composable
fun ProfileList(
    screenHeight: Dp,
    screenWidth: Dp,
    modifier: Modifier = Modifier,
    profileList: List<Profile>,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    diaryViewModel: DiaryViewModel,
    wordViewModel: WordViewModel,
    quizViewModel: QuizViewModel,
    onShowQuizAlert: (String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
            profileViewModel.loadProfiles()
            }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(profileList) { profile ->
            ProfileItem(
                profile = profile,
                navController = navController,
                diaryViewModel = diaryViewModel,
                wordViewModel = wordViewModel,
                quizViewModel = quizViewModel,
                onShowQuizAlert = onShowQuizAlert,
                screenHeight = screenHeight,
                screenWidth = screenWidth,
            )
        }

        item {
            Spacer(modifier = Modifier.height(screenHeight * 0.015f))
            if (profileList.size <= 3) {
                AddProfileButton(onClick = { showDialog = true })
            }

            if (showDialog) {
                CreateProfile(
                    screenHeight = screenHeight,
                    screenWidth = screenWidth,
                    profileViewModel = profileViewModel,
                    onCancel = { showDialog = false }
                )
            }
        }
    }
}


@Composable
fun DeleteProfileList(
    modifier: Modifier = Modifier,
    profileList : List<Profile>,
    viewModel: ProfileViewModel
) {
    Log.d("ProfileScreen", "${profileList}")

    LaunchedEffect(Unit) {
        viewModel.loadProfiles()
    }

    LazyColumn (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(profileList) { profile ->
            DeleteProfileItem(profile)
        }
    }
}