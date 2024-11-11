package com.example.diaryApp.domain.repository.diary

import com.example.diaryApp.domain.dto.request.diary.DiaryCommentRequestDto
import com.example.diaryApp.domain.dto.response.diary.Diary
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import retrofit2.Response

interface DiaryRepository {
    suspend fun getDiaryList(memberId: Int, year: Int, month: Int): Response<List<DiaryForList>>
    suspend fun getDiaryById(diaryId: Int): Diary
    suspend fun fetchComment(diaryCommentRequestDto: DiaryCommentRequestDto) : Response<String>
}