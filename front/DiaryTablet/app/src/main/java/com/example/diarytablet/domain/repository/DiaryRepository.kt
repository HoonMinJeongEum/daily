package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.response.diary.Diary
import com.example.diarytablet.domain.dto.response.diary.DiaryForList
import okhttp3.MultipartBody
import retrofit2.Response

interface DiaryRepository {
    suspend fun uploadDiary(drawUri: MultipartBody.Part, writeUri: MultipartBody.Part): Response<Void>
    suspend fun getDiaryList(year: Int, month: Int): Response<List<DiaryForList>>
    suspend fun getDiaryById(diaryId: Int): Diary

}