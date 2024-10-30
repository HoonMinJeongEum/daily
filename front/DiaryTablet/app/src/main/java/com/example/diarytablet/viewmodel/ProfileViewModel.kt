package com.example.diarytablet.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.dto.response.ProfileListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileListRepository: ProfileListRepository
): ViewModel() {
    val profileList = mutableStateOf(ProfileListResponse(emptyList()))

    init {
        // Mock 데이터 추가
        addMockData()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            // ProfileListRepository에서 프로필 리스트를 가져오는 로직
            profileListRepository.getProfileList().collect { response ->
                when (response) {
                    is Response.Success -> {
                        profileList.value = ProfileListResponse(response.data.profiles)
                    }
                    is Response.Failure -> {
                        // 에러 처리 로직 추가
                    }
                    Response.Loading -> {
                        // 로딩 상태 처리 (필요 시)
                    }
                }
            }
        }
    }
    private fun addMockData() {
        val mockProfiles = listOf(
            Profile(id = 1, name = "Alice", img = "https://example.com/alice.jpg"),
            Profile(id = 2, name = "Bob", img = "https://example.com/bob.jpg"),
            Profile(id = 3, name = "Charlie", img = "https://example.com/charlie.jpg")
        )
        profileList.value = ProfileListResponse(mockProfiles)
    }


    // 예시: 프로필을 추가하는 함수
    fun addProfile(profile: Profile) {
        profileList.value = ProfileListResponse(profileList.value.profiles + profile)
    }
}
