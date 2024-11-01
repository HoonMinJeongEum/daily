package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.request.JoinRequestDto
import com.example.diaryApp.domain.dto.request.LoginRequestDto
import retrofit2.Response

interface UserRepository {
    suspend fun login(loginRequestDto: LoginRequestDto): Response<Void>
    suspend fun join(joinRequestDto: JoinRequestDto): Response<Void>
}