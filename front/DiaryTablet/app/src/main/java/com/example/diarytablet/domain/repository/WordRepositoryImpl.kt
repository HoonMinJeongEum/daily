package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.service.WordService
import okhttp3.MultipartBody
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

    override suspend fun checkWordValidate(orgFile: MultipartBody.Part, writeFile: MultipartBody.Part): Response<String> {
        return wordService.checkWordValidate(orgFile,writeFile)
    }

    override suspend fun finishWordLearning(words: List<WordRequestDto>): Response<String> {
        return wordService.finishWordLearning(words)
    }
}
