package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.request.quiz.CheckSessionRequestDto
import com.example.diaryApp.domain.dto.request.quiz.SessionRequestDto
import com.example.diaryApp.domain.dto.response.quiz.CheckSessionResponseDto
import com.example.diaryApp.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diaryApp.domain.dto.response.quiz.SessionResponseDto
import com.example.diaryApp.domain.dto.response.quiz.TokenResponseDto
import com.example.diaryApp.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
    @POST("${Const.API_PATH}quiz/sessions/check")
    suspend fun checkSession(@Body params: CheckSessionRequestDto): Response<CheckSessionResponseDto>
}