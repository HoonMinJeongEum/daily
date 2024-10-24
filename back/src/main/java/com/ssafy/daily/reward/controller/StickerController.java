package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.service.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        String result = stickerService.buySticker(request);

        // 결과에 따라 응답 처리
        if (result.equals("스티커가 정상적으로 구매되었습니다.")) {
            return ResponseEntity.ok(result);  // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);  // 400 Bad Request
        }
    }
}
