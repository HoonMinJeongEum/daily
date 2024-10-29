package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography

@Composable
fun UserProfile(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 프로필 사진을 위한 Box
    Box(
        modifier = modifier
            .size(75.dp) // 크기 설정
            .clickable { onProfileClick() }, // 클릭 이벤트 처리
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)), // 동그란 모양
            color = Color.LightGray // 배경색 설정 (필요에 따라 수정 가능)
        ) {
            Image(
                painter = painterResource(id = R.drawable.duck), // 기본 프로필 이미지 리소스
                contentDescription = "User Profile",
                modifier = Modifier.size(75.dp) // 프로필 이미지 크기
            )
        }
    }
}

@Preview
@Composable
fun previewProfile(){
    UserProfile(
        onProfileClick = {}
    )
}
