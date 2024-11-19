package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diaryApp.R
import com.example.diaryApp.ui.components.DailyButton
import com.example.diaryApp.ui.components.MyTextField
import com.example.diaryApp.ui.components.quiz.QuizAlert
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelGreen
import com.example.diaryApp.ui.theme.PastelNavy
import com.example.diaryApp.ui.theme.PastelRed
import com.example.diaryApp.ui.theme.White
import com.example.diaryApp.viewmodel.JoinViewModel


@Composable
fun JoinScreen(
    navController: NavController,
    joinViewModel: JoinViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    val focusManager = LocalFocusManager.current
    var isUsernameValid by remember { mutableStateOf(false) }
    var isCheckUsername by remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(false) }
    var isPasswordCheckValid by remember { mutableStateOf(false) }
    var isUsernameTouched by remember { mutableStateOf(false) }
    var isPasswordTouched by remember { mutableStateOf(false) }
    var isPasswordCheckTouched by remember { mutableStateOf(false) }
    val allValid = isUsernameValid && isPasswordValid && isPasswordCheckValid
    val usernameErrorMessage = joinViewModel.usernameErrorMessage
    val WarningColor = Color(0xFFF44336) // 밝은 레드
    val SuccessColor = Color(0xFF4CAF50)

    fun validateUsername() {
        isUsernameValid = joinViewModel.username.value.length in 4..20 &&
                joinViewModel.username.value.any { it.isLetter() } &&
                joinViewModel.username.value.any { it.isDigit() }
    }

    fun validatePassword() {
        isPasswordValid = joinViewModel.password.value.length in 8..20 &&
                joinViewModel.password.value.any { !it.isLetterOrDigit() } &&
                joinViewModel.password.value.any { it.isLetter() }
    }

    fun validatePasswordCheck() {
        isPasswordCheckValid = joinViewModel.password.value == joinViewModel.passwordCheck.value
    }

    // Alert Dialog 상태 관리
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

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
                .align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .width(screenWidth * 0.75f)
                .align(Alignment.Center)
                .padding(top = screenHeight * 0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer( modifier = Modifier
                .height(screenWidth * 0.02f)
            )
            if (isUsernameTouched) {
                if (!isUsernameValid) {
                    Text(
                        fontSize = (screenWidth.value * 0.03f).sp,
                        text = "아이디는 영어, 숫자 포함 4-20자",
                        color = WarningColor,
                        modifier = Modifier.offset(x = -screenWidth * 0.2f)
                    )
                } else {
                    Text(
                        fontSize = (screenWidth.value * 0.03f).sp,
                        text = usernameErrorMessage.value,
                        color = if (joinViewModel.isUsernameAvailable.value == true) SuccessColor  else WarningColor,
                        modifier = Modifier.offset(x = -screenWidth * 0.2f)
                    )
                }
            } else {
                Spacer( modifier = Modifier
                    .height(screenWidth * 0.036f)
                )
            }
            Spacer( modifier = Modifier
                .height(screenWidth * 0.02f)
            )
            Row(
                modifier = Modifier.width(screenWidth * 0.75f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MyTextField(
                    value = joinViewModel.username.value,
                    placeholder = "아이디",
                    onValueChange = {
                        if (it.length <= 20) { // 글자 수 제한 확인
                            joinViewModel.username.value = it
                            isUsernameTouched = true
                            validateUsername()
                            joinViewModel.usernameErrorMessage.value = ""
                            isCheckUsername = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    width = screenWidth,
                    height = screenHeight * 0.9f,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        isUsernameTouched = true
                        validateUsername()
                        if (isUsernameValid) {
                            focusManager.clearFocus()
                        }


                    }
                )

                Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                DailyButton(
                    text = "중복확인",
                    fontSize = (screenWidth.value * 0.04f).toInt(),
                    textColor = White,
                    fontWeight = FontWeight.Bold,
                    backgroundColor = PastelNavy,
                    cornerRadius = 50,
                    width = (screenWidth * 0.25f).value.toInt(),
                    height = (screenHeight * 0.08f).value.toInt(),
                    onClick = {
                        isUsernameTouched = true
                        validateUsername()
                        if (isUsernameValid) {
                            joinViewModel.checkUsernameAvailability()
                        }
                    },
                )
            }

            Spacer( modifier = Modifier
                .height(screenWidth * 0.02f)
            )
            if (isPasswordTouched) {
                if (!isPasswordValid) {
                    Text(
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Thin,
                        text = "비밀번호는 영어, 숫자, 특수문자 포함 8-20자",
                        color = WarningColor,
                        modifier = Modifier.offset(x = -screenWidth * 0.2f)
                    )
                } else {
                    Text(
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Thin,
                        text = "사용 가능한 비밀번호입니다.",
                        color = SuccessColor ,
                        modifier = Modifier.offset(x = -screenWidth * 0.2f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(screenWidth * 0.036f))
            }
            Spacer( modifier = Modifier
                .height(screenWidth * 0.02f)
            )

            MyTextField(
                value = joinViewModel.password.value,
                placeholder = "비밀번호",
                isPassword = true,
                onValueChange = {
                    if (it.length <= 20) { // 글자 수 제한 확인
                        joinViewModel.password.value = it
                        isPasswordTouched = true
                        validatePassword()
                        validatePasswordCheck()
                    }
                },
                width = screenWidth,
                height = screenHeight * 0.9f,
                imeAction = ImeAction.Next,
                onImeAction = {
                    validatePassword()
                    if (isPasswordValid) {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                }
            )
            Spacer( modifier = Modifier
                .height(screenWidth * 0.02f)
            )
            if (isPasswordCheckTouched) {
                if (!isPasswordCheckValid) {
                    Text(
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Thin,
                        text = "비밀번호가 일치하지 않습니다.",
                        color = WarningColor,
                        modifier = Modifier.offset(x = -screenWidth * 0.2f)
                    )
                } else {
                    Text(
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Thin,
                        text = "비밀번호가 일치합니다.",
                        color = SuccessColor ,
                        modifier = Modifier.offset(x = -screenWidth * 0.2f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(screenWidth * 0.036f))
            }
            Spacer( modifier = Modifier
                .height(screenWidth * 0.02f)
            )
            MyTextField(
                value = joinViewModel.passwordCheck.value,
                placeholder = "비밀번호 확인",
                isPassword = true,
                onValueChange = {
                    if (it.length <= 20) { // 글자 수 제한 확인
                        joinViewModel.passwordCheck.value = it
                        validatePasswordCheck()
                        isPasswordCheckTouched = true
                    }

                },
                width = screenWidth,
                height = screenHeight * 0.9f,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (allValid) {
                        joinViewModel.join(
                            onSuccess = {
                                Log.d("JoinScreen", "JoinSuccess called")
                                showSuccessDialog = true
                            },
                            onErrorPassword = { showErrorDialog = true },
                            onError = { showErrorDialog = true }
                        )
                    }
                }

            )
            Spacer(modifier = Modifier.height(screenWidth * 0.07f))

            DailyButton(
                text = "회원가입",
                fontSize = (screenWidth.value * 0.05f).toInt(),
                textColor = White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = if (allValid) PastelNavy else Color.Gray,
                cornerRadius = 50,
                width = (screenWidth * 0.35f).value.toInt(),
                height = (screenHeight * 0.08f).value.toInt(),
                onClick = {
                    if (allValid) {
                        joinViewModel.join(
                            onSuccess = {
                                Log.d("JoinScreen", "JoinSuccess called")
                                showSuccessDialog = true
                            },
                            onErrorPassword = { showErrorDialog = true },
                            onError = { showErrorDialog = true }
                        )
                    }
                },

            )
            Spacer(modifier = Modifier.height(screenHeight * 0.007f)) // 원하는 간격 설정

            DailyButton(
                text = "로그인",
                fontSize = ((screenWidth.value) * 0.04f).toInt(),
                textColor = PastelNavy,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                backgroundColor = Color.Transparent,
                cornerRadius = 35,
                width = ((screenWidth.value) * 0.35f).toInt(),
                height = ((screenWidth.value) * 0.15f).toInt(),
                onClick = {navController.navigate("login")},
            )
        }


        if (showSuccessDialog) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000) // 2초 후 닫힘
                showErrorDialog = false
                navController.navigate("login") {
                    popUpTo("join") { inclusive = true }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)), // 어두운 반투명 배경
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // 너비 80%
                        .fillMaxHeight(0.2f) // 높이 20%
                        .background(color = Color.White, shape = RoundedCornerShape(15.dp)),
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val boxHeight = with(LocalDensity.current) { maxHeight.toPx() }
                        val lineHeight = boxHeight * 0.1f

                        Text(
                            text = "회원가입이 완료되었습니다.",
                            fontSize = (boxHeight * 0.05f).sp,
                            style = MyTypography.bodyLarge,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = lineHeight.sp
                        )
                    }
                }
            }
        }


        if (showErrorDialog) {
            LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000) // 2초 후 닫힘
                    showErrorDialog = false
                    navController.navigate("login") {
                    popUpTo("join") { inclusive = true }
                }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)), // 어두운 반투명 배경
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // 너비 80%
                            .fillMaxHeight(0.2f) // 높이 20%
                            .background(color = Color.White, shape = RoundedCornerShape(15.dp)),
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val boxHeight = with(LocalDensity.current) { maxHeight.toPx() }
                            val lineHeight = boxHeight * 0.1f

                            Text(
                                text = "회원가입 실패\n\n정보를 다시 확인해주세요.",
                                fontSize = (boxHeight * 0.05f).sp,
                                style = MyTypography.bodyLarge,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = lineHeight.sp
                            )
                        }
                    }
                }
            }



    }
}