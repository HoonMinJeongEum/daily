package com.ssafy.daily.diary.dto;

import com.ssafy.daily.diary.entity.Diary;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryResponse {
    private int id;
    private String content;
    private String drawImg;
    private String writeImg;
    private String sound;
    private String video;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;

    public DiaryResponse(Diary diary, List<CommentResponse> comments){
        this.id = diary.getId();
        this.content = diary.getContent();
        this.drawImg = diary.getDrawImg();
        this.writeImg = diary.getWriteImg();
        this.sound = diary.getSound();
        this.video = diary.getVideo();
        this.createdAt = diary.getCreatedAt();
        this.comments = comments;
    }
}
