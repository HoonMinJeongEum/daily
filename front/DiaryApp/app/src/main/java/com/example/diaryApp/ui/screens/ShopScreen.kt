package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.BuyCoupon
import com.example.diaryApp.ui.components.CouponListItem
import com.example.diaryApp.ui.components.CreateCoupon
import com.example.diaryApp.ui.components.DailyRegisterButton
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TopBackImage
import com.example.diaryApp.ui.components.TopLogoImg
import com.example.diaryApp.ui.components.UsageCouponListItem
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.viewmodel.CouponViewModel

@Composable
fun ShoppingScreen(
    navController: NavController,
    couponViewModel: CouponViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.NORMAL
) {

    BackgroundPlacement(backgroundType = backgroundType)

    var showDialog by remember { mutableStateOf(false) }
    var showBuyDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("쿠폰 등록") }
    val couponList by couponViewModel.couponList
    val usageCouponList by couponViewModel.usageCouponList

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopLogoImg(
                    characterImg = R.drawable.daily_character,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "상점",
                    color = Color.White,
                    style = MyTypography.bodyMedium,
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 40.dp)
                )
            }
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxWidth()
                    .height(780.dp)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,

                ) {
                    Row(
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                    ) {
                        DailyRegisterButton(
                            text = "쿠폰 등록",
                            backgroundColor = Color.Transparent,
                            width = 140,
                            height = 60,
                            isSelected = selectedTab == "쿠폰 등록",
                            onClick = { selectedTab = "쿠폰 등록" },
                        )
                        DailyRegisterButton(
                            text = "쿠폰 내역",
                            backgroundColor = Color.Transparent,
                            width = 140,
                            height = 60,
                            isSelected = selectedTab == "쿠폰 내역",
                            onClick = { selectedTab = "쿠폰 내역" },
                        )
                    }

                    when (selectedTab) {
                        "쿠폰 등록" -> {
                            CouponListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                couponList,
                                onShowDialogChange = { showDialog = it }
                            )
                        }
                        "쿠폰 내역" -> {
                            UsageCouponListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                usageCouponList,
                                onShowBuyDialogChange = { showBuyDialog = it }
                            )
                        }
                    }
                    if (showDialog) {
                        CreateCoupon(
                            couponViewModel = couponViewModel,
                            onCancel = { showDialog = false }
                        )
                    }
                    if (showBuyDialog) {
                        BuyCoupon(
                            couponViewModel = couponViewModel,
                            onCancel = { showBuyDialog = false }
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavMenu(navController, "shop", "shop")
        }
    }
}

