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
        val response = userService.login(loginRequest)

//        if (response.isSuccessful) {
//            // 헤더에서 토큰 가져오기
//            val newAccessToken = response.headers()["Authorization"]?.replace("Bearer ", "")
//            val newRefreshToken = response.headers()["Set-Cookie"]?.replace("refresh=", "")
//
//            // UserStore에 토큰 저장
//            if (newAccessToken != null && newRefreshToken != null) {
//                userStore.setValue(UserStore.KEY_ACCESS_TOKEN, newAccessToken)
//                userStore.setValue(UserStore.KEY_REFRESH_TOKEN, newRefreshToken)
//            }
//        }

        return response
    }
}
