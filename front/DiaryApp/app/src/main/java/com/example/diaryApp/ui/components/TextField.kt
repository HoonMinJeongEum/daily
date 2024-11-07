package com.example.diaryApp.ui.components

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    value: String,
    placeholder: String,
    @DrawableRes iconResId: Int? = null, // 이미지 리소스 ID 추가
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    width: Int = 300,
    height: Int = 60
) {
    val textState = remember { mutableStateOf("") }
    val textPlace = if (iconResId == null) {
        androidx.compose.ui.text.TextStyle(fontSize = 14.sp, textAlign = TextAlign.Start) // 왼쪽 정렬
    } else {
        androidx.compose.ui.text.TextStyle(fontSize = 14.sp) // 기본 텍스트 스타일
    }
    TextField(
        value = value, // value 추가
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 15.sp, color = Color.Gray) },
        modifier = modifier
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(150.dp))
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(35.dp))
            .size(width = width.dp, height = height.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = {
            iconResId?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp), // 아이콘 크기 조정
                    tint = Color.Gray
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White, // 배경색을 흰색으로 설정
            focusedIndicatorColor = Color.Transparent, // 포커스 상태의 하단 선 색상
            unfocusedIndicatorColor = Color.Transparent // 비포커스 상태의 하단 선 색상
        ),
        shape = RoundedCornerShape(35.dp),
        textStyle = textPlace
    )
}
