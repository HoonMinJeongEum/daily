package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DailyButton
import com.example.diaryApp.ui.components.MyTextField
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.DeepPastelNavy
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
    val focusManager = LocalFocusManager.current

    BackgroundPlacement(backgroundType = backgroundType)

    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Image(
            painter = painterResource(id = R.drawable.main_logo), // 다일리 로고 이미지
            contentDescription = "Centered Logo",
            modifier = Modifier
                .size(screenWidth * 0.4f)
                .offset(y = -screenHeight * 0.15f)
                .align(Alignment.Center),

        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(top = screenHeight * 0.4f)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyTextField(
                value = loginViewModel.username.value,
                placeholder = "아이디",
                iconResId= R.drawable.daily_id_icon,
                onValueChange = { loginViewModel.username.value = it },
                width = screenWidth,
                height = screenHeight,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.04f)) // 원하는 간격 설정

            MyTextField(
                value = loginViewModel.password.value,
                placeholder = "비밀번호",
                iconResId = R.drawable.daily_password_icon,
                isPassword = true,
                onValueChange = { loginViewModel.password.value = it },
                width = screenWidth,
                height = screenHeight,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                },

            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = screenHeight * 0.02f)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isError) {
                    Text(
                        text = "아이디와 비밀번호를 확인해주세요.",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.03f).sp
                        ),
                        color = Color.Red,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(start = screenWidth*0.18f)
                    )
                } else {
                Spacer(modifier = Modifier.weight(1f)) // Space between error message and checkbox
                }
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.05f) // 체크박스 크기와 동일하게 설정
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    ) {
                        Checkbox(
                            checked = loginViewModel.autoLogin.value,
                            onCheckedChange = { isChecked ->
                                loginViewModel.autoLogin.value = isChecked
                            },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkmarkColor = DeepPastelNavy,
                                checkedColor = Color.Transparent, // 체크박스 내부 색상을 하얀색으로 설정
                                uncheckedColor = Color.Transparent, // 체크되지 않은 상태일 때도 내부 색상을 하얀색으로 설정
                            ),
                        )
                    }
                    Spacer(modifier = Modifier
                        .width(screenWidth * 0.02f))
                    Text(
                        text = "자동 로그인",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.035f).sp
                        ),
                        color = Color.Gray,
                        modifier = Modifier.padding(end = screenWidth*0.15f)
                    )

                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.03f)) // 원하는 간격 설정

            DailyButton(
                text = "로그인",
                fontSize = ((screenWidth.value) * 0.07f).toInt(),
                textColor = White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = PastelNavy,
                cornerRadius = 35,
                width = ((screenWidth.value) * 0.35f).toInt(),
                height = ((screenWidth.value) * 0.15f).toInt(),
                onClick = {
                    loginViewModel.login(
                        onSuccess = {
                            Log.d("LoginScreen", "onLoginSuccess called")
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = false }
                            }
                        } , onErrorPassword = {
                            isError = true
                        }, onError = {
                            isError = true
                        }
                    )
                },
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.007f)) // 원하는 간격 설정

            DailyButton(
                text = "회원 가입",
                fontSize = ((screenWidth.value) * 0.04f).toInt(),
                textColor = PastelNavy,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = Color.Transparent,
                cornerRadius = 35,
                width = ((screenWidth.value) * 0.35f).toInt(),
                height = ((screenWidth.value) * 0.15f).toInt(),
                onClick = {navController.navigate("join")},
            )
        }
    }
}
