package com.ssafy.daily.reward.dto;

import com.ssafy.daily.reward.entity.Coupon;
import lombok.Data;

@Data
public class CouponResponse {

    private final long id;

    private final String description;

    private final int price;

    public CouponResponse(Coupon coupon) {
        this.id = coupon.getId();
        this.description = coupon.getDescription();
        this.price = coupon.getPrice();
    }
}
