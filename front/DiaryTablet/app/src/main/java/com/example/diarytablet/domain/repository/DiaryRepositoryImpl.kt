package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.response.diary.Diary
import com.example.diarytablet.domain.dto.response.diary.DiaryForList
import com.example.diarytablet.domain.service.DiaryService
import com.example.diarytablet.model.StickerStock
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val diaryService: DiaryService,
) : DiaryRepository {

    override suspend fun uploadDiary(drawFile: MultipartBody.Part, writeFile: MultipartBody.Part, videoFile: MultipartBody.Part): Response<Void> {
        return diaryService.uploadDiary(drawFile, writeFile, videoFile)
    }
    override suspend fun getDiaryList(year: Int, month: Int): Response<List<DiaryForList>> {
        return diaryService.getDiaryList(year, month)
    }

    override suspend fun getDiaryById(diaryId: Int): Diary {
        return diaryService.getDiaryById(diaryId)
    }

    override suspend fun getUserStickers(): Response<List<StickerStock>> {
        return diaryService.getUserStickers()
    }
}