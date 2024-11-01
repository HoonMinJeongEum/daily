package com.example.diaryApp.domain.repository

import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.dto.request.JoinRequestDto
import com.example.diaryApp.domain.dto.request.LoginRequestDto
import com.example.diaryApp.domain.services.UserService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val userStore: UserStore // UserStore 주입
) : UserRepository {
    override suspend fun login(loginRequestDto: LoginRequestDto): Response<Void> {
        val response = userService.login(loginRequestDto)
        return response
    }

    override suspend fun join(joinRequestDto: JoinRequestDto): Response<Void> {
        val response = userService.join(joinRequestDto)
        return response
    }
}