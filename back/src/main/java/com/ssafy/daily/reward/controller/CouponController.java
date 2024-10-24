package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<?> addCoupon() {
//        couponService.addCoupon();
        return null;
    }
}
