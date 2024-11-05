package com.example.diaryApp.domain.repository.diary

import com.example.diaryApp.domain.dto.response.diary.Diary
import com.example.diaryApp.domain.dto.response.diary.DiaryForList
import com.example.diaryApp.domain.services.DiaryService

class DiaryRepositoryImpl(
    private val diaryService: DiaryService
) : DiaryRepository {

    override suspend fun getDiaryList(memberId: Int, year: Int, month: Int): List<DiaryForList> {
        return diaryService.getDiaryList(memberId, year, month)
    }

    override suspend fun getDiaryById(diaryId: Int): Diary {
        return diaryService.getDiaryById(diaryId)
    }
}