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
        // 여기서 로그인 실패에 대한 추가 로직을 구현할 수 있습니다.
    }
}
