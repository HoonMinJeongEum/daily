package com.example.diaryApp.domain.dto.request.coupon

import com.google.gson.annotations.SerializedName

data class UseCouponRequest(
    @SerializedName("earnedCouponId")
    val earnedCouponId: Int
)
