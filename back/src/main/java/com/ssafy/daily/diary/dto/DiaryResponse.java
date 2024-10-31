package com.ssafy.daily.diary.dto;

import com.ssafy.daily.diary.entity.Diary;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryResponse {
    private int id;
    private String content;
    private String img;
    private String sound;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;

    public DiaryResponse(Diary diary, List<CommentResponse> comments){
        this.id = diary.getId();
        this.content = diary.getContent();
        this.img = diary.getImg();
        this.sound = diary.getSound();
        this.createdAt = diary.getCreatedAt();
        this.comments = comments;
    }
}
