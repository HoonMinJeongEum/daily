package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.service.StickerService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stickers")
public class StickerController {

    private final StickerService stickerService;
    
    // 사용자 스티커 조회
    @GetMapping("/user")
    public ResponseEntity<?> getUserSticker(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(stickerService.getUserSticker(userDetails));
    }
    
    // 모든 스티커 조회
    @GetMapping
    public ResponseEntity<?> getSticker(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(stickerService.getSticker(userDetails));
    }
    
    // 스티커 구매
    @PostMapping("/buy")
    public ResponseEntity<?> buySticker(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody BuyStickerRequest request) {
        return ResponseEntity.ok(stickerService.buySticker(userDetails, request));
    }
}
