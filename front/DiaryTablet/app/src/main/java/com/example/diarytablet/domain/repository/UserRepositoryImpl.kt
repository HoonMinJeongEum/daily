package com.example.diarytablet.domain.repository

import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.dto.request.LoginRequestDto
import com.example.diarytablet.domain.service.UserService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val userStore: UserStore // UserStore 주입
) : UserRepository {
    override suspend fun login(loginRequest: LoginRequestDto): Response<Void> {
        return userService.login(loginRequest)
    }
}
