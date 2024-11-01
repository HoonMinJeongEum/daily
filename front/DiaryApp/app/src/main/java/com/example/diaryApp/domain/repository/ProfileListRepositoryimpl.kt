package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.response.ProfileListResponse
import com.example.diaryApp.domain.services.ProfileListService
import com.example.diaryApp.utils.Response
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileListRepositoryImpl @Inject constructor(
    private val profileService: ProfileListService
) : ProfileListRepository {
    override suspend fun getProfileList(): Flow<Response<ProfileListResponse>> {
        return flow {
            emit(Response.Loading) // Optional: 로딩 상태
            try {
                val response = profileService.getProfileList()
                emit(Response.Success(response))
            } catch (e: Exception) {
                emit(Response.Failure(e))
            }
        }
    }

    override suspend fun selectProfile(selectProfileRequestDto: SelectProfileRequestDto) {
        return profileService.selectProfile(selectProfileRequestDto)
    }

    override suspend fun createProfile(createProfileRequestDto: CreateProfileRequestDto) {
        return profileService.createProfile(createProfileRequestDto)
    }
}
