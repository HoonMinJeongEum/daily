package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.response.profile.Profile
import com.example.diaryApp.domain.dto.response.profile.ProfileCreateDto
import com.example.diaryApp.utils.Const
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileListService {
    @GET("${Const.API_PATH}user/profile")
    suspend fun getProfileList(): MutableList<Profile>

    @DELETE("${Const.API_PATH}user/member/{memberId}")
    suspend fun deleteProfile(@Path("memberId") memberId:Int)

    @Multipart
    @POST("${Const.API_PATH}user/add")
    suspend fun createProfile(
        @Part("memberName") name : RequestBody,
        @Part img: MultipartBody.Part?):Response<Void>
}