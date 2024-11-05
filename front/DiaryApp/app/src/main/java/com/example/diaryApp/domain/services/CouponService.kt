package com.example.diaryApp.domain.services

import com.example.diaryApp.domain.dto.request.coupon.CreateCouponRequest
import com.example.diaryApp.domain.dto.request.coupon.UseCouponRequest
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.utils.Const
import com.example.diaryApp.utils.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface CouponService {

    @GET("${Const.API_PATH}coupons")
    suspend fun getCouponList() : MutableList<Coupon>

    @GET("${Const.API_PATH}coupons/child")
    suspend fun getUsageCouponList() : MutableList<UsageCoupon>

    @POST("${Const.API_PATH}coupons")
    suspend fun createCoupon(@Body createCouponRequest: CreateCouponRequest) : Response<Void>

    @PATCH("${Const.API_PATH}coupons/use")
    suspend fun useCoupon(@Body useCouponRequest: UseCouponRequest) : Response<String>
}