package com.example.diaryApp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.viewmodel.CouponViewModel

@Composable
fun CouponListItem(
    modifier: Modifier = Modifier,
    couponList : List<Coupon>,
    onShowDialogChange: (Boolean) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        items(couponList) { coupon ->
            CouponItem(coupon)
        }

        item {
            AddCouponButton(onClick = { onShowDialogChange(true) })
            Spacer(modifier = Modifier.height(160.dp))
        }
    }
}

@Composable
fun UsageCouponListItem(
    modifier: Modifier = Modifier,
    usageCouponList : List<UsageCoupon>,
    onShowBuyDialogChange: (Boolean) -> Unit
) {
    val couponViewModel: CouponViewModel = hiltViewModel()

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        items(usageCouponList) { usageCoupon ->
            if (usageCoupon.usedAt == null) {
                UsageCouponItem(
                    usageCoupon,
                    onClick = {
                    onShowBuyDialogChange(true)
                    couponViewModel.earnedCouponId.intValue = usageCoupon.couponId
                    }
                ) } else {
                    UsageCouponItem(
                        usageCoupon,
                        onClick = {
                            couponViewModel.earnedCouponId.intValue = usageCoupon.couponId
                        }
                    )
                }
            }
        }
    }