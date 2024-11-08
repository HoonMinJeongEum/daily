package com.example.diarytablet.domain.dto.request

import com.google.gson.annotations.SerializedName

data class CompleteMissionItemRequestDto(
    @SerializedName("questType")
    val questType : String
)
