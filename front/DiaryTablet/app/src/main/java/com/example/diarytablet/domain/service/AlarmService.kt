package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmListResponseDto
import com.example.diarytablet.domain.dto.response.alarm.AlarmResponseDto
import com.example.diarytablet.utils.Const
import com.ssafy.daily.alarm.dto.CheckAlarmRequestDto
import com.ssafy.daily.alarm.dto.SaveTokenRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AlarmService {
    @POST("${Const.API_PATH}alarm/save")
    suspend fun saveToken(
        @Body request: SaveTokenRequestDto
    ): Response<StatusResponseDto>
    @GET("${Const.API_PATH}alarm/list/{userId}")
    suspend fun getAlarms(): Response<AlarmListResponseDto>
    @POST("${Const.API_PATH}alarm/check")
    suspend fun checkAlarm(
        @Body request: CheckAlarmRequestDto
    ): Response<StatusResponseDto>
}
