package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.quiz.CheckWordRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SessionRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SetWordRequestDto
import com.example.diarytablet.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diarytablet.domain.dto.response.quiz.SessionResponseDto
import com.example.diarytablet.domain.dto.response.quiz.TokenResponseDto
import com.example.diarytablet.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizService {
    @POST("${Const.API_PATH}quiz/sessions")
    suspend fun initializeSession(@Body params: SessionRequestDto): Response<SessionResponseDto>
    @POST("${Const.API_PATH}quiz/sessions/{sessionId}/connections")
    suspend fun createConnection(
        @Path("sessionId") sessionId: String
    ): Response<TokenResponseDto>
    @GET("${Const.API_PATH}quiz/word/recommend")
    suspend fun recommendWord(): Response<List<RecommendWordResponseDto>>
    @PATCH("${Const.API_PATH}quiz/word/set")
    suspend fun setWord(@Body request: SetWordRequestDto): Response<String>
    @POST("${Const.API_PATH}quiz/word/check")
    suspend fun checkWord(@Body request: CheckWordRequestDto): Response<Boolean>
}