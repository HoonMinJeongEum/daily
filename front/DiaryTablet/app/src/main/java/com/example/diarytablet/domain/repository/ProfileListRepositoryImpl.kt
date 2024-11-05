package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.service.ProfileListService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileListRepositoryImpl @Inject constructor(
    private val profileListService: ProfileListService
) : ProfileListRepository {
    override suspend fun getProfileList(): MutableList<Profile> {
        return profileListService.getProfileList()
    }

    override suspend fun selectProfile(selectProfileRequestDto: SelectProfileRequestDto): Response<Void> {
        return profileListService.selectProfile(selectProfileRequestDto)
    }

    override suspend fun createProfile(createProfileRequestDto: CreateProfileRequestDto) {
        return profileListService.createProfile(createProfileRequestDto)
    }
}
