package com.example.diaryApp.ui.screens

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.diaryApp.R
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.components.DailyButton
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TabletHeader
import com.example.diaryApp.ui.components.TopBackImage
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType
import com.example.diaryApp.ui.theme.DeepPastelNavy
import com.example.diaryApp.ui.theme.MyTypography
import com.example.diaryApp.ui.theme.PastelNavy

@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryId : String?,
    diaryViewModel: DiaryViewModel,
    backgroundType: BackgroundType = BackgroundType.ACTIVE,
) {
    LaunchedEffect(diaryId) {
        Log.d("DiaryDetailScreen", "diaryId: $diaryId")
        if (diaryId != null) {
            Log.d("DiaryDetailScreen", "Fetching diary with ID: $diaryId")
            diaryViewModel.fetchDiaryById(diaryId.toInt())
        } else {
            Log.e("DiaryDetailScreen", "diaryId is null!")
        }
    }

    BackgroundPlacement(backgroundType = backgroundType)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val footerHeight = screenWidth / 4.5f
        val textFieldHeight = screenWidth / 5f
        Column(
            modifier = Modifier
                .fillMaxSize()

            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabletHeader(
                pageName = "${diaryViewModel.memberName.value}의 그림 일기",
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onClick = {
                    diaryViewModel.clearDiaryDetail()
                }
            )
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxSize()
                    .padding(bottom = footerHeight)
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = screenWidth * 0.02f)
                        .padding(bottom = textFieldHeight * 2f, top = screenHeight * 0.04f )
                ) {
                    item {
                        val painterDraw = rememberAsyncImagePainter(diaryViewModel.diaryDetail.value?.drawImg)
                        Image(
                            painter = painterDraw,
                            contentDescription = "Draw Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()

                        )
                    }
                    item {
                        val painterWrite = rememberAsyncImagePainter(diaryViewModel.diaryDetail.value?.writeImg)
                        Image(
                            painter = painterWrite,
                            contentDescription = "Write Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(390.dp)
                        )
                    }
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = screenWidth * 0.04f, vertical = screenHeight * 0.02f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            screenHeight * 0.02f
                        ))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.comment_little_balloon),
                            contentDescription = null,
                            modifier = Modifier.size(screenWidth * 0.07f)
                        )
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                        Text(
                            text = "코멘트",
                            color = DeepPastelNavy,
                            style = MyTypography.bodyLarge.copy(fontSize = (screenWidth.value * 0.05f).sp)
                        )
                    }

                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            screenHeight * 0.01f
                        ))
                        val commentText = remember { mutableStateOf("") }
                    val textFieldHeight = screenWidth / 8f
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = commentText.value,
                                onValueChange = { commentText.value = it },

                                modifier = Modifier
                                    .height(textFieldHeight)
                                    .weight(1f)
                                    .border(
                                        width = 3.dp,
                                        color = PastelNavy,
                                        shape = RoundedCornerShape(15.dp)
                                    ),
                                placeholder = {
                                    Text(
                                        "댓글을 입력하세요",
                                        style = MyTypography.bodyLarge.copy(fontSize = (screenWidth.value * 0.04f).sp)
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                textStyle = MyTypography.bodyMedium.copy(fontSize = (screenWidth.value * 0.04f).sp)
                            )
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                        Button(
                                modifier = Modifier
                                    .height(textFieldHeight)
                                ,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PastelNavy,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),

                                onClick = {
                                    diaryViewModel.fetchComment(commentText.value)
                                    commentText.value = ""
                                }
                            ) {
                                Text(
                                    "등록",
                                    style = MyTypography.bodyLarge.copy(fontSize = (screenWidth.value * 0.05f).sp)
                                )
                            }
                        }

                }
            }
        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            NavMenu(navController, "main", "diary")
        }
    }
}