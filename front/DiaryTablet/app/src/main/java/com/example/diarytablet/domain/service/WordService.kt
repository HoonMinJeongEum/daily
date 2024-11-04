package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.utils.Const
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response

interface WordService {
    @GET("${Const.API_PATH}word/session")
    suspend fun getWordList(): MutableList<WordResponseDto>

    @GET("${Const.API_PATH}word/learnd")
    suspend fun getLearnedWordList(): MutableList<WordLearnedResponseDto>

    @Multipart
    @POST("${Const.API_PATH}word/validate")
    suspend fun checkWordValidate(
        @Part wordRequest: WordRequestDto
    ): Response<String>

    @Multipart
    @POST("${Const.API_PATH}word/session/complete")
    suspend fun finishWordLearning(
        @Part words: List<WordRequestDto>
    ): Response<String>



}