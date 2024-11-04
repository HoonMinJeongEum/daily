package com.ssafy.daily.reward.dto;

import com.ssafy.daily.reward.entity.EarnedCoupon;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChildCouponResponse {
    private final int memberId;
    private final long earnedCouponId;
    private final String name;
    private final String description;
    private final LocalDateTime usedAt;
    private final LocalDateTime createdAt;

    public ChildCouponResponse(EarnedCoupon earnedCoupon) {
        this.memberId = earnedCoupon.getMember().getId();
        this.earnedCouponId = earnedCoupon.getId();
        this.name = earnedCoupon.getMember().getName();
        this.description = earnedCoupon.getCoupon().getDescription();
        this.usedAt = earnedCoupon.getUsedAt();
        this.createdAt = earnedCoupon.getCoupon().getPurchasedAt();
    }
}
