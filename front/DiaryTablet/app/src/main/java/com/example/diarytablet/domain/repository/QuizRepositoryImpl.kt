package com.example.diarytablet.domain.repository


import com.example.diarytablet.domain.dto.request.quiz.CheckWordRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SessionRequestDto
import com.example.diarytablet.domain.dto.request.quiz.SetWordRequestDto
import com.example.diarytablet.domain.dto.response.quiz.RecommendWordResponseDto
import com.example.diarytablet.domain.dto.response.quiz.SessionResponseDto
import com.example.diarytablet.domain.dto.response.quiz.TokenResponseDto
import com.example.diarytablet.domain.service.QuizService
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

    override suspend fun setWord(request: SetWordRequestDto): Response<String> {
        return quizService.setWord(request)
    }

    override suspend fun checkWord(request: CheckWordRequestDto): Response<Boolean> {
        return quizService.checkWord(request)
    }
}