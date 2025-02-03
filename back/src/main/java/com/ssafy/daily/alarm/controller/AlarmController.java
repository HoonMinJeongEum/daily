package com.ssafy.daily.alarm.controller;

import com.ssafy.daily.alarm.dto.AlarmListResponse;
import com.ssafy.daily.alarm.dto.CheckAlarmRequest;
import com.ssafy.daily.alarm.dto.SaveTokenRequest;
import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.StatusResponse;
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

    /**
     * 토큰 저장 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param request 저장할 토큰 정보
     * @return 처리 상태를 나타내는 응답
     */
    @PostMapping("/save")
    public ResponseEntity<StatusResponse> saveToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SaveTokenRequest request
    ) {
        return ResponseEntity.ok(alarmService.saveToken(userDetails, request));
    }

    /**
     * 알림 조회 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @return 사용자의 알림 목록
     */
    @GetMapping("/list")
    public ResponseEntity<AlarmListResponse> getAlarms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(alarmService.getAlarms(userDetails));
    }

    /**
     * 알림 확인 API
     *
     * @param request 확인할 알림의 ID 정보
     * @return 처리 상태를 나타내는 응답
     */
    @PostMapping("/check")
    public ResponseEntity<StatusResponse> checkAlarm(
            @RequestBody CheckAlarmRequest request
    ) {
        return ResponseEntity.ok(alarmService.checkAlarm(request));
    }
}
