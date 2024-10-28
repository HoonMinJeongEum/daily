package com.example.diarytablet.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.model.Coupon

@Composable
fun CouponShopList(coupons: List<Coupon>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(coupons) { coupon ->
            CouponCard(coupon)
        }
    }
}

@Composable
fun CouponCard(coupon: Coupon) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = coupon.description, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text(text = "${coupon.price} 조개", fontSize = 16.sp, color = Color.Gray)
        }
    }
}

