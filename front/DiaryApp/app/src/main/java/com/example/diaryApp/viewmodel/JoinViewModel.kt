package com.example.diaryApp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.dto.request.user.JoinRequestDto
import com.example.diaryApp.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository
) : ViewModel() {
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val passwordCheck = mutableStateOf("")
    val isUsernameAvailable = mutableStateOf<Boolean?>(null) // 중복 체크 결과 상태
    val usernameErrorMessage = mutableStateOf("")

    private val userStore: UserStore = UserStore(application)

    fun join(
        onSuccess: () -> Unit,
        onErrorPassword: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch{
            if (password.value == passwordCheck.value) {
                val joinRequestDto = JoinRequestDto(
                    username = username.value,
                    password = password.value
                )

                try {
                    val response: Response<Void> = userRepository.join(joinRequestDto)
                    Log.d("JoinViewModel", "Response: $response")
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError()
                    }
                } catch (e:Exception) {
                    handleException(e, onErrorPassword, onError)
                }
            }
        }
    }
    fun checkUsernameAvailability() {
        viewModelScope.launch {
            try {
                val response = userRepository.checkUsernameAvailability(username.value)
                if (response.isSuccessful) {
                    isUsernameAvailable.value = true
                    usernameErrorMessage.value = "사용 가능한 아이디입니다."

                } else {
                    isUsernameAvailable.value = false
                    usernameErrorMessage.value = "아이디가 중복됩니다."
                }
            } catch (e: Exception) {
                Log.e("JoinViewModel", "Username check error: ${e.message}")
                isUsernameAvailable.value = false
                usernameErrorMessage.value = "서버와 통신 중 오류가 발생했습니다."
            }
        }
    }


    private fun handleException(e: Exception, onErrorPassword: () -> Unit, onError: () -> Unit) {
        when (e) {
            is HttpException -> {
                if (e.code() == 401) {
                    onErrorPassword() // 비밀번호 오류 처리
                } else {
                    Log.e("JoinViewModel", "HTTP error: ${e.message}")
                    onError() // 일반 오류 처리
                }
            }
            else -> {
                Log.e("JoinViewModel", "Network error: ${e.message}")
                onError() // 네트워크 오류 처리
            }
        }
    }
}