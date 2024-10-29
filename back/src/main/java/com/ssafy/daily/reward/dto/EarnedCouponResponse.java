package com.ssafy.daily.reward.dto;

import com.ssafy.daily.reward.entity.EarnedCoupon;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EarnedCouponResponse {

    private final Long id;
    private final String description;
    private final LocalDateTime used_at;
    private final LocalDateTime created_at;


    public EarnedCouponResponse(EarnedCoupon earnedCoupon) {
        this.id = earnedCoupon.getId();
        this.description = earnedCoupon.getCoupon().getDescription();
        this.used_at = earnedCoupon.getUsedAt();
        this.created_at = earnedCoupon.getCoupon().getPurchasedAt();
    }
}
