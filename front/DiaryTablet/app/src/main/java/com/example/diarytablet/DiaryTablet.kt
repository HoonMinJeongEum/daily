package com.example.diarytablet

import LoginScreen
import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.screens.ProfileScreen
import com.example.diarytablet.ui.screens.ShopScreen
import com.example.diarytablet.ui.screens.StockScreen
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.ui.theme.DiaryTabletTheme
import com.example.diarytablet.viewmodel.LoginViewModel
import com.example.diarytablet.viewmodel.MainViewModel
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()

    DiaryTabletTheme {
        NavHost(navController, startDestination = "main") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    navController = navController,
                    backgroundType = BackgroundType.DEFAULT // 여기에 적절한 BackgroundType 값을 추가
                )
            }

            composable("profileList") {
                ProfileScreen(
                    navController = navController
                )
            }
            composable("main") {
                // 메인 화면을 위한 뷰모델을 생성합니다.
                MainScreen(navController = navController)
            }
            composable("shop"){
                ShopScreen(navController = navController)
            }
            composable("stock"){
                StockScreen(navController = navController)
            }
        }
    }
}



@HiltAndroidApp
class DiaryTablet : Application() {}
