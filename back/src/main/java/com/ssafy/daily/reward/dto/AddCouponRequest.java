package com.ssafy.daily.reward.dto;

import lombok.Data;

@Data
public class AddCouponRequest {
    private String description;
    private int price;
}
