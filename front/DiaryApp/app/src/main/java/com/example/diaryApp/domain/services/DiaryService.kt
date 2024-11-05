package com.example.diaryApp.domain.services

import com.example.diaryApp.utils.Const
import retrofit2.http.GET

interface DiaryService {

    @GET("${Const.API_PATH}diaries")
    suspend fun getDiaryList()
}