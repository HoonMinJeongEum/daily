import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.R
import com.example.diarytablet.viewmodel.ShopStockViewModel
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun newImageLoader(context: android.content.Context): ImageLoader {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    return ImageLoader.Builder(context)
//        .okHttpClient(okHttpClient)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .build()
}

@Composable
fun StickerShopList(stickers: List<Sticker>, viewModel: ShopStockViewModel) {
    var selectedSticker by remember { mutableStateOf<Sticker?>(null) }
    var isModalVisible by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(stickers) { index, sticker ->
            StickerCard(
                sticker = sticker,
                index = index,
                onStickerClick = {
                    selectedSticker = sticker
                    isModalVisible = true
                },
                viewModel = viewModel
            )
        }
    }

    // Modal for purchase confirmation
    if (isModalVisible && selectedSticker != null) {
        PurchaseConfirmationModal(
            sticker = selectedSticker!!,
            onConfirm = {
                viewModel.buySticker(selectedSticker!!.id)
                isModalVisible = false
            },
            onCancel = {
                isModalVisible = false
            }
        )
    }
}

@Composable
fun StickerCard(sticker: Sticker, index: Int, onStickerClick: () -> Unit, viewModel: ShopStockViewModel) {
    var isPressed by remember { mutableStateOf(false) }

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
                onStickerClick() // 스티커 클릭 시 모달 표시
            }
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val context = LocalContext.current
                val imageLoader = newImageLoader(context)
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(sticker.img)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading)
                        .build(),
                    imageLoader = imageLoader,
                )

                Image(
                    painter = painter,
                    contentDescription = "Sticker Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
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

@Composable
fun PurchaseConfirmationModal(sticker: Sticker, onConfirm: () -> Unit, onCancel: () -> Unit) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .width(300.dp) // 모달 너비 설정
                .clip(RoundedCornerShape(16.dp)) // 모서리를 둥글게 설정
                .background(Color.White)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 스티커 이미지 및 구매 질문 텍스트
                Image(
                    painter = rememberAsyncImagePainter(model = sticker.img),
                    contentDescription = "Sticker Image",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "스티커를 구매하시겠습니까?",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 구매 버튼 안에 조개 이미지와 가격 텍스트 배치
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(50.dp), // 둥근 테두리 설정
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.jogae),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${sticker.price} 조개",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 취소 버튼
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(50.dp) // 둥근 테두리 설정
                    ) {
                        Text("취소", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
