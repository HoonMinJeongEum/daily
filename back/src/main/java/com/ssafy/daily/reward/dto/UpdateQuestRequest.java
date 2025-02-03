package com.ssafy.daily.reward.dto;

import com.ssafy.daily.common.QuestType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateQuestRequest {

    @NotNull(message = "퀘스트 타입(questType)은 필수 입력값입니다.")
    private QuestType questType;
}
