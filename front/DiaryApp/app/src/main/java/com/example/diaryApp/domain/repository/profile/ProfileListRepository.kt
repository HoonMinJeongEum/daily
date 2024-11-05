package com.example.diaryApp.domain.repository.profile

import com.example.diaryApp.domain.dto.response.profile.Profile
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileListRepository {
    suspend fun getProfileList(): MutableList<Profile>
    suspend fun createProfile(name: RequestBody, file: MultipartBody.Part?)
    suspend fun deleteProfile(memberId : Int)
}