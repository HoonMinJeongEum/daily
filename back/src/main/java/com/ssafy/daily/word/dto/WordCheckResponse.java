package com.ssafy.daily.word.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class WordCheckResponse {
    private int status;
    private String msg;

    public WordCheckResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
}
