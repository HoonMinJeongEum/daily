package com.example.diaryApp.domain.services

import com.example.diaryApp.utils.Const
import retrofit2.http.GET

interface WordService {

    @GET("${Const.API_PATH}word/learned")
    suspend fun getWordList()
}