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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.viewmodel.LogViewModel

@Composable
fun MyDiaryDetail(
    onBackClick: () -> Unit, // 뒤로 가기 등의 동작을 위한 클릭 핸들러
    viewModel: LogViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 왼쪽에 있는 버튼
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 16.dp)
        ) {
            DynamicColorButton(
                text = "< 날짜 선택",
                isSelected = true,
                onClick = onBackClick
            )
        }


        // 오른쪽에 있는 MyDiaryContent 컴포넌트
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            MyDiaryContent(viewModel = viewModel)
        }
    }
}

@Composable
fun MyDiaryContent(
    viewModel: LogViewModel
){
    val diaryDetail = viewModel.diaryDetail.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // diaryDetail이 null인 경우 로딩 또는 기본 메시지 표시
        if (diaryDetail.value == null) {
            CircularProgressIndicator() // 로딩 표시
        } else {
            // diaryDetail 데이터가 있는 경우 drawImg와 writeImg 순서대로 표시
            val diary = diaryDetail.value!!

            // drawImg 표시
            Image(
                painter = rememberAsyncImagePainter(diary.drawImg),
                contentDescription = "Draw Image",
                modifier = Modifier.padding(8.dp)
            )

            // writeImg 표시
            Image(
                painter = rememberAsyncImagePainter(diary.writeImg),
                contentDescription = "Write Image",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}