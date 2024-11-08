package com.example.diaryApp.domain.repository.quiz

import com.example.diaryApp.domain.dto.request.quiz.CheckSessionRequestDto
import com.example.diaryApp.domain.dto.request.quiz.SessionRequestDto
import com.example.diaryApp.domain.dto.response.quiz.CheckSessionResponseDto
import com.example.diaryApp.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diaryApp.domain.dto.response.quiz.SessionResponseDto
import com.example.diaryApp.domain.dto.response.quiz.TokenResponseDto
import retrofit2.Response

interface QuizRepository {
    suspend fun initializeSession(params: SessionRequestDto): Response<SessionResponseDto>
    suspend fun createConnection(sessionId: String): Response<TokenResponseDto>
    suspend fun recommendWord(): Response<List<RecommendWordResponseDto>>
    suspend fun checkSession(checkSessionRequestDto: CheckSessionRequestDto): Response<CheckSessionResponseDto>
}