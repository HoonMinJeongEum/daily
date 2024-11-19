package com.example.diarytablet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diarytablet.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.example.diarytablet.domain.repository.AlarmRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val userStore: UserStore
) : ViewModel() {

    private val _alarms = MutableLiveData<List<AlarmResponseDto>>()
    val alarms: LiveData<List<AlarmResponseDto>> get() = _alarms

    private val _saveTokenStatus = MutableLiveData<String>()
    val saveTokenStatus: LiveData<String> get() = _saveTokenStatus

    private val _checkAlarmStatus = MutableLiveData<String>()
    val checkAlarmStatus: LiveData<String> get() = _checkAlarmStatus

    // FCM 토큰을 가져와 저장하는 함수
    private fun saveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("LoginViewModel", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("LoginViewModel", "FCM 토큰: $token")

            viewModelScope.launch {
                val response = alarmRepository.saveToken(SaveTokenRequestDto(token))
                if (response.isSuccessful) {
                    Log.d("LoginViewModel", "알림 토큰이 정상적으로 등록되었습니다.")
                } else {
                    Log.e("LoginViewModel", "토큰 저장 실패: ${response.message()}")
                }
            }
        }
    }

    // 알림 목록 조회
    fun getAlarms() {
        viewModelScope.launch {
            val response = alarmRepository.getAlarms()
            if (response.isSuccessful) {
                _alarms.value = response.body()?.list
            } else {
                _alarms.value = emptyList()
            }
        }
    }

    // 알림 확인
    fun checkAlarm(alarmId: Long) {
        viewModelScope.launch {
            val response = alarmRepository.checkAlarm(CheckAlarmRequestDto(alarmId))
            if (response.isSuccessful) {
                _checkAlarmStatus.value = response.body()?.msg
            } else {
                _checkAlarmStatus.value = "알림 확인 실패: ${response.message()}"
            }
        }
    }
}
