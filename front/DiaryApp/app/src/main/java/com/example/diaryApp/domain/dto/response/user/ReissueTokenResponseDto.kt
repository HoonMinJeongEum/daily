package com.example.diaryApp.domain.dto.response.user

import com.google.gson.annotations.SerializedName

data class ReissueTokenResponseDto(
    @SerializedName("access_token")
    val accessToken: String
)