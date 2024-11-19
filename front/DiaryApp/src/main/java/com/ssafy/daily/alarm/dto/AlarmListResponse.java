package com.ssafy.daily.alarm.dto;

import lombok.Data;

import java.util.List;

@Data
public class AlarmListResponse {
    List<AlarmResponse> list;

    public AlarmListResponse(List<AlarmResponse> list) {
        this.list = list;
    }
}
