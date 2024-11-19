import androidx.compose.foundation.Image
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
import com.example.diarytablet.R
import com.example.diarytablet.model.CouponStock
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.viewmodel.ShopStockViewModel
import kotlinx.coroutines.delay

@Composable
fun CouponStockList(coupons: List<CouponStock>, viewModel: ShopStockViewModel) {
    if (coupons.isEmpty()) {
        // 리스트가 비어 있을 때 표시할 텍스트
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "아직 구매한 쿠폰이 없어요.",
                style = MyTypography.bodyMedium,
                color = DarkGray
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize() // 전체 화면을 채우도록 설정
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp), // 패딩 값을 0으로 설정
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // 리스트가 위쪽부터 시작되도록 설정
            ) {
                itemsIndexed(coupons.reversed()) { index, coupon ->
                    CouponStockBox(coupon, index) { couponId ->
                        viewModel.useCoupon(couponId)
                    }
                }
            }
        }
    }
}

@Composable
fun CouponStockBox(coupon: CouponStock, index: Int, onClick: (Int) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    // 배경 이미지를 클릭 상태에 따라 설정
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
    ) {
        // 클릭 후 일정 시간 후에 상태를 원래대로 복귀
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100L) // 100밀리초 동안 `down` 상태 유지
                isPressed = false // `up` 상태로 복귀
            }
        }

        // 배경 이미지 설정
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
            // 설명 텍스트
            Text(
                text = coupon.description,
                fontSize = 28.sp,
                color = DarkGray,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 10.dp)
            )
        }
    }
}
