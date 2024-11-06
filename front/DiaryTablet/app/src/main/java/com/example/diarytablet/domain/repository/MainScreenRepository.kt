package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.response.MainScreenResponseDto

interface MainScreenRepository {
    suspend fun getMainScreenStatus():MainScreenResponseDto
    suspend fun updateUserName(userNameUpdateRequestDto : UserNameUpdateRequestDto)
}