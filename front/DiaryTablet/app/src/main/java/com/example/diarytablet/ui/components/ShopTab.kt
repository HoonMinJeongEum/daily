import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.model.Coupon
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.ui.components.CouponShopList
import com.example.diarytablet.ui.components.StickerShopList

@Composable
fun ShopTab(
    coupons: List<Coupon>,
    stickers: List<Sticker>,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("쿠폰", "스티커")

    // 전체 박스 (탭 + 내용물)
    Box(
        modifier = Modifier
            .width(1300.dp) // 전체 박스의 너비 조정
            .height(650.dp) // 전체 박스의 높이 조정
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(25.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 왼쪽에 세로 탭을 배치하는 Column
            Column(
                modifier = Modifier
                    .width(200.dp) // 왼쪽 탭 영역의 너비
                    .padding(top = 80.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(25.dp), // 탭 간 간격 추가
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 각 탭에 대한 구성
                tabTitles.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = { selectedTabIndex = index },
                                indication = null, // ripple effect 제거
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 탭 텍스트를 가운데에 배치
                            Text(
                                text = title,
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTabIndex == index) Color(0xFF83B4FF) else Color(0xFF959595),
                                modifier = Modifier
                                    .padding(start = 35.dp) // 텍스트 왼쪽에 패딩 추가
                                    .weight(1f) // 텍스트를 중앙에 위치하게 함
                            )

                            // 오른쪽 끝에 강조 표시
                            if (selectedTabIndex == index) {
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(40.dp) // 텍스트 높이에 맞추어 강조선 높이 설정
                                        .background(Color(0xFF83B4FF), shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                                )
                            }
                        }
                    }
                }
            }

            // 구분선 Divider
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color.Gray)
            )

            // 오른쪽에 내용물 표시
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp) // 구분선과 내용물 간의 여백 설정
            ) {
                if (selectedTabIndex == 0) {
                    CouponShopList(coupons) // "쿠폰" 탭이 선택된 경우
                } else {
                    StickerShopList(stickers) // "스티커" 탭이 선택된 경우
                }
            }
        }
    }
}
