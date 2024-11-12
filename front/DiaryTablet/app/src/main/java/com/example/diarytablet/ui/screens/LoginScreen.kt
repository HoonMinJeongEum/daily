import android.util.Log
import android.util.MutableBoolean
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
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
                .width(screenWidth * 0.3f)
                .aspectRatio(1.67f)
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * 0.1f)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.45f)
                .padding(top = screenHeight * 0.1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.25f))
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
                            style = MyTypography.bodyLarge.copy(
                                fontSize = (screenWidth.value * 0.03f).sp
                            ),
                            color = Color.Gray
                        )
                    },
                    onValueChange = { loginViewModel.username.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = screenWidth*0.06f), // Adjust padding for text alignment
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(screenHeight *0.02f))
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
                            style = MyTypography.bodyLarge.copy(
                                fontSize = (screenWidth.value * 0.03f).sp
                            ),
                            color = Color.Gray
                        )
                    },
                    onValueChange = { loginViewModel.password.value = it },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = screenWidth*0.06f), // Adjust padding for text alignment
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
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
                            fontSize = (screenWidth.value * 0.015f).sp
                        ),
                        color = Color.Gray,
                        modifier = Modifier.padding(end = screenWidth*0.02f)
                    )

                }
            }
            Spacer(modifier = Modifier.height(screenHeight *0.01f))



            // Login Button
            BasicButton(
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


