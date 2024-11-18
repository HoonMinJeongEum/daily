package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.EarnedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EarnedCouponRepository extends JpaRepository<EarnedCoupon, Long> {
    List<EarnedCoupon> findByMemberId(int id);

    void deleteByMemberId(int memberId);

    List<EarnedCoupon> findByMemberIdAndUsedAtIsNull(int memberId);

//    @Query("SELECT ec FROM EarnedCoupon ec " +
//            "WHERE ec.member.id = :memberId " +
//            "ORDER BY ec.usedAt NULLS FIRST, " +
//            "COALESCE(ec.usedAt, ec.coupon.purchasedAt) DESC")
//    List<EarnedCoupon> findByMemberIdWithSorting(@Param("memberId") int memberId);
}
