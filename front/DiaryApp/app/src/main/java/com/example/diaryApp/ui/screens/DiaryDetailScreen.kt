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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
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
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryId: String?,
    diaryViewModel: DiaryViewModel,
    backgroundType: BackgroundType = BackgroundType.NORMAL,
    childName: String
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
    val listState = rememberLazyListState() // LazyColumn 상태 관리
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(comments.value.size) {
        if (comments.value.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(comments.value.size - 1)
            }
        }
    }
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
                pageName = "${childName}의 그림 일기",
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
                    state = listState,
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
                        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

                        val localDateTime = LocalDateTime.parse(comment.createdAt)
                        val displayDate = localDateTime.format(dateTimeFormatter)
                        Log.d("diaryDetail","${comment.createdAt} ${displayDate}")

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
                                    text = "작성 시간: ${displayDate}",
                                    fontSize = (screenWidth * 0.03f).value.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = screenWidth * 0.005f)
                                )

                                Text(
                                    text = comment.comment,
                                    style = MyTypography.bodyLarge.copy(fontSize = (screenWidth * 0.04f).value.sp),
                                    color = DeepPastelNavy
                                )

                            }
                        }
                }}

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = screenWidth * 0.02f, vertical = screenHeight * 0.02f),
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
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${commentText.value.length}/250",
                            style = MyTypography.bodySmall.copy(
                                fontSize = (screenWidth.value * 0.035f).sp,
                                color = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = commentText.value,
                            onValueChange = {
                                if (it.length <= 250) {
                                    commentText.value = it
                                }
                            },
                            modifier = Modifier
                                .height(textFieldHeight)
                                .weight(1f)
                                .border(
                                    width = 3.dp,
                                    color = PastelNavy,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            singleLine = true,
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
                            textStyle = MyTypography.bodyMedium.copy(fontSize = (screenWidth.value * 0.04f).sp),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (commentText.value.isNotBlank()) {
                                        val now = LocalDateTime.now().toString() // 저장용 ISO 포맷
                                        diaryViewModel.fetchComment(commentText.value)
                                        val newComment = CommentDto(
                                            comment = commentText.value,
                                            createdAt = now // or use a formatted current timestamp
                                        )
                                        comments.value = comments.value + newComment
                                        commentText.value = ""

                                    }
                                }
                            )
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
                                if (commentText.value.isNotBlank()) {
                                    val now = LocalDateTime.now().toString() // 저장용 ISO 포맷
                                    diaryViewModel.fetchComment(commentText.value)
                                    val newComment = CommentDto(
                                        comment = commentText.value,
                                        createdAt = now // or use a formatted current timestamp
                                    )
                                    comments.value = comments.value + newComment
                                    commentText.value = ""


                                }
                            }
                        )
                         {
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
                var isLoading by remember { mutableStateOf(true) } // 로딩 상태 관리

                val videoPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        diaryDetail.value?.video?.let {
                            val mediaItem = MediaItem.fromUri(it)
                            setMediaItem(mediaItem)
                            repeatMode = Player.REPEAT_MODE_ONE // 비디오 반복 재생 설정
                            prepare()
                            playWhenReady = true
                        }

                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                isLoading = when (state) {
                                    Player.STATE_BUFFERING -> true // 로딩 중
                                    Player.STATE_READY -> false // 준비 완료
                                    else -> isLoading
                                }
                            }
                        })
                    }
                }
                val soundPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        diaryDetail.value?.sound?.let {
                            val mediaItem = MediaItem.fromUri(it)
                            setMediaItem(mediaItem)
                            repeatMode = Player.REPEAT_MODE_ONE // 사운드 반복 재생 설정
                            prepare()
                            playWhenReady = true
                        }
                    }
                }

                DisposableEffect(Unit) {
                    onDispose {
                        videoPlayer.release() // ExoPlayer 해제
                        soundPlayer?.release()

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
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.White)
                        ) {
                            Text(
                                text = "${childName}의 그림 일기",
                                fontSize = 18.sp,
                                color = DeepPastelNavy,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // 로딩 상태 표시 또는 비디오 플레이어
                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.LightGray), // 로딩 중 배경색
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator() // 로딩 표시
                                }
                            } else {
                                AndroidView(
                                    factory = {
                                        PlayerView(context).apply {
                                            player = videoPlayer
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Close Button
                            Button(
                                onClick = { isDialogVisible = false },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepPastelNavy),
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("닫기", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }



    }
}

