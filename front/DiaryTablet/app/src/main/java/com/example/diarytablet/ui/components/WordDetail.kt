package com.example.diarytablet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.rememberAsyncImagePainter
import com.example.diarytablet.R
import com.example.diarytablet.domain.dto.response.WordLearnedResponseDto
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.utils.playButtonSound

@Composable
fun WordDetail(word: WordLearnedResponseDto, onDismissRequest: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f)) // 타이틀 왼쪽에 빈 공간을 추가
                    IconButton(onClick = {
                        onDismissRequest()
                        playButtonSound(context, R.raw.all_button )
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                // 타이틀 부분
                Text(
                    text = word.word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = Color(0xFF5A72A0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    textAlign = TextAlign.Center,
                    style = MyTypography.titleLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 콘텐츠 부분
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = word.createdAt.toCalendarDateString(),
                        color = Color(0xFF49566F),
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        style = MyTypography.bodySmall,
                    )

                    Image(
                        painter = rememberAsyncImagePainter(word.orgUrl),
                        contentDescription = "Original Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.4f),
                        contentScale = ContentScale.Crop
                    )

                    Image(
                        painter = rememberAsyncImagePainter(word.imageUrl),
                        contentDescription = "Transcription Image",
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Inside
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
