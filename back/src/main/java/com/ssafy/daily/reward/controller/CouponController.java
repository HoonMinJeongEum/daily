package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.AddCouponRequest;
import com.ssafy.daily.reward.dto.BuyCouponRequest;
import com.ssafy.daily.reward.dto.UseCouponRequest;
import com.ssafy.daily.reward.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    // 쿠폰 등록
    @PostMapping
    public ResponseEntity<?> addCoupon(@RequestBody AddCouponRequest request) {
        couponService.addCoupon(request);
        return ResponseEntity.ok("쿠폰이 정상적으로 등록되었습니다.");
    }

    // 쿠폰 삭제
    @DeleteMapping("{couponId}")
    public ResponseEntity<?> deleteCoupon(@PathVariable long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.ok("쿠폰이 정상적으로 삭제되었습니다.");
    }

    // 쿠폰 리스트 조회
    @GetMapping
    public ResponseEntity<?> getCoupons() {
        return ResponseEntity.ok(couponService.getCoupons());
    }

    // 쿠폰 구매
    @PostMapping("/buy")
    public ResponseEntity<?> buyCoupon(@RequestBody BuyCouponRequest request) {
        couponService.buyCoupon(request);
        return ResponseEntity.ok("쿠폰이 정상적으로 구매되었습니다.");
    }

    // 사용자가 보유한 쿠폰 조회
    @GetMapping("/user")
    public ResponseEntity<?> getUserCoupons() {
        return ResponseEntity.ok(couponService.getUserCoupons());
    }

    // 쿠폰 사용
    @PatchMapping("/use")
    public ResponseEntity<?> useCoupon(@RequestBody UseCouponRequest request) {
        couponService.useCoupon(request);
        return ResponseEntity.ok("쿠폰이 정상적으로 사용되었습니다.");
    }
}
