import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diarytablet.model.CouponStock
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.ui.components.CouponStockList
import com.example.diarytablet.ui.components.StickerStockList

@Composable
fun StockTab(
    coupons: List<CouponStock>,
    stickers: List<StickerStock>
) {
    var selectedTab by remember { mutableStateOf("Coupon") }

    Row(
        modifier = Modifier
            .size(1160.dp, 570.dp)
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        // 좌측 세로 탭
        Column(
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 쿠폰 탭
            TextButton(
                onClick = { selectedTab = "Coupon" },
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(
//                        if (selectedTab == "Coupon") Color(0xFFDCEEFF) else Color.Transparent,
//                        shape = RoundedCornerShape(8.dp)
//                    )
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedTab == "Coupon") {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color.Blue) // 파란색 강조선
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "쿠폰", color = if (selectedTab == "Coupon") Color.Blue else Color.Gray)
                }
            }

            // 스티커 탭
            TextButton(
                onClick = { selectedTab = "Sticker" },
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(
//                        if (selectedTab == "Sticker") Color(0xFFDCEEFF) else Color.Transparent,
//                        shape = RoundedCornerShape(8.dp)
//                    )
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedTab == "Sticker") {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color.Blue) // 파란색 강조선
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "스티커", color = if (selectedTab == "Sticker") Color.Blue else Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 우측 콘텐츠 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            if (selectedTab == "Coupon") {
                CouponStockList(coupons)
            } else {
                StickerStockList(stickers)
            }
        }
    }
}

