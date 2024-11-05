package com.example.diaryApp.domain.repository.alarm

import com.example.diaryApp.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diaryApp.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diaryApp.domain.dto.response.alarm.AlarmResponseDto
import com.example.diaryApp.domain.services.AlarmService
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
