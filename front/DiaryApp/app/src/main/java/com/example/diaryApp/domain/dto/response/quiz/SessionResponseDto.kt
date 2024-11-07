package com.example.diaryApp.domain.dto.response.quiz

import com.google.gson.annotations.SerializedName

data class SessionResponseDto(
    @SerializedName("customSessionId")
    val customSessionId: String
)
