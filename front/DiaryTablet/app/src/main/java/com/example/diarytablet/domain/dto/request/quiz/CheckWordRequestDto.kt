package com.example.diarytablet.domain.dto.request.quiz

import com.google.gson.annotations.SerializedName

data class CheckWordRequestDto(
    @SerializedName("word")
    val word: String
)
