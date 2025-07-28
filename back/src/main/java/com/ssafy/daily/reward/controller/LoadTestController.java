package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.*;
import com.ssafy.daily.reward.service.LoadTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class LoadTestController {
    private final LoadTestService loadTestService;

    @GetMapping
    public ResponseEntity<List<Long>> insertCoupon (
    ) {
        return ResponseEntity.ok(loadTestService.insertCoupon());
    }

    @GetMapping("/insertAndBuy")
    public ResponseEntity<List<Long>> insertBuyCoupon (
    ) {
        return ResponseEntity.ok(loadTestService.insertBuyCoupon());
    }

}
