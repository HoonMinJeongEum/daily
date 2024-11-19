package com.example.diarytablet

import DiaryScreen
import LoginScreen
import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.ui.components.modal.CommonModal
import com.example.diarytablet.ui.components.quiz.Alert
import com.example.diarytablet.ui.screens.LandingScreen
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.screens.ProfileScreen
import com.example.diarytablet.ui.screens.RecordScreen
import com.example.diarytablet.ui.screens.QuizScreen
import com.example.diarytablet.ui.screens.ShopScreen
import com.example.diarytablet.ui.screens.StockScreen
import com.example.diarytablet.ui.screens.WordLearningScreen
import com.example.diarytablet.ui.theme.DiaryTabletTheme
import com.example.diarytablet.utils.clearFocusOnClick
import com.example.diarytablet.viewmodel.SpenEventViewModel
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenUnitManager

import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp(
    startDestination: String = "login",
    spentEventViewmodel : SpenEventViewModel,
    navController: NavHostController,
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler {
        showExitDialog = true
    }
    DiaryTabletTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnClick()
        ) {
            NavHost(navController, startDestination = "landing") {
                composable("landing") {
                    LandingScreen(
                        startDestination = startDestination,
                        navController = navController
                    )
                }
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
                composable("record/{titleId}") { backStackEntry ->
                    val titleId = backStackEntry.arguments?.getString("titleId")?.toIntOrNull() ?: -1
                    RecordScreen(navController = navController, titleId = titleId)
                }

                composable("record") {
                    RecordScreen(
                        navController = navController,
                        titleId = -1
                    )
                }
                composable("diary") {
                    DiaryScreen(navController = navController, spenEventViewModel = spentEventViewmodel)
                }
                composable("wordLearning") {
                    WordLearningScreen(navController = navController, spenEventViewModel = spentEventViewmodel)
                }
                composable("quiz") {
                    QuizScreen(navController = navController, spenEventViewModel = spentEventViewmodel)
                }
            }
    }
    if (showExitDialog) {
        CommonModal(
            onDismissRequest = { showExitDialog = false },
            titleText = "앱을 종료하시겠어요?",
            confirmText= "종료",
            onConfirm = {
                activity?.finishAffinity()
            }
        )
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

        // UserStore 초기화 확인 후 RetrofitClient 초기화
        if (::userStore.isInitialized) {
            RetrofitClient.init(userStore)
        } else {
            throw IllegalStateException("UserStore is not initialized.")
        }


    }


}




