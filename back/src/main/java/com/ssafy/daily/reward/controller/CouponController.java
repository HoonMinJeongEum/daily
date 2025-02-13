package com.ssafy.daily.reward.controller;

import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.exception.MyNotFoundException;
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
     * @param userDetails 사용자 정보
     * @param request 등록하는 쿠폰 정보
     * @throws MyNotFoundException 해당 가족 계정을 찾을 수 없을 때 던지는 예외
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
     * @param couponId 쿠폰 고유 번호
     * @throws MyNotFoundException 쿠폰을 찾을 수 없을 때 던지는 예외
     * @throws AlreadyOwnedException 이미 구매한 쿠폰일 때 던지는 예외
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
     * @param userDetails 사용자 정보
     * @return 아직 구매하지 않은 쿠폰 리스트
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
     * @param userDetails 사용자 정보
     * @param request 구매하는 쿠폰 정보
     * @return 남은 조개 수
     * @throws MyNotFoundException 쿠폰을 찾을 수 없을 때 던지는 예외
     * @throws AlreadyOwnedException 이미 구매한 쿠폰일 때 던지는 예외
     * @throws Exception 알림에서 에러가 발생했을 때 던지는 예외
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
     * @param userDetails 사용자 정보
     * @return 사용자가 보유한 쿠폰 리스트
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
     * @param request 사용하는 쿠폰 정보
     * @return 사용 완료 시 204 No Content
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
