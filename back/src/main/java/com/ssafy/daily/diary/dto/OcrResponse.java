package com.ssafy.daily.diary.dto;

import lombok.Data;

import java.util.List;
@Data
public class OcrResponse {
    private String version;
    private String requestId;
    private long timestamp;
    private List<ImageDto> images;
}