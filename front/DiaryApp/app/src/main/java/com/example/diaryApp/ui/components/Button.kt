package com.example.diaryApp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DailyButton(
    text: String,
    fontSize: Int = 16,
    textColor: Color = Color.White,
    fontWeight: androidx.compose.ui.text.font.FontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
    backgroundColor: Color = Color.Blue,
    cornerRadius: Int = 8,
    @DrawableRes iconResId: Int? = null,
    width: Int = 200,
    height: Int = 50,
    onClick: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isPressed.value) 0.8f else 1.0f,
        label = "Button Press Alpha Animation" // label 추가
    )

    Box(
        modifier = Modifier
            .size(width = width.dp, height = height.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius.dp))
            .alpha(alpha)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed.value = true
                        tryAwaitRelease() // 사용자가 터치에서 손을 뗄 때까지 대기
                        isPressed.value = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        iconResId?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(24.dp) // 아이콘 크기 조정
            )
        }
        Text(
            text = text,
            style = TextStyle(
                fontSize = fontSize.sp,
                color = textColor,
                fontWeight = fontWeight
            ),
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.Center) // 텍스트 중앙 정렬
        )
    }
}