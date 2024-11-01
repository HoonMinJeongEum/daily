package com.example.diarytablet.domain.dto.request

import com.google.gson.annotations.SerializedName

data class CreateProfileRequestDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("img")
    val img: String
)