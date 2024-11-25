package com.ssafy.daily.reward.dto;

import com.ssafy.daily.common.QuestType;
import lombok.Data;

@Data
public class UpdateQuestRequest {
    private QuestType questType;
}
