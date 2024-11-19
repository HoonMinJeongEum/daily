package com.example.diaryApp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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

    BoxWithConstraints (modifier = Modifier
        .fillMaxWidth()

    ) {
        val screenWidth = maxWidth
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = screenWidth * 0.08f, vertical = screenWidth * 0.04f)
                .height(screenWidth * 0.23f)
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
                        .padding(start = screenWidth * 0.05f)
                ) {
                    Text(
                        text = coupon.description,
                        style = MyTypography.bodySmall.copy(
                            color = DeepPastelNavy,
                            fontSize = (screenWidth.value * 0.05f).sp
                        ),
                        color = DeepPastelNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(screenWidth * 0.03f))

                    Text(
                        text = "등록일시 $displayDate",
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Thin,
                        color = GrayDetail
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.weight(0.3f)
                        .padding(end = screenWidth * 0.04f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.shell),
                        contentDescription = "shell",
                        modifier = Modifier
                            .size(screenWidth * 0.07f)
                    )

                    Spacer(modifier = Modifier.width(screenWidth * 0.025f))

                        Text(
                            text = coupon.price,
                            fontSize = (screenWidth.value * 0.05f).sp,
                            fontWeight = FontWeight.Thin,
                            color = DeepPastelNavy
                        )

                }
            }
        }
    }
}

@Composable
fun UsageCouponItem(
    screenHeight: Dp,
    screenWidth: Dp,
    usageCoupon: UsageCoupon,
    onClick: () -> Unit
) {
    val boxColor = if (usageCoupon.usedAt != null) LightGray  else Color.Transparent
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val displayDate = if (usageCoupon.usedAt != null) {
        usageCoupon.usedAt.format(dateTimeFormatter)
    } else {
        usageCoupon.createdAt.format(dateTimeFormatter)
    }
    BoxWithConstraints (modifier = Modifier
        .fillMaxWidth()

    ) {
        val screenWidth = maxWidth
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = screenWidth * 0.08f, vertical = screenWidth * 0.04f)
                .height(screenWidth * 0.23f)
                .background(boxColor, RoundedCornerShape(30))
                .clickable(
                    interactionSource = remember{ MutableInteractionSource() },
                    indication = null
                ) { onClick() }
            ,
            contentAlignment = Alignment.Center
        ) {
            if (usageCoupon.usedAt == null) {
                Image(
                    painter = painterResource(R.drawable.coupon_container),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(start = screenWidth * 0.05f)
                ) {
                    Text(
                        text = usageCoupon.description,
                        style = MyTypography.bodySmall.copy(
                            fontSize = (screenWidth.value * 0.05f).sp
                        ),
                        color = DeepPastelNavy
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                    Text(
                        text = if (usageCoupon.usedAt != null) "사용 일시 $displayDate" else "등록 일시 $displayDate",
                        style = MyTypography.bodySmall.copy(
                            fontSize = (screenWidth.value * 0.03f).sp
                        ),
                        color = GrayDetail
                    )
                }

                Spacer(modifier = Modifier.width(screenWidth * 0.04f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(end = screenWidth * 0.05f)
                ) {
                    Text(
                        text = usageCoupon.name,
                        style = MyTypography.bodySmall.copy(
                            fontSize = (screenWidth.value * 0.05f).sp
                        ),
                        color = DeepPastelNavy
                    )
                }
            }
        }
    }
}