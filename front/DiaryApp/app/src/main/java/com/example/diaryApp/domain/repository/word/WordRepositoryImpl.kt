package com.example.diaryApp.domain.repository.word

import com.example.diaryApp.domain.dto.response.word.Word
import com.example.diaryApp.domain.services.WordService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordService: WordService
) : WordRepository {

    override suspend fun getWordList(memberId: Int): Response<List<Word>> {
        return wordService.getWordList(memberId)
    }

}