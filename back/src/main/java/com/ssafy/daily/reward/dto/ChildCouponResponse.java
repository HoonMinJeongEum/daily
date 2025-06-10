package com.ssafy.daily.reward.dto;

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

    public ChildCouponResponse(
            int memberId,
            long earnedCouponId,
            String name,
            String description,
            LocalDateTime usedAt,
            LocalDateTime createdAt
    ) {
        this.memberId = memberId;
        this.earnedCouponId = earnedCouponId;
        this.name = name;
        this.description = description;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }
}
