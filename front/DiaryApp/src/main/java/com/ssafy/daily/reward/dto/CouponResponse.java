package com.ssafy.daily.reward.dto;

import com.ssafy.daily.reward.entity.Coupon;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponResponse {

    private final long id;

    private final String description;

    private final int price;

    private final LocalDateTime createdAt;

    public CouponResponse(Coupon coupon) {
        this.id = coupon.getId();
        this.description = coupon.getDescription();
        this.price = coupon.getPrice();
        this.createdAt = coupon.getCreatedAt();
    }
}
