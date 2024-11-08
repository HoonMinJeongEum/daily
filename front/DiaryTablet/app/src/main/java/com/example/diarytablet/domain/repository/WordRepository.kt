package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.dto.response.WordStatusDto
import com.example.diarytablet.utils.Const
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WordRepository {
    suspend fun getWordList(): MutableList<WordResponseDto>
    suspend fun getLearnedWordList(): Response<List<WordLearnedResponseDto>>
    suspend fun checkWordValidate(
        writeFile: MultipartBody.Part,
        word: RequestBody
    ): Response<WordStatusDto>
    suspend fun finishWordLearning(
        ids: RequestBody,
        writeFile: List<MultipartBody.Part>
    ): Response<String>
}