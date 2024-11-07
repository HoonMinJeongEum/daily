package com.example.diaryApp.domain.repository.word

import com.example.diaryApp.domain.dto.response.word.Word
import retrofit2.Response

interface WordRepository {
    suspend fun getWordList(memberId : Int) : Response<List<Word>>
}