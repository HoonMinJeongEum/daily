package com.example.diaryApp

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.screens.CatchMindScreen
import com.example.diaryApp.ui.screens.DiaryScreen
import com.example.diaryApp.ui.screens.JoinScreen
import com.example.diaryApp.ui.screens.LandingScreen
import com.example.diaryApp.ui.screens.LoginScreen
import com.example.diaryApp.ui.screens.MainScreen
import com.example.diaryApp.ui.screens.NotificationScreen
import com.example.diaryApp.ui.screens.SettingScreen
import com.example.diaryApp.ui.screens.ShoppingScreen
import com.example.diaryApp.ui.screens.WordScreen
import com.example.diaryApp.ui.theme.DiaryAppTheme
import com.example.diaryApp.viewmodel.ProfileViewModel
import com.example.diaryApp.viewmodel.WordViewModel
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryMobileApp(
    navController: NavHostController,
    diaryViewModel: DiaryViewModel,
    profileViewModel: ProfileViewModel,
    wordViewModel: WordViewModel
) {
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
                MainScreen(navController = navController, profileViewModel, diaryViewModel, wordViewModel)
            }
            composable("catchMind") {
                CatchMindScreen(navController = navController)
            }
            composable("diary") {
                DiaryScreen(navController = navController, diaryViewModel = diaryViewModel)
            }
            composable("notification") {
                NotificationScreen(navController = navController)
            }
            composable("shop") {
                ShoppingScreen(navController = navController)
            }
            composable("word") {
                WordScreen(navController = navController, wordViewModel = wordViewModel)
            }
            composable("setting") {
                SettingScreen(navController = navController)
            }
        }
    }
}

@HiltAndroidApp
class DiaryApp : Application() {}
