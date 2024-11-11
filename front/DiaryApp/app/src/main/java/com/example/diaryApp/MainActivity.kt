package com.example.diaryApp

import android.Manifest
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
import androidx.navigation.compose.rememberNavController
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.RetrofitClient
import com.example.diaryApp.domain.dto.request.user.LoginRequestDto
import com.example.diaryApp.domain.repository.user.UserRepository
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.viewmodel.ProfileViewModel
import com.example.diaryApp.viewmodel.WordViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val diaryViewModel: DiaryViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val wordViewModel : WordViewModel by viewModels()

    @Inject
    lateinit var userStore: UserStore
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()
        val startDestination = runBlocking {
            val isAutoLoginEnabled = userStore.getAutoLoginState().firstOrNull() ?: false
            val username = userStore.getValue(UserStore.KEY_USER_NAME).firstOrNull()
            val password = userStore.getValue(UserStore.KEY_PASSWORD).firstOrNull()
            Log.d("start","${username} , ${password} , ${isAutoLoginEnabled}")
            if (isAutoLoginEnabled && !username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                val success = performLogin(username, password)
                Log.d("start","${success}")
                if (success) "main" else "login"
            } else {
                "login"
            }
        }

        setContent {
            DiaryMobileApp(startDestination = startDestination, navController = rememberNavController())
        }
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

