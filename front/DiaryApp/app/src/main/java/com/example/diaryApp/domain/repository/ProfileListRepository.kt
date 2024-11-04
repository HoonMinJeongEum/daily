package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.response.Profile
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody

interface ProfileListRepository {
    suspend fun getProfileList(): MutableList<Profile>
    suspend fun createProfile(name: RequestBody, file: MultipartBody.Part?)
}