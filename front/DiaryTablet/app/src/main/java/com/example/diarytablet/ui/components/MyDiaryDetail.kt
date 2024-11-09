package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.domain.dto.response.diary.Diary
import com.example.diarytablet.viewmodel.LogViewModel

@Composable
fun MyDiaryDetail(
    diaryId: Int,
    onBackClick: () -> Unit, // 뒤로 가기 등의 동작을 위한 클릭 핸들러
    viewModel: LogViewModel
) {
    LaunchedEffect(diaryId) {
        viewModel.fetchDiaryById(diaryId)
    }

    val diaryDetail = viewModel.diaryDetail.observeAsState()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 16.dp)
        ) {
            DynamicColorButton(
                text = "<  날짜 선택",
                isSelected = true,
                onClick = onBackClick
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            diaryDetail.value?.let { diary ->
                MyDiaryContent(diary = diary)
            } ?: CircularProgressIndicator() // 로딩 표시
        }
    }
}

@Composable
fun MyDiaryContent(
    diary: Diary
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(diary.drawImg),
            contentDescription = "Draw Image",
            modifier = Modifier.padding(8.dp)
        )

        Image(
            painter = rememberAsyncImagePainter(diary.writeImg),
            contentDescription = "Write Image",
            modifier = Modifier.padding(8.dp)
        )
    }
}