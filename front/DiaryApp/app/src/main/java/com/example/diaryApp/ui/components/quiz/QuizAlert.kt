package com.example.diaryApp.ui.components.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.MyTypography
import kotlinx.coroutines.delay

@Composable
fun QuizAlert(
    title: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onDismiss()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.2f)
                .background(color = Color.White, shape = RoundedCornerShape(15)),
        ){
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val boxHeight = with(LocalDensity.current) { maxHeight.toPx() }
                val lineHeight = boxHeight * 0.1f
                Text(
                    text = title,
                    fontSize = (boxHeight * 0.05f).sp,
                    style = MyTypography.bodyLarge,
                    color = Color(0xFF49566F),
                    textAlign = TextAlign.Center,
                    lineHeight = lineHeight.sp
                )
            }
        }

    }
}
