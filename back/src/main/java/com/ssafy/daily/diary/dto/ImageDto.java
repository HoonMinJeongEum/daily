package com.ssafy.daily.diary.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImageDto {
    private String uid;
    private String name;
    private String inferResult;
    private String message;
    private ValidationResultDto validationResult;
    private List<FieldDto> fields;
}