package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import retrofit2.Response
import kotlinx.coroutines.flow.Flow

interface ProfileListRepository {
    suspend fun getProfileList(): MutableList<Profile>
    suspend fun selectProfile(selectProfileRequestDto: SelectProfileRequestDto):Response<Void>
    suspend fun createProfile(createProfileRequestDto: CreateProfileRequestDto)
}