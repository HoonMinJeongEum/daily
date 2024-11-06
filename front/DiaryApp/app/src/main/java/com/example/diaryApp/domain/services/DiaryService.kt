package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.response.diary.Diary
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import com.example.diaryApp.utils.Const
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryService {

    @GET("${Const.API_PATH}diaries")
    suspend fun getDiaryList(
        @Query("memberId") memberId: Int,
        @Query("year") year: Int,
        @Query("month") month: Int
    ) : List<DiaryForList>

    @GET("${Const.API_PATH}diaries/{diaryId}")
    suspend fun getDiaryById(
        @Path("diaryId") diaryId:Int
    ) : Diary
}