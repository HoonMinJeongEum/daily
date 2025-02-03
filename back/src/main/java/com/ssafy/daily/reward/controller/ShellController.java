package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.ChildShellResponse;
import com.ssafy.daily.reward.service.ShellService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shells")
public class ShellController {
    private final ShellService shellService;

    /**
     * 자식들의 조개 잔액 조회 API
     *
     * @param userDetails 현재 로그인한 부모 사용자 정보
     * @return 자식들의 조개 정보 리스트
     */
    @GetMapping("/child")
    public ResponseEntity<List<ChildShellResponse>> getChildShellBalances(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ChildShellResponse> response = shellService.getChildShells(userDetails);
        return ResponseEntity.ok(response);
    }
}
