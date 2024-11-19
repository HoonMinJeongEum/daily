package com.example.diaryApp.domain.dto.request.alarm

import com.google.gson.annotations.SerializedName

data class CheckAlarmRequestDto(
    @SerializedName("alarmId")
    val alarmId: Long
)
