package com.example.diarytablet

import DiaryScreen
import LoginScreen
import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.screens.ProfileScreen
import com.example.diarytablet.ui.screens.RecordScreen
import com.example.diarytablet.ui.screens.QuizScreen
import com.example.diarytablet.ui.screens.ShopScreen
import com.example.diarytablet.ui.screens.StockScreen
import com.example.diarytablet.ui.screens.WordLearningScreen
import com.example.diarytablet.ui.theme.DiaryTabletTheme

import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()

    DiaryTabletTheme {
        NavHost(navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    navController = navController)
            }
            composable("profileList") {
                ProfileScreen(
                    navController = navController
                )
            }
            composable(
                "main?origin={origin}&isFinished={isFinished}",
                arguments = listOf(
                    navArgument("origin") { type = NavType.StringType; defaultValue = "Unknown" },
                    navArgument("isFinished") { type = NavType.BoolType; defaultValue = false }
                )
            ) {
                MainScreen(navController = navController)
            }
            composable("shop"){
                ShopScreen(navController = navController)
            }
            composable("stock"){
                StockScreen(navController = navController)
            }
            composable("record") {
                RecordScreen(navController = navController)
            }
            composable("diary") {
                DiaryScreen(navController = navController)
            }
            composable("wordLearning") {
                WordLearningScreen(navController = navController)
            }
            composable("quiz") {
                QuizScreen(navController = navController)
            }
        }
    }
}



@HiltAndroidApp
class DiaryTablet : Application() {
    @Inject
    lateinit var userStore: UserStore

    companion object {
        lateinit var instance: DiaryTablet
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (::userStore.isInitialized) {
            RetrofitClient.init(userStore)
        } else {
            throw IllegalStateException("UserStore is not initialized.")
        }
    }
}
