package com.example.diaryApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.example.diaryApp.ui.components.TabletHeader
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val footerHeight = screenWidth / 4.5f
        val textFieldHeight = screenWidth / 5f
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabletHeader(
                    pageName = "상점",
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )
            }
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxSize()
                    .padding(bottom = footerHeight)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,

                ) {
                    Row(
                        modifier = Modifier.padding(
                            top = screenWidth  * 0.03f,
                            start = screenWidth  * 0.03f,
                            end = screenWidth  * 0.03f,
                        )
                    ) {
                        DailyRegisterButton(
                            text = "쿠폰 등록",
                            backgroundColor = Color.Transparent,
                            width = (screenWidth.value * 0.42f).toInt() ,
                            height = (screenWidth.value * 0.2f).toInt(),
                            isSelected = selectedTab == "쿠폰 등록",
                            onClick = { selectedTab = "쿠폰 등록" },
                        )
                        DailyRegisterButton(
                            text = "쿠폰 내역",
                            backgroundColor = Color.Transparent,
                            width = (screenWidth.value * 0.42f).toInt() ,
                            height = (screenWidth.value * 0.2f).toInt(),
                            isSelected = selectedTab == "쿠폰 내역",
                            onClick = { selectedTab = "쿠폰 내역" },
                        )
                    }

                    when (selectedTab) {
                        "쿠폰 등록" -> {
                            CouponListItem(
                                screenWidth = screenWidth,
                                screenHeight = screenHeight,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = screenWidth  * 0.05f),
                                couponList = couponList,
                                onShowDialogChange = { showDialog = it }
                            )
                        }
                        "쿠폰 내역" -> {
                            UsageCouponListItem(
                                screenWidth = screenWidth,
                                screenHeight = screenHeight,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = screenWidth  * 0.05f),
                                usageCouponList =usageCouponList,
                                onShowBuyDialogChange = { showBuyDialog = it }
                            )
                        }
                    }
                    if (showDialog) {
                        CreateCoupon(
                            screenWidth = screenWidth,
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

