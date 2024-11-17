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
import androidx.compose.ui.text.font.FontWeight
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
import com.example.diarytablet.ui.components.modal.CommonModal
import com.example.diarytablet.ui.components.modal.CommonPopup
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.utils.playButtonSound
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
    stickers: List<Sticker>,
    shopViewModel: ShopStockViewModel,
    navBarViewModel: NavBarViewModel
) {
    var selectedSticker by remember { mutableStateOf<Sticker?>(null) }
    var isModalVisible by remember { mutableStateOf(false) }
    var stickerModalState by remember { mutableStateOf(StickerModalState.NONE) }

    val shellCount by navBarViewModel.shellCount
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        navBarViewModel.initializeData()
    }
    if (stickers.isEmpty()) {
        // 리스트가 비어 있을 때 표시할 텍스트
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "등록된 스티커가 없어요.",
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
            when (stickerModalState) {
                StickerModalState.PURCHASE_CONFIRMATION -> {
                    CommonModal(
                        onDismissRequest = {
                            isModalVisible = false
                            stickerModalState = StickerModalState.NONE
                        },
                        titleText = "스티커를 구매하시겠습니까?",
                        confirmText = "${selectedSticker!!.price}",
                        confirmIconResId = R.drawable.jogae,
                        onConfirm = {
                            if (shellCount >= selectedSticker!!.price) {
                                shopViewModel.buySticker(selectedSticker!!.id)
                                stickerModalState = StickerModalState.PURCHASE_SUCCESS
                            } else {
                                stickerModalState = StickerModalState.INSUFFICIENT_SHELLS
                            }
                        }
                    )
                }

                StickerModalState.INSUFFICIENT_SHELLS -> {
                    // 소리 재생
                    LaunchedEffect(stickerModalState) {
                        playButtonSound(context,R.raw.warning)
                    }

                    CommonPopup(
                        onDismissRequest = {
                            isModalVisible = false
                            stickerModalState = StickerModalState.NONE
                        },
                        titleText = "조개를 조금 더 모아보아요!"
                    )
                }

                StickerModalState.PURCHASE_SUCCESS -> {
                    // 소리 재생
                    LaunchedEffect(stickerModalState) {
                        playButtonSound(context,R.raw.main_clear)
                    }

                    CommonPopup(
                        onDismissRequest = {
                            isModalVisible = false
                            stickerModalState = StickerModalState.NONE
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
fun StickerCard(sticker: Sticker, index: Int, onStickerClick: () -> Unit, viewModel: ShopStockViewModel) {
    val context = LocalContext.current
    val imageLoader = remember { newImageLoader(context) }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(sticker.img)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .build(),
        imageLoader = imageLoader
    )

    val backgroundImage = if (index % 2 == 0) R.drawable.sticker_yellow_up else R.drawable.sticker_blue_up

    Box(
        modifier = Modifier
            .padding(5.dp, bottom = 20.dp)
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
                {
                    onStickerClick()
                    playButtonSound(context, R.raw.shop_buy)

                }
    ) {
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
