package com.example.diarytablet.ui.components

import StickerShopList
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.LogViewModel

@Composable
fun RecordTab(
    modifier: Modifier = Modifier,
    viewModel: LogViewModel,
    titleId: Int
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("단어장", "일기장")
    var isDiaryDetailVisible by remember { mutableStateOf(false) }
    var selectedDiaryId by remember { mutableStateOf<Int?>(null) }
    // 전체 박스 (탭 + 내용물)

    var showInfo by remember { mutableStateOf(false) } // 물음표 클릭 시 표시할 문구 상태

    var shakeState by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (shakeState) 5f else 0f)

    LaunchedEffect(selectedTabIndex) {
        showInfo = false
    }
    LaunchedEffect(Unit) {
        while (true) {
            // 5초 동안 대기
            kotlinx.coroutines.delay(3500L)
            // 흔들림 애니메이션 시작
            shakeState = true
            kotlinx.coroutines.delay(100L)  // 좌우 한 번씩 0.1초씩 흔들기
            shakeState = false
            kotlinx.coroutines.delay(100L)
            shakeState = true
            kotlinx.coroutines.delay(100L)
            shakeState = false
            kotlinx.coroutines.delay(100L)
            shakeState = true
            kotlinx.coroutines.delay(100L)
            shakeState = false
        }
    }

    LaunchedEffect(titleId) {
        if (titleId != -1) {
            viewModel.fetchDiaryById(titleId)
            selectedDiaryId = titleId
            selectedTabIndex = 1
            isDiaryDetailVisible = true
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(top=30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 왼쪽에 세로 탭을 배치하는 Column
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.15f) // 왼쪽 탭 영역의 너비
                    .fillMaxHeight()
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 각 탭에 대한 구성
                tabTitles.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTabIndex = index
                                playButtonSound(context,R.raw.all_button )
                            }
                            .padding(vertical = 1.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = title,
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTabIndex == index) Color(0xFF83B4FF) else Color(0xFF959595),
                                modifier = Modifier
                                    .padding(start = 35.dp)
                                    .weight(1f)
                            )

                            if (selectedTabIndex == index) {
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(40.dp)
                                        .background(Color(0xFF83B4FF), shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                                )
                            }
                        }
                    }
                }
            }

            // 구분선 Divider - Row 내부에서 탭과 내용물 사이에 배치
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.Gray)
            )

            // 오른쪽에 내용물 표시
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                contentAlignment = Center // 오른쪽 박스의 중앙 정렬
            ) {
                if (selectedTabIndex == 0){
                    LearnedWordTab(viewModel = viewModel)
                } else {
                    if (isDiaryDetailVisible) {
                        MyDiaryDetail(
                            diaryId = selectedDiaryId!!,
                            onBackClick = {isDiaryDetailVisible = false},
                            viewModel = viewModel)
                    } else {
                        DailyCalendar(
                            viewModel = viewModel,
                            onDateCellClick = { diaryId ->
                                selectedDiaryId = diaryId
                                isDiaryDetailVisible = true
                            }
                        )
                    }
                }
            }
        }
        // 왼쪽 하단에 캐릭터와 물음표 배치 (독립적 배치)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .align(Alignment.BottomStart)
        ) {
            // 캐릭터
            Image(
                painter = painterResource(
                id = when (selectedTabIndex) {
                    0 -> R.drawable.main_char3 // tabIndex가 0일 때 이미지
                    1 -> R.drawable.main_char2 // tabIndex가 1일 때 이미지
                    else -> R.drawable.otter_character // 기본 이미지 (예외 처리)
                }),
                contentDescription = null,
                modifier = Modifier
                    .size(screenHeight * 0.3f)
                    .offset(x = -screenWidth * 0.01f, y = screenHeight * 0.03f)
                    .graphicsLayer(rotationZ = rotation)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ){
                        showInfo = !showInfo
                    }
                    .zIndex(0f)
            )

            // 물음표 또는 문구
            Box(
                modifier = Modifier
                    .offset(x = screenWidth * 0.08f, y = screenHeight * 0.05f)
                    .width(if (showInfo) screenWidth * 0.5f else screenWidth * 0.07f)
                    .height(if (showInfo) screenHeight * 0.15f else screenHeight * 0.07f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ){
                        showInfo = !showInfo
                    }
                    .zIndex(1f)
            ) {
                if (showInfo) {
                    // 말풍선 이미지 안에 텍스트 배치
                    Box(
                        modifier = Modifier
                            .offset(x = screenWidth * 0.01f, y = -screenHeight * 0.08f) // 오른쪽과 위로 이동
                            .clickable { showInfo = !showInfo }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.popup_balloon),
                            contentDescription = "말풍선",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = 1.6f
                                    scaleY = 1.35f
                                }
                        )

                        Text(
                            text = buildAnnotatedString {
                                append("단어장과 일기장은 ")
                                withStyle(style = SpanStyle(color = Color(0xFF42A5F5))) {
                                    append("부모님")
                                }
                                append("도\n확인할 수 있어요 !")
                            },
                            color = DarkGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            softWrap = false,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .offset(x = (-10).dp)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.question_mark),
                        contentDescription = "도움말",
                        modifier = Modifier
                            .size(screenHeight * 0.1f)
                    )
                }
            }
        }
    }
}
