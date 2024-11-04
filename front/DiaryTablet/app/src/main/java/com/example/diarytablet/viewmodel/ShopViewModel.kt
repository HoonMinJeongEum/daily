package com.example.diarytablet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diarytablet.R
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.model.Sticker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
) : ViewModel() {
    // 임시 쿠폰 목록 데이터
    val coupons: MutableLiveData<List<Coupon>> = MutableLiveData(listOf(
        Coupon(1, "꿀돈 생포 갈비 같이 먹으러 가기", "100"),
        Coupon(2, "영화관에서 티니핑 같이 보러 가기", "150"),
        Coupon(3, "주말에 롯데월드 같이 놀러 가기", "200"),
        Coupon(3, "월드 통닭 먹으러 가기", "200"),
        Coupon(3, "같이 술 한잔 하기", "200"),
        Coupon(3, "대한항공 1등석 기내식 먹게 해주기 ", "200"),
    ))

    // 스티커 목록 데이터
    val stickers: MutableLiveData<List<Sticker>> = MutableLiveData(listOf(
        Sticker(1, R.drawable.sticker_1, 100),
        Sticker(2, R.drawable.sticker_2, 100),
        Sticker(3, R.drawable.sticker_3, 150),
        Sticker(3, R.drawable.sticker_4, 150),
        Sticker(3, R.drawable.sticker_5, 200),
        Sticker(3, R.drawable.sticker_6, 200),
        Sticker(3, R.drawable.sticker_7, 250),
        Sticker(3, R.drawable.sticker_8, 250)
    ))}

