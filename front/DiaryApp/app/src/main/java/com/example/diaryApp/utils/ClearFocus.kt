package com.example.diaryApp.utils

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.clearFocusOnClick(): Modifier {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    return this.clickable(
        indication = null, // 리플 효과 비활성화
        interactionSource = null, // 상호작용 관련 효과 제거
        onClick = {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    )
}
