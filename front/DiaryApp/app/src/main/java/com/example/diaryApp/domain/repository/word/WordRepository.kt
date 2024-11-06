package com.example.diaryApp.domain.repository.word

import com.example.diaryApp.domain.dto.response.word.Word

interface WordRepository {
    suspend fun getWordList(memberId : Int) : List<Word>
}