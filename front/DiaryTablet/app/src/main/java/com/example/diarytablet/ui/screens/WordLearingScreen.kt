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

    Box (
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier
                .padding(top = 40.dp, start = 60.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.back_button), // 뒤로가기 이미지 리소스
                contentDescription = "뒤로가기 버튼",
                modifier = Modifier
                    .size(60.dp) // 이미지 크기 조정
                    .clickable { /* 뒤로가기 버튼 클릭 시 동작 */ }
            )
            Spacer(modifier = Modifier.width(30.dp)) // 버튼과 텍스트 간 간격
            Text(
                text = "단어 학습",
                fontSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
        }
        WordTap (
            wordList = wordList,
            modifier = Modifier.align(Alignment.Center),
            onValidate = { wordRequest ->
                viewModel.checkWordValidate(wordRequest)
            },
            onFinish = { words ->
                viewModel.finishWordLearning(words)
            },

        )
    }
}