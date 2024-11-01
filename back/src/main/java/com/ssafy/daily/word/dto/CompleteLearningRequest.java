package com.ssafy.daily.word.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CompleteLearningRequest {
    private int id;
    private MultipartFile image;
}
