package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.ui.theme.PastelNavy
import com.example.diarytablet.utils.playButtonSound

enum class ButtonType {
    DRAWING_DIARY, WORD_LEARNING, DRAWING_QUIZ
}

@Composable
fun BlockButton(
    screenWidth : Dp,
    screenHeight : Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonType: ButtonType,
    navController: NavController
) {
    val (imageResId, text) = when (buttonType) {
        ButtonType.DRAWING_DIARY -> R.drawable.drawing_diary to "그림 일기"
        ButtonType.WORD_LEARNING -> R.drawable.word_learn to "단어 학습"
        ButtonType.DRAWING_QUIZ -> R.drawable.drawing to "그림 퀴즈"
    }

    // Hover 상태를 기억하는 변수
    val context = LocalContext.current
    var isHovered by remember { mutableStateOf(false) }
    val backgroundResId = if (isHovered) R.drawable.clicked_container_shadow else R.drawable.container_shadow

    Box(
        modifier = modifier
            .width(screenHeight * 0.4f) // 높이에 비례한 너비
            .height(screenHeight * 0.5f) // 비율 기반 높이
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        tryAwaitRelease() // 사용자가 손을 뗄 때까지 대기
                        playButtonSound(context,R.raw.all_button )
                        onClick()
                        isHovered = false
                    }
                )
            }
    ) {
        // 배경 이미지 설정
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(8),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(screenHeight * 0.24f)
                        .height(screenHeight * 0.2f)
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.024f))
                Text(
                    text = text,
                    color = PastelNavy,
                    style = TextStyle(
                        fontSize = (screenHeight * 0.05f).value.sp,
                        fontFamily = MyTypography.bodyLarge.fontFamily,
                        fontWeight = MyTypography.bodyLarge.fontWeight
                    )
                )
            }
        }
    }
}

