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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.diaryApp.ui.theme.GrayDetail
import com.example.diaryApp.ui.theme.LightGray
import com.example.diaryApp.ui.theme.LightSkyBlue
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelSkyBlue
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CouponItem(
    coupon: Coupon
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val displayDate = coupon.createdAt.format(dateTimeFormatter)

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 10.dp)
        .height(100.dp)
        .background(LightSkyBlue, RoundedCornerShape(30)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(0.7f)
                    .padding(start = 20.dp)
            ) {
                Text(
                    text = coupon.description,
                    style = MyTypography.bodySmall,
                    color = DeepPastelNavy
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "등록일시 $displayDate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    color = GrayDetail
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(0.3f)
                    .padding(end = 20.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.shell),
                    contentDescription = "shell",
                    modifier = Modifier
                        .size(25.dp, 25.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = coupon.price,
                    fontSize = 24.sp,
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
    val boxColor = if (usageCoupon.usedAt != null) LightGray else LightSkyBlue
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val displayDate = if (usageCoupon.usedAt != null) {
        usageCoupon.usedAt.format(dateTimeFormatter)
    } else {
        usageCoupon.createdAt.format(dateTimeFormatter)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .height(100.dp)
            .background(boxColor, RoundedCornerShape(30))
            .shadow(6.dp, RoundedCornerShape(30))
            .clickable(
                interactionSource = remember{ MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(boxColor, RoundedCornerShape(30)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(start = 20.dp)
                ) {
                    Text(
                        text = usageCoupon.description,
                        style = MyTypography.bodySmall,
                        color = DeepPastelNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (usageCoupon.usedAt != null) "사용 일시 $displayDate" else "등록 일시 $displayDate",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Thin,
                        color = GrayDetail
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(end = 20.dp)
                ) {
                    Text(
                        text = usageCoupon.name,
                        style = MyTypography.bodySmall,
                        color = DeepPastelNavy
                    )
                }
            }
        }
    }
}