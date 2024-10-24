package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.dto.StatusResponse;
import com.ssafy.daily.reward.dto.EarnedStickerResponse;
import com.ssafy.daily.reward.dto.StickerResponse;
import com.ssafy.daily.reward.service.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> buySticker(@RequestBody BuyStickerRequest request) {
        StatusResponse response = stickerService.buySticker(request);

        return ResponseEntity.ok(response);
    }





}
