package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.dto.response.LoginResponseDto
import com.example.diarytablet.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("${Const.API_PATH}user/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<Void> // Response<Void>로 수정
}
