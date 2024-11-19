package com.example.diarytablet.domain.dto.response.alarm

import com.google.gson.annotations.SerializedName

data class AlarmListResponseDto(
    @SerializedName("list")
    val list: List<AlarmResponseDto>,
)
