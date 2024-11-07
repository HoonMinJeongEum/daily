package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.request.user.JoinRequestDto
import com.example.diaryApp.domain.dto.request.user.LoginRequestDto
import com.example.diaryApp.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("${Const.API_PATH}user/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<Void>

    @POST("${Const.API_PATH}user/join")
    suspend fun join(@Body joinRequestDto: JoinRequestDto): Response<Void>
}
