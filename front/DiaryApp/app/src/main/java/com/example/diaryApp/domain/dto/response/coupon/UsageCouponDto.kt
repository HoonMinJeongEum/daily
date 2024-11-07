package com.example.diaryApp.domain.dto.response.coupon

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UsageCoupon(
    @SerializedName("memberId")
    val memberId: Int,

    @SerializedName("earnedCouponId")
    val couponId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("usedAt")
    val usedAt: LocalDateTime?,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime
)

data class UsageCouponList(
    @SerializedName("coupons")
    val profiles: List<UsageCoupon>
)