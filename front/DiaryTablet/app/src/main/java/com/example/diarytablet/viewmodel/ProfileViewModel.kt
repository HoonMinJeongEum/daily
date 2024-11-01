package com.example.diarytablet.viewmodel

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

    val profileList = mutableStateOf<List<Profile>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        loadProfiles() // 초기 화면 로딩 시 프로필 리스트 가져오기
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            profileListRepository.getProfileList().collect { response ->
                when (response) {
                    is Response.Success -> {
                        profileList.value = response.data!!
                        isLoading.value = false
                        errorMessage.value = null
                    }
                    is Response.Failure -> {
                        errorMessage.value = response.e?.message
                        isLoading.value = false
                    }
                    Response.Loading -> {
                        isLoading.value = true
                    }
                }
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
