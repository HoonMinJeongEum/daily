package com.example.diarytablet.domain.dto.response.quiz

import com.google.gson.annotations.SerializedName

data class TokenResponseDto(
    @SerializedName("token")
    val token: String
)