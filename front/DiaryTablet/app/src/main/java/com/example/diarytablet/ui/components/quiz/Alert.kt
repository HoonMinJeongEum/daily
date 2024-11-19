package com.example.diarytablet.ui.components.quiz

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarytablet.R
import com.example.diarytablet.ui.theme.MyTypography

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
                    .align(Alignment.Center),
            ){
                Image(
                    painter = painterResource(id = R.drawable.quiz_pop_up),
                    contentDescription = "퀴즈 팝업 이미지",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.7f)
                        .align(Alignment.Center)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center

                    ) {
                        val boxHeight = with(LocalDensity.current) { maxHeight.toPx() }
                        Text(
                            text = title,
                            fontSize = (boxHeight * 0.2f).sp,
                            style = MyTypography.bodyLarge,
                            color = Color(0xFF49566F),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier
                        .weight(0.2f))
                    Row(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxHeight(0.7f)
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            onClick = {
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray,
                            )
                        ){
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center

                            ) {
                                val buttonHeight = with(LocalDensity.current) { maxHeight.toPx() }
                                Text(
                                    text = "아니오",
                                    fontSize = (buttonHeight * 0.3f).sp,
                                    style = MyTypography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.2f))
                        Button(
                            modifier = Modifier
                                .fillMaxHeight(0.7f)
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            onClick = {
                                onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5A72A0)
                            )
                        ){
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center

                            ) {
                                val buttonHeight = with(LocalDensity.current) { maxHeight.toPx() }
                                Text(
                                    text = confirmText,
                                    fontSize = (buttonHeight * 0.3f).sp,
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
