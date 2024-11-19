package com.example.diaryApp.ui.components

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.GrayText

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(
    value: String,
    placeholder: String,
    @DrawableRes iconResId: Int? = null, // 이미지 리소스 ID 추가
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    width: Dp,
    height: Dp,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    focusRequester: FocusRequester = FocusRequester()
) {
    val textPlace = if (iconResId == null) {
        com.example.diaryApp.ui.theme.MyTypography.bodyMedium.copy(
            fontSize = (width.value * 0.05f).sp, textAlign = TextAlign.Start, color = DeepPastelNavy)
    } else {
        com.example.diaryApp.ui.theme.MyTypography.bodyMedium.copy(
            fontSize = (width.value * 0.05f).sp,
            color = DeepPastelNavy
        )
    }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(elevation = width * 0.01f, shape = RoundedCornerShape(35.dp), clip = false)
            .background(Color.White, RoundedCornerShape(35.dp))
            .size(width = width * 0.75f, height = height * 0.08f),
        contentAlignment = Alignment.CenterStart

    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = (width.value * 0.05f).sp, color = Color.Gray) },
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = {
                iconResId?.let {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width * 0.07f)
                            .offset( y = -height * 0.007f),
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true,
            trailingIcon = {
                if (isPassword) {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible },
                        modifier = Modifier.padding(end = width * 0.03f)
                            .offset( y = -height * 0.007f)

                        ,
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = width * 0.03f)
                                .fillMaxHeight(0.75f),
                            painter = painterResource(
                                if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                            ),
                            contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                            tint = GrayText
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType = if (placeholder == "가격") KeyboardType.Number else KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onNext = { onImeAction() }, onDone = { onImeAction() }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(35.dp),
            textStyle = textPlace,
            modifier = Modifier
                .fillMaxSize()

                .then(
                    if (iconResId != null) {
                        Modifier
                            .padding(start = width * 0.03f)
                            .offset(y = height * 0.003f)
                    } else Modifier
                        .offset(x = -width * 0.04f)
                        .offset(y = height * 0.003f)
                )// TextField가 Box 내에서 전체 크기 차지
                .focusRequester(focusRequester)
        )
    }
}

