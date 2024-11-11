package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DailyButton
import com.example.diaryApp.ui.components.MyTextField
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.White
import com.example.diaryApp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel(),
//    onLoginSuccess: () -> Unit,
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    var isError by remember { mutableStateOf(false) }


    BackgroundPlacement(backgroundType = backgroundType)

    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Image(
            painter = painterResource(id = R.drawable.daily_logo), // 다일리 로고 이미지
            contentDescription = "Centered Logo",
            modifier = Modifier
                .size(screenWidth * 1f)
                .offset(y = -screenHeight * 0.1f)
                .align(Alignment.Center),

        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = screenHeight * 0.4f)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyTextField(
                value = loginViewModel.username.value,
                placeholder = "아이디",
                iconResId= R.drawable.daily_id_icon,
                onValueChange = { loginViewModel.username.value = it }
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f)) // 원하는 간격 설정

            MyTextField(
                value = loginViewModel.password.value,
                placeholder = "비밀번호",
                iconResId = R.drawable.daily_password_icon,
                isPassword = true,
                onValueChange = { loginViewModel.password.value = it },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isError) {
                    Text(
                        text = "아이디와 비밀번호를 정확히 입력해 주세요.",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.015f).sp
                        ),
                        color = Color.Red,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = screenWidth*0.02f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Space between error message and checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = loginViewModel.autoLogin.value,
                        onCheckedChange = { isChecked ->
                            loginViewModel.autoLogin.value = isChecked                        }
                    )

                    Text(
                        text = "자동 로그인",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.04f).sp
                        ),
                        color = Color.Gray,
                        modifier = Modifier.padding(end = screenWidth*0.15f)
                    )

                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.02f)) // 원하는 간격 설정

            DailyButton(
                text = "로그인",
                fontSize = 20,
                textColor = White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = PastelNavy,
                cornerRadius = 35,
                width = 120,
                height = 60,
                onClick = {
                    loginViewModel.login(
                        onSuccess = {
                            Log.d("LoginScreen", "onLoginSuccess called")
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        } , onErrorPassword = { }, onError = { }
                    )
                },
            )

            Spacer(modifier = Modifier.height(8.dp)) // 원하는 간격 설정

            DailyButton(
                text = "회원 가입",
                fontSize = 16,
                textColor = PastelNavy,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = Color.Transparent,
                cornerRadius = 35,
                width = 120,
                height = 60,
                onClick = {navController.navigate("join")},
            )
        }
    }
}
