package com.example.diaryApp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.MyTypography

@Composable
fun TopLogoImg(
    modifier: Modifier = Modifier,
    logoImg: Int? = null,
    characterImg: Int? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        characterImg?.let {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(if(characterImg == R.drawable.navigate_back) {45.dp} else {75.dp}) // 캐릭터 이미지 크기 지정
            ) {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Character Image",
                    modifier = Modifier.fillMaxSize() // Box 내에서 최대 크기로 확장
                )
            }
        }

        logoImg?.let {
            Box(
                modifier = Modifier
                    .padding(end = 100.dp)
                    .size(500.dp)
                    .align(Alignment.Top)
            ) {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Logo Image",
                    modifier = Modifier.fillMaxSize().offset(x = 10.dp, y = (-210).dp)
                )
            }
        }
    }
}

@Composable
fun TopBackImage(
    logoText: String? = null,
    BackImage: Int? = null,
    onBackClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackImage?.let {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(25.dp) // 뒤로가기 이미지 크기 지정
                    .clickable {
                        onBackClick?.invoke()
                    }
            ) {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Back Image",
                    modifier = Modifier.fillMaxSize() // Box 내에서 최대 크기로 확장
                )
            }
        }

        logoText?.let {
            Box(
                modifier = Modifier
                    .weight(1f) // 남은 공간을 모두 차지하도록 설정
                    .padding(start = 32.dp, end = 91.dp), // 좌우 패딩 추가
                contentAlignment = Alignment.Center // 박스 내에서 중앙 정렬
            ) {
                Text(
                    text = logoText,
                    color = Color.White, // 텍스트 색상 흰색으로 설정
                    fontSize = 24.sp, // 텍스트 크기 설정
                    style = MyTypography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}