package com.example.diarytablet.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.model.Sticker

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickerShopList(stickers: List<Sticker>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(stickers) { sticker ->
            StickerCard(sticker)
        }
    }
}

@Composable
fun StickerCard(sticker: Sticker) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 174.dp, height = 204.dp), // 카드 크기 설정
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // 수평 중앙 정렬
            verticalArrangement = Arrangement.Center // 수직 중앙 정렬
        ) {
            Image(
                painter = painterResource(sticker.imgRes), // ID로 이미지 로딩
                contentDescription = "스티커 이미지",
                modifier = Modifier.size(100.dp) // 이미지 크기 설정
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${sticker.price} 조개",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}