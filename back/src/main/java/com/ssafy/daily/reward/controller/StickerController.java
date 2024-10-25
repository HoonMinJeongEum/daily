package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.service.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stickers")
public class StickerController {

    private final StickerService stickerService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserSticker() {
        return ResponseEntity.ok(stickerService.getUserSticker());
    }

    @GetMapping
    public ResponseEntity<?> getSticker() {
        return ResponseEntity.ok(stickerService.getSticker());
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buySticker(@RequestBody BuyStickerRequest request) {
        stickerService.buySticker(request);

        return ResponseEntity.ok("스티커가 정상적으로 구매되었습니다.");
    }
}
