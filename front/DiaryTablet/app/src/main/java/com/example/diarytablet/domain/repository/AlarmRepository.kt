package com.ssafy.daily.alarm.repository

import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.ssafy.daily.alarm.dto.CheckAlarmRequestDto
import com.ssafy.daily.alarm.dto.SaveTokenRequestDto
import retrofit2.Response

interface AlarmRepository {
    suspend fun saveToken(request: SaveTokenRequestDto): Response<String>
    suspend fun getAlarms(): Response<List<AlarmResponseDto>>
    suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<String>
}
