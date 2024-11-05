package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.quiz.CheckWordRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SessionRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SetWordRequestDto
import com.example.diarytablet.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diarytablet.domain.dto.response.quiz.SessionResponseDto
import com.example.diarytablet.domain.dto.response.quiz.TokenResponseDto
import retrofit2.Response

interface QuizRepository {
    suspend fun initializeSession(params: SessionRequestDto): Response<SessionResponseDto>
    suspend fun createConnection(sessionId: String): Response<TokenResponseDto>
    suspend fun recommendWord(): Response<List<RecommendWordResponseDto>>
    suspend fun setWord(request: SetWordRequestDto): Response<String>
    suspend fun checkWord(request: CheckWordRequestDto): Response<Boolean>
}