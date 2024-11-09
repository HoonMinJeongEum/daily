import android.util.Log
import android.util.MutableBoolean
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.diarytablet.viewmodel.LoginViewModel

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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .aspectRatio(1.67f)
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.5f)
                .padding(top = 70.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(170.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f)
                    .padding(vertical = 8.dp)

            ) {
                // Image behind TextField
                Image(
                    painter = painterResource(id = R.drawable.id_container),
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
                            fontSize = 30.sp,
                            color = Color.Gray
                        )
                    },
                    onValueChange = { loginViewModel.username.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 80.dp), // Adjust padding for text alignment
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            // Password Field with Background Image
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f)
                    .padding(vertical = 5.dp)
            ) {
                // Image behind TextField
                Image(
                    painter = painterResource(id = R.drawable.password_container),
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
                            fontSize = 30.sp,
                            color = Color.Gray
                        )
                    },
                    onValueChange = { loginViewModel.password.value = it },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 80.dp), // Adjust padding for text alignment
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
                        fontSize = 20.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 20.dp)
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
                        fontSize = 20.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                }
            }
            Spacer(modifier = Modifier.height(5.dp))



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


