package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.response.Profile
import com.example.diaryApp.domain.services.ProfileListService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileListRepositoryImpl @Inject constructor(
    private val profileService: ProfileListService
) : ProfileListRepository {
    override suspend fun getProfileList(): MutableList<Profile> {
        return profileService.getProfileList()
    }

    override suspend fun createProfile(name : RequestBody, file : MultipartBody.Part?) {
        val headers = hashMapOf(
            "Authorization" to "Bearer your_token_here", // 토큰 추가
            "Content-Type" to "multipart/form-data" // 필요 시 Content-Type 추가
        )
        return profileService.createProfile(name, file)
    }
}
