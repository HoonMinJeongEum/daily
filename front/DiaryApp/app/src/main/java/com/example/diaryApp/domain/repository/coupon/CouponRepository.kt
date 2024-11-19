package com.example.diaryApp.domain.repository.coupon

import com.example.diaryApp.domain.dto.request.coupon.CreateCouponRequest
import com.example.diaryApp.domain.dto.request.coupon.UseCouponRequest
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.utils.Response

interface CouponRepository {
    suspend fun getCouponList(): MutableList<Coupon>
    suspend fun getUsageCouponList(): MutableList<UsageCoupon>
    suspend fun createCoupon(createCouponRequest: CreateCouponRequest) : Response<Void>
    suspend fun useCoupon(useCouponRequest: UseCouponRequest) : Response<String>
}