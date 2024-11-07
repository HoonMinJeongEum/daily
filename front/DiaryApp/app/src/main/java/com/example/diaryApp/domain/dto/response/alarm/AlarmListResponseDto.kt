package com.example.diaryApp.domain.dto.response.alarm

import com.google.gson.annotations.SerializedName

data class AlarmListResponseDto(
    @SerializedName("list")
    val list: List<AlarmResponseDto>,
)
