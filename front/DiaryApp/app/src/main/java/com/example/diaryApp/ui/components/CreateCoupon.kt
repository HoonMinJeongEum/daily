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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.Gray
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.PastelSkyBlue
import com.example.diaryApp.ui.theme.White
import com.example.diaryApp.viewmodel.CouponViewModel

@Composable
fun CreateCoupon(
    couponViewModel: CouponViewModel,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "쿠폰 생성",
                    color = Color(0xFF5A72A0),
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MyTypography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MyTextField(
                        value = couponViewModel.couponDescription.value,
                        placeholder = "소원명",
                        onValueChange = { couponViewModel.couponDescription.value = it },
                        width = 280,
                        height = 50
                    )
                    MyTextField(
                        value = if (couponViewModel.couponPrice.value == 0) "" else couponViewModel.couponPrice.value.toString(),
                        placeholder = "가격",
                        width = 280,
                        height = 50,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                couponViewModel.couponPrice.value = it.toIntOrNull() ?: 0
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        DailyButton(
                            text = "쿠폰 생성",
                            fontSize = 20,
                            textColor = White,
                            fontWeight = FontWeight.Bold,
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
                                        Log.d("CouponScreen", "Coupon creation failed")
                                    }
                                )
                                onCancel()
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Composable
fun BuyCoupon(
    couponViewModel: CouponViewModel,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp,
                        bottom = 24.dp,
                        start = 18.dp,
                        end = 18.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "쿠폰을 사용할까요?",
                    color = DeepPastelNavy,
                    style = MyTypography.bodySmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DailyButton(
                        text = "아니오",
                        fontSize = 18,
                        textColor = White,
                        fontWeight = FontWeight.SemiBold,
                        backgroundColor = Gray,
                        cornerRadius = 35,
                        width = 80,
                        height = 50,
                        onClick = { onCancel() },
                    )

                    DailyButton(
                        text = "네",
                        fontSize = 18,
                        textColor = White,
                        fontWeight = FontWeight.SemiBold,
                        backgroundColor = DeepPastelNavy,
                        cornerRadius = 35,
                        width = 80,
                        height = 50,
                        onClick = {
                            couponViewModel.buyCoupon(
                                onSuccess = {
                                    Log.d("CouponScreen", "Coupon Success called")
                                    onCancel()
                                },
                                onError = {
                                    Log.d("CouponScreen", "Coupon creation failed")
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
