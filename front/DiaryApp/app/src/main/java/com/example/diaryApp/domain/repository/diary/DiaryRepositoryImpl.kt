package com.example.diaryApp.domain.repository.diary

import com.example.diaryApp.domain.dto.request.diary.DiaryCommentRequestDto
import com.example.diaryApp.domain.dto.response.diary.Diary
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import com.example.diaryApp.domain.services.DiaryService
import retrofit2.Response

class DiaryRepositoryImpl(
    private val diaryService: DiaryService
) : DiaryRepository {

    override suspend fun getDiaryList(memberId: Int, year: Int, month: Int): Response<List<DiaryForList>> {
        return diaryService.getDiaryList(memberId, year, month)
    }

    override suspend fun getDiaryById(diaryId: Int): Diary {
        return diaryService.getDiaryById(diaryId)
    }

    override suspend fun fetchComment(diaryCommentRequestDto: DiaryCommentRequestDto): Response<String> {
        return diaryService.fetchComment(diaryCommentRequestDto)
    }
}