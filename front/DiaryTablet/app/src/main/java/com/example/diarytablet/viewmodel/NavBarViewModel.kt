package com.example.diarytablet.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.datastore.UserStore.Companion.KEY_PROFILE_IMAGE
import com.example.diarytablet.datastore.UserStore.Companion.KEY_USER_NAME
import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.repository.MainScreenRepository
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.ui.components.MissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavBarViewModel @Inject constructor(
    private val userStore: UserStore,
    private val mainScreenRepository: MainScreenRepository
) : ViewModel(){
    init {
        loadStatus()
    }

    private val _shellCount = mutableIntStateOf(0)
    val shellCount: State<Int> get() = _shellCount
    private val _profileImageUrl = mutableStateOf("")
    val profileImageUrl: State<String> get() = _profileImageUrl
    private  val _isAlarmOn = mutableStateOf(false)
    val isAlarmOn: State<Boolean> get() = _isAlarmOn

    private val _userName = mutableStateOf("")
    val userName: State<String> get() = _userName

    // 미션을 로드하는 함수
    private fun loadStatus() {
        viewModelScope.launch {
            try {
                val response = mainScreenRepository.getMainScreenStatus()

                // shellCount 업데이트
                _shellCount.value = response.shellCount

                _profileImageUrl.value = response.image

                userStore.getValue(UserStore.KEY_PROFILE_IMAGE).collect { url ->
                    _profileImageUrl.value = url
                }
                userStore.getValue(UserStore.KEY_USER_NAME).collect { name ->
                    _userName.value = name
                }
                userStore.setValue(KEY_PROFILE_IMAGE, response.image)            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            try {
                val requestDto = UserNameUpdateRequestDto(newName)
                val response = mainScreenRepository.updateUserName(requestDto)

                // Update the local state and save the new name to UserStore
                _userName.value = newName
                userStore.setValue(KEY_USER_NAME, newName)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}



