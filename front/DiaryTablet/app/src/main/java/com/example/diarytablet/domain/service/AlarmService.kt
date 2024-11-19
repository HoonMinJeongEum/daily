package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.alarm.CheckAlarmRequestDto
import com.example.diarytablet.domain.dto.request.alarm.SaveTokenRequestDto
import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diarytablet.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AlarmService {
    @POST("${Const.API_PATH}alarm/save")
    suspend fun saveToken(
        @Body request: SaveTokenRequestDto
    ): Response<StatusResponseDto>
    @GET("${Const.API_PATH}alarm/list")
    suspend fun getAlarms(): Response<AlarmListResponseDto>
    @POST("${Const.API_PATH}alarm/check")
    suspend fun checkAlarm(
        @Body request: CheckAlarmRequestDto
    ): Response<StatusResponseDto>
}
