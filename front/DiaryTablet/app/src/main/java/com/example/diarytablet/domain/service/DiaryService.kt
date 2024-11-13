package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.response.diary.Diary
import com.example.diarytablet.domain.dto.response.diary.DiaryForList
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.utils.Const
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface DiaryService {
    @Multipart
    @POST("${Const.API_PATH}diaries")
    suspend fun uploadDiary(
        @Part drawFile: MultipartBody.Part,
        @Part writeFile: MultipartBody.Part,
        @Part videoFile: MultipartBody.Part,
    ): Response<Void>
    @GET("${Const.API_PATH}diaries")
    suspend fun getDiaryList(
        @Query("year") year: Int,
        @Query("month") month: Int
    ) : Response<List<DiaryForList>>

    @GET("${Const.API_PATH}diaries/{diaryId}")
    suspend fun getDiaryById(
        @Path("diaryId") diaryId:Int
    ) : Diary

    @GET("${Const.API_PATH}stickers/user")
    suspend fun getUserStickers(): Response<List<StickerStock>>

}
