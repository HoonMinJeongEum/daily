package com.ssafy.daily.diary.dto;

import com.ssafy.daily.diary.entity.DiaryComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private String comment;
    private LocalDateTime createdAt;

    public CommentResponse(DiaryComment diaryComment){
        this.comment = diaryComment.getComment();
        this.createdAt = diaryComment.getCreatedAt();
    }
}
