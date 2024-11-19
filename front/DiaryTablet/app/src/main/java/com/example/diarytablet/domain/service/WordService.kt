package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.dto.response.WordStatusDto
import com.example.diarytablet.utils.Const
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response

interface WordService {
    @GET("${Const.API_PATH}word/session")
    suspend fun getWordList(): MutableList<WordResponseDto>

    @GET("${Const.API_PATH}word/learned")
    suspend fun getLearnedWordList(): Response<List<WordLearnedResponseDto>>

    @Multipart
    @POST("${Const.API_PATH}word/session/validate")
    suspend fun checkWordValidate(
        @Part writeFile: MultipartBody.Part,
        @Part("word") word: RequestBody
    ): Response<WordStatusDto>

    @Multipart
    @POST("${Const.API_PATH}word/session/complete")
    suspend fun finishWordLearning(
        @Part("ids") ids: RequestBody,
        @Part writeFile: List<MultipartBody.Part>
    ): Response<String>



}