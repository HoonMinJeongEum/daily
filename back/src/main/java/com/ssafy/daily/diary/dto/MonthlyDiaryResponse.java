package com.ssafy.daily.diary.dto;

import com.ssafy.daily.diary.entity.Diary;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
public class MonthlyDiaryResponse {
    private int id;
    private LocalDateTime createdAt;
    public MonthlyDiaryResponse(Diary diary){
        this.id = diary.getId();
        this.createdAt = diary.getCreatedAt();
    }
}
