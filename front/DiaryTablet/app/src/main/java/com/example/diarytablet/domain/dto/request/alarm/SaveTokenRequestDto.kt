package com.ssafy.daily.alarm.dto

import com.google.gson.annotations.SerializedName

data class SaveTokenRequestDto(
    @SerializedName("token")
    val token: String
)
