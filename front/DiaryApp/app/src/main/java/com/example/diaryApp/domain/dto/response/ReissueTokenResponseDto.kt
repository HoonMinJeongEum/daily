package com.example.diaryApp.domain.dto.response

import com.google.gson.annotations.SerializedName

data class ReissueTokenResponseDto(
    @SerializedName("access_token")
    val accessToken: String
)