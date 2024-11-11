import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.myFontFamily
import com.example.diarytablet.viewmodel.ShopStockViewModel
import kotlinx.coroutines.delay

@Composable
fun CouponShopList(coupons: List<Coupon>, viewModel: ShopStockViewModel) {
    var selectedCoupon by remember { mutableStateOf<Coupon?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(coupons) { index, coupon ->
            CouponBox(coupon, index) {
                selectedCoupon = coupon
                showDialog = true
            }
        }
    }

    if (showDialog && selectedCoupon != null) {
        PurchaseConfirmationDialog(
            coupon = selectedCoupon!!,
            onConfirm = {
                viewModel.buyCoupon(selectedCoupon!!.id)
                showDialog = false
            },
            onCancel = {
                showDialog = false
            }
        )
    }
}

@Composable
fun CouponBox(coupon: Coupon, index: Int, onClick: (Coupon) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

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
                    isPressed = true
                    onClick(coupon) // Coupon 객체를 그대로 전달
                }
            )
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100L)
                isPressed = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp, end = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coupon_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .aspectRatio(1f)
                        .padding(start = 8.dp, end = 8.dp)
                )

                Text(
                    text = coupon.description,
                    fontSize = 28.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(start = 10.dp)
                )

                // 버튼을 추가하여 조개 아이콘과 가격 표시
                Button(
                    onClick = { onClick(coupon) }, // 구매 이벤트 전달
                    modifier = Modifier
                        .weight(0.15f)
                        .height(64.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp), // 둥근 테두리 설정
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelNavy)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jogae), // 조개 아이콘
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${coupon.price}",
                        fontSize = 28.sp,
                        fontFamily = myFontFamily,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseConfirmationDialog(
    coupon: Coupon,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)) // 모서리를 둥글게 설정
                .background(Color.White)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${coupon.description} 쿠폰을 구매할까요?",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 구매 버튼
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp), // 둥근 테두리 설정
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        // 버튼 안에 조개 이미지와 가격 텍스트 배치
                        Image(
                            painter = painterResource(id = R.drawable.jogae),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${coupon.price} 조개",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 취소 버튼
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp) // 둥근 테두리 설정
                    ) {
                        Text("취소", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}



