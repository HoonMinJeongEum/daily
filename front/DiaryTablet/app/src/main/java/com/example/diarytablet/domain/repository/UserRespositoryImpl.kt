package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.response.LoginResponseDto
import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.service.UserService
import com.example.diarytablet.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {
    override suspend fun login(loginRequestDto: LoginRequestDto): Flow<Response<LoginResponseDto>> {
        return flow {
            emit(Response.Loading) // Optional: 로딩 상태
            try {
                val response = userService.login(loginRequestDto)
                emit(Response.Success(response))
            } catch (e: Exception) {
                emit(Response.Failure(e))    }
        }
    }
}