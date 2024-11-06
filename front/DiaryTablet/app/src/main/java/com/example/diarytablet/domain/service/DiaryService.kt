package com.example.diarytablet.domain.service

import com.example.diarytablet.utils.Const
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DiaryService {
    @Multipart
    @POST("${Const.API_PATH}diaries")
    suspend fun uploadDiary(
        @Part drawFile: MultipartBody.Part,
        @Part writeFile: MultipartBody.Part
    ): Response<Void>
}
