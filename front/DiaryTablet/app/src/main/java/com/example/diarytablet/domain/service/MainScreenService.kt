package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.CompleteMissionItemRequestDto
import com.example.diarytablet.domain.dto.request.UserNameUpdateRequestDto
import com.example.diarytablet.domain.dto.response.CompleteMissionItemResponseDto
import com.example.diarytablet.domain.dto.response.MainScreenResponseDto
import com.example.diarytablet.domain.dto.response.Profile
import com.example.diarytablet.utils.Const
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface MainScreenService {
    @GET("${Const.API_PATH}user/main")
    suspend fun getMainScreenStatus(): MainScreenResponseDto

    @PATCH("${Const.API_PATH}user/member")
    suspend fun updateUserName(@Body userNameUpdateRequestDto : UserNameUpdateRequestDto)

    @PATCH("${Const.API_PATH}quest")
    suspend fun completeMissionItem(@Body completeMissionItemRequestDto : CompleteMissionItemRequestDto) :
            Response<CompleteMissionItemResponseDto>
    @Multipart
    @PATCH("${Const.API_PATH}user/member/img")
    suspend fun updateProfileImage(
        @Part file: MultipartBody.Part,
    ): Response<Void>
}