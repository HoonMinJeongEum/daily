package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.*;
import com.ssafy.daily.reward.service.CouponService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    /**
     * 쿠폰 등록 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @param request 쿠폰 등록 요청 DTO
     * @return 성공 메시지 반환
     */
    @PostMapping
    public ResponseEntity<String> addCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddCouponRequest request
    ) {
        couponService.addCoupon(userDetails, request);
        return ResponseEntity.ok("쿠폰이 정상적으로 등록되었습니다.");
    }

    /**
     * 쿠폰 삭제 API
     * @param couponId 삭제할 쿠폰 ID
     * @return 삭제 완료 시 204 No Content 반환
     */
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(
            @PathVariable long couponId
    ) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 쿠폰 조회 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 사용자가 등록한 쿠폰 목록
     */
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CouponResponse> coupons = couponService.getCoupons(userDetails);
        return ResponseEntity.ok(coupons);
    }

    /**
     * 쿠폰 구매 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @param request 구매할 쿠폰 정보
     * @return 구매 후 남은 재화 개수 반환
     */
    @PostMapping("/buy")
    public ResponseEntity<Integer> buyCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BuyCouponRequest request
    ) throws Exception {
        int remainingShell = couponService.buyCoupon(userDetails, request);
        return ResponseEntity.ok(remainingShell);
    }

    /**
     * 사용자가 보유한 쿠폰 조회 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 사용자가 보유한 쿠폰 목록
     */
    @GetMapping("/user")
    public ResponseEntity<List<EarnedCouponResponse>> getUserCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<EarnedCouponResponse> userCoupons = couponService.getUserCoupons(userDetails);
        return ResponseEntity.ok(userCoupons);
    }

    /**
     * 쿠폰 사용 API
     * @param request 사용하려는 쿠폰 정보
     * @return 사용 완료 시 204 No Content 반환
     */
    @PatchMapping("/use")
    public ResponseEntity<Void> useCoupon(
            @RequestBody UseCouponRequest request
    ) {
        couponService.useCoupon(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 자식들 쿠폰 조회 API
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 가족 내 자녀들의 쿠폰 목록
     */
    @GetMapping("/child")
    public ResponseEntity<List<ChildCouponResponse>> getChildCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ChildCouponResponse> childCoupons = couponService.getChildCoupons(userDetails);
        return ResponseEntity.ok(childCoupons);
    }
}
