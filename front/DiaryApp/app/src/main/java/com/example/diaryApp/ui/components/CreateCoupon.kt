package com.example.diaryApp.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.Gray
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.PastelSkyBlue
import com.example.diaryApp.ui.theme.White
import com.example.diaryApp.viewmodel.CouponViewModel

@Composable
fun CreateCoupon(
    couponViewModel: CouponViewModel,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(350.dp, 400.dp)
                .background(PastelSkyBlue, RoundedCornerShape(35))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_cancel),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .offset(x = 110.dp, y = (-150).dp)
                    .clickable(onClick = { onCancel() })
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MyTextField(
                    value = couponViewModel.couponDescription.value,
                    placeholder = "소원명",
                    onValueChange = { couponViewModel.couponDescription.value = it },
                    height = 80
                )
                MyTextField(
                    value = couponViewModel.couponPrice.value.toString(),
                    placeholder = "가격",
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                            couponViewModel.couponPrice.value = it.toIntOrNull() ?: 0
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                ) {
                    DailyButton(
                        text = "쿠폰 생성",
                        fontSize = 20,
                        textColor = White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        backgroundColor = PastelNavy,
                        cornerRadius = 35,
                        width = 120,
                        height = 50,
                        onClick = {
                            couponViewModel.createCoupon(
                                onSuccess = {
                                    Log.d("CouponScreen", "Coupon Success called")
                                    onCancel()
                                },
                                onError = {
                                    Log.d("CouponScreen", "Coupon Success failed")
                                }
                            )
                            onCancel()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun BuyCoupon(
    couponViewModel: CouponViewModel,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .background(White, RoundedCornerShape(25))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "쿠폰을 사용할까요?",
                    fontSize = 25.sp,
                    color = DeepPastelNavy,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DailyButton(
                        text = "아니오",
                        fontSize = 19,
                        textColor = White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        backgroundColor = Gray,
                        cornerRadius = 35,
                        width = 85,
                        height = 60,
                        onClick = { onCancel() },
                    )

                    DailyButton(
                        text = "네",
                        fontSize = 19,
                        textColor = White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        backgroundColor = DeepPastelNavy,
                        cornerRadius = 35,
                        width = 85,
                        height = 60,
                        onClick = {
                            couponViewModel.buyCoupon(
                                onSuccess = {
                                    Log.d("CouponScreen", "Coupon Success called")
                                    onCancel()
                                },
                                onError = {
                                    Log.d("CouponScreen", "Coupon Success failed")
                                }
                            )
                            onCancel()
                        },
                    )
                }
            }
        }
    }
}