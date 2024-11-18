package com.example.diaryApp.ui.components.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.MyTypography

@Composable
fun Alert(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.25f)
                    .align(Alignment.Center)
                    .background(color = Color.White, shape = RoundedCornerShape(15))
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.65f)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center

                    ) {
                        val boxHeight = with(LocalDensity.current) { maxHeight.toPx() }
                        Text(
                            text = title,
                            fontSize = (boxHeight * 0.13f).sp,
                            style = MyTypography.bodyLarge,
                            color = Color(0xFF49566F),
                            textAlign = TextAlign.Center,

                        )
                    }
                    Spacer(modifier = Modifier
                        .weight(0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .weight(1f),
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            onClick = {
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBDBDBD),
                            ),
                        ){
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center

                            ) {
                                val buttonHeight = with(LocalDensity.current) { maxHeight.toPx() }
                                Text(
                                    text = "아니오",
                                    color = Color.White,
                                    fontSize = (buttonHeight * 0.13f).sp,
                                    style = MyTypography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            onClick = {
                                onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5A72A0)
                            ),
                        ){
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center

                            ) {
                                val buttonHeight = with(LocalDensity.current) { maxHeight.toPx() }
                                Text(
                                    text = "네",
                                    color = Color.White,
                                    fontSize = (buttonHeight * 0.15f).sp,
                                    style = MyTypography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}
