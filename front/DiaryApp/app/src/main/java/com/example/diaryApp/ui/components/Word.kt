package com.example.diaryApp.ui.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.domain.dto.response.word.Word
import com.example.diaryApp.viewmodel.WordViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WordItem(word: Word, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF83B4FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 2.dp)
    ) {
        Text(text = word.word,
            fontSize = 20.sp,
            modifier = Modifier.run { padding(vertical = 4.dp) }) // 버튼 내부 여백)
    }
}

fun LocalDateTime.toCalendarDateString(): String {
    // Calendar 객체로 변환
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, this@toCalendarDateString.year)
        set(Calendar.MONTH, this@toCalendarDateString.monthValue - 1) // Calendar의 월은 0부터 시작하므로 -1
        set(Calendar.DAY_OF_MONTH, this@toCalendarDateString.dayOfMonth)
    }
    // SimpleDateFormat으로 날짜 형식 지정
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(calendar.time)
}

fun getInitialConsonant(word: String): String {
    val initial = word.firstOrNull() ?: return ""
    return when (initial) {
        in '가'..'깋' -> "ㄱ"
        in '나'..'닣' -> "ㄴ"
        in '다'..'딯' -> "ㄷ"
        in '라'..'맇' -> "ㄹ"
        in '마'..'밓' -> "ㅁ"
        in '바'..'빟' -> "ㅂ"
        in '사'..'싷' -> "ㅅ"
        in '아'..'잏' -> "ㅇ"
        in '자'..'짛' -> "ㅈ"
        in '차'..'칳' -> "ㅊ"
        in '카'..'킿' -> "ㅋ"
        in '타'..'팋' -> "ㅌ"
        in '파'..'핗' -> "ㅍ"
        in '하'..'힣' -> "ㅎ"
        else -> "#" // 초성이 없거나 외국어일 경우
    }
}

@Composable
fun WordListItemByMember(wordList: List<Word>,
                         selectedTab: String,
                         onWordClick: (Word) -> Unit) {
    // 선택된 탭에 따라 그룹화 방식을 다르게 설정
    val groupedWords = if (selectedTab == "날짜순") {
        // 날짜를 "yyyy-MM-dd" 형식의 문자열로 변환해 그룹화
        wordList.groupBy { it.createdAt.toCalendarDateString() } // 날짜 변환 함수 사용
    } else {
        wordList.groupBy { getInitialConsonant(it.word) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (wordList.isEmpty()) {
            item {
                Text(
                    text = "단어 목록이 없습니다.",
                    color = Color(0xFF6d6d6d),
                    fontSize = 20.sp,
                )
            }
        } else {
            groupedWords.forEach { (groupKey, words) ->
                item {
                    Text(
                        text = groupKey,
                        color = Color(0xFF6d6d6d),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채워서 시작 위치로 정렬 가능
                            .padding(start = 60.dp) // 왼쪽에 16dp 여백 추가
                    )
                }
                items(words) { word ->
                    WordItem(word = word, onClick = { onWordClick(word) })
                }
            }
        }
    }
}



