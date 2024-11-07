package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.dto.request.quest.UpdateQuestRequestDto
import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.domain.service.QuestService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepositoryImpl@Inject constructor(
    private val questService: QuestService
) : QuestRepository {
    override suspend fun updateQuest(request: UpdateQuestRequestDto): Response<StatusResponseDto> {
        return questService.updateQuest(request)
    }
}
