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
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.ui.theme.DiaryTabletTheme
import com.example.diarytablet.viewmodel.LoginViewModel
import com.example.diarytablet.viewmodel.MainViewModel
import dagger.hilt.android.HiltAndroidApp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel() // 로그인 뷰모델을 생성합니다.

    DiaryTabletTheme {
        NavHost(navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    backgroundType = BackgroundType.DEFAULT // 여기에 적절한 BackgroundType 값을 추가
                )
            }
            composable("main") {
                // 메인 화면을 위한 뷰모델을 생성합니다.
                val mainViewModel: MainViewModel = hiltViewModel()
                MainScreen(viewModel = mainViewModel)
            }
        }
    }
}



@HiltAndroidApp
class DiaryTablet : Application() {}
