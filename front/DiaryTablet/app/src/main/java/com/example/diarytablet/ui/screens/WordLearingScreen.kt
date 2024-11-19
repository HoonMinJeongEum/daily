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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.WordTap
import com.example.diarytablet.ui.components.modal.CommonModal
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.utils.playButtonSound
import com.example.diarytablet.viewmodel.MainViewModel
import com.example.diarytablet.viewmodel.SpenEventViewModel
import com.example.diarytablet.viewmodel.WordLearningViewModel
import com.samsung.android.sdk.penremote.AirMotionEvent
import com.samsung.android.sdk.penremote.ButtonEvent
@Composable
fun WordLearningScreen(
    navController: NavController,
    viewModel: WordLearningViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT,
    spenEventViewModel : SpenEventViewModel
) {
//    LaunchedEffect(spenEventViewModel) {
//        spenEventViewModel.spenEventFlow.collect { event ->
//            when (event) {
//                is ButtonEvent -> {
//                    when (event.action) {
//                        ButtonEvent.ACTION_DOWN -> Log.d("WordLearningScreen", "S Pen 버튼이 눌렸습니다.")
//                        ButtonEvent.ACTION_UP -> Log.d("WordLearningScreen", "S Pen 버튼이 해제되었습니다.")
//                    }
//                }
//                is AirMotionEvent -> {
//                    Log.d("WordLearningScreen", "에어 모션 움직임: X=${event.deltaX}, Y=${event.deltaY}")
//                }
//            }
//        }
//    }
    val wordList by viewModel.wordList
    val learnedWordList by viewModel.learnedWordList
    val username by viewModel.username.collectAsState(initial = "")
    var isModalOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                        .clickable {
                            playButtonSound(context, R.raw.all_button)
                            isModalOpen = true
                        }
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

            WordTap(
                wordList = wordList,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onValidate = { context, word, writtenBitmap ->
                    viewModel.checkWordValidate(context, word, writtenBitmap) // String과 Bitmap 전달
                },
                onFinish = {
                    viewModel.finishWordLearning()
                },
                learnedWordList = learnedWordList,
                navController = navController,
                username = username
            )
        }
    }
    if (isModalOpen) {
        CommonModal(
            onDismissRequest = { isModalOpen = false },
            titleText = "단어 학습을 종료할까요?",
            confirmText= "종료",
            onConfirm = {
                isModalOpen = false
                navController.navigate("main") {
                    popUpTo("wordLearning") { inclusive = true }
                }
            }
        )
    }
}