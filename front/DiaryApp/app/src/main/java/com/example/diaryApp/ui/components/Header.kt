package com.example.diaryApp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TopLogoImg(
    logoImg: Int? = null,
    characterImg: Int? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        characterImg?.let {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(75.dp) // 캐릭터 이미지 크기 지정
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
                    .padding(end = 100.dp) // 로고와의 간격 조정
                    .size(500.dp) // Box의 크기 지정
                    .align(Alignment.Top) // Box 정렬을 상단으로 조정
            ) {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Logo Image",
                    modifier = Modifier.fillMaxSize().offset(x= 10.dp , y= (-210).dp)
                )
            }
        }
    }
}
