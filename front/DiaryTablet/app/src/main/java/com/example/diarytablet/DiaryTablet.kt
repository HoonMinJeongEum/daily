package com.example.diarytablet

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.ui.screens.LoginScreen
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.viewmodel.LoginViewModel
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()
    val viewModel: LoginViewModel = hiltViewModel()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel) {
                navController.navigate("main")
            }
        }
        composable("main") {
            MainScreen()
        }
    }
}

@HiltAndroidApp
class DiaryTablet : Application() {}
