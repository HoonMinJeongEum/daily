package com.example.diarytablet.ui.components.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diarytablet.ui.components.BasicButton

@Composable
fun NormalDialog(
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
                modifier = Modifier
                    .width(550.dp)
                    .wrapContentHeight()
                    .background(Color.White, shape = RoundedCornerShape(30.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        fontSize = 28.sp,
                        text = mainText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BasicButton(
                            text = buttonText,
                            imageResId = 11,
                            onClick = { onSuccessClick() },
                            ButtonColor = successButtonColor
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        BasicButton(
                            text = "취소",
                            imageResId = 11,
                            onClick = { onDismiss() },
                            ButtonColor = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
