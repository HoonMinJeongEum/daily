package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.request.user.JoinRequestDto
import com.example.diaryApp.domain.dto.request.user.LoginRequestDto
import com.example.diaryApp.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @POST("${Const.API_PATH}user/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<Void>

    @GET("${Const.API_PATH}user/check/{username}")
    suspend fun checkUsernameAvailability(@Path("username") username: String): Response<Void>

    @POST("${Const.API_PATH}user/join")
    suspend fun join(@Body joinRequestDto: JoinRequestDto): Response<Void>
}
