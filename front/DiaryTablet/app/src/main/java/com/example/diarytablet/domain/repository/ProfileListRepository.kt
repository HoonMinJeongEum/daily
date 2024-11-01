package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.flow.Flow

interface ProfileListRepository {
    suspend fun getProfileList(): MutableList<Profile>
    suspend fun selectProfile(selectProfileRequestDto: SelectProfileRequestDto)
    suspend fun createProfile(createProfileRequestDto: CreateProfileRequestDto)
}