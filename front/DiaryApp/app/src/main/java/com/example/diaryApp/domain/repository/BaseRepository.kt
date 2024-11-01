package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.response.ReissueTokenResponseDto

interface BaseRepository {
    suspend fun reissueToken(): ReissueTokenResponseDto
}