package com.example.diarytablet.ui.components.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.myFontFamily
import kotlinx.coroutines.delay

@Composable
fun CorrectQuizPopup(
    answer: String,
    onDismissRequest: () -> Unit,
    titleText: String,
){
    val context = LocalContext.current
    val screenWidth = context.resources.displayMetrics.widthPixels
    val screenHeight = context.resources.displayMetrics.heightPixels

    val textFontSize = (screenWidth * 0.014).sp
    val lineHeightRatio = 1.5f // 원하는 배율로 설정
    val lineHeight = textFontSize * lineHeightRatio

    LaunchedEffect(Unit) {
        delay(2000)
        onDismissRequest()
    }
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 40.dp, start = 30.dp, end = 30.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val annotatedText = buildAnnotatedString {
                        if (answer != null) {
                            withStyle(style = SpanStyle(color = Color(0xFFD27979), fontWeight = FontWeight.Bold)) {
                                append(answer) // answer를 빨간색으로 강조
                            }
                            append("\n")
                        }
                        append(titleText) // 나머지 텍스트는 기본 스타일
                    }
                    Text(
                        text = annotatedText,
                        fontSize = textFontSize,
                        color = DeepPastelNavy,
                        fontFamily = myFontFamily,
                        textAlign = TextAlign.Center,
                        lineHeight = lineHeight,
                    )
                }
            }
        }
    }
}