package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.RecordTab
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.LogViewModel
import com.example.diarytablet.viewmodel.ShopStockViewModel

@Composable
fun RecordScreen(
    navController: NavController,
    viewModel: LogViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT,
    titleId: Int
) {

    val context = LocalContext.current
    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(40.dp)
    ) {
        // 상단에 뒤로 가기 버튼과 제목 배치
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, bottom = 40.dp),
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
                        navController.navigate("main")
                    }
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "기록", // 제목 텍스트
                fontSize = 40.sp,
                color = Color.White
            )
        }

        // RecordTab 구성 요소를 하단에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp)
        ) {
            RecordTab(
                modifier = Modifier.align(Alignment.Center),
                viewModel = viewModel,
                titleId = titleId
            )
        }
    }
}
