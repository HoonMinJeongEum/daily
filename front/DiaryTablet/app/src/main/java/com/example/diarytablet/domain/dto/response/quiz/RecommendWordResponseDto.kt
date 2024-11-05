package com.example.diarytablet.domain.dto.response.quiz

import com.google.gson.annotations.SerializedName

data class RecommendWordResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("word")
    val word: String
)
