package com.example.diaryApp.ui.screens

import WordDetail
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
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.domain.dto.response.word.Word
import com.example.diaryApp.ui.components.DailyRegisterButton
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TopBackImage
import com.example.diaryApp.ui.components.WordListItemByMember
import com.example.diaryApp.viewmodel.WordViewModel
import retrofit2.Response

@Composable
fun WordScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    backgroundType: BackgroundType = BackgroundType.ACTIVE
) {
    BackgroundPlacement(backgroundType = backgroundType)

    LaunchedEffect(Unit) {
        wordViewModel.getWordList()
    }

    var selectedTab by remember { mutableStateOf("가나다순") }

    // Modal 상태 관리
    var isModalOpen by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf<Word?>(null) }

    // wordList와 sortedWordList를 observe하여 상태를 감시
    val wordListResponse by wordViewModel.wordList.observeAsState(Response.success(emptyList()))
    val wordList = wordListResponse.body() ?: emptyList() // LiveData에서 실제 List<Word> 추출
    val sortedWordList by wordViewModel.dateSortedWordList.observeAsState(emptyList())

    val displayedList = if (selectedTab == "날짜순") sortedWordList else wordList

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
            ) {
                TopBackImage(
                    logoText = "${wordViewModel.memberName.value} 의 단어장!",
                    BackImage = R.drawable.navigate_back,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
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
                            shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp)
                        )
                        .fillMaxWidth()
                        .height(780.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(bottom = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            DailyRegisterButton(
                                text = "가나다순",
                                fontSize = 26,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                backgroundColor = Color.Transparent,
                                width = 140,
                                height = 60,
                                isSelected = selectedTab == "가나다순",
                                onClick = { selectedTab = "가나다순" },
                            )
                            DailyRegisterButton(
                                text = "날짜순",
                                fontSize = 26,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                backgroundColor = Color.Transparent,
                                width = 140,
                                height = 60,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                NavMenu(navController, "main", "word")
            }


    }
}
