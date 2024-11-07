package com.example.diaryApp.ui.components.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun QuizAlert(
    title: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000) // 3초 대기
        onDismiss() // 3초 후 알림 닫기
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .fillMaxHeight(0.4f)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White),
        ){
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
