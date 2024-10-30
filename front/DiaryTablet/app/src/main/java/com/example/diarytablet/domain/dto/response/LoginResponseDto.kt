package com.example.diarytablet.domain.dto.response

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)
