package com.example.diarytablet.ui.screens

import StockTab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diarytablet.viewmodel.StockViewModel

@Composable
fun StockScreen(viewModel: StockViewModel = viewModel()) {
    val coupons by viewModel.coupons.observeAsState(emptyList())
    val stickers by viewModel.stickers.observeAsState(emptyList())

    StockTab(
        coupons = coupons,
        stickers = stickers
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStockScreen() {
    StockScreen()
}

