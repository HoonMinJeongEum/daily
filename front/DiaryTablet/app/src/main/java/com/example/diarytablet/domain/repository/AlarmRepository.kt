package com.ssafy.daily.alarm.repository

import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.ssafy.daily.alarm.dto.CheckAlarmRequestDto
import com.ssafy.daily.alarm.dto.SaveTokenRequestDto
import retrofit2.Response

interface AlarmRepository {
    suspend fun saveToken(request: SaveTokenRequestDto): Response<StatusResponseDto>
    suspend fun getAlarms(): Response<AlarmListResponseDto>
    suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<StatusResponseDto>
}
