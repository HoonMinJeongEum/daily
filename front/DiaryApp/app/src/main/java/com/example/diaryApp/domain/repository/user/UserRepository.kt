package com.example.diaryApp.domain.repository.user

import com.example.diaryApp.domain.dto.request.user.JoinRequestDto
import com.example.diaryApp.domain.dto.request.user.LoginRequestDto
import retrofit2.Response

interface UserRepository {
    suspend fun login(loginRequestDto: LoginRequestDto): Response<Void>
    suspend fun join(joinRequestDto: JoinRequestDto): Response<Void>
    suspend fun checkUsernameAvailability(username: String): Response<Void>
}