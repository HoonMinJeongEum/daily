package com.example.diarytablet

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.theme.DiaryTabletTheme
import com.example.diarytablet.viewmodel.MainViewModel
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()

    DiaryTabletTheme {
        NavHost(navController, startDestination = "main") {
//            composable("login") {
//                LoginScreen(viewModel) {
//                    navController.navigate("main")
//                }
//            }
            composable("main") {
                MainScreen(viewModel)
            }
        }
    }
}

@HiltAndroidApp
class DiaryTablet : Application() {}
