package com.example.diarytablet.ui.screens

import MainModal
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.BlockButton
import com.example.diarytablet.ui.components.ButtonType
import com.example.diarytablet.ui.components.MissionBar
import com.example.diarytablet.ui.components.MissionItem
import com.example.diarytablet.ui.components.Navbar
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.MainViewModel


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {

    BackgroundPlacement(backgroundType = backgroundType)

    var isModalVisible by remember { mutableStateOf(false) }

    val missions = viewModel.missions

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            MissionBar(
                missions = missions,
                modifier = Modifier
                    .offset(x = 58.dp, y = 27.dp)
            )

            Navbar(
                modifier = Modifier.padding(end = 16.dp),
                navController = navController
            )
        }

        // 캐릭터 이미지와 텍스트 풍선을 나란히 배치
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 캐릭터 이미지 추가
            Image(
                painter = painterResource(id = R.drawable.main_char), // 캐릭터 이미지 리소스 ID로 교체
                contentDescription = "Character",
                modifier = Modifier.size(400.dp)
                    .offset(x = -50.dp ,y = 50.dp)
            )

            Box(
                modifier = Modifier.size(432.dp, 450.dp)
                    .offset(x = -120.dp)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.text_balloon),
                    contentDescription = "Text Balloon"
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "다일리에 온 걸 환영해!",
                        color = Color.Black,
                        fontSize = 26.sp,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = "나랑 그림일기도 쓰고",
                        color = Color.Black,
                        fontSize = 26.sp,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = "단어도 학습해보자!",
                        color = Color.Black,
                        fontSize = 26.sp,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = "그림퀴즈도 할 수 있어~",
                        color = Color.Black,
                        fontSize = 26.sp,
                        lineHeight = 44.sp
                    )
                }
            }
        }

        // BasicButton 배치 (왼쪽 하단)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            BasicButton(
                onClick = { navController.navigate("shop") },
                text = "상점",
                imageResId = R.drawable.shop
            )
            BasicButton(
                onClick = { navController.navigate("stock") },
                text = "보관함",
                imageResId = R.drawable.stack_room
            )
            BasicButton(
                onClick = { /* 기록 클릭 이벤트 */ },
                text = "기록",
                imageResId = R.drawable.record
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp)
        ) {
            BasicButton(
                onClick = { isModalVisible = true },
                text = "시작하기",
                imageResId = 11,
                fontSize = 36f
            )
        }

        MainModal(
            isModalVisible = isModalVisible,
            onDismiss = { isModalVisible = false },
            navController = navController,
            onWordLearningClick = {
                navController.navigate("wordLearning") {
                    popUpTo("main") { inclusive = true }
                }
                isModalVisible = false
            },
            onDrawingDiaryClick = {
                navController.navigate("diary") {
                    popUpTo("main") { inclusive = true }
                }
                isModalVisible = false
            },
            onDrawingQuizClick = {
                navController.navigate("quiz") {
                    popUpTo("main") { inclusive = true }
                }
                isModalVisible = false
            }
        )
    }
}


//@Preview(widthDp = 1280, heightDp = 800, showBackground = true)
//@Composable
//fun previewMain() {
//    MainScreen(viewModel = MainViewModel(),    navController= NavController,
//        backgroundType = BackgroundType.DEFAULT)
//}