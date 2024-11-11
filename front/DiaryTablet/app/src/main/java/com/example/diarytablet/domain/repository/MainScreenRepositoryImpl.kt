package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CompleteMissionItemRequestDto
import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.response.CompleteMissionItemResponseDto
import com.example.diarytablet.domain.dto.response.MainScreenResponseDto
import com.example.diarytablet.domain.service.MainScreenService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainScreenRepositoryImpl @Inject constructor (
    private val mainScreenService : MainScreenService
) : MainScreenRepository {
    override suspend fun getMainScreenStatus(): MainScreenResponseDto{
        return mainScreenService.getMainScreenStatus()
    }
    override suspend fun updateUserName(userNameUpdateRequestDto : UserNameUpdateRequestDto) {
        return mainScreenService.updateUserName(userNameUpdateRequestDto)
    }

    override suspend fun completeMissionItem(completeMissionItemRequestDto : CompleteMissionItemRequestDto) : Response<CompleteMissionItemResponseDto> {
        return mainScreenService.completeMissionItem(completeMissionItemRequestDto)
    }

    override suspend fun updateProfileImage(file: MultipartBody.Part): Response<Void>{
        return mainScreenService.updateProfileImage(file)
    }



}