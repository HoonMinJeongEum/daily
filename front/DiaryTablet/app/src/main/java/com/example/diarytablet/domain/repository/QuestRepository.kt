package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.quest.UpdateQuestRequestDto
import com.example.diarytablet.domain.dto.response.StatusResponseDto
import retrofit2.Response

interface QuestRepository {
    suspend fun updateQuest(request: UpdateQuestRequestDto): Response<StatusResponseDto>
}