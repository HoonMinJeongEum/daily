package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.response.MainScreenResponseDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.utils.Const
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface MainScreenService {
    @GET("${Const.API_PATH}user/main")
    suspend fun getMainScreenStatus(): MainScreenResponseDto

    @PATCH("${Const.API_PATH}user/member")
    suspend fun updateUserName(@Body userNameUpdateRequestDto : UserNameUpdateRequestDto)

}