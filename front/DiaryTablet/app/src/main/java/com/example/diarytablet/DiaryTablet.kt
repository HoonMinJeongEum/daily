package com.example.diarytablet

import DiaryScreen
import LoginScreen
import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.ui.components.quiz.Alert
import com.example.diarytablet.ui.screens.MainScreen
import com.example.diarytablet.ui.screens.ProfileScreen
import com.example.diarytablet.ui.screens.RecordScreen
import com.example.diarytablet.ui.screens.QuizScreen
import com.example.diarytablet.ui.screens.ShopScreen
import com.example.diarytablet.ui.screens.StockScreen
import com.example.diarytablet.ui.screens.WordLearningScreen
import com.example.diarytablet.ui.theme.DiaryTabletTheme
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenUnitManager

import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiaryTabletApp(startDestination: String = "login") {
    val navController = rememberNavController()
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler {
        showExitDialog = true
    }

    DiaryTabletTheme {
        NavHost(navController, startDestination = startDestination) {
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
            composable(
                "record?titleId={titleId}",
                arguments = listOf(
                    navArgument("titleId") { type = NavType.IntType; defaultValue = -1 },
                )
            ) {
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
    if (showExitDialog) {
        Alert(
            isVisible = true,
            onDismiss = { showExitDialog = false },
            onConfirm = { activity?.finishAffinity() },
            title = "앱을 종료하시겠어요?",
            confirmText = "종료"
        )
    }
}



@HiltAndroidApp
class DiaryTablet : Application() {
    @Inject
    lateinit var userStore: UserStore

    var spenRemote: SpenRemote? = null
    var spenUnitManager: SpenUnitManager? = null

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

        // S Pen Remote 초기화 및 연결 시도 (삼성 기기에서만)
        if (isSamsungDevice()) {
//            initializeSpenRemote()
        } else {
            Log.d("DiaryTablet", "S Pen 기능은 삼성 기기에서만 지원됩니다.")
        }
    }

    private fun initializeSpenRemote() {
        try {
            spenRemote = SpenRemote.getInstance()
            // S Pen 기능 확인
            val isFeatureAvailable = spenRemote?.isFeatureEnabled(SpenRemote.FEATURE_TYPE_BUTTON) ?: false
            if (isFeatureAvailable) {
                Log.d("DiaryTablet", "S Pen Button 기능이 사용 가능합니다.")
                connectSpenRemote()
            } else {
                Log.d("DiaryTablet", "S Pen Button 기능을 지원하지 않습니다.")
            }
        } catch (e: NoClassDefFoundError) {
            Log.e("DiaryTablet", "S Pen 기능이 이 기기에서 지원되지 않습니다.", e)
        }
    }

    private fun connectSpenRemote() {
        spenRemote?.let { spen ->
            if (!spen.isConnected) {
                spen.connect(this, object : SpenRemote.ConnectionResultCallback {
                    override fun onSuccess(manager: SpenUnitManager?) {
                        spenUnitManager = manager
                        Log.d("DiaryTablet", "S Pen이 성공적으로 연결되었습니다.")
//                        Toast.makeText(context, "S Pen connected.", Toast.LENGTH_SHORT).show()

                    }

                    override fun onFailure(error: Int) {
                        val errorMsg = when (error) {
                            else -> "알 수 없는 오류입니다."
                        }
                        Log.e("DiaryTablet", "S Pen 연결 실패: $errorMsg")
//                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()

                    }
                })
            }
        }
    }

    private fun isSamsungDevice(): Boolean {
        return android.os.Build.MANUFACTURER.equals("Samsung", ignoreCase = true)
    }
}




