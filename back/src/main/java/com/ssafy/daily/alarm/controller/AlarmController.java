package com.ssafy.daily.alarm.controller;

import com.ssafy.daily.alarm.dto.CheckAlarmRequest;
import com.ssafy.daily.alarm.dto.SaveTokenRequest;
import com.ssafy.daily.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    // 토큰 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveToken(@RequestBody SaveTokenRequest request) {
        alarmService.saveToken(request);
        return ResponseEntity.ok("알림 토큰이 정상적으로 등록되었습니다.");
    }

    // 알림 조회
    @GetMapping("/list")
    public ResponseEntity<?> getAlarms() {
        return ResponseEntity.ok(alarmService.getAlarms());
    }
    
    // 알림 확인
    @PostMapping("/check")
    public ResponseEntity<?> checkAlarm(@RequestBody CheckAlarmRequest request) {
        alarmService.checkAlarm(request);
        return ResponseEntity.ok("알림이 정상적으로 확인되었습니다.");
    }
}
