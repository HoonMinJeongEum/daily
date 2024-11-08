package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.exception.CouponNotFoundException;
import com.ssafy.daily.exception.InsufficientFundsException;
import com.ssafy.daily.reward.dto.*;
import com.ssafy.daily.reward.entity.*;
import com.ssafy.daily.reward.repository.CouponRepository;
import com.ssafy.daily.reward.repository.EarnedCouponRepository;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final EarnedCouponRepository earnedCouponRepository;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final ShellService shellService;

    // 쿠폰 등록
    @Transactional
    public void addCoupon(CustomUserDetails userDetails, AddCouponRequest request) {
        // 입력값 유효성 검사
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new IllegalArgumentException("쿠폰 설명을 입력해 주세요.");  // 잘못된 요청
        }
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("쿠폰 가격은 0보다 커야 합니다.");  // 잘못된 요청
        }

        // 부모님 계정이 존재하는지 확인
        int familyId = userDetails.getFamily().getId();
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 가족 계정을 찾을 수 없습니다.", 1));

        // 쿠폰 등록
        Coupon coupon = Coupon.builder()
                .family(family)
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
        couponRepository.save(coupon);
    }

    // 쿠폰 삭제
    public void deleteCoupon(long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("해당 쿠폰을 찾을 수 없습니다."));
        if (coupon.getPurchasedAt() != null) {
            throw new AlreadyOwnedException("이미 구매한 쿠폰입니다.");
        }
        couponRepository.deleteById(couponId);
    }

    // 쿠폰 조회
    @Transactional
    public List<CouponResponse> getCoupons(CustomUserDetails userDetails) {
        int familyId =  userDetails.getFamily().getId(); ;

        // 획득하지 않은 쿠폰을 제외하고 쿠폰 조회
        List<Coupon> list = couponRepository.findByPurchasedAtIsNullAndFamilyId(familyId);

        return list.stream()
                .map(CouponResponse::new)
                .collect(Collectors.toList());
    }

    // 쿠폰 구매
    @Transactional
    public int buyCoupon(CustomUserDetails userDetails, BuyCouponRequest request) {
        // 멤버 있는지 확인
        int memberId =  userDetails.getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 구성원 계정을 찾을 수 없습니다.", 1));

        // 쿠폰 있는지 확인
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException("해당 쿠폰을 찾을 수 없습니다."));

        // 이미 구매한 쿠폰인지 확인
        if (coupon.getPurchasedAt() != null) {
            throw new AlreadyOwnedException("이미 구매한 쿠폰입니다.");
        }

        // 조개가 충분한지 확인
        int shellCount = shellService.getUserShell(memberId);
        if (shellCount < coupon.getPrice()) {
            throw new InsufficientFundsException("재화가 부족합니다.");
        }

        // 쿠폰 구매 시간 설정
        coupon.updatePurchasedAt(LocalDateTime.now());
        couponRepository.save(coupon);

        // earnedCoupon 엔티티 생성 및 저장
        EarnedCoupon earnedCoupon = EarnedCoupon.builder()
                .coupon(coupon)
                .member(member)
                .build();
        earnedCouponRepository.save(earnedCoupon);

        // Shell 로그
        shellService.saveShellLog(member, (byte) (-coupon.getPrice()), Content.COUPON);

        return shellService.getUserShell(memberId);
    }

    // 사용자가 보유한 쿠폰 조회
    @Transactional
    public List<EarnedCouponResponse> getUserCoupons(CustomUserDetails userDetails) {

        // 멤버 있는지 확인
        int memberId = userDetails.getMember().getId();

        // memberId로 EarnedCoupon 리스트 조회
        List<EarnedCoupon> list = earnedCouponRepository.findByMemberIdAndUsedAtIsNull(memberId);

        // EarnedCoupon 리스트를 EarnedCouponResponse 리스트로 변환
        return list.stream()
                .map(EarnedCouponResponse::new)
                .collect(Collectors.toList());
    }

    // 쿠폰 사용
    @Transactional
    public void useCoupon(UseCouponRequest request) {
        // 획득한 쿠폰이 존재하는지 확인
        EarnedCoupon earnedCoupon = earnedCouponRepository.findById(request.getEarnedCouponId())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰이 존재하지 않습니다."));

        if(earnedCoupon.getUsedAt() != null) {
            throw new AlreadyOwnedException("이미 사용된 쿠폰 입니다.");
        }
        // 쿠폰 사용으로 변경
        earnedCoupon.updateUsedAt(LocalDateTime.now());
        earnedCouponRepository.save(earnedCoupon);
    }

    // 자식들 쿠폰 조회
    public List<ChildCouponResponse> getChildCoupons(CustomUserDetails userDetails) {
        int familyId = userDetails.getFamily().getId();

        return memberRepository.findByFamilyId(familyId).stream()
                .flatMap(member -> earnedCouponRepository.findByMemberId(member.getId()).stream())
                .map(ChildCouponResponse::new)
                .collect(Collectors.toList());
    }
}
