package com.example.diaryApp.domain.dto.request.quiz

import com.google.gson.annotations.SerializedName

data class SetWordRequestDto(
    @SerializedName("word")
    val word: String
)
