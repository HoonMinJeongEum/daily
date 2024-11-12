package com.ssafy.daily.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class BgmRequestDto {
    @JsonProperty("diary_content")
    private String diaryContent;
    private Integer tokens;

    public BgmRequestDto(String diaryContent, Integer tokens){
        this.diaryContent = diaryContent;
        this.tokens = tokens;
    }
}
