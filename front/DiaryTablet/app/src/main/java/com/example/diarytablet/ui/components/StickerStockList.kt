package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.model.StickerStock

@Composable
fun StickerStockList(stickers: List<StickerStock>) {
    LazyVerticalGrid(columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(stickers) { sticker ->
            StickerStockCard(sticker)
        }
    }
}

@Composable
fun StickerStockCard(sticker: StickerStock) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 174.dp, height = 204.dp), // 카드 크기 설정
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Box를 사용해 카드 안의 이미지를 중앙 정렬
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center,  // 중앙 정렬 설정
        ) {
            Image(
                painter = painterResource(sticker.img), // 리소스 ID로 이미지 로딩
                contentDescription = "스티커 이미지",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}
