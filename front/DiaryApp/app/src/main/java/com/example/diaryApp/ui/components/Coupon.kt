package com.example.diaryApp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.domain.dto.response.coupon.Coupon
import com.example.diaryApp.domain.dto.response.coupon.UsageCoupon
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.PastelSkyBlue

@Composable
fun CouponItem(
    coupon: Coupon
) {
    Box(modifier = Modifier
        .size(356.dp, 94.dp)
        .background(PastelSkyBlue, RoundedCornerShape(30)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = coupon.description,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Normal,
                    color = DeepPastelNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "등록시간 ${coupon.createdAt}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.shell),
                    contentDescription = "shell",
                    modifier = Modifier
                        .size(25.dp, 25.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = coupon.price,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Thin,
                    color = DeepPastelNavy
                )
            }
        }
    }
}

@Composable
fun UsageCouponItem(
    usageCoupon: UsageCoupon,
    onClick: () -> Unit
) {
    val boxColor = if (usageCoupon.usedAt != null) Color.Gray else PastelSkyBlue

    Box(
        modifier = Modifier
            .size(356.dp, 94.dp)
            .background(boxColor, RoundedCornerShape(30))
            .shadow(6.dp, RoundedCornerShape(30))
            .clickable(
                interactionSource = remember{ MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(356.dp, 94.dp)
                .background(boxColor, RoundedCornerShape(30)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = usageCoupon.description,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Normal,
                        color = DeepPastelNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (usageCoupon.usedAt != null) {
                        Text(
                            text = "사용 일시 ${usageCoupon.usedAt}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Thin,
                            color = Color.LightGray
                        )
                    } else {
                        Text(
                            text = "등록 일시 ${usageCoupon.createdAt}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Thin,
                            color = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = usageCoupon.name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Thin,
                    color = DeepPastelNavy
                )
            }
        }
    }
}