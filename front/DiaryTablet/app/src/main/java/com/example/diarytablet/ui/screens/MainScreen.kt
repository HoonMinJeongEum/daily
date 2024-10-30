package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                missions = listOf( // 예시 미션 데이터 추가
                    MissionItem("미션 1", isSuccess = true),
                    MissionItem("미션 2", isSuccess = false),
                    MissionItem("미션 3", isSuccess = true)
                ),
                modifier = Modifier
                    .offset(x = 58.dp, y = 27.dp) // 위치 조정
            )

            Navbar(
                point = "240",
                isBasicButtonOutlined = false,
                isAlarmOn = true,
                onBasicButtonClick = { /* TODO: BasicButton 클릭 이벤트 */ },
                onAlarmButtonClick = { /* TODO: AlarmButton 클릭 이벤트 */ },
                onProfileClick = { /* TODO: UserProfile 클릭 이벤트 */ },
                modifier = Modifier
                    .padding(end = 16.dp) // 오른쪽 여백 조정
            )
            // MissionRow 배치

        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(50.dp), // 버튼 간 간격 조정
            verticalAlignment = Alignment.CenterVertically // 수직 정렬
        ) {
            BlockButton(onClick = {}, buttonType = ButtonType.WORD_LEARNING)
            BlockButton(onClick = {}, buttonType = ButtonType.DRAWING_DIARY)
            BlockButton(onClick = {}, buttonType = ButtonType.DRAWING_QUIZ)
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
                onClick = { /* 상점 클릭 이벤트 */ },
                text = "상점",
                imageResId = R.drawable.shop // 상점 이미지 리소스
            )
            BasicButton(
                onClick = { /* 보관함 클릭 이벤트 */ },
                text = "보관함",
                imageResId = R.drawable.stack_room // 보관함 이미지 리소스
            )
            BasicButton(
                onClick = { /* 기록 클릭 이벤트 */ },
                text = "기록",
                imageResId = R.drawable.record // 기록 이미지 리소스
            )
        }
    }
}

//@Preview(widthDp = 1280, heightDp = 800, showBackground = true)
//@Composable
//fun previewMain() {
//    MainScreen(viewModel = MainViewModel(),    navController= NavController,
//        backgroundType = BackgroundType.DEFAULT)
//}