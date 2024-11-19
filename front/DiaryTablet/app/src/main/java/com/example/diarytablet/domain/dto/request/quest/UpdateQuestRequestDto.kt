package com.example.diarytablet.domain.dto.request.quest

import com.google.gson.annotations.SerializedName

data class UpdateQuestRequestDto(
    @SerializedName("questType")
    val questType: String
)
