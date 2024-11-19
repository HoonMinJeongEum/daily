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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.utils.playButtonSound
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

    var isModalOpen by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf<WordLearnedResponseDto?>(null) }

    val wordListResponse by viewModel.wordList.observeAsState(Response.success(emptyList()))
    val wordList = wordListResponse.body() ?: emptyList()
    val sortedWordList by viewModel.dateSortedWordList.observeAsState(emptyList())
    val displayedList = if (selectedTab == "날짜순") sortedWordList else wordList
    val context = LocalContext.current

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
                    modifier = Modifier.fillMaxSize()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
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
                            selectedWord = word
                            isModalOpen = true
                        }
                    )
                    // 모달 창 표시 (isModalOpen이 true일 때만)
                    if (isModalOpen && selectedWord != null) {
                        LaunchedEffect(isModalOpen) {
                            if (isModalOpen) {
                                playButtonSound(context, R.raw.all_button) // 모달 열릴 때 소리 재생
                            }
                        }
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
