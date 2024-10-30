package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.response.ProfileListResponse
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.utils.Response
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
}
