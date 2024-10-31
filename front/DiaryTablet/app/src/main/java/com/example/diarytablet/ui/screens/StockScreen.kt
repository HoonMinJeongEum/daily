package com.example.diarytablet.ui.screens

import StockTab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.MainViewModel
import com.example.diarytablet.viewmodel.StockViewModel

@Composable
fun StockScreen(navController: NavController,
                viewModel: StockViewModel = hiltViewModel(),
                backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    val coupons by viewModel.coupons.observeAsState(emptyList())
    val stickers by viewModel.stickers.observeAsState(emptyList())

    StockTab(
        coupons = coupons,
        stickers = stickers
    )
}

