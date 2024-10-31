package com.ssafy.daily.word.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LearningWordResponse {
    private int id;
    private String word;
    private String img;
}
