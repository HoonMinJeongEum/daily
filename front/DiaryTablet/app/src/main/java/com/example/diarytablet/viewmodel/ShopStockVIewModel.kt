package com.example.diarytablet.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.repository.ShopStockRepository
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.model.CouponStock
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.model.StickerStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class ShopStockViewModel @Inject constructor(
    private val userStore: UserStore,
    private val shopStockRepository: ShopStockRepository
) : ViewModel() {

    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _stickers = MutableLiveData<List<Sticker>>()
    val stickers: LiveData<List<Sticker>> get() = _stickers

    private val _userCoupons = MutableLiveData<List<CouponStock>>()
    val userCoupons: LiveData<List<CouponStock>> get() = _userCoupons

    private val _userStickers = MutableLiveData<List<StickerStock>>()
    val userStickers: LiveData<List<StickerStock>> get() = _userStickers

    private val _remainingShells = MutableLiveData<Int>()
    val remainingShells: LiveData<Int> get() = _remainingShells

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _couponUseMessage = MutableLiveData<String>()
    val couponUseMessage: LiveData<String> get() = _couponUseMessage

    // 쿠폰 목록 가져오기
    fun fetchCoupons() {
        viewModelScope.launch {
            try {
                val response = shopStockRepository.getCoupons()
                if (response.isSuccessful) {
                    _coupons.postValue(response.body())
                } else {
                    _errorMessage.postValue("쿠폰 목록을 불러오는데 실패했습니다.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }

    // 스티커 목록 가져오기
    fun fetchStickers() {
        viewModelScope.launch {
            try {
                val response = shopStockRepository.getStickers()
                if (response.isSuccessful) {
                    _stickers.postValue(response.body())
                } else {
                    _errorMessage.postValue("스티커 목록을 불러오는데 실패했습니다.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }

    // 쿠폰 구매
    fun buyCoupon(couponId: Int) {
        viewModelScope.launch {
            val json = """{"couponId": $couponId}"""
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            try {
                val response = shopStockRepository.buyCoupon(requestBody)
                if (response.isSuccessful) {
                    _remainingShells.postValue(response.body())
                    fetchCoupons()
                    Log.d("ShopStockViewModel", "remainingShells: ${response.body()}")
                } else {
                    _errorMessage.postValue("쿠폰 구매에 실패했습니다.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }
    // 스티커 구매
    fun buySticker(stickerId: Int) {
        viewModelScope.launch {
            val json = """{"stickerId": $stickerId}"""
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            try {
                val response = shopStockRepository.buySticker(requestBody)
                if (response.isSuccessful) {
                    _remainingShells.postValue(response.body())
                    fetchStickers()
                } else {
                    _errorMessage.postValue("스티커 구매에 실패했습니다.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }

    // 사용자 보유 쿠폰 목록 가져오기
    fun fetchUserCoupons() {
        viewModelScope.launch {
            try {
                val response = shopStockRepository.getUserCoupons()
                if (response.isSuccessful) {
                    _userCoupons.postValue(response.body())
                } else {
                    _errorMessage.postValue("사용자 쿠폰 목록을 불러오는데 실패했습니다.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }

    // 사용자 보유 스티커 목록 가져오기
    // 스티커 목록 가져오기
    fun fetchUserStickers() {
        viewModelScope.launch {
            try {
                val response = shopStockRepository.getUserStickers()
                if (response.isSuccessful) {
                    _userStickers.postValue(response.body())
                } else {
                    _errorMessage.postValue("스티커 목록을 불러오는데 실패했습니다.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }
    // 보관함 쿠폰 사용
    fun useCoupon(earnedCouponId: Int) {
        viewModelScope.launch {
            val json = """{"earnedCouponId": $earnedCouponId}"""
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            try {
                val response = shopStockRepository.useCoupon(requestBody)
                if (response.isSuccessful) {
                    _couponUseMessage.postValue(response.body() ?: "쿠폰이 정상적으로 사용되었습니다.")
                } else {
                    _couponUseMessage.postValue("쿠폰 사용에 실패했습니다.")
                }
            } catch (e: Exception) {
                _couponUseMessage.postValue("오류 발생: ${e.localizedMessage}")
            }
        }
    }
}