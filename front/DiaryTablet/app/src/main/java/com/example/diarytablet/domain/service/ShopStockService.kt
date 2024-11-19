package com.example.diarytablet.domain.service

import com.example.diarytablet.model.Coupon
import com.example.diarytablet.model.CouponStock
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.utils.Const
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ShopStockService {
//  쿠폰 조회 (상점)
    @GET("${Const.API_PATH}coupons")
    suspend fun getCoupons(): Response<List<Coupon>>

//  스티커 조회 (상점)
    @GET("${Const.API_PATH}stickers")
    suspend fun getStickers(): Response<List<Sticker>>

//  쿠폰 구매 (상점)
    @POST("${Const.API_PATH}coupons/buy")
    suspend fun buyCoupon(@Body requestBody: RequestBody): Response<Int>

//  스티커 구매 (상점)
    @POST("${Const.API_PATH}stickers/buy")
    suspend fun buySticker(@Body requestBody: RequestBody): Response<Int>

//  쿠폰 조회 (보관함)
    @GET("${Const.API_PATH}coupons/user")
    suspend fun getUserCoupons(): Response<List<CouponStock>>

//  스티커 조회 (보관함)
    @GET("${Const.API_PATH}stickers/user")
    suspend fun getUserStickers(): Response<List<StickerStock>>

    @PATCH("${Const.API_PATH}coupons/use")
    suspend fun useCoupon(@Body requestBody: RequestBody): Response<String>
}
