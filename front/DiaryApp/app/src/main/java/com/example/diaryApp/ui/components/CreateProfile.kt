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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.diaryApp.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateProfile(
    profileViewModel: ProfileViewModel,
    onCancel: () -> Unit
) {

    Dialog(onDismissRequest = { onCancel() }) {
            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f)) // 타이틀 왼쪽에 빈 공간을 추가
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
                            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
                                selectedImageUri = uri
                                profileViewModel.memberImg.value = selectedImageUri
                            }

                            if (selectedImageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = "Selected Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(50.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(50.dp))
                                ) {
                                    DailyButton(
                                        text = "프로필 추가",
                                        fontSize = 12,
                                        textColor = DarkGray,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                        backgroundColor = Color.Transparent,
                                        cornerRadius = 60,
                                        width = 80,
                                        height = 80,
                                        onClick = { launcher.launch("image/*") },
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val focusRequester = remember { FocusRequester() }
                            val isFocused = remember { mutableStateOf(false) }

                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable { focusRequester.requestFocus() },
                                contentAlignment = Alignment.Center
                            ) {
                                if (profileViewModel.memberName.value.isEmpty() && !isFocused.value) {
                                    Text(
                                        text = "이름",
                                        modifier = Modifier.alpha(0.5f),
                                        fontSize = 18.sp,
                                        fontFamily = myFontFamily,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                BasicTextField(
                                    value = profileViewModel.memberName.value,
                                    onValueChange = {
                                        if (it.length <= 20) {
                                            profileViewModel.memberName.value = it
                                        }
                                    },
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .onFocusChanged { focusState -> isFocused.value = focusState.isFocused }
                                        .background(Color.Transparent)
                                        .width(100.dp) // Specific width setting
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        DailyButton(
                            text = "추가",
                            fontSize = 20,
                            textColor = DarkGray,
                            fontWeight = FontWeight.Normal,
                            backgroundColor = PastelSkyBlue,
                            cornerRadius = 50,
                            width = 90,
                            height = 35,
                            onClick = {
                                Log.d("JoinScreen", "click the create profile button")
                                if (profileViewModel.memberName.value.isNotBlank()) {
                                    profileViewModel.addProfile(
                                        onSuccess = {
                                            Log.d("JoinScreen", "JoinSuccess called")
                                            onCancel()
                                        },
                                        onError = {
                                            Log.d("JoinScreen", "JoinSuccess Fail")
                                            onCancel()
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
