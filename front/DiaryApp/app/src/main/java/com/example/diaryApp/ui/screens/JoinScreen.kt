package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DailyButton
import com.example.diaryApp.ui.components.MyTextField
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.White
import com.example.diaryApp.viewmodel.JoinViewModel


@Composable
fun JoinScreen(
    navController: NavController,
    joinViewModel: JoinViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    val isUsernameValid = joinViewModel.username.value.length in 4..20 &&
            joinViewModel.username.value.any { it.isLetter() } && // 영어 알파벳 포함
            joinViewModel.username.value.any { it.isDigit() } // 숫자 포함

    val isPasswordValid = joinViewModel.password.value.length in 8..20 &&
            joinViewModel.password.value.any { !it.isLetterOrDigit() } &&
            joinViewModel.password.value.any { it.isLetter() }

    // Alert Dialog 상태 관리
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.daily_logo), // 다일리 로고 이미지
            contentDescription = "Centered Logo",
            modifier = Modifier
                .size(500.dp)
                .offset(y = (-100).dp)
                .align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top=330.dp)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!isUsernameValid) {
                Text(
                    fontSize = 12.sp,
                    text = "아이디는 영어, 숫자 포함 4-20자",
                    color = Color.Red,
                    modifier = Modifier.padding(end = 92.dp, top = 4.dp) // 적절한 패딩 추가
                )
            }

            Row(
                modifier = Modifier.width(315.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MyTextField(
                    value = joinViewModel.username.value,
                    placeholder = "아이디",
                    iconResId = R.drawable.daily_id_icon,
                    onValueChange = { joinViewModel.username.value = it },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                DailyButton(
                    text = "중복확인",
                    fontSize = 16,
                    textColor = White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    backgroundColor = PastelNavy,
                    cornerRadius = 50,
                    width = 85,
                    height = 60,
                    onClick = {},
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // 원하는 간격 설정

            if (!isPasswordValid) {
                Text(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    text = "비밀번호는 영어, 숫자, 특수문자 포함 8-20자",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp) // 적절한 패딩 추가
                )
            }

            MyTextField(
                value = joinViewModel.password.value,
                placeholder = "비밀번호",
                iconResId = R.drawable.daily_password_icon,
                isPassword = true,
                onValueChange = { joinViewModel.password.value = it },
            )

            Spacer(modifier = Modifier.height(8.dp)) // 원하는 간격 설정

            MyTextField(
                value = joinViewModel.passwordCheck.value,
                placeholder = "비밀번호 확인",
                iconResId = R.drawable.daily_password_icon,
                isPassword = true,
                onValueChange = { joinViewModel.passwordCheck.value = it },
            )

            Spacer(modifier = Modifier.height(30.dp)) // 원하는 간격 설정

            DailyButton(
                text = "회원가입",
                fontSize = 26,
                textColor = White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = PastelNavy,
                cornerRadius = 50,
                width = 140,
                height = 60,
                onClick = {
                    joinViewModel.join(
                        onSuccess = {
                            Log.d("JoinScreen", "JoinSuccess called")
                            navController.navigate("login") {
                                popUpTo("join") { inclusive = true }
                                showSuccessDialog = true
                            }
                        } , onErrorPassword = {
                            showErrorDialog = true
                        }, onError = {
                            showErrorDialog = true
                        }
                    )
                },
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("회원가입 성공") },
                text = { Text("회원가입이 성공적으로 완료되었습니다.") },
                confirmButton = {
                    Button(onClick = {
                        showSuccessDialog = false
                        navController.navigate("login") // 로그인 화면으로 이동
                    }) {
                        Text("확인")
                    }
                }
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("회원가입 실패") },
                text = { Text("정확한 정보를 기입해 주세요.") },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }

    }
}