package com.example.diarytablet.ui.components.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diarytablet.R
import com.example.diarytablet.ui.components.BlockButton
import com.example.diarytablet.ui.components.ButtonType
import com.example.diarytablet.utils.playButtonSound

@Composable
fun MainModal(
    screenWidth : Dp,
    screenHeight : Dp,
    isModalVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    onWordLearningClick: () -> Unit,
    onDrawingDiaryClick: () -> Unit,
    onDrawingQuizClick: () -> Unit
) {
    val context = LocalContext.current
    if (isModalVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xB3000000)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally, // 수평 중앙 정렬
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * 0.16f)
                    ,
                    contentAlignment = Alignment.TopStart
                ){
                    Image(
                        modifier = Modifier
                            .aspectRatio(0.6f)
                            .align(Alignment.CenterStart)
                            .clickable(onClick = {
                                onDismiss()
                                playButtonSound(context, R.raw.all_button )

                            }
                            ),
                        painter = painterResource(id = R.drawable.back_button), // 취소 버튼 아이콘
                        contentDescription = "Close",
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .weight(4f),
                    horizontalArrangement = Arrangement.SpaceBetween, // 버튼 간 간격 조정
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    BlockButton(screenWidth = screenWidth, screenHeight = screenHeight,modifier = Modifier.weight(1f), onClick = onDrawingDiaryClick, buttonType = ButtonType.DRAWING_DIARY, navController = navController)
                    BlockButton(screenWidth = screenWidth, screenHeight = screenHeight,modifier = Modifier.weight(1f), onClick = onDrawingQuizClick, buttonType = ButtonType.DRAWING_QUIZ, navController = navController)
                    BlockButton(screenWidth = screenWidth, screenHeight = screenHeight,modifier = Modifier.weight(1f), onClick = onWordLearningClick, buttonType = ButtonType.WORD_LEARNING, navController = navController)
                }
            }
        }
    }
}

