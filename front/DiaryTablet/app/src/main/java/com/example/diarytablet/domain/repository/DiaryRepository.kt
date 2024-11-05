package com.example.diarytablet.domain.repository

import okhttp3.MultipartBody
import retrofit2.Response

interface DiaryRepository {
    suspend fun uploadDiary(drawUri: MultipartBody.Part, writeUri: MultipartBody.Part): Response<Void>
}