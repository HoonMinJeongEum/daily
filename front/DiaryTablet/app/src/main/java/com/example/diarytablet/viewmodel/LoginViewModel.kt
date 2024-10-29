package com.example.diarytablet.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.dto.response.LoginResponseDto
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.utils.Response
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
            userStore.getValue(UserStore.KEY_REFRESH_TOKEN)
                .collect { token ->
                    val loginRequestDto = LoginRequestDto(
                        username = username.value,
                        password = password.value
                    )
                    userRepository.login(loginRequestDto)
                        .collect { response: Response<LoginResponseDto> ->  // 명시적 타입 추가
                            when (response) {
                                is Response.Failure -> {
                                    if (response.e is HttpException) {
                                        when (response.e.code()) {
                                            401 -> onErrorPassword()
                                            else -> onError() // 다른 오류 처리
                                        }
                                    } else {
                                        onError() // 네트워크 오류 처리
                                    }
                                }
                                is Response.Success -> {
                                    saveUserInfo(response.data)
                                    onSuccess()
                                }
                                Response.Loading -> {
                                    // Optional: 로딩 상태 처리
                                }
                            }
                        }
                }
        }
    }

    private suspend fun saveUserInfo(
        loginResponseDto: LoginResponseDto
    ) {
        with(loginResponseDto) {
            // RetrofitClient.login()을 호출하여 액세스 토큰과 리프레시 토큰을 설정합니다.
            RetrofitClient.login(accessToken, refreshToken)

            // UserStore에 비밀번호, 리프레시 토큰, 사용자 이름, 액세스 토큰을 저장합니다.
            userStore.setValue(UserStore.KEY_PASSWORD, password.value)
                .setValue(UserStore.KEY_REFRESH_TOKEN, refreshToken)
                .setValue(UserStore.KEY_USER_NAME, username.value)
                .setValue(UserStore.KEY_ACCESS_TOKEN, accessToken)
        }
    }
}
