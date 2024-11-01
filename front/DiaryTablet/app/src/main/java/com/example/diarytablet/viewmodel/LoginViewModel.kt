package com.example.diarytablet.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository
) : ViewModel() {
    val username = mutableStateOf("")
    val password = mutableStateOf("")

    private val userStore: UserStore = UserStore(application)

    fun login(
        onSuccess: () -> Unit,
        onErrorPassword: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            val loginRequestDto = LoginRequestDto(
                username = username.value,
                password = password.value
            )

            try {
                val response: Response<Void> = userRepository.login(loginRequestDto)

                Log.d("LoginViewModel", "Response: $response") // Response 로그

                if (response.isSuccessful) {
                    // 헤더에서 토큰 가져오기
                    val headers = response.headers()
                    val accessToken = headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                    val refreshToken = headers["Set-Cookie"]

                    if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                        Log.d("LoginViewModel", "Access Token: $accessToken")
                        Log.d("LoginViewModel", "Refresh Token: $refreshToken")
                        saveUserInfo(accessToken, refreshToken)

                        onSuccess() // 로그인 성공 처리
                    } else {
                        Log.d("LoginViewModel", "Token not found in headers")
                        onError() // 토큰이 없으면 에러 처리
                    }
                } else {
                    handleErrorResponse(response.code(), onErrorPassword, onError)
                }
            } catch (e: Exception) {
                handleException(e, onErrorPassword, onError)
            }
        }
    }

    private suspend fun saveUserInfo(accessToken: String, refreshToken: String) {
        RetrofitClient.login(accessToken,refreshToken)
        userStore.setValue(UserStore.KEY_PASSWORD, password.value)
            .setValue(UserStore.KEY_REFRESH_TOKEN, refreshToken)
            .setValue(UserStore.KEY_USER_NAME, username.value)
            .setValue(UserStore.KEY_ACCESS_TOKEN, accessToken)

        Log.d("LoginViewModel", "User info saved: Username: ${username.value}, AccessToken: $accessToken")
    }

    private fun handleErrorResponse(code: Int, onErrorPassword: () -> Unit, onError: () -> Unit) {
        when (code) {
            401 -> {
                Log.d("LoginViewModel", "Unauthorized") // 비밀번호 오류
                onErrorPassword()
            }
            else -> {
                Log.d("LoginViewModel", "Error: $code") // 다른 오류
                onError()
            }
        }
    }

    private fun handleException(e: Exception, onErrorPassword: () -> Unit, onError: () -> Unit) {
        when (e) {
            is HttpException -> {
                if (e.code() == 401) {
                    onErrorPassword() // 비밀번호 오류 처리
                } else {
                    Log.e("LoginViewModel", "HTTP error: ${e.message}")
                    onError() // 일반 오류 처리
                }
            }
            else -> {
                Log.e("LoginViewModel", "Network error: ${e.message}")
                onError() // 네트워크 오류 처리
            }
        }
    }
}
