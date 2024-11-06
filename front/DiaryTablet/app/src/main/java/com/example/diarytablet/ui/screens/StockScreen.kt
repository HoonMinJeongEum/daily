package com.example.diarytablet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.ui.components.StockTab
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.ShopStockViewModel

@Composable
fun StockScreen(
    navController: NavController,
    viewModel: ShopStockViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val userCoupons by viewModel.userCoupons.observeAsState(emptyList())
    val userStickers by viewModel.userStickers.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchUserCoupons()
        viewModel.fetchUserStickers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(40.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter) // 하단에 배치
        ) {
            StockTab(
                coupons = userCoupons,
                stickers = userStickers,
                modifier = Modifier
                    .align(Alignment.Center),
                viewModel = viewModel
            )
        }
    }
}
