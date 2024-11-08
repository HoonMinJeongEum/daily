package com.example.diarytablet.domain.dto.response

import com.google.gson.annotations.SerializedName

data class StatusResponseDto(
    @SerializedName("status")
    val status: Int,

    @SerializedName("msg")
    val msg: String
)
