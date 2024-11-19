package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CompleteMissionItemRequestDto
import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.response.CompleteMissionItemResponseDto
import com.example.diarytablet.domain.dto.response.MainScreenResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Part

interface MainScreenRepository {
    suspend fun getMainScreenStatus():MainScreenResponseDto
    suspend fun updateUserName(userNameUpdateRequestDto : UserNameUpdateRequestDto)
    suspend fun completeMissionItem(completeMissionItemRequestDto : CompleteMissionItemRequestDto) : Response<CompleteMissionItemResponseDto>
    suspend fun updateProfileImage(file: MultipartBody.Part): Response<Void>
}