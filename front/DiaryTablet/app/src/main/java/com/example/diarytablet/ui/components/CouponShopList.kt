import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.modal.CommonModal
import com.example.diarytablet.ui.components.modal.CommonPopup
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.ui.theme.myFontFamily
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.NavBarViewModel
import com.example.diarytablet.viewmodel.ShopStockViewModel
import kotlinx.coroutines.delay

enum class CouponModalState {
    NONE,
    PURCHASE_CONFIRMATION,
    INSUFFICIENT_SHELLS,
    PURCHASE_SUCCESS
}

@Composable
fun CouponShopList(
    coupons: List<Coupon>,
    viewModel: ShopStockViewModel,
    navBarViewModel: NavBarViewModel
) {
    var selectedCoupon by remember { mutableStateOf<Coupon?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var couponModalState by remember { mutableStateOf(CouponModalState.NONE) }

    val shellCount by navBarViewModel.shellCount
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        navBarViewModel.initializeData()
    }
    if (coupons.isEmpty()) {
        // 리스트가 비어 있을 때 표시할 텍스트
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "등록된 쿠폰이 없어요.",
                style = MyTypography.bodyMedium,
                color = DarkGray
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                itemsIndexed(coupons) { index, coupon ->
                    CouponBox(coupon, index) {
                        selectedCoupon = coupon
                        couponModalState = CouponModalState.PURCHASE_CONFIRMATION
                        showDialog = true
                    }
                }
            }
        }

        if (showDialog && selectedCoupon != null) {
            when (couponModalState) {
                CouponModalState.PURCHASE_CONFIRMATION -> {
                    val titleText = "${selectedCoupon!!.description}\n쿠폰을 구매할까요?"
                    val confirmText = "${selectedCoupon!!.price}"
                    val confirmIconResId = R.drawable.jogae

                    CommonModal(
                        onDismissRequest = {
                            showDialog = false
                            couponModalState = CouponModalState.NONE
                        },
                        titleText = titleText,
                        confirmText = confirmText,
                        confirmIconResId = confirmIconResId,
                        onConfirm = {
                            if (shellCount >= selectedCoupon!!.price) {
                                viewModel.buyCoupon(selectedCoupon!!.id)
                                couponModalState = CouponModalState.PURCHASE_SUCCESS
                            } else {
                                couponModalState = CouponModalState.INSUFFICIENT_SHELLS
                            }
                        }
                    )
                }

                CouponModalState.INSUFFICIENT_SHELLS -> {
                    // 소리 재생
                    LaunchedEffect(couponModalState) {
                        playButtonSound(context,R.raw.warning)
                    }

                    CommonPopup(
                        onDismissRequest = {
                            showDialog = false
                            couponModalState = CouponModalState.NONE
                        },
                        titleText = "조개를 조금 더 모아보아요!"
                    )
                }

                CouponModalState.PURCHASE_SUCCESS -> {
                    LaunchedEffect(couponModalState) {
                        playButtonSound(context,R.raw.main_clear)
                    }

                    CommonPopup(
                        onDismissRequest = {
                            showDialog = false
                            couponModalState = CouponModalState.NONE
                        },
                        titleText = "구매가 완료되었습니다!"
                    )
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun CouponBox(coupon: Coupon, index: Int, onClick: (Coupon) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                    playButtonSound(context, R.raw.shop_buy )
                    onClick(coupon)
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
                    color = DarkGray,
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(start = 10.dp)
                )

                Button(
                    onClick = {
                        playButtonSound(context, R.raw.shop_buy )
                        onClick(coupon)
                              },
                    modifier = Modifier
                        .weight(0.15f)
                        .height(64.dp),
                    shape = RoundedCornerShape(50.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelNavy)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jogae),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${coupon.price}",
                        fontSize = 28.sp,
                        fontFamily = myFontFamily,
                        color = Color.White,
                        modifier = Modifier.offset(y = (2).dp)
                    )
                }
            }
        }
    }
}
