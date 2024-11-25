package com.ssafy.daily.word.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearnedWordResponse {
    private final Long id;
    private final String word;
    private final String img;
    private final String org;
    private final LocalDateTime createdAt;
}
