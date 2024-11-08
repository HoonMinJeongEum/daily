package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CompleteMissionItemRequestDto
import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.response.CompleteMissionItemResponseDto
import com.example.diarytablet.domain.dto.response.MainScreenResponseDto
import retrofit2.Response
import retrofit2.http.Body

interface MainScreenRepository {
    suspend fun getMainScreenStatus():MainScreenResponseDto
    suspend fun updateUserName(userNameUpdateRequestDto : UserNameUpdateRequestDto)
    suspend fun completeMissionItem(completeMissionItemRequestDto : CompleteMissionItemRequestDto) : Response<CompleteMissionItemResponseDto>
}