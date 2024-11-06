import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BasicButton

@Composable
fun NormalModal(
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    onSuccessClick:() -> Unit,
    mainText: String,
    buttonText: String,
    successButtonColor: Color

) {


    if (isModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3000000))
        ) {
            Box(
                modifier = Modifier
                    .width(550.dp)
                    .wrapContentHeight()
                    .padding(40.dp)
                    .background(Color.White, shape = RoundedCornerShape(30.dp))
                    .padding(24.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 닫기 버튼
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        fontSize = 28.sp,
                        text = mainText
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // 닉네임과 수정 아이콘
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        BasicButton(
                            text = buttonText,
                            imageResId = 11,
                            onClick = {
                                onSuccessClick()
                            },
                            ButtonColor = successButtonColor
                        )
                            Spacer(modifier = Modifier.width(16.dp))
                            BasicButton(
                                text = "취소",
                                imageResId = 11,
                                onClick = {
                                    onDismiss()
                                },
                                ButtonColor = Color.LightGray
                            )

                    }
                    Spacer(modifier = Modifier)

                }
            }
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun previews(){
    NormalModal(
        isModalVisible = true,
        onDismiss = {},
        onSuccessClick = {},
        mainText = "sadasdasdasdasdasda",
        buttonText = "asdadasd",
        successButtonColor = Color.Gray
    )
}

