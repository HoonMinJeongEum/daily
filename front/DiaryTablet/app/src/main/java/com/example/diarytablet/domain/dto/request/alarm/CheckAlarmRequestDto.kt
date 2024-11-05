package com.ssafy.daily.alarm.dto

import com.google.gson.annotations.SerializedName

data class CheckAlarmRequestDto(
    @SerializedName("alarmId")
    val alarmId: Long
)
