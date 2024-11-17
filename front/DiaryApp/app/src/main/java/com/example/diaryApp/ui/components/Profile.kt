package com.example.diaryApp.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    onShowQuizAlert: (String, String) -> Unit,
    screenHeight: Dp,
    screenWidth: Dp,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(screenWidth * 0.9f, screenHeight * 0.2f),

    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_container),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = screenWidth * 0.02f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile.img),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(screenWidth * 0.18f)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                Text(
                    text = profile.name,
                    fontSize = (screenWidth.value * 0.05f).sp,
                    color = PastelGreen
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DailyButton(
                    text = "그림 일기",
                    fontSize = (screenWidth.value * 0.045f).toInt(),
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelYellow,
                    cornerRadius = 30,
                    width = (screenWidth.value * 0.23f).toInt(),
                    height = (screenHeight.value * 0.06f).toInt(),
                    shadowElevation = screenWidth * 0.03f,
                    onClick = {
                        runBlocking {
                            updateMemberInfoDiary(profile, diaryViewModel)
                            navController.navigate("diary")
                        }
                  },
            )

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                DailyButton(
                    text = "그림 퀴즈",
                    fontSize = (screenWidth.value * 0.045f).toInt(),
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelPink,
                    cornerRadius = 30,
                    width = (screenWidth.value * 0.23f).toInt(),
                    height = (screenHeight.value * 0.06f).toInt(),
                    shadowElevation = screenWidth * 0.03f,
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DailyButton(
                    text = "단어장",
                    fontSize = (screenWidth.value * 0.045f).toInt(),
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelSkyBlue,
                    cornerRadius = 30,
                    width = (screenWidth.value * 0.23f).toInt(),
                    height = (screenHeight.value * 0.06f).toInt(),
                    shadowElevation = screenWidth * 0.03f,
                    onClick = {
                        runBlocking {
                        updateMemberInfoWord(profile, wordViewModel)
                        navController.navigate("word")
                    }},
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                DailyButton(
                    text = profile.shellCount.toString(),
                    fontSize = (screenWidth.value * 0.045f).toInt(),
                    textColor = DarkGray,
                    fontWeight = FontWeight.Normal,
                    backgroundColor = PastelLightGreen,
                    cornerRadius = 30,
                    width = (screenWidth.value * 0.23f).toInt(),
                    height = (screenHeight.value * 0.06f).toInt(),
                    shadowElevation = screenWidth * 0.03f,
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()  // 화면 너비에 맞춰 확장
    ) {
        val screenWidth = maxWidth

        Box(
            modifier = Modifier
                .fillMaxWidth()  // 화면 너비에 맞춰 확장
                .padding(
                    horizontal = screenWidth * 0.06f,
                    vertical = screenWidth * 0.04f
                )  // 양옆과 위아래에 여백 설정
                .height(screenWidth * 0.3f),
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
                    .padding(screenWidth * 0.04f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile.img),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxSize()
                        .size(screenWidth * 0.17f)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                )

                Text(
                    text = profile.name,
                    fontSize = (screenWidth.value * 0.06f).sp,
                    color = PastelGreen,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )


                DailyButton(
                    text = "삭제",
                    fontSize = (screenWidth.value * 0.05f).toInt(),
                    textColor = White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    backgroundColor = PastelRed,
                    cornerRadius = 30,
                    width = (screenWidth.value * 0.2f).toInt(),
                    height = (screenWidth.value * 0.15f).toInt(),
                    shadowElevation = 8.dp,
                    onClick = {
                        showDialog = true
                    },
                )
            }
        }

        BasicModal(
            isDialogVisible = showDialog,
            screenWidth = screenWidth,
            mainText = "정말 삭제하시겠습니까?",
            buttonText = "삭제",
            successButtonColor = PastelRed,
            onDismiss = { showDialog = false },
            onSuccessClick = {
                coroutineScope.launch {
                    profileViewModel.deleteProfile(profile.id)
                    showDialog = false
                }
            }
        )
    }
}


fun updateMemberInfoDiary(
    profile:Profile,
    diaryViewModel: DiaryViewModel
) {
    diaryViewModel.memberName.value = profile.name
    diaryViewModel.memberId.value = profile.id
}

fun updateMemberInfoWord(
    profile:Profile,
    wordViewModel: WordViewModel
) {
    wordViewModel.memberName.value = profile.name
    wordViewModel.memberId.intValue = profile.id
}

