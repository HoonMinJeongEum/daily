package com.example.diaryApp.domain.repository.alarm

import com.example.diaryApp.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diaryApp.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import retrofit2.Response

interface AlarmRepository {
    suspend fun saveToken(request: SaveTokenRequestDto): Response<String>
    suspend fun getAlarms(): Response<List<AlarmResponseDto>>
    suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<String>
}
