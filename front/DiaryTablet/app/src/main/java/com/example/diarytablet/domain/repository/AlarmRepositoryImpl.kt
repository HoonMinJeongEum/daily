package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diarytablet.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diarytablet.domain.service.AlarmService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmService: AlarmService
) : AlarmRepository {
    override suspend fun saveToken(request: SaveTokenRequestDto): Response<StatusResponseDto> {
        return alarmService.saveToken(request)
    }
    override suspend fun getAlarms(): Response<AlarmListResponseDto> {
        return alarmService.getAlarms()
    }
    override suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<StatusResponseDto> {
        return alarmService.checkAlarm(request)
    }
}
