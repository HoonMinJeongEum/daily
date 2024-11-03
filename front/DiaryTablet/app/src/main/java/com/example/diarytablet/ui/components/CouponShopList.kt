import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.R
import kotlinx.coroutines.delay

@Composable
fun CouponShopList(coupons: List<Coupon>) {
    LazyColumn(
        contentPadding = PaddingValues(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(coupons) { index, coupon ->
            CouponBox(coupon, index)
        }
    }
}

@Composable
fun CouponBox(coupon: Coupon, index: Int) {
    var isPressed by remember { mutableStateOf(false) }

    // 클릭 상태에 따라 배경 이미지 설정
    val backgroundImage = if (isPressed) {
        if (index % 2 == 0) R.drawable.coupon_yellow_down else R.drawable.coupon_blue_down
    } else {
        if (index % 2 == 0) R.drawable.coupon_yellow_up else R.drawable.coupon_blue_up
    }

    Box(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(0.9f)
            .aspectRatio(6.8f / 1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    // 클릭 시 상태를 `true`로 설정
                    isPressed = true
                }
            )
    ) {
        // 클릭 후 일정 시간 후에 상태를 원래대로 복귀
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100L) // 100ms 동안 `down` 상태 유지
                isPressed = false // `up` 상태로 복귀
            }
        }

        // 배경 이미지와 내용물 레이아웃
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 쿠폰 아이콘
                Image(
                    painter = painterResource(id = R.drawable.coupon_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .aspectRatio(1f)
                        .padding(start = 8.dp, end = 8.dp)
                )

                // 쿠폰 설명 텍스트
                Text(
                    text = coupon.description,
                    fontSize = 28.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(start = 10.dp)
                )

                // 가격 텍스트
                Text(
                    text = "${coupon.price} 조개",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(0.1f)
                )
            }
        }
    }
}
