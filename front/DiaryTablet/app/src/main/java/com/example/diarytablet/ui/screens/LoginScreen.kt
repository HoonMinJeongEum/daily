import android.util.Log
import android.util.MutableBoolean
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.ui.components.BasicButton
import com.example.diarytablet.ui.components.MyTextField
import com.example.diarytablet.ui.theme.BackgroundPlacement
import com.example.diarytablet.ui.theme.BackgroundType
import com.example.diarytablet.ui.theme.DarkGray
import com.example.diarytablet.ui.theme.DarkRed
import com.example.diarytablet.ui.theme.DeepPastelNavy
import com.example.diarytablet.ui.theme.GrayText
import com.example.diarytablet.ui.theme.MyTypography
import com.example.diarytablet.viewmodel.LoginViewModel
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenUnitManager

// 로그인 텍스트 필드 쪽 수정 필요
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    backgroundType: BackgroundType = BackgroundType.DEFAULT
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    BackgroundPlacement(backgroundType = backgroundType)


    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(screenWidth * 0.26f)
                .aspectRatio(1.67f)
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * 0.08f)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.4f)
                .padding(top = screenHeight * 0.15f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.24f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f)
                    .padding(vertical = screenHeight * 0.01f)

            ) {
                // Image behind TextField
                Image(
                    painter = painterResource(id = R.drawable.login_input),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // TextField in front of the Image
                TextField(
                    value = loginViewModel.username.value,
                    placeholder = {
                        Text(
                            "아이디",
                            style = MyTypography.bodyMedium.copy(
                                fontSize = (screenWidth.value * 0.025f).sp
                            ),
                            color = GrayText
                        )
                    },
                    onValueChange = { loginViewModel.username.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = screenWidth*0.06f), // Adjust padding for text alignment
                    textStyle = MyTypography.bodyMedium.copy(
                        fontSize = (screenWidth.value * 0.025f).sp // 입력된 텍스트의 글자 크기 설정
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next // Enter 키를 누르면 다음 입력 필드로 포커스를 이동
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down) // 비밀번호 입력창으로 포커스 이동
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = DeepPastelNavy,
                        unfocusedTextColor = DeepPastelNavy
                    )
                )
            }
            Spacer(modifier = Modifier.height(screenHeight *0.01f))
            // Password Field with Background Image
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f)
                    .padding(vertical = screenHeight*0.01f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.password_input),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // TextField in front of the Image
                TextField(
                    value = loginViewModel.password.value,
                    placeholder = {
                        Text(
                            "비밀번호",
                            style = MyTypography.bodyMedium.copy(
                                fontSize = (screenWidth.value * 0.025f).sp
                            ),
                            color = GrayText
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier
                                .padding(end = screenWidth * 0.02f)
                            ,
                            onClick = { isPasswordVisible = !isPasswordVisible }) {

                            Icon(

                                painter = painterResource(
                                    if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                                ),
                                contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                                tint = GrayText
                            )
                        }
                    },
                    onValueChange = { loginViewModel.password.value = it },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = screenWidth*0.06f), // Adjust padding for text alignment
                    textStyle = MyTypography.bodyMedium.copy(
                        fontSize = (screenWidth.value * 0.025f).sp // 입력된 텍스트의 글자 크기 설정
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // Enter 키를 누르면 작업 완료
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // 로그인 버튼 동작 호출
//                            loginViewModel.login(
//                                onSuccess = {
//                                    Log.d("LoginScreen", "onLoginSuccess called")
//                                    navController.navigate("profileList") {
//                                        popUpTo("login") { inclusive = true }
//                                    }
//                                },
//                                onErrorPassword = { message ->
//                                    isError = true
//                                    errorMessage = message
//                                },
//                                onError = { message ->
//                                    isError = true
//                                    errorMessage = message
//                                }
//                            )
                            focusManager.clearFocus()
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = DarkGray,
                        unfocusedTextColor = DarkGray
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isError) {
                    Text(
                        text = "아이디와 비밀번호를 정확히 입력해주세요.",
                        style = MyTypography.bodyLarge.copy(
                            fontSize = (screenWidth.value * 0.015f).sp
                        ),
                        color = DarkRed,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = screenWidth*0.01f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // Space between error message and checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color.White) // 흰색 배경
                            .size(screenWidth * 0.02f)

                    ) {
                        Checkbox(
                            checked = loginViewModel.autoLogin.value,
                            onCheckedChange = { isChecked ->
                                loginViewModel.autoLogin.value = isChecked
                            },
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = DeepPastelNavy,
                                checkedColor = Color.Transparent,
                                uncheckedColor = Color.Transparent,
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width( screenWidth *0.015f))

                    Text(
                        text = "자동 로그인",
                        style = MyTypography.bodyMedium.copy(
                            fontSize = (screenWidth.value * 0.018f).sp
                        ),
                        color = DarkGray,
                        modifier = Modifier.padding(end = screenWidth*0.01f)
                    )

                }
            }
            Spacer(modifier = Modifier.height(screenHeight *0.017f))



            val buttonFontSize = screenHeight.value * 0.035f
            // Login Button
            BasicButton(
                modifier = Modifier
                    .width(screenWidth * 0.15f)
                    .height(screenHeight * 0.11f),
                fontSize = buttonFontSize,
                text = "로그인",
                onClick = {
                    loginViewModel.login(
                        onSuccess = {
                            Log.d("LoginScreen", "onLoginSuccess called") // 로그 추가
                            navController.navigate("profileList") {
                                popUpTo("login") { inclusive = true }
                            }
                    }, onErrorPassword = { message ->
                            isError = true
                            errorMessage = message
                        },
                        onError = { message ->
                            isError = true
                            errorMessage = message
                        }
                    )
                },
                imageResId = 11
            )
        }
    }
}


