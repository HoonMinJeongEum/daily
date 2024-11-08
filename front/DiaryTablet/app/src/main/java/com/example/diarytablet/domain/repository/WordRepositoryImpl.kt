package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.WordRequestDto
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.domain.dto.response.WordResponseDto
import com.example.diarytablet.domain.dto.response.WordStatusDto
import com.example.diarytablet.domain.service.WordService
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    override suspend fun getLearnedWordList() : Response<List<WordLearnedResponseDto>> {
        return wordService.getLearnedWordList()
    }

    override suspend fun checkWordValidate(
        writeFile: MultipartBody.Part,
        word: RequestBody
    ): Response<WordStatusDto> {
        return wordService.checkWordValidate(writeFile,word)
    }

    override suspend fun finishWordLearning(ids: RequestBody, writeFile: List<MultipartBody.Part>): Response<String> {
        return wordService.finishWordLearning(ids,writeFile)
    }
}
