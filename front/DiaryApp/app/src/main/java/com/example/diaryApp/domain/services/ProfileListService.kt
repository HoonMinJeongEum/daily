package com.example.diaryApp.domain.services


import com.example.diaryApp.domain.dto.response.ProfileListResponse
import com.example.diaryApp.utils.Const
import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileListService {
    @GET("${Const.API_PATH}user/profile")
    suspend fun getProfileList(): ProfileListResponse

    @POST("${Const.API_PATH}user/member")
    suspend fun selectProfile(@Body selectProfileRequestDto : SelectProfileRequestDto)

    @POST("${Const.API_PATH}user/add")
    suspend fun createProfile(@Body createProfileRequestDto: CreateProfileRequestDto)
}