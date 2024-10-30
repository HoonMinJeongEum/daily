package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.response.ProfileListResponse
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.flow.Flow

interface ProfileListRepository {
    suspend fun getProfileList(): Flow<Response<ProfileListResponse>>
}