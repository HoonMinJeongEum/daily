package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.EarnedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarnedCouponRepository extends JpaRepository<EarnedCoupon, Long> {
}
