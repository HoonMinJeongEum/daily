package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.dto.response.ProfileListResponse
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileListRepositoryImpl @Inject constructor(
    private val profileListService: ProfileListService
) : ProfileListRepository {
    override suspend fun getProfileList(): Flow<Response<List<Profile>>> {
        return flow {
            emit(Response.Loading) // Optional: 로딩 상태
            try {
                val response = profileListService.getProfileList()
                emit(Response.Success(response.profiles))
            } catch (e: Exception) {
                emit(Response.Failure(e))
            }
        }
    }

    override suspend fun selectProfile(selectProfileRequestDto: SelectProfileRequestDto) {
        return profileListService.selectProfile(selectProfileRequestDto)
    }

    override suspend fun createProfile(createProfileRequestDto: CreateProfileRequestDto) {
        return profileListService.createProfile(createProfileRequestDto)
    }
}
