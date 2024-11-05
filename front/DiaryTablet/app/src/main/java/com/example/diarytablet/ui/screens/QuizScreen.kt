package com.example.diarytablet.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.QuizViewModel
import androidx.compose.ui.Modifier
import com.example.diarytablet.ui.components.Draw
import com.example.diarytablet.ui.components.Video

@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: QuizViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {

    BackgroundPlacement(backgroundType = backgroundType)
    Video(
        modifier = Modifier,
        viewModel = viewModel
    )
//    Scaffold(
//        modifier = Modifier.fillMaxSize()
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            Draw(
//                modifier = Modifier.fillMaxSize(),
//                viewModel = viewModel
//            )
//        }
//    }
}