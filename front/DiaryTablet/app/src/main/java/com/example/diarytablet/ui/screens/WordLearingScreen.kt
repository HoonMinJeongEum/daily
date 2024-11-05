package com.example.diarytablet.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.WordTap
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.MainViewModel
import com.example.diarytablet.viewmodel.WordLearningViewModel

@Composable
fun WordLearningScreen(
    navController: NavController,
    viewModel: WordLearningViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {

    val wordList by viewModel.wordList

    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 상단 뒤로가기 및 타이틀
            Row(
                modifier = Modifier
                    .padding(top = 40.dp, start = 60.dp)
                    .align(Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cute_back), // 뒤로가기 이미지 리소스
                    contentDescription = "뒤로가기 버튼",
                    modifier = Modifier
                        .size(60.dp)
                        .clickable { /* 뒤로가기 버튼 클릭 시 동작 */ }
                )
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = "단어 학습",
                    fontSize = 40.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(20.dp)) // 상단 텍스트와 WordTap 간격

            // WordTap 수직 중앙 배치
            WordTap(
                wordList = wordList,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onValidate = { context, originalBitmap, writtenBitmap ->
                    viewModel.checkWordValidate(context, originalBitmap, writtenBitmap)
                },
                onFinish = { words ->
                    viewModel.finishWordLearning(words)
                }
            )
        }
    }
}