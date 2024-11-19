package com.example.diaryApp.domain.dto.response.alarm

import com.google.gson.annotations.SerializedName

data class StatusResponseDto(
    @SerializedName("status")
    val status: Int,

    @SerializedName("msg")
    val msg: String
)
