package com.example.diaryApp.domain.dto.request.alarm

import com.google.gson.annotations.SerializedName

data class SaveTokenRequestDto(
    @SerializedName("token")
    val token: String
)
