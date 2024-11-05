package com.ssafy.daily.alarm.repository

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
    override suspend fun saveToken(request: SaveTokenRequestDto): Response<String> {
        return alarmService.saveToken(request)
    }
    override suspend fun getAlarms(): Response<List<AlarmResponseDto>> {
        return alarmService.getAlarms()
    }
    override suspend fun checkAlarm(request: CheckAlarmRequestDto): Response<String> {
        return alarmService.checkAlarm(request)
    }
}
