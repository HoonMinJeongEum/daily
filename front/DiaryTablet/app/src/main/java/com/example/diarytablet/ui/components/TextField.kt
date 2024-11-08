package com.example.diarytablet.ui.components


import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        TextField(
            value = value, // value 추가
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 30.sp,
                    color = Color.Gray
                )
            },
            modifier = modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                .fillMaxWidth(),

            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = {
                iconResId?.let {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                            .padding(start = 14.dp), // 아이콘 크기 조정
                        tint = Color.Gray
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White, // 배경색을 흰색으로 설정
                focusedIndicatorColor = Color.White, // 포커스 상태의 하단 선 색상
                unfocusedIndicatorColor = Color.White // 비포커스 상태의 하단 선 색상
            ),

        )
    }
}