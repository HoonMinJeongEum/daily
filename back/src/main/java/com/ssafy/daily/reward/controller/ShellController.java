package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.service.ShellService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shells")
public class ShellController {
    private final ShellService shellService;

    // 자식들 조개 조회
    @GetMapping("/child")
    public ResponseEntity<?> getChildShells(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(shellService.getChildShells(userDetails));
    }
}
