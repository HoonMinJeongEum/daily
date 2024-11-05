package com.example.diarytablet

import DiaryScreen
import LoginScreen
import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.screens.ProfileScreen
import com.example.diarytablet.ui.screens.ShopScreen
import com.example.diarytablet.ui.screens.StockScreen
import com.example.diarytablet.ui.screens.WordLearningScreen
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.ui.theme.DiaryTabletTheme

import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()

    DiaryTabletTheme {
        NavHost(navController, startDestination = "wordLearning") {
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
            composable("shop"){
                ShopScreen(navController = navController)
            }
            composable("stock"){
                StockScreen(navController = navController)
            }
<<<<<<< HEAD
            composable("diary"){
                DiaryScreen(navController = navController)
            }
=======
            composable("diary") {
                DiaryScreen(navController = navController)
            }

            composable("wordLearning") {
                WordLearningScreen(navController = navController)
            }
>>>>>>> 1a83e2854da8034d5cecf68e5e995ad0baf1aee7
        }
    }
}



@HiltAndroidApp
class DiaryTablet : Application() {}
