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
    private val userStore: UserStore,
    private val userRepository: UserRepository
) : ViewModel() {
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val autoLogin = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)  // 에러 메시지 상태 추가

    fun login(
        onSuccess: () -> Unit,
        onErrorPassword: (String) -> Unit,  // 오류 메시지를 전달받을 수 있도록 변경
        onError: (String) -> Unit
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
                        val errorMsg = "토큰이 헤더에서 찾을 수 없습니다."
                        Log.d("LoginViewModel", errorMsg)
                        onError(errorMsg) // 토큰이 없으면 에러 처리
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
        userStore.setAutoLoginState(autoLogin.value)

        Log.d("LoginViewModel", "User info saved:${autoLogin.value} ${username.value}")
    }

    private fun handleErrorResponse(code: Int, onErrorPassword: (String) -> Unit, onError: (String) -> Unit) {
        when (code) {
            401 -> {
                val msg = "비밀번호가 올바르지 않습니다."
                Log.d("LoginViewModel", msg)
                onErrorPassword(msg) // 비밀번호 오류
            }
            else -> {
                val msg = "알 수 없는 오류가 발생했습니다. 코드: $code"
                Log.d("LoginViewModel", msg)
                onError(msg) // 다른 오류
            }
        }
    }

    private fun handleException(e: Exception, onErrorPassword: (String) -> Unit, onError: (String) -> Unit) {
        when (e) {
            is HttpException -> {
                val msg = if (e.code() == 401) {
                    "비밀번호 오류"
                } else {
                    "HTTP 오류: ${e.message}"
                }
                Log.e("LoginViewModel", msg)
                onError(msg) // HTTP 예외 처리
            }
            else -> {
                val msg = "네트워크 오류: ${e.message}"
                Log.e("LoginViewModel", msg)
                onError(msg) // 네트워크 오류 처리
            }
        }
    }
}
