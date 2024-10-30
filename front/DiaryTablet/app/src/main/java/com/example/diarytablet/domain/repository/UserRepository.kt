package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.dto.response.LoginResponseDto
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun login(loginRequestDto: LoginRequestDto): Flow<Response<LoginResponseDto>>

}