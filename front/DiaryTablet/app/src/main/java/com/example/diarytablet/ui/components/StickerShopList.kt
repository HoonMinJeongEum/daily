import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.R
import kotlinx.coroutines.delay

@Composable
fun StickerShopList(stickers: List<Sticker>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(stickers) { index, sticker ->
            StickerCard(sticker, index)
        }
    }
}

@Composable
fun StickerCard(sticker: Sticker, index: Int) {
    var isPressed by remember { mutableStateOf(false) }

    // 클릭 상태에 따라 배경 이미지 설정
    val backgroundImage = if (isPressed) {
        if (index % 2 == 0) R.drawable.sticker_yellow_down else R.drawable.sticker_blue_down
    } else {
        if (index % 2 == 0) R.drawable.sticker_yellow_up else R.drawable.sticker_blue_up
    }

    Box(
        modifier = Modifier
            .padding(5.dp, bottom = 20.dp)
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
            }
    ) {
        // 클릭 후 일정 시간 후에 상태를 원래대로 복귀
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100L) // 100밀리초 동안 `down` 상태 유지
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(sticker.imgRes),
                    contentDescription = "스티커 이미지",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${sticker.price} 조개",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
