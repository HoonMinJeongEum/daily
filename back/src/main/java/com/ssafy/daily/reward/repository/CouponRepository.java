package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByPurchasedAtIsNullAndFamilyId(int familyId);
}
