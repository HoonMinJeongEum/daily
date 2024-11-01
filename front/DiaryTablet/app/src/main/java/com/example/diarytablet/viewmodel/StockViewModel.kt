package com.example.diarytablet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diarytablet.R
import com.example.diarytablet.model.CouponStock
import com.example.diarytablet.model.StickerStock
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
) : ViewModel() {
    // 임시 쿠폰 목록 데이터
    val coupons: MutableLiveData<List<CouponStock>> = MutableLiveData(listOf(
        CouponStock(1, "꿀돈 생포 갈비 같이 먹으러 가기", "2024-10-25"),
        CouponStock(2, "영화관에서 티니핑 같이 보러 가기", "2024-10-25"),
        CouponStock(3, "주말에 롯데월드 같이 놀러 가기", "2024-10-25"),
        CouponStock(3, "월드 통닭 먹으러 가기", "2024-10-25"),
        CouponStock(3, "같이 술 한잔 하기", "2024-10-25"),
        CouponStock(3, "대한항공 1등석 기내식 먹게 해주기 ", "2024-10-25"),
    ))

    // 스티커 목록 데이터
    val stickers: MutableLiveData<List<StickerStock>> = MutableLiveData(listOf(
        StickerStock(1, R.drawable.sticker_1),
        StickerStock(2, R.drawable.sticker_2),
        StickerStock(3, R.drawable.sticker_3),
        StickerStock(5, R.drawable.sticker_5),
        StickerStock(6, R.drawable.sticker_6),
        StickerStock(7, R.drawable.sticker_7),
        StickerStock(8, R.drawable.sticker_8)
    ))}

