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


    // 전체 화면을 채우는 Box
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Row가 가로 전체를 차지하도록 설정
            horizontalArrangement = Arrangement.SpaceBetween, // 양 끝에 배치
            verticalAlignment = Alignment.Top // 수직 정렬
        )  {
            MissionBar(
                missions = missions,
                modifier = Modifier
                    .offset(x = 58.dp, y = 27.dp) // 위치 조정
            )

            Navbar(

                modifier = Modifier
                    .padding(end = 16.dp) // 오른쪽 여백 조정
            )
            // MissionRow 배치

        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(432.dp, 450.dp)
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
                    lineHeight = 44.sp              )
                Text(
                    text = "단어도 학습해보자!",
                    color = Color.Black,
                    fontSize = 26.sp,
                    lineHeight = 44.sp               )
                Text(
                    text = "그림퀴즈도 할 수 있어~",
                    color = Color.Black,
                    fontSize = 26.sp,
                    lineHeight = 44.sp               )
            }
        }

        // BasicButton 배치 (왼쪽 하단)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp) // 여백 설정
        ) {
            BasicButton(
                onClick = { navController.navigate("shop") },
                text = "상점",
                imageResId = R.drawable.shop // 상점 이미지 리소스
            )
            BasicButton(
                onClick = { navController.navigate("stock")},
                text = "보관함",
                imageResId = R.drawable.stack_room // 보관함 이미지 리소스
            )
            BasicButton(
                onClick = { /* 기록 클릭 이벤트 */ },
                text = "기록",
                imageResId = R.drawable.record // 기록 이미지 리소스
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