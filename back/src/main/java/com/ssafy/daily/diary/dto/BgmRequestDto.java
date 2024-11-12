package com.ssafy.daily.diary.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class BgmRequestDto {
    private String diaryContent;
    private Integer tokens;
    public BgmRequestDto(String diaryContent, Integer tokens){
        this.diaryContent = diaryContent;
        this.tokens = tokens;
    }
}
