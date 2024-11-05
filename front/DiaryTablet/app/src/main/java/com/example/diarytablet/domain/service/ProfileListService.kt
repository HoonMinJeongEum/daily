package com.example.diarytablet.domain.service


import com.example.diarytablet.domain.dto.request.CreateProfileRequestDto
import com.example.diarytablet.domain.dto.request.SelectProfileRequestDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.domain.dto.response.ProfileListResponse
import com.example.diarytablet.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ProfileListService {
    @GET("${Const.API_PATH}user/profile")
    suspend fun getProfileList(): MutableList<Profile>

    @POST("${Const.API_PATH}user/member")
    suspend fun selectProfile(@Body selectProfileRequestDto : SelectProfileRequestDto): Response<Void>

    @POST("${Const.API_PATH}user/add")
    suspend fun createProfile(@Body createProfileRequestDto: CreateProfileRequestDto)
}