package com.ssafy.daily.diary.dto;

import com.ssafy.daily.diary.entity.Diary;
import lombok.Data;

@Data
public class WriteDiaryRequest {
    private String content;
    private String img;
    private String sound;
}
