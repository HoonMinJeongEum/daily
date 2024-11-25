package com.ssafy.daily.diary.dto;

import lombok.Data;

import java.util.List;

@Data
class BoundingPolyDto {
    private List<VertexDto> vertices;
}