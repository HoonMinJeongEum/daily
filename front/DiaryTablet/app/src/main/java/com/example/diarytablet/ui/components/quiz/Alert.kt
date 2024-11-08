package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.ui.components.BasicButtonShape

@Composable
fun Alert(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    confirmText: String,
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .fillMaxHeight(0.4f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White),
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.7f)
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier
                        .weight(0.2f))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f),
                            shape = BasicButtonShape.ROUNDED.getShape(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBDBDBD),
                            )
                        ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text ="아니오",
                                    textAlign = TextAlign.Center,
                                    fontSize = 30.sp
                                )
                        }
                        Spacer(modifier = Modifier.weight(0.2f))
                        Button(
                            onClick = {
                                onConfirm()
                            },
                            modifier = Modifier
                                .weight(1f),
                            shape = BasicButtonShape.ROUNDED.getShape(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5A72A0),
                            )
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = confirmText,
                                textAlign = TextAlign.Center,
                                fontSize = 30.sp
                            )
                        }
                    }
                }
            }

        }
    }
}
