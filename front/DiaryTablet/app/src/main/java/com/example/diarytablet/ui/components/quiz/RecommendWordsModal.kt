package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecommendWordModal(
    roundWords: List<String>,
    onWordSelected: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.7f) // Row 너비 조정
                .fillMaxHeight(0.3f) // Row 높이 조정
        ) {
            roundWords.forEach { word ->
                Button(
                    modifier = Modifier
                        .padding(horizontal = 8.dp) // 버튼 사이 간격 조정
                        .weight(1f), // 버튼 너비를 균등하게 설정
                    onClick = {
                        onWordSelected(word)
                    },
                ) {
                    Text(
                        text = word,
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}

