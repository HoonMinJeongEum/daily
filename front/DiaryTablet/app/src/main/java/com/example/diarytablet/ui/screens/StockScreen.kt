package com.example.diarytablet.ui.screens

import StockTab
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.StockViewModel

@Composable
fun StockScreen(
    navController: NavController,
    viewModel: StockViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val coupons by viewModel.coupons.observeAsState(emptyList())
    val stickers by viewModel.stickers.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize() // 화면 전체를 채우도록 설정
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
                coupons = coupons,
                stickers = stickers,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}
