package com.example.diaryApp

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.RetrofitClient
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.screens.CatchMindScreen
import com.example.diaryApp.ui.screens.DiaryDetailScreen
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
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryMobileApp(
    startDestination: String = "login",
    navController: NavHostController,

) {
    val diaryViewModel: DiaryViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val wordViewModel: WordViewModel = hiltViewModel()

    DiaryAppTheme() {
        NavHost(navController, startDestination = "landing") {
            composable("landing") {
                LandingScreen(startDestination = startDestination,navController = navController)
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
            composable("catchMind/{sessionId}") { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId")
                sessionId?.let {
                    CatchMindScreen(navController = navController, sessionId = it)
                }
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
            composable("diary/{diaryId}") { backStackEntry ->
                val diaryId = backStackEntry.arguments?.getString("diaryId")
                DiaryDetailScreen(navController = navController, diaryId = diaryId, diaryViewModel = diaryViewModel)
            }
        }
    }
}

@HiltAndroidApp
class DiaryApp : Application() {
    @Inject
    lateinit var userStore: UserStore


    companion object {
        lateinit var instance: DiaryApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // UserStore 초기화 확인 후 RetrofitClient 초기화
        if (::userStore.isInitialized) {
            RetrofitClient.init(userStore)
        } else {
            throw IllegalStateException("UserStore is not initialized.")
        }
    }


    }
