package com.ssafy.daily.diary.dto;

import lombok.Data;

@Data
public class WriteCommentRequest {
    private int diaryId;
    private String comment;
}
