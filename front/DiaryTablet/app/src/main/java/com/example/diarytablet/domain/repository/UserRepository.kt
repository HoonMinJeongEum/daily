package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.LoginRequestDto
import retrofit2.Response

interface UserRepository {
    suspend fun login(loginRequest: LoginRequestDto): Response<Void>
}