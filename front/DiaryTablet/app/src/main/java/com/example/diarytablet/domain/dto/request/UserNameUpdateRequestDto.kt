package com.example.diarytablet.domain.dto.request

import com.google.gson.annotations.SerializedName

data class UserNameUpdateRequestDto(
    @SerializedName("name")
    val name: String,
)
