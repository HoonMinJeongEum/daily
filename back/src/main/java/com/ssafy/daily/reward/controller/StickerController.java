package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.dto.EarnedStickerResponse;
import com.ssafy.daily.reward.dto.StickerResponse;
import com.ssafy.daily.reward.service.StickerService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stickers")
public class StickerController {
    private final StickerService stickerService;

    /**
     * 사용자가 보유한 스티커 조회 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 사용자가 보유한 스티커 목록
     */
    @GetMapping("/user")
    public ResponseEntity<List<EarnedStickerResponse>> getUserStickers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<EarnedStickerResponse> stickers = stickerService.getUserSticker(userDetails);
        return ResponseEntity.ok(stickers);
    }

    /**
     * 모든 스티커 조회 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 사용자가 아직 보유하지 않은 스티커 목록
     */
    @GetMapping
    public ResponseEntity<List<StickerResponse>> getAllStickers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<StickerResponse> stickers = stickerService.getSticker(userDetails);
        return ResponseEntity.ok(stickers);
    }

    /**
     * 스티커 구매 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @param request 구매할 스티커 정보
     * @return 현재 사용자 보유 조개 개수
     */
    @PostMapping("/buy")
    public ResponseEntity<Integer> purchaseSticker(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BuyStickerRequest request) {
        int updatedShellCount = stickerService.buySticker(userDetails, request);
        return ResponseEntity.ok(updatedShellCount);
    }
}
