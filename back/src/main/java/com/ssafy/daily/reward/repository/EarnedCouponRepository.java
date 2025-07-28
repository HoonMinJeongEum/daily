package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.dto.ChildCouponResponse;
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

    @Query("""
        SELECT ec FROM EarnedCoupon  ec
        JOIN FETCH ec.coupon
        WHERE ec.member.id = :memberId AND ec.usedAt IS NULL
    """)
    List<EarnedCoupon> findByMemberIdAndUsedAtIsNull(@Param("memberId") int memberId);

    @Query("""
        SELECT new com.ssafy.daily.reward.dto.ChildCouponResponse(
            m.id, ec.id, m.name, c.description, ec.usedAt, c.purchasedAt
        )
        FROM EarnedCoupon ec
        JOIN ec.member m
        JOIN ec.coupon c
        WHERE m.family.id = :familyId
        ORDER BY
             ec.usedAt, c.purchasedAt DESC
    """)
    List<ChildCouponResponse> findChildCouponsByFamilyId(@Param("familyId") int familyId);

}
