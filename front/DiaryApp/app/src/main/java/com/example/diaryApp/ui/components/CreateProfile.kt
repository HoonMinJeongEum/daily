package com.example.diaryApp.ui.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.AndroidViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.diaryApp.R
import com.example.diaryApp.ui.theme.Black
import com.example.diaryApp.ui.theme.DarkGray
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.Gray
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelRed
import com.example.diaryApp.ui.theme.PastelSkyBlue
import com.example.diaryApp.ui.theme.myFontFamily
import com.example.diaryApp.utils.clearFocusOnClick
import com.example.diaryApp.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateProfile(
    screenHeight: Dp,
    screenWidth: Dp,
    profileViewModel: ProfileViewModel,
    onCancel: () -> Unit
) {
    var showWarning by remember { mutableStateOf(false) }
    var warningMessage by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val isLoading by profileViewModel.isLoading

    LaunchedEffect(Unit) {
        showWarning = false
        warningMessage = ""
        selectedImageUri = null
        profileViewModel.memberName.value = ""
        profileViewModel.memberImg.value = null
    }


    Dialog(onDismissRequest = { onCancel() }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnClick()

        ) {
            Surface(
                shape = RoundedCornerShape(10),
                color = Color.White,
                modifier = Modifier
                    .width(screenWidth * 0.8f)
                    .wrapContentHeight()
                    .padding(screenWidth * 0.04f)
            ) {
                Column(

                    modifier = Modifier
                        .padding(screenWidth * 0.04f)
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                        ),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                if (!isLoading) { // isLoading이 false일 때만 동작
                                    onCancel()
                                }
                            },
                            enabled = !isLoading // isLoading이 true일 때 비활성화
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier.size(screenWidth * 0.08f)
                            )
                        }
                    }

                    if (showWarning) {
                        Text(
                            text = warningMessage,
                            color = Color.Red,
                            fontSize = (screenWidth * 0.035f).value.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Spacer(
                            modifier = Modifier
                                .height(screenWidth * 0.04f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(screenHeight * 0.01f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val launcher =
                            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
                                selectedImageUri = uri
                                profileViewModel.memberImg.value = selectedImageUri
                                showWarning = false
                            }

                        Box(
                            modifier = Modifier
                                .size(screenWidth * 0.3f)
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                                .clickable(enabled = !isLoading) { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = "Selected Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(screenWidth * 0.3f)
                                )
                            } else {
                                Text(
                                    text = "프로필 추가",
                                    fontSize = (screenWidth * 0.04f).value.sp,
                                    color = DarkGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(screenWidth * 0.05f))

                        // 커스텀 스타일을 추가한 텍스트 필드
                        Box(
                            modifier = Modifier
                                .width(screenWidth * 0.4f)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .padding(
                                    horizontal = screenWidth * 0.02f,
                                    vertical = screenHeight * 0.01f
                                )
                        ) {
                            val focusRequester = remember { FocusRequester() }
                            val isFocused = remember { mutableStateOf(false) }

                            if (profileViewModel.memberName.value.isEmpty() && !isFocused.value) {
                                Text(
                                    text = "이름",
                                    modifier = Modifier.alpha(0.5f),
                                    fontSize = (screenWidth * 0.04f).value.sp,
                                    color = Color.Gray
                                )
                            }

                            BasicTextField(
                                value = profileViewModel.memberName.value,
                                onValueChange = {
                                    if (it.length <= 5) {
                                        profileViewModel.memberName.value = it
                                    } else {
                                        showWarning = true

                                        warningMessage = "이름은 5글자 이하로 입력해주세요."
                                    }
                                },
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { focusState ->
                                        isFocused.value = focusState.isFocused
                                    }
                                    .background(Color.Transparent)
                                    .fillMaxWidth()
                                    .padding(vertical = screenHeight * 0.005f),
                                enabled = !isLoading,
                                singleLine = true, // 한 줄로 제한
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done), // 엔터키 처리
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (profileViewModel.memberName.value.isNotBlank()) {
                                            if (!isLoading) {
                                                if (profileViewModel.memberImg.value == null) {
                                                    showWarning = true
                                                    warningMessage = "프로필 이미지를 선택해주세요."
                                                } else {
                                                    profileViewModel.addProfile(
                                                        onSuccess = {
                                                            Log.d("CreateProfile", "Profile creation successful")
                                                            onCancel()
                                                        },
                                                        onError = {
                                                            showWarning = true
                                                            warningMessage = "프로필 추가에 실패했습니다. 다시 시도해 주세요."
                                                        }
                                                    )
                                                }
                                            }
                                        } else {
                                            showWarning = true
                                            warningMessage = "이름을 입력해주세요."
                                        }
                                    }
                                )

                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.05f))




                    // 비활성화 여부를 조건으로 설정
                    DailyButton(
                        text = if (isLoading) "보내는 중..." else "추가",
                        fontSize = (screenWidth * 0.05f).value.toInt(),
                        textColor = Color.White,
                        fontWeight = FontWeight.Normal,
                        backgroundColor = if (isLoading || profileViewModel.memberName.value.isBlank() ) Color.Gray else DeepPastelNavy,
                        cornerRadius = 50,
                        width = (screenWidth * 0.25f).value.toInt(),
                        height = (screenHeight * 0.06f).value.toInt(),
                        onClick = {
                            if (!isLoading) {
                                if (profileViewModel.memberName.value.isBlank()) {
                                    showWarning = true
                                    warningMessage = "이름을 입력해주세요."
                                } else if (profileViewModel.memberImg.value == null) {
                                    showWarning = true
                                    warningMessage = "프로필 이미지를 선택해주세요."
                                } else {
                                    profileViewModel.addProfile(
                                        onSuccess = {
                                            Log.d("CreateProfile", "Profile creation successful")
                                            onCancel()
                                        },
                                        onError = {
                                            showWarning = true
                                            warningMessage = "프로필 추가에 실패했습니다. 다시 시도해 주세요."
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Spacer(modifier = Modifier.height(screenHeight * 0.02f))
                }
            }
        }
    }
}



