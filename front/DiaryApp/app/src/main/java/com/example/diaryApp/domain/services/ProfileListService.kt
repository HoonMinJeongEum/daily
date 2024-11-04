package com.example.diaryApp.domain.services


import com.example.diaryApp.domain.dto.response.Profile
import com.example.diaryApp.utils.Const
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ProfileListService {
    @GET("${Const.API_PATH}user/profile")
    suspend fun getProfileList(): MutableList<Profile>

    @Multipart
    @POST("${Const.API_PATH}user/add")
    suspend fun createProfile(
        @Part("memberName") name : RequestBody,
        @Part img: MultipartBody.Part?)
}