package com.example.diarytablet.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CreateProfileModal(
    showDialog: Boolean,
    onConfirm: (String, String) -> Unit,
    onCancel: () -> Unit,
) {
    if (showDialog) {
        Dialog(onDismissRequest = { onCancel() }) {
            Surface(
                shape = MaterialTheme.shapes.medium.copy(all = CornerSize(30.dp)), // Radius 30 적용
                color = Color.White,
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Create Profile", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

                    var username by remember { mutableStateOf(TextFieldValue()) }
                    var img by remember { mutableStateOf(TextFieldValue()) }


                    BasicTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(1.dp, Color.Gray) // 테두리 추가
                            .padding(8.dp),
                        singleLine = true
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onConfirm(username.text, img.text) },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Confirm")
                        }
                        Button(
                            onClick = { onCancel() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }
        }
    }
}
