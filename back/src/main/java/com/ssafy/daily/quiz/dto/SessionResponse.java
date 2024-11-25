package com.ssafy.daily.quiz.dto;

import lombok.Data;

@Data
public class SessionResponse {
    private String customSessionId;;

    public SessionResponse(String customSessionId) {
        this.customSessionId = customSessionId;
    }
}
