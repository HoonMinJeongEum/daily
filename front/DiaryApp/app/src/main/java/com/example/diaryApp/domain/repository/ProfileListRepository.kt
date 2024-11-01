package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.response.ProfileListResponse
import com.example.diaryApp.utils.Response
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import kotlinx.coroutines.flow.Flow

interface ProfileListRepository {
    suspend fun getProfileList(): Flow<Response<ProfileListResponse>>
    suspend fun selectProfile(selectProfileRequestDto: SelectProfileRequestDto)
    suspend fun createProfile(createProfileRequestDto: CreateProfileRequestDto)

}