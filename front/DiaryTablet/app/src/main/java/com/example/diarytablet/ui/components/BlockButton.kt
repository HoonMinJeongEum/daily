package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.diarytablet.R

@Composable
fun BlockButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageResId: Int,
    text: String
) {
    val colorScheme = MaterialTheme.colorScheme // 테마의 색상 팔레트를 가져옵니다

    // BlockButton의 배경 색상과 텍스트 색상을 테마 색상으로 설정
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick), // 클릭 가능하게 설정
        colors = androidx.compose.material3.CardDefaults.cardColors( // 색상 설정
            containerColor = colorScheme.primary // 배경 색상
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(80.dp) // 이미지 크기 조정
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = colorScheme.onPrimary, // 텍스트 색상
                style = MaterialTheme.typography.bodyLarge // 텍스트 스타일
            )
        }
    }
}

@Preview
@Composable
fun PreviewBlockButton() {
    BlockButton(
        onClick = { /* TODO: Handle click event */ },
        imageResId = R.drawable.ic_launcher_foreground, // 사용할 이미지 리소스 ID로 실제 리소스 이름으로 변경
        text = "버튼 텍스트" // 표시할 텍스트
    )
}
