package com.example.diarytablet.ui.screens

import ShopTab
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.DailyButton
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.NavBarViewModel
import com.example.diarytablet.viewmodel.ShopStockViewModel

@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopStockViewModel = hiltViewModel(),
    navBarViewModel: NavBarViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    val coupons by viewModel.coupons.observeAsState(emptyList())
    val stickers by viewModel.stickers.observeAsState(emptyList())
    val shellCount by navBarViewModel.shellCount
    val remainingShells by viewModel.remainingShells.observeAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val buttonWidth = screenWidth * 0.15f
    val buttonHeight = screenWidth * 0.07f
    val buttonFontSize = (buttonHeight.value * 0.4f).sp
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.fetchCoupons()
        viewModel.fetchStickers()
        navBarViewModel.initializeData()
    }
    val shellCountToDisplay = if (remainingShells != null) remainingShells!! else shellCount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.cute_back),
                contentDescription = "뒤로 가기 버튼",
                modifier = Modifier
                    .size(60.dp)
                    .clickable (
                        indication = null, // 클릭 효과 제거
                        interactionSource = remember { MutableInteractionSource() }
                    ){
                        playButtonSound(context, R.raw.all_button)
                        navController.navigate("main") {
                            popUpTo("shop") { inclusive = true }
                        }
                    }
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "상점",
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.weight(1f))
            BasicButton(
                modifier = Modifier
                    .height(buttonHeight),
                onClick = {},
                text = shellCountToDisplay.toString(),
                isOutlined = false,
                enabled = false,
                useDisabledColor = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp)
        ) {
            ShopTab(
                coupons = coupons,
                stickers = stickers,
                modifier = Modifier.align(Alignment.Center),
                viewModel = viewModel,
                navBarViewModel = navBarViewModel
            )
        }
    }
}
