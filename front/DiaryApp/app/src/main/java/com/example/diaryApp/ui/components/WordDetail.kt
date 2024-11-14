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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.diaryApp.domain.dto.response.word.Word
import com.example.diaryApp.ui.components.toCalendarDateString

@Composable
fun WordDetail(
    screenWidth: Dp,
    screenHeight: Dp,
    word: Word,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Surface(
                shape = RoundedCornerShape(screenWidth * 0.04f), // 화면 너비의 4%로 둥근 모서리 설정
                color = Color.White,
                modifier = Modifier.padding(screenWidth * 0.05f) // 화면 너비의 5%로 패딩 설정
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
                            .padding(
                                top = screenHeight * 0.02f,
                                end = screenWidth * 0.04f
                            ), // 화면 비율로 상단 패딩 설정
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier.size(screenWidth * 0.08f) // 화면 너비의 8%로 아이콘 크기 설정
                            )
                        }
                    }
                    // 타이틀 부분
                    Text(
                        text = word.word,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A72A0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = screenHeight * 0.01f), // 화면 높이의 1%로 패딩 설정
                        textAlign = TextAlign.Center,
                        fontSize = (screenWidth * 0.07f).value.sp // 화면 너비의 6%로 폰트 크기 설정
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.02f)) // 화면 높이의 2% 간격

                    // 콘텐츠 부분
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.005f) // 화면 높이의 0.5% 간격
                    ) {
                        Text(
                            text = word.createdAt.toCalendarDateString(),
                            color = Color(0xFF49566F),
                            fontSize = (screenWidth * 0.05f).value.sp, // 화면 너비의 5%로 폰트 크기 설정
                            textAlign = TextAlign.Center
                        )

                        Image(
                            painter = rememberAsyncImagePainter(word.org),
                            contentDescription = "Original Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.2f), // 화면 높이의 30%로 이미지 높이 설정
                            contentScale = ContentScale.Fit
                        )

                        Image(
                            painter = rememberAsyncImagePainter(word.url),
                            contentDescription = "Transcription Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.2f), // 화면 높이의 24%로 이미지 높이 설정
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.02f)) // 화면 높이의 2% 간격
                }
            }
        }
    }
}

