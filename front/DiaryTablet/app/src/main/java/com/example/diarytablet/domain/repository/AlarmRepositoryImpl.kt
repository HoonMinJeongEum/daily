package com.ssafy.daily.alarm.repository

import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.example.diarytablet.domain.service.AlarmService
import com.ssafy.daily.alarm.dto.CheckAlarmRequestDto
import com.ssafy.daily.alarm.dto.SaveTokenRequestDto
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
