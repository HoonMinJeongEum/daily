package com.example.diarytablet

import LoginScreen
import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.screens.ProfileScreen
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.ui.theme.DiaryTabletTheme

import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()

    DiaryTabletTheme {
        NavHost(navController, startDestination = "profileList") {
            composable("login") {
                LoginScreen(
                    navController = navController)
            }

            composable("profileList") {
                ProfileScreen(
                    navController = navController
                )
            }
            composable("main") {
                MainScreen(navController = navController)
            }
        }
    }
}



@HiltAndroidApp
class DiaryTablet : Application() {}
