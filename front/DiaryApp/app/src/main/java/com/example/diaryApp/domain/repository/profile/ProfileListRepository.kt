package com.example.diaryApp.domain.repository.profile

import com.example.diaryApp.domain.dto.response.profile.Profile
import com.example.diaryApp.domain.dto.response.profile.ProfileCreateDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ProfileListRepository {
    suspend fun getProfileList(): MutableList<Profile>
    suspend fun createProfile(name: RequestBody, file: MultipartBody.Part?): Response<Void>
    suspend fun deleteProfile(memberId : Int)
}