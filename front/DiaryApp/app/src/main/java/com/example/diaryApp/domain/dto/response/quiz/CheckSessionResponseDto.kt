package com.example.diaryApp.domain.dto.response.quiz

import com.google.gson.annotations.SerializedName

data class CheckSessionResponseDto(
    @SerializedName("sessionId")
    val sessionId: String
)
