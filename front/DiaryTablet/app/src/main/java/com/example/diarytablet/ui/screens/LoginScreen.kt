package com.example.diarytablet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.viewmodel.LoginViewModel
import com.example.diarytablet.R
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.ui.components.BasicButton

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit, backgroundType: BackgroundType = BackgroundType.DEFAULT) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // 여기에 로고 리소스 ID를 넣으세요.
            contentDescription = "Logo",
            modifier = Modifier
                .size(254.dp, 153.dp) // 크기 설정
                .offset(x = 511.dp, y = 79.dp) // 위치 조정
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp), // 여백 조정
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // 사용자 이름 텍스트 필드
            TextField(
                value = username,
                onValueChange = { username = it },
                placeholder = {
                    Text("아이디", color = Color.White) // 플레이스홀더 텍스트 색상
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp) // 높이 설정
                    .padding(vertical = 5.dp) // 위 아래 여백
                    .background(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .shadow(2.dp, shape = RoundedCornerShape(30.dp)),
                colors = TextFieldDefaults.textFieldColors(
//                    backgroundColor = Color.Transparent, // 배경을 투명하게 설정
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // 비밀번호 텍스트 필드
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text("비밀번호", color = Color.White) // 플레이스홀더 텍스트 색상
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp) // 높이 설정
                    .padding(vertical = 5.dp) // 위 아래 여백
                    .background(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .shadow(2.dp, shape = RoundedCornerShape(30.dp)),
                colors = TextFieldDefaults.textFieldColors(
//                    backgroundColor = Color.Transparent, // 배경을 투명하게 설정
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            // 로그인 버튼
            BasicButton(
                text = "로그인",
                onClick = {},
                imageResId = 11

            )
        }
    }
}
class FakeUserStore : UserStore {
    override fun login(username: String, password: String): Boolean {
        // 로그인 로직을 여기에 구현, 예시로 항상 true를 반환
        return username == "test" && password == "password"
    }
}

@Preview
@Composable
fun previewLogin() {
    val fakeUserStore = FakeUserStore()
    val viewModel = LoginViewModel(userStore = fakeUserStore)

    LoginScreen(
        viewModel = viewModel,
        onLoginSuccess = { } ,
        backgroundType = BackgroundType.DEFAULT
    )
}