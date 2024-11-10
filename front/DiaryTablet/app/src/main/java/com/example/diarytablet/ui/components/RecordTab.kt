package com.example.diarytablet.ui.components

import StickerShopList
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.viewmodel.LogViewModel

@Composable
fun RecordTab(
    modifier: Modifier = Modifier,
    viewModel: LogViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("단어장", "일기장")
    var isDiaryDetailVisible by remember { mutableStateOf(false) }
    var selectedDiaryId by remember { mutableStateOf<Int?>(null) }

    // 전체 박스 (탭 + 내용물)
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
                            .clickable { selectedTabIndex = index }
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
    }
}
