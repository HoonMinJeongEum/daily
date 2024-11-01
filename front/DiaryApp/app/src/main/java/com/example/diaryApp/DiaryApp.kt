package com.example.diaryApp

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diaryApp.ui.screens.JoinScreen
import com.example.diaryApp.ui.screens.LandingScreen
import com.example.diaryApp.ui.screens.LoginScreen
import com.example.diaryApp.ui.screens.MainScreen
import com.example.diaryApp.ui.theme.DiaryAppTheme
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryMobileApp() {
    val navController = rememberNavController()

    DiaryAppTheme() {
        NavHost(navController, startDestination = "landing") {
            composable("landing") {
                LandingScreen(navController = navController)
            }
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("join") {
                JoinScreen(navController = navController)
            }
            composable("main" ) {
                MainScreen(navController = navController)
            }
        }
    }
}

@HiltAndroidApp
class DiaryApp : Application() {}
