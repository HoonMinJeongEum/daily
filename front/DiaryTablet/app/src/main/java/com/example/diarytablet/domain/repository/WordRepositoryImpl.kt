package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.domain.service.WordService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordService: WordService
) : WordRepository {
    override suspend fun getWordList(): MutableList<WordResponseDto> {
        return wordService.getWordList()
    }

    override suspend fun getLearnedWordList() : MutableList<WordLearnedResponseDto> {
        return wordService.getLearnedWordList()
    }

    override suspend fun checkWordValidate(wordRequest: WordRequestDto): Response<String> {
        return wordService.checkWordValidate(wordRequest)
    }

    override suspend fun finishWordLearning(words: List<WordRequestDto>): Response<String> {
        return wordService.finishWordLearning(words)
    }
}
