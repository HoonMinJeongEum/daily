package com.example.diarytablet.domain.service


import com.example.diarytablet.domain.dto.response.ProfileListResponse
import com.example.diarytablet.utils.Const
import retrofit2.http.GET

interface ProfileListService {
    @GET("${Const.API_PATH}user/profile")
    suspend fun getProfileList(): ProfileListResponse

}