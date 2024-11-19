package com.example.diaryApp.domain.repository.quiz


import com.example.diaryApp.domain.dto.request.quiz.CheckSessionRequestDto
import com.example.diaryApp.domain.dto.request.quiz.SessionRequestDto
import com.example.diaryApp.domain.dto.response.quiz.CheckSessionResponseDto
import com.example.diaryApp.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diaryApp.domain.dto.response.quiz.SessionResponseDto
import com.example.diaryApp.domain.dto.response.quiz.TokenResponseDto
import com.example.diaryApp.domain.services.QuizService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizService: QuizService
) : QuizRepository {

    override suspend fun initializeSession(params: SessionRequestDto): Response<SessionResponseDto> {
        return quizService.initializeSession(params)
    }

    override suspend fun createConnection(sessionId: String): Response<TokenResponseDto> {
        return quizService.createConnection(sessionId)
    }

    override suspend fun recommendWord(): Response<List<RecommendWordResponseDto>> {
        return quizService.recommendWord()
    }

    override suspend fun checkSession(checkSessionRequestDto: CheckSessionRequestDto): Response<CheckSessionResponseDto> {
        return quizService.checkSession(checkSessionRequestDto)
    }

}