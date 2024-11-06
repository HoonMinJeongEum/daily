package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.service.DiaryService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val diaryService: DiaryService,
) : DiaryRepository {

    override suspend fun uploadDiary(drawFile: MultipartBody.Part, writeFile: MultipartBody.Part): Response<Void> {
        return diaryService.uploadDiary(drawFile, writeFile)
    }
}