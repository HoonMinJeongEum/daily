package com.example.diaryApp.domain.repository.alarm

import com.example.diaryApp.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diaryApp.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diaryApp.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diaryApp.domain.dto.response.alarm.StatusResponseDto
import retrofit2.Response

interface AlarmRepository {
    suspend fun saveToken(request: SaveTokenRequestDto): Response<StatusResponseDto>
    suspend fun getAlarms(): Response<AlarmListResponseDto>
    suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<StatusResponseDto>
}
