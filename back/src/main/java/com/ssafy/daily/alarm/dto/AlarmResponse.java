package com.ssafy.daily.alarm.dto;

import com.ssafy.daily.alarm.entity.Alarm;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmResponse {
    private final long id;

    private final String titleId;

    private final String name;

    private final String title;

    private final String body;

    private final LocalDateTime createdAt;

    private final LocalDateTime confirmedAt;

    public AlarmResponse(Alarm alarm) {
        this.id = alarm.getId();
        this.titleId = alarm.getTitleId();
        this.name = alarm.getName();
        this.title = alarm.getTitle();
        this.body = alarm.getBody();
        this.createdAt = alarm.getCreatedAt();
        this.confirmedAt = alarm.getConfirmedAt();
    }
}
