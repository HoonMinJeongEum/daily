package com.example.diarytablet.viewmodel

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.datastore.UserStore.Companion.KEY_PROFILE_IMAGE
import com.example.diarytablet.datastore.UserStore.Companion.KEY_PROFILE_NAME
import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diarytablet.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.example.diarytablet.domain.repository.AlarmRepository
import com.example.diarytablet.domain.repository.MainScreenRepository
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.ui.components.MissionItem
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class NavBarViewModel @Inject constructor(
    private val userStore: UserStore,
    private val mainScreenRepository: MainScreenRepository,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    fun initializeData() {
        loadStatus()
//        saveFcmToken()
    }

    init {
        initializeData()
    }

    private val _shellCount = mutableIntStateOf(0)
    val shellCount: State<Int> get() = _shellCount

    private val _profileImageUrl = mutableStateOf("")
    val profileImageUrl: State<String> get() = _profileImageUrl

    private val _isAlarmOn = mutableStateOf(false)
    val isAlarmOn: State<Boolean> get() = _isAlarmOn

    private val _userName = mutableStateOf("")
    val userName: State<String> get() = _userName

    private val _alarms = mutableStateOf<List<AlarmResponseDto>>(emptyList())
    val alarms: State<List<AlarmResponseDto>> get() = _alarms

    private val _saveTokenStatus = mutableStateOf("")
    val saveTokenStatus: State<String> get() = _saveTokenStatus

    private val _checkAlarmStatus = mutableStateOf("")
    val checkAlarmStatus: State<String> get() = _checkAlarmStatus

    fun loadStatus() {
        viewModelScope.launch {
            try {
                val response = mainScreenRepository.getMainScreenStatus()

                // shellCount 업데이트
                _shellCount.value = response.shellCount

                userStore.getValue(UserStore.KEY_PROFILE_IMAGE).collect { url ->
                    _profileImageUrl.value = url
                }
                _profileImageUrl.value = response.image
                userStore.getValue(UserStore.KEY_PROFILE_NAME).collect { name ->
                    _userName.value = name
                }
                userStore.setValue(KEY_PROFILE_IMAGE, response.image)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("NavBarViewModel", "FCM 토큰 가져오기 실패", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("NavBarViewModel", "FCM 토큰: $token")

            viewModelScope.launch {
                val response = alarmRepository.saveToken(SaveTokenRequestDto(token))
                if (response.isSuccessful) {
                    _saveTokenStatus.value = "토큰이 정상적으로 저장되었습니다."
                    Log.d("NavBarViewModel", "알림 토큰 저장 성공")
                } else {
                    _saveTokenStatus.value = "토큰 저장 실패: ${response.message()}"
                    Log.e("NavBarViewModel", "토큰 저장 실패")
                }
            }
        }
    }

    fun getAlarms(onAlarmsFetched: () -> Unit) {
        viewModelScope.launch {
            val response = alarmRepository.getAlarms()
            if (response.isSuccessful) {
                _alarms.value = response.body()?.list ?: emptyList()
                onAlarmsFetched() // Trigger modal visibility after fetching
            } else {
                _alarms.value = emptyList()
            }
        }
    }


    fun checkAlarm(alarmId: Long) {
        viewModelScope.launch {
            val response = alarmRepository.checkAlarm(CheckAlarmRequestDto(alarmId))
            if (response.isSuccessful) {
                _checkAlarmStatus.value = "알림이 정상적으로 확인되었습니다."
                Log.d("NavBarViewModel", "알림 확인 성공")
            } else {
                _checkAlarmStatus.value = "알림 확인 실패: ${response.message()}"
                Log.e("NavBarViewModel", "알림 확인 실패")
            }
        }
    }


    private fun observeAlarmState() {
        viewModelScope.launch {
            userStore.getAlarmState().collect { isOn ->
                _isAlarmOn.value = isOn
            }
        }
    }

    fun setAlarmState(isOn: Boolean) {
        viewModelScope.launch {
            userStore.setAlarmState(isOn)
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            try {
                val requestDto = UserNameUpdateRequestDto(newName)
                val response = mainScreenRepository.updateUserName(requestDto)

                // Update the local state and save the new name to UserStore
                _userName.value = newName
                userStore.setValue(KEY_PROFILE_NAME, newName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfileImage(imageFilePath: String) {
        viewModelScope.launch {
            try {
                Log.d("NavBarViewModel", "updateProfileImage() - 호출됨, 파일 경로: $imageFilePath")

                val file = File(imageFilePath)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = mainScreenRepository.updateProfileImage(body)
                if (response.isSuccessful) {
                    Log.d("NavBarViewModel", "updateProfileImage() - 이미지 업로드 성공")

                    // 이미지 업로드 후 loadStatus() 호출
                    loadStatus()
                    Log.d("NavBarViewModel", "updateProfileImage() - loadStatus() 호출 완료 후 profileImageUrl: ${_profileImageUrl.value}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("NavBarViewModel", "updateProfileImage() - 프로필 이미지 업데이트 실패: $errorBody")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NavBarViewModel", "updateProfileImage() - 프로필 이미지 업데이트 중 오류 발생")
            }
        }
    }
}




