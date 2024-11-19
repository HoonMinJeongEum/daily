package com.example.diarytablet.domain.service

import com.example.diarytablet.domain.dto.request.quest.UpdateQuestRequestDto
import com.example.diarytablet.domain.dto.response.StatusResponseDto
import com.example.diarytablet.utils.Const
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH

interface QuestService {
    @PATCH("${Const.API_PATH}quest")
    suspend fun updateQuest(
        @Body request: UpdateQuestRequestDto
    ): Response<StatusResponseDto>

}