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

    @GetMapping("/user")
    public ResponseEntity<?> getUserSticker(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(stickerService.getUserSticker(userDetails));
    }

    @GetMapping
    public ResponseEntity<?> getSticker(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(stickerService.getSticker(userDetails));
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buySticker(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody BuyStickerRequest request) {
        stickerService.buySticker(userDetails, request);

        return ResponseEntity.ok("스티커가 정상적으로 구매되었습니다.");
    }
}
