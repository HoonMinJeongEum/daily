package com.example.diaryApp.domain.dto.request.quiz

import com.google.gson.annotations.SerializedName

data class SessionRequestDto(
    @SerializedName("customSessionId")
    val customSessionId: String
)
