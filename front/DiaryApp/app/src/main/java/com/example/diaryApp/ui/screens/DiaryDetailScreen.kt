package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.diaryApp.R
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
import com.example.diaryApp.ui.components.DailyButton
import com.example.diaryApp.ui.components.NavMenu
import com.example.diaryApp.ui.components.TopBackImage
import com.example.diaryApp.ui.theme.BackgroundPlacement
import com.example.diaryApp.ui.theme.BackgroundType

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            TopBackImage(
                logoText = "${diaryViewModel.memberName.value}의 그림 일기",
                BackImage = R.drawable.navigate_back,
                onBackClick = {
                    navController.popBackStack()
                    diaryViewModel.clearDiaryDetail()
                }
            )
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxWidth()
                    .height(780.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    val painterDraw = rememberAsyncImagePainter(diaryViewModel.diaryDetail.value?.drawImg)
                    val painterWrite = rememberAsyncImagePainter(diaryViewModel.diaryDetail.value?.writeImg)

                    if ( painterDraw.state is AsyncImagePainter.State.Loading ||
                        painterWrite.state is AsyncImagePainter.State.Loading ) {
                        CircularProgressIndicator()
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            Log.d("diary", "${painterDraw} ${painterWrite}")
                            Image(
                                painter = painterDraw,
                                contentDescription = "Draw Image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(390.dp)
                            )

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
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Gray)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val commentText = remember { mutableStateOf(TextFieldValue("")) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText.value,
                    onValueChange = { commentText.value = it },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .aspectRatio(4f)
                    ,
                    placeholder = { Text("댓글을 입력하세요") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, // Remove focused underline
                        unfocusedIndicatorColor = Color.Transparent
                )

                )

//                Spacer(modifier = Modifier.size(8.dp))

//                DailyButton (
//                    width = 2,
//                    text = "완료",
//                    onClick = {
//                        diaryViewModel.fetchComment(commentText.value.text)
//                        commentText.value = TextFieldValue("") // 전송 후 텍스트 필드 초기화
//                    }
//                )
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