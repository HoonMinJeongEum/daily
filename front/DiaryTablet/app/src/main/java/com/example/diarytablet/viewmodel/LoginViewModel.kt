package com.example.diarytablet.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.diarytablet.datastore.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userStore: UserStore
) : ViewModel() {
    private val _loginResult = mutableStateOf(false)
    val loginResult: State<Boolean> get() = _loginResult

    fun login(username: String, password: String) {
        _loginResult.value = userStore.login(username, password)
        if (_loginResult.value) {
            // 예: 사용자 정보를 가져오거나, 다음 화면으로 전환하는 로직을 추가
        } else {
            // 로그인 실패 시 처리할 로직 추가
        }
    }
}
