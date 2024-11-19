package com.example.diarytablet

import DiaryScreen
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.repository.UserRepository
//import com.example.diarytablet.utils.CustomGLView
import com.example.diarytablet.viewmodel.SpenEventViewModel
import com.samsung.android.sdk.SsdkVendorCheck.isSamsungDevice
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEventListener
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenUnit
import com.samsung.android.sdk.penremote.SpenUnitManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userStore: UserStore
    @Inject
    lateinit var userRepository: UserRepository
    private var spenRemote: SpenRemote? = null
    private var spenUnitManager: SpenUnitManager? = null
    private val spenEventViewModel: SpenEventViewModel by viewModels()
    private val TAG = "MainActivity"
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()
        if (isSamsungDevice()) {
            initializeSpenRemote()
        } else {
            Log.d(TAG, "S Pen 기능은 삼성 기기에서만 지원됩니다.")
        }

        val startDestination = runBlocking {
            val isAutoLoginEnabled = userStore.getAutoLoginState().firstOrNull() ?: false
            val username = userStore.getValue(UserStore.KEY_USER_NAME).firstOrNull()
            val password = userStore.getValue(UserStore.KEY_PASSWORD).firstOrNull()
            Log.d("start","${username} , ${password} , ${isAutoLoginEnabled}")
            if (isAutoLoginEnabled && !username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                val success = performLogin(username, password)
                Log.d("start","${success}")
                if (success) {
                    "profileList"
                } else "login"
            } else {
                "login"
            }
        }
//        val glView = CustomGLView(this)
//        setContentView(glView)
        setContent {
            navController = rememberNavController()
            DiaryTabletApp(startDestination = startDestination, spenEventViewModel, navController = navController as NavHostController)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val navigationTarget = intent.getStringExtra("navigation_target")
        if (!navigationTarget.isNullOrEmpty()) {
            navController.navigate(navigationTarget)
        }
    }

    private fun initializeSpenRemote() {
        try {
            spenRemote = SpenRemote.getInstance()
            val isFeatureAvailable = spenRemote?.isFeatureEnabled(SpenRemote.FEATURE_TYPE_BUTTON) ?: false
            if (isFeatureAvailable) {
                Log.d(TAG, "S Pen Button 기능이 사용 가능합니다.")
                connectSpenRemote()
            } else {
                Log.d(TAG, "S Pen Button 기능을 지원하지 않습니다.")
            }
        } catch (e: NoClassDefFoundError) {
            Log.e(TAG, "S Pen 기능이 이 기기에서 지원되지 않습니다.", e)
        } catch (e: SecurityException) {
            Log.e(TAG, "S Pen 권한이 부족합니다. AndroidManifest.xml에 권한을 추가하세요.", e)
        } catch (e: Exception) {
            Log.e(TAG, "S Pen 초기화 중 오류 발생", e)
        }
    }

    // S Pen 연결
    private fun connectSpenRemote() {
        spenRemote?.let { spen ->
            if (!spen.isConnected) {
                spen.connect(this, object : SpenRemote.ConnectionResultCallback {
                    override fun onSuccess(manager: SpenUnitManager?) {
                        spenUnitManager = manager
                        Log.d(TAG, "S Pen이 성공적으로 연결되었습니다.")
                        registerSpenButtonEventListener()  // S Pen 버튼 이벤트 리스너 등록
                    }

                    override fun onFailure(error: Int) {
                        val errorMsg = when (error) {
//                            SpenRemote.ConnectionResultCallback.UNSUPPORTED_DEVICE -> "지원되지 않는 기기입니다."
//                            SpenRemote.ConnectionResultCallback.CONNECTION_FAILED -> "연결 실패입니다."
//                            SpenRemote.ConnectionResultCallback.UNKNOWN -> "알 수 없는 오류입니다."
                            else -> "알 수 없는 오류입니다."
                        }
                        Log.e(TAG, "S Pen 연결 실패: $errorMsg")
                    }
                })
            }
        }
    }

    private fun registerSpenButtonEventListener() {
        try {
            val buttonUnit = spenUnitManager?.getUnit(SpenUnit.TYPE_BUTTON)
            buttonUnit?.let {
                if (spenRemote?.isFeatureEnabled(SpenRemote.FEATURE_TYPE_BUTTON) == true) {
                    spenUnitManager?.registerSpenEventListener(mButtonEventListener, it)
                    Log.d(TAG, "S Pen Button 이벤트 리스너가 등록되었습니다.")
                } else {
                    Log.d(TAG, "S Pen Button 기능은 이 기기에서 지원되지 않습니다.")
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 문제로 S Pen 버튼 이벤트 리스너를 등록할 수 없습니다.", e)
        } catch (e: Exception) {
            Log.e(TAG, "S Pen 버튼 이벤트 리스너 등록 중 예외 발생", e)
        }
    }



    // 버튼 이벤트 리스너
    private val mButtonEventListener = SpenEventListener { ev ->
        Log.d(TAG, "S Pen 버튼이 눌렸습니다.")
        lifecycleScope.launch {
            spenEventViewModel.emitSpenEvent(ev) // 이벤트 발행
        }
    }

    // S Pen 연결 해제
    fun disconnectSpenRemote() {
        spenRemote?.disconnect(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        val buttonUnit = spenUnitManager?.getUnit(SpenUnit.TYPE_BUTTON)
        spenUnitManager?.unregisterSpenEventListener(buttonUnit)
        disconnectSpenRemote()
        spenRemote = null
        spenUnitManager = null
    }



    private suspend fun performLogin(username: String, password: String): Boolean {
        val loginRequestDto = LoginRequestDto(username, password)
        return try {
            val response: Response<Void> = userRepository.login(loginRequestDto)
            if (response.isSuccessful) {
                val headers = response.headers()
                val accessToken = headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                val refreshToken = headers["Set-Cookie"]

                if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                    saveUserInfo(accessToken, refreshToken, username, password)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun saveUserInfo(accessToken: String, refreshToken: String, username: String, password: String) {
        RetrofitClient.login(accessToken, refreshToken)
        userStore.setValue(UserStore.KEY_USER_NAME, username)
        userStore.setValue(UserStore.KEY_PASSWORD, password)
        userStore.setValue(UserStore.KEY_ACCESS_TOKEN, accessToken)
        userStore.setValue(UserStore.KEY_REFRESH_TOKEN, refreshToken)
    }

    private fun askForPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }
    }
}

