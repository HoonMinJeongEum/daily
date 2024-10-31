package com.example.diarytablet.ui.screens

import ShopTab
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.ShopViewModel

@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val coupons by viewModel.coupons.observeAsState(emptyList())
    val stickers by viewModel.stickers.observeAsState(emptyList())

    // BoxScope 내에서 align을 사용하여 ShopTab을 화면의 중앙에 배치
    Box(
        modifier = Modifier.fillMaxSize()
            .wrapContentSize(Alignment.BottomCenter)
            .padding(bottom = 80.dp)
            .background(Color.Transparent)
    ) {
        ShopTab(
            coupons = coupons,
            stickers = stickers,
            modifier = Modifier
                .align(Alignment.Center) // 화면 가로 및 세로 중앙 정렬
//                .padding(bottom = 50.dp) // 하단에서 50.dp 위로 위치
        )
    }
}
