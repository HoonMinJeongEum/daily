package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByPurchasedAtIsNullAndFamilyId(int familyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findById(Long id);

    @Modifying
    @Query("""
        update Coupon c
        set c.purchasedAt = CURRENT_TIMESTAMP
        where c.id = :id
        and c.purchasedAt is null
    """)
    int buyCoupon(@Param("id") long id);
}
