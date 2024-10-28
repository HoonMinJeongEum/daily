package com.example.diarytablet.ui.screens

import ShopTab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diarytablet.viewmodel.ShopViewModel

@Composable
fun ShopScreen(viewModel: ShopViewModel = viewModel()) {
    val coupons by viewModel.coupons.observeAsState(emptyList())
    val stickers by viewModel.stickers.observeAsState(emptyList())

    // LargeTab에 데이터 전달
    ShopTab(
        coupons = coupons,
        stickers = stickers
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewShopScreen() {
    ShopScreen()
}

