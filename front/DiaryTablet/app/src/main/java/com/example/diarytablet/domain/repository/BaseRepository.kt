package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.response.ReissueTokenResponseDto

interface BaseRepository {
    suspend fun reissueToken(): ReissueTokenResponseDto
}