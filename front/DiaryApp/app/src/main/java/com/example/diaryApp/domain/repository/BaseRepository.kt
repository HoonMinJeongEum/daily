package com.example.diaryApp.domain.repository

import com.example.diaryApp.domain.dto.response.user.ReissueTokenResponseDto

interface BaseRepository {
    suspend fun reissueToken(): ReissueTokenResponseDto
}