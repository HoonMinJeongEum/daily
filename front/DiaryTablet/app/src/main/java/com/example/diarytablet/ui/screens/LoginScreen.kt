import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

    BackgroundPlacement(backgroundType = backgroundType)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(254.dp, 153.dp)
                .offset(x = 511.dp, y = 79.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(560.dp)
                .padding(top = 150.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 5.dp)

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
                        .padding(start = 60.dp), // Adjust padding for text alignment
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            // Password Field with Background Image
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
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
                        .padding(start = 60.dp), // Adjust padding for text alignment
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))



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
                    }, onErrorPassword = {
                        // 비밀번호 오류 처리ㅅ
                    }, onError = {
                        // 네트워크 오류 처리
                    })
                },
                imageResId = 11
            )
        }
    }
}


