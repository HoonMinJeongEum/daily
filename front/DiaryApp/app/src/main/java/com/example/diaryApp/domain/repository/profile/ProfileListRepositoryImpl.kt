package com.example.diaryApp.domain.repository.profile

import com.example.diaryApp.domain.dto.response.profile.Profile
import com.example.diaryApp.domain.dto.response.profile.ProfileCreateDto
import com.example.diaryApp.domain.services.ProfileListService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileListRepositoryImpl @Inject constructor(
    private val profileService: ProfileListService
) : ProfileListRepository {
    override suspend fun getProfileList(): MutableList<Profile> {
        return profileService.getProfileList()
    }

    override suspend fun createProfile(name : RequestBody, file : MultipartBody.Part?) : Response<Void> {
        return profileService.createProfile(name, file)
    }

    override suspend fun deleteProfile(memberId : Int) {
        return profileService.deleteProfile(memberId)
    }
}
