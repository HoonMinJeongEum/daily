package com.example.diaryApp.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import com.example.diaryApp.R
import com.example.diaryApp.domain.dto.response.profile.Profile
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.theme.Black
import com.example.diaryApp.ui.theme.DarkGray
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.Gray
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelGreen
import com.example.diaryApp.ui.theme.PastelLightGreen
import com.example.diaryApp.ui.theme.PastelPink
import com.example.diaryApp.ui.theme.PastelRed
import com.example.diaryApp.ui.theme.PastelSkyBlue
import com.example.diaryApp.ui.theme.PastelYellow
import com.example.diaryApp.ui.theme.White
import com.example.diaryApp.viewmodel.ProfileViewModel
import com.example.diaryApp.viewmodel.QuizViewModel
import com.example.diaryApp.viewmodel.WordViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

@Composable
fun ProfileItem(
    profile: Profile,
    navController: NavController,
    diaryViewModel: DiaryViewModel,
    wordViewModel: WordViewModel,
    quizViewModel: QuizViewModel,
    onShowQuizAlert: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(394.dp, 165.dp)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_container),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile.img),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(50.dp))
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = profile.name,
                    fontSize = 16.sp,
                    color = PastelGreen
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DailyButton(
                    text = "그림 일기",
                    fontSize = 17,
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelYellow,
                    cornerRadius = 30,
                    width = 110,
                    height = 42,
                    shadowElevation = 8.dp,
                    onClick = {
                        runBlocking {
                            updateMemberInfoDiary(profile, diaryViewModel)
                            navController.navigate("diary")
                        }
                  },
            )

                Spacer(modifier = Modifier.height(12.dp))

                DailyButton(
                    text = "그림 퀴즈",
                    fontSize = 17,
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelPink,
                    cornerRadius = 30,
                    width = 110,
                    height = 42,
                    shadowElevation = 8.dp,
                    onClick = {
                        coroutineScope.launch {
                            quizViewModel.checkSession(
                                profile.name,
                                onShowQuizAlert = { newSessionId ->
                                    onShowQuizAlert(newSessionId, profile.name)
                                }
                            )
                        }
                    },
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DailyButton(
                    text = "단어장",
                    fontSize = 17,
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelSkyBlue,
                    cornerRadius = 30,
                    width = 110,
                    height = 42,
                    shadowElevation = 8.dp,
                    onClick = {
                        runBlocking {
                        updateMemberInfoWord(profile, wordViewModel)
                        navController.navigate("word")
                    }},
                )

                Spacer(modifier = Modifier.height(12.dp))

                DailyButton(
                    text = profile.shellCount.toString(),
                    fontSize = 17,
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelLightGreen,
                    cornerRadius = 30,
                    width = 110,
                    height = 42,
                    shadowElevation = 8.dp,
                    iconResId = R.drawable.shell,
                    onClick = {},
                )
            }
        }

    }
}

@Composable
fun DeleteProfileItem(profile: Profile) {
    var showDialog by remember { mutableStateOf(false) }
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()  // 화면 너비에 맞춰 확장
            .padding(horizontal = 18.dp, vertical = 10.dp)  // 양옆과 위아래에 여백 설정
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.setting_profile_container),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = profile.img),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(50.dp))
            )

            Text(
                text = profile.name,
                fontSize = 24.sp,
                color = PastelGreen
            )


            DailyButton(
                text = "삭제",
                fontSize = 17,
                textColor = White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                backgroundColor = PastelRed,
                cornerRadius = 30,
                width = 60,
                height = 44,
                shadowElevation = 8.dp,
                onClick = {
                    showDialog = true
                },
            )
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                modifier = Modifier.padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp, bottom = 24.dp, start = 18.dp, end = 18.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "정말 삭제하시겠습니까?",
                        color = DeepPastelNavy,
                        style = MyTypography.bodySmall,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DailyButton(
                            text = "취소",
                            fontSize = 18,
                            textColor = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            backgroundColor = Gray,
                            cornerRadius = 35,
                            width = 80,
                            height = 50,
                            onClick = { showDialog = false }
                        )

                        DailyButton(
                            text = "삭제",
                            fontSize = 18,
                            textColor = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            backgroundColor = PastelRed,
                            cornerRadius = 35,
                            width = 80,
                            height = 50,
                            onClick = {
                                coroutineScope.launch {
                                    profileViewModel.deleteProfile(profile.id)
                                    showDialog = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


suspend fun updateMemberInfoDiary(
    profile:Profile,
    diaryViewModel: DiaryViewModel
) {
    diaryViewModel.memberName.value = profile.name
    diaryViewModel.memberId.value = profile.id
    delay(1000)
}

suspend fun updateMemberInfoWord(
    profile:Profile,
    wordViewModel: WordViewModel
) {
    wordViewModel.memberName.value = profile.name
    wordViewModel.memberId.intValue = profile.id
    delay(1000)
}

