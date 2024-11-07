package com.example.diaryApp.domain.repository.coupon

import com.example.diaryApp.domain.dto.request.coupon.CreateCouponRequest
import com.example.diaryApp.domain.dto.request.coupon.UseCouponRequest
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.domain.services.CouponService
import com.example.diaryApp.utils.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CouponRepositoryImpl @Inject constructor(
    private val couponService: CouponService
) : CouponRepository {
    override suspend fun getCouponList(): MutableList<Coupon> {
        return couponService.getCouponList()
    }

    override suspend fun getUsageCouponList(): MutableList<UsageCoupon> {
        return couponService.getUsageCouponList()
    }

    override suspend fun createCoupon(createCouponRequest: CreateCouponRequest): Response<Void> {
        return couponService.createCoupon(createCouponRequest)
    }

    override suspend fun useCoupon(useCouponRequest: UseCouponRequest) : Response<String> {
        return couponService.useCoupon(useCouponRequest)
    }
}