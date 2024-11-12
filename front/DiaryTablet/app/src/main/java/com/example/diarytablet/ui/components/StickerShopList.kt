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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.diarytablet.model.Sticker
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.viewmodel.NavBarViewModel
import com.example.diarytablet.viewmodel.ShopStockViewModel
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

enum class StickerModalState {
    NONE,
    PURCHASE_CONFIRMATION,
    INSUFFICIENT_SHELLS,
    PURCHASE_SUCCESS
}

fun newImageLoader(context: android.content.Context): ImageLoader {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    return ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .build()
}

@Composable
fun StickerShopList(
    initialStickers: List<Sticker>,
    shopViewModel: ShopStockViewModel,
    navBarViewModel: NavBarViewModel = hiltViewModel()
) {
    var stickers by remember { mutableStateOf(initialStickers) }
    var selectedSticker by remember { mutableStateOf<Sticker?>(null) }
    var isModalVisible by remember { mutableStateOf(false) }
    var stickerModalState by remember { mutableStateOf(StickerModalState.NONE) }

    val shellCount by navBarViewModel.shellCount

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
                    stickerModalState = StickerModalState.PURCHASE_CONFIRMATION
                    isModalVisible = true
                },
                viewModel = shopViewModel
            )
        }
    }

    if (isModalVisible && selectedSticker != null) {
        PurchaseConfirmationModal(
            sticker = selectedSticker!!,
            stickerModalState = stickerModalState,
            onConfirm = {
                if (shellCount >= selectedSticker!!.price) {
                    shopViewModel.buySticker(selectedSticker!!.id)
                    stickers = stickers.filter { it.id != selectedSticker!!.id }
                    stickerModalState = StickerModalState.PURCHASE_SUCCESS
                } else {
                    stickerModalState = StickerModalState.INSUFFICIENT_SHELLS
                }
            },
            onCancel = {
                isModalVisible = false
                stickerModalState = StickerModalState.NONE
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
                onStickerClick()
            }
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100L)
                isPressed = false
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 15.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jogae),
                        contentDescription = null,
                        modifier = Modifier
                            .size(37.dp)
                            .offset(y = (-3).dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "${sticker.price}",
                        fontSize = 30.sp,
                        color = DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseConfirmationModal(
    sticker: Sticker,
    stickerModalState: StickerModalState,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val message = when (stickerModalState) {
                    StickerModalState.PURCHASE_CONFIRMATION -> "스티커를 구매하시겠습니까?"
                    StickerModalState.INSUFFICIENT_SHELLS -> "조개를 조금 더 모아볼까요?"
                    StickerModalState.PURCHASE_SUCCESS -> "구매가 완료되었습니다!"
                    else -> ""
                }

                Text(
                    text = message,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (stickerModalState == StickerModalState.PURCHASE_CONFIRMATION) {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(50.dp),
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

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onCancel,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text("취소", fontSize = 16.sp)
                        }
                    } else {
                        Button(
                            onClick = onCancel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text("확인", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
