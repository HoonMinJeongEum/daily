package com.example.diaryApp.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.VideoView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Size
import com.example.diaryApp.R
import com.example.diaryApp.domain.dto.response.diary.CommentDto
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryId: String?,
    diaryViewModel: DiaryViewModel,
    backgroundType: BackgroundType = BackgroundType.ACTIVE,
) {
    LaunchedEffect(diaryId) {
        if (diaryId != null) {
            diaryViewModel.fetchDiaryById(diaryId.toInt())
        } else {
            Log.e("DiaryDetailScreen", "diaryId is null!")
        }
    }

    val diaryDetail = diaryViewModel.diaryDetail.observeAsState()
    val comments = remember(diaryDetail.value?.comments) { mutableStateOf(diaryDetail.value?.comments ?: emptyList()) }
    val commentText = remember { mutableStateOf("") }

    BackgroundPlacement(backgroundType = backgroundType)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val textFieldHeight = screenWidth / 7f
        var isDialogVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabletHeader(
                pageName = "${diaryViewModel.memberName.value}의 그림 일기",
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onClick = { diaryViewModel.clearDiaryDetail() }
            )

            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(topEnd = 50.dp, topStart = 50.dp))
                    .fillMaxSize()
            ) {

                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = screenWidth * 0.02f)
                        .padding(bottom = textFieldHeight * 2.2f, top = screenHeight * 0.04f)
                ) {
                    item {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = diaryDetail.value?.drawImg,
                                placeholder = painterResource(R.drawable.main_logo),
                                error = painterResource(R.drawable.main_logo)
                            ),
                            contentDescription = "drawImg",
                            modifier = Modifier
                                .fillMaxWidth()
                        )



                    }
                    item {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = diaryDetail.value?.writeImg,
                                placeholder = painterResource(R.drawable.main_logo),
                                error = painterResource(R.drawable.main_logo)
                            ),
                            contentDescription = "writeImg",
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Button(
                            onClick = { isDialogVisible = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelNavy),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("영상 보기", color = Color.White)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(screenWidth * 0.04f))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(50))
                        )
                        Spacer(modifier = Modifier.height(screenWidth * 0.04f))

                    }
                    item {
                        if (comments.value.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(screenWidth * 0.4f)
                                    .align(Alignment.Center),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "댓글이 없습니다.",
                                    style = MyTypography.bodyLarge.copy(
                                        fontSize = (screenWidth.value * 0.045f).sp,
                                        color = Color.Gray // 색상 지정
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    items(comments.value.size) { index ->
                        val comment = comments.value[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = screenWidth * 0.02f)
                                .padding(horizontal = screenWidth * 0.04f),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Circle Image Icon
                            Image(
                                painter = painterResource(R.drawable.daily_character),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(screenWidth * 0.12f)
                                    .background(Color.LightGray, shape = CircleShape)
                                    .padding(8.dp)
                            )

                            Spacer(modifier = Modifier.width(screenWidth * 0.04f))

                            // Comment Text and Date
                            Column {
                                Text(
                                    text = comment.comment,
                                    style = MyTypography.bodyLarge.copy(fontSize = (screenWidth * 0.04f).value.sp),
                                    color = DeepPastelNavy
                                )
                                Text(
                                    text = "작성 시간: ${comment.createdAt.substringBefore("T")}",
                                    fontSize = (screenWidth * 0.035f).value.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = screenWidth * 0.005f)
                                )
                            }
                        }
                }}

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = screenWidth * 0.04f, vertical = screenHeight * 0.02f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.height(screenHeight * 0.02f))

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

                    Spacer(modifier = Modifier.height(screenHeight * 0.01f))

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
                                    style = MyTypography.bodyLarge.copy(
                                        fontSize = (screenWidth.value * 0.04f).sp,
                                        color = DeepPastelNavy
                                    )
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
                            modifier = Modifier.height(textFieldHeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelNavy,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                diaryViewModel.fetchComment(commentText.value)
                                val newComment = CommentDto(
                                    comment = commentText.value,
                                    createdAt = "방금 전" // or use a formatted current timestamp
                                )
                                comments.value = comments.value + newComment
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
        if (isDialogVisible) {
            Dialog(
                onDismissRequest = { isDialogVisible = false },
                properties = DialogProperties(dismissOnClickOutside = true)
            ) {
                val context = LocalContext.current

                // Video Player 설정
                val videoPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        diaryDetail.value?.video?.let {
                            val mediaItem = MediaItem.fromUri(Uri.parse(it))
                            setMediaItem(mediaItem)
                            repeatMode = Player.REPEAT_MODE_ONE // 비디오 반복 재생 설정
                            prepare()
                            playWhenReady = true
                        }
                    }
                }

                // Sound Player 설정
                val soundPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        diaryDetail.value?.sound?.let {
                            val mediaItem = MediaItem.fromUri(Uri.parse(it))
                            setMediaItem(mediaItem)
                            repeatMode = Player.REPEAT_MODE_ONE // 사운드 반복 재생 설정
                            prepare()
                            playWhenReady = false
                        }
                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        videoPlayer.release() // ExoPlayer 해제
                        soundPlayer.release()
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.9f) // 화면 너비의 80%
                            .wrapContentHeight()
                            .background(Color.White, shape = RoundedCornerShape(screenWidth * 0.03f))
                            .padding(screenWidth * 0.04f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(screenWidth * 0.04f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${diaryViewModel.memberName.value}의 그림 일기",
                                fontSize = (screenWidth * 0.05f).value.sp,
                                color = DeepPastelNavy,
                                modifier = Modifier.padding(bottom = screenWidth * 0.04f)
                            )

                            // ExoPlayer Video Player
                            AndroidView(
                                factory = {
                                    PlayerView(context).apply {
                                        player = videoPlayer
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(screenWidth * 0.03f))
                            )

                            Spacer(modifier = Modifier.height(screenWidth * 0.02f))

                            // Play Sound Button
                            Button(
                                onClick = { soundPlayer.playWhenReady = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepPastelNavy),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f) // 너비의 60%
                                    .height(screenWidth * 0.12f), // 버튼 높이 설정
                                shape = RoundedCornerShape(screenWidth * 0.03f)
                            ) {
                                Text("소리 재생", color = Color.White, fontSize = (screenWidth * 0.045f).value.sp)
                            }

                            Spacer(modifier = Modifier.height(screenWidth * 0.02f))

                            // Close Button
                            Button(
                                onClick = { isDialogVisible = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(screenWidth * 0.12f),
                                shape = RoundedCornerShape(screenWidth * 0.03f)
                            ) {
                                Text("닫기", color = Color.White, fontSize = (screenWidth * 0.045f).value.sp)
                            }
                        }
                    }
                }
            }
        }


    }
}

