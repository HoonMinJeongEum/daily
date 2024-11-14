package com.example.diarytablet.ui.components.main

import android.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.TextUnit
import com.example.diarytablet.ui.theme.PastelNavy
import kotlinx.coroutines.delay

@Composable
fun TypingText(
    text: String,
    typingSpeed: Long = 50L,
    fontSize: TextUnit,
    lineHeight: TextUnit
) {
    var displayedText by remember { mutableStateOf("") }

    // 타이핑 효과 적용
    LaunchedEffect(text) {
        displayedText = ""  // 초기화
        text.forEach { char ->
            displayedText += char
            delay(typingSpeed)  // 글자 등장 속도 조절
        }
    }

    Text(
        text = displayedText,
        color = PastelNavy,
        fontSize = fontSize, // 원하는 폰트 크기로 조정
        lineHeight = lineHeight
    )
}