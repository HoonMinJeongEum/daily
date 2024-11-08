package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.EarnedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EarnedCouponRepository extends JpaRepository<EarnedCoupon, Long> {
    List<EarnedCoupon> findByMemberId(int id);

    void deleteByMemberId(int memberId);

    List<EarnedCoupon> findByMemberIdAndUsedAtIsNull(int memberId);
}
