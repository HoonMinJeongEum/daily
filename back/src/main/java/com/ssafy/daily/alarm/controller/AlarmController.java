package com.ssafy.daily.alarm.controller;

import com.ssafy.daily.alarm.dto.CheckAlarmRequest;
import com.ssafy.daily.alarm.dto.SaveTokenRequest;
import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    // 토큰 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveToken(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SaveTokenRequest request) {
        return ResponseEntity.ok(alarmService.saveToken(userDetails, request));
    }

    // 알림 조회
    @GetMapping("/list")
    public ResponseEntity<?> getAlarms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(alarmService.getAlarms(userDetails));
    }
    
    // 알림 확인
    @PostMapping("/check")
    public ResponseEntity<?> checkAlarm(@RequestBody CheckAlarmRequest request) {
        return ResponseEntity.ok(alarmService.checkAlarm(request));
    }
}
