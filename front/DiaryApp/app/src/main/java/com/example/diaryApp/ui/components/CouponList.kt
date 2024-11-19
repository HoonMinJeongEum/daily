package com.example.diaryApp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.viewmodel.CouponViewModel

@Composable
fun CouponListItem(
    screenHeight: Dp,
    screenWidth: Dp,
    modifier: Modifier = Modifier,
    couponList : List<Coupon>,
    onShowDialogChange: (Boolean) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(couponList) { coupon ->
            CouponItem(coupon)
        }
        item {
            Spacer(modifier = Modifier.height(screenHeight * 0.03f))
            AddCouponButton(onClick = { onShowDialogChange(true) })
            Spacer(modifier = Modifier.height(screenHeight * 0.3f))
        }
    }
}

@Composable
fun UsageCouponListItem(
    screenHeight: Dp,
    screenWidth: Dp,
    modifier: Modifier = Modifier,
    usageCouponList : List<UsageCoupon>,
    onShowBuyDialogChange: (Boolean) -> Unit
) {
    val couponViewModel: CouponViewModel = hiltViewModel()
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(usageCouponList) { usageCoupon ->
            if (usageCoupon.usedAt == null) {
                UsageCouponItem(
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    usageCoupon = usageCoupon,
                    onClick = {
                    onShowBuyDialogChange(true)
                    couponViewModel.earnedCouponId.intValue = usageCoupon.couponId
                    }
                ) } else {
                    UsageCouponItem(
                        screenWidth = screenWidth,
                        screenHeight = screenHeight,
                        usageCoupon = usageCoupon,
                        onClick = {
                            couponViewModel.earnedCouponId.intValue = usageCoupon.couponId
                        }
                    )
                }
            }
        }
    }