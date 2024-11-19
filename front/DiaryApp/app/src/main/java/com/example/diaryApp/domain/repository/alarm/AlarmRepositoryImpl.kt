package com.example.diaryApp.domain.repository.alarm

import com.example.diaryApp.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diaryApp.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diaryApp.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diaryApp.domain.dto.response.alarm.StatusResponseDto
import com.example.diaryApp.domain.services.AlarmService
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
