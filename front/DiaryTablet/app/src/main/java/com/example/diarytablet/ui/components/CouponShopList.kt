package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.R // 배경 이미지 리소스 추가

@Composable
fun CouponShopList(coupons: List<Coupon>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // 가로 정렬을 중앙으로 설정
    ) {
        itemsIndexed(coupons) { index, coupon ->
            CouponCard(coupon, index)
        }
    }
}

@Composable
fun CouponCard(coupon: Coupon, index: Int) {
    val backgroundImage = if (index % 2 == 0) {
        R.drawable.coupon_yellow_up // 짝수 인덱스 배경 이미지
    } else {
        R.drawable.coupon_blue_up // 홀수 인덱스 배경 이미지
    }

    Card(
        modifier = Modifier
            .padding(11.dp)
            .width(950.dp)  // 고정된 너비를 이미지 크기와 맞춤
            .height(120.dp), // 고정된 높이도 이미지 크기와 맞춤

        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp) // 그림자 제거
    ) {
        // 배경 이미지와 내용물을 위한 Box
        Box(modifier = Modifier.fillMaxSize()) {
            // 배경 이미지
            Image(
                painter = painterResource(id = backgroundImage), // 인덱스에 따라 배경 이미지 변경
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            // 카드의 내용물
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 쿠폰 이미지
                Image(
                    painter = painterResource(id = R.drawable.coupon_icon), // 쿠폰 이미지 리소스 ID
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp) // 이미지 크기 조정
                        .padding(start = 20.dp, end = 20.dp) // 텍스트와의 간격 설정
                )

                // 쿠폰 설명 텍스트
                Text(
                    text = coupon.description,
                    fontSize = 30.sp, // 폰트 크기 추가로 증가
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )

                // 가격 텍스트
                Text(
                    text = "${coupon.price} 조개",
                    fontSize = 22.sp, // 폰트 크기 증가
                    color = Color.Gray
                )
            }
        }
    }
}
