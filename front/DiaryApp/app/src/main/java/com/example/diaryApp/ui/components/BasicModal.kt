package com.example.diaryApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diaryApp.ui.theme.DeepPastelNavy

@Composable
fun BasicModal(
    screenWidth: Dp,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit,
    onSuccessClick: () -> Unit,
    mainText: String,
    buttonText: String,
    successButtonColor: Color
) {
    if (isDialogVisible) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {

                Surface(
                    modifier = Modifier
                        .width(screenWidth * 0.8f)
                        .wrapContentHeight()
                        .background(Color.White, shape = RoundedCornerShape(15))
                        .padding(screenWidth * 0.1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(screenWidth * 0.04f),
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Spacer(modifier = Modifier.height(screenWidth * 0.05f))

                        Text(
                            fontSize = (screenWidth.value * 0.06f).sp,
                            color = DeepPastelNavy,
                            text = mainText
                        )

                        Spacer(modifier = Modifier.height(screenWidth * 0.05f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            DailyButton(
                                text = "취소",
                                onClick = { onDismiss() },
                                backgroundColor = Color.LightGray,
                                width = (screenWidth.value * 0.3f).toInt(),
                                height = (screenWidth.value * 0.15f).toInt(),
                                cornerRadius = 20,
                                fontSize = (screenWidth.value * 0.05f).toInt()
                            )
                            Spacer(modifier = Modifier.width(screenWidth * 0.03f))

                            DailyButton(
                                text = buttonText,
                                onClick = { onSuccessClick() },
                                backgroundColor = successButtonColor,
                                width = (screenWidth.value * 0.3f).toInt(),
                                height = (screenWidth.value * 0.15f).toInt(),
                                cornerRadius = 20,
                                fontSize = (screenWidth.value * 0.05f).toInt(),

                                )

                        }
                    }
                }
            }
        }
    }
}
