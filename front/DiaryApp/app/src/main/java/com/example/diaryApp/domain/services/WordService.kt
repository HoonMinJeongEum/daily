package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.response.word.Word
import com.example.diaryApp.domain.dto.response.word.WordList
import com.example.diaryApp.utils.Const
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WordService {

    @GET("${Const.API_PATH}word/learned/{memberId}")
    suspend fun getWordList(
        @Path("memberId") memberId : Int
    ) : Response<List<Word>>

}