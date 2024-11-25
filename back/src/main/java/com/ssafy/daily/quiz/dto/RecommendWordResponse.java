package com.ssafy.daily.quiz.dto;

import lombok.Data;

@Data
public class RecommendWordResponse {
    private int id;
    private String word;

    public RecommendWordResponse(int id, String word) {
        this.id = id;
        this.word = word;
    }
}
