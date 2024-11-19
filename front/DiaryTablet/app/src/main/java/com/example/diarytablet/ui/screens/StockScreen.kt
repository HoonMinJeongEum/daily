package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.StockTab
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.ShopStockViewModel

@Composable
fun StockScreen(
    navController: NavController,
    viewModel: ShopStockViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val userCoupons by viewModel.userCoupons.observeAsState(emptyList())
    val userStickers by viewModel.userStickers.observeAsState(emptyList())
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.fetchUserCoupons()
        viewModel.fetchUserStickers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(40.dp)
    ) {
        // 뒤로 가기 버튼과 제목을 표시하는 상단 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, bottom= 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.cute_back), // 뒤로 가기 이미지 리소스
                contentDescription = "뒤로 가기 버튼",
                modifier = Modifier
                    .size(60.dp)
                    .clickable (
                        indication = null, // 클릭 효과 제거
                        interactionSource = remember { MutableInteractionSource() }
                    ){
                        playButtonSound(context, R.raw.all_button)
                        navController.navigate("main") {
                            popUpTo("stock") { inclusive = true }
                        }
                    }
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "보관함", // 제목 텍스트
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
        }

        // StockTab 구성 요소를 하단에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp)
        ) {
            StockTab(
                coupons = userCoupons,
                stickers = userStickers,
                modifier = Modifier.align(Alignment.Center),
                viewModel = viewModel
            )
        }
    }
}
