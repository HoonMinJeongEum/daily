package com.ssafy.daily.reward.service;

import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Content;
import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.exception.MyNotFoundException;
import com.ssafy.daily.reward.dto.*;
import com.ssafy.daily.reward.entity.Coupon;
import com.ssafy.daily.reward.entity.EarnedCoupon;
import com.ssafy.daily.reward.repository.CouponRepository;
import com.ssafy.daily.reward.repository.EarnedCouponRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final EarnedCouponRepository earnedCouponRepository;
    private final MemberRepository memberRepository;
    private final ShellService shellService;
    private final AlarmService alarmService;
    private final CacheManager cacheManager;

    /**
     * 쿠폰 등록
     * @param userDetails 사용자 정보
     * @param request 등록하는 쿠폰 정보
     * @throws MyNotFoundException 해당 가족 계정을 찾을 수 없을 때 던지는 예외
     */
    @CacheEvict(value = "coupons", key = "#userDetails.family.id")
    @Transactional
    public void addCoupon(CustomUserDetails userDetails, AddCouponRequest request) {
        Family family = shellService.validateFamily(userDetails.getFamily().getId());

        Coupon coupon = Coupon.builder()
                .family(family)
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        couponRepository.save(coupon);
    }

    /**
     * 쿠폰 삭제
     * @param userDetails 사용자 정보
     * @param couponId 쿠폰 고유 번호
     * @throws MyNotFoundException 쿠폰을 찾을 수 없을 때 던지는 예외
     * @throws AlreadyOwnedException 이미 구매한 쿠폰일 때 던지는 예외
     */
    @CacheEvict(value = "coupons", key = "#userDetails.family.id")
    @Transactional
    public void deleteCoupon(CustomUserDetails userDetails, long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new MyNotFoundException("해당 쿠폰을 찾을 수 없습니다."));

        coupon.validateDeletable();

        couponRepository.deleteById(couponId);
    }

    /**
     * 사용 가능한 쿠폰 목록 조회
     * @param userDetails 사용자 정보
     * @return 아직 구매하지 않은 쿠폰 리스트
     */
    @Cacheable(
            value = "coupons",
            key = "#userDetails.family.id",
            sync = true
    )
    public List<CouponResponse> getCoupons(CustomUserDetails userDetails) {
        return couponRepository.findByPurchasedAtIsNullAndFamilyId(userDetails.getFamily().getId()).stream()
                .map(CouponResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 구매
     * @param userDetails 사용자 정보
     * @param request 구매하는 쿠폰 정보
     * @return 남은 조개 수
     * @throws MyNotFoundException 쿠폰을 찾을 수 없을 때 던지는 예외
     * @throws AlreadyOwnedException 이미 구매한 쿠폰일 때 던지는 예외
     * @throws Exception 알림에서 에러가 발생했을 때 던지는 예외
     */
    @Caching(evict = {
        @CacheEvict(value = "coupons", key = "#userDetails.family.id"),
        @CacheEvict(value = "coupons-user", key = "#userDetails.member.id"),
        @CacheEvict(value = "coupons-child", key = "#userDetails.family.id")
    })
    @Transactional
    public int buyCoupon(CustomUserDetails userDetails, BuyCouponRequest request) throws Exception {
        Member member = shellService.validateMember(userDetails.getMember().getId());
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new MyNotFoundException("해당 쿠폰을 찾을 수 없습니다."));
        shellService.validateShellBalance(member.getId(), coupon.getPrice());

        coupon.buy(LocalDateTime.now());

        EarnedCoupon earnedCoupon = EarnedCoupon.builder()
                .coupon(coupon)
                .member(member)
                .build();
        earnedCouponRepository.save(earnedCoupon);

        shellService.saveShellLog(member, -coupon.getPrice(), Content.COUPON);

//        alarmService.sendNotification(member.getName(), String.valueOf(coupon.getId()), userDetails.getFamily().getId(), Role.PARENT, "쿠폰", member.getName() + " - 쿠폰을 구매했어요");
        return shellService.getUserShell(member.getId());
    }

    /**
     * 사용자가 보유한 쿠폰 조회
     * @param userDetails 사용자 정보
     * @return 사용자가 보유한 쿠폰 리스트
     */
    @Cacheable(
            value = "coupons-user",
            key = "#userDetails.member.id",
            sync = true
    )
    public List<EarnedCouponResponse> getUserCoupons(CustomUserDetails userDetails) {
        return earnedCouponRepository.findByMemberIdAndUsedAtIsNull(userDetails.getMember().getId()).stream()
                .map(EarnedCouponResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 사용
     * @param userDetails 사용자 정보
     * @param request 사용하는 쿠폰 정보
     */
    @Transactional
    public void useCoupon(CustomUserDetails userDetails, UseCouponRequest request) {
        EarnedCoupon earnedCoupon = earnedCouponRepository.findById(request.getEarnedCouponId())
                .orElseThrow(() -> new MyNotFoundException("쿠폰이 존재하지 않습니다."));

        earnedCoupon.use(LocalDateTime.now());

        Objects.requireNonNull(cacheManager.getCache("coupons-user")).evict(earnedCoupon.getMember().getId());

        Objects.requireNonNull(cacheManager.getCache("coupons-child")).evict(userDetails.getFamily().getId());
    }

    /**
     * 자식들의 쿠폰 조회
     * @param userDetails 사용자 정보
     * @return 자식들이 구매한 쿠폰 리스트
     */
    @Cacheable(
            value = "coupons-child",
            key = "#userDetails.family.id",
            sync=true
    )
    public List<ChildCouponResponse> getChildCoupons(CustomUserDetails userDetails) {
        return earnedCouponRepository.findChildCouponsByFamilyId(userDetails.getFamilyId());
    }
}
