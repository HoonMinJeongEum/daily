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
fun ProfileModal(
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    profileImageUrl: String,
    userName: String,
    onEditNameClick: (String) -> Unit

) {

    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }

    if (isModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3000000)) // 반투명 검정 배경
        ) {
            Box(
                modifier = Modifier
                    .width(500.dp)
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
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onDismiss() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mission_close),
                            contentDescription = "Close",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 프로필 이미지
//                    AsyncImage(
//                        model = profileImageUrl,
//                        contentDescription = "Profile Image",
//                        modifier = Modifier.size(120.dp),
//                        contentScale = ContentScale.Crop
//                    )
                    Image(
                        painter = painterResource(id = R.drawable.pencil),
                        modifier = Modifier.size(120.dp),
                        contentDescription = null,
                        contentScale = ContentScale.Crop

                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // 닉네임과 수정 아이콘
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isEditing) {
                            TextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                modifier = Modifier.width(200.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicButton(
                                text = "완료",
                                imageResId = 11,
                                onClick = {
                                    onEditNameClick(editedName)
                                    isEditing = false
                                }
                            )
                        } else {
                            Text(
                                text = userName,
                                fontSize = 24.sp,
                                color = Color(0xFF49566F)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.pencil), // 펜 아이콘 리소스
                                contentDescription = "Edit Name",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { isEditing = true}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun preview(){
    ProfileModal(
        isModalVisible = true,
        onDismiss = {},
        onEditNameClick = {},
        userName = "gd",
        profileImageUrl = "da"
    )
}