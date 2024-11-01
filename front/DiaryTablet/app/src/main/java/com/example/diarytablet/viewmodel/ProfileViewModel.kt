package com.example.diarytablet.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileListRepository: ProfileListRepository
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


    fun selectProfile(profile: SelectProfileRequestDto) {
        viewModelScope.launch {
            profileListRepository.selectProfile(profile)
        }
    }

    fun addProfile(profile: CreateProfileRequestDto) {
        viewModelScope.launch {
            profileListRepository.createProfile(profile)
            loadProfiles() // 새로운 프로필 추가 후 리스트 갱신
        }
    }
}
