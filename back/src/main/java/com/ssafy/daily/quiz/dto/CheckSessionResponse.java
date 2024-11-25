package com.ssafy.daily.quiz.dto;

import lombok.Data;

@Data
public class CheckSessionResponse {
    private String sessionId;

    public CheckSessionResponse(String sessionId) {
        this.sessionId = sessionId;
    }
}
