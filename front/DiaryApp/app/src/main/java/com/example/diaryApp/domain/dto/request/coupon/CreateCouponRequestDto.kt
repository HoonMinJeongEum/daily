package com.example.diaryApp.domain.dto.request.coupon

import com.google.gson.annotations.SerializedName

data class CreateCouponRequest(
    @SerializedName("description")
    val description: String, // 쿠폰 설명

    @SerializedName("price")
    val price: Int // 쿠폰 가격
)
