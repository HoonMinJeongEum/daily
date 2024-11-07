package com.example.diaryApp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaryApp.domain.dto.request.coupon.CreateCouponRequest
import com.example.diaryApp.domain.dto.request.coupon.UseCouponRequest
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.domain.repository.coupon.CouponRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CouponViewModel @Inject constructor(
    application: Application,
    private val couponRepository: CouponRepository
) : AndroidViewModel(application) {

    private val _couponList = mutableStateOf<List<Coupon>>(emptyList())
    private val _usageCouponList = mutableStateOf<List<UsageCoupon>>(emptyList())
    val couponList: State<List<Coupon>> get() = _couponList
    val usageCouponList : State<List<UsageCoupon>> get() = _usageCouponList

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    val couponDescription = mutableStateOf<String>("")
    val couponPrice = mutableIntStateOf(0)
    var earnedCouponId = mutableIntStateOf(0)

    init {
        loadCoupons()
        loadUsageCoupon()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val coupons = couponRepository.getCouponList()
                _couponList.value = coupons
                Log.d("CouponViewModel", "Coupon list updated: ${_couponList.value}")
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("CouponViewModel", "Error loading coupons: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun loadUsageCoupon() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val usageCoupons = couponRepository.getUsageCouponList()
                _usageCouponList.value = usageCoupons
                Log.d("CouponViewModel", "Coupon list updated: ${_couponList.value}")
                errorMessage.value = null
            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("CouponViewModel", "Error loading coupons: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createCoupon(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                val createCouponRequest = CreateCouponRequest(
                    description = couponDescription.value,
                    price = couponPrice.intValue
                )

                couponRepository.createCoupon(createCouponRequest)
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = e.message
                Log.e("CouponViewModel", "Error adding profile: ${e.message}")
                onError()
            } finally {
                isLoading.value = false
                loadCoupons()
            }
        }
    }

    fun buyCoupon(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val buyCouponRequest = UseCouponRequest(earnedCouponId = earnedCouponId.intValue)
                couponRepository.useCoupon(buyCouponRequest)
                onSuccess()
            } catch (e:Exception) {
                errorMessage.value = e.message
                Log.e("CouponViewModel", "Error adding profile: ${e.message}")
                onError()
            } finally {
                isLoading.value = false
                loadUsageCoupon()
            }
        }
    }
}