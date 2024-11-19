package com.ssafy.daily.diary.dto;

import lombok.Data;

@Data
public class FieldDto {
    private String valueType;
    private BoundingPolyDto boundingPoly;
    private String inferText;
    private double inferConfidence;
}