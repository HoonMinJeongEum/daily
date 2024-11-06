package com.example.diarytablet.domain.repository

import com.example.diarytablet.domain.service.ShopStockService
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.model.CouponStock
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.model.StickerStock
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopStockRepository @Inject constructor(
    private val shopStockService: ShopStockService
) {
    suspend fun getCoupons(): Response<List<Coupon>> {
        return shopStockService.getCoupons()
    }
    suspend fun getStickers(): Response<List<Sticker>> {
        return shopStockService.getStickers()
    }
    suspend fun buyCoupon(requestBody: RequestBody): Response<Int> {
        return shopStockService.buyCoupon(requestBody)
    }
    suspend fun buySticker(requestBody: RequestBody): Response<Int> {
        return shopStockService.buySticker(requestBody)
    }
    suspend fun getUserCoupons(): Response<List<CouponStock>> {
        return shopStockService.getUserCoupons()
    }
    suspend fun getUserStickers(): Response<List<StickerStock>> {
        return shopStockService.getUserStickers()
    }
    suspend fun useCoupon(requestBody: RequestBody): Response<String> {
        return shopStockService.useCoupon(requestBody)
    }
}