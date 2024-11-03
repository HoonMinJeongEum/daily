package com.example.diarytablet.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.ui.components.WordTap
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.MainViewModel
import com.example.diarytablet.viewmodel.WordLearningViewModel

@Composable
fun WordLearningScreen(
    navController: NavController,
    viewModel: WordLearningViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        WordTap (
            imageUrl = "https://example.com/your_image.jpg",
            onButtonClick = {}
        )
    }
}