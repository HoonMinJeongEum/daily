package com.example.diaryApp.domain.dto.response.coupon

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Coupon(
    @SerializedName("id")
    val id: Int,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("createdAt")
    val createdAt: LocalDateTime
)

data class CouponList(
    @SerializedName("couponList")
    val couponList: List<Coupon>
)