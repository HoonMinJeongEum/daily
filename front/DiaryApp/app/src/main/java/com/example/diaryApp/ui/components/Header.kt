package com.example.diaryApp.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diaryApp.R
import com.example.diaryApp.domain.RetrofitClient
import com.example.diaryApp.ui.theme.DeepPastelBlue
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelRed
import com.example.diaryApp.ui.theme.White

@Composable
fun TabletHeader(
    pageName: String,
    modifier: Modifier = Modifier,
    navController: NavController,
    onClick: () -> Unit = {}
) {
    BoxWithConstraints (
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight()
    ) {
        val screenWidth = maxWidth
        Row(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .aspectRatio(4f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (pageName) {
                "관리" -> {
                    // 왼쪽 캐릭터 이미지
                    Image(
                        painter = painterResource(R.drawable.daily_character),
                        contentDescription = "Character Icon",
                        modifier = Modifier.fillMaxHeight()
                            .aspectRatio(0.85f),

                    )
                    // 가운데 로고 텍스트
                    Text(
                            text = pageName,
                    style = MyTypography.bodyLarge.copy(
                        color = Color.White,
                        fontSize = (screenWidth.value * 0.08f).sp
                    ),
                    textAlign = TextAlign.Center

                    )
//
                    DailyButton(
                        text = "로그아웃",
                        fontSize = (screenWidth.value * 0.04f).toInt(),
                        textColor = White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        backgroundColor = DeepPastelBlue,
                        cornerRadius = 30,
                        width = (screenWidth.value * 0.25f).toInt(),
                        height =(screenWidth.value * 0.125f).toInt(),
                        shadowElevation = 8.dp,
                        onClick = onClick
                    )
                }
                "main" -> {
                    Image(
                        painter = painterResource(R.drawable.daily_character),
                        contentDescription = "Character Icon",
                        modifier = Modifier.fillMaxHeight()
                            .aspectRatio(0.85f),

                        )
                    // 가운데 로고 텍스트
                    Image(
                        painter = painterResource(R.drawable.main_logo),
                        contentDescription = "logo",
                        modifier = Modifier.fillMaxHeight(0.5f)
                            .aspectRatio(1.8f)
//                            .offset(x = screenWidth * 0.03f),
                    )
                    Spacer(modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(0.85f)
                    )
                }
                "상점", "알림" -> {
                    // 왼쪽 캐릭터 이미지
                    Image(
                        painter = painterResource(R.drawable.daily_character),
                        contentDescription = "Character Icon",
                        modifier = Modifier.fillMaxHeight()
                            .aspectRatio(0.85f),

                        )
                    // 가운데 페이지 이름 텍스트
                    Text(
                        text = pageName,
                        style = MyTypography.bodyLarge.copy(
                            color = Color.White,
                            fontSize = (screenWidth.value * 0.08f).sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .aspectRatio(1.8f)
                    )// 일관성 유지를 위한 빈 공간

                }

                else -> {
                    // 왼쪽 뒤로 가기 버튼
                    IconButton(
                        onClick = {
                            val currentDestination = navController.currentDestination?.route
                            val canNavigateBack = navController.previousBackStackEntry != null

                            if (currentDestination != "main" && canNavigateBack) {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.navigate_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(screenWidth * 0.1f),
                            tint = Color.White
                        )
                    }
                    // 가운데 페이지 이름
                    Text(
                        text = pageName,
                        style = MyTypography.bodyLarge.copy(
                            color = Color.White,
                            fontSize = (screenWidth.value * 0.08f).sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    // 오른쪽 빈 공간
                    Spacer(modifier = Modifier.size(40.dp)) // 일관성 유지를 위한 빈 공간
                }
            }
        }
    }
}


@Composable
fun TopLogoImg(
    modifier: Modifier = Modifier,
    logoImg: Int? = null,
    characterImg: Int? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // 상단 여백
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        characterImg?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "Character Image",
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(if(characterImg == R.drawable.navigate_back) {50.dp} else {75.dp}) // 캐릭터 이미지 크기
            )
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