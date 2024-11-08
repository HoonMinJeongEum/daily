package com.example.diaryApp.domain.dto.request.quiz

import com.google.gson.annotations.SerializedName

data class CheckSessionRequestDto(
    @SerializedName("childName")
    val childName: String
)
