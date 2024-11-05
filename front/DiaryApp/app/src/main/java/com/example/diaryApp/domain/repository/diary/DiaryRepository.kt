package com.example.diaryApp.domain.repository.diary

import com.example.diaryApp.domain.dto.response.diary.Diary
import com.example.diaryApp.domain.dto.response.diary.DiaryForList

interface DiaryRepository {
    suspend fun getDiaryList(memberId: Int, year: Int, month: Int): List<DiaryForList>
    suspend fun getDiaryById(diaryId: Int): Diary
}