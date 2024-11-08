package com.example.diarytablet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.ui.components.WordListItemByMember
import com.example.diarytablet.viewmodel.LogViewModel
import retrofit2.Response

@Composable
fun LearnedWordTab(
    viewModel: LogViewModel,
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    BackgroundPlacement(backgroundType = backgroundType)

    LaunchedEffect(Unit) {
        viewModel.getWordList()
    }

    var selectedTab by remember { mutableStateOf("가나다순") }

    // Modal 상태 관리
    var isModalOpen by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf<WordLearnedResponseDto?>(null) }

    val wordListResponse by viewModel.wordList.observeAsState(Response.success(emptyList()))
    val wordList = wordListResponse.body() ?: emptyList() // LiveData에서 실제 List<Word> 추출
    val sortedWordList by viewModel.dateSortedWordList.observeAsState(emptyList())

    val displayedList = if (selectedTab == "날짜순") sortedWordList else wordList

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Color.White,
                    )
                    .fillMaxWidth()
                    .height(780.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                        horizontalArrangement = Arrangement.End
                    ) {
                        DynamicColorButton(
                            text = "가나다순",
                            isSelected = selectedTab == "가나다순",
                            onClick = { selectedTab = "가나다순" },
                        )
                        DynamicColorButton(
                            text = "날짜순",
                            isSelected = selectedTab == "날짜순",
                            onClick = { selectedTab = "날짜순" },
                        )
                    }
                    WordListItemByMember(
                        wordList = displayedList,
                        selectedTab = selectedTab,
                        onWordClick = { word ->
                            // WordItem 클릭 시 모달 표시 및 선택된 단어 설정
                            selectedWord = word
                            isModalOpen = true
                        }
                    )
                    // 모달 창 표시 (isModalOpen이 true일 때만)
                    if (isModalOpen && selectedWord != null) {
                        WordDetail(
                            word = selectedWord!!,
                            onDismissRequest = { isModalOpen = false }
                        )
                    }
                }
            }
        }
    }
}
