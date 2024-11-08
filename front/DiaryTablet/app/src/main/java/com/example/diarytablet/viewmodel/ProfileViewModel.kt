package com.example.diarytablet.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileListRepository: ProfileListRepository,
    private val alarmRepository: AlarmRepository,
    application: Application,
    private val userStore: UserStore
) : ViewModel() {

    val _profileList = mutableStateOf<List<Profile>>(emptyList())
    val profileList: State<List<Profile>> get() = _profileList
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        loadProfiles() // 초기 화면 로딩 시 프로필 리스트 가져오기
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val profiles = profileListRepository.getProfileList()
                _profileList.value = profiles
                Log.d("ProfileViewModel", "Profile list updated: ${_profileList.value}")
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("ProfileViewModel", "Error loading profiles: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }


    fun selectProfile(profile: SelectProfileRequestDto, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response: retrofit2.Response<Void> = profileListRepository.selectProfile(profile)

                if (response.isSuccessful) {
                    val headers = response.headers()
                    val accessToken = headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                    val refreshToken = headers["Set-Cookie"]
                    val selectedProfile = _profileList.value.find { it.id == profile.memberId }
                    val profileName = selectedProfile?.name

                    if (!profileName.isNullOrEmpty() && !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                        RetrofitClient.login(accessToken, refreshToken)
                        userStore
                            .setValue(UserStore.KEY_REFRESH_TOKEN, refreshToken)
                            .setValue(UserStore.KEY_ACCESS_TOKEN, accessToken)
                            .setValue(UserStore.KEY_PROFILE_NAME, profileName)
                        Log.d("ProfileList", "Tokens stored successfully")
                        saveFcmToken()
                        onComplete(true) // 성공 시 콜백 호출

                    }
                } else {
                    Log.d("ProfilePage", "ProfileSelect Fail")
                    onComplete(false) // 실패 시 콜백 호출
                }
            } catch (e: Exception) {
                Log.e("ProfilePage", "Profile selection error", e)
                onComplete(false) // 실패 시 콜백 호출
            }
        }
    }




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
}
