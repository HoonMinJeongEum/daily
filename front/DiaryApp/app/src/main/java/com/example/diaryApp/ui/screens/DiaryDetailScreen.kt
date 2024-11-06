package com.example.diaryApp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.diaryApp.R
import com.example.diaryApp.presentation.viewmodel.DiaryViewModel
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            TopBackImage(
                logoText = "${diaryViewModel.memberName.value}의 그림 일기!",
                BackImage = R.drawable.navigate_back,
                onBackClick = {
                    navController.popBackStack()
                    diaryViewModel.clearDiaryDetail()
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentAlignment = Alignment.BottomCenter
        ) {

            Box(
                modifier = Modifier
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp)
                    )
                    .fillMaxWidth()
                    .height(780.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxSize()
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
    }
}