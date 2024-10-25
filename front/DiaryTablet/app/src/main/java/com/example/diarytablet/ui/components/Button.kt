package com.example.diarytablet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 버튼 색상 열거형
enum class BasicButtonColor {
    PRIMARY,
    SECONDARY,
    ERROR;

    fun getBackgroundColor(colorScheme: ColorScheme): Color {
        return when (this) {
            PRIMARY -> colorScheme.primary
            SECONDARY -> colorScheme.secondary
            ERROR -> colorScheme.error
        }
    }

    fun getTextColor(colorScheme: ColorScheme): Color {
        return when (this) {
            PRIMARY, ERROR -> colorScheme.onPrimary
            SECONDARY -> colorScheme.onSecondary
        }
    }
}

// 버튼 모양 열거형
enum class BasicButtonShape {
    ROUNDED,
    FLAT;

    fun getShape(): RoundedCornerShape {
        return when (this) {
            ROUNDED -> RoundedCornerShape(16.dp)
            FLAT -> RoundedCornerShape(0.dp)
        }
    }
}

// 기본 버튼 컴포넌트
@Composable
fun BasicButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
    text: String,
    buttonColor: BasicButtonColor = BasicButtonColor.PRIMARY,
    buttonShape: BasicButtonShape = BasicButtonShape.ROUNDED,
    enabled: Boolean = true,
    isOutlined: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme  // 테마의 색상 팔레트를 가져옵니다

    // 색상 설정
    val backgroundColor = buttonColor.getBackgroundColor(colorScheme)
    val contentColor = buttonColor.getTextColor(colorScheme)

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = buttonShape.getShape(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = contentColor,
                containerColor = Color.Transparent  // OutlinedButton은 배경색을 투명으로 설정
            ),
            border = BorderStroke(1.dp, backgroundColor)  // 테두리 색상 설정
        ) {
            Text(text = text, fontSize = 16.sp)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = buttonShape.getShape(),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Text(text = text, fontSize = 16.sp)
        }
    }
}
