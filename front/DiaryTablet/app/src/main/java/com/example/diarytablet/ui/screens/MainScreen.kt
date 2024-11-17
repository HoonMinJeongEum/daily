package com.example.diarytablet.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.example.diarytablet.ui.components.modal.MainModal
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.MissionBar
import com.example.diarytablet.ui.components.MissionItem
import com.example.diarytablet.ui.components.Navbar
import com.example.diarytablet.ui.components.main.TypingText
import com.example.diarytablet.ui.components.modal.CommonPopup
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.MainViewModel
import com.example.diarytablet.viewmodel.NavBarViewModel
import com.example.diarytablet.ui.components.modal.MissionModal


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    navbarViewModel: NavBarViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    var isModalVisible by remember { mutableStateOf(false) }
    var isPopupVisible by remember { mutableStateOf(false) }
    val isFinished by viewModel.isFinished
    val origin = viewModel.origin
    val missions = viewModel.missions
    val username by navbarViewModel.userName

    var currentIndex by remember { mutableStateOf(0) }

    // 캐릭터 이미지와 텍스트 목록
    val characterImages = listOf(
        R.drawable.main_char,   // 하이해꽁
        R.drawable.main_char2,  // 그림해꽁
        R.drawable.main_char3,  // 학습해꽁
        R.drawable.main_char4,  // 부모해꽁
        R.drawable.main_char5   // 조개해꽁
    )

    val characterTexts = listOf(
        "안녕, $username!\n나는 해꽁이야.\n나를 눌러봐~",
        "$username~\n오늘 있었던 일을\n그림일기에 써보자!",
        "$username~\n단어를 학습해서\n조개를 모아보자!",
        "$username~\n부모님이랑 같이\n그림 퀴즈를 해볼까?",
        "조개를 모아서\n상점에서 쓸 수 있어!"
    )

    val missionItems = missions.map { mission ->
        val isSuccessFromOrigin = when (origin) {
            "wordLearning" -> mission.text == "단어 학습"
            "diary" -> mission.text == "그림 일기"
            "quiz" -> mission.text == "그림 퀴즈"
            else -> false
        }
        mission.copy(isSuccess = mission.isSuccess || isSuccessFromOrigin)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                MissionBar(
                    missions = missions,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
                Spacer(modifier = Modifier.weight(1f))
                Navbar(
                    mainViewModel = viewModel,
                    navController = navController,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.Bottom,
            ) {
                Spacer(modifier = Modifier.width(blockWidth)) // 첫 번째 블록
                Image(
                    painter = painterResource(id = characterImages[currentIndex]),
                    contentDescription = "Character",
                    modifier = Modifier
                        .width(screenWidth * 0.5f) // 두 번째 블록
                        .aspectRatio(1.67f)
                        .offset(x = -blockWidth * 0.5f , y = screenHeight * 0.06f)
                        .clickable(
                            indication = null, // 클릭 효과 없애기
                            interactionSource = remember { MutableInteractionSource() } // 필수: 사용자 인터랙션 관리
                        ) {
                            // 이미지 클릭 시 인덱스를 업데이트하여 다음 이미지와 텍스트로 변경
                            currentIndex = (currentIndex + 1) % characterImages.size
                        }
                )

                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.8f) // 세 번째 블록
                        .aspectRatio(0.9f)
                        .offset(x = -blockWidth * 1.2f)
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
                            .padding(blockWidth * 0.1f)
                            .clickable(
                                indication = null, // 클릭 효과 없애기
                                interactionSource = remember { MutableInteractionSource() } // 필수: 사용자 인터랙션 관리
                            ) {
                                // 이미지 클릭 시 인덱스를 업데이트하여 다음 이미지와 텍스트로 변경
                                currentIndex = (currentIndex + 1) % characterImages.size
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TypingText(
                            text = characterTexts[currentIndex],
                            fontSize = (screenHeight.value * 0.04f).sp,
                            lineHeight = (screenHeight.value * 0.06f).sp
                        )
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

            val buttonFontSize = screenHeight.value * 0.046f
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
                    modifier = Modifier
                        .width(screenWidth * 0.2f)
                        .height(screenHeight * 0.14f),
                    fontSize = buttonFontSize // 버튼 글자 크기 비율로 조정
                )
            }


        }
        // MainModal
        MainModal(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
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
                // "그림 일기" 미션 상태 확인
                val diaryMission = missions.find { it.text == "그림 일기" }
                if (diaryMission?.isSuccess == true) {
                    // 일기 미션이 완료되었을 경우 팝업만 표시하고 이동 차단
                    isPopupVisible = true
                } else {
                    // 미션이 완료되지 않았을 경우에만 DiaryScreen으로 이동
                    navController.navigate("diary") {
                        popUpTo("main") { inclusive = true }
                    }
                    isModalVisible = false
                }
            },
            onDrawingQuizClick = {
                navController.navigate("quiz") {
                    popUpTo("main") { inclusive = true }
                }
                isModalVisible = false
            }
        )
        if (isPopupVisible) {
            CommonPopup(
                onDismissRequest = {
                    isPopupVisible = false
                },
                titleText = "일기는 하루에 한번 만 쓸수 있어요!!"
            )
        }
        // MissionModal
        AnimatedVisibility(
            visible = isFinished,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            MissionModal(
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                isDialogVisible = isFinished,
                onDismiss = {
                    viewModel.setFinished(false)
                    val completedMission = when (origin) {
                        "wordLearning" -> MissionItem("단어 학습", isSuccess = true)
                        "diary" -> MissionItem("그림 일기", isSuccess = true)
                        "quiz" -> MissionItem("그림 퀴즈", isSuccess = true)
                        else -> null
                    }
                    completedMission?.let {
                        viewModel.completeMissionItem(it, navbarViewModel) // NavbarViewModel 전달
                    }
                },
                missionItems = missionItems,
                lastMissions = missions

            )
        }
    }
}


