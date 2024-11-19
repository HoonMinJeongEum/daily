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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.model.Sticker
import kotlinx.coroutines.delay
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.model.StickerStock
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.ui.theme.MyTypography

@Composable
fun StickerStockList(stickers: List<StickerStock>) {
    if (stickers.isEmpty()) {
        // 리스트가 비어 있을 때 표시할 텍스트
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "아직 구매한 스티커가 없어요.",
                style = MyTypography.bodyMedium,
                color = DarkGray
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(stickers.reversed()) { index, sticker ->
                StickerStockCard(sticker, index)
            }
        }
    }
}

@Composable
fun StickerStockCard(sticker: StickerStock, index: Int) {
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
//            .clickable(
//                interactionSource = remember { MutableInteractionSource() },
//                indication = null
//            ) {
//                isPressed = true
//            }
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

        // 카드 내용물
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = sticker.img,
                    placeholder = painterResource(R.drawable.loading),
                    error = painterResource(R.drawable.loading)
                ),
                contentDescription = "스티커 이미지",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
