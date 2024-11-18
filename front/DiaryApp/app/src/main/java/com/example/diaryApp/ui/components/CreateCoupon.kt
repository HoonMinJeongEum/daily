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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.example.diaryApp.utils.clearFocusOnClick
import com.example.diaryApp.viewmodel.CouponViewModel

@Composable
fun CreateCoupon(
    screenWidth: Dp,
    couponViewModel: CouponViewModel,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var showAlertDescription by remember { mutableStateOf(false) } // 소원명 경고 상태
    var showAlertPrice by remember { mutableStateOf(false) } // 가격 경고 상태
    val focusManager = LocalFocusManager.current
    val WarningColor = Color(0xFFF44336)
    val descriptionFocusRequester = remember { FocusRequester() }
    val priceFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        showAlertDescription = false
        showAlertPrice = false
        couponViewModel.couponDescription.value = ""
        couponViewModel.couponPrice.value = 0
    }

    Dialog(onDismissRequest = onCancel) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
                .clearFocusOnClick()

        ) {

            Surface(
                shape = RoundedCornerShape(screenWidth * 0.08f),
                color = Color.White,
                modifier = Modifier
                    .padding(screenWidth * 0.1f)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = screenWidth * 0.05f, vertical = screenWidth * 0.03f)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier.size(screenWidth * 0.08f)
                            )
                        }
                    }

                    Text(
                        text = "쿠폰 생성",
                        color = Color(0xFF5A72A0),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MyTypography.bodyMedium.copy(
                            color = DeepPastelNavy,
                            fontSize = (screenWidth * 0.07f).value.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(screenWidth * 0.05f))

                    Text(
                        text = "소원명",
                        style = MyTypography.bodySmall.copy(
                            fontSize = (screenWidth.value * 0.04f).sp,
                            color = DeepPastelNavy
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = screenWidth * 0.02f, bottom = screenWidth * 0.02f),


                    )
                    Spacer(modifier = Modifier.height(screenWidth * 0.01f))


                    if (showAlertDescription) {
                        Text(
                            fontSize = (screenWidth.value * 0.03f).sp,
                            fontWeight = FontWeight.Thin,
                            text = "소원명은 최대 12글자까지 입력할 수 있습니다.",
                            color = WarningColor,
                        )
                    } else {
                        Spacer(modifier = Modifier.height(screenWidth * 0.036f)) // 빈 공간으로 높이 확보
                    }
                    Spacer(modifier = Modifier.height(screenWidth * 0.01f))
                    MyTextField(
                        value = couponViewModel.couponDescription.value,
                        placeholder = "소원명",
                        onValueChange = {
                            if (it.length <= 12) {
                                couponViewModel.couponDescription.value = it
                            } else {
                                showAlertDescription = true
                            }
                        },
                        width = screenWidth,
                        height = screenWidth * 1.9f,
                        imeAction = ImeAction.Next,
                        onImeAction = {
                            if (couponViewModel.couponDescription.value.isNotBlank()) {
                                priceFocusRequester.requestFocus()
                            }
                        },
                        focusRequester = descriptionFocusRequester

                    )
                    Spacer(modifier = Modifier.height(screenWidth * 0.05f))

                    Text(
                        text = "가격",
                        style = MyTypography.bodySmall.copy(
                            fontSize = (screenWidth.value * 0.04f).sp,
                            color = DeepPastelNavy
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = screenWidth * 0.02f, bottom = screenWidth * 0.02f)
                    )
                    Spacer(modifier = Modifier.height(screenWidth * 0.01f))

                    if (showAlertPrice) {
                        Text(
                            fontSize = (screenWidth.value * 0.03f).sp,
                            fontWeight = FontWeight.Thin,
                            text = "가격은 최대 세 자리까지만 입력할 수 있습니다.",
                            color = WarningColor,
                        )

                    } else {
                        Spacer(modifier = Modifier.height(screenWidth * 0.036f)) // 빈 공간으로 높이 확보
                    }


                    Spacer(modifier = Modifier.height(screenWidth * 0.01f))


                    MyTextField(
                        value = if (couponViewModel.couponPrice.value == 0) "" else couponViewModel.couponPrice.value.toString(),
                        placeholder = "가격",
                        width = screenWidth,
                        height = screenWidth * 1.9f,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 3) {
                                couponViewModel.couponPrice.value = it.toIntOrNull() ?: 0
                            } else if (it.length > 3) {
                                showAlertPrice = true
                            }
                        },
                        focusRequester = priceFocusRequester,
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            val isDescriptionEmpty =
                                couponViewModel.couponDescription.value.isBlank()
                            val isPriceEmpty = couponViewModel.couponPrice.value == 0

                            if (isDescriptionEmpty || isPriceEmpty) {
                                showAlertDescription = isDescriptionEmpty
                                showAlertPrice = isPriceEmpty
                            } else {
                                showAlertDescription = false
                                showAlertPrice = false
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
                            }
                        }
                    )



                    Spacer(modifier = Modifier.height(screenWidth * 0.06f))

                    DailyButton(
                        text = "쿠폰 생성",
                        fontSize = (screenWidth * 0.05f).value.toInt(),
                        textColor = White,
                        cornerRadius = 16,
                        fontWeight = FontWeight.Bold,
                        backgroundColor = if (
                            couponViewModel.couponDescription.value.isBlank() || couponViewModel.couponPrice.value == 0
                            ) Gray else PastelNavy,
                        width = (screenWidth * 0.5f).value.toInt(),
                        height = (screenWidth * 0.13f).value.toInt(),
                        onClick = {
                            // 소원명과 가격 모두 입력 여부 확인
                            val isDescriptionEmpty =
                                couponViewModel.couponDescription.value.isBlank()
                            val isPriceEmpty = couponViewModel.couponPrice.value == 0

                            if (isDescriptionEmpty || isPriceEmpty) {
                                showAlertDescription = isDescriptionEmpty
                                showAlertPrice = isPriceEmpty
                            } else {
                                showAlertDescription = false
                                showAlertPrice = false
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
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(screenWidth * 0.04f))
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
    Dialog(onDismissRequest = onCancel) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 36.dp,
                            bottom = 24.dp,
                            start = 18.dp,
                            end = 18.dp
                        )
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
                            text = "취소",
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
                            text = "사용",
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
}
