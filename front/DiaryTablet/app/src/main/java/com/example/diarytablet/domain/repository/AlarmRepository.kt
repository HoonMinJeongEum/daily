package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diarytablet.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmListResponseDto
import retrofit2.Response

interface AlarmRepository {
    suspend fun saveToken(request: SaveTokenRequestDto): Response<StatusResponseDto>
    suspend fun getAlarms():Response<AlarmListResponseDto>
    suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<StatusResponseDto>
}
