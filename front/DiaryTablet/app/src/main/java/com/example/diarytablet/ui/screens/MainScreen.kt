package com.example.diarytablet.ui.screens


import MainModal
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.example.diarytablet.ui.components.MissionModal


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var isModalVisible by remember { mutableStateOf(false) }
    val isFinished by viewModel.isFinished
    val origin = viewModel.origin
    val missions = viewModel.missions

    val missionItems = when (origin) {
        "wordLearning" -> listOf(
            MissionItem("단어 학습", isSuccess = true),
            MissionItem("그림 일기", isSuccess = false),
            MissionItem("그림 퀴즈", isSuccess = false)
        )
        "diary" -> listOf(
            MissionItem("단어 학습", isSuccess = false),
            MissionItem("그림 일기", isSuccess = true),
            MissionItem("그림 퀴즈", isSuccess = false)
        )
        "quiz" -> listOf(
            MissionItem("단어 학습", isSuccess = false),
            MissionItem("그림 일기", isSuccess = false),
            MissionItem("그림 퀴즈", isSuccess = true)
        )
        else -> listOf(
            MissionItem("단어 학습", isSuccess = false),
            MissionItem("그림 일기", isSuccess = false),
            MissionItem("그림 퀴즈", isSuccess = false)
        )
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val blockWidth = screenWidth / 5

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenWidth * 0.02f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MissionBar(
                    missions = missions,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
                Navbar(
                    navController = navController,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.Bottom,
            ) {
                // 캐릭터는 2번째 블록에 위치
                Spacer(modifier = Modifier.width(blockWidth)) // 첫 번째 블록
                Image(
                    painter = painterResource(id = R.drawable.main_char),
                    contentDescription = "Character",
                    modifier = Modifier
                        .width(screenWidth * 0.5f) // 두 번째 블록
                        .aspectRatio(1.67f)
                        .offset(x = -blockWidth * 0.9f , y = screenHeight * 0.1f)
                )

                // 말풍선은 3번째 블록에 위치
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.8f) // 세 번째 블록
                        .aspectRatio(0.9f)
                        .offset(x = -blockWidth * 1.5f)
                ) {
                    Image(
                        modifier =
                        Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                        painter = painterResource(id = R.drawable.text_balloon),
                        contentDescription = "Text Balloon"
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(blockWidth * 0.1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("다일리에 온 걸 환영해!", color = Color.Black, fontSize = 24.sp, lineHeight = 26.sp)
                        Text("나랑 그림일기도 쓰고", color = Color.Black, fontSize = 24.sp, lineHeight = 26.sp)
                        Text("단어도 학습해보자!", color = Color.Black, fontSize = 24.sp, lineHeight = 26.sp)
                        Text("그림퀴즈도 할 수 있어~", color = Color.Black, fontSize = 24.sp, lineHeight = 26.sp)
                    }
                }
            }

            // 하단 왼쪽 BasicButton
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(screenWidth * 0.02f)
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
                    onClick = { navController.navigate("record") },
                    text = "기록",
                    imageResId = R.drawable.record
                )
            }

            // 하단 오른쪽 시작하기 버튼
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(screenWidth * 0.02f)
            ) {
                BasicButton(
                    onClick = { isModalVisible = true },
                    text = "시작하기",
                    imageResId = 11,
//                    fontSize = screenHeight * 0.045f // 버튼 글자 크기 비율로 조정
                )
            }

            // MainModal
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

            // MissionModal
            MissionModal(
                isDialogVisible = isFinished,
                onDismiss = {
                    viewModel.setFinished(false)
                    val completedMission = when (origin) {
                        "wordLearning" -> MissionItem("단어 학습", isSuccess = true)
                        "diary" -> MissionItem("그림 일기", isSuccess = true)
                        "quiz" -> MissionItem("그림 퀴즈", isSuccess = true)
                        else -> null
                    }
                    completedMission?.let { viewModel.completeMissionItem(it) }
                },
                missionItems = missionItems
            )
        }
    }
}


